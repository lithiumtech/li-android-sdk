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

package com.lithium.community.android.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiAppSdkSettings;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.notification.FirebaseTokenProvider;
import com.lithium.community.android.notification.LiNotificationProviderImpl;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiAuthAsyncRequestCallback;
import com.lithium.community.android.rest.LiAuthRestClient;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.LiUUIDUtils;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.UUID;

/**
 * Implementation class for LiAuthService. Starts login flow, performs authorization, fetches access and refresh tokens.
 *
 * @author shoureya.kant
 */
public class LiAuthServiceImpl implements LiAuthService {

    private static final String REFRESH_TOKEN_FETCH_ERROR = "Couldn't refresh access token from refresh token";

    @NonNull
    private final LiSDKManager sdkManager;

    @VisibleForTesting
    protected Context context;

    private boolean isDisposed = false;

    /**
     * Default constructor for Non-SSO flow.
     *
     * @param context Android context.
     * @param manager Li SDK Manager.
     */
    public LiAuthServiceImpl(@NonNull Context context, @NonNull LiSDKManager manager) {
        this.context = LiCoreSDKUtils.checkNotNull(context, "context was null");
        this.sdkManager = LiCoreSDKUtils.checkNotNull(manager, "Li SDK Manager was null");
    }

    /**
     * Default constructor for Non-SSO flow.
     *
     * @param context Android context.
     * @deprecated use {@link #LiAuthServiceImpl(Context, LiSDKManager)} instead
     */
    @Deprecated
    public LiAuthServiceImpl(@NonNull Context context) {
        this.context = LiCoreSDKUtils.checkNotNull(context);
        sdkManager = LiCoreSDKUtils.checkNotNull(LiSDKManager.getInstance(), "Li SDK Manager was null");
    }

    private static Uri buildRefreshTokenUri(@NonNull LiAppCredentials credentials) {
        LiCoreSDKUtils.checkNotNull(credentials, "credentials");
        return credentials.getCommunityUri()
                .buildUpon()
                .appendPath(credentials.getTenantId())
                .appendPath("api")
                .appendPath("2.0")
                .appendPath("auth")
                .appendPath("refreshToken")
                .build();
    }

    @Override
    public void startLoginFlow() {
        startLoginFlow(null);
    }

    /**
     * starts login flow
     */
    @Override
    public void startLoginFlow(String ssoToken) {
        LiAppCredentials liAppCredentials = this.sdkManager.getCredentials();
        LiSSOAuthorizationRequest request = new LiSSOAuthorizationRequest();
        request.setClientId(liAppCredentials.getClientKey());
        request.setRedirectUri(liAppCredentials.getRedirectUri());
        request.setState(UUID.randomUUID().toString());
        request.setUri(String.valueOf(liAppCredentials.getAuthorizeUri()));
        if (ssoToken != null) {
            request.setSsoToken(ssoToken);
            request.setUri(liAppCredentials.getSsoAuthorizeUri());
            try {
                performSSOAuthorizationRequest(request);
            } catch (LiRestResponseException e) {
                enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            }
        } else {
            performAuthorizationRequest(request);
        }

        dispose();
    }

    /**
     * Performs Authorization (Non SSO flow).
     *
     * @param request {@link LiSSOAuthorizationRequest}
     */
    @Override
    public void performAuthorizationRequest(@NonNull LiSSOAuthorizationRequest request) {
        Uri requestUri = request.toUri();
        Intent intent;
        LiAuthRequestStore.getInstance().addAuthRequest(request);
        intent = new Intent(context, LiLoginActivity.class);
        intent.setData(requestUri);
        context.startActivity(intent);
        dispose();
    }

    /**
     * Performs Authorization (SSO flow).
     *
     * @param request {@link LiSSOAuthorizationRequest }
     * @throws LiRestResponseException throws {@link LiRestResponseException}
     */
    @Override
    public void performSSOAuthorizationRequest(@NonNull LiSSOAuthorizationRequest request) throws LiRestResponseException {
        checkIfDisposed();

        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "performing SSO Authorization Request");

        final LiAuthRestClient client = getLiAuthRestClient();
        LiAuthRequestStore.getInstance().addAuthRequest(request);

        client.authorizeAsync(context, request, new LiAuthAsyncRequestCallback<LiBaseResponse>() {
            @Override
            public void onSuccess(LiBaseResponse response) {
                if (response == null || response.getHttpCode() != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                    int httpCode = LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR;
                    if (response != null) {
                        httpCode = response.getHttpCode();
                    }
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, String.format("Response received for unknown request with http code %d", httpCode));
                    enablePostAuthorizationFlows(false, httpCode);
                    return;
                }
                LiSSOAuthResponse liSsoAuthResponse = getLiSSOAuthResponse(response);
                String state = liSsoAuthResponse.getState();
                LiSSOAuthorizationRequest request = LiAuthRequestStore.getInstance().getLiOriginalRequest(state);
                if (request == null) {
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, String.format("Response received for unknown request with state %s", state));
                    enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_FORBIDDEN);
                    return;
                }
                liSsoAuthResponse.setJsonString(String.valueOf(response.getData().get("data")));
                try {
                    handleAuthorizationResponse(liSsoAuthResponse, client, new LoginCompleteCallBack() {
                        @Override
                        public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "SSO Authorization login complete. It was " + String.valueOf(isSuccess));
                            enablePostAuthorizationFlows(isSuccess, LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL);
                        }
                    });
                } catch (LiRestResponseException e) {
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "SSO Authorization response handling failed");
                    e.printStackTrace();
                    enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "SSO Authorization Request failed");
                e.printStackTrace();
                enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            }
        });
        dispose();
    }

    /**
     * Fetching json from response and converting it back to required model.
     *
     * @param response {@link LiBaseResponse}
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
     * @param response {@link LiSSOAuthResponse}
     * @param client   {@link LiAuthRestClient}
     * @param callback {@link LoginCompleteCallBack}
     * @throws LiRestResponseException throws {@link LiRestResponseException}
     */
    @Override
    public void handleAuthorizationResponse(LiSSOAuthResponse response, LiAuthRestClient client, final LoginCompleteCallBack callback)
            throws LiRestResponseException {
        this.sdkManager.persistAuthState(context, response);
        LiSSOTokenRequest request = new LiSSOTokenRequest();
        LiAppCredentials credentials = this.sdkManager.getCredentials();
        request.setClientId(credentials.getClientKey());
        request.setRedirectUri(credentials.getRedirectUri());
        request.setAccessCode(response.getAuthCode());
        request.setClientSecret(credentials.getClientSecret());
        request.setGrantType("authorization_code");
        try {
            Uri uri = credentials.getCommunityUri()
                    .buildUpon()
                    .appendPath(credentials.getTenantId())
                    .appendPath("api")
                    .appendPath("2.0")
                    .appendPath("auth")
                    .appendPath("accessToken")
                    .build();

            request.setUri(uri);

            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "Requesting access token");

            client.accessTokenAsync(context, request, new LiAuthAsyncRequestCallback<LiBaseResponse>() {
                @Override
                public void onSuccess(LiBaseResponse response) {
                    if (response == null || response.getHttpCode() != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "API returned invalid response for access token");
                        final int code = response != null ? response.getHttpCode() : LiAuthorizationException.GeneralErrors.SERVER_ERROR.code;
                        LiAuthorizationException exception = LiAuthorizationException.generalEx(code, "API returned invalid response for access token");
                        callback.onLoginComplete(exception, false);
                        return;
                    }

                    Gson gson = LiClientManager.getRestClient().getGson();
                    JsonObject responseObject = response.getData();

                    if (responseObject.has("data")) {
                        JsonObject data = responseObject.get("data").getAsJsonObject();
                        LiTokenResponse accessToken = gson.fromJson(data, LiTokenResponse.class);

                        // set expiry time
                        long expiresAt = LiCoreSDKUtils.addCurrentTime(accessToken.getExpiresIn());
                        data.addProperty("expiresAt", expiresAt);
                        accessToken.setExpiresAt(expiresAt);

                        // serialize the token and save it
                        accessToken.setJsonString(String.valueOf(data));
                        sdkManager.persistAuthState(context, accessToken);

                        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "Successfully fetched SDK settings");
                        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "Getting user details post login");

                        getUserAfterTokenResponse(callback);
                    } else {
                        Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "API returned invalid response json for access token");
                        callback.onLoginComplete(LiAuthorizationException.generalEx(response.getHttpCode(), "Access Token API call invalid response."), false);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Access token request failed");
                    e.printStackTrace();
                    LiAuthorizationException ex = LiAuthorizationException.generalEx(LiAuthorizationException.GeneralErrors.SERVER_ERROR.code, e.getMessage());
                    callback.onLoginComplete(ex, false);
                }
            });
        } catch (RuntimeException e) {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Exception thrown while requesting Access token");
            e.printStackTrace();
            throw LiRestResponseException.runtimeError(e.getMessage());
        }
    }

    /**
     * Fetches setting details from the community post user login
     *
     * @param callBack {@link LoginCompleteCallBack}
     */
    private void getSDKSettings(final LoginCompleteCallBack callBack) {
        try {
            String clientId = LiUUIDUtils.toUUID(this.sdkManager.getCredentials().getClientKey().getBytes()).toString();
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiSdkSettingsClientRequestParams(context, clientId);
            LiClient settingsClient = LiClientManager.getSdkSettingsClient(liClientRequestParams);
            settingsClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
                    if (response != null && response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Gson gson = LiClientManager.getRestClient().getGson();
                        JsonObject responseJsonObject = response.getJsonObject();
                        if (responseJsonObject.has("data")) {
                            JsonObject dataObj = responseJsonObject.get("data").getAsJsonObject();
                            if (dataObj.has("items")) {
                                JsonArray items = dataObj.get("items").getAsJsonArray();
                                if (!items.isJsonNull() && items.size() > 0) {
                                    LiAppSdkSettings liAppSdkSettings = gson.fromJson(items.get(0), LiAppSdkSettings.class);
                                    if (liAppSdkSettings != null) {
                                        sdkManager.putInSecuredPreferences(context, LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS, liAppSdkSettings
                                                .getAdditionalInformation());
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiAuthServiceImpl - API returned invalid response for SDK settings");
                    }

                    try {
                        FirebaseTokenProvider tokenProvider = sdkManager.getFirebaseTokenProvider();
                        if (tokenProvider != null) {
                            String deviceId = tokenProvider.getDeviceToken();
                            new LiNotificationProviderImpl().onIdRefresh(deviceId, context);
                        }
                    } catch (LiRestResponseException e) {
                        Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Exception in generating device token");
                        e.printStackTrace();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthServiceImpl - successfully fetched user and settings.");
                    callBack.onLoginComplete(null, true);
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiAuthServiceImpl - request failed for SDK settings");
                    exception.printStackTrace();
                    callBack.onLoginComplete(null, true);
                }
            });
        } catch (LiRestResponseException e) {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiAuthServiceImpl - request exception for SDK settings");
            e.printStackTrace();
            callBack.onLoginComplete(null, true);
        }
    }

    /**
     * Fetches user details after Tokens are received.
     *
     * @param callBack {@link LoginCompleteCallBack}
     */
    @Override
    @VisibleForTesting
    public void getUserAfterTokenResponse(final LoginCompleteCallBack callBack) {
        try {
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiUserDetailsClientRequestParams(context, "self");
            LiClientManager.getUserDetailsClient(liClientRequestParams).processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiGetClientResponse liClientResponse) {
                    if (liClientResponse != null && liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL
                            && liClientResponse.getResponse() != null && !liClientResponse.getResponse().isEmpty()) {
                        LiUser user = (LiUser) liClientResponse.getResponse().get(0).getModel();
                        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthServiceImpl - setting logged in user");
                        sdkManager.setLoggedInUser(context, user);
                    }
                    Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthServiceImpl - Successfully fetched user details");
                    getSDKSettings(callBack);
                }

                @Override
                public void onError(Exception e) {
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiAuthServiceImpl - API returned invalid response for user details");
                    e.printStackTrace();
                    getSDKSettings(callBack);
                    LiAuthorizationException ex = LiAuthorizationException.generalEx(LiAuthorizationException.GeneralErrors.SERVER_ERROR.code, e.getMessage());
                    callBack.onLoginComplete(ex, false);
                }
            });
        } catch (LiRestResponseException e) {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiAuthServiceImpl - request failed for user details");
            e.printStackTrace();
            getSDKSettings(callBack);
        }
    }

    /**
     * Fetching fresh access token from refresh token. Its Asyn call.
     *
     * @param callback {@link LiTokenResponseCallback}
     * @throws LiRestResponseException throws {@link LiRestResponseException}
     */
    @Override
    public void performRefreshTokenRequest(@NonNull final LiTokenResponseCallback callback) throws LiRestResponseException {
        checkIfDisposed();
        final LiAuthRestClient authRestClient = getLiAuthRestClient();
        final LiAppCredentials credentials = this.sdkManager.getCredentials();
        LiRefreshTokenRequest request = new LiRefreshTokenRequest();
        request.setClientId(credentials.getClientKey());
        request.setClientSecret(credentials.getClientSecret());
        request.setGrantType("refresh_token");
        request.setRefreshToken(this.sdkManager.getRefreshToken());

        Uri uri = buildRefreshTokenUri(credentials);

        request.setUri(uri);

        try {
            authRestClient.refreshTokenAsync(context, request, new LiAuthAsyncRequestCallback<LiBaseResponse>() {

                @Override
                public void onSuccess(LiBaseResponse response) {
                    if (response == null || response.getHttpCode() != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        callback.onTokenRequestCompleted(null, new Exception(REFRESH_TOKEN_FETCH_ERROR));
                        return;
                    }
                    try {
                        Gson gson = LiClientManager.getRestClient().getGson();
                        JsonObject responseData = response.getData().getAsJsonObject();
                        if (responseData.has("data")) {
                            JsonObject tokenData = responseData.get("data").getAsJsonObject();

                            LiTokenResponse tokenResponse = gson.fromJson(tokenData, LiTokenResponse.class);

                            long expiresAt = LiCoreSDKUtils.addCurrentTime(tokenResponse.getExpiresIn());
                            tokenResponse.setExpiresAt(expiresAt);
                            tokenData.addProperty("expiresAt", expiresAt);
                            tokenResponse.setJsonString(tokenData.toString());

                            callback.onTokenRequestCompleted(tokenResponse, null);
                        } else {
                            String error = "Refresh Token API invalid response.";
                            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, error);
                            callback.onTokenRequestCompleted(null, new InvalidObjectException(error));
                        }
                    } catch (RuntimeException e) {
                        String error = "Refresh Token API invalid response.";
                        Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, error);
                        e.printStackTrace();
                        callback.onTokenRequestCompleted(null, e);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Refresh Token API call failed.");
                    exception.printStackTrace();
                    callback.onTokenRequestCompleted(null, exception);
                }

            });
        } catch (RuntimeException e) {
            Log.d(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Refresh Token API request aborted.");
            e.printStackTrace();
            callback.onTokenRequestCompleted(null, e);
        }
        dispose();
    }

    /**
     * Fetching fresh access token from refresh token. Its a Sync call.
     *
     * @return Refresh Token {@link LiTokenResponse}
     * @throws LiRestResponseException throws {@link LiRestResponseException}
     */
    @Override
    public LiTokenResponse performSyncRefreshTokenRequest() throws LiRestResponseException {

        final LiAuthRestClient client = getLiAuthRestClient();
        final LiAppCredentials credentials = this.sdkManager.getCredentials();
        LiRefreshTokenRequest request = new LiRefreshTokenRequest();
        request.setClientId(credentials.getClientKey());
        request.setClientSecret(credentials.getClientSecret());
        request.setGrantType("refresh_token");
        request.setRefreshToken(this.sdkManager.getRefreshToken());

        Uri uri = buildRefreshTokenUri(credentials);

        request.setUri(uri);

        LiTokenResponse response = null;
        LiBaseResponse resp = client.refreshTokenSync(context, request);
        Gson gson = LiClientManager.getRestClient().getGson();

        JsonObject responseData = resp.getData();
        try {
            if (responseData.has("data")) {
                JsonObject tokenData = responseData.get("data").getAsJsonObject();

                response = gson.fromJson(tokenData, LiTokenResponse.class);

                long expiresAt = LiCoreSDKUtils.addCurrentTime(response.getExpiresIn());
                response.setExpiresAt(expiresAt);
                tokenData.addProperty("expiresAt", expiresAt);
                response.setJsonString(tokenData.toString());

                response.setJsonString(tokenData.toString());
            }
        } catch (RuntimeException ex) {
            String error = "Refresh Token API invalid response.";
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, error);
            ex.printStackTrace();
            throw LiRestResponseException.runtimeError(error);
        }
        if (response == null) {
            throw LiRestResponseException.runtimeError("Refresh Token API invalid response.");
        }
        return response;
    }

    /**
     * Process post Authorization.
     *
     * @param isLoginSuccess Checks if login is complete i.e user details has been fetched.
     * @param responseCode   HTTP error code that gets returned in the intent
     */
    @Override
    public void enablePostAuthorizationFlows(boolean isLoginSuccess, int responseCode) {
        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "Enabling post authorization flow. Login success is " + String.valueOf(isLoginSuccess));
        LiCoreSDKUtils.sendLoginBroadcast(context, isLoginSuccess, responseCode);
        this.dispose();
    }

    /**
     * Killing the service.
     */
    @Override
    public void dispose() {
        if (isDisposed) {
            return;
        }
        isDisposed = true;
    }

    private void checkIfDisposed() {
        if (isDisposed) {
            throw new IllegalStateException("Service has been disposed and rendered inoperable");
        }
    }
}
