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

package com.lithium.community.android.sdk.auth;

import android.net.Uri;

import com.lithium.community.android.sdk.auth.LiSSOTokenRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by saiteja.tokala on 12/5/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class LiSSOTokenRequestTest {

    public static final Uri TEST_URI = Uri.parse("testUri");
    public static final String TEST_CLIENT_ID = "testClientId";
    public static final String TEST_CLIENT_SECRET = "testClientSecret";
    public static final String TEST_GRANT_TYPE = "testGrantType";
    public static final String TEST_ACCESS_CODE = "testAccessCode";
    public static final String TEST_REDIRECT_URI = "testRedirectUri";
    private LiSSOTokenRequest liSSOTokenRequest;


    @Before
    public void setupRequest() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUriTest() {
        liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setUri(TEST_URI);
        assertNotEquals(null, liSSOTokenRequest.getUri());
        assertEquals(TEST_URI, liSSOTokenRequest.getUri());
    }

    @Test
    public void getClientIdTest() {
        liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setClientId(TEST_CLIENT_ID);
        assertNotEquals(null, liSSOTokenRequest.getClientId());
        assertEquals(TEST_CLIENT_ID, liSSOTokenRequest.getClientId());
    }

    @Test
    public void getClientSecretTest() {
        liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setClientSecret(TEST_CLIENT_SECRET);
        assertNotEquals(null, liSSOTokenRequest.getClientSecret());
        assertEquals(TEST_CLIENT_SECRET, liSSOTokenRequest.getClientSecret());
    }

    @Test
    public void getGrantTypeTest() {
        liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setGrantType(TEST_GRANT_TYPE);
        assertNotEquals(null, liSSOTokenRequest.getGrantType());
        assertEquals(TEST_GRANT_TYPE, liSSOTokenRequest.getGrantType());
    }

    @Test
    public void getAcceesCodeTest() {
        liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setAccessCode(TEST_ACCESS_CODE);
        assertNotEquals(null, liSSOTokenRequest.getAccessCode());
        assertEquals(TEST_ACCESS_CODE, liSSOTokenRequest.getAccessCode());
    }

    @Test
    public void getRedirectUriTest() {
        liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setRedirectUri(TEST_REDIRECT_URI);
        assertNotEquals(null, liSSOTokenRequest.getRedirectUri());
        assertEquals(TEST_REDIRECT_URI, liSSOTokenRequest.getRedirectUri());
    }
}
