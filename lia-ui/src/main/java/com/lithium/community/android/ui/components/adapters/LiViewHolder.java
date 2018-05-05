/*
 * LiViewHolder.java
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

package com.lithium.community.android.ui.components.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Common ViewHolder class for SDK
 */

public class LiViewHolder extends RecyclerView.ViewHolder {
    final private View mView;

    LiViewHolder(View view) {
        super(view);
        this.mView = view;
    }

    public View getView() {
        return mView;
    }
}
