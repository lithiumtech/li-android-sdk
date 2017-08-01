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
    private String href;
    private String name;
    private Long id;
    private String type;
    private int position;
    private String color;
    private String icon_left;
    private String icon_right;
    private String formula;
    private String icon_topic;
    private boolean formula_enabled;
    private boolean bold;

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public String getIcon_topic() {
        return icon_topic;
    }

    public void setIcon_topic(String icon_topic) {
        this.icon_topic = icon_topic;
    }

    public String getIcon_right() {
        return icon_right;
    }

    public void setIcon_right(String icon_right) {
        this.icon_right = icon_right;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean isFormula_enabled() {
        return formula_enabled;
    }

    public void setFormula_enabled(boolean formula_enabled) {
        this.formula_enabled = formula_enabled;
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
        return icon_left;
    }

    public void setIcon_left(String icon_left) {
        this.icon_left = icon_left;
    }

    @SerializedName("display")
    private LiRankingDisplay lithiumRankingDisplay;

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
