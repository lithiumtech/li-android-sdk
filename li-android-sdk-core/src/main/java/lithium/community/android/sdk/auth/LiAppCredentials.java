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

package lithium.community.android.sdk.auth;

import android.net.Uri;
import android.support.annotation.NonNull;

import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiUriUtils;

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
    private final Uri apiGatewayHost;

    @NonNull
    private final Uri authorizeUri;

    @NonNull
    private final String redirectUri;

    @NonNull
    private final String ssoAuthorizeUri;

    /**
     * Default public constructor for Lia SDK Credentials.
     *
     * @param clientName     The client name.
     * @param clientKey      The client Id.
     * @param clientSecret   The client secret.
     * @param tenantId       tenant ID of the community.
     * @param communityURL   The LIA community's URL.
     * @param apiGatewayHost API gateway host.
     */
    public LiAppCredentials(@NonNull String clientName, @NonNull String clientKey, @NonNull String clientSecret,
                            @NonNull String tenantId, @NonNull String communityURL, @NonNull String apiGatewayHost) {
        this.clientName = LiCoreSDKUtils.checkNullOrNotEmpty(clientName, "clientName was empty");
        this.clientKey = LiCoreSDKUtils.checkNullOrNotEmpty(clientKey, "clientKey was empty");
        this.clientSecret = LiCoreSDKUtils.checkNullOrNotEmpty(clientSecret, "clientSecret was empty");
        this.tenantId = LiCoreSDKUtils.checkNullOrNotEmpty(tenantId, "tenantId was empty");

        this.communityUri = Uri.parse(LiCoreSDKUtils.checkNullOrNotEmpty(communityURL, "communityURL was empty"));
        this.apiGatewayHost = Uri.parse(LiCoreSDKUtils.checkNullOrNotEmpty(apiGatewayHost, "apiGatewayHost was empty"));

        this.redirectUri = buildRedirectUri(communityUri);
        this.ssoAuthorizeUri = communityURL + SSO_AUTH_END_POINT;
        this.authorizeUri = this.communityUri.buildUpon().path(OAUTH_END_POINT).build();
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
    public Uri getApiGatewayHost() {
        return apiGatewayHost;
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
    @NonNull
    public String getClientAppName() {
        return clientName;
    }

    /**
     * @return the API Gateways host address
     * @deprecated Use {@link #getApiGatewayHost()}
     */
    @NonNull
    public String getApiProxyHost() {
        return getApiGatewayHost().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiAppCredentials that = (LiAppCredentials) o;

        if (!clientName.equals(that.clientName)) return false;
        if (!tenantId.equals(that.tenantId)) return false;
        if (!clientKey.equals(that.clientKey)) return false;
        if (!clientSecret.equals(that.clientSecret)) return false;
        if (!communityUri.equals(that.communityUri)) return false;
        if (!apiGatewayHost.equals(that.apiGatewayHost)) return false;
        if (!authorizeUri.equals(that.authorizeUri)) return false;
        if (!redirectUri.equals(that.redirectUri)) return false;
        return ssoAuthorizeUri.equals(that.ssoAuthorizeUri);
    }

    @Override
    public int hashCode() {
        int result = clientName.hashCode();
        result = 31 * result + tenantId.hashCode();
        result = 31 * result + clientKey.hashCode();
        result = 31 * result + clientSecret.hashCode();
        result = 31 * result + communityUri.hashCode();
        result = 31 * result + apiGatewayHost.hashCode();
        result = 31 * result + authorizeUri.hashCode();
        result = 31 * result + redirectUri.hashCode();
        result = 31 * result + ssoAuthorizeUri.hashCode();
        return result;
    }

    /**
     * Builder to create instances of {@link LiAppCredentials}.
     *
     * @deprecated Use the default public constructor {@link #LiAppCredentials(String, String, String, String, String, String)} instead.
     */
    public static final class Builder {

        private String clientName;
        private String clientKey;
        private String clientSecret;
        private String tenantId;
        private String communityUri;
        private String apiGatewayUri;

        /**
         * Default public constructor for the builder.
         */
        public Builder() {
        }

        /**
         * Set the client name.
         *
         * @param clientName the client name.
         * @return this builder.
         */
        public Builder setClientName(@NonNull String clientName) {
            this.clientName = clientName;
            return this;
        }

        /**
         * Set the client key.
         *
         * @param clientKey the client key.
         * @return this builder.
         */
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
         */
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
         */
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
         */
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
         */
        @NonNull
        public Builder setApiGatewayUri(@NonNull String apiGatewayUri) {
            this.apiGatewayUri = apiGatewayUri;
            return this;
        }

        /**
         * Set the API gateways host address.
         *
         * @param apiProxyHost The API gateways host address.
         * @return this builder.
         * @deprecated Use {@link #getApiGatewayHost()} instead.
         */
        public Builder setApiProxyHost(@NonNull String apiProxyHost) {
            this.apiGatewayUri = apiProxyHost;
            return this;
        }

        /**
         * Set the client name.
         *
         * @param clientAppName The client name to set
         * @return this builder
         * @deprecated Use {@link #setClientName(String)}
         */
        public Builder setClientAppName(@NonNull String clientAppName) {
            this.clientName = clientAppName;
            return this;
        }

        /**
         * Build and return a new instance of {@link LiAppCredentials}.
         *
         * @return a new instance of {@link LiAppCredentials}.
         */
        @NonNull
        public LiAppCredentials build() {
            return new LiAppCredentials(clientName, clientKey, clientSecret, tenantId, communityUri, apiGatewayUri);
        }
    }
}