/*
 * LiSSOAuthResponse.java
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

/**
 * Response class of LiSSOAuthorizationRequest.
 * Created by saiteja.tokala on 12/2/16.
 */

public class LiSSOAuthResponse {
    @SerializedName("code")
    private String authCode;
    @SerializedName("proxy-host")
    private String apiProxyHost;
    private String state;
    @SerializedName("tenant-id")
    private String tenantId;
    @SerializedName("user-id")
    private String userId;

    private String jsonString;

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getApiProxyHost() {
        return apiProxyHost;
    }

    public void setApiProxyHost(String apiProxyHost) {
        this.apiProxyHost = apiProxyHost;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
