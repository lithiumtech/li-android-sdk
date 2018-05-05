/*
 * Copyright 2018 Lithium Technologies Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lithium.community.android.manager;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.lithium.community.android.auth.LiAppCredentials;
import com.lithium.community.android.auth.LiSSOAuthResponse;
import com.lithium.community.android.auth.LiTokenResponse;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.queryutil.LiDefaultQueryHelper;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.MessageConstants;

import org.json.JSONException;

import com.lithium.community.android.exception.LiInitializationException;

/**
 * <p>
 * This class also manages the responsibility of initiating the OAuth2-authorize flow if the user state is blank or empty. When
 * the token is expired, this class manages the responsibility of refreshing the auth state using OAuth refresh token api.
 * </p>
 *
 * @author kunal.shrivastava
 */
class LiAuthManager {

    @NonNull
    private final LiAppCredentials credentials;

    @NonNull
    private final LiSecuredPrefManager preferences;

    @Nullable
    private LiAuthState state;

    LiAuthManager(@NonNull Context context, @NonNull LiAppCredentials credentials) throws LiInitializationException {
        this.credentials = LiCoreSDKUtils.checkNotNull(credentials, MessageConstants.wasNull("credentials"));
        LiSecuredPrefManager.initialize(credentials.getClientSecret() + credentials.getDeviceId());
        LiDefaultQueryHelper.initialize(context);
        this.preferences = LiSecuredPrefManager.getInstance();
        this.state = restoreAuthState(LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context")));
    }

    /**
     * Get the credentials used to initialize the SDK.
     *
     * @return the credentials.
     */
    @NonNull
    public LiAppCredentials getCredentials() {
        return credentials;
    }

    /**
     * Get the credentials used to initialize the SDK.
     *
     * @return the credentials.
     * @deprecated Use {@link #getCredentials()} instead.
     */
    @Deprecated
    @NonNull
    public LiAppCredentials getLiAppCredentials() {
        return getCredentials();
    }

    /**
     * Get the Community URI.
     *
     * @return the community URI.
     * @deprecated Use {@link #getCredentials()} and call {@link LiAppCredentials#getCommunityUri()} instead.
     */
    @Deprecated
    public Uri getCommunityUrl() {
        return credentials.getCommunityUri();
    }

    /**
     * Get the current logged in User.
     *
     * @return If a user is logged in the logged in user will be return else {@code null}.
     */
    @Nullable
    public LiUser getLoggedInUser() {
        if (state != null) {
            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#getLoggedInUser() - Li Auth State is not null");
        } else {
            Log.e(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#getLoggedInUser() - Li Auth State is null");
        }
        return state != null ? state.getUser() : null;
    }

    /**
     * Sets a logged in User
     *
     * @param user a LIA user.
     */
    public void setLoggedInUser(@NonNull Context context, @Nullable LiUser user) {
        if (state != null) {
            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#setLoggedInUser() - Li Auth State is not null");
            if (user != null) {
                Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#setLoggedInUser() - Li User is not null");
            } else {
                Log.e(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#setLoggedInUser() - Li User is null");
            }
            state.setUser(user);
            putInSecuredPreferences(context, LiCoreSDKConstants.LI_AUTH_STATE, state.toJsonString());
        } else {
            Log.e(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "Li Auth State is null");
        }
    }

    /**
     * Get the access token for API calls.
     *
     * @return the access token if logged in and initialized else {@code null}.
     */
    @Nullable
    public String getAuthToken() {
        return state == null ? null : state.getAccessToken();
    }

    /**
     * Get the access token for API calls.
     *
     * @return the access token if logged in and initialized else {@code null}.
     * @deprecated Use {@link #getAuthToken()} instead.
     */
    @Deprecated
    @Nullable
    public String getNewAuthToken() {
        return getAuthToken();
    }

    /**
     * Get the refresh token for generating new access token
     *
     * @return the refresh token if logged in and initialized else {@code null}.
     */
    @Nullable
    public String getRefreshToken() {
        return state == null ? null : state.getRefreshToken();
    }

    @Nullable
    public String getFromSecuredPreferences(@NonNull Context context, @NonNull String key) {
        return preferences.getString(context, key);
    }

    public void putInSecuredPreferences(@NonNull Context context, @NonNull String key, @NonNull String value) {
        preferences.putString(context, key, value);
    }

    public void removeFromSecuredPreferences(@NonNull Context context, @NonNull String key) {
        preferences.remove(context, key);
    }

    /**
     * persists Auth State in shared preference when  Authorization Response is received
     *
     * @param context  {@link Context}
     * @param response {@link LiSSOAuthResponse}
     */
    public void persistAuthState(Context context, @NonNull LiSSOAuthResponse response) {
        this.state = new LiAuthState();
        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#persistAuthState() - persisting SSO auth state");
        state.update(response);
        putInSecuredPreferences(context, LiCoreSDKConstants.LI_AUTH_STATE, state.toJsonString());
    }

    /**
     * persists Auth State in shared preference when  Token Response is received
     *
     * @param context  {@link Context}
     * @param response {@link LiTokenResponse}
     */
    public void persistAuthState(Context context, @NonNull LiTokenResponse response) {
        if (this.state != null) {
            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#persistAuthState() - persisting auth state");
            this.state.update(response);
            putInSecuredPreferences(context, LiCoreSDKConstants.LI_AUTH_STATE, state.toJsonString());
        } else {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiAuthManager#persistAuthState() - auth state is null");
        }
    }

    /**
     * Flushes auth state when a call to logout is made.
     *
     * @param context {@link Context}
     */
    public void logout(@NonNull Context context) {
        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#logout() - logging out from SDK");

        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));

        preferences.clear(context);

        // For clearing cookies, if the android OS is Lollipop (5.0) and above use new
        // way of using CookieManager else use the deprecate methods for older versions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(LiCoreSDKConstants.LI_LOG_TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(LiCoreSDKConstants.LI_LOG_TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager manager = CookieSyncManager.createInstance(context);

            //noinspection deprecation tagets old API version
            manager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();

            //noinspection deprecation tagets old API version
            manager.stopSync();

            //noinspection deprecation tagets old API version
            manager.sync();
        }
        this.state = null;
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true or false depending whether user is logged in.
     */
    public boolean isUserLoggedIn() {
        return this.state != null && this.state.isAuthorized();
    }

    /**
     * Restores Auth State from preferences.
     *
     * @param context An Android Context.
     * @return the Auth State.
     */
    @Nullable
    public final LiAuthState restoreAuthState(Context context) {
        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#restoreAuthState() - restoring auth state");
        String json = getFromSecuredPreferences(context, LiCoreSDKConstants.LI_AUTH_STATE);
        if (!TextUtils.isEmpty(json)) {
            try {
                state = LiAuthState.deserialize(json);
            } catch (JSONException e) {
                Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiAuthManager#restoreAuthState() - deserialization of auth state failed");
                e.printStackTrace();
            }
        } else {
            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#restoreAuthState() - No saved auth state found");
        }

        return state;
    }

    /**
     * Gets the API gateway's host address.
     *
     * @return the API gateway's host address.
     * @deprecated Use {@link #getCredentials()} and call {@link LiAppCredentials#getCommunityUri()} instead.
     */
    @Deprecated
    public String getApiGatewayHost() {
        return credentials.getCommunityUri().toString();
    }

    /**
     * Gets the API gateway's host address.
     *
     * @return the API gateway's host address.
     * @deprecated Use {@link #getCredentials()} and call {@link LiAppCredentials#getCommunityUri()} instead.
     */
    @Deprecated
    public String getProxyHost() {
        return credentials.getCommunityUri().toString();
    }

    /**
     * Returns tenant id received along with auth code .
     */
    public String getTenantId() {
        return state != null && !TextUtils.isEmpty(state.getTenantId()) ? state.getTenantId() : credentials.getTenantId();
    }

    /**
     * Returns tenant Id.
     *
     * @return Returns tenant id received along with auth code.
     * @deprecated Use {@link #getTenantId()} instead.
     */
    @Deprecated
    public String getTenant() {
        return getTenantId();
    }

    /**
     * Checks if the SDK needs to refresh the access token.
     *
     * @return {@code true} if the access token has expired or user is logged out, otherwise {@code false}.
     */
    public boolean getNeedsTokenRefresh() {
        return state == null || this.state.getNeedsTokenRefresh();
    }
}
