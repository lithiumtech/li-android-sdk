/*
 * LiAuthManager.java
 * Created on Dec 27, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.client.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.net.URISyntaxException;

import lithium.community.android.sdk.LiSDKManager;
import lithium.community.android.sdk.auth.LiAuthService;
import lithium.community.android.sdk.auth.LiAuthServiceImpl;
import lithium.community.android.sdk.auth.LiAuthTokenProvider;
import lithium.community.android.sdk.auth.LiSSOAuthResponse;
import lithium.community.android.sdk.auth.LiTokenResponse;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_AUTH_STATE;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_SHARED_PREFERENCES_NAME;

/**
 * This class also manages
 * the responsibility of initiating the OAuth2-authorize flow if the user state is blank or empty. When
 * the token is expired, this class manages the responsibility of refreshing the auth state using OAuth
 * refresh token api.
 * Created by kunal.shrivastava on 10/18/16.
 */

public class LiAuthManager implements LiAuthTokenProvider {

    private LiAuthState liAuthState;

    LiAuthManager(Context context) {
        this.liAuthState = restoreAuthState(context);
    }

    /**
     * Fetches the current logged in User.
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
     * @param user {@Link LiUser}
     */
    public void setLoggedInUser(LiUser user) {
        if (liAuthState != null) {
            liAuthState.setUser(user);
            getSharedPreferences(LiSDKManager.getInstance().getContext()).edit()
                    .putString(LI_AUTH_STATE, liAuthState.jsonSerializeString())
                    .commit();
        }
    }

    /**
     *
     * Returns community URI.
     */
    @Override
    public Uri getCommunityUrl() {
        return LiSDKManager.getInstance().getLiAppCredentials().getCommunityUri();
    }

    /**
     *
     * Returns Access Token.
     */
    @Override
    public String getNewAuthToken() {
        return liAuthState == null ? null : liAuthState.getAccessToken();
    }

    /**
     *
     * Returns Refresh Token.
     */
    @Override
    public String getRefreshToken() {
        return liAuthState == null ? null : liAuthState.getRefreshToken();
    }

    /**
     * Login flow is initiated from here and then call goes to LiAuthService
     * @param context {@link Context}
     * @throws URISyntaxException
     */
    public void initLoginFlow(Context context) throws URISyntaxException {
        initLoginFlow(context, null);
    }

    /**
     * Login flow is initiated from here and then call goes to LiAuthService
     * @param context {@link Context}
     * @throws URISyntaxException
     */
    public void initLoginFlow(Context context, String ssoToken) throws URISyntaxException {
        if (!isUserLoggedIn()) {
            if (!TextUtils.isEmpty(ssoToken)) {
                new LiAuthServiceImpl(context, ssoToken).startLoginFlow();
            }
            else {
                new LiAuthServiceImpl(context).startLoginFlow();
            }
        }
    }

    /**
     * fetches shared preference
     * @param context {@link Context}
     * @return SharedPreferences
     */
    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(LiCoreSDKConstants.LI_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * persists Auth State in shared preference when  Authorization Response is received
     * @param context {@link Context}
     * @param authorizationResponse {@link LiSSOAuthResponse}
     */
    public void persistAuthState(Context context, @NonNull LiSSOAuthResponse authorizationResponse) {
        this.liAuthState = new LiAuthState();
        liAuthState.update(authorizationResponse);
        getSharedPreferences(context).edit()
                .putString(LI_AUTH_STATE, liAuthState.jsonSerializeString())
                .commit();
    }

    /**
     * persists Auth State in shared preference when  Token Response is received
     * @param context {@link Context}
     * @param response {@link LiTokenResponse}
     */
    public void persistAuthState(Context context, @NonNull LiTokenResponse response) {
        this.liAuthState.update(response);
        getSharedPreferences(context).edit()
                .putString(LI_AUTH_STATE, liAuthState.jsonSerializeString())
                .commit();
    }

    /**
     * Flushes auth state when a call to logout is made.
     * @param context {@link Context}
     */
    public void logout(Context context) {
        getSharedPreferences(context).edit().remove(LI_SHARED_PREFERENCES_NAME).commit();
        this.liAuthState = null;
    }

    /**
     * Checks if the user is logged in.
     * @return true or false depending whether user is logged in.
     */
    public boolean isUserLoggedIn() {
        return (this.liAuthState != null && this.liAuthState.isAuthorized());
    }

    /**
     * Fetches Auth State from Shared Preference.
     * @param context {@link Context}
     * @return the LiAuthState class.
     */
    public final LiAuthState restoreAuthState(Context context) {
        String jsonString = context.getSharedPreferences(LI_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(LI_AUTH_STATE, null);
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
        return liAuthState == null ? null : liAuthState.getProxyHost();
    }

    /**
     * Returns tenant id received along with auth code .
     */
    public String getTenant() {
        return liAuthState == null ? null : liAuthState.getTenantId();
    }

    /**
     * Checks if need to getch fresh access tokens.
     */
    public boolean getNeedsTokenRefresh() {
        return liAuthState == null ? true : this.liAuthState.getNeedsTokenRefresh();
    }

    /**
     * Fetches Fresh Access Token and Persists it.
     * @param context {@link Context}
     * @param mFreshTokenCallBack {@link LiAuthService.FreshTokenCallBack}
     * @throws URISyntaxException {@link URISyntaxException}
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
                Log.i("LiClientManager", "received access token :" + response.getAccessToken());
                persistAuthState(context, response);
                mFreshTokenCallBack.onFreshTokenFetched(true);
            }
        });
    }
}
