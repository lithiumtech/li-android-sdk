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

import com.google.gson.Gson;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import lithium.community.android.sdk.auth.LiRefreshTokenRequest;
import lithium.community.android.sdk.auth.LiSSOAuthResponse;
import lithium.community.android.sdk.auth.LiTokenResponse;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiSystemClock;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author kunal.shrivastava
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LiSystemClock.class, LiAuthState.class})
public class LiAuthStateTest {

    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String REFRESH = "refresh";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ACCESS_TOKEN = "ACCESSTOKEN";
    public static final String USER_ID = "UserId";
    public static final String TOKEN_TYPE_BEARER = "TokenTypeBearer";
    public static final String NEW_REFRESH_TOKEN = "NewRefreshToken";
    public static final String LITHIUM_USER_ID = "LithiumUserId";
    public static final String AUTH_CODE = "AuthCode";
    public static final String TENANT_ID = "TenantId";
    public static final String API_PROXY_HOST = "ApiProxyHost";
    public static final String STATE = "State";
    public static final long EXPIRES_IN = 1L;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private LiSystemClock mock;

    @Before
    public void setUp() throws IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        Field field = PowerMockito.field(LiSystemClock.class, "INSTANCE");
        mock = mock(LiSystemClock.class);
        field.set(LiSystemClock.class, mock);
        when(mock.getCurrentTimeMillis()).thenReturn(1L);
    }

    @Test
    public void testJsonSerialize() {
        Gson gson = new Gson();
        LiAuthState liAuthState = new LiAuthState();
        LiRefreshTokenRequest liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientId(CLIENT_ID);
        liRefreshTokenRequest.setRefreshToken(REFRESH);
        liRefreshTokenRequest.setClientSecret(CLIENT_SECRET);
        liRefreshTokenRequest.setGrantType(REFRESH_TOKEN);

        LiSSOAuthResponse liSsoAuthResponse = new LiSSOAuthResponse();
        liSsoAuthResponse.setAuthCode(AUTH_CODE);
        liSsoAuthResponse.setTenantId(TENANT_ID);
        liSsoAuthResponse.setApiProxyHost(API_PROXY_HOST);
        liSsoAuthResponse.setState(STATE);
        liSsoAuthResponse.setUserId(USER_ID);
        liSsoAuthResponse.setJsonString(gson.toJson(liSsoAuthResponse));
        System.out.printf(gson.toJson(liSsoAuthResponse));
        liAuthState.update(liSsoAuthResponse);

        LiTokenResponse liTokenResponse = new LiTokenResponse();
        liTokenResponse.setAccessToken(ACCESS_TOKEN);
        liTokenResponse.setUserId(USER_ID);
        liTokenResponse.setTokenType(TOKEN_TYPE_BEARER);
        liTokenResponse.setRefreshToken(NEW_REFRESH_TOKEN);
        liTokenResponse.setLithiumUserId(LITHIUM_USER_ID);
        liTokenResponse.setExpiresIn(EXPIRES_IN);
        liTokenResponse.setExpiresAt(LiCoreSDKUtils.getTime(EXPIRES_IN));

        String json = gson.toJson(liTokenResponse);
        liTokenResponse.setJsonString(json);
        liAuthState.update(liTokenResponse);

        Assert.assertEquals("{\"lastSSOAuthorizationResponse\":\"{\\\"code\\\":\\\"AuthCode\\\","
                + "\\\"proxy-host\\\":\\\"ApiProxyHost\\\",\\\"state\\\":\\\"State\\\","
                + "\\\"tenant-id\\\":\\\"TenantId\\\",\\\"user-id\\\":\\\"UserId\\\"}\","
                + "\"mLastLiTokenResponse\":\"{\\\"lithiumUserId\\\":\\\"LithiumUserId\\\","
                + "\\\"access_token\\\":\\\"ACCESSTOKEN\\\",\\\"refresh_token\\\":\\\"NewRefreshToken\\\","
                + "\\\"expires_in\\\":1,\\\"userId\\\":\\\"UserId\\\","
                + "\\\"token_type\\\":\\\"TokenTypeBearer\\\",\\\"expiresAt\\\":1001}\","
                + "\"refreshToken\":\"NewRefreshToken\"}", liAuthState.jsonSerializeString());
    }

    @Test
    public void testJsonDeserialize() throws JSONException {
        LiAuthState liAuthState = LiAuthState.jsonDeserialize("{\"mLastTokenResponse\":{\"access_token\":\"accessToken\",\"request\":{\"clientId\":\"clientId\","
                + "\"configuration\":{\"authorizationEndpoint\":\"http:\\/\\/localhost:8080\"},"
                + "\"additionalParameters\":{},\"client_secret\":\"clientSecret\","
                + "\"grantType\":\"refresh_token\",\"refreshToken\":\"referesh\"},"
                + "\"refresh_token\":\"refresh\",\"id_token\":\"token\",\"additionalParameters\":{},"
                + "\"token_type\":\"tokenType\"},\"refreshToken\":\"refresh\"}");
        Assert.assertEquals("refresh", liAuthState.getRefreshToken());
    }

    @Test
    public void testLiUser() {
        LiAuthState liAuthState = new LiAuthState();
        LiUser liUser = new LiUser();
        liUser.setId(new Long("1"));
        liAuthState.setUser(liUser);
        Assert.assertEquals("1", liAuthState.getUser().getId().toString());
    }
}
