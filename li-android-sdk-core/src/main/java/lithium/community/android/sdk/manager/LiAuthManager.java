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

package lithium.community.android.sdk.manager;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.json.JSONException;

import java.net.URISyntaxException;

import lithium.community.android.sdk.auth.LiAppCredentials;
import lithium.community.android.sdk.auth.LiAuthService;
import lithium.community.android.sdk.auth.LiAuthServiceImpl;
import lithium.community.android.sdk.auth.LiDeviceTokenProvider;
import lithium.community.android.sdk.auth.LiSSOAuthResponse;
import lithium.community.android.sdk.auth.LiTokenResponse;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_AUTH_STATE;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEVICE_ID;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_RECEIVER_DEVICE_ID;
/*
 * Created by kunal.shrivastava on 10/18/16.
 */

/**
 * This class also manages
 * the responsibility of initiating the OAuth2-authorize flow if the user state is blank or empty. When
 * the token is expired, this class manages the responsibility of refreshing the auth state using OAuth
 * refresh token api.
 */

class LiAuthManager {

    protected final LiAppCredentials liAppCredentials;
    private LiAuthState liAuthState;
    private LiDeviceTokenProvider liDeviceTokenProvider;

    LiAuthManager(@NonNull Context context, @NonNull LiAppCredentials liAppCredentials) {
        this.liAuthState = restoreAuthState(context);
        this.liAppCredentials = liAppCredentials;
    }

    public LiDeviceTokenProvider getLiDeviceTokenProvider() {
        return liDeviceTokenProvider;
    }

    public void setLiDeviceTokenProvider(LiDeviceTokenProvider liDeviceTokenProvider) {
        this.liDeviceTokenProvider = liDeviceTokenProvider;
    }

    /**
     * Fetches the current logged in User.
     *
     * @return user details.
     */
    public LiUser getLoggedInUser() {
        if (liAuthState != null) {
            return liAuthState.getUser();
        }
        return null;
    }

    /**
     * Sets User
     *
     * @param user {@Link LiUser}
     */
    public void setLoggedInUser(Context context, LiUser user) {
        if (liAuthState != null) {
            liAuthState.setUser(user);
            putInSecuredPreferences(context, LI_AUTH_STATE, liAuthState.jsonSerializeString());
        }
    }

    /**
     * Returns Access Token.
     */
    public String getNewAuthToken() {
        return liAuthState == null ? null : liAuthState.getAccessToken();
    }

    /**
     * Returns Refresh Token.
     */
    public String getRefreshToken() {
        return liAuthState == null ? null : liAuthState.getRefreshToken();
    }

    /**
     * Login flow is initiated from here and then call goes to LiAuthService
     *
     * @param context {@link Context}
     * @throws URISyntaxException
     */
    public void initLoginFlow(Context context) throws URISyntaxException {
        initLoginFlow(context, null, null);
    }

    /**
     * Login flow is initiated from here and then call goes to LiAuthService
     *
     * @param context  Android context
     * @param ssoToken pass the Single Sign-on token if the community uses its own identity provider
     * @throws URISyntaxException
     */
    public void initLoginFlow(Context context, String ssoToken) throws URISyntaxException {
        initLoginFlow(context, ssoToken, null);
    }

    /**
     * Login flow is initiated from here and then call goes to LiAuthService
     *
     * @param context               Android context
     * @param liDeviceTokenProvider this provider fetches device token id based upon whatever the app is using.
     *                              Either Firebase or GCM
     * @throws URISyntaxException
     */
    public void initLoginFlow(Context context, LiDeviceTokenProvider liDeviceTokenProvider) throws URISyntaxException {
        this.liDeviceTokenProvider = liDeviceTokenProvider;
        initLoginFlow(context, null, liDeviceTokenProvider);
    }

    /**
     * Login flow is initiated from here and then call goes to LiAuthService
     *
     * @param ssoToken              pass the Single Sign-on token if the community uses its own identity provider
     * @param liDeviceTokenProvider this provider fetches device token id based upon whatever the app is using.
     *                              Either Firebase or GCM
     * @param context               {@link Context}
     * @throws URISyntaxException
     */
    public void initLoginFlow(Context context, String ssoToken,
                              LiDeviceTokenProvider liDeviceTokenProvider) throws URISyntaxException {
        this.liDeviceTokenProvider = liDeviceTokenProvider;
        if (!isUserLoggedIn()) {
            new LiAuthServiceImpl(context).startLoginFlow(ssoToken);
        }
    }

    public boolean removeFromSecuredPreferences(Context context, String key) {
        if (LiSecuredPrefManager.getInstance() != null) {
            LiSecuredPrefManager.getInstance().remove(context, key);
            return true;
        } else {
            return false;
        }
    }

    public boolean putInSecuredPreferences(Context context, String key, String value) {
        if (LiSecuredPrefManager.getInstance() != null) {
            LiSecuredPrefManager.getInstance().putString(context, key, value);
            return true;
        } else {
            return false;
        }
    }

    public String getFromSecuredPreferences(Context context, String key) {
        return LiSecuredPrefManager.getInstance() == null ? null
                : LiSecuredPrefManager.getInstance().getString(context, key);
    }

    /**
     * persists Auth State in shared preference when  Authorization Response is received
     *
     * @param context               {@link Context}
     * @param authorizationResponse {@link LiSSOAuthResponse}
     */
    public void persistAuthState(Context context, @NonNull LiSSOAuthResponse authorizationResponse) {
        this.liAuthState = new LiAuthState();
        liAuthState.update(authorizationResponse);
        putInSecuredPreferences(context, LI_AUTH_STATE, liAuthState.jsonSerializeString());
    }

    /**
     * persists Auth State in shared preference when  Token Response is received
     *
     * @param context  {@link Context}
     * @param response {@link LiTokenResponse}
     */
    public void persistAuthState(Context context, @NonNull LiTokenResponse response) {
        this.liAuthState.update(response);
        putInSecuredPreferences(context, LI_AUTH_STATE, liAuthState.jsonSerializeString());
    }

    /**
     * Flushes auth state when a call to logout is made.
     *
     * @param context {@link Context}
     */
    public void logout(Context context) {
        this.removeFromSecuredPreferences(context, LI_DEFAULT_SDK_SETTINGS);
        this.removeFromSecuredPreferences(context, LI_AUTH_STATE);
        this.removeFromSecuredPreferences(context, LI_DEVICE_ID);
        this.removeFromSecuredPreferences(context, LI_RECEIVER_DEVICE_ID);
        this.removeFromSecuredPreferences(context, LiCoreSDKConstants.LI_SHARED_PREFERENCES_NAME);
        // For clearing cookies, if the android OS is Lollipop (5.0) and above use new way of using CookieManager
        // else use the deprecate methods for older versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(LiCoreSDKConstants.LI_LOG_TAG,
                    "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(LiCoreSDKConstants.LI_LOG_TAG,
                    "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
        this.liAuthState = null;
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true or false depending whether user is logged in.
     */
    public boolean isUserLoggedIn() {
        return (this.liAuthState != null && this.liAuthState.isAuthorized());
    }

    /**
     * Fetches Auth State from Shared Preference.
     *
     * @param context {@link Context}
     * @return the LiAuthState class.
     */
    public final LiAuthState restoreAuthState(Context context) {
        String jsonString = getFromSecuredPreferences(context, LI_AUTH_STATE);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                liAuthState = LiAuthState.jsonDeserialize(jsonString);
                return liAuthState;
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }

    /**
     * Returns proxy host received along with auth code .
     */
    public String getProxyHost() {
        String proxyHost;
        if (liAuthState == null || TextUtils.isEmpty(liAuthState.getProxyHost())) {
            proxyHost = liAppCredentials.getApiProxyHost();
        } else {
            proxyHost = liAuthState.getProxyHost();
        }
        return proxyHost;
    }

    /**
     * Returns tenant id received along with auth code .
     */
    public String getTenant() {
        String tenant;
        if (liAuthState == null || TextUtils.isEmpty(liAuthState.getTenantId())) {
            tenant = liAppCredentials.getTenantId();
        } else {
            tenant = liAuthState.getTenantId();
        }
        return tenant;
    }

    /**
     * Checks if need to getch fresh access tokens.
     */
    public boolean getNeedsTokenRefresh() {
        return liAuthState == null || this.liAuthState.getNeedsTokenRefresh();
    }

    /**
     * Fetches Fresh Access Token and Persists it.
     *
     * @param context             {@link Context}
     * @param mFreshTokenCallBack {@link LiAuthService.FreshTokenCallBack}
     * @throws URISyntaxException      {@link URISyntaxException}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public void fetchFreshAccessToken(final Context context,
                                      final LiAuthService.FreshTokenCallBack mFreshTokenCallBack)
            throws URISyntaxException, LiRestResponseException {

        new LiAuthServiceImpl(context).performRefreshTokenRequest(new LiAuthService.LiTokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable LiTokenResponse response, @Nullable Exception ex) {
                if (ex != null) {
                    mFreshTokenCallBack.onFreshTokenFetched(false);
                    return;
                }
                persistAuthState(context, response);
                mFreshTokenCallBack.onFreshTokenFetched(true);
            }
        });
    }
}
