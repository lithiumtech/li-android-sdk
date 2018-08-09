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

package com.lithium.community.android.ui.components.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.helpers.LiKudoMetrics;
import com.lithium.community.android.model.post.LiSubscriptionPostModel;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.model.response.LiTargetModel;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiClientResponse;
import com.lithium.community.android.rest.LiDeleteClientResponse;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.rest.LiPostClientResponse;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.activities.LiCreateMessageActivity;
import com.lithium.community.android.ui.components.activities.LiReportAbuseActivity;
import com.lithium.community.android.ui.components.custom.ui.LiImageGetter;
import com.lithium.community.android.ui.components.fragments.LiConversationFragment;
import com.lithium.community.android.ui.components.fragments.LiOnMessageRowClickListener;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.util.List;


/**
 * {@link RecyclerView.Adapter} that can display a {@link LiMessage} and makes a call to the
 * specified {@link LiMessage}.
 * This class is responsible for displaying the message thread.
 */
public class LiConversationAdapter extends LiBaseRecyclerAdapter {

    public interface ActionInProgressListener {
        void actionInProgress(boolean inProgress);
    }

    private static final int TYPE_ORIGINAL_POST = 0;
    LiConversationFragment fragment;
    private boolean isAcceptedSolutionPresent;
    private int avatarDefaultIcon;
    private int firstDiscussionIdx;
    private String htmlTemplate;
    private ActionInProgressListener mActionInProgressListener;

    public LiConversationAdapter(List<LiBaseModel> items, LiOnMessageRowClickListener listener, Activity activity,
            RecyclerView recyclerView, LiConversationFragment fragment) {
        super(items, activity, listener);
        analyzeItems();
        htmlTemplate = LiUIUtils.getRawString(activity, R.raw.message_template);
        TypedArray typedArrForBrowse = activity.getTheme().obtainStyledAttributes(
                new int[]{R.attr.li_theme_messageUserAvatarIcon});
        avatarDefaultIcon = typedArrForBrowse.getResourceId(0, -1);
        typedArrForBrowse.recycle();
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(activity);
        }
        this.fragment = fragment;
        cookieManager.setAcceptCookie(true);
    }

    public void setActionInProgressListener(ActionInProgressListener actionInProgressListener) {
        this.mActionInProgressListener = actionInProgressListener;
    }

    @Override
    public void setItems(List<LiBaseModel> mValues) {
        this.mValues = mValues;
        analyzeItems();
    }

    protected void analyzeItems() {
        firstDiscussionIdx = 0;
        for (LiBaseModel clientResponseModel : mValues) {
            if (firstDiscussionIdx == 0) {
                firstDiscussionIdx++;
                continue;
            }
            LiMessage item = ((LiTargetModel) clientResponseModel).getLiMessage();
            if (item.isAcceptedSolution() == null || !item.isAcceptedSolution()) {
                break;
            } else {
                isAcceptedSolutionPresent = true;
                firstDiscussionIdx++;
            }
        }
    }

    @Override
    public LiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_conversation_row_view, parent, false);
        return new LiViewHolderConversationMessage(view, activity);
    }

    @Override
    public void onBindViewHolder(final LiViewHolder holder, final int position) {
        LiBaseModel clientResponseModel = mValues.get(position);
        final LiMessage item = ((LiTargetModel) clientResponseModel).getLiMessage();
        final LiViewHolderConversationMessage liViewHolder = (LiViewHolderConversationMessage) holder;
        setupAuthorDetails(item, liViewHolder);
        liViewHolder.mPostTimeAgo.setText(
                LiUIUtils.toDuration(activity, item.getPostTime().getValue()));
        setupMessageBody(item, liViewHolder);
        setupKudoUI(item, liViewHolder);
        setupReplyUI(item, liViewHolder);
        setupAcceptUI(item, liViewHolder);
        setupRowHeaders(position, item, liViewHolder);
        liViewHolder.mLiConversationActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(liViewHolder.mLiConversationActionsButton, position);
            }
        });

    }

    /**
     * This method populates the actions that can be performed on a particular message in the conversation
     *
     * @param view     The view which was clicked
     * @param position position tells which model to pick from the list.
     */
    private void showPopupMenu(final View view, final int position) {
        // inflate menu
        final PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.li_conversation_action_menu, popup.getMenu());
        LiBaseModel clientResponseModel = mValues.get(position);
        final LiMessage item = ((LiTargetModel) clientResponseModel).getLiMessage();
        LiUIUtils.updateMarkReadPopmenuTitle(popup, item);

        if (!item.getUserContext().isCanDelete()) {
            popup.getMenu().removeItem(R.id.li_action_delete);
        }

        boolean isSubscribed = item.getUserContext().isSubscribed();
        popup.getMenu().findItem(R.id.li_action_subscribe).setTitle(isSubscribed ? R.string.li_unsubscribe : R.string.li_subscribe);
        boolean isParentItem = ((getItems().size() == 1) || (position == 0));
        popup.setOnMenuItemClickListener(new PopupMenuClickListener(item, isParentItem, position));
        popup.show();
    }

    private void actionInProgress(boolean inProgress) {
        if (mActionInProgressListener != null) {
            mActionInProgressListener.actionInProgress(inProgress);
        }
    }

    private class PopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {

        private final LiMessage item;
        private final boolean isParentItem;
        private final int position;

        PopupMenuClickListener(LiMessage item, boolean isParentItem, int position) {
            this.isParentItem = isParentItem;
            this.position = position;
            this.item = item;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.li_action_report_abuse) {
                openReportAbuse(item);
                return true;
            } else if (menuItem.getItemId() == R.id.li_action_mark_read) {
                markRead(item);
                return true;
            } else if (menuItem.getItemId() == R.id.li_action_delete) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteMessage(item, isParentItem);
                                dialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.li_are_you_sure)
                        .setPositiveButton(R.string.li_yes, dialogClickListener)
                        .setNegativeButton(R.string.li_no, dialogClickListener).show();

                return true;
            } else if (menuItem.getItemId() == R.id.li_action_subscribe) {
                boolean isSubscribed = item.getUserContext().isSubscribed();
                if (isSubscribed) {
                    unsubscribe(item, position);
                } else {
                    subscribe(item, position);
                }
                return false;
            } else {
                return false;
            }
        }
    }

    private void openReportAbuse(LiMessage item) {
        Bundle bundle = new Bundle();
        bundle.putString(LiSDKConstants.REPORT_ABUSE_MESSAGE_ID, item.getId() + "");
        bundle.putString(LiSDKConstants.REPORT_ABUSE_AUTHOR_ID, item.getAuthor().getId() + "");
        Intent i = new Intent(activity, LiReportAbuseActivity.class);
        i.putExtras(bundle);
        activity.startActivity(i);
    }

    private void markRead(LiMessage item) {
        if (item.getUserContext() != null) {
            boolean isMarkUnread = false;
            if (item.getUserContext().getRead()) {
                isMarkUnread = true;
            }
            actionInProgress(true);

            LiClientRequestParams.LiMarkMessageParams params = new LiClientRequestParams.LiMarkMessageParams(activity,
                    String.valueOf(item.getAuthor().getId()),
                    String.valueOf(item.getId()), isMarkUnread);
            try {
                LiClientManager.getMarkMessagePostClient(params).processAsync(new MarkReadCallback(item));
            } catch (LiRestResponseException e) {
                actionInProgress(false);
                Log.d(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
            }

        }
    }

    private class MarkReadCallback implements LiAsyncRequestCallback<LiPostClientResponse> {

        private final LiMessage item;

        MarkReadCallback(LiMessage item) {
            this.item = item;
        }

        @Override
        public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                item.getUserContext().setRead(!item.getUserContext().getRead());
                LiUIUtils.showMarkReadSuccessful(activity, item);
            } else {
                LiUIUtils.showMarkReadUnSuccessful(activity, item);
            }
            actionInProgress(false);
        }

        @Override
        public void onError(Exception exception) {
            actionInProgress(false);
            LiUIUtils.showMarkReadUnSuccessful(activity, item);
        }
    }

    private void deleteMessage(LiMessage item, boolean isParentMessage) {
        LiClientRequestParams.LiMessageDeleteClientRequestParams params;
        params = new LiClientRequestParams.LiMessageDeleteClientRequestParams(activity, String.valueOf(item.getId()));

        actionInProgress(true);
        try {
            LiClientManager.getMessageDeleteClient(params).processAsync(new DeleteMessageCallack(item, isParentMessage));
        } catch (LiRestResponseException e) {
            actionInProgress(false);
            LiUIUtils.showInAppNotification(activity, R.string.li_action_delete_unsuccessful);
            Log.d(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
        }
    }

    private class DeleteMessageCallack implements LiAsyncRequestCallback<LiDeleteClientResponse> {

        private final boolean isParentMessage;
        private final LiMessage item;

        DeleteMessageCallack(LiMessage item, boolean isParentMessage) {
            this.isParentMessage = isParentMessage;
            this.item = item;
        }

        @Override
        public void onSuccess(LiBaseRestRequest request, LiDeleteClientResponse response) {
            if (activity == null || fragment == null || !fragment.isAdded()) {
                return;
            }
            LiBaseResponse deleteResponse = response.getResponse();
            actionInProgress(false);

            if (deleteResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //if there was only one message in the conversation and delete was successful,
                        // finish the activity
                        if (isParentMessage) {
                            activity.finish();
                        } else {
                            fragment.onRefresh();
                        }

                        LiUIUtils.showInAppNotification(activity, R.string.li_action_delete_successful);
                    }
                });
            } else {
                LiUIUtils.showInAppNotification(activity, R.string.li_action_delete_unsuccessful);
            }
        }

        @Override
        public void onError(Exception exception) {
            LiUIUtils.showInAppNotification(activity, R.string.li_action_delete_unsuccessful);
            actionInProgress(false);
            Log.d(LiSDKConstants.GENERIC_LOG_TAG, exception.getMessage());
        }
    }

    private void subscribe(LiMessage item, int position) {
        actionInProgress(true);

        LiSubscriptionPostModel.Target target = new LiSubscriptionPostModel.MessageTarget(item.getId() + "");
        LiClientRequestParams.LiPostSubscriptionParams params = new LiClientRequestParams.LiPostSubscriptionParams(activity, target);
        try {
            LiClient liClient = LiClientManager.getSubscriptionPostClient(params);
            liClient.processAsync(new MessageSubscriptionCallback(position, item));
        } catch (LiRestResponseException lrre) {
            actionInProgress(false);
            LiUIUtils.showInAppNotification(activity, R.string.li_action_subscribe_unsuccessful);
            Log.d(LiSDKConstants.GENERIC_LOG_TAG, lrre.getMessage());
        }
    }

    private class MessageSubscriptionCallback implements LiAsyncRequestCallback<LiPostClientResponse> {
        private final int position;
        private final LiMessage item;

        MessageSubscriptionCallback(int position, LiMessage item) {
            this.position = position;
            this.item = item;
        }

        @Override
        public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
            actionInProgress(false);

            if (response.getHttpCode() == HttpURLConnection.HTTP_OK || response.getHttpCode() == HttpURLConnection.HTTP_CREATED) {
                LiUIUtils.showInAppNotification(activity, R.string.li_action_subscribe_successful);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item.getUserContext().setSubscribed(true);
                        notifyItemChanged(position);
                    }
                });
            } else {
                LiUIUtils.showInAppNotification(activity, R.string.li_action_subscribe_unsuccessful);
            }
        }

        @Override
        public void onError(Exception exception) {
            LiUIUtils.showInAppNotification(activity, R.string.li_action_subscribe_unsuccessful);
            actionInProgress(false);
            Log.d(LiSDKConstants.GENERIC_LOG_TAG, exception.getMessage());
        }
    }

    private void unsubscribe(LiMessage item, int position) {
        actionInProgress(true);

        LiUser user = LiSDKManager.getInstance().getLoggedInUser();
        LiClientRequestParams.LiUserMessageSusbscriptionRequestParans params
                = new LiClientRequestParams.LiUserMessageSusbscriptionRequestParans(activity, "" + item.getId(), "" + user.getId());
        try {
            LiClient client = LiClientManager.getUserMessageSubscriptionsClient(params);
            client.processAsync(new MessageUserSubscriptionsCallback(item, position));
        } catch (LiRestResponseException lrre) {
            actionInProgress(false);
            LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_unsuccessful);
            Log.d(LiSDKConstants.GENERIC_LOG_TAG, lrre.getMessage());
        }
    }

    public class MessageUserSubscriptionsCallback implements LiAsyncRequestCallback<LiGetClientResponse> {

        private final int position;
        private final LiMessage item;

        public MessageUserSubscriptionsCallback(LiMessage item, int position) {
            this.position = position;
            this.item = item;
        }

        @Override
        public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == HttpURLConnection.HTTP_OK || response.getHttpCode() == HttpURLConnection.HTTP_CREATED) {
                JsonObject responseObject = response.getJsonObject();
                if (!responseObject.has(LiBaseResponse.DATA)) {
                    LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_unsuccessful);
                    actionInProgress(false);
                    return;
                }
                JsonObject dataObject = responseObject.getAsJsonObject(LiBaseResponse.DATA);
                if (!dataObject.has(LiBaseResponse.ITEMS)) {
                    LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_unsuccessful);
                    actionInProgress(false);
                    return;
                }
                JsonArray array = dataObject.getAsJsonArray(LiBaseResponse.ITEMS);
                if (array == null || array.size() != 1) {
                    LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_unsuccessful);
                    actionInProgress(false);
                    return;
                }
                String id = array.get(0).getAsJsonObject().get(LiBaseResponse.ID).getAsString();
                LiClientRequestParams.LiDeleteSubscriptionParams deleteSubscriptionParams = new LiClientRequestParams.LiDeleteSubscriptionParams(activity, id);
                LiClient client = LiClientManager.getSubscriptionDeleteClient(deleteSubscriptionParams);
                client.processAsync(new MessageSubscriptionDeleteCallback(item, position));
            }
        }

        @Override
        public void onError(Exception exception) {
            actionInProgress(false);
            LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_unsuccessful);
            Log.d(LiSDKConstants.GENERIC_LOG_TAG, exception.getMessage());
        }
    }

    private class MessageSubscriptionDeleteCallback implements LiAsyncRequestCallback<LiDeleteClientResponse> {

        private final int position;
        private final LiMessage item;

        public MessageSubscriptionDeleteCallback(LiMessage item, int position) {
            this.position = position;
            this.item = item;
        }

        @Override
        public void onSuccess(LiBaseRestRequest request, LiDeleteClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == HttpURLConnection.HTTP_OK || response.getHttpCode() == HttpURLConnection.HTTP_CREATED) {
                LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_successful);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item.getUserContext().setSubscribed(false);
                        notifyItemChanged(position);
                    }
                });
            } else {
                LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_unsuccessful);
            }
            actionInProgress(false);
        }

        @Override
        public void onError(Exception exception) {
            actionInProgress(false);
            LiUIUtils.showInAppNotification(activity, R.string.li_action_unsubscribe_unsuccessful);
            Log.d(LiSDKConstants.GENERIC_LOG_TAG, exception.getMessage());
        }
    }

    /**
     * This method sets up the row headers for the replies whether it is an accepted solution or a discussion post
     *
     * @param position     position at which the row is to be displayed
     * @param item         @{@link LiMessage}
     * @param liViewHolder @{@link LiViewHolder}
     */
    protected void setupRowHeaders(int position, LiMessage item, LiViewHolderConversationMessage liViewHolder) {
        switch (position) {
            case TYPE_ORIGINAL_POST:
                liViewHolder.mPostSubjectTxt.setText(item.getSubject());
                liViewHolder.mPostStatusHeader.setVisibility(View.GONE);
                break;
            case 1:
                liViewHolder.mPostSubjectTxt.setVisibility(View.GONE);
                if (isAcceptedSolutionPresent) {
                    liViewHolder.mPostStatusHeader.setVisibility(View.VISIBLE);
                    liViewHolder.mPostStatusHeader.setText(activity.getString(R.string.li_post_status_accepted_solution));
                } else {
                    liViewHolder.mPostStatusHeader.setText(activity.getString(R.string.li_post_status_discussion));
                }
                break;
            default:
                liViewHolder.mPostSubjectTxt.setVisibility(View.GONE);
                if (position == firstDiscussionIdx) {
                    liViewHolder.mPostStatusHeader.setVisibility(View.VISIBLE);
                    liViewHolder.mPostStatusHeader.setText(activity.getString(R.string.li_post_status_discussion));
                } else {
                    liViewHolder.mPostStatusHeader.setVisibility(View.GONE);
                }
                break;
        }

    }

    /**
     * This method sets up the UI for a message accept button. If a message is already accepted, don't show the accept button
     * If a user doesn't have the right access to accept a post the button is hidden
     * Also sets up the click on listeners on indivudual buttons.
     *
     * @param item         @{@link LiMessage}
     * @param liViewHolder @{@link LiViewHolder}
     */
    protected void setupAcceptUI(final LiMessage item, final LiViewHolderConversationMessage liViewHolder) {
        if (item.isAcceptedSolution() || (item.isAcceptedOnUI() != null && item.isAcceptedOnUI())) {
            liViewHolder.mPostAcceptTxt.setVisibility(View.GONE);
            liViewHolder.mPostAcceptBtn.setVisibility(View.GONE);
        } else if (item.getCanAcceptSolution()) {
            liViewHolder.mPostAcceptTxt.setVisibility(View.VISIBLE);
            liViewHolder.mPostAcceptBtn.setVisibility(View.VISIBLE);
            View.OnClickListener acceptClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiAcceptSolutionClientRequestParams(activity, item.getId());
                        LiClient client = LiClientManager.getAcceptSolutionClient(liClientRequestParams);
                        liViewHolder.mPostAcceptBtn.setVisibility(View.INVISIBLE);
                        liViewHolder.mPosAcceptProgressBar.setVisibility(View.VISIBLE);
                        client.processAsync(new LiAsyncRequestCallback<LiClientResponse>() {
                            @Override
                            public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                                    final LiClientResponse liClientResponse) throws LiRestResponseException {
                                if (activity == null) {
                                    return;
                                }
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LiBaseResponse acceptResponse =
                                                (LiBaseResponse) liClientResponse.getResponse();
                                        if (acceptResponse.getHttpCode()
                                                == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                                            item.setAcceptedOnUI(true);
                                            if (!item.isAcceptedSolution()) {
                                                liViewHolder.mPostAcceptTxt.setVisibility(View.GONE);
                                                liViewHolder.mPostAcceptBtn.setVisibility(View.GONE);
                                            }
                                            cloneAcceptedSolution(item);
                                            LiUIUtils.showInAppNotification(activity,
                                                    R.string.li_accept_success_txt);
                                        } else {
                                            item.setAcceptedOnUI(false);
                                            LiUIUtils.showInAppNotification(activity,
                                                    R.string.li_accept_forbidden_txt);
                                        }
                                        liViewHolder.mPosAcceptProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                if (activity == null) {
                                    return;
                                }
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        liViewHolder.mPostAcceptBtn.setVisibility(View.VISIBLE);
                                        liViewHolder.mPosAcceptProgressBar.setVisibility(View.GONE);
                                        LiUIUtils.showInAppNotification(activity, R.string.li_accept_error_txt);
                                    }
                                });
                            }
                        });
                    } catch (LiRestResponseException e) {
                        Log.e(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
                    }
                }
            };
            liViewHolder.mPostAcceptTxt.setOnClickListener(acceptClickListener);
            liViewHolder.mPostAcceptBtn.setOnClickListener(acceptClickListener);
        } else {
            liViewHolder.mPostAcceptTxt.setVisibility(View.GONE);
            liViewHolder.mPostAcceptBtn.setVisibility(View.GONE);
        }
    }

    /**
     * clones the accepted solution and copies it into the list of items in the accepted solutions section i.e. just after the original message.
     *
     * @param item {@link LiMessage}
     */
    private void cloneAcceptedSolution(LiMessage item) {
        LiMessage newLiMessage = LiUIUtils.clone(item);
        newLiMessage.setIsAcceptedSolution(true);
        mValues.add(1, newLiMessage);
        analyzeItems();
        this.notifyDataSetChanged();
    }

    /**
     * Sets up the reply UI for the message row and also add the listener for the reply button click
     *
     * @param item         {@link LiMessage}
     * @param liViewHolder {@link LiViewHolder}
     */
    protected void setupReplyUI(final LiMessage item, LiViewHolderConversationMessage liViewHolder) {
        View.OnClickListener replyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, false);
                bundle.putLong(LiSDKConstants.SELECTED_MESSAGE_ID, item.getId());
                LiMessage originalMessage = (LiMessage) mValues.get(0);
                bundle.putString(LiSDKConstants.ORIGINAL_MESSAGE_TITLE, originalMessage.getSubject());
                Intent i = new Intent(activity, LiCreateMessageActivity.class);
                i.putExtras(bundle);
                activity.startActivity(i);
            }
        };

        if (item.getUserContext().isCanReply()) {
            liViewHolder.mPostReplyBtn.setOnClickListener(replyClickListener);
            liViewHolder.mPostReplyTxt.setOnClickListener(replyClickListener);
        } else {
            liViewHolder.mPostReplyBtn.setEnabled(false);
            liViewHolder.mPostReplyTxt.setEnabled(false);
        }
    }

    /**
     * Sets up the Kudo UI for the message and adds the listener for the kudo button
     *
     * @param message      {@link LiMessage}
     * @param liViewHolder {@link LiViewHolder}
     */
    protected void setupKudoUI(final LiMessage message, final LiViewHolderConversationMessage liViewHolder) {
        liViewHolder.mPostKudoCount.setText(String.valueOf(message.getKudoMetrics() == null ? 0
                : message.getKudoMetrics().getSum().getWeight().getValue()));
        final View.OnClickListener kudosClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    liViewHolder.mPosKudoBtn.setVisibility(View.INVISIBLE);
                    liViewHolder.mPosKudoBtn.setEnabled(false);
                    liViewHolder.mPostKudoCount.setEnabled(false);
                    liViewHolder.mPosKudoProgressBar.setVisibility(View.VISIBLE);
                    LiClientRequestParams liKudoClientRequestParams = new LiClientRequestParams.LiKudoClientRequestParams(activity,
                            String.valueOf(message.getId()));

                    if (message.getUserContext().getKudo()) {
                        LiClientRequestParams liUnKudoClientRequestParams;
                        liUnKudoClientRequestParams = new LiClientRequestParams.LiUnKudoClientRequestParams(activity, String.valueOf(message.getId()));
                        final LiClient unKudoClient = LiClientManager.getUnKudoClient(liUnKudoClientRequestParams);
                        unKudoClient.processAsync(new LiAsyncRequestCallback<LiDeleteClientResponse>() {
                            @Override
                            public void onSuccess(LiBaseRestRequest request, final LiDeleteClientResponse response) {
                                if (activity == null) {
                                    return;
                                }
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        liViewHolder.mPosKudoBtn.setEnabled(true);
                                        liViewHolder.mPostKudoCount.setEnabled(true);
                                        liViewHolder.mPosKudoBtn.setVisibility(View.VISIBLE);
                                        liViewHolder.mPosKudoProgressBar.setVisibility(View.GONE);

                                        if (response.getResponse().getHttpCode() != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                                            LiUIUtils.showInAppNotification(activity, R.string.li_kudo_error_txt);
                                            return;
                                        }
                                        LiKudoMetrics kudoMetrics = message.getKudoMetrics();
                                        Long kudoCount = kudoMetrics.getSum()
                                                .getWeight().getValue();
                                        //extract kudo weight for subtracting it to the number of kudos
                                        //TODO in future this logic must be left to the LIA backend. We should read the kudo metrics that will come from the
                                        // response and update the kudos count directly
                                        int kudoWeight = 1;
                                        if (response.getResponse().getData().get("data").getAsJsonObject().has("weight")) {
                                            kudoWeight = response.getResponse().getData().get("data").getAsJsonObject().get("weight").getAsInt();
                                        }
                                        liViewHolder.mPostKudoCount.setText(
                                                String.valueOf(kudoCount - kudoWeight));
                                        message.getUserContext().setKudo(false);
                                        kudoMetrics.getSum().getWeight().setValue(kudoCount - kudoWeight);
                                        setTint(liViewHolder.mPosKudoBtn, R.color.li_theme_remove_tint);
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception exception) {
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            liViewHolder.mPosKudoBtn.setEnabled(true);
                                            liViewHolder.mPostKudoCount.setEnabled(true);
                                            liViewHolder.mPosKudoBtn.setVisibility(View.VISIBLE);
                                            liViewHolder.mPosKudoProgressBar.setVisibility(View.GONE);
                                            LiUIUtils.showInAppNotification(activity, R.string.li_kudo_error_txt);
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        final LiClient kudoClient = LiClientManager.getKudoClient(liKudoClientRequestParams);
                        kudoClient.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                            @Override
                            public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                                    final LiPostClientResponse liClientResponse) {
                                if (activity == null) {
                                    return;
                                }
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        liViewHolder.mPosKudoBtn.setEnabled(true);
                                        liViewHolder.mPostKudoCount.setEnabled(true);
                                        liViewHolder.mPosKudoBtn.setVisibility(View.VISIBLE);
                                        liViewHolder.mPosKudoProgressBar.setVisibility(View.GONE);
                                        if (liClientResponse.getResponse().getHttpCode()
                                                != LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                                            LiUIUtils.showInAppNotification(activity,
                                                    R.string.li_kudo_error_txt);
                                            return;
                                        }
                                        LiKudoMetrics kudoMetrics = message.getKudoMetrics();
                                        //extract kudo weight for adding it to the number of kudos
                                        //TODO in future this logic must be left to the LIA backend. We should read the kudo metrics that will come from the
                                        // response and update the kudos count directly
                                        int kudoWeight = 1;
                                        if (liClientResponse.getResponse().getData().get("data").getAsJsonObject().has("weight")) {
                                            kudoWeight = liClientResponse.getResponse().getData().get("data").getAsJsonObject().get("weight").getAsInt();
                                        }

                                        if (kudoMetrics == null
                                                || kudoMetrics.getSum().getWeight().getValue() == 0) {
                                            liViewHolder.mPostKudoCount.setText(String.valueOf(kudoWeight));
                                        } else {
                                            liViewHolder.mPostKudoCount.setText(
                                                    String.valueOf(kudoMetrics.getSum()
                                                            .getWeight().getValue() + kudoWeight));
                                        }
                                        kudoMetrics.getSum().getWeight()
                                                .setValue(kudoMetrics.getSum().getWeight().getValue() + kudoWeight);
                                        message.getUserContext().setKudo(true);
                                        setTint(liViewHolder.mPosKudoBtn, R.color.li_theme_already_kudoed);
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            liViewHolder.mPosKudoBtn.setEnabled(true);
                                            liViewHolder.mPostKudoCount.setEnabled(true);
                                            liViewHolder.mPosKudoBtn.setVisibility(View.VISIBLE);
                                            liViewHolder.mPosKudoProgressBar.setVisibility(View.GONE);
                                            LiUIUtils.showInAppNotification(activity,
                                                    R.string.li_kudo_error_txt);
                                        }
                                    });
                                }
                            }
                        });
                    }
                } catch (LiRestResponseException e) {
                    Log.e(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
                }
            }
        };
        //Change color of the kudo button
        int btnColorRes = R.color.li_listIconColor;// default grey color (disabled)

        if (message.getUserContext().isCanKudo()) {
            liViewHolder.mPostKudoCount.setEnabled(true);
            liViewHolder.mPosKudoBtn.setEnabled(true);
            liViewHolder.mPostKudoCount.setOnClickListener(kudosClickListener);
            liViewHolder.mPosKudoBtn.setOnClickListener(kudosClickListener);
            if (message.getUserContext().getKudo()) {
                btnColorRes = R.color.li_theme_already_kudoed; //blue color if already kudo-ed
            } else {
                btnColorRes = R.color.li_theme_remove_tint; //remove tint means kudo is enabled
            }
        } else {
            liViewHolder.mPostKudoCount.setEnabled(false);
            liViewHolder.mPosKudoBtn.setEnabled(false);
            liViewHolder.mPostKudoCount.setOnClickListener(null);
            liViewHolder.mPosKudoBtn.setOnClickListener(null);
        }
        setTint(liViewHolder.mPosKudoBtn, btnColorRes);
    }

    private void setTint(ImageView kudoBtn, final int btnColorRes) {
        Drawable kudoDrawable = kudoBtn.getDrawable();
        kudoDrawable = kudoDrawable.mutate();
        kudoDrawable = DrawableCompat.wrap(kudoDrawable);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            DrawableCompat.setTint(kudoDrawable, activity.getResources().getColor(
                    btnColorRes, activity.getTheme()));
        } else {
            DrawableCompat.setTint(kudoDrawable,
                    activity.getResources().getColor(btnColorRes));
        }

        DrawableCompat.setTintMode(kudoDrawable, PorterDuff.Mode.SRC_IN);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            kudoBtn.setImageDrawable(kudoDrawable);
        }
    }

    /**
     * Parses the HTML content of the message body and sets the content in the TextView with the images using {@link LiImageGetter}
     *
     * @param item         {@link LiMessage}
     * @param liViewHolder {@link LiViewHolderConversationMessage}
     */
    protected void setupMessageBody(LiMessage item, LiViewHolderConversationMessage liViewHolder) {
        liViewHolder.mPostBody.loadDataWithBaseURL(
                LiSDKManager.getInstance().getCredentials().getCommunityUri().toString(),
                String.format(htmlTemplate, item.getBody()), "text/html", "utf-8", null);
    }

    @Override
    public void onViewDetachedFromWindow(LiViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((LiViewHolderConversationMessage) holder).mPostBody.onPause();
    }

    @Override
    public void onViewAttachedToWindow(LiViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ((LiViewHolderConversationMessage) holder).mPostBody.onResume();
    }

    /**
     * Sets up the Author details for the message row
     *
     * @param item         {@link LiMessage}
     * @param liViewHolder {@link LiViewHolderConversationMessage}
     */
    protected void setupAuthorDetails(LiMessage item, LiViewHolderConversationMessage liViewHolder) {
        String avatarImageUrl = null;
        String loginName = null;
        if (item.getAuthor() != null) {
            if (item.getAuthor().getLogin() != null) {
                loginName = item.getAuthor().getLogin();
            }

            if (item.getAuthor() != null
                    && item.getAuthor().getAvatar() != null) {
                avatarImageUrl = item.getAuthor().getAvatar().getMessage();
            }
        }

        if (avatarImageUrl != null) {
            Picasso.with(activity)
                    .load(avatarImageUrl)
                    .error(avatarDefaultIcon)
                    .into(liViewHolder.mUserImage);
        }
        liViewHolder.mUserName.setText(loginName);
    }
}
