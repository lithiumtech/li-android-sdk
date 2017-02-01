/*
 * LiTokenResponse.java
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

package lithium.community.android.sdk.auth;

import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.utils.LiSystemClock;

/**
 * Response class for LiSSOTokenRequest.
 * Created by saiteja.tokala on 12/2/16.
 */

public class LiTokenResponse {

    @SerializedName("lithiumUserId")
    private String lithiumUserId;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("expires_in")
    private Long expiresIn;
    @SerializedName("userId")
    private String userId;
    @SerializedName("token_type")
    private String tokenType;

    private String jsonString;

    private Long expiresAt;

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getLithiumUserId() {
        return lithiumUserId;
    }

    public void setLithiumUserId(String lithiumUserId) {
        this.lithiumUserId = lithiumUserId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        expiresAt = (expiresIn != null ? (expiresIn * 1000 + LiSystemClock.INSTANCE.getCurrentTimeMillis()) : null);
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long time) {
        this.expiresAt = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
