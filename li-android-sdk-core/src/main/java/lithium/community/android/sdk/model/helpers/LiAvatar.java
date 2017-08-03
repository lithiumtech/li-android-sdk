/*
 * LiAvatar.java
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

import org.json.JSONException;
import org.json.JSONObject;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;

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

    public static LiAvatar jsonDeserialize(JSONObject json) throws JSONException {
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
    public JSONObject jsonSerialize() {
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

}
