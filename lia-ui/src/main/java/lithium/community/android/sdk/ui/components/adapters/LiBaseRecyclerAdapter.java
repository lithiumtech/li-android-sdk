/*
 * LiBaseRecyclerAdapter.java
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
import android.support.v7.widget.RecyclerView;

import java.util.List;

import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.ui.components.fragments.LiOnMessageRowClickListener;

/**
 * {@link RecyclerView.Adapter} that is common to sdk fragments
 * This adapter is used where ever we want to show a list of items
 */
public abstract class LiBaseRecyclerAdapter extends RecyclerView.Adapter<LiViewHolder> {

    protected final Activity activity;
    protected final LiOnMessageRowClickListener mListener;
    protected List<LiBaseModel> mValues;

    public LiBaseRecyclerAdapter(List<LiBaseModel> items, Activity activity, LiOnMessageRowClickListener listener) {
        mValues = items;
        this.activity = activity;
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * This message returns the list of items that is currently displayed on the screen
     *
     * @return List of {@link LiBaseModel}
     */
    public List<LiBaseModel> getItems() {
        return mValues;
    }


    /**
     * This message sets the new list of items that will be displayed on the screen.
     */
    public void setItems(List<LiBaseModel> mValues) {
        this.mValues = mValues;
    }

}
