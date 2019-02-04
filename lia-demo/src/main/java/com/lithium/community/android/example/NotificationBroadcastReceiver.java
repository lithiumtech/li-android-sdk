package com.lithium.community.android.example;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiPostClientResponse;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // for this tutorial, we'll just show it in a toast;
        Long messageId = intent.getLongExtra(ReferenceFirebaseMessagingService.KEY_MESSAGE_ID, 0);
        int notificationId = intent.getIntExtra(ReferenceFirebaseMessagingService.KEY_NOTIFICATION_ID, 0);
        if (ReferenceFirebaseMessagingService.REPLY_ACTION.equals(intent.getAction())) {
            // do whatever you want with the message. Send to the server or add to the db.
            String message = getReplyMessage(intent).toString();
            new ReplyThread(context, messageId, message, notificationId).start();
        }

        if (ReferenceFirebaseMessagingService.KUDO_ACTION.equals(intent.getAction())) {
            new KudoThread("" + messageId, context, notificationId).start();
        }

        if (ReferenceFirebaseMessagingService.ACCEPT_SOLUTION_ACTION.equals(intent.getAction())) {
            new ASThread(messageId, context, notificationId).start();
        }
    }

    private CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(ReferenceFirebaseMessagingService.KEY_REPLY);
        }
        return null;
    }

    private class ASThread extends Thread {

        private Long messageId;
        private Context context;
        private int notificationId;

        ASThread(Long messageId, Context context, int notificationId) {
            this.context = context;
            this.messageId = messageId;
            this.notificationId = notificationId;
        }

        @Override
        public void run() {
            LiClientRequestParams params = new LiClientRequestParams.LiAcceptSolutionClientRequestParams(context, messageId);
            try {
                LiClient client = LiClientManager.getAcceptSolutionClient(params);
                client.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                    @Override
                    public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
                        cancelNotification(context, notificationId);
                    }

                    @Override
                    public void onError(Exception exception) {
                        cancelNotification(context, notificationId);
                    }
                });
            } catch (LiRestResponseException lrre) {
                lrre.printStackTrace();
                cancelNotification(context, notificationId);
            }
        }
    }

    private class KudoThread extends Thread {

        private String messageId;
        private Context context;
        private int notificationId;

        KudoThread(String messageId, Context context, int notificationId) {
            this.context = context;
            this.messageId = messageId;
            this.notificationId = notificationId;
        }

        @Override
        public void run() {

            LiClientRequestParams params = new LiClientRequestParams.LiKudoClientRequestParams(context, messageId);
            try {
                LiClient client = LiClientManager.getKudoClient(params);
                client.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                    @Override
                    public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
                        cancelNotification(context, notificationId);
                    }

                    @Override
                    public void onError(Exception exception) {
                        cancelNotification(context, notificationId);
                    }
                });
            } catch (LiRestResponseException lrre) {
                lrre.printStackTrace();
                cancelNotification(context, notificationId);
            }
        }
    }

    private class ReplyThread extends Thread {

        private Long messageId;
        private String replyText;
        private Context context;
        private int notificationId;

        ReplyThread(Context context, Long messageId, String replyText, int notificationId) {
            this.context = context;
            this.messageId = messageId;
            this.replyText = replyText;
            this.notificationId = notificationId;
        }

        @Override
        public void run() {
            String subject = "";

            LiClientRequestParams params =
                    new LiClientRequestParams.LiCreateReplyClientRequestParams(
                            context,
                            subject,
                            replyText,
                            messageId,
                            null,
                            null);
            try {
                LiClient client = LiClientManager.getCreateReplyClient(params);
                client.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                    @Override
                    public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
                        cancelNotification(context, notificationId);
                    }

                    @Override
                    public void onError(Exception exception) {
                        cancelNotification(context, notificationId);
                    }
                });
            } catch (LiRestResponseException lrre) {
                lrre.printStackTrace();
                cancelNotification(context, notificationId);
            }
        }
    }

    private void cancelNotification(Context context, int notificationId) {
        NotificationManager manager = ( NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
