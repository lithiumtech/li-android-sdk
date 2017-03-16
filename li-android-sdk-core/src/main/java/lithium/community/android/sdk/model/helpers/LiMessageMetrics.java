/*
 * LiMessageMetrics.java
 * Created on Dec 27, 2016
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

package lithium.community.android.sdk.model.helpers;

import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.model.LiBaseModelImpl;

/**
 * Metrics about the message.
 */
public class LiMessageMetrics extends LiBaseModelImpl {

    @SerializedName("views")
    private Long viewCount;

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

}
