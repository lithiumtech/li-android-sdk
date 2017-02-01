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

import java.net.MalformedURLException;

import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiUriUtils;

/**
 * A credentials object representing client application developer key and secret. It also includes
 * which community the developer app wishes to connect to.
 */
public final class LiAppCredentials {

    private final String ssoToken;

    @NonNull
    private final String clientKey;
    @NonNull
    private final String clientSecret;
    @NonNull
    private final Uri communityUri;

    @NonNull
    private final Uri authorizeUri;

    private final boolean deferredLogin;

    @NonNull
    private final String redirectUri;

    private final String ssoAuthorizeUri;

    /**
     * private constructor.
     * @param clientKey This is client Id.
     * @param clientSecret This is client secret.
     * @param communityURL This is the URL of the community.
     * @param deferredLogin Parameter to suggest if deferred login is enabled.
     * @param ssoToken This is SSO Token.
     * @throws MalformedURLException
     */
    private LiAppCredentials(@NonNull String clientKey, @NonNull String clientSecret, @NonNull String communityURL,
                             boolean deferredLogin, String ssoToken) throws MalformedURLException {
        LiCoreSDKUtils.checkNullOrNotEmpty(clientKey, "clientKey cannot be empty");
        LiCoreSDKUtils.checkNullOrNotEmpty(clientSecret, "clientKey cannot be empty");
        LiCoreSDKUtils.checkNullOrNotEmpty(communityURL, "communityURL cannot be empty");
        this.clientKey = clientKey;
        this.ssoToken = ssoToken;
        this.clientSecret = clientSecret;
        this.communityUri = Uri.parse(communityURL);
        this.deferredLogin = deferredLogin;
        this.authorizeUri = this.communityUri.buildUpon().path("auth/oauth2/authorize").build();
        this.redirectUri = buildRedirectUri(communityUri);
        this.ssoAuthorizeUri = communityURL.toString()+"api/2.0/auth/authorize";
    }

    private static String buildRedirectUri(Uri communityUri) {
        return LiUriUtils.reverseDomainName(communityUri) + "://oauth2callback";
    }

    public String getSsoToken() {
        return ssoToken;
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

    public boolean isDeferredLogin() {
        return deferredLogin;
    }

    public Uri getAuthorizeUri() {
        return authorizeUri;
    }

    @NonNull
    public String getRedirectUri() {
        return redirectUri;
    }

    public String getSsoAuthorizeUri() { return ssoAuthorizeUri; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LiAppCredentials that = (LiAppCredentials) o;

        if (deferredLogin != that.deferredLogin) {
            return false;
        }
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
        result = 31 * result + (deferredLogin ? 1 : 0);
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
        private String ssoToken;
        private boolean deferredLogin;

        public Builder() {
            this.ssoToken = null;
        }

        public Builder(String ssoToken) {
            this.ssoToken = ssoToken;
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
        public Builder setCommunityUri(@NonNull String communityUri) {
            this.communityUri = communityUri;
            return this;
        }

        public Builder setDeferredLogin(boolean deferredLogin) {
            this.deferredLogin = deferredLogin;
            return this;
        }

        public LiAppCredentials build() throws MalformedURLException {
            return new LiAppCredentials(clientKey, clientSecret, communityUri, deferredLogin, ssoToken);
        }
    }
}
