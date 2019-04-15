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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.LiUriUtils;
import com.lithium.community.android.utils.MessageConstants;

/**
 * The credentials for initializing the LIA SDK Manager. The client name, client Id, client secret,
 * and tenant Id can be found in the Community Admin page; under the System category choose Api Apps.
 * Make sure that the credentials are for Android. The community URL is the public domain where your
 * community is hosted. The default API gateway host is "api.lithium.com".
 */
public final class LiAppCredentials {

    /**
     * The API end point for authorizing SSO login.
     */
    @NonNull
    private static final String SSO_AUTH_END_POINT = "api/2.0/auth/authorize";

    /**
     * The API end point for authorizing login using OAuth 2.0.
     */
    @NonNull
    private static final String OAUTH_END_POINT = "auth/oauth2/authorize";

    @NonNull
    private final String clientName;

    @NonNull
    private final String tenantId;

    @NonNull
    private final String clientKey;

    @NonNull
    private final String clientSecret;

    @NonNull
    private final Uri communityUri;

    @NonNull
    private final String encryptionKey;

    @NonNull
    private final Uri authorizeUri;

    @NonNull
    private final String redirectUri;

    @NonNull
    private final String ssoAuthorizeUri;

    /**
     * <p>
     * Default public constructor for Lia SDK Credentials.
     * </p>
     *
     * @param clientName    The client name.
     * @param clientKey     The client Id.
     * @param clientSecret  The client secret.
     * @param tenantId      tenant ID of the community.
     * @param communityURL  The LIA community's URL.
     * @param encryptionKey A unique static encryption key for this device. Use a GUID or Instance ID which is 'Single App' scoped with 'Install-reset'
     *                      resettability and persistence. Example: <code>UUID.randomUUID().toString()</code> persisted in a Shared Preferences.
     */
    public LiAppCredentials(@NonNull String clientName, @NonNull String clientKey, @NonNull String clientSecret,
            @NonNull String tenantId, @NonNull String communityURL, @NonNull String encryptionKey) {
        this.clientName = LiCoreSDKUtils.checkNotNullAndNotEmpty(clientName, MessageConstants.wasEmpty("clientName"));
        this.clientKey = LiCoreSDKUtils.checkNotNullAndNotEmpty(clientKey, MessageConstants.wasEmpty("clientKey"));
        this.clientSecret = LiCoreSDKUtils.checkNotNullAndNotEmpty(clientSecret, MessageConstants.wasEmpty("clientSecret"));
        this.tenantId = LiCoreSDKUtils.checkNotNullAndNotEmpty(tenantId, MessageConstants.wasEmpty("tenantId"));

        this.communityUri = Uri.parse(LiCoreSDKUtils.checkNotNullAndNotEmpty(communityURL, MessageConstants.wasEmpty("communityURL")));
        this.encryptionKey = LiCoreSDKUtils.checkNotNullAndNotEmpty(encryptionKey, MessageConstants.wasEmpty("encryptionKey"));

        this.redirectUri = buildRedirectUri(communityUri);
        this.ssoAuthorizeUri = communityURL + SSO_AUTH_END_POINT;
        this.authorizeUri = this.communityUri.buildUpon().path(OAUTH_END_POINT).build();
    }

    /**
     * Default public constructor for Lia SDK Credentials.
     *
     * @param clientName     The client name.
     * @param clientKey      The client Id.
     * @param clientSecret   The client secret.
     * @param tenantId       tenant ID of the community.
     * @param communityURL   The LIA community's URL. (scheme + host)
     * @param apiGatewayHost API gateway host. (host)
     * @param encryptionKey  A unique static encryption key for this device. Use a GUID or Instance ID which is 'Single App' scoped with 'Install-reset'
     *                       resettability and persistence. Example: <code>UUID.randomUUID().toString()</code> persisted in a Shared Preferences.
     * @deprecated Use {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
     */
    @Deprecated
    public LiAppCredentials(@NonNull String clientName, @NonNull String clientKey, @NonNull String clientSecret, @NonNull String tenantId,
            @NonNull String communityURL, @SuppressWarnings("unused") @Deprecated @Nullable String apiGatewayHost,
            @NonNull String encryptionKey) {
        this(clientName, clientKey, clientSecret, tenantId, communityURL, encryptionKey);
    }

    private static String buildRedirectUri(Uri communityUri) {
        return LiUriUtils.reverseDomainName(communityUri) + "://oauth2callback";
    }

    @NonNull
    public String getClientName() {
        return clientName;
    }

    @NonNull
    public String getClientKey() {
        return clientKey;
    }

    @NonNull
    public String getClientSecret() {
        return clientSecret;
    }

    @NonNull
    public String getTenantId() {
        return tenantId;
    }

    @NonNull
    public Uri getCommunityUri() {
        return communityUri;
    }

    @NonNull
    public String getEncryptionKey() {
        return encryptionKey;
    }

    @NonNull
    public Uri getAuthorizeUri() {
        return authorizeUri;
    }

    @NonNull
    public String getRedirectUri() {
        return redirectUri;
    }

    @NonNull
    public String getSsoAuthorizeUri() {
        return ssoAuthorizeUri;
    }

    /**
     * @return The client name
     * @deprecated Use {@link #getClientName()} instead.
     */
    @Deprecated
    @NonNull
    public String getClientAppName() {
        return clientName;
    }

    /**
     * The encryption key used for local storage.
     *
     * @return The encryption key used for local storage.
     * @deprecated use {@link #getEncryptionKey()} instead.
     */
    @NonNull
    @Deprecated
    public String getDeviceId() {
        return getEncryptionKey();
    }

    /**
     * The API Gateways host address.
     *
     * @return The API Gateway host Uri.
     * @deprecated This property is no longer used. Use {@link #getCommunityUri()} instead.
     */
    @Deprecated
    @NonNull
    public Uri getApiGatewayHost() {
        return getCommunityUri();
    }

    /**
     * @return the API Gateways host address
     * @deprecated This property is no longer used. Use {@link #getCommunityUri()} instead.
     */
    @Deprecated
    @NonNull
    public String getApiProxyHost() {
        return getCommunityUri().toString();
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

        return clientName.equals(that.clientName) && tenantId.equals(that.tenantId) && clientKey.equals(that.clientKey)
                && clientSecret.equals(that.clientSecret) && communityUri.equals(that.communityUri) && encryptionKey.equals(that.encryptionKey)
                && authorizeUri.equals(that.authorizeUri) && redirectUri.equals(that.redirectUri) && ssoAuthorizeUri.equals(that.ssoAuthorizeUri);
    }

    @Override
    public int hashCode() {
        int result = clientName.hashCode();
        result = 31 * result + tenantId.hashCode();
        result = 31 * result + clientKey.hashCode();
        result = 31 * result + clientSecret.hashCode();
        result = 31 * result + communityUri.hashCode();
        result = 31 * result + encryptionKey.hashCode();
        result = 31 * result + authorizeUri.hashCode();
        result = 31 * result + redirectUri.hashCode();
        result = 31 * result + ssoAuthorizeUri.hashCode();
        return result;
    }

    /**
     * Builder to create instances of {@link LiAppCredentials}.
     *
     * @deprecated Use the default public constructor {@link #LiAppCredentials(String, String, String, String, String, String, String)} instead.
     */
    @Deprecated
    public static final class Builder {

        private String clientName;
        private String clientKey;
        private String clientSecret;
        private String tenantId;
        private String communityUri;

        /**
         * Default public constructor for the builder.
         *
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        public Builder() {
        }

        /**
         * Set the client name.
         *
         * @param clientName the client name.
         * @return this builder.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        public Builder setClientName(@NonNull String clientName) {
            this.clientName = clientName;
            return this;
        }

        /**
         * Set the client key.
         *
         * @param clientKey the client key.
         * @return this builder.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        @NonNull
        public Builder setClientKey(@NonNull String clientKey) {
            this.clientKey = clientKey;
            return this;
        }

        /**
         * Set the client secret.
         *
         * @param clientSecret the client secret.
         * @return this builder.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        @NonNull
        public Builder setClientSecret(@NonNull String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Set the tenant Id.
         *
         * @param tenantId the tenant Id.
         * @return this builder.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        @NonNull
        public Builder setTenantId(@NonNull String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Set the LIA community URL.
         *
         * @param communityUri the LIA community URL.
         * @return this builder.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        @NonNull
        public Builder setCommunityUri(@NonNull String communityUri) {
            this.communityUri = communityUri;
            return this;
        }

        /**
         * Set default API Gateway host address.
         *
         * @param apiGatewayUri the API Gateway host address.
         * @return this builder.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        @NonNull
        public Builder setApiGatewayUri(@NonNull String apiGatewayUri) {
            return this;
        }

        /**
         * Set the API gateways host address.
         *
         * @param apiProxyHost The API gateways host address.
         * @return this builder.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        public Builder setApiProxyHost(@NonNull String apiProxyHost) {
            return this;
        }

        /**
         * Set the client name.
         *
         * @param clientAppName The client name to set
         * @return this builder
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        public Builder setClientAppName(@NonNull String clientAppName) {
            this.clientName = clientAppName;
            return this;
        }

        /**
         * Build and return a new instance of {@link LiAppCredentials}.
         *
         * @return a new instance of {@link LiAppCredentials}.
         * @deprecated Use the public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
         */
        @Deprecated
        @NonNull
        public LiAppCredentials build() {
            return new LiAppCredentials(clientName, clientKey, clientSecret, tenantId, communityUri, "encryptionKey");
        }
    }
}