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

package lithium.community.android.sdk.utils;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kunal.shrivastava on 12/1/16.
 */

public class LiUriLiCoreSDKUtilsTest {
    private static final String URI = "http://localhost:8080/?q=1";
    private static final String URI_NO_QUERY = "http://localhost:8080/";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {

    }

    @Test
    public void testBuildUrlFromString() throws MalformedURLException {
        Assert.assertEquals(new URL(URI), LiUriUtils.buildUrlFromString(URI));
    }

    @Test
    public void testBuildUrlFromStringForException() throws MalformedURLException {
        thrown.expect(MalformedURLException.class);
        LiUriUtils.buildUrlFromString("test");
    }


    @Test
    public void testParseUriIfAvailable() {
        Assert.assertEquals(Uri.parse(URI), LiUriUtils.parseUriIfAvailable(URI));
    }

    @Test
    public void testParseUriIfAvailableForNullValue() {
        Assert.assertEquals(null, LiUriUtils.parseUriIfAvailable(null));
    }

    @Test
    public void testGetLongQueryParameter() {
        Assert.assertEquals(new Long(1), LiUriUtils.getLongQueryParameter(Uri.parse(URI), "q"));
    }

    @Test
    public void testGetLongQueryParameterForNull() {
        Assert.assertEquals(null, LiUriUtils.getLongQueryParameter(Uri.parse(URI_NO_QUERY), "q"));
    }

    @Test
    public void testGetUriIfDefined() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String uri = "http://localhost:8080";
        jsonObject.put("hello", uri);
        Assert.assertEquals(uri, LiUriUtils.getUri(jsonObject, "hello").toString());
    }

    @Test
    public void testAppendQueryParameterIfNotNullForNullValue() {
        Uri.Builder builder = new Uri.Builder();
        LiUriUtils.appendQueryParameterIfNotNull(builder, "test", null);
        Assert.assertEquals("", builder.toString());
    }

    @Test
    public void testAppendQueryParameterIfNotNull() {
        Uri.Builder builder = new Uri.Builder();
        LiUriUtils.appendQueryParameterIfNotNull(builder, "test", "test");
        Assert.assertEquals("?test=test", builder.toString());
    }

}
