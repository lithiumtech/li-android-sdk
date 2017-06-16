/*
 * LiAppCredentials.java
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
import android.text.TextUtils;

import java.net.MalformedURLException;

import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiUriUtils;

/**
 * A credentials object representing client application developer key and secret. It also includes
 * which community the developer app wishes to connect to.
 */
public final class LiAppCredentials {

    @NonNull
    private final String clientKey;
    @NonNull
    private final String clientSecret;
    @NonNull
    private final Uri communityUri;

    @NonNull
    private final Uri authorizeUri;

    @NonNull
    private final String redirectUri;

    private final String ssoAuthorizeUri;

    private final String tenantId;

    private final String apiProxyHost;

    private final String clientAppName;

    /**
     * private constructor.
     * @param clientKey This is client Id.
     * @param clientSecret This is client secret.
     * @param communityURL This is the URL of the community.
     * @param tenantId tenant ID for the community.
     * @param apiProxyHost API proxy host.
     * @throws MalformedURLException If the community URL is not valid this exception is thrown
     */
    private LiAppCredentials(@NonNull String clientKey, @NonNull String clientSecret,
                             @NonNull String communityURL, @NonNull String tenantId, @NonNull String apiProxyHost,
                             String clientAppName)
            throws MalformedURLException {
        LiCoreSDKUtils.checkNullOrNotEmpty(clientKey, "clientKey cannot be empty");
        LiCoreSDKUtils.checkNullOrNotEmpty(clientSecret, "clientKey cannot be empty");
        LiCoreSDKUtils.checkNullOrNotEmpty(communityURL, "communityURL cannot be empty");
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        this.communityUri = Uri.parse(communityURL);
        this.authorizeUri = this.communityUri.buildUpon().path("auth/oauth2/authorize").build();
        this.redirectUri = buildRedirectUri(communityUri);
        this.ssoAuthorizeUri = communityURL+"api/2.0/auth/authorize";
        this.tenantId = tenantId;
        this.apiProxyHost = apiProxyHost;
        if (!TextUtils.isEmpty(clientAppName)) {
            this.clientAppName = clientAppName;
        }
        else {
            this.clientAppName = tenantId +"-sdk";
        }
    }

    private static String buildRedirectUri(Uri communityUri) {
        return LiUriUtils.reverseDomainName(communityUri) + "://oauth2callback";
    }

    public String getClientAppName() {
        return clientAppName;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Uri getCommunityUri() {
        return communityUri;
    }

    public Uri getAuthorizeUri() {
        return authorizeUri;
    }

    @NonNull
    public String getRedirectUri() {
        return redirectUri;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getSsoAuthorizeUri() { return ssoAuthorizeUri; }

    public String getApiProxyHost() {
        return apiProxyHost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LiAppCredentials that = (LiAppCredentials) o;

        if (!clientKey.equals(that.clientKey)) {
            return false;
        }
        if (!clientSecret.equals(that.clientSecret)) {
            return false;
        }
        if (!communityUri.equals(that.communityUri)) {
            return false;
        }
        if (!authorizeUri.equals(that.authorizeUri)) {
            return false;
        }
        return redirectUri.equals(that.redirectUri);

    }

    @Override
    public int hashCode() {
        int result = clientKey.hashCode();
        result = 31 * result + clientSecret.hashCode();
        result = 31 * result + communityUri.hashCode();
        result = 31 * result + authorizeUri.hashCode();
        result = 31 * result + redirectUri.hashCode();
        return result;
    }

    /**
     * Creates instances of {@link LiAppCredentials}.
     */
    public static final class Builder {
        private String clientKey;
        private String clientSecret;
        private String communityUri;
        private String tenantId;
        private String apiProxyHost;
        private String clientAppName;

        public Builder() {
        }

        @NonNull
        public Builder setClientKey(@NonNull String clientKey) {
            this.clientKey = clientKey;
            return this;
        }

        @NonNull
        public Builder setClientSecret(@NonNull String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        @NonNull
        public Builder setTenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        @NonNull
        public Builder setCommunityUri(@NonNull String communityUri) {
            this.communityUri = communityUri;
            return this;
        }

        public Builder setApiProxyHost(String apiProxyHost) {
            this.apiProxyHost = apiProxyHost;
            return this;
        }

        public Builder setClientAppName(String clientAppName) {
            this.clientAppName = clientAppName;
            return this;
        }

        public LiAppCredentials build() throws MalformedURLException {
            return new LiAppCredentials(clientKey, clientSecret, communityUri, tenantId, apiProxyHost, clientAppName);
        }
    }
}