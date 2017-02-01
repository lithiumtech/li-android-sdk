/*
 * LiImage.java
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
 * A media image asset
 *
 * @link http://community.lithium.com/t5/Developer-Documentation/bd-p/dev-doc-portal?section=commv2&collection=images
 */
public class LiImage extends LiBaseModelImpl {

    public String href;
    private Long height;
    private String url;

    @SerializedName("view_href")
    private String viewHref;
    private Long width;
    private String title;
    private String description;

    @SerializedName("content")
    private LiImageMetaData metaData;

    public Long getHeight() {
        return height;
    }

    public void setHeight(LiBaseModelImpl.LiInteger result) {
        this.height = result.getValue();
    }

    public LiBaseModelImpl.LiInteger getHeightAsLiInteger() {
        final LiBaseModelImpl.LiInteger ret = new LiBaseModelImpl.LiInteger();
        ret.setValue(getHeight());
        return ret;
    }

    public LiBaseModelImpl.LiInteger getWidthAsLiInteger() {
        final LiBaseModelImpl.LiInteger ret = new LiBaseModelImpl.LiInteger();
        ret.setValue(getWidth());
        return ret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(LiBaseModelImpl.LiString result) {
        url = result.getValue();
    }

    public LiBaseModelImpl.LiString getUrlAsLiString() {
        final LiBaseModelImpl.LiString ret = new LiBaseModelImpl.LiString();
        ret.setValue(getUrl());
        return ret;
    }

    public String getViewHref() {
        return viewHref;
    }

    public void setViewHref(String viewHref) {
        this.viewHref = viewHref;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(LiBaseModelImpl.LiInteger result) {
        this.width = result.getValue();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(LiBaseModelImpl.LiString title) {
        this.title = title.getValue();
    }

    public LiBaseModelImpl.LiString getTitleAsLiString() {
        final LiBaseModelImpl.LiString ret = new LiBaseModelImpl.LiString();
        ret.setValue(getTitle());
        return ret;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(LiBaseModelImpl.LiString description) {
        this.description = description.getValue();
    }

    public LiBaseModelImpl.LiString getDescriptionAsLiString() {
        final LiBaseModelImpl.LiString ret = new LiBaseModelImpl.LiString();
        ret.setValue(getDescription());
        return ret;
    }

    public LiImageMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(LiImageMetaData metaData) {
        this.metaData = metaData;
    }
}
