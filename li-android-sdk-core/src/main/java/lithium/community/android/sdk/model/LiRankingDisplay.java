/*
 * LiRankingDisplay.java
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
 * This model represents how a user's rank appears in the community.
 * http://community.lithium.com/t5/Developer-Documentation/bd-p/dev-doc-portal?section=commv2&collection=ranks
 */
public class LiRankingDisplay extends LiBaseModelImpl {

    private Boolean bold;
    private String color;

    @SerializedName("left_image")
    private LiImage leftImage;

    @SerializedName("right_image")
    private LiImage rightImage;

    @SerializedName("thread_image")
    private LiImage threadImage;


    public LiImage getThreadImage() {
        return threadImage;
    }

    @SerializedName("thread_image")
    public void setThreadImage(LiImage threadImage) {
        this.threadImage = threadImage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(LiBaseModelImpl.LiString color) {
        this.color = color.getValue();
    }

    public LiBaseModelImpl.LiString getColorAsLithiumString() {
        final LiBaseModelImpl.LiString ret = new LiBaseModelImpl.LiString();
        ret.setValue(getColor());
        return ret;
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(LiBaseModelImpl.LiBoolean bold) {
        this.bold = bold.getValue();
    }

    public LiBaseModelImpl.LiBoolean getBoldAsLithiumBoolean() {
        final LiBaseModelImpl.LiBoolean ret = new LiBaseModelImpl.LiBoolean();
        ret.setValue(getBold());
        return ret;
    }

    public LiImage getLeftImage() {
        return leftImage;
    }

    public void setLeftImage(LiImage leftImage) {
        this.leftImage = leftImage;
    }


    public LiImage getRightImage() {
        return rightImage;
    }

    public void setRightImage(LiImage rightImage) {
        this.rightImage = rightImage;
    }
}
