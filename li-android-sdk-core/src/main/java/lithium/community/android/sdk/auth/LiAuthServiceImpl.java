/*
 * LiAuthServiceImpl.java
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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.UUID;

import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.model.response.LiAppSdkSettings;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.notification.LiNotificationProviderImpl;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiAuthAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiAuthRestClient;
import lithium.community.android.sdk.rest.LiBaseResponse;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiGetClientResponse;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiUUIDUtils;

import static lithium.community.android.sdk.auth.LiAuthConstants.LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS;

/**
 * Implementation class for LiAuthService. Starts login flow, performs authorization, fetches access and refresh tokens.
 * Created by shoureya.kant on 9/12/16.
 */
public class LiAuthServiceImpl implements LiAuthService {

    @VisibleForTesting
    protected Context mContext;

    private String ssoToken;

    private boolean mDisposed = false;
    private LiDeviceTokenProvider liDeviceTokenProvider;
    /**
     * Constructor for SSO flow.
     *
     * @param context  {@link Context}
     * @param ssoToken This is the SSO Token.
     */
    public LiAuthServiceImpl(@NonNull Context context, @NonNull String ssoToken, LiDeviceTokenProvider liDeviceTokenProvider) {
        mContext = LiCoreSDKUtils.checkNotNull(context);
        this.ssoToken = ssoToken;
        this.liDeviceTokenProvider = liDeviceTokenProvider;
    }

    /**
     * Constructor for non SSO flow
     *
     * @param context {@link Context}
     */
    public LiAuthServiceImpl(@NonNull Context context, LiDeviceTokenProvider liDeviceTokenProvider) {
        mContext = LiCoreSDKUtils.checkNotNull(context);
        this.liDeviceTokenProvider = liDeviceTokenProvider;
    }

    /**
     * Constructor for non SSO flow
     *
     * @param context {@link Context}
     */
    public LiAuthServiceImpl(@NonNull Context context) {
        mContext = LiCoreSDKUtils.checkNotNull(context);
    }

    /**
     * starts login flow
     */
    @Override
    public void startLoginFlow() {
        LiAppCredentials liAppCredentials = LiSDKManager.getInstance().getLiAppCredentials();
        LiSSOAuthorizationRequest authorizationRequest = new LiSSOAuthorizationRequest();
        authorizationRequest.setClientId(liAppCredentials.getClientKey());
        authorizationRequest.setRedirectUri(liAppCredentials.getRedirectUri());
        authorizationRequest.setState(UUID.randomUUID().toString());
        authorizationRequest.setUri(String.valueOf(liAppCredentials.getAuthorizeUri()));
        if (ssoToken != null) {
            authorizationRequest.setSsoToken(ssoToken);
            authorizationRequest.setUri(liAppCredentials.getSsoAuthorizeUri());
            try {
                performSSOAuthorizationRequest(authorizationRequest);
            } catch (LiRestResponseException e) {
                enablePostAuthorizationFlows(false
                        , LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            }
        } else {
            performAuthorizationRequest(authorizationRequest);
        }

        dispose();
    }

    /**
     * Performs Authorization (Non SSO flow).
     *
     * @param request {@link LiSSOAuthorizationRequest}
     */
    @Override
    public void performAuthorizationRequest(
            @NonNull LiSSOAuthorizationRequest request) {
        Uri requestUri = request.toUri();
        Intent intent;
        LiAuthRequestStore.getInstance().addAuthRequest(request);
        intent = new Intent(mContext, LiLoginActivity.class);
        intent.setData(requestUri);
        mContext.startActivity(intent);
        dispose();
    }


    /**
     * Performs Authorization (SSO flow).
     *
     * @param request {@link LiSSOAuthorizationRequest }
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @Override
    public void performSSOAuthorizationRequest(@NonNull LiSSOAuthorizationRequest request) throws LiRestResponseException {
        checkIfDisposed();
        final LiAuthRestClient authRestClient = getLiAuthRestClient();

        LiAuthRequestStore.getInstance().addAuthRequest(request);
        try {
            authRestClient.authorizeAsync(request, new LiAuthAsyncRequestCallback<LiBaseResponse>() {
                @Override
                public void onSuccess(LiBaseResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        enablePostAuthorizationFlows(false, response.getHttpCode());
                        return;
                    }
                    LiSSOAuthResponse liSsoAuthResponse = getLiSSOAuthResponse(response);
                    String state = liSsoAuthResponse.getState();
                    LiSSOAuthorizationRequest request = LiAuthRequestStore.getInstance().getLiOriginalRequest(state);
                    if (request == null) {
                        Log.e(LOG_TAG, String.format(
                                "Response received for unknown request with state %s", state));
                        enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_FORBIDDEN);
                        return;
                    }
                    liSsoAuthResponse.setJsonString(String.valueOf(response.getData().get("data")));
                    handleAuthorizationResponse(liSsoAuthResponse, authRestClient, new LoginCompleteCallBack() {

                        @Override
                        public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                            enablePostAuthorizationFlows(isSuccess, LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
                    Log.e(LOG_TAG, "Error Fetching Auth Code: " + e);
                }
            });
        } catch (RuntimeException e) {
            throw LiRestResponseException.runtimeError(e.getMessage());
        }
        dispose();
    }

    /**
     * Fetching json from response and converting it back to required model.
     *
     * @param response {@Link LiBaseResponse}
     * @return Response of Authorization request.
     */
    private LiSSOAuthResponse getLiSSOAuthResponse(LiBaseResponse response) {
        Gson gson = new Gson();
        return gson.fromJson(response.getData().get("data"), LiSSOAuthResponse.class);
    }

    /**
     * provided AuthrestClient for making network calls
     *
     * @return LiAuthRestClient {@link LiAuthRestClient}
     */
    @VisibleForTesting
    @NonNull
    LiAuthRestClient getLiAuthRestClient() {
        return new LiAuthRestClient();
    }

    /**
     * Fetch Access Token from Auth Code received after Authorization Process.
     *
     * @param response              {@link LiSSOAuthResponse}
     * @param authRestClient        {@link LiAuthRestClient}
     * @param loginCompleteCallBack {@link LoginCompleteCallBack}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @Override
    public void handleAuthorizationResponse(LiSSOAuthResponse response, LiAuthRestClient authRestClient, final LoginCompleteCallBack loginCompleteCallBack) throws LiRestResponseException {
        LiSDKManager.getInstance().persistAuthState(mContext, response);
        LiSSOTokenRequest liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setClientId(LiSDKManager.getInstance().getLiAppCredentials().getClientKey());
        liSSOTokenRequest.setRedirectUri(LiSDKManager.getInstance().getLiAppCredentials().getRedirectUri());
        liSSOTokenRequest.setAcceesCode(response.getAuthCode());
        liSSOTokenRequest.setClientSecret(LiSDKManager.getInstance().getLiAppCredentials().getClientSecret());
        liSSOTokenRequest.setGrantType("authorization_code");
        String uri = "https://" + response.getApiProxyHost() + "/auth/v1/accessToken";
        liSSOTokenRequest.setUri(Uri.parse(uri));

        if (response != null) {
            try {
                authRestClient.accessTokenAsync(liSSOTokenRequest, new LiAuthAsyncRequestCallback<LiBaseResponse>() {
                    @Override
                    public void onSuccess(LiBaseResponse response) throws LiRestResponseException {
                        Gson gson = new Gson();
                        JsonObject data = LiClientManager.getRestClient().getGson().fromJson(response.getData(), JsonObject.class);
                        if (data != null && data.has("response") && data.get("response").getAsJsonObject().has("data")) {
                            LiTokenResponse tokenResponse = gson.fromJson(response.getData().get("response").getAsJsonObject().get("data"), LiTokenResponse.class);
                            tokenResponse.setExpiresAt(LiCoreSDKUtils.getTime(tokenResponse.getExpiresIn()));
                            JsonObject obj = response.getData().get("response").getAsJsonObject().get("data").getAsJsonObject();
                            obj.addProperty("expiresAt", tokenResponse.getExpiresAt());
                            tokenResponse.setJsonString(String.valueOf(obj));
                            LiSDKManager.getInstance().persistAuthState(mContext, tokenResponse);
                            getUserAfterTokenResponse(loginCompleteCallBack);
                        }
                        else {
                            loginCompleteCallBack.onLoginComplete(LiAuthorizationException.generalEx(
                                    data.get("statusCode").getAsInt(),
                                    data.get("message").getAsString()), false);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(LOG_TAG, "Error fetching access token: " + e);
                        loginCompleteCallBack.onLoginComplete(LiAuthorizationException.generalEx(
                                LiAuthorizationException.GeneralErrors.SERVER_ERROR.code, e.getMessage()), false);

                    }
                });
            } catch (RuntimeException e) {
                throw LiRestResponseException.runtimeError(e.getMessage());
            }
        } else {
            loginCompleteCallBack.onLoginComplete(null, false);
        }
    }

    /**
     * Fetches setting details from the community post user login
     *
     * @param loginCompleteCallBack {@Link LoginCompleteCallBack}
     */
    private void getSDKSettings(final LoginCompleteCallBack loginCompleteCallBack) {
        try {
            if (this.liDeviceTokenProvider != null && !TextUtils.isEmpty(this.liDeviceTokenProvider.getDeviceId())) {
                new LiNotificationProviderImpl().onIdRefresh(this.liDeviceTokenProvider.getDeviceId(), mContext);
            }

            String clientId = LiUUIDUtils.toUUID(LiSDKManager.getInstance().getLiAppCredentials().getClientKey().getBytes()).toString();
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiSdkSettingsClientRequestParams(mContext, clientId);
            LiClient settingsClient = LiClientManager.getSdkSettingsClient(liClientRequestParams);
            settingsClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request,
                                      LiGetClientResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Gson gson = new Gson();
                        JsonArray items = response.getJsonObject().get("data")
                                .getAsJsonObject().get("items").getAsJsonArray();
                        if (!items.isJsonNull() && items.size() > 0) {
                            LiAppSdkSettings liAppSdkSettings =
                                    gson.fromJson(items.get(0), LiAppSdkSettings.class);
                            if (liAppSdkSettings != null) {
                                LiSDKManager.getInstance().putInSecuredPreferences(
                                        mContext,
                                        LI_DEFAULT_SDK_SETTINGS,
                                        liAppSdkSettings.getAdditionalInformation());
                            }
                        }
                    }

                    loginCompleteCallBack.onLoginComplete(null, true);
                }

                @Override
                public void onError(Exception exception) {
                    loginCompleteCallBack.onLoginComplete(null, true);
                    Log.e(LiAuthConstants.LOG_TAG, exception.getMessage());
                }
            });
        } catch (LiRestResponseException e) {
            loginCompleteCallBack.onLoginComplete(null, true);
            Log.e(LiAuthConstants.LOG_TAG, e.getMessage());
        }
    }

    /**
     * Fetches user details after Tokens are received.
     *
     * @param loginCompleteCallBack {@link LoginCompleteCallBack}
     */
    @Override
    @VisibleForTesting
    public void getUserAfterTokenResponse(final LoginCompleteCallBack loginCompleteCallBack) {
        try {
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiUserDetailsClientRequestParams(mContext, "self");
            LiClientManager.getUserDetailsClient(liClientRequestParams)
                    .processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                        @Override
                        public void onSuccess(LiBaseRestRequest request, LiGetClientResponse
                                liClientResponse) throws LiRestResponseException {
                            LiUser user = (LiUser) liClientResponse.getResponse().get(0).getModel();
                            LiSDKManager.getInstance().setLoggedInUser(mContext, user);
                            getSDKSettings(loginCompleteCallBack);
                        }

                        @Override
                        public void onError(Exception e) {
                            getSDKSettings(loginCompleteCallBack);
                            Log.e(LOG_TAG, "Unable to fetch user details: " + e);
                            loginCompleteCallBack.onLoginComplete(LiAuthorizationException.generalEx(
                                    LiAuthorizationException.GeneralErrors.SERVER_ERROR.code, e.getMessage()), false);
                        }
                    });
        } catch (LiRestResponseException e) {
            Log.e(LOG_TAG, "ERROR: " + e);
            getSDKSettings(loginCompleteCallBack);
        }
    }


    /**
     * Fetching fresh access token from refresh token. Its Asyn call.
     *
     * @param callback {@link LiTokenResponseCallback}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @Override
    public void performRefreshTokenRequest(@NonNull final LiTokenResponseCallback callback) throws LiRestResponseException {
        checkIfDisposed();
        final LiAuthRestClient authRestClient = getLiAuthRestClient();
        LiRefreshTokenRequest liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientId(LiSDKManager.getInstance().getLiAppCredentials().getClientKey());
        liRefreshTokenRequest.setClientSecret(LiSDKManager.getInstance().getLiAppCredentials().getClientSecret());
        liRefreshTokenRequest.setGrantType("refresh_token");
        liRefreshTokenRequest.setRefreshToken(LiSDKManager.getInstance().getRefreshToken());
        String uri = "https://" + LiSDKManager.getInstance().getProxyHost() + "/auth/v1/refreshToken";
        liRefreshTokenRequest.setUri(Uri.parse(uri));

        try {
            authRestClient.refreshTokenAsync(liRefreshTokenRequest, new LiAuthAsyncRequestCallback<LiBaseResponse>() {

                @Override
                public void onSuccess(LiBaseResponse response) throws LiRestResponseException {
                    System.out.println(response);
                    Gson gson = new Gson();
                    JsonObject data = gson.fromJson(response.getData(), JsonObject.class);
                    if (data.has("response") && data.get("response").getAsJsonObject().has("data")) {
                        LiTokenResponse tokenResponse = gson.fromJson(response.getData().get("response").getAsJsonObject().get("data"), LiTokenResponse.class);
                        tokenResponse.setExpiresAt(LiCoreSDKUtils.getTime(tokenResponse.getExpiresIn()));
                        JsonObject obj = response.getData().get("response").getAsJsonObject().get("data").getAsJsonObject();
                        obj.addProperty("expiresAt", tokenResponse.getExpiresAt());
                        tokenResponse.setJsonString(String.valueOf(obj));
                        callback.onTokenRequestCompleted(tokenResponse, null);
                    }
                    else {
                        callback.onTokenRequestCompleted(null, new Exception("Couldn't fetch access token from access code"));
                    }
                }

                @Override
                public void onError(Exception exception) {
                    callback.onTokenRequestCompleted(null, exception);
                }

            });
        } catch (RuntimeException e) {
            throw LiRestResponseException.runtimeError(e.getMessage());
        }
        dispose();
    }

    /**
     * Fetching fresh access token from refresh token. Its Syn call.
     *
     * @return Refresh Token {@link LiTokenResponse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @Override
    public LiTokenResponse performSyncRefreshTokenRequest() throws LiRestResponseException {

        final LiAuthRestClient authRestClient = getLiAuthRestClient();
        LiRefreshTokenRequest liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientId(LiSDKManager.getInstance().getLiAppCredentials().getClientKey());
        liRefreshTokenRequest.setClientSecret(LiSDKManager.getInstance().getLiAppCredentials().getClientSecret());
        liRefreshTokenRequest.setGrantType("refresh_token");
        liRefreshTokenRequest.setRefreshToken(LiSDKManager.getInstance().getRefreshToken());
        String uri = "https://" + LiSDKManager.getInstance().getProxyHost() + "/auth/v1/refreshToken";
        liRefreshTokenRequest.setUri(Uri.parse(uri));
        try {
            LiBaseResponse resp = authRestClient.refreshTokenSync(liRefreshTokenRequest);
            System.out.println(resp);
            Gson gson = new Gson();
            LiTokenResponse tokenResponse = gson.fromJson(resp.getData().get("response").getAsJsonObject().get("data"), LiTokenResponse.class);
            tokenResponse.setExpiresAt(LiCoreSDKUtils.getTime(tokenResponse.getExpiresIn()));
            JsonObject obj = resp.getData().get("response").getAsJsonObject().get("data").getAsJsonObject();
            obj.addProperty("expiresAt", tokenResponse.getExpiresAt());
            tokenResponse.setJsonString(String.valueOf(obj));
            return tokenResponse;
        } catch (RuntimeException e) {
            throw LiRestResponseException.runtimeError(e.getMessage());
        }
    }

    /**
     * Process post Authorization.
     *
     * @param isLoginSuccess Checks if login is complete i.e user details has been fetched.
     * @param  responseCode HTTP error code that gets returned in the intent
     */
    @Override
    public void enablePostAuthorizationFlows(boolean isLoginSuccess, int responseCode) {
        LiCoreSDKUtils.sendLoginBroadcast(mContext, isLoginSuccess, responseCode);
        this.dispose();
    }

    /**
     * Killing the service.
     */
    @Override
    public void dispose() {
        if (mDisposed) {
            return;
        }
        mDisposed = true;
    }

    private void checkIfDisposed() {
        if (mDisposed) {
            throw new IllegalStateException("Service has been disposed and rendered inoperable");
        }
    }


}
