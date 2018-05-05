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

package com.lithium.community.android.sdk.auth;

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
