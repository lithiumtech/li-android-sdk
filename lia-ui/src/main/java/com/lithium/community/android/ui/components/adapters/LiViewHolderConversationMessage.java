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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lithium.community.android.sdk.ui.components.R;
import com.lithium.community.android.ui.components.custom.ui.LiRoundedImageView;

/**
 * Common ViewHolder class for SDK
 */

class LiViewHolderConversationMessage extends LiViewHolder {
    final View mView;
    final LiRoundedImageView mUserImage;
    final TextView mUserName;
    final TextView mPostTimeAgo;
    final WebView mPostBody;
    final TextView mPostKudoCount;
    final TextView mPostAcceptTxt;
    final ImageView mPosKudoBtn;
    final ProgressBar mPosKudoProgressBar;
    final ProgressBar mPosAcceptProgressBar;
    final ImageView mPostReplyBtn;
    final TextView mPostReplyTxt;
    final ImageView mPostAcceptBtn;
    final TextView mPostStatusHeader;
    final TextView mPostSubjectTxt;
    final RelativeLayout mLiPostActionsContainer;
    final ImageView mLiConversationActionsButton;

    LiViewHolderConversationMessage(View view, final Activity activity) {
        super(view);
        mView = view;
        mUserImage = mView.findViewById(R.id.li_user_image);
        mUserName = mView.findViewById(R.id.li_user_name);
        mPostTimeAgo = mView.findViewById(R.id.li_post_time_ago);
        mPostBody = mView.findViewById(R.id.li_post_body);
        mPostBody.setFocusable(true);
        mLiPostActionsContainer = mView.findViewById(R.id.li_post_actions_container);
        mLiConversationActionsButton = mView.findViewById(R.id.li_conversation_actions_button);
        mPostBody.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebView.HitTestResult result = view.getHitTestResult();
                int type = result.getType();
                switch (type) {
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE:
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        break;
                    case WebView.HitTestResult.EDIT_TEXT_TYPE:
                        break;
                    case WebView.HitTestResult.GEO_TYPE:
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE:
                        break;
                    case WebView.HitTestResult.PHONE_TYPE:
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        break;
                    case WebView.HitTestResult.UNKNOWN_TYPE:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        mPostBody.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                    boolean isUserGesture, Message resultMsg) {
                WebView.HitTestResult result = view.getHitTestResult();
                int type = result.getType();
                String data = result.getExtra();
                switch (type) {
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data)));
                        break;
                    case WebView.HitTestResult.EMAIL_TYPE:
                        break;
                    case WebView.HitTestResult.EDIT_TEXT_TYPE:
                        break;
                    case WebView.HitTestResult.GEO_TYPE:
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE:
                        break;
                    case WebView.HitTestResult.PHONE_TYPE:
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data)));
                        break;
                    case WebView.HitTestResult.UNKNOWN_TYPE:
                        break;
                    default:
                        break;
                }
                return super.onCreateWindow(view, isDialog, isUserGesture,
                        resultMsg);
            }
        });
        WebSettings settings = mPostBody.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mPostBody.setFocusableInTouchMode(true);
        mPostBody.getSettings().setJavaScriptEnabled(true);
        mPostBody.getSettings().setSupportMultipleWindows(true);
        mPostBody.setBackgroundColor(0);
        mPostKudoCount = mView.findViewById(R.id.li_post_kudo_count);
        mPostAcceptTxt = mView.findViewById(R.id.li_post_accept_txt);
        mPosKudoBtn = mView.findViewById(R.id.li_post_kudo_btn);
        mPostReplyBtn = mView.findViewById(R.id.li_post_reply_btn);
        mPostReplyTxt = mView.findViewById(R.id.li_post_reply_txt);
        mPostAcceptBtn = mView.findViewById(R.id.li_post_accept_btn);
        mPostStatusHeader = mView.findViewById(R.id.li_post_status_header);
        mPostSubjectTxt = mView.findViewById(R.id.li_post_title_txt);
        mPosKudoProgressBar = mView.findViewById(R.id.li_kudo_action_progress);
        mPosAcceptProgressBar = mView.findViewById(R.id.li_accept_action_progress);
    }
}