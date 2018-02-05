/*
 * Copyright 2018 Lithium Technologies Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lithium.community.android.sdk.model.helpers;

import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.model.LiBaseModelImpl;

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
