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

package com.lithium.community.android.sdk.model.helpers;

import com.lithium.community.android.sdk.model.LiBaseModelImpl;

import org.json.JSONException;
import org.json.JSONObject;

import com.lithium.community.android.sdk.utils.LiCoreSDKUtils;

/**
 * This data model represents profile of user.
 * Created by shoureya.kant on 11/18/16.
 */

public class LiAvatar extends LiBaseModelImpl {

    private static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";
    private static final String MESSAGE_IMAGE_URL = "MESSAGE_IMAGE_URL";
    private String profile;
    private String message;
    private String inline;
    private String favicon;
    private String print;
    private String url;
    private String image;
    private String internal;
    private String external;

    public static LiAvatar deserialize(JSONObject json) throws JSONException {
        LiAvatar avatar = new LiAvatar();
        avatar.setMessage(json.getString(MESSAGE_IMAGE_URL));
        avatar.setProfile(json.getString(PROFILE_IMAGE_URL));
        return avatar;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Produces a JSON string representation of the token response for persistent storage or
     * local transmission (e.g. between activities).
     */
    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        LiCoreSDKUtils.put(json, PROFILE_IMAGE_URL, String.valueOf(this.getProfile()));
        LiCoreSDKUtils.put(json, MESSAGE_IMAGE_URL, this.getMessage());
        return json;
    }

    public String getInline() {
        return inline;
    }

    public void setInline(String inline) {
        this.inline = inline;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getPrint() {
        return print;
    }

    public void setPrint(String print) {
        this.print = print;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        this.internal = internal;
    }

    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }
}
