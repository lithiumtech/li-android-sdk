package com.lithium.community.android.example;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.rest.LiPostClientResponse;
import com.lithium.community.android.ui.components.adapters.LiConversationAdapter;
import com.lithium.community.android.ui.components.utils.LiUIUtils;

import java.net.HttpURLConnection;

import static com.lithium.community.android.example.ReferenceFirebaseMessagingService.KEY_USER_ID;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // for this tutorial, we'll just show it in a toast;
        Long messageId = intent.getLongExtra(ReferenceFirebaseMessagingService.KEY_MESSAGE_ID, 0);
        int notificationId = intent.getIntExtra(ReferenceFirebaseMessagingService.KEY_NOTIFICATION_ID, 0);
        if (ReferenceFirebaseMessagingService.REPLY_ACTION.equals(intent.getAction())) {
            String message = getReplyMessage(intent).toString();
            new ReplyThread(context, messageId, message, notificationId).start();
        }

        if (ReferenceFirebaseMessagingService.KUDO_ACTION.equals(intent.getAction())) {
            new KudoThread("" + messageId, context, notificationId).start();
        }

        if (ReferenceFirebaseMessagingService.ACCEPT_SOLUTION_ACTION.equals(intent.getAction())) {
            new ASThread(messageId, context, notificationId).start();
        }

        if (ReferenceFirebaseMessagingService.ACTION_REPORT_ABUSE.equals(intent.getAction())) {
            long userId = intent.getLongExtra(KEY_USER_ID, 0);
            new ReportAbuseThread(context, messageId, userId, notificationId).start();
        }

        if (ReferenceFirebaseMessagingService.ACTION_SHARE.equals(intent.getAction())) {
            shareContent(context, intent.getStringExtra(ReferenceFirebaseMessagingService.KEY_MESSAGE), notificationId);
        }

        if (ReferenceFirebaseMessagingService.ACTION_UNSUBSCRIBE.equals(intent.getAction())) {
            new UnsubscribeThread(context, messageId, notificationId).start();
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

    private class ReportAbuseThread extends Thread {

        private Long messageId;
        private Long userId;
        private Context context;
        private int notificationId;

        ReportAbuseThread(Context context, Long messageId, Long userId, int notificationId) {
            this.messageId = messageId;
            this.userId = userId;
            this.context = context;
            this.notificationId = notificationId;
        }

        @Override
        public void run() {
            LiClientRequestParams params = new LiClientRequestParams.LiReportAbuseClientRequestParams(context, messageId + "", userId + "", "Abusive comment");
            try {
                LiClient client = LiClientManager.getReportAbuseClient(params);
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

    private class UnsubscribeThread extends Thread {

        private Context context;
        private Long messageId;
        private int notificationId;

        UnsubscribeThread(Context context, Long messageId, int notificationId) {
            this.context = context;
            this.messageId = messageId;
            this.notificationId = notificationId;
        }

        @Override
        public void run() {
            LiUser user = LiSDKManager.getInstance().getLoggedInUser();
            LiClientRequestParams.LiUserMessageSusbscriptionRequestParans params
                    = new LiClientRequestParams.LiUserMessageSusbscriptionRequestParans(context, "" + messageId, "" + user.getId());
            try {
                LiClient client = LiClientManager.getUserMessageSubscriptionsClient(params);
                client.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                    @Override
                    public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
                        if (response.getHttpCode() == HttpURLConnection.HTTP_OK || response.getHttpCode() == HttpURLConnection.HTTP_CREATED) {
                            JsonObject responseObject = response.getJsonObject();
                            if (!responseObject.has(LiBaseResponse.DATA)) {
                                cancelNotification(context, notificationId);
                                return;
                            }
                            JsonObject dataObject = responseObject.getAsJsonObject(LiBaseResponse.DATA);
                            if (!dataObject.has(LiBaseResponse.ITEMS)) {
                                cancelNotification(context, notificationId);
                                return;
                            }
                            JsonArray array = dataObject.getAsJsonArray(LiBaseResponse.ITEMS);
                            if (array == null || array.size() != 1) {
                                cancelNotification(context, notificationId);
                                return;
                            }
                            String id = array.get(0).getAsJsonObject().get(LiBaseResponse.ID).getAsString();
                            LiClientRequestParams.LiDeleteSubscriptionParams deleteSubscriptionParams =
                                    new LiClientRequestParams.LiDeleteSubscriptionParams(context, id);
                            LiClient client = LiClientManager.getSubscriptionDeleteClient(deleteSubscriptionParams);
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
                        } else {
                            cancelNotification(context, notificationId);
                        }
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

    private void shareContent(Context context, String content, int notificationId) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity( Intent.createChooser(intent, "Share with..."));
        cancelNotification(context, notificationId);
    }

    private void cancelNotification(Context context, int notificationId) {
        NotificationManager manager = ( NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
