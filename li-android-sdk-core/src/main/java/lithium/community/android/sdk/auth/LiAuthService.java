/*
 * LiAuthService.java
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

package lithium.community.android.sdk.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.rest.LiAuthRestClient;

/**
 * Auth service interface to do token exchange and refresh.
 */
public interface LiAuthService {

    /**
     * Initiates Login Flow
     */
    void startLoginFlow();

    /**
     * Initiates Authorization request (Non SSO).
     * @param request {@link LiSSOAuthorizationRequest}
     */
    void performAuthorizationRequest(
            @NonNull LiSSOAuthorizationRequest request);

    /**
     * Initiates Authorization request for SSO case.
     * @param request {@link LiSSOAuthorizationRequest}
     * @throws LiRestResponseException
     */
    void performSSOAuthorizationRequest(@NonNull LiSSOAuthorizationRequest request) throws LiRestResponseException;

    /**
     * Handles what to do after authorization response.
     * @param response {@link LiSSOAuthResponse}
     * @param authRestClient {@link LiAuthRestClient}
     * @param loginCompleteCallBack {@link LoginCompleteCallBack}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    void handleAuthorizationResponse(LiSSOAuthResponse response, LiAuthRestClient authRestClient, LoginCompleteCallBack loginCompleteCallBack) throws LiRestResponseException;

    /**
     * Fetching details of User after login is completed.
     * @param loginCompleteCallBack {@link LoginCompleteCallBack}
     */
    @VisibleForTesting
    void getUserAfterTokenResponse(LoginCompleteCallBack loginCompleteCallBack);

    /**
     * Use to fetch Refresh token in an Async call.
     * @param callback {@link LiTokenResponseCallback}
     * @throws LiRestResponseException
     */
    void performRefreshTokenRequest(@NonNull LiTokenResponseCallback callback) throws LiRestResponseException;

    /**
     * Use to fetch Refresh token in an Sync call.
     * @return The Token response {@link LiTokenResponse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    LiTokenResponse performSyncRefreshTokenRequest() throws LiRestResponseException;

    /**
     * Performs post authorization flow.
     * @param isLoginSuccess Says whether login is success and user data has been retrieved.
     * @param  responseCode HTTP error code that gets returned in the intent
     */
    void enablePostAuthorizationFlows(boolean isLoginSuccess, int responseCode);

    /**
     * Disposes the LiAuthServiceImpl after auth flow.
     */
    void dispose();

    /**
     * Callback interface for token endpoint requests.
     */
    interface FreshTokenCallBack {
        void onFreshTokenFetched(boolean isFetched);
    }

    /**
     * Callback interface after login process is done.
     */
    interface LoginCompleteCallBack {
        void onLoginComplete(LiAuthorizationException authException, boolean isSuccess);
    }

    interface LiTokenResponseCallback {
        /**
         * Invoked when the request completes successfully or fails.
         * <p>
         * <p>Exactly one of {@code response} or {@code ex} will be non-null. If
         * {@code response} is {@code null}, a failure occurred during the request. This can
         * happen if a bad URI was provided, no connection to the server could be established, or
         * the response JSON was incomplete or badly formatted.
         *
         * @param response the retrieved token response, if successful; {@code null} otherwise.
         * @param ex       a description of the failure, if one occurred: {@code null} otherwise.
         * @see LiAuthorizationException.TokenRequestErrors
         */
        void onTokenRequestCompleted(@Nullable LiTokenResponse response,
                                     @Nullable Exception ex);
    }
}
