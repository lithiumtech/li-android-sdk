/*
 * LiSSOTokenRequest.java
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

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Request class to fetch Access and Refresh Tokens.
 * Created by saiteja.tokala on 12/2/16.
 */

public class LiSSOTokenRequest {

    @SerializedName("code")
    private String acceesCode;
    @SerializedName("client_id")
    private String clientId;
    @SerializedName("redirect_uri")
    private String redirectUri;
    @SerializedName("client_secret")
    private String clientSecret;
    @SerializedName("grant_type")
    private String grantType;

    private transient Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getAcceesCode() {
        return acceesCode;
    }

    public void setAcceesCode(String acceesCode) {
        this.acceesCode = acceesCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
}
