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

import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;

import lithium.community.android.sdk.utils.LiUriUtils;

/**
 * Created by shoureya.kant on 12/11/16.
 */

public class LiAppCredentialsTest {

    private final String ssoToken = "ssoToken";
    private final String clientKey = "clientKey";
    private final String clientSecret = "clientSecret";
    private final String communityUri = "http://www.lithium.com/";
    private final boolean deferredLogin = false;
    private final String ssoAuthorizeUri = "http://www.lithium.com/api/2.0/auth/authorize";

    @Test
    public void getParamsTest() throws MalformedURLException {
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder()
                .setClientKey(clientKey)
                .setClientSecret(clientSecret)
                .setCommunityUri(communityUri)
                .build();
        Assert.assertEquals(clientKey, liAppCredentials.getClientKey());
        Assert.assertEquals(clientSecret, liAppCredentials.getClientSecret());
        Assert.assertEquals(Uri.parse(communityUri), liAppCredentials.getCommunityUri());
        Assert.assertEquals(ssoAuthorizeUri, liAppCredentials.getSsoAuthorizeUri());
        Assert.assertEquals(Uri.parse(communityUri).buildUpon().path("auth/oauth2/authorize").build(),
                liAppCredentials.getAuthorizeUri());
        Assert.assertEquals(LiUriUtils.reverseDomainName(Uri.parse(communityUri)) + "://oauth2callback",
                liAppCredentials.getRedirectUri());
    }
}
