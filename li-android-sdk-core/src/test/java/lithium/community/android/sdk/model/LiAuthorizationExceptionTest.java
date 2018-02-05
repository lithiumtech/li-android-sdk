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

package lithium.community.android.sdk.model;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import lithium.community.android.sdk.auth.LiAuthorizationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/*
  Created by mahaveer.udabal on 10/18/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class LiAuthorizationExceptionTest {

    private final String INVALID_REQUEST = "invalid_request";
    @Mock
    private Intent mIntent;
    private String validJson, inValidJson;

    @Before
    public void setUp() {
        validJson = "{\n" +
                "    \"type\": 1,\n" +
                "    \"code\": 1001,\n" +
                "    \"error\": \"Invalid_Auth\",\n" +
                "    \"errorDescription\": \"Not Authorized\",\n" +
                "    \"errorUri\": \"www.testinguri.com\"\n" +
                "}";
        inValidJson = "{\n" +
                "    \"type\": 1,\n" +
                "    \"code\": 1001,\n" +
                "    \"error\": \"Invalid_Auth\",\n" +
                "    \"errorDescription\": \"Not Authorized\",\n" +
                "    \"errorUri\": \"www.testinguri.com\",\n" +
                "}";
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void fromJsonNullTest() throws JSONException {
        String json = null;
        LiAuthorizationException.fromJson(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromJsonEmptyTest() throws JSONException {
        String json = "";
        LiAuthorizationException.fromJson(json);
    }

    @Test
    public void fromJsonNonNullTest() throws JSONException {
        LiAuthorizationException liAuthorizationException = LiAuthorizationException.fromJson(validJson);
        assertTrue(liAuthorizationException != null);
        assertEquals(1, liAuthorizationException.type);
        assertEquals(1001, liAuthorizationException.code);
        assertEquals("Invalid_Auth", liAuthorizationException.error);
        assertEquals("Not Authorized", liAuthorizationException.errorDescription);
    }

    @Test(expected = JSONException.class)
    public void fromJsonJSONExceptionTest() throws JSONException {
        LiAuthorizationException.fromJson(inValidJson);
    }

    @Test
    public void byStringAuthRequestErrorsTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.AuthorizationRequestErrors.byString(INVALID_REQUEST);
        assertEquals(1, inValidRequestLiAuthorizationException.type);
        assertEquals(INVALID_REQUEST, inValidRequestLiAuthorizationException.error);
        assertEquals(1000, inValidRequestLiAuthorizationException.code);
        assertEquals(null, inValidRequestLiAuthorizationException.errorDescription);
        assertEquals(null, inValidRequestLiAuthorizationException.errorUri);
        LiAuthorizationException inValidRequestLiAuthorizationException2
                = LiAuthorizationException.AuthorizationRequestErrors.byString("invalid_request2");
        assertEquals(1008, inValidRequestLiAuthorizationException2.code);
    }

    @Test
    public void byStringTokenRequestErrorsTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.TokenRequestErrors.byString("invalid_client");
        assertEquals(2, inValidRequestLiAuthorizationException.type);
        assertEquals("invalid_client", inValidRequestLiAuthorizationException.error);
        assertEquals(2001, inValidRequestLiAuthorizationException.code);
        assertEquals(null, inValidRequestLiAuthorizationException.errorDescription);
        assertEquals(null, inValidRequestLiAuthorizationException.errorUri);
        LiAuthorizationException inValidRequestLiAuthorizationException2
                = LiAuthorizationException.TokenRequestErrors.byString("invalid_client2");
        assertEquals(2007, inValidRequestLiAuthorizationException2.code);
    }

    @Test
    public void byStringRegistrationRequestErrorsTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.RegistrationRequestErrors.byString("invalid_redirect_uri");
        assertEquals(4, inValidRequestLiAuthorizationException.type);
        assertEquals("invalid_redirect_uri", inValidRequestLiAuthorizationException.error);
        assertEquals(4001, inValidRequestLiAuthorizationException.code);
        assertEquals(null, inValidRequestLiAuthorizationException.errorDescription);
        assertEquals(null, inValidRequestLiAuthorizationException.errorUri);
        LiAuthorizationException inValidRequestLiAuthorizationException2
                = LiAuthorizationException.RegistrationRequestErrors.byString("invalid_redirect_uri2");
        assertEquals(4004, inValidRequestLiAuthorizationException2.code);
    }

    @Test
    public void generalExTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR;
        assertEquals(0, inValidRequestLiAuthorizationException.type);
        assertEquals("JSON deserialization error", inValidRequestLiAuthorizationException.errorDescription);
        assertEquals(5, inValidRequestLiAuthorizationException.code);
        assertEquals(null, inValidRequestLiAuthorizationException.error);
        assertEquals(null, inValidRequestLiAuthorizationException.errorUri);
    }

    @Test
    public void authExTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST;
        assertEquals(1, inValidRequestLiAuthorizationException.type);
        assertEquals(INVALID_REQUEST, inValidRequestLiAuthorizationException.error);
        assertEquals(1000, inValidRequestLiAuthorizationException.code);
        assertEquals(null, inValidRequestLiAuthorizationException.errorDescription);
        assertEquals(null, inValidRequestLiAuthorizationException.errorUri);
    }

    @Test
    public void tokenExTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.TokenRequestErrors.INVALID_CLIENT;
        assertEquals(2, inValidRequestLiAuthorizationException.type);
        assertEquals("invalid_client", inValidRequestLiAuthorizationException.error);
        assertEquals(2001, inValidRequestLiAuthorizationException.code);
        assertEquals(null, inValidRequestLiAuthorizationException.errorDescription);
        assertEquals(null, inValidRequestLiAuthorizationException.errorUri);
    }

    @Test
    public void registrationExTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.RegistrationRequestErrors.INVALID_REDIRECT_URI;
        assertEquals(4, inValidRequestLiAuthorizationException.type);
        assertEquals("invalid_redirect_uri", inValidRequestLiAuthorizationException.error);
        assertEquals(4001, inValidRequestLiAuthorizationException.code);
        assertEquals(null, inValidRequestLiAuthorizationException.errorDescription);
        assertEquals(null, inValidRequestLiAuthorizationException.errorUri);
    }

    @Test(expected = NullPointerException.class)
    public void fromTemplateNullTest() {
        LiAuthorizationException.fromTemplate(null, new Throwable());
    }

    @Test
    public void fromTemplateNonNullTest() {
        LiAuthorizationException liAuthorizationException
                = LiAuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR;
        LiAuthorizationException authException = LiAuthorizationException.fromTemplate(liAuthorizationException,
                new Throwable());
        assertEquals(0, authException.type);
        assertEquals("JSON deserialization error", authException.errorDescription);
        assertEquals(5, authException.code);
        assertEquals(null, authException.error);
        assertEquals(null, authException.errorUri);
    }

    @Test(expected = NullPointerException.class)
    public void fromOAuthTemplateNullTest() {
        LiAuthorizationException.fromOAuthTemplate(null, "error", "error_Desc", null);
    }

    @Test
    public void fromOAuthTemplateNonNullTest() {
        LiAuthorizationException liAuthorizationException
                = LiAuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR;
        LiAuthorizationException authException = LiAuthorizationException.fromOAuthTemplate(liAuthorizationException,
                "newError", "newErrorDescription", null);
        assertEquals(0, authException.type);
        assertEquals("newErrorDescription", authException.errorDescription);
        assertEquals(5, authException.code);
        assertEquals("newError", authException.error);
        assertEquals(null, authException.errorUri);
    }

    @Test(expected = NullPointerException.class)
    public void fromIntentNonNullTest() throws JSONException {
        when(mIntent.hasExtra(anyString())).thenReturn(true);
        when(LiAuthorizationException.fromJson(mIntent.getStringExtra(anyString()))).thenThrow(JSONException.class);
        LiAuthorizationException.fromIntent(mIntent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromIntentNullTest() {
        LiAuthorizationException.fromIntent(null);
    }

    @Test
    public void toIntentTest() {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST;
        Intent intent = inValidRequestLiAuthorizationException.toIntent();
        String jsonStr = inValidRequestLiAuthorizationException.toJsonString();
        assertNotEquals(null, intent);
    }

    @Test
    public void toJsonTest() throws JSONException {
        LiAuthorizationException inValidRequestLiAuthorizationException
                = LiAuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST;
        JSONObject jsonObj = inValidRequestLiAuthorizationException.toJson();
        assertEquals(1, jsonObj.get("type"));
    }
}
