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
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonObject;

import lithium.community.android.sdk.LiSDKManager;
import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.client.manager.LiClientManager;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseResponse;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiPostClientResponse;

import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEVICE_ID;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_RECEIVER_DEVICE_ID;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_SHARED_PREFERENCES_NAME;

/**
 * This class is used to update device id corresponding to 'id' in the community side when onIdRefresh is called.
 * Created by shoureya.kant on 12/28/16.
 */

public class LiNotificationProviderImpl implements LiNotificationProvider {

    @Override
    public void onIdRefresh(final String deviceId, LiPushNotificationProvider liPushNotificationProvider) throws LiRestResponseException {

        final Context context = LiSDKManager.getInstance().getContext();
        String savedId = getSharedPreferences(context).getString(LI_DEVICE_ID, null);

        /**
         * If there is no 'id' corresponding to the device id, a fresh call is made to get the 'id' and save it in
         * shared preferences.
         */
        if(savedId == null || savedId.isEmpty()){
            LiClient deviceIdFetchClient = LiClientManager.getInstance().getDeviceIdFetchClient(deviceId, liPushNotificationProvider.name());
            deviceIdFetchClient.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
                    LiBaseResponse liBaseResponse = response.getResponse();
                    JsonObject data = liBaseResponse.getData();
                    if (data != null && data.has("data")) {
                        JsonObject dataObj = data.get("data").getAsJsonObject();
                        if (dataObj.has("id")) {
                            String id = dataObj.get("id").getAsString();
                            getSharedPreferences(context).edit()
                                    .putString(LI_DEVICE_ID, id).putString(LI_RECEIVER_DEVICE_ID, deviceId).commit();
                        }
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

            if(deviceId.equals(getSharedPreferences(context).getString(LI_RECEIVER_DEVICE_ID, null))){
                return;
            }
            LiClient deviceIdUpdateClient = LiClientManager.getInstance().getDeviceIdUpdateClient(deviceId, savedId);
            deviceIdUpdateClient.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
                    Log.i(LI_LOG_TAG, "Successfully updated device Id");
                }
                @Override
                public void onError(Exception exception) {
                    Log.e(LI_LOG_TAG, "Unable to update device Id");
                }
            });

        }

    }

    /**
     * provides the shared prefernece to save the 'id'.
     * @param context {@link Context}
     * @return SharedPreferences {@link SharedPreferences}
     */
    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(LI_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
