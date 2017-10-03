/*
 * NotificationProvider.java
 * Created on Dec 28, 2016
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

package lithium.community.android.sdk.notification;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseResponse;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiPostClientResponse;
import lithium.community.android.sdk.rest.LiPutClientResponse;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEVICE_ID;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_RECEIVER_DEVICE_ID;

/**
 * This class is used to update device id corresponding to 'id' in the community side when onIdRefresh is called.
 * Created by shoureya.kant on 12/28/16.
 */

public class LiNotificationProviderImpl implements LiNotificationProvider {
    private static final String PUSH_NOTIFICATION_ADAPTER = "push_notification_adapter";

    @Override
    public void onIdRefresh(final String deviceId, final Context context) throws LiRestResponseException {
        String savedId = LiSDKManager.getInstance().getFromSecuredPreferences(context, LI_DEVICE_ID);
        /**
         * If there is no 'id' corresponding to the device id, a fresh call is made to get the 'id' and save it in
         * shared preferences.
         */
        if (savedId == null || savedId.isEmpty()) {
            String settingFromServer;
            settingFromServer = LiSDKManager.getInstance().getFromSecuredPreferences(context, LI_DEFAULT_SDK_SETTINGS);
            String pushNotificationAdapter = "FIREBASE";
            JsonObject settingFromServerJson;
            if (settingFromServer != null && !settingFromServer.isEmpty()) {
                JsonElement jsonElement = new JsonParser().parse(settingFromServer);
                if (!jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                    settingFromServerJson = jsonElement.getAsJsonObject();
                    if (settingFromServerJson.has(PUSH_NOTIFICATION_ADAPTER) && settingFromServerJson.get(PUSH_NOTIFICATION_ADAPTER) != null) {
                        pushNotificationAdapter = settingFromServerJson.get(PUSH_NOTIFICATION_ADAPTER).getAsString();
                    }
                }
            }

            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiDeviceIdFetchClientRequestParams(context, deviceId, pushNotificationAdapter);
            LiClient deviceIdFetchClient = LiClientManager.getDeviceIdFetchClient(liClientRequestParams);
            deviceIdFetchClient.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
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
                    }
                    else {
                        Log.e(LI_LOG_TAG, "Unable to fetch device id");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(LI_LOG_TAG, "Unable to fetch device id");
                }
            });
        }
        /**
         * If 'id' is present then device id corresponding to it on the community end is update with the passed device id.
         */
        else {
            String deviceIdFromPref = LiSDKManager.getInstance().getFromSecuredPreferences(context,LI_RECEIVER_DEVICE_ID);
            if (deviceId.equals(deviceIdFromPref)) {
                return;
            }
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiDeviceIdUpdateClientRequestParams(context, deviceId, savedId);
            LiClient deviceIdUpdateClient = LiClientManager.getDeviceIdUpdateClient(liClientRequestParams);
            deviceIdUpdateClient.processAsync(new LiAsyncRequestCallback<LiPutClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPutClientResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Log.i(LI_LOG_TAG, "Successfully updated device Id");
                    }
                    else {
                        Log.e(LI_LOG_TAG, "Unable to update device Id");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(LI_LOG_TAG, "Unable to update device Id");
                }
            });
        }

    }
}
