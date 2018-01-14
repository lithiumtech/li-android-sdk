/*
 * LiUser.java
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

package lithium.community.android.sdk.model.response;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.helpers.LiAvatar;
import lithium.community.android.sdk.model.helpers.LiImage;
import lithium.community.android.sdk.model.helpers.LiRanking;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;

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

    private LiBaseModelImpl.LiString login;

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

    public static LiUser jsonDeserialize(JSONObject jsonObject) throws JSONException {
        LiUser user = new LiUser();
        user.setId(jsonObject.getLong(USER_ID));
        LiString emailStr = new LiString();
        emailStr.setValue(jsonObject.getString(USER_EMAIL));
        user.setEmail(emailStr);
        LiAvatar avatar = LiAvatar.jsonDeserialize(jsonObject.getJSONObject(USER_AVATAR));
        user.setAvatar(avatar);
        LiString loginStr = new LiString();
        loginStr.setValue(jsonObject.getString(USER_LOGIN));
        user.setLogin(loginStr);
        user.setHref(jsonObject.getString(USER_HREF));
        user.setProfilePageUrl(jsonObject.getString(USER_VIEW_HREF));
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

    public LiBaseModelImpl.LiString getAnonymousAsLiString() {
        final LiBaseModelImpl.LiString ret = new LiBaseModelImpl.LiString();
        ret.setValue(getEmail());
        return ret;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(LiBaseModelImpl.LiString email) {
        this.email = email.getValue();
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

    public void setProfilePageUrl(final String view_href) {
        profilePageUrl = view_href;
    }

    public LiBaseModelImpl.LiString getLoginAsLiString() {
        return login;
    }

    public String getLogin() {
        return login.getValue();
    }

    public void setLogin(LiBaseModelImpl.LiString login) {
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long numericId) {
        this.id = numericId;
    }

    public LiBaseModelImpl.LiInt getNumericIdAsLiInt() {
        final LiBaseModelImpl.LiInt ret = new LiBaseModelImpl.LiInt();
        ret.setValue(getId());
        return ret;
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
                return login.getValue();
            } else {
                return null;
            }
        }

        String[] args = href.split("/");

        if (args.length == 4) {
            return href.split("/")[3];
        }

        if (login != null) {
            return login.getValue();
        } else {
            return null;
        }
    }

    /**
     * Produces a JSON string representation of the token response for persistent storage or
     * local transmission (e.g. between activities).
     */
    public JSONObject jsonSerialize() {
        JSONObject json = new JSONObject();
        LiCoreSDKUtils.putIfNotNull(json, USER_ID, String.valueOf(this.id));
        LiCoreSDKUtils.put(json, USER_EMAIL, this.email);
        LiCoreSDKUtils.put(json, USER_AVATAR, this.avatar.jsonSerialize());
        LiCoreSDKUtils.put(json, USER_LOGIN, this.login.getValue());
        LiCoreSDKUtils.put(json, USER_HREF, this.href);
        if (!TextUtils.isEmpty(profilePageUrl)) {
            LiCoreSDKUtils.put(json, USER_VIEW_HREF, this.profilePageUrl);
        }
        return json;
    }
}