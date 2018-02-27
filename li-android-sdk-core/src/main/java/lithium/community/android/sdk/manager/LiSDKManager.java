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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

import lithium.community.android.sdk.BuildConfig;
import lithium.community.android.sdk.auth.LiAppCredentials;
import lithium.community.android.sdk.auth.LiAuthConstants;
import lithium.community.android.sdk.auth.LiAuthService;
import lithium.community.android.sdk.auth.LiAuthServiceImpl;
import lithium.community.android.sdk.auth.LiDeviceTokenProvider;
import lithium.community.android.sdk.auth.LiTokenResponse;
import lithium.community.android.sdk.exception.LiInitializationException;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.model.response.LiAppSdkSettings;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.queryutil.LiDefaultQueryHelper;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiGetClientResponse;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiUUIDUtils;
import lithium.community.android.sdk.utils.MessageConstants;

import static lithium.community.android.sdk.auth.LiAuthConstants.LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_VISITOR_ID;

/**
 * Interface to Lithium Community Android SDK. Provides the entry point into
 * the Community REST API v2 using OAuth2.
 */
public final class LiSDKManager extends LiAuthManager {

    private static final String COMPONENT_NAME = LiSDKManager.class.getName();

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
    private LiSDKManager(@NonNull Context context, @NonNull LiAppCredentials credentials) {
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
            if (LiDefaultQueryHelper.initHelper(context) == null) {
                throw new LiInitializationException(COMPONENT_NAME);
            }
            if (LiSecuredPrefManager.init(context) == null) {
                throw new LiInitializationException(COMPONENT_NAME);
            }
            instance = new LiSDKManager(context, credentials);
        }
        updatePreferences(context);
    }

    /**
     * Initializes LiSDKManager.
     *
     * @param context     The android context.
     * @param credentials The credentials for authentication.
     * @return An instance of LiSDKManager
     * @throws URISyntaxException Does not throw a URISyntaxException but still here for legacy support.
     * @deprecated Use {@link #initialize(Context, LiAppCredentials)} instead.
     */
    @Nullable
    public static synchronized LiSDKManager init(@NonNull final Context context, @NonNull final LiAppCredentials credentials) throws URISyntaxException {

        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        LiCoreSDKUtils.checkNotNull(credentials, MessageConstants.wasNull("credentials"));

        if (isInitialized.compareAndSet(false, true)) {
            if (LiDefaultQueryHelper.initHelper(context) == null) {
                return null;
            }
            if (LiSecuredPrefManager.init(context) == null) {
                return null;
            }
            instance = new LiSDKManager(context, credentials);
        }
        updatePreferences(context);
        return instance;
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
    public static boolean isEnvironmentInitialized() {
        return isInitialized.get() && instance != null;
    }

    /**
     * Returns an instance of the {@link LiSDKManager}.
     *
     * @return if the SDK initialized then the current instance of {@link LiSDKManager} is return, else {@code null} is returned.
     */
    @Nullable
    public static LiSDKManager getInstance() {
        return instance;
    }

    private static void updatePreferences(@NonNull Context context) {
        if (getInstance() != null && getInstance().getFromSecuredPreferences(context, LI_VISITOR_ID) == null) {
            getInstance().putInSecuredPreferences(context, LI_VISITOR_ID, LiCoreSDKUtils.getRandomHexString()); // generate a visitor ID and save it
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
            try {
                String clientId = LiUUIDUtils.toUUID(getLiAppCredentials().getClientKey().getBytes()).toString();
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiSdkSettingsClientRequestParams(context, clientId);
                LiClientManager.getSdkSettingsClient(liClientRequestParams).processAsync(
                        new LiAsyncRequestCallback<LiGetClientResponse>() {

                            @Override
                            public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response)
                                    throws LiRestResponseException {
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
                                                    putInSecuredPreferences(context, LI_DEFAULT_SDK_SETTINGS, liAppSdkSettings.getAdditionalInformation());
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Log.e(LiCoreSDKConstants.LI_LOG_TAG, "Error getting SDK settings");
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                Log.e(LiCoreSDKConstants.LI_LOG_TAG, "Error getting SDK settings: " + exception.getMessage());
                            }
                        });
            } catch (LiRestResponseException e) {
                Log.e(LiAuthConstants.LOG_TAG, e.getMessage());
            }
            //get logged in user details
            try {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiUserDetailsClientRequestParams(context, "self");
                LiClientManager.getUserDetailsClient(liClientRequestParams)
                        .processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                            @Override
                            public void onSuccess(LiBaseRestRequest request, LiGetClientResponse liClientResponse) throws LiRestResponseException {
                                if (liClientResponse != null && liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL
                                        && liClientResponse.getResponse() != null && !liClientResponse.getResponse().isEmpty()) {
                                    LiUser user = (LiUser) liClientResponse.getResponse().get(0).getModel();
                                    instance.setLoggedInUser(context, user);
                                } else {
                                    Log.e(LOG_TAG, "No user found while fetching UserDetails");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(LOG_TAG, "Unable to fetch user details: " + e);
                            }
                        });
            } catch (LiRestResponseException e) {
                Log.e(LOG_TAG, "ERROR: " + e);
            }

        }
    }

    /**
     * Gets version name of the SDK.
     *
     * @return the version name of the SDK.
     */
    @NonNull
    public String getCoreSDKVersion() {
        return BuildConfig.li_sdk_core_version;
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
     * @param context             An Android context.
     * @param mFreshTokenCallBack {@link LiAuthService.FreshTokenCallBack}
     * @throws LiRestResponseException An exception is thrown when an invalid response is received by the SDK.
     */
    public void fetchFreshAccessToken(final Context context, final LiAuthService.FreshTokenCallBack mFreshTokenCallBack) throws LiRestResponseException {
        new LiAuthServiceImpl(context, this).performRefreshTokenRequest(new LiAuthService.LiTokenResponseCallback() {
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

