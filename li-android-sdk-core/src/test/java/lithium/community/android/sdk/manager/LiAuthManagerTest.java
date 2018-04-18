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

package lithium.community.android.sdk.manager;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.exception.LiInitializationException;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.helpers.LiAvatar;
import lithium.community.android.sdk.model.helpers.LiImage;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by kunal.shrivastava on 12/4/16.
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest({LiSDKManager.class, SharedPreferences.class})
public class LiAuthManagerTest {

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String NEW_REFRESH_TOKEN = "NewRefreshToken";
    private static final String AUTH_STATE_JSON = "{\"lastSSOAuthorizationResponse\":\"{\\\"code\\\":\\\"AuthCode\\\","
            + "\\\"proxy-host\\\":\\\"ApiProxyHost\\\",\\\"state\\\":\\\"State\\\","
            + "\\\"tenant-id\\\":\\\"TenantId\\\",\\\"user-id\\\":\\\"UserId\\\"}\","
            + "\"mLastLiTokenResponse\":\"{\\\"lithiumUserId\\\":\\\"LithiumUserId\\\","
            + "\\\"access_token\\\":\\\"ACCESS_TOKEN\\\",\\\"refresh_token\\\":\\\"NewRefreshToken\\\","
            + "\\\"expires_in\\\":1,\\\"userId\\\":\\\"UserId\\\",\\\"token_type\\\":\\\"TokenTypeBearer\\\","
            + "\\\"expiresAt\\\":1001}\",\"refreshToken\":\"NewRefreshToken\"}";
    private static final String AVATAR_IMAGE_DESC = "avatarImageDescription";
    private static final String EMAIL = "liuser@test.com";
    private static final String AVATAR_PROFILE = "profile";
    private static final String AVATAR_MESSAGE = "message";
    private static final String LOGIN = "login";
    private static final String HREF = "href";
    private static final String PROFILE_PAGE_URL = "profile_page_url";


    @Test
    public void testSetLoggedInUser() throws LiInitializationException {
        Context context = TestHelper.createMockContext();

        LiSDKManager.initialize(context, TestHelper.getTestAppCredentials());
        LiSecuredPrefManager.getInstance().putString(context, LiCoreSDKConstants.LI_AUTH_STATE, AUTH_STATE_JSON);
        LiSDKManager.getInstance().restoreAuthState(context);

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

        LiSDKManager.getInstance().setLoggedInUser(context, liUser);

        Assert.assertEquals(EMAIL, LiSDKManager.getInstance().getLoggedInUser().getEmail());
        Assert.assertEquals(HREF, LiSDKManager.getInstance().getLoggedInUser().getHref());
        Assert.assertEquals(PROFILE_PAGE_URL, LiSDKManager.getInstance().getLoggedInUser().getProfilePageUrl());

        Assert.assertEquals(ACCESS_TOKEN, LiSDKManager.getInstance().getAuthToken());
        Assert.assertEquals(NEW_REFRESH_TOKEN, LiSDKManager.getInstance().getRefreshToken());
        Assert.assertTrue(LiSDKManager.getInstance().isUserLoggedIn());
    }
}
