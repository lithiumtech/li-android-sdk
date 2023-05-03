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

package com.lithium.community.android.auth;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;
import com.lithium.community.android.utils.LiUriUtils;

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
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof LiSSOAuthorizationRequest)) {
            return false;
        }

        LiSSOAuthorizationRequest that = (LiSSOAuthorizationRequest) o;

        if (getSsoToken() != null ? !getSsoToken().equals(that.getSsoToken()) : that.getSsoToken() != null) {
            return false;
        }
        if (getClientId() != null ? !getClientId().equals(that.getClientId()) : that.getClientId() != null) {
            return false;
        }
        if (getRedirectUri() != null ? !getRedirectUri().equals(that.getRedirectUri())
                : that.getRedirectUri() != null) {
            return false;
        }
        if (getState() != null ? !getState().equals(that.getState()) : that.getState() != null) {
            return false;
        }
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

