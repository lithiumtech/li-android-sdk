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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lithium.community.android.auth.LiAppCredentials;
import com.lithium.community.android.auth.LiAuthService;
import com.lithium.community.android.auth.LiAuthServiceImpl;
import com.lithium.community.android.auth.LiDeviceTokenProvider;
import com.lithium.community.android.auth.LiTokenResponse;
import com.lithium.community.android.exception.LiInitializationException;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiAppSdkSettings;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.LiUUIDUtils;
import com.lithium.community.android.utils.MessageConstants;

import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.lithium.community.android.BuildConfig;

/**
 * <p>
 * Interface to Lithium Community Android SDK. Provides the entry point into
 * the Community REST API v2 using OAuth2.
 * </p>
 *
 * @author adityasharat
 */
public final class LiSDKManager extends LiAuthManager {

    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static LiSDKManager instance;

    @Nullable
    private LiDeviceTokenProvider deviceTokenProvider;

    /**
     * Default private constructor.
     *
     * @param context     The Android context.
     * @param credentials The credentials to be used to authenticate the SDK.
     */
    private LiSDKManager(@NonNull Context context, @NonNull LiAppCredentials credentials) throws LiInitializationException {
        super(context, credentials);
    }

    /**
     * Initializes the SDK. If the initialization is successful {@link #getInstance()} will return an initialized SDK Manager. This
     * function may or may not create a new instance or reinitialize the SDK Manager with subsequent calls.
     *
     * @param context     The android {@link Context} to be used by the SDK.
     * @param credentials The {@link LiAppCredentials} for authenticating the SDK.
     */
    public static synchronized void initialize(@NonNull final Context context, @NonNull final LiAppCredentials credentials) throws LiInitializationException {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        LiCoreSDKUtils.checkNotNull(credentials, MessageConstants.wasNull("credentials"));
        if (isInitialized.compareAndSet(false, true)) {
            instance = new LiSDKManager(context, credentials);
        }
        updatePreferences(context);
    }

    /**
     * Initializes Lia SDK Manager.
     *
     * @param context     The android context.
     * @param credentials The credentials for authentication.
     * @return An instance of Li SDK Manager
     * @deprecated Use {@link #initialize(Context, LiAppCredentials)} instead.
     */
    @Deprecated
    @Nullable
    public static synchronized LiSDKManager init(@NonNull final Context context, @NonNull final LiAppCredentials credentials) {
        try {
            initialize(context, credentials);
        } catch (LiInitializationException e) {
            e.printStackTrace();
        }

        return getInstance();
    }

    /**
     * To check if the the SDK is initialized correctly. Should be used before invoking
     * APIs of the SDK or components which depend on it.
     *
     * @return {@code true} iff SDK is fully initialized, otherwise {@code false}.
     */
    public static boolean isInitialized() {
        return isInitialized.get() && instance != null;
    }

    /**
     * Checks whether the SDK is initialized.
     *
     * @return true or false depending on whether the SDK is initialized
     * @deprecated Use {@link #isInitialized()} instead.
     */
    @Deprecated
    public static boolean isEnvironmentInitialized() {
        return isInitialized.get() && instance != null;
    }

    /**
     * Returns an instance of the {@link LiSDKManager}.
     *
     * @return if the SDK initialized then the current instance of {@link LiSDKManager} is return, else {@code null} is returned.
     */
    public static LiSDKManager getInstance() {
        if (instance == null) {
            String message = "SDK was not initialized. Call `LiSDKManager.initialize(Context, LiAppCredentials)` before invoking getInstance().";
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, message);
        }
        return instance;
    }

    private static void updatePreferences(@NonNull Context context) {
        if (getInstance() != null && getInstance().getFromSecuredPreferences(context, LiCoreSDKConstants.LI_VISITOR_ID) == null) {

            // generate a visitor ID and save it
            getInstance().putInSecuredPreferences(context, LiCoreSDKConstants.LI_VISITOR_ID, LiCoreSDKUtils.getRandomHexString());
        }
    }

    /**
     * This method is used to pull configurations from the community and
     * update the local configurations.
     *
     * @param context The android context.
     */
    public void syncWithCommunity(final Context context) {
        if (instance.isUserLoggedIn()) {
            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "SDK is retrieving settings from the community");
            try {
                String clientId = LiUUIDUtils.toUUID(getCredentials().getClientKey().getBytes()).toString();
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiSdkSettingsClientRequestParams(context, clientId);
                LiClientManager.getSdkSettingsClient(liClientRequestParams).processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {

                    @Override
                    public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) {
                        if (response != null && response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                            Gson gson = LiClientManager.getRestClient().getGson();
                            JsonObject responseJsonObject = response.getJsonObject();
                            if (responseJsonObject.has("data")) {
                                JsonObject dataObj = responseJsonObject.get("data").getAsJsonObject();
                                if (dataObj.has("items")) {
                                    JsonArray items = dataObj.get("items").getAsJsonArray();
                                    if (!items.isJsonNull() && items.size() > 0) {
                                        LiAppSdkSettings settings = gson.fromJson(items.get(0), LiAppSdkSettings.class);
                                        if (settings != null) {
                                            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "SDK retrieved settings from the community successfully");
                                            putInSecuredPreferences(context, LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS, settings.getAdditionalInformation());
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "API retrieved invalid settings from the community");
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "API failed to retrieve settings from the community");
                        exception.printStackTrace();
                    }
                });
            } catch (LiRestResponseException e) {
                Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Sync with community request aborted");
                e.printStackTrace();
            }

            //get logged in user details
            try {
                Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "SDK is retrieving logged in user details");
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiUserDetailsClientRequestParams(context, "self");
                LiClientManager.getUserDetailsClient(liClientRequestParams).processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                    @Override
                    public void onSuccess(LiBaseRestRequest request, LiGetClientResponse liClientResponse) {
                        if (liClientResponse != null && liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL
                                && liClientResponse.getResponse() != null && !liClientResponse.getResponse().isEmpty()) {
                            LiUser user = (LiUser) liClientResponse.getResponse().get(0).getModel();
                            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "SDK is setting logged in user details");
                            instance.setLoggedInUser(context, user);
                        } else {
                            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "API returned invalid user details");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "API failed to return user details");
                        e.printStackTrace();
                    }
                });
            } catch (LiRestResponseException e) {
                Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "User details request aborted");
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets version name of the SDK.
     *
     * @return the version name of the SDK.
     */
    @NonNull
    public String getVersion() {
        return BuildConfig.LI_SDK_CORE_VERSION;
    }

    /**
     * Gets version name of the SDK.
     *
     * @return the version name of the SDK.
     * @deprecated Use {@link #getVersion()} instead.
     */
    @Deprecated
    @NonNull
    public String getCoreSDKVersion() {
        return getVersion();
    }

    /**
     * Get the device token provider currently being used by the SDK.
     *
     * @return the device token provider.
     */
    @Nullable
    public LiDeviceTokenProvider getLiDeviceTokenProvider() {
        return deviceTokenProvider;
    }

    /**
     * Set a new device token provider to be used by the SDK.
     *
     * @param deviceTokenProvider A device token provider.
     */
    public void setLiDeviceTokenProvider(@Nullable LiDeviceTokenProvider deviceTokenProvider) {
        this.deviceTokenProvider = deviceTokenProvider;
    }

    /**
     * Start SSO login.
     *
     * @param context             An Android context.
     * @param ssoToken            Single SignOn token if the community uses its own identity provider.
     * @param deviceTokenProvider Provider to get the device ID for push notifications. A Firebase or GCM token.
     */
    public void initLoginFlow(Context context, String ssoToken, LiDeviceTokenProvider deviceTokenProvider) {
        this.deviceTokenProvider = deviceTokenProvider;
        if (!isUserLoggedIn()) {
            new LiAuthServiceImpl(context, this).startLoginFlow(ssoToken);
        }
    }

    /**
     * Start SSO login.
     *
     * @param context  An Android context.
     * @param ssoToken Single SignOn token if the community uses its own identity provider.
     */
    public void initLoginFlow(Context context, String ssoToken) throws URISyntaxException {
        initLoginFlow(context, ssoToken, null);
    }

    /**
     * Start community login.
     *
     * @param context             An Android context.
     * @param deviceTokenProvider Provider to get the device ID for push notifications. A Firebase or GCM token.
     */
    public void initLoginFlow(Context context, LiDeviceTokenProvider deviceTokenProvider) {
        this.deviceTokenProvider = deviceTokenProvider;
        initLoginFlow(context, null, deviceTokenProvider);
    }

    /**
     * Start anonymous login.
     *
     * @param context An Android context object.
     */
    public void initLoginFlow(Context context) {
        initLoginFlow(context, null, null);
    }

    /**
     * Fetches Fresh Access Token and Persists it.
     *
     * @param context  An Android context.
     * @param callBack {@link LiAuthService.FreshTokenCallBack}
     * @throws LiRestResponseException An exception is thrown when an invalid response is received by the SDK.
     */
    public void fetchFreshAccessToken(final Context context, final LiAuthService.FreshTokenCallBack callBack) throws LiRestResponseException {
        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiAuthManager#fetchFreshAccessToken() - refreshing access token");
        new LiAuthServiceImpl(context, this).performRefreshTokenRequest(new LiAuthService.LiTokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable LiTokenResponse response, @Nullable Exception ex) {
                if (ex != null) {
                    ex.printStackTrace();
                    callBack.onFreshTokenFetched(false);
                    return;
                }
                if (response != null) {
                    Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiSDKManager#fetchFreshAccessToken() - refresh access token successful");
                    persistAuthState(context, response);
                    callBack.onFreshTokenFetched(true);
                } else {
                    Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "LiSDKManager#restoreAuthState() - API returned invalid refreshed access token");
                    callBack.onFreshTokenFetched(false);
                }
            }
        });
    }
}

