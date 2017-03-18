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
        Assert.assertEquals(Uri.parse(communityUri).buildUpon().path("auth/oauth2/authorize").build(), liAppCredentials.getAuthorizeUri());
        Assert.assertEquals(LiUriUtils.reverseDomainName(Uri.parse(communityUri)) + "://oauth2callback", liAppCredentials.getRedirectUri());
    }
}
