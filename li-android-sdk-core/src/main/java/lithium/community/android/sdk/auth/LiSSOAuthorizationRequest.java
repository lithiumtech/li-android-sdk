/*
 * LiSSOAuthorizationRequest.java
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
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.utils.LiUriUtils;

/**
 * Request for Authorization for SSO flow.
 * Created by saiteja.tokala on 12/2/16.
 */

public class LiSSOAuthorizationRequest {

    @VisibleForTesting
    protected static final String PARAM_CLIENT_ID = "client_id";

    @VisibleForTesting
    protected static final String PARAM_REDIRECT_URI = "redirect_uri";

    @VisibleForTesting
    protected static final String PARAM_RESPONSE_TYPE = "response_type";

    @VisibleForTesting
    protected static final String PARAM_STATE = "state";


    @SerializedName("jwt")
    private String ssoToken;
    @SerializedName("clientID")
    private String clientId;
    private String redirectUri;
    private String state;


    private transient Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = Uri.parse(uri);
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(String ssoToken) {
        this.ssoToken = ssoToken;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @NonNull
    public Uri toUri() {
        Uri.Builder uriBuilder = uri.buildUpon()
                .appendQueryParameter(PARAM_REDIRECT_URI, redirectUri.toString())
                .appendQueryParameter(PARAM_CLIENT_ID, clientId)
                .appendQueryParameter(PARAM_RESPONSE_TYPE, "code");
        LiUriUtils.appendQueryParameterIfNotNull(uriBuilder, PARAM_STATE, state);
        return uriBuilder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof LiSSOAuthorizationRequest)) return false;

        LiSSOAuthorizationRequest that = (LiSSOAuthorizationRequest) o;

        if (getSsoToken() != null ? !getSsoToken().equals(that.getSsoToken()) : that.getSsoToken() != null)
            return false;
        if (getClientId() != null ? !getClientId().equals(that.getClientId()) : that.getClientId() != null)
            return false;
        if (getRedirectUri() != null ? !getRedirectUri().equals(that.getRedirectUri()) : that.getRedirectUri() != null)
            return false;
        if (getState() != null ? !getState().equals(that.getState()) : that.getState() != null)
            return false;
        return getUri() != null ? getUri().equals(that.getUri()) : that.getUri() == null;

    }

    @Override
    public int hashCode() {
        int result = getSsoToken() != null ? getSsoToken().hashCode() : 0;
        result = 31 * result + (getClientId() != null ? getClientId().hashCode() : 0);
        result = 31 * result + (getRedirectUri() != null ? getRedirectUri().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getUri() != null ? getUri().hashCode() : 0);
        return result;
    }
}

