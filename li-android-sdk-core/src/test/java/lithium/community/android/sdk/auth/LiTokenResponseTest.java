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


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;

import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiSystemClock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by saiteja.tokala on 12/5/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LiSystemClock.class, LiTokenResponse.class})
public class LiTokenResponseTest {
    public static final String TEST_CLIENT_ID = "testClientId";
    public static final String TEST_REFRESH_TOKEN = "testRefreshToken";
    public static final String TEST_ACCCESS_TOKEN = "testAcccessToken";
    public static final String TEST_LITHIUM_USER_ID = "testLithiumUserId";
    public static final String TEST_TOKEN_TYPE = "testTokenType";
    public static final String TEST_JSON_STRING = "testJsonString";
    public static final String TEST_USER_ID = "testUserId";

    @Mock
    private LiSystemClock mock;


    private LiTokenResponse liTokenResponse;

    @Before
    public void setupRequest() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
        //systemClock= Mockito.mock(LiSystemClock.class);
        Field field = PowerMockito.field(LiSystemClock.class, "INSTANCE");
        mock = mock(LiSystemClock.class);
        field.set(LiSystemClock.class, mock);
        when(mock.getCurrentTimeMillis()).thenReturn(1L);
    }

    @Test
    public void getExpiresAtTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setExpiresAt(LiCoreSDKUtils.addCurrentTime(2L));
        assertNotEquals(null, liTokenResponse.getExpiresAt());
        assertEquals(2001, liTokenResponse.getExpiresAt());
    }

    @Test
    public void setExpiresAtTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setExpiresAt(2L);
        assertNotEquals(null, liTokenResponse.getExpiresAt());
        assertEquals(2, liTokenResponse.getExpiresAt());
    }

    @Test
    public void getExpiresInTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setExpiresIn(2L);
        assertNotEquals(null, liTokenResponse.getExpiresIn());
        assertEquals(2, liTokenResponse.getExpiresIn());
    }

    @Test
    public void getRefreshTokenTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setRefreshToken(TEST_REFRESH_TOKEN);
        assertNotEquals(null, liTokenResponse.getRefreshToken());
        assertEquals(TEST_REFRESH_TOKEN, liTokenResponse.getRefreshToken());
    }


    @Test
    public void getAccessTokenTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setAccessToken(TEST_ACCCESS_TOKEN);
        assertNotEquals(null, liTokenResponse.getAccessToken());
        assertEquals(TEST_ACCCESS_TOKEN, liTokenResponse.getAccessToken());
    }


    @Test
    public void getLithiumUserIdTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setLithiumUserId(TEST_LITHIUM_USER_ID);
        assertNotEquals(null, liTokenResponse.getLithiumUserId());
        assertEquals(TEST_LITHIUM_USER_ID, liTokenResponse.getLithiumUserId());
    }

    @Test
    public void getTokenTypeTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setTokenType(TEST_TOKEN_TYPE);
        assertNotEquals(null, liTokenResponse.getTokenType());
        assertEquals(TEST_TOKEN_TYPE, liTokenResponse.getTokenType());
    }

    @Test
    public void getJsonStringTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setJsonString(TEST_JSON_STRING);
        assertNotEquals(null, liTokenResponse.getJsonString());
        assertEquals(TEST_JSON_STRING, liTokenResponse.getJsonString());
    }

    @Test
    public void getUserIdTest() {
        liTokenResponse = new LiTokenResponse();
        liTokenResponse.setUserId(TEST_USER_ID);
        assertNotEquals(null, liTokenResponse.getUserId());
        assertEquals(TEST_USER_ID, liTokenResponse.getUserId());
    }


}
