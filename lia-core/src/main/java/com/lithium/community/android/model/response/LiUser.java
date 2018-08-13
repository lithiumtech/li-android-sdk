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

package com.lithium.community.android.model.response;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.helpers.LiAvatar;
import com.lithium.community.android.model.helpers.LiImage;
import com.lithium.community.android.model.helpers.LiRanking;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A user model represents anonymous and registered community user. Its fields provide
 * information about registration, profile, subscriptions, ranks, roles, badges, and activity
 *
 * @link http://community.lithium.com/t5/Developer-Documentation/bd-p/dev-doc-portal?section=commv2&collection=users
 */
public class LiUser extends LiBaseModelImpl {

    private static final String USER_ID = "USER_ID";
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String USER_AVATAR = "USER_AVATAR";
    private static final String USER_LOGIN = "USER_LOGIN";
    private static final String USER_HREF = "USER_HREF";
    private static final String USER_VIEW_HREF = "USER_VIEW_HREF";
    private static final String USER_LAST_VISIT_TIME = "USER_LAST_VISIT_TIME";
    private static final String USER_STRING = "USER_STRING";
    private Boolean anonymous;

    @SerializedName("avatar-image")
    private LiImage avatarImage;

    @SerializedName("average_message_rating")
    private Float averageMessageRating;

    @SerializedName("average_rating")
    private Float averageRating;
    private Boolean banned;
    private Boolean deleted;
    private String email;

    @SerializedName("href")
    private String href;

    @SerializedName("last_visit_time")
    private LiDateInstant lastVisitInstant;

    @SerializedName("login")
    private String login;

    private Long id;

    @SerializedName("view_href")
    private String profilePageUrl;

    @SerializedName("rank")
    private LiRanking ranking;

    @SerializedName("registered")
    private Boolean registered;

    @SerializedName("registration_time")
    private LiDateInstant registrationInstant;

    @SerializedName("valid")
    private Boolean valid;

    @SerializedName("avatar")
    private LiAvatar avatar;

    public static LiUser deserialize(JSONObject jsonObject) throws JSONException {
        LiUser user = new LiUser();
        user.setId(jsonObject.getLong(USER_ID));
        user.setEmail(jsonObject.getString(USER_EMAIL));
        LiAvatar avatar = LiAvatar.deserialize(jsonObject.getJSONObject(USER_AVATAR));
        user.setAvatar(avatar);
        user.setLogin(jsonObject.getString(USER_LOGIN));
        user.setHref(jsonObject.getString(USER_HREF));
        user.setProfilePageUrl(jsonObject.getString(USER_VIEW_HREF));

        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "LiUser#deserialize() - " + user.toString());

        return user;
    }

    public LiAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(LiAvatar avatar) {
        this.avatar = avatar;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public LiRanking getRanking() {
        return ranking;
    }

    public void setRanking(LiRanking ranking) {
        this.ranking = ranking;
    }

    public LiBaseModelImpl.LiBoolean getBannedAsLithiumBoolean() {
        final LiBaseModelImpl.LiBoolean ret = new LiBaseModelImpl.LiBoolean();
        ret.setValue(getBanned());
        return ret;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(LiBaseModelImpl.LiBoolean banned) {
        this.banned = banned.getValue();
    }

    public LiImage getAvatarImage() {
        return avatarImage;
    }

    /* why does this class take Object?
     because if it does not take Object, then Jackson completely ignores the custom deserializer!
     */
    public void setAvatarImage(Object avatarImage) {
        this.avatarImage = (LiImage) avatarImage;
    }

    public String getAvatarImageUrl() {
        if (avatarImage != null) {
            return avatarImage.getUrl();
        } else {
            return null;
        }

    }

    public LiBaseModelImpl.LiBoolean getRegisteredAsLithiumBoolean() {
        final LiBaseModelImpl.LiBoolean ret = new LiBaseModelImpl.LiBoolean();
        ret.setValue(getRegistered());
        return ret;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(LiBaseModelImpl.LiBoolean registered) {
        this.registered = registered.getValue();
    }

    public LiBaseModelImpl.LiBoolean getDeletedAsLithiumBoolean() {
        final LiBaseModelImpl.LiBoolean ret = new LiBaseModelImpl.LiBoolean();
        ret.setValue(getDeleted());
        return ret;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(LiBaseModelImpl.LiBoolean deleted) {
        this.deleted = deleted.getValue();
    }

    public LiBaseModelImpl.LiBoolean getAnonymousAsLithiumBoolean() {
        final LiBaseModelImpl.LiBoolean ret = new LiBaseModelImpl.LiBoolean();
        ret.setValue(getAnonymous());
        return ret;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(LiBaseModelImpl.LiBoolean anonymous) {
        this.anonymous = anonymous.getValue();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LiDateInstant getLastVisitInstant() {
        return lastVisitInstant;
    }

    public void setLastVisitInstant(LiBaseModelImpl.LiDate lastVisitInstant) {
        this.lastVisitInstant = lastVisitInstant.getValue();
    }

    public LiBaseModelImpl.LiDate getLastVisitInstantAsLithiumInstant() {
        final LiBaseModelImpl.LiDate ret = new LiBaseModelImpl.LiDate();
        ret.setValue(getLastVisitInstant());
        return ret;
    }

    public LiDateInstant getRegistrationInstant() {
        return registrationInstant;
    }

    public void setRegistrationInstant(LiBaseModelImpl.LiDate registrationInstant) {
        this.registrationInstant = registrationInstant.getValue();
    }

    public LiBaseModelImpl.LiDate getRegistrationInstantAsLithiumInstant() {
        final LiBaseModelImpl.LiDate ret = new LiBaseModelImpl.LiDate();
        ret.setValue(getRegistrationInstant());
        return ret;
    }

    public Float getAverageMessageRating() {
        return averageMessageRating;
    }

    public void setAverageMessageRating(LiBaseModelImpl.LiFloat averageMessageRating) {
        this.averageMessageRating = averageMessageRating.getValue();
    }

    public LiBaseModelImpl.LiFloat getAverageMessageRatingAsLiFloat() {
        final LiBaseModelImpl.LiFloat ret = new LiBaseModelImpl.LiFloat();
        ret.setValue(getAverageMessageRating());
        return ret;
    }

    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(LiBaseModelImpl.LiFloat averageRating) {
        this.averageRating = averageRating.getValue();
    }

    public LiBaseModelImpl.LiFloat getAverageRatingAsLiFloat() {
        final LiBaseModelImpl.LiFloat ret = new LiBaseModelImpl.LiFloat();
        ret.setValue(getAverageRating());
        return ret;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getProfilePageUrl() {
        return profilePageUrl;
    }

    public void setProfilePageUrl(final String url) {
        profilePageUrl = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long numericId) {
        this.id = numericId;
    }

    /**
     * This is NOT the numeric id.  This method existed before I added the numeric id.
     *
     * @return
     */
    public String getLoginId() {

        if (href == null || href.isEmpty()) {
            //Logger we cannot handle null/empty author id

            if (login != null) {
                return login;
            } else {
                return null;
            }
        }

        String[] args = href.split("/");

        if (args.length == 4) {
            return href.split("/")[3];
        }

        if (login != null) {
            return login;
        } else {
            return null;
        }
    }

    /**
     * Produces a JSON string representation of the token response for persistent storage or
     * local transmission (e.g. between activities).
     */
    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        LiCoreSDKUtils.putIfNotNull(json, USER_ID, String.valueOf(this.id));
        LiCoreSDKUtils.put(json, USER_EMAIL, this.email);
        LiCoreSDKUtils.put(json, USER_AVATAR, this.avatar.serialize());
        LiCoreSDKUtils.put(json, USER_LOGIN, this.login);
        LiCoreSDKUtils.put(json, USER_HREF, this.href);
        if (!TextUtils.isEmpty(profilePageUrl)) {
            LiCoreSDKUtils.put(json, USER_VIEW_HREF, this.profilePageUrl);
        }
        return json;
    }

    @Override
    public String toString() {
        return "LiUser{email='" + login + "'}";
    }
}