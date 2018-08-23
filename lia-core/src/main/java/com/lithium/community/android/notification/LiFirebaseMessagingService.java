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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lithium.community.android.R;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.io.IOException;
import java.util.Map;

/**
 * This class is for receiving a notification from FireBase and showing it on the notification bar of the device.
 *
 * @author adityasharat
 */
public class LiFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        registerToCommunityPushNotifications();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> map = remoteMessage.getData();
        String notificationSrc = map.get("source");
        if (notificationSrc != null && notificationSrc.equals("community")) {
            LiNotificationPayload notificationPayload = new LiNotificationPayload();
            notificationPayload.setFromId(map.get("fromId"));
            notificationPayload.setFromName(map.get("fromName"));
            notificationPayload.setType(map.get("type"));
            notificationPayload.setMessage(map.get("message"));
            showCommunityNotification(this, notificationPayload);
        }
    }

    /**
     * This method displays community specific notifications.
     *
     * @param context             {@link Context}
     * @param notificationPayload {@link LiNotificationPayload}
     */
    private void showCommunityNotification(Context context, LiNotificationPayload notificationPayload) {
        String contentText;
        String alertTitle;
        String type = notificationPayload.getType();
        String fromName = notificationPayload.getFromName();

        switch (type) {
            case "kudos":
                contentText = String.format(getString(R.string.li_message_kudo_notif_content_text), fromName);
                alertTitle = getString(R.string.li_message_kudo_notif_alert_text);
                break;
            case "solutions":
                contentText = String.format(getString(R.string.li_message_solution_notif_content_text), fromName);
                alertTitle = getString(R.string.li_message_solution_notif_alert_text);
                break;
            default:
                contentText = String.format(getString(R.string.li_message_messages_notif_content_text), fromName);
                alertTitle = getString(R.string.li_message_messages_notif_alert_text);
                break;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(alertTitle)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = mBuilder.build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(1, notification);
        }
    }

    /**
     * Persist token to the Community Notification Service.
     */
    private void registerToCommunityPushNotifications() {
        // Get updated Instance ID token.
        try {
            if (LiSDKManager.isInitialized()) {
                LiSDKManager sdk = LiSDKManager.getInstance();
                if (sdk.getFirebaseTokenProvider() != null) {
                    String token = sdk.getFirebaseTokenProvider().getDeviceToken();
                    new LiNotificationProviderImpl().onIdRefresh(token, this);
                }
            }
        } catch (IOException e) {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Exception in getting updated device token");
            e.printStackTrace();
        } catch (LiRestResponseException e) {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Exception in registering device token with community");
            e.printStackTrace();
        }
    }

}
