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
import android.content.res.TypedArray;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.response.LiBrowse;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.fragments.LiBrowseFragment;
import com.lithium.community.android.ui.components.fragments.LiOnMessageRowClickListener;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link LiBrowse} and makes a call to the
 * specified {@link LiOnMessageRowClickListener}.
 */
public class LiBrowseAdapter extends LiBaseRecyclerAdapter {
//    private static final int VIEW_TYPE_FOLDER_UP = 0;
//    private static final int VIEW_TYPE_ITEM = 1;
//    private LiBrowseFragment.DepthChangeListener depthChangeListener;
//    private boolean displayAsDialog;
//    LiBrowseFragment liBrowseFragment;

    public LiBrowseAdapter(List<LiBaseModel> items, LiOnMessageRowClickListener listener,
            Activity activity, LiBrowseFragment.DepthChangeListener depthChangeListener,
            boolean displayAsDialog, LiBrowseFragment liBrowseFragment) {
        super(items, activity, listener);
//        this.liBrowseFragment = liBrowseFragment;
//        this.depthChangeListener = depthChangeListener;
//        this.displayAsDialog = displayAsDialog;
    }

    //    @Override
//    public int getItemViewType(int position) {
//        if (position == VIEW_TYPE_FOLDER_UP && displayAsDialog) {
//            return VIEW_TYPE_FOLDER_UP;
//        }
//        else {
//            return VIEW_TYPE_ITEM;
//        }
//    }
//
    @Override
    public LiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_browse_row_view, parent, false);
        return new LiBrowseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LiViewHolder holder, final int position) {
        LiBaseModel clientResponseModel = mValues.get(position);
        final LiBrowse liBrowse = (LiBrowse) clientResponseModel.getModel();
        LiBrowseViewHolder browseViewHolder = (LiBrowseViewHolder) holder;
//        if (position == VIEW_TYPE_FOLDER_UP && displayAsDialog) {
//            browseViewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    depthChangeListener.handleDepthChange(false);
//                }
//            });
//            browseViewHolder.mCategoryName.setText(activity.getString(R.string.li_one_level_up));
//            browseViewHolder.mBrowseArrow.setVisibility(View.GONE);
//            browseViewHolder.mCategorySubText.setVisibility(View.VISIBLE);
//        }
//        else {
        browseViewHolder.mCategoryName.setText(liBrowse.getTitle());
        browseViewHolder.mCategorySubText.setVisibility(View.GONE);
        TypedArray typedArrForBrowse = activity.getTheme().obtainStyledAttributes(
                new int[]{R.attr.li_theme_browseListCategoryIcon});
        if (liBrowse.getId().startsWith(LiSDKConstants.LI_BOARD_ID_PREFIX)) {
            typedArrForBrowse = activity.getTheme().obtainStyledAttributes(
                    new int[]{R.attr.li_theme_browseListBoardIcon});
        }
        int browseListBoardIcon = typedArrForBrowse.getResourceId(0, -1);
        browseViewHolder.mCategoryIcon.setImageResource(browseListBoardIcon);
        typedArrForBrowse.recycle();

        browseViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onMessageRowClick(liBrowse);
                }
            }
        });
//        }
    }

    private class LiBrowseViewHolder extends LiViewHolder {
        final View mView;
        final TextView mCategoryName;
        final TextView mCategorySubText;
        final ImageView mCategoryIcon;
        final ImageView mBrowseArrow;

        LiBrowseViewHolder(View view) {
            super(view);
            mView = view;
            mCategoryName = mView.findViewById(R.id.li_browse_category_name);
            mCategoryIcon = mView.findViewById(R.id.li_ask_question_list_header_img);
            mCategorySubText = mView.findViewById(R.id.li_browse_sub_text);
            mBrowseArrow = mView.findViewById(R.id.li_browseArrow);
        }
    }
}
