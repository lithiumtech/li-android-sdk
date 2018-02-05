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

package lithium.community.android.sdk.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import lithium.community.android.sdk.R;

/**
 * This class is for receiving a notification from FireBase and showing it on the notification bar of the device.
 * Created by sumit.pannalall on 12/29/16.
 */
public class LiFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "LiFirebaseMessagingService";

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
        if (type.equals("kudos")) {
            contentText = String.format(getString(R.string.li_message_kudo_notif_content_text),
                    fromName);
            alertTitle = getString(R.string.li_message_kudo_notif_alert_text);
        } else if (type.equals("solutions")) {
            contentText = String.format(getString(R.string.li_message_solution_notif_content_text),
                    fromName);
            alertTitle = getString(R.string.li_message_solution_notif_alert_text);
        } else {
            contentText = String.format(getString(R.string.li_message_messages_notif_content_text),
                    fromName);
            alertTitle = getString(R.string.li_message_messages_notif_alert_text);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(alertTitle)
                        .setContentText(contentText);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notif = mBuilder.build();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notif);

    }
    // [END receive_message]
}
