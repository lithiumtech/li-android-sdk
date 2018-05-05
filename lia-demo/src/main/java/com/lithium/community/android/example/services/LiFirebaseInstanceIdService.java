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

package com.lithium.community.android.example.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.notification.LiNotificationProviderImpl;

/**
 * Created by sumit.pannalall on 12/29/16.
 */
public class LiFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the community side, send the
        // Instance ID token to your app server.
        registerWithCommunity();
    }
    // [END refresh_token]

    /**
     * Persist token to Notification service.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     */
    private void registerWithCommunity() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        try {
            new LiNotificationProviderImpl().onIdRefresh(token, this);
        } catch (LiRestResponseException e) {
            Log.e("LiSDK", "Could not post token (device id) to Notification service.");
            e.printStackTrace();
        }
    }
}
