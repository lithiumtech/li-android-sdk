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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.notification.LiNotificationPayload;
import com.lithium.community.android.notification.LiNotificationProviderImpl;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.ui.components.activities.LiConversationActivity;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author adityasharat
 */
public class ReferenceFirebaseMessagingService extends FirebaseMessagingService {

    public static final String REPLY_ACTION = "action.reply";
    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_NOTIFICATION_ID = "notification_id";
    public static final int VALUE_NOTIFICATION_ID = 100001;
    public static final String KEY_REPLY = "Reply";
    public static final String KEY_KUDO = "Kudos";
    public static final String KEY_ACCEPT_SOLUTION = "Accept Solution";
    public static final String KUDO_ACTION = "action.Kudo";
    public static final String ACCEPT_SOLUTION_ACTION = "action.AS";
    public static final String KEY_REPORT_ABUSE = "Report";
    public static final String KEY_SHARE = "Share";
    public static final String ACTION_REPORT_ABUSE = "action.RA";
    public static final String ACTION_SHARE = "action.Share";
    public static final String KEY_USER_ID = "AUTHOR_ID";
    public static final String ACTION_UNSUBSCRIBE = "action.Unsubscribe";
    public static final String KEY_UNSUBSCRIBE = "Unsubscribe";


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

        String type = notificationPayload.getType();
        String fromName = notificationPayload.getFromName();
        String message = notificationPayload.getMessage();
        String messageID;
        String replyMessageId;

        switch (type) {
            case "kudos":
                replyMessageId = null;
                messageID = new JsonParser().parse(message).getAsJsonObject().get("eventSummary").getAsJsonObject().get("entityId").getAsString();
                break;
            case "solutions":
                replyMessageId = null;
                messageID = new JsonParser().parse(message).getAsJsonObject().get("eventSummary").getAsJsonObject().get("topicUid").getAsString();
                break;
            default:
                JsonObject object = new JsonParser().parse(message).getAsJsonObject().get("eventSummary").getAsJsonObject();
                messageID = object.get("topicUid").getAsString();
                replyMessageId = object.get("messageUid").getAsString();
                break;
        }
        new RetrieveMessageThread(messageID, fromName, replyMessageId, type).start();
    }

    private void showNotification(String alertTitle, String contentText, String messageID, LiMessage message, LiMessage reply, String type) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentInfo(reply == null ? message.getBody() : reply.getBody())
                .setContentTitle(alertTitle)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setSmallIcon(R.mipmap.ic_launcher);

        switch (type) {
            case "solutions" : {
                Intent intent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
                intent.setAction(ACTION_SHARE);
                intent.putExtra(KEY_NOTIFICATION_ID, VALUE_NOTIFICATION_ID);
                String data = String.format("Topic '%1$s' has a solution \n\n '%2$s'",
                        message.getSubject(), Html.fromHtml(message.getBody()));
                intent.putExtra(KEY_MESSAGE_ID, Long.valueOf(messageID));
                intent.putExtra(KEY_MESSAGE, data);
                PendingIntent pi = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        101,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(R.mipmap.ic_launcher, KEY_SHARE, pi);
            }
            break;

            case "kudos": {
                {
                    Intent intent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
                    intent.setAction(ACTION_UNSUBSCRIBE);
                    intent.putExtra(KEY_NOTIFICATION_ID, VALUE_NOTIFICATION_ID);
                    intent.putExtra(KEY_MESSAGE_ID, Long.valueOf(messageID));
                    PendingIntent pi = PendingIntent.getBroadcast(
                            getApplicationContext(),
                            102,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.mipmap.ic_launcher, KEY_UNSUBSCRIBE, pi);
                }
                /*{ //Report
                    Intent intent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
                    intent.setAction(ACTION_REPORT_ABUSE);
                    intent.putExtra(KEY_NOTIFICATION_ID, VALUE_NOTIFICATION_ID);
                    intent.putExtra(KEY_MESSAGE_ID, Long.valueOf(reply.getId()));
                    intent.putExtra(KEY_USER_ID, reply.getAuthor().getId());
                    PendingIntent pi = PendingIntent.getBroadcast(
                            getApplicationContext(),
                            103,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.mipmap.ic_launcher, KEY_REPORT_ABUSE, pi);
                }*/
            }
            break;
            default:
                if (reply != null) {
                    String imageUrl = null;
                    if (reply.getBody().contains("<img src=\"")) {
                        String value = reply.getBody().split("<img src=\"")[1];
                        imageUrl = value.substring(0, value.indexOf("\"") - 1);
                    }

                    if (!TextUtils.isEmpty(imageUrl)) {
                        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
                        style.setSummaryText(contentText);
                        style.bigPicture(getBitmapFromURL(imageUrl));
                        builder.setStyle(style);
                    }

                    { //kudo action
                        Intent intent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
                        intent.setAction(KUDO_ACTION);
                        intent.putExtra(KEY_NOTIFICATION_ID, VALUE_NOTIFICATION_ID);
                        intent.putExtra(KEY_MESSAGE_ID, Long.valueOf(reply.getId()));
                        PendingIntent pi = PendingIntent.getBroadcast(
                                getApplicationContext(),
                                104,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.addAction(R.mipmap.ic_launcher, KEY_KUDO, pi);
                    }
                    { //accept solution action
                        Intent intent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
                        intent.setAction(ACCEPT_SOLUTION_ACTION);
                        intent.putExtra(KEY_NOTIFICATION_ID, VALUE_NOTIFICATION_ID);
                        intent.putExtra(KEY_MESSAGE_ID, Long.valueOf(reply.getId()));
                        PendingIntent pi = PendingIntent.getBroadcast(
                                getApplicationContext(),
                                105,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.addAction(R.mipmap.ic_launcher, KEY_ACCEPT_SOLUTION, pi);
                    }
                    { //reply action
                        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                                .setLabel(KEY_REPLY)
                                .build();
                        Intent intent = new Intent(getApplicationContext(), NotificationBroadcastReceiver.class);
                        intent.setAction(REPLY_ACTION);
                        intent.putExtra(KEY_NOTIFICATION_ID, VALUE_NOTIFICATION_ID);
                        intent.putExtra(KEY_MESSAGE_ID, Long.valueOf(messageID));
                        PendingIntent pi = PendingIntent.getBroadcast(
                                getApplicationContext(),
                                106,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                                R.drawable.ic_reply_black, KEY_REPLY, pi)
                                .addRemoteInput(remoteInput)
                                .setAllowGeneratedReplies(true)
                                .build();
                        builder.addAction(replyAction);
                    }
                }
        }

        Intent notificationIntent = new Intent(this, LiConversationActivity.class);
        notificationIntent.putExtra(LiSDKConstants.UPDATE_TOOLBAR_TITLE, true);
        notificationIntent.putExtra(LiSDKConstants.SELECTED_MESSAGE_ID, Long.valueOf(messageID));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(VALUE_NOTIFICATION_ID, builder.build());
        } else {
            Log.e(getClass().getSimpleName(), "Notification Manager was null");
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private class RetrieveMessageThread extends Thread {

        private String name;
        private String replyMessageId;
        private String type;
        private String messageId;

        RetrieveMessageThread(String messageId, String name, String replyMessageId, String type) {
            this.messageId = messageId;
            this.name = name;
            this.replyMessageId = replyMessageId;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                LiClientRequestParams params = new LiClientRequestParams.LiMessageClientRequestParams(getApplicationContext(), Long.parseLong(messageId));
                LiClient client = LiClientManager.getMessageClient(params);
                client.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                    @Override
                    public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
                        if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                            String contentText;
                            String alertTitle;
                            final String messageID;
                            final LiMessage message = (LiMessage) response.getResponse().get(0);
                            switch (type) {
                                case "kudos":
                                    contentText = String.format(getString(R.string.li_message_kudo_notif_content_text), name, Html.fromHtml(message.getBody()));
                                    alertTitle = getString(R.string.li_message_kudo_notif_alert_text);
                                    replyMessageId = null;
                                    messageID = "" + message.getId();
                                    showNotification(alertTitle, contentText, messageID, message, null, type);
                                    break;
                                case "solutions":
                                    contentText = String.format(getString(R.string.li_message_solution_notif_content_text),
                                            name, Html.fromHtml(message.getBody()));
                                    alertTitle = getString(R.string.li_message_solution_notif_alert_text);
                                    replyMessageId = null;
                                    messageID = "" + message.getId();
                                    showNotification(alertTitle, contentText, messageID, message, null, type);
                                    break;

                                default:
                                    if (!TextUtils.isEmpty(replyMessageId)) {
                                        LiClientRequestParams params
                                                = new LiClientRequestParams.LiMessageClientRequestParams(
                                                getApplicationContext(),
                                                Long.parseLong(replyMessageId));
                                        LiClient client = LiClientManager.getMessageClient(params);
                                        client.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                                            @Override
                                            public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
                                                if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                                                    final LiMessage replyMessage = (LiMessage) response.getResponse().get(0);
                                                    String contentText = String.format(getString(R.string.li_message_messages_notif_content_text),
                                                            name, message.getSubject(), Html.fromHtml(replyMessage.getBody()));
                                                    String alertTitle = getString(R.string.li_message_messages_notif_alert_text);
                                                    String messageID = "" + message.getId();
                                                    showNotification(alertTitle, contentText, messageID, message, replyMessage, type);
                                                }
                                            }

                                            @Override
                                            public void onError(Exception exception) {
                                                exception.printStackTrace();
                                            }
                                        });
                                    }
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            } catch (LiRestResponseException lrre) {
                lrre.printStackTrace();
            }
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
