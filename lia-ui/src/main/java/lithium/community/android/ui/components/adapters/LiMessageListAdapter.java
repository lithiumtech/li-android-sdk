/*
 * LiArticleListAdapter.java
 * Created on Dec 21, 2016
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
package lithium.community.android.ui.components.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.model.response.LiTargetModel;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiPostClientResponse;
import lithium.community.android.sdk.ui.components.R;
import lithium.community.android.ui.components.activities.LiBrowseActivity;
import lithium.community.android.ui.components.activities.LiCreateMessageActivity;
import lithium.community.android.ui.components.fragments.LiOnMessageRowClickListener;
import lithium.community.android.ui.components.utils.LiSDKConstants;
import lithium.community.android.ui.components.utils.LiUIUtils;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

/**
 * {@link RecyclerView.Adapter} that can display a {@link LiMessage} and makes a call to the
 * specified {@link LiOnMessageRowClickListener}.
 * Extends {@link LiBaseRecyclerAdapter} to displays the article rows.
 * This adapter is used where ever we want to show common article row view.
 */
public class LiMessageListAdapter extends LiBaseRecyclerAdapter {

    private static final int TYPE_ASK_Q_HEADER = 0;
    private static final int TYPE_BROWSE_HEADER = 1;
    private static final int TYPE_ITEM = 2;
    private boolean applyHeaders;
    private int longPressedPosition;

    public LiMessageListAdapter(List<LiBaseModel> items, LiOnMessageRowClickListener listener,
            boolean applyHeaders, Activity activity) {
        super(items, activity, listener);
        this.applyHeaders = applyHeaders;
    }

    public int getLongPressedPosition() {
        return longPressedPosition;
    }

    public void setLongPressedPosition(int longPressedPosition) {
        this.longPressedPosition = longPressedPosition;
    }

    @Override
    public LiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //based upon the view type inflate row view. If we want to show the headers then inflate appropriate view holder.
        if (viewType == TYPE_ASK_Q_HEADER && applyHeaders) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.li_list_header_ask_question, parent, false);
            return new LiViewHolder(view);
        } else if (viewType == TYPE_BROWSE_HEADER && applyHeaders) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.li_list_header_browse_all, parent, false);
            return new LiViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.li_fragment_message_list_row, parent, false);
            return new LiViewHolderItem(view);
        }
    }


    @Override
    public void onBindViewHolder(final LiViewHolder holder, final int position) {
        //configure each row based upon the row position
        if (position == TYPE_ASK_Q_HEADER && applyHeaders) {
            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, LiCreateMessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, true);
                    i.putExtras(bundle);
                    activity.startActivity(i);
                }
            });
        } else if (position == TYPE_BROWSE_HEADER && applyHeaders) {
            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, LiBrowseActivity.class);
                    activity.startActivity(i, new Bundle());
                }
            });
        } else {
            //extract LiMessage object which would used to popuate row data.
            LiBaseModel clientResponseModel = getItem(position);
            if (clientResponseModel == null || clientResponseModel.getModel() == null) {
                return;
            }
            final LiMessage item = ((LiTargetModel)
                    clientResponseModel.getModel()).getLiMessage();
            final LiViewHolderItem viewHolderNormal = (LiViewHolderItem) holder;

            //set the subject in the row.
            viewHolderNormal.mSubjectView.setText(item.getSubject());

            setupKudoCount(item, viewHolderNormal);

            setRelativeTimeStamp(item, viewHolderNormal);

            setPinnedUI(item, viewHolderNormal);

            setupResolvedRowStatus(item, viewHolderNormal);

            if (item.getUnreadReplyCount() > 0) {
                viewHolderNormal.mLiArticleNewReplyCount.setVisibility(View.VISIBLE);
                String countText = activity.getString(R.string.li_article_list_new_reply_count);
                if (item.getUnreadReplyCount() > 1) {
                    countText = activity.getString(R.string.li_article_list_new_reply_count_plural);
                }
                viewHolderNormal.mLiArticleNewReplyCount.
                        setText(String.format(countText, String.valueOf(item.getUnreadReplyCount())));
            } else {
                viewHolderNormal.mLiArticleNewReplyCount.setVisibility(View.GONE);
            }

            //add click listener for the article row
            viewHolderNormal.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onMessageRowClick(item);
                    }
                }
            });
//            viewHolderNormal.mliMessageListRowActionBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showPopupMenu(viewHolderNormal.mliMessageListRowActionBtn, position);
//                }
//            });
        }
    }

    /**
     * This method populates the actions that can be performed on a particular message in the conversation
     *
     * @param view     The view which was clicked
     * @param position position tells which model to pick from the list.
     */
    private void showPopupMenu(View view, final int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.li_message_row_action_menu, popup.getMenu());
        LiBaseModel clientResponseModel = mValues.get(position);
        final LiMessage item = ((LiTargetModel) clientResponseModel).getLiMessage();
        LiUIUtils.updateMarkReadPopmenuTitle(popup, item);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.li_action_mark_read) {
                    if (item.getUserContext() != null) {
                        boolean isMarkUnread = false;
                        if (item.getUserContext() != null && item.getUserContext().getRead()) {
                            isMarkUnread = true;
                        }

                        LiClientRequestParams.LiMarkTopicParams params =
                                new LiClientRequestParams.LiMarkTopicParams(activity,
                                        String.valueOf(item.getAuthor().getId()),
                                        String.valueOf(item.getId()), isMarkUnread);
                        try {
                            LiClientManager.getMarkTopicPostClient(params).processAsync(
                                    new LiAsyncRequestCallback<LiPostClientResponse>() {
                                        @Override
                                        public void onSuccess(LiBaseRestRequest request,
                                                LiPostClientResponse response)
                                                throws LiRestResponseException {
                                            if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                                                item.getUserContext().setRead(!item.getUserContext().getRead());
                                                LiUIUtils.showMarkReadSuccessful(activity, item);
                                            } else {
                                                LiUIUtils.showMarkReadUnSuccessful(activity, item);
                                            }
                                        }

                                        @Override
                                        public void onError(Exception exception) {
                                            LiUIUtils.showMarkReadUnSuccessful(activity, item);
                                        }
                                    });
                        } catch (LiRestResponseException e) {
                            Log.d(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
                        }

                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }


    /**
     * This method updates the status of a message row as "Resolved" or not.
     *
     * @param item             LiMessage row
     * @param viewHolderNormal The view where the status needs to updated
     */
    protected void setupResolvedRowStatus(LiMessage item, LiViewHolderItem viewHolderNormal) {
        if (item.getLiConversation() != null && item.getLiConversation().isSolved()) {
            viewHolderNormal.mArticleStatusSolvedText.setText(
                    activity.getString(R.string.li_article_status_resolved));
            viewHolderNormal.mArticleStatusSolvedImg.setImageResource(R.drawable.ic_done_black);
            viewHolderNormal.mArticleSatusSolvedSeparator.setVisibility(View.VISIBLE);
            viewHolderNormal.mArticleStatusSolvedText.setVisibility(View.VISIBLE);
            viewHolderNormal.mArticleStatusSolvedImg.setVisibility(View.VISIBLE);
        } else {
            viewHolderNormal.mArticleStatusSolvedText.setVisibility(View.GONE);
            viewHolderNormal.mArticleStatusSolvedImg.setVisibility(View.GONE);
            viewHolderNormal.mArticleSatusSolvedSeparator.setVisibility(View.GONE);
        }
    }

    /**
     * This method updates the timestamp into relative timestamp from current time.
     *
     * @param item             LiMessage row
     * @param viewHolderNormal The view where the status needs to updated
     */
    protected void setRelativeTimeStamp(LiMessage item, LiViewHolderItem viewHolderNormal) {
        if (item.getLiConversation() != null && item.getLiConversation().getLastPostTime() != null) {
            viewHolderNormal.mPostTime.setText(
                    LiUIUtils.toDuration(activity, item.getLiConversation().getLastPostTime().getValue()));
        }
    }

    /**
     * This method updates the kudo count
     *
     * @param item             LiMessage row
     * @param viewHolderNormal The view where the status needs to updated
     */
    protected void setupKudoCount(LiMessage item, LiViewHolderItem viewHolderNormal) {
        Long kudoCount = 0L;
        if (item.getKudoMetrics() != null) {
            kudoCount = item.getKudoMetrics().getSum().getWeight().getValue();
        }
        String kudoCountTxt = String.format(activity
                .getString(R.string.li_kudos_plural_txt), kudoCount);

        viewHolderNormal.mKudosView.setText(kudoCountTxt);
    }

    /**
     * This method updates the row if the message is a floating message on the board
     *
     * @param item             LiMessage row
     * @param viewHolderNormal The view where the status needs to updated
     */
    protected void setPinnedUI(LiMessage item, LiViewHolderItem viewHolderNormal) {
        if (item.isFloating() != null && item.isFloating()) {
            viewHolderNormal.mArticleStatusPinnedImg.setVisibility(View.VISIBLE);
            viewHolderNormal.mArticleStatusPinnedText.setVisibility(View.VISIBLE);
            viewHolderNormal.mArticleSatusPinnedSeparator.setVisibility(View.VISIBLE);
            viewHolderNormal.mArticleStatusPinnedText.setText(
                    activity.getString(R.string.li_article_status_pinned));
            TypedArray a = activity.getTheme().obtainStyledAttributes(
                    new int[]{R.attr.li_theme_articlePinnedIcon});
            int id = a.getResourceId(0, -1);
            viewHolderNormal.mArticleStatusPinnedImg.setImageResource(id);
            a.recycle();
        } else {
            viewHolderNormal.mArticleStatusPinnedImg.setVisibility(View.GONE);
            viewHolderNormal.mArticleStatusPinnedText.setVisibility(View.GONE);
            viewHolderNormal.mArticleSatusPinnedSeparator.setVisibility(View.GONE);
        }
    }

    public LiBaseModel getItem(int position) {
        // if headers are present then the size of the items will be +2
        // hence while retrieving the actual item need to subtract 2 to get the original position
//        if (applyHeaders) {
//            return mValues.get(position-2);
//        }
//        else {
        return mValues.get(position);
//        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_ASK_Q_HEADER) {
            return TYPE_ASK_Q_HEADER;
        } else if (position == TYPE_BROWSE_HEADER) {
            return TYPE_BROWSE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

//    @Override
//    public int getItemCount() {
//        int itemSize = mValues.size();
//        if (applyHeaders) {
//            return itemSize+2;
//        }
//        else {
//            return itemSize;
//        }
//    }

    private class LiViewHolderItem extends LiViewHolder {
        final View mView;
        final TextView mSubjectView;
        final TextView mKudosView;
        final TextView mPostTime;
        final ImageView mArticleStatusPinnedImg;
        final TextView mArticleStatusPinnedText;
        final ImageView mArticleStatusSolvedImg;
        final TextView mArticleStatusSolvedText;
        final TextView mArticleSatusPinnedSeparator;
        final TextView mArticleSatusSolvedSeparator;
        final TextView mLiArticleNewReplyCount;
//        final ImageView mliMessageListRowActionBtn;

        LiViewHolderItem(View view) {
            super(view);
            mView = view;
            mSubjectView = view.findViewById(R.id.li_subject);
            mKudosView = view.findViewById(R.id.li_kudos_count);
            mPostTime = view.findViewById(R.id.li_article_post_time);
            mArticleStatusPinnedImg = view.findViewById(R.id.li_article_status_pinned_img);
            mArticleStatusPinnedText = view.findViewById(R.id.li_article_status_pinned_txt);
            mArticleStatusSolvedImg = view.findViewById(R.id.li_article_status_solved_img);
            mArticleStatusSolvedText = view.findViewById(R.id.li_article_status_solved_txt);
            mArticleSatusPinnedSeparator = view.findViewById(R.id.li_article_status_pinned_separator);
            mArticleSatusSolvedSeparator = view.findViewById(R.id.li_article_status_solved_separator);
            mLiArticleNewReplyCount = view.findViewById(R.id.li_article_new_reply_count);
//            mliMessageListRowActionBtn = (ImageView) view.findViewById(R.id.li_message_list_row_action_btn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mSubjectView.getText() + "'";
        }
    }
}
