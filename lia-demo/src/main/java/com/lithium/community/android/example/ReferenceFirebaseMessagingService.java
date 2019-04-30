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

package com.lithium.community.android.example;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonParser;
import com.lithium.community.android.example.R;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.notification.LiNotificationPayload;
import com.lithium.community.android.notification.LiNotificationProviderImpl;
import com.lithium.community.android.ui.components.activities.LiConversationActivity;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.io.IOException;
import java.util.Map;

/**
 * @author adityasharat
 */
public class ReferenceFirebaseMessagingService extends FirebaseMessagingService {

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
        } else {
            //TODO this is the space where the developer will have his other notification layout setup
        }
    }

    private void showCommunityNotification(Context context, LiNotificationPayload notificationPayload) {
        String contentText;
        String alertTitle;
        String type = notificationPayload.getType();
        String fromName = notificationPayload.getFromName();
        String message = notificationPayload.getMessage();
        String messageID;

        switch (type) {
            case "kudos":
                contentText = String.format(getString(R.string.li_message_kudo_notif_content_text), fromName);
                alertTitle = getString(R.string.li_message_kudo_notif_alert_text);
                messageID = new JsonParser().parse(message).getAsJsonObject().get("eventSummary").getAsJsonObject().get("entityId").getAsString();
                break;
            case "solutions":
                contentText = String.format(getString(R.string.li_message_solution_notif_content_text), fromName);
                alertTitle = getString(R.string.li_message_solution_notif_alert_text);
                messageID = new JsonParser().parse(message).getAsJsonObject().get("eventSummary").getAsJsonObject().get("topicUid").getAsString();
                break;
            default:
                contentText = String.format(getString(R.string.li_message_messages_notif_content_text), fromName);
                alertTitle = getString(R.string.li_message_messages_notif_alert_text);
                messageID = new JsonParser().parse(message).getAsJsonObject().get("eventSummary").getAsJsonObject().get("topicUid").getAsString();
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(alertTitle)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Khoros_Community";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Khoros Community", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        Intent notificationIntent = new Intent(this, LiConversationActivity.class);
        notificationIntent.putExtra(LiSDKConstants.UPDATE_TOOLBAR_TITLE, true);
        notificationIntent.putExtra(LiSDKConstants.SELECTED_MESSAGE_ID, Long.valueOf(messageID));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        if (manager != null) {
            manager.notify(1, builder.build());
        } else {
            Log.e(getClass().getSimpleName(), "Notification Manager was null");
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
                if (sdk.getFirebaseTokenProvider() != null && sdk.isUserLoggedIn()) {
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
