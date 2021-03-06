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

package com.lithium.community.android.model.helpers;

import com.google.gson.annotations.SerializedName;
import com.lithium.community.android.model.LiBaseModelImpl;

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

    public void setHeight(Long result) {
        this.height = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String result) {
        url = result;
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

    public void setWidth(Long result) {
        this.width = result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LiImageMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(LiImageMetaData metaData) {
        this.metaData = metaData;
    }
}
