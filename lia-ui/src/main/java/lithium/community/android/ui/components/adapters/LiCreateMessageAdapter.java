/*
 * LiAskQuestionAdapter.java
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
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.ui.components.R;
import lithium.community.android.ui.components.custom.ui.LiSoftKeyboardLsnrLinearLayout;
import lithium.community.android.ui.components.fragments.LiCreateMessageFragment;
import lithium.community.android.ui.components.utils.LiUIUtils;


/**
 * This adapter extends {@link RecyclerView.Adapter} that can display a form using which user can post a question on reply to a message
 */
public class LiCreateMessageAdapter extends RecyclerView.Adapter<LiViewHolder> {
    private static final int VIEW_TYPE_INLINE_REPLY = 0;
    private static final int VIEW_TYPE_AUTHORING = 1;
    private boolean canSelectABoard;
    private LiMessage liMessage;
    private int avatarDefaultIcon;
    private LiCreateMessageFragment liCreateMessageFragment;
    private Activity activity;
    private String htmlTemplate;

    public LiCreateMessageAdapter(Activity activity, boolean canSelectABoard,
            LiCreateMessageFragment liCreateMessageFragment) {
        this.canSelectABoard = canSelectABoard;
        this.liCreateMessageFragment = liCreateMessageFragment;
        this.activity = activity;
        TypedArray typedArrForBrowse = activity.getTheme().obtainStyledAttributes(
                new int[]{R.attr.li_theme_messageUserAvatarIcon});
        avatarDefaultIcon = typedArrForBrowse.getResourceId(0, -1);
        typedArrForBrowse.recycle();
        htmlTemplate = LiUIUtils.getRawString(activity, R.raw.message_template);
        this.liMessage = liCreateMessageFragment.selectedMessage;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == VIEW_TYPE_INLINE_REPLY) {
            return VIEW_TYPE_INLINE_REPLY;
        } else {
            return VIEW_TYPE_AUTHORING;
        }
    }

    @Override
    public LiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_INLINE_REPLY && !canSelectABoard) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.li_conversation_row_view, parent, false);
            return new LiViewHolderConversationMessage(view, activity);
        } else {
            return new LiAuthoringViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.li_authoring_row, parent, false));
        }
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
            if (item.getAuthor().getLoginAsLiString() != null) {
                loginName = item.getAuthor().getLoginAsLiString().getValue();
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

    @Override
    public void onBindViewHolder(LiViewHolder holder, int position) {
        if (position == VIEW_TYPE_INLINE_REPLY && !canSelectABoard) {
            LiViewHolderConversationMessage liViewHolderConversationMessage = (LiViewHolderConversationMessage) holder;
            setupAuthorDetails(liMessage, liViewHolderConversationMessage);
            liViewHolderConversationMessage.mPostTimeAgo.setText(
                    LiUIUtils.toDuration(activity, liMessage.getPostTime().getValue()));
            liViewHolderConversationMessage.mPostBody.loadDataWithBaseURL(
                    LiSDKManager.getInstance().getCredentials().getCommunityUri().toString(),
                    String.format(htmlTemplate, liMessage.getBody()), "text/html", "utf-8", null);
            liViewHolderConversationMessage.mLiPostActionsContainer.setVisibility(View.GONE);
            liViewHolderConversationMessage.mLiConversationActionsButton.setVisibility(View.INVISIBLE);
            liViewHolderConversationMessage.mPostSubjectTxt.setVisibility(View.GONE);
            liViewHolderConversationMessage.mPostStatusHeader.setVisibility(View.GONE);
        } else {
            final LiAuthoringViewHolder liAuthoringViewHolder = (LiAuthoringViewHolder) holder;
            liAuthoringViewHolder.askQuestionSubject.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE
            );
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            if (canSelectABoard) {
                liAuthoringViewHolder.inReplyToContainer.setVisibility(View.GONE);
                liAuthoringViewHolder.liAskQuestionSubjectContainer.setVisibility(View.VISIBLE);
                liAuthoringViewHolder.askQuestionSubject.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //since the actual post form resides in the fragment, set the subject text back to fragment
                        liCreateMessageFragment.setAskQuestionSubject(String.valueOf(charSequence));
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            } else {
                liAuthoringViewHolder.inReplyToContainer.setVisibility(View.VISIBLE);
                String loginName = null;
                if (liMessage.getAuthor() != null) {
                    if (liMessage.getAuthor().getLoginAsLiString() != null) {
                        loginName = liMessage.getAuthor().getLoginAsLiString().getValue();
                    }
                }
                String inReplyTo = String.format(activity.getString(R.string.li_in_reply_to_text), loginName);
                liAuthoringViewHolder.inReplyToText.setText(inReplyTo);
                liAuthoringViewHolder.liAskQuestionSubjectContainer.setVisibility(View.GONE);
                liAuthoringViewHolder.inReplyToContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        liCreateMessageFragment.focusInlineReply();
                    }
                });
            }

//            liAuthoringViewHolder.mView.addSoftKeyboardLsner(new LiSoftKeyboardLsnrLinearLayout.SoftKeyboardLsner() {
//                @Override
//                public void onSoftKeyboardShow() {}
//
//                @Override
//                public void onSoftKeyboardHide() {
//                    liCreateMessageFragment.focusAuthoring(liAuthoringViewHolder.getView());
//                }
//            });

            liAuthoringViewHolder.askQuestionBody.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //since the actual post form resides in the fragment, set the subject text back to fragment
                    liCreateMessageFragment.setAskQuestionBody(String.valueOf(charSequence));
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            liAuthoringViewHolder.askQuestionBody.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (v.getId() == R.id.li_ask_question_body) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_UP:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (canSelectABoard) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * ViewHolder for authoring layout
     */
    private class LiAuthoringViewHolder extends LiViewHolder {
        final LiSoftKeyboardLsnrLinearLayout mView;
        final EditText askQuestionBody;
        final EditText askQuestionSubject;
        final TextView inReplyToText;
        final RelativeLayout inReplyToContainer;
        final LinearLayout liAskQuestionSubjectContainer;

        LiAuthoringViewHolder(View view) {
            super(view);
            mView = (LiSoftKeyboardLsnrLinearLayout) view;
            askQuestionBody = mView.findViewById(R.id.li_ask_question_body);
            askQuestionSubject = mView.findViewById(R.id.li_ask_question_subject);
            inReplyToText = mView.findViewById(R.id.in_reply_to_text);
            inReplyToContainer = mView.findViewById(R.id.in_reply_to_container);
            liAskQuestionSubjectContainer = mView.findViewById(R.id.li_ask_question_subject_container);
        }
    }
}
