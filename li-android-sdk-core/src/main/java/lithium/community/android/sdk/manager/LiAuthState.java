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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import lithium.community.android.sdk.auth.LiAuthorizationException;
import lithium.community.android.sdk.auth.LiSSOAuthResponse;
import lithium.community.android.sdk.auth.LiTokenResponse;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.utils.LiClock;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiSystemClock;

import static lithium.community.android.sdk.utils.LiCoreSDKUtils.checkArgument;
import static lithium.community.android.sdk.utils.LiCoreSDKUtils.checkNotNull;
import static lithium.community.android.sdk.utils.LiCoreSDKUtils.checkNullOrNotEmpty;

/**
 * This class holds all the Authorization related responses and the Tokens.
 * Created by kunal.shrivastava on 10/18/16.
 */
@VisibleForTesting
class LiAuthState {

    private static final String LOG_TAG = "LiSDKAuth";

    /**
     * Tokens which have less time than this value left before expiry will be considered to be
     * expired for the purposes of calls to
     * performActionWithFreshTokens}.
     */
    private static final int EXPIRY_TIME_TOLERANCE_MS = 60000;
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_LAST_SSO_AUTHORIZATION_RESPONSE = "lastSSOAuthorizationResponse";
    private static final String KEY_LI_LAST_TOKEN_RESPONSE = "mLastLiTokenResponse";
    private static final String KEY_LOGGED_IN_USER = "LOGGED_IN_USER";
    private static final String KEY_AUTHORIZATION_EXCEPTION = "mLiAuthorizationException";

    @Nullable
    private String mRefreshToken;

    @Nullable
    private String mScope;

    @Nullable
    private LiSSOAuthResponse mLastLiSSOAuthResponse;

    @Nullable
    private LiTokenResponse mLastLiTokenResponse;

    @Nullable
    private LiAuthorizationException mLiAuthorizationException;

    private LiUser user;

    private boolean mNeedsTokenRefreshOverride;

    private boolean isSSOLogin;

    private String mProxyHost;

    private String mTenant;

    /**
     * Creates an empty, unauthenticated {@link LiAuthState}.
     */
    LiAuthState() {
        isSSOLogin = true;
    }

    /**
     * Reads an authorization state instance from a JSON string representation produced by
     * {@link #jsonSerialize()}.
     *
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    private static LiAuthState jsonDeserialize(@NonNull JSONObject json) throws JSONException {
        checkNotNull(json, "json cannot be null");
        Gson gson = new Gson();
        LiAuthState state = new LiAuthState();
        state.mRefreshToken = LiCoreSDKUtils.getStringIfDefined(json, KEY_REFRESH_TOKEN);
        state.mScope = LiCoreSDKUtils.getStringIfDefined(json, KEY_SCOPE);
        if (json.has(KEY_AUTHORIZATION_EXCEPTION)) {
            state.mLiAuthorizationException = LiAuthorizationException.fromJson(
                    json.getJSONObject(KEY_AUTHORIZATION_EXCEPTION));
        }

        if (json.has(KEY_LOGGED_IN_USER)) {
            state.user = LiUser.jsonDeserialize(json.getJSONObject(KEY_LOGGED_IN_USER));
        }
        if (json.has(KEY_LAST_SSO_AUTHORIZATION_RESPONSE)) {
            String string = json.getString(KEY_LAST_SSO_AUTHORIZATION_RESPONSE);
            state.mLastLiSSOAuthResponse = gson.fromJson(string, LiSSOAuthResponse.class);
            state.mLastLiSSOAuthResponse.setJsonString(string);
        }
        if (json.has(KEY_LI_LAST_TOKEN_RESPONSE)) {
            String string = json.getString(KEY_LI_LAST_TOKEN_RESPONSE);
            state.mLastLiTokenResponse = gson.fromJson(string, LiTokenResponse.class);
            state.mLastLiTokenResponse.setJsonString(string);
        }
        return state;
    }

    /**
     * Reads an authorization state instance from a JSON string representation produced by
     * {@link #jsonSerializeString()}. This method is just a convenience wrapper for
     * {@link #jsonDeserialize(JSONObject)}, converting the JSON string to its JSON object form.
     *
     * @throws JSONException if the provided JSON does not match the expected structure.
     */
    static LiAuthState jsonDeserialize(@NonNull String jsonStr) throws JSONException {
        checkNullOrNotEmpty(jsonStr, "jsonStr cannot be null or empty");
        return jsonDeserialize(new JSONObject(jsonStr));
    }

    /**
     * The most recent refresh token received from the server, if available. Rather than using
     * this property directly as part of any request depending on authorization state, it is
     * recommended to call performActionWithFreshTokens} to ensure that fresh tokens are available.
     */
    @Nullable
    String getRefreshToken() {
        return mRefreshToken;
    }

    /**
     * The scope of the current authorization grant. This represents the latest scope returned by
     * the server and may be a subset of the scope that was initially granted.
     */
    @Nullable
    String getScope() {
        return mScope;
    }

    /**
     * A set representation of {@link #getScope()}, for convenience.
     */
    @Nullable
    Set<String> getScopeSet() {
        return LiCoreSDKUtils.stringToSet(mScope);
    }

    /**
     * The most recent authorization response used to update the authorization state. For the
     * implicit flow, this will contain the latest access token. It is rarely necessary to
     * directly use the response; instead convenience methods are provided to retrieve the
     */

    @Nullable
    public String getProxyHost() {
        if (isSSOLogin) {
            if (mLastLiSSOAuthResponse != null) {
                return mLastLiSSOAuthResponse.getApiProxyHost();
            }
        }

        return null;
    }

    /**
     * provides tenant id fetched during authorization.
     *
     * @return
     */
    @Nullable
    public String getTenantId() {
        if (isSSOLogin) {
            if (mLastLiSSOAuthResponse != null) {
                return mLastLiSSOAuthResponse.getTenantId();
            }
        }

        return null;
    }

    /**
     * The current access token, if available.
     */
    @Nullable
    String getAccessToken() {
        if (isSSOLogin) {
            if (mLastLiTokenResponse != null) {
                return mLastLiTokenResponse.getAccessToken();
            }
            return null;
        }

        return null;
    }

    /**
     * The expiration time of the current access token (if available), as milliseconds from the
     * UNIX epoch (consistent with {@link System#currentTimeMillis()}).
     */
    @Nullable
    Long getAccessTokenExpirationTime() {
        if (isSSOLogin) {
            if (mLastLiTokenResponse != null) {
                return mLastLiTokenResponse.getExpiresAt();
            }
            return null;

        }
        return -1L;
    }

    /**
     * Determines whether the current state represents a successful authorization,
     * from which at least either an access token or an ID token have been retrieved.
     */
    boolean isAuthorized() {
        if (isSSOLogin) {
            return getAccessToken() != null;
        }
        return false;
    }

    /**
     * If the last response was an OAuth related failure, this returns the exception describing
     * the failure.
     */
    @Nullable
    LiAuthorizationException getAuthorizationException() {
        return mLiAuthorizationException;
    }

    /**
     * Determines whether the access token is considered to have expired. If no refresh token
     * has been acquired, then this method will always return {@code false}. A token refresh
     * can be forced, regardless of the validity of any currently acquired access token, by
     */
    boolean getNeedsTokenRefresh() {

        return getNeedsTokenRefresh(LiSystemClock.INSTANCE);
    }

    @VisibleForTesting
    boolean getNeedsTokenRefresh(LiClock liClock) {
        if (isSSOLogin) {
            if (getAccessTokenExpirationTime() == null) {
                // if there is no expiration but we have an access token, it is assumed
                // to never expire.
                return getAccessToken() == null;
            }

            return getAccessTokenExpirationTime()
                    <= liClock.getCurrentTimeMillis() + EXPIRY_TIME_TOLERANCE_MS;

        } else {
            if (mNeedsTokenRefreshOverride) {
                return true;
            }

            if (getAccessTokenExpirationTime() == null) {
                // if there is no expiration but we have an access token, it is assumed
                // to never expire.
                return getAccessToken() == null;
            }

            return getAccessTokenExpirationTime()
                    <= liClock.getCurrentTimeMillis() + EXPIRY_TIME_TOLERANCE_MS;
        }
    }

    /**
     * Updates the authorization state based on a new authorization response.
     */
    void update(
            @Nullable LiSSOAuthResponse authResponse) {
        checkArgument(authResponse != null, "exactly one of authResponse or authException should be non-null");

        // the last token response and refresh token are now stale, as they are associated with
        // any previous authorization response
        mRefreshToken = null;
        mLiAuthorizationException = null;

        // if the response's mScope is null, it means that it equals that of the request

        mScope = null;
        mLastLiSSOAuthResponse = authResponse;
        mProxyHost = authResponse.getApiProxyHost();
        mTenant = authResponse.getTenantId();
    }

    /**
     * Updates the authorization state based on a new token response.
     */
    void update(
            @Nullable LiTokenResponse liTokenResponse) {
        checkArgument(liTokenResponse != null, "no tokenResponse ");

        mLastLiTokenResponse = liTokenResponse;
        mScope = null;
        mRefreshToken = liTokenResponse.getRefreshToken();

    }

    /**
     * Produces a JSON representation of the authorization state for persistent storage or local
     * transmission (e.g. between activities).
     */
    JSONObject jsonSerialize() {
        JSONObject json = new JSONObject();
        LiCoreSDKUtils.putIfNotNull(json, KEY_REFRESH_TOKEN, mRefreshToken);
        LiCoreSDKUtils.putIfNotNull(json, KEY_SCOPE, mScope);

        if (mLiAuthorizationException != null) {
            LiCoreSDKUtils.put(json, KEY_AUTHORIZATION_EXCEPTION, mLiAuthorizationException.toJson());
        }

        if (mLastLiSSOAuthResponse != null) {
            LiCoreSDKUtils.put(
                    json,
                    KEY_LAST_SSO_AUTHORIZATION_RESPONSE,
                    mLastLiSSOAuthResponse.getJsonString());
        }
        if (mLastLiTokenResponse != null) {
            LiCoreSDKUtils.put(
                    json,
                    KEY_LI_LAST_TOKEN_RESPONSE,
                    mLastLiTokenResponse.getJsonString());
        }

        if (user != null) {
            LiCoreSDKUtils.put(
                    json,
                    KEY_LOGGED_IN_USER,
                    user.jsonSerialize());
        }

        return json;
    }

    /**
     * Produces a JSON string representation of the authorization state for persistent storage or
     * local transmission (e.g. between activities). This method is just a convenience wrapper
     * for {@link #jsonSerialize()}, converting the JSON object to its string form.
     */
    String jsonSerializeString() {
        return jsonSerialize().toString();
    }

    /**
     * Returns user details.
     */
    public LiUser getUser() {
        return user;
    }

    public void setUser(LiUser user) {
        this.user = user;
    }

}
