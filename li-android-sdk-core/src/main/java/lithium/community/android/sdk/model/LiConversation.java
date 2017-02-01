/*
 * LiConversation.java
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

package lithium.community.android.sdk.model;

import com.google.gson.annotations.SerializedName;

/**
 * This data model represents conversation of user.
 * Created by sumit.pannalall on 11/15/16.
 */

public class LiConversation extends LiBaseModelImpl {
    private String type;
    private boolean solved;

    @SerializedName("last_post_time")
    private LiBaseModelImpl.LiDateInstant lastPostTime;

    public LiDateInstant getLastPostTime() {
        return lastPostTime;
    }

    public void setLastPostTime(LiDateInstant lastPostTime) {
        this.lastPostTime = lastPostTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
