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
 * A board is the parent of a conversation (a thread of topic messages and replies).
 *
 * @link http://community.lithium.com/t5/Developer-Documentation/bd-p/dev-doc-portal?section=commv2&collection=boards
 */
public class LiBoard extends LiBaseModelImpl {

    private Boolean blog;
    private String href;
    @SerializedName("view_href")
    private String viewHref;
    private String id;
    private String type;
    @SerializedName("interaction_style")
    private String interactionStyle;
    private String title;
    @SerializedName("short_title")
    private String shortTitle;
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getBlog() {
        return blog;
    }

    public void setBlog(LiBaseModelImpl.LiBoolean result) {
        blog = result.getValue();
    }

    public String getHref() {
        return href;
    }

    public String getView_href() {
        return viewHref;
    }

    public String getId() {
        return id;
    }

    public void setId(LiBaseModelImpl.LiString result) {
        this.id = result.getValue();
    }

    public String getInteractionStyle() {
        return interactionStyle;
    }

    public void setInteractionStyle(LiBaseModelImpl.LiString result) {
        this.interactionStyle = result.getValue();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(LiBaseModelImpl.LiString result) {
        title = result.getValue();
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(LiBaseModelImpl.LiString result) {
        shortTitle = result.getValue();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(LiBaseModelImpl.LiString result) {
        description = result.getValue() == null ? "N/A" : result.getValue();
    }

}
