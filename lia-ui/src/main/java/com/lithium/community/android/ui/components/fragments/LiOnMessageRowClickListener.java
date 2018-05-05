/*
 * LiOnArticleRowClickListener.java
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

package com.lithium.community.android.ui.components.fragments;

import lithium.community.android.sdk.model.LiBaseModel;

/**
 * An interface for adding click and long click behaviors to a particular message from the list of messages displayed.
 * Created by sumit.pannalall on 10/25/16.
 */
public interface LiOnMessageRowClickListener {
    /**
     * Normal Click handler method for the selected message
     *
     * @param item {@link LiBaseModel} for the selected message
     */
    void onMessageRowClick(LiBaseModel item);

    /**
     * Long Click handler method for the selected message
     *
     * @param item {@link LiBaseModel} for the selected message
     */
    void onMessageRowLongClick(LiBaseModel item);
}
