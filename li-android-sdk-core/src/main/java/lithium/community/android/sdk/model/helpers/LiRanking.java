/*
 * LiRanking.java
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
 * A rank model represents a way to recognize and reward community members' achievements.
 */
public class LiRanking extends LiBaseModelImpl {

    private Long id;
    private String type;
    private String name;
    private int position;
    private String color;
    private String href;

    @SerializedName("icon_left")
    private String iconLeft;

    @SerializedName("icon_right")
    private String iconRight;

    private String formula;

    @SerializedName("icon_topic")
    private String iconTopic;

    @SerializedName("formula_enabled")
    private boolean formulaEnabled;

    private boolean bold;

    @SerializedName("display")
    private LiRankingDisplay lithiumRankingDisplay;

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public String getIcon_topic() {
        return iconTopic;
    }

    public void setIcon_topic(String iconTopic) {
        this.iconTopic = iconTopic;
    }

    public String getIcon_right() {
        return iconRight;
    }

    public void setIcon_right(String iconRight) {
        this.iconRight = iconRight;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean isFormula_enabled() {
        return formulaEnabled;
    }

    public void setFormula_enabled(boolean formulaEnabled) {
        this.formulaEnabled = formulaEnabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon_left() {
        return iconLeft;
    }

    public void setIcon_left(String iconLeft) {
        this.iconLeft = iconLeft;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public LiBaseModelImpl.LiString getNameAsLithiumString() {
        final LiBaseModelImpl.LiString ret = new LiBaseModelImpl.LiString();
        ret.setValue(getName());
        return ret;
    }

    public String getName() {
        return name;
    }

    public void setName(LiBaseModelImpl.LiString name) {
        this.name = name.getValue();
    }

    public LiBaseModelImpl.LiInt getIdAsLithiumInt() {
        final LiBaseModelImpl.LiInt ret = new LiBaseModelImpl.LiInt();
        ret.setValue(getId());
        return ret;
    }

    public Long getId() {
        return id;
    }

    public void setId(LiBaseModelImpl.LiInt id) {
        this.id = id.getValue();
    }

    public LiRankingDisplay getLithiumRankingDisplay() {
        return lithiumRankingDisplay;
    }

    public void setLithiumRankingDisplay(LiRankingDisplay lithiumRankingDisplay) {
        this.lithiumRankingDisplay = lithiumRankingDisplay;
    }
}
