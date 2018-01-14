package lithium.community.android.sdk.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.helpers.LiAvatar;
import lithium.community.android.sdk.model.helpers.LiImage;
import lithium.community.android.sdk.model.response.LiUser;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by kunal.shrivastava on 12/4/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LiSDKManager.class, SharedPreferences.class})
public class LiAuthManagerTest {

    private static final String communityUrl = "http://community.lithium.com";
    private static final String authStateJson = "{\"lastSSOAuthorizationResponse\":\"{\\\"code\\\":\\\"AuthCode\\\",\\\"proxy-host\\\":\\\"ApiProxyHost\\\",\\\"state\\\":\\\"State\\\",\\\"tenant-id\\\":\\\"TenantId\\\",\\\"user-id\\\":\\\"UserId\\\"}\",\"mLastLiTokenResponse\":\"{\\\"lithiumUserId\\\":\\\"LithiumUserId\\\",\\\"access_token\\\":\\\"ACCESSTOKEN\\\",\\\"refresh_token\\\":\\\"NewRefreshToken\\\",\\\"expires_in\\\":1,\\\"userId\\\":\\\"UserId\\\",\\\"token_type\\\":\\\"TokenTypeBearer\\\",\\\"expiresAt\\\":1001}\",\"refreshToken\":\"NewRefreshToken\"}";
    private static final String AVATAR_IMAGE_DESC = "avatarImageDescription";
    private static final String EMAIL = "liuser@test.com";
    private static final String AVATAR_PROFILE = "profile";
    private static final String AVATAR_MESSAGE = "message";
    private static final String LOGIN = "login";
    private static final String HREF = "href";
    private static final String PROFILE_PAGE_URL = "profile_page_url";
    private static final String SSO_TOKEN = "sso_token";

    //
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String REFRESH = "refresh";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ACCESSTOKEN = "ACCESSTOKEN";
    public static final String USER_ID = "UserId";
    public static final String TOKEN_TYPE_BEARER = "TokenTypeBearer";
    public static final String NEW_REFRESH_TOKEN = "NewRefreshToken";
    public static final String LITHIUM_USER_ID = "LithiumUserId";
    public static final long EXPIRES_IN = 1L;
    public static final String AUTH_CODE = "AuthCode";
    public static final String TENANT_ID = "TenantId";
    public static final String API_PROXY_HOST = "ApiProxyHost";
    public static final String STATE = "State";

    //
    private Activity activity;

    private SharedPreferences sharedPreferences;


    @Test
    public void testSetLoggedInUser() throws MalformedURLException, URISyntaxException {
        activity = mock(Activity.class);
        sharedPreferences = mock(SharedPreferences.class);
        Resources resource = mock(Resources.class);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        when(activity.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn(authStateJson);
        when(activity.getResources()).thenReturn(resource);
        LiSDKManager.init(activity, TestHelper.getTestAppCredentials());
        LiUser liUser = new LiUser();
        LiImage avatarImage = mock(LiImage.class);
        when(avatarImage.getDescription()).thenReturn(AVATAR_IMAGE_DESC);
        liUser.setAvatarImage(avatarImage);
        LiAvatar liAvatar = new LiAvatar();
        liAvatar.setProfile(AVATAR_PROFILE);
        liAvatar.setMessage(AVATAR_MESSAGE);
        liUser.setAvatar(liAvatar);
        LiBaseModelImpl.LiString email = new LiBaseModelImpl.LiString();
        email.setValue(EMAIL);
        liUser.setEmail(email);
        LiBaseModelImpl.LiString login = new LiBaseModelImpl.LiString();
        login.setValue(LOGIN);
        liUser.setLogin(login);
        liUser.setHref(HREF);
        liUser.setProfilePageUrl(PROFILE_PAGE_URL);

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(editor.commit()).thenReturn(true);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(sharedPreferences.edit().putString(anyString(), anyString())).thenReturn(editor);
        LiSDKManager.getInstance().setLoggedInUser(activity, liUser);

        Assert.assertEquals(EMAIL, LiSDKManager.getInstance().getLoggedInUser().getEmail());
        Assert.assertEquals(HREF, LiSDKManager.getInstance().getLoggedInUser().getHref());
        Assert.assertEquals(PROFILE_PAGE_URL, LiSDKManager.getInstance().getLoggedInUser().getProfilePageUrl());

        Assert.assertEquals(ACCESSTOKEN, LiSDKManager.getInstance().getNewAuthToken());
        Assert.assertEquals(NEW_REFRESH_TOKEN, LiSDKManager.getInstance().getRefreshToken());
        Assert.assertTrue(LiSDKManager.getInstance().isUserLoggedIn());

    }

}
