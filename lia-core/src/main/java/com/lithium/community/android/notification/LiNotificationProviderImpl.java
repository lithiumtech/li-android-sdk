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

package com.lithium.community.android.notification;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiPostClientResponse;
import com.lithium.community.android.rest.LiPutClientResponse;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_DEBUG_LOG_TAG;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_DEVICE_ID;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_ERROR_LOG_TAG;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_RECEIVER_DEVICE_ID;

/**
 * This class is used to update device id corresponding to 'id' in the community side when onIdRefresh is called.
 *
 * @author shoureya.kant
 */
public class LiNotificationProviderImpl implements LiNotificationProvider {
    private static final String PUSH_NOTIFICATION_ADAPTER = "push_notification_adapter";

    @Override
    public void onIdRefresh(final String deviceId, final Context context) throws LiRestResponseException {

        // if the SDK is not initialized and user is not logged in
        // then device id cannot be registered with the community
        if (!LiSDKManager.isInitialized() || !LiSDKManager.getInstance().isUserLoggedIn()) {
            return;
        }

        String savedId = LiSDKManager.getInstance().getFromSecuredPreferences(context, LI_DEVICE_ID);

        /*
         * If there is no 'id' corresponding to the device id, a fresh call is made to get the 'id' and save it in
         * shared preferences.
         */
        if (savedId == null || savedId.isEmpty()) {
            String settingFromServer;
            settingFromServer = LiSDKManager.getInstance().getFromSecuredPreferences(context, LI_DEFAULT_SDK_SETTINGS);
            String provider = "FIREBASE";
            JsonObject settingFromServerJson;
            if (settingFromServer != null && !settingFromServer.isEmpty()) {
                JsonElement jsonElement = new JsonParser().parse(settingFromServer);
                if (!jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                    settingFromServerJson = jsonElement.getAsJsonObject();
                    if (settingFromServerJson.has(PUSH_NOTIFICATION_ADAPTER) && settingFromServerJson.get(PUSH_NOTIFICATION_ADAPTER) != null) {
                        provider = settingFromServerJson.get(PUSH_NOTIFICATION_ADAPTER).getAsString();
                    }
                }
            }

            LiClientRequestParams requestParams = new LiClientRequestParams.LiDeviceIdFetchClientRequestParams(context, deviceId, provider);
            LiClient client = LiClientManager.getDeviceIdFetchClient(requestParams);
            client.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) {
                    if (response != null && response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        LiBaseResponse liBaseResponse = response.getResponse();
                        JsonObject data = liBaseResponse.getData();
                        if (data != null && data.has("data")) {
                            JsonObject dataObj = data.get("data").getAsJsonObject();
                            if (dataObj.has("id")) {
                                String id = dataObj.get("id").getAsString();
                                LiSDKManager.getInstance().putInSecuredPreferences(context, LI_DEVICE_ID, id);
                                LiSDKManager.getInstance().putInSecuredPreferences(context, LI_RECEIVER_DEVICE_ID, deviceId);
                            }
                        }
                    } else {
                        Log.e(LI_ERROR_LOG_TAG, "Unable to fetch device id");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(LI_ERROR_LOG_TAG, "Device Id API request failed.");
                    exception.printStackTrace();
                }
            });
        } else {
            /*
             * If 'id' is present then device id corresponding to it on the community end is update with the passed device id.
             */
            String deviceIdFromPref = LiSDKManager.getInstance().getFromSecuredPreferences(context, LI_RECEIVER_DEVICE_ID);
            if (deviceId.equals(deviceIdFromPref)) {
                return;
            }
            LiClientRequestParams requestParams = new LiClientRequestParams.LiDeviceIdUpdateClientRequestParams(context, deviceId, savedId);
            LiClient client = LiClientManager.getDeviceIdUpdateClient(requestParams);
            client.processAsync(new LiAsyncRequestCallback<LiPutClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPutClientResponse response) {
                    if (response != null && response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Log.d(LI_DEBUG_LOG_TAG, "Successfully updated device Id");
                    } else {
                        Log.e(LI_ERROR_LOG_TAG, "Unable to update device Id");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(LI_ERROR_LOG_TAG, "Unable to update device Id");
                    exception.printStackTrace();
                }
            });
        }
    }
}
