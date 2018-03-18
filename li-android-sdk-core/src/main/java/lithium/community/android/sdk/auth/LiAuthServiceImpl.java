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
import com.google.gson.JsonElement;
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
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_LOG_TAG;

/**
 * Implementation class for LiAuthService. Starts login flow, performs authorization, fetches access and refresh tokens.
 * Created by shoureya.kant on 9/12/16.
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
                    enablePostAuthorizationFlows(false, httpCode);
                    return;
                }
                LiSSOAuthResponse liSsoAuthResponse = getLiSSOAuthResponse(response);
                String state = liSsoAuthResponse.getState();
                LiSSOAuthorizationRequest request = LiAuthRequestStore.getInstance().getLiOriginalRequest(state);
                if (request == null) {
                    Log.e(LOG_TAG, String.format("Response received for unknown request with state %s", state));
                    enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_FORBIDDEN);
                    return;
                }
                liSsoAuthResponse.setJsonString(String.valueOf(response.getData().get("data")));
                try {
                    handleAuthorizationResponse(liSsoAuthResponse, client, new LoginCompleteCallBack() {
                        @Override
                        public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                            enablePostAuthorizationFlows(isSuccess, LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL);
                        }
                    });
                } catch (LiRestResponseException e) {
                    enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
                    Log.e(LOG_TAG, "Error Fetching Auth Code: " + e);
                }
            }

            @Override
            public void onError(Exception e) {
                enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
                Log.e(LOG_TAG, "Error Fetching Auth Code: " + e);
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
            String proxyHost;
            if (response.getApiProxyHost() == null) {
                proxyHost = credentials.getApiGatewayHost().toString();
            } else {
                proxyHost = response.getApiProxyHost();
            }
            String uri = "https://" + proxyHost + "/auth/v1/accessToken";
            request.setUri(Uri.parse(uri));
            client.accessTokenAsync(context, request, new LiAuthAsyncRequestCallback<LiBaseResponse>() {
                @Override
                public void onSuccess(LiBaseResponse response) throws LiRestResponseException {
                    if (response == null || response.getHttpCode() != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Log.e(LOG_TAG, "Error fetching access token");
                        callback.onLoginComplete(LiAuthorizationException.generalEx(
                                LiAuthorizationException.GeneralErrors.SERVER_ERROR.code, "Error fetching access token"), false);
                        return;
                    }
                    Gson gson = new Gson();
                    JsonObject data = LiClientManager.getRestClient().getGson().fromJson(response.getData(), JsonObject.class);
                    if (data != null && data.has("response")) {
                        JsonObject responseObject = data.get("response").getAsJsonObject();
                        if (responseObject.has("data")) {
                            JsonElement dataObjElement = responseObject.get("data");
                            LiTokenResponse tokenResponse = gson.fromJson(dataObjElement, LiTokenResponse.class);
                            tokenResponse.setExpiresAt(LiCoreSDKUtils.getTime(tokenResponse.getExpiresIn()));
                            JsonObject dataJsonObject = dataObjElement.getAsJsonObject();
                            dataJsonObject.addProperty("expiresAt", tokenResponse.getExpiresAt());
                            tokenResponse.setJsonString(String.valueOf(dataJsonObject));
                            sdkManager.persistAuthState(context, tokenResponse);
                            getUserAfterTokenResponse(callback);
                        }
                    } else {
                        callback.onLoginComplete(LiAuthorizationException.generalEx(response.getHttpCode(), "Error fetching accessToken"),
                                false);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(LOG_TAG, "Error fetching access token: " + e);
                    LiAuthorizationException ex = LiAuthorizationException.generalEx(LiAuthorizationException.GeneralErrors.SERVER_ERROR.code, e.getMessage());
                    callback.onLoginComplete(ex, false);
                }
            });
        } catch (RuntimeException e) {
            Log.e(LiAuthConstants.LOG_TAG, e.getMessage());
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
                        Gson gson = new Gson();
                        JsonObject responseJsonObject = response.getJsonObject();
                        if (responseJsonObject.has("data")) {
                            JsonObject dataObj = responseJsonObject.get("data").getAsJsonObject();
                            if (dataObj.has("items")) {
                                JsonArray items = dataObj.get("items").getAsJsonArray();
                                if (!items.isJsonNull() && items.size() > 0) {
                                    LiAppSdkSettings liAppSdkSettings = gson.fromJson(items.get(0), LiAppSdkSettings.class);
                                    if (liAppSdkSettings != null) {
                                        sdkManager.putInSecuredPreferences(context, LI_DEFAULT_SDK_SETTINGS, liAppSdkSettings.getAdditionalInformation());
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(LiCoreSDKConstants.LI_LOG_TAG, "Error getting SDK settings");
                    }

                    LiDeviceTokenProvider provider = sdkManager.getLiDeviceTokenProvider();
                    if (provider != null && !TextUtils.isEmpty(provider.getDeviceId())) {
                        new LiNotificationProviderImpl().onIdRefresh(provider.getDeviceId(), context);
                    }

                    callBack.onLoginComplete(null, true);
                }

                @Override
                public void onError(Exception exception) {
                    callBack.onLoginComplete(null, true);
                    Log.e(LiAuthConstants.LOG_TAG, exception.getMessage());
                }
            });
        } catch (LiRestResponseException e) {
            callBack.onLoginComplete(null, true);
            Log.e(LiAuthConstants.LOG_TAG, e.getMessage());
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
                public void onSuccess(LiBaseRestRequest request, LiGetClientResponse liClientResponse) throws LiRestResponseException {
                    if (liClientResponse != null && liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL
                            && liClientResponse.getResponse() != null && !liClientResponse.getResponse().isEmpty()) {
                        LiUser user = (LiUser) liClientResponse.getResponse().get(0).getModel();
                        sdkManager.setLoggedInUser(context, user);
                    }
                    getSDKSettings(callBack);
                }

                @Override
                public void onError(Exception e) {
                    getSDKSettings(callBack);
                    Log.e(LOG_TAG, "Unable to fetch user details: " + e);
                    LiAuthorizationException ex = LiAuthorizationException.generalEx(LiAuthorizationException.GeneralErrors.SERVER_ERROR.code, e.getMessage());
                    callBack.onLoginComplete(ex, false);
                }
            });
        } catch (LiRestResponseException e) {
            Log.e(LOG_TAG, "ERROR: " + e);
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
        LiRefreshTokenRequest request = new LiRefreshTokenRequest();
        request.setClientId(this.sdkManager.getCredentials().getClientKey());
        request.setClientSecret(this.sdkManager.getCredentials().getClientSecret());
        request.setGrantType("refresh_token");
        request.setRefreshToken(this.sdkManager.getRefreshToken());
        String uri = "https://" + this.sdkManager.getProxyHost() + "/auth/v1/refreshToken";
        request.setUri(Uri.parse(uri));

        try {
            authRestClient.refreshTokenAsync(context, request, new LiAuthAsyncRequestCallback<LiBaseResponse>() {

                @Override
                public void onSuccess(LiBaseResponse response) throws LiRestResponseException {
                    if (response == null || response.getHttpCode() != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        callback.onTokenRequestCompleted(null, new Exception(REFRESH_TOKEN_FETCH_ERROR));
                        return;
                    }
                    try {
                        Gson gson = new Gson();
                        JsonObject data = gson.fromJson(response.getData(), JsonObject.class);
                        if (data != null && data.has("response")) {
                            JsonObject responseJsonObj = data.get("response").getAsJsonObject();
                            if (responseJsonObj.has("data")) {
                                LiTokenResponse tokenResponse = gson.fromJson(responseJsonObj.get("data"), LiTokenResponse.class);
                                tokenResponse.setExpiresAt(LiCoreSDKUtils.getTime(tokenResponse.getExpiresIn()));
                                JsonObject obj = responseJsonObj.get("data").getAsJsonObject();
                                obj.addProperty("expiresAt", tokenResponse.getExpiresAt());
                                tokenResponse.setJsonString(String.valueOf(obj));
                                callback.onTokenRequestCompleted(tokenResponse, null);
                            } else {
                                Log.e(LI_LOG_TAG, REFRESH_TOKEN_FETCH_ERROR);
                                callback.onTokenRequestCompleted(null, new RuntimeException(REFRESH_TOKEN_FETCH_ERROR));
                            }
                        } else {
                            Log.e(LI_LOG_TAG, REFRESH_TOKEN_FETCH_ERROR);
                            callback.onTokenRequestCompleted(null, new RuntimeException(REFRESH_TOKEN_FETCH_ERROR));
                        }
                    } catch (RuntimeException e) {
                        callback.onTokenRequestCompleted(null, e);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    callback.onTokenRequestCompleted(null, exception);
                }

            });
        } catch (RuntimeException e) {
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

        final LiAuthRestClient authRestClient = getLiAuthRestClient();
        LiRefreshTokenRequest request = new LiRefreshTokenRequest();
        request.setClientId(this.sdkManager.getCredentials().getClientKey());
        request.setClientSecret(this.sdkManager.getCredentials().getClientSecret());
        request.setGrantType("refresh_token");
        request.setRefreshToken(this.sdkManager.getRefreshToken());
        String uri = "https://" + this.sdkManager.getProxyHost() + "/auth/v1/refreshToken";
        request.setUri(Uri.parse(uri));

        LiTokenResponse response = null;
        LiBaseResponse resp = authRestClient.refreshTokenSync(context, request);
        Gson gson = new Gson();
        JsonObject dataObject = resp.getData();
        try {
            if (dataObject.has("response")) {
                JsonObject responseObj = dataObject.get("response").getAsJsonObject();
                if (responseObj.has("data")) {
                    JsonElement dataJsonElement = responseObj.get("data");
                    response = gson.fromJson(dataJsonElement, LiTokenResponse.class);
                    response.setExpiresAt(LiCoreSDKUtils.getTime(response.getExpiresIn()));
                    JsonObject obj = dataJsonElement.getAsJsonObject();
                    obj.addProperty("expiresAt", response.getExpiresAt());
                    response.setJsonString(String.valueOf(obj));
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw LiRestResponseException.runtimeError("Error refreshing access token");
        }
        if (response == null) {
            throw LiRestResponseException.runtimeError("Error refreshing access token");
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
