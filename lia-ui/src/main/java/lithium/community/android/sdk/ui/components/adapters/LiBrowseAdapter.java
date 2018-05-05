/*
 * LiBrowseAdapter.java
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

package lithium.community.android.sdk.ui.components.adapters;

import android.app.Activity;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.response.LiBrowse;
import lithium.community.android.sdk.ui.components.R;
import lithium.community.android.sdk.ui.components.fragments.LiBrowseFragment;
import lithium.community.android.sdk.ui.components.fragments.LiOnMessageRowClickListener;
import lithium.community.android.sdk.ui.components.utils.LiSDKConstants;

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
