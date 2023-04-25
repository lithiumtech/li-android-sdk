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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lithium.community.android.BuildConfig;
import com.lithium.community.android.R;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.auth.LiAppCredentials;
import com.lithium.community.android.auth.LiAuthService;
import com.lithium.community.android.auth.LiAuthServiceImpl;
import com.lithium.community.android.auth.LiDeviceTokenProvider;
import com.lithium.community.android.auth.LiTokenResponse;
import com.lithium.community.android.callback.Callback;
import com.lithium.community.android.exception.LiInitializationException;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiAppSdkSettings;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.notification.FirebaseTokenProvider;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.rest.LiPostClientResponse;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.LiUUIDUtils;
import com.lithium.community.android.utils.MessageConstants;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_ERROR_LOG_TAG;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_RECEIVER_DEVICE_ID;

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
    private FirebaseTokenProvider firebaseTokenProvider;

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
     * To check if the the SDK is initialized correctly. Should be used before invoking
     * APIs of the SDK or components which depend on it.
     *
     * @return {@code true} iff SDK is fully initialized, otherwise {@code false}.
     */
    public static boolean isInitialized() {
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

    @Nullable
    public FirebaseTokenProvider getFirebaseTokenProvider() {
        return firebaseTokenProvider;
    }

    public void setFirebaseTokenProvider(@Nullable FirebaseTokenProvider provider) {
        this.firebaseTokenProvider = provider;
    }

    /**
     * Start the login flow.
     *
     * @param context  An Android context.
     * @param ssoToken Single SignOn token if the community uses its own identity provider.
     */
    public void login(@NonNull Context context, @Nullable String ssoToken) {
        if (!isUserLoggedIn()) {
            new LiAuthServiceImpl(context, this).startLoginFlow(ssoToken);
        } else {
            Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "Sign out to login again.");
        }
    }

    /**
     * Start anonymous login flow.
     *
     * @param context An Android context object.
     */
    public void login(@NonNull Context context) {
        login(context, null);
    }

    /**
     * Logs out the user from the community for this device, clears all references of the current user for this device, at both backend and local storage
     *
     * @param context  instance of {@link Context} to access local cache and storage
     * @param callback instance of {@link com.lithium.community.android.callback.Callback to inform about the state of the logout operation,
     *                 success() will be called if everything goes well, a necessary exception will be returned in failure(..) if something isn't done.
     */
    public void logout(@NonNull Context context, @NonNull Callback<Void, Throwable, Throwable> callback) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        LiCoreSDKUtils.checkNotNull(callback, MessageConstants.wasNull("callback"));
        if (!isUserLoggedIn()) {
            callback.abort(new IllegalAccessException(context.getString(R.string.li_error_logout_user_not_looged_in)));
            return;
        }
        String deviceId = getFromSecuredPreferences(context, LI_RECEIVER_DEVICE_ID);
        LiClientRequestParams.LiLogoutRequestParams params = new LiClientRequestParams.LiLogoutRequestParams(context, deviceId);
        try {
            LiClient client = LiClientManager.getLogoutClient(params);
            client.processAsync(new LogoutRequestCallback(context, callback));
        } catch (LiRestResponseException e) {
            e.printStackTrace();
            callback.abort(e);
        }
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
     * @deprecated Use {@link #getFirebaseTokenProvider()} instead.
     */
    @Deprecated
    @Nullable
    public LiDeviceTokenProvider getLiDeviceTokenProvider() {
        return LiDeviceTokenProvider.Wrapper.getWrappedProvider(firebaseTokenProvider);

    }

    /**
     * Set a new device token provider to be used by the SDK.
     *
     * @param provider A device token provider.
     * @deprecated Use {@link #setFirebaseTokenProvider(FirebaseTokenProvider)} instead.
     */
    @Deprecated
    public void setLiDeviceTokenProvider(@Nullable LiDeviceTokenProvider provider) {
        this.firebaseTokenProvider = new LiDeviceTokenProvider.Wrapper(provider);
    }

    /**
     * Start SSO login.
     *
     * @param context  An Android context.
     * @param ssoToken Single SignOn token if the community uses its own identity provider.
     * @param provider Provider to get the device ID for push notifications. A Firebase or GCM token.
     * @deprecated Use {@link #setFirebaseTokenProvider(FirebaseTokenProvider)} and  {@link #login(Context, String)} instead.
     */
    @Deprecated
    public void initLoginFlow(Context context, String ssoToken, LiDeviceTokenProvider provider) {
        setFirebaseTokenProvider(new LiDeviceTokenProvider.Wrapper(provider));
        login(context, ssoToken);
    }

    /**
     * Start SSO login.
     *
     * @param context  An Android context.
     * @param ssoToken Single SignOn token if the community uses its own identity provider.
     * @deprecated Use {@link #login(Context, String)} )} instead.
     */
    @Deprecated
    public void initLoginFlow(Context context, String ssoToken) {
        login(context, ssoToken);
    }

    /**
     * Start community login.
     *
     * @param context  An Android context.
     * @param provider Provider to get the device ID for push notifications. A Firebase or GCM token.
     * @deprecated Use {@link #setFirebaseTokenProvider(FirebaseTokenProvider)} and {@link #login(Context)} instead.
     */
    @Deprecated
    public void initLoginFlow(Context context, LiDeviceTokenProvider provider) {
        setFirebaseTokenProvider(new LiDeviceTokenProvider.Wrapper(provider));
        login(context, null);
    }

    /**
     * Start anonymous login.
     *
     * @param context An Android context object.
     * @deprecated Use {@link #login(Context)} instead.
     */
    @Deprecated
    public void initLoginFlow(Context context) {
        login(context, null);
    }

    /**
     * A callback on Logout rest API operation
     */
    public class LogoutRequestCallback implements LiAsyncRequestCallback<LiPostClientResponse> {

        private Callback<Void, Throwable, Throwable> callback = null;
        private Context context;

        /**
         * Initialize a callback for Logout rest operation
         *
         * @param context  - And android context {@link Context}
         * @param callback - the generic SDK callback {@link Callback}, which is the feeder fort SDK clients.
         */
        public LogoutRequestCallback(Context context, Callback<Void, Throwable, Throwable> callback) {
            this.callback = callback;
            this.context = context;
        }

        /**
         * API call succeeded, but doesn't necessarily mean operation succeeded. It has more internal meaning.
         *
         * @param request  {@link LiBaseRestRequest} actual request made with this callback
         * @param response Generic success response.
         * @throws LiRestResponseException response or request exception
         */
        @Override
        public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
            done();
        }

        /**
         * API call itself failed
         *
         * @param exception {@link Exception}
         */
        @Override
        public void onError(Exception exception) {
            done();
        }

        private void done() {
            if (getFirebaseTokenProvider() != null) {
                try {
                    getFirebaseTokenProvider().deleteDeviceToken();
                } catch (IOException e) {
                    Log.e(LI_ERROR_LOG_TAG, "IOException while deleting device token");
                    e.printStackTrace();
                } catch (UnsupportedOperationException e) {
                    Log.e(LI_ERROR_LOG_TAG, "UnsupportedOperationException while deleting device token");
                    e.printStackTrace();
                }
            }
            logout(context);
            if (callback != null) {
                callback.success(null);
            }
        }
    }
}

