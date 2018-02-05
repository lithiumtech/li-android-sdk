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

package lithium.community.android.sdk.rest;

import android.app.Activity;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import static org.mockito.Mockito.mock;

/**
 * Created by shoureya.kant on 12/10/16.
 */

public class LiBaseRestRequestTest {

    private LiBaseRestRequest.RestMethod method;
    private RequestBody requestBody;
    private Map<String, String> additionalHttpHeaders;
    private String path;
    private boolean isAuthenticatedRequest = false;
    private LiBaseRestRequest liBaseRestRequest;

    @Mock
    private Activity mContext;


    public void setUpTest() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mContext = mock(Activity.class);
    }

    @Test
    public void getParamsTest() {
        method = LiBaseRestRequest.RestMethod.POST;
        requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json; charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {

            }
        };
        additionalHttpHeaders = new HashMap<>();
        additionalHttpHeaders.put("test", "test");
        path = "path";
        liBaseRestRequest = new LiBaseRestRequest(mContext, method, path, requestBody, additionalHttpHeaders,
                isAuthenticatedRequest);
        liBaseRestRequest.addQueryParam("param=param");
        Assert.assertEquals(method, liBaseRestRequest.getMethod());
        Assert.assertEquals(requestBody, liBaseRestRequest.getRequestBody());
        Assert.assertEquals(additionalHttpHeaders, liBaseRestRequest.getAdditionalHttpHeaders());
        Assert.assertEquals(path, liBaseRestRequest.getPath());
        Assert.assertEquals(isAuthenticatedRequest, liBaseRestRequest.isAuthenticatedRequest());
        Assert.assertEquals("param", liBaseRestRequest.getQueryParams().get("param"));
        liBaseRestRequest.addRequestHeader("header", "header");
        Assert.assertEquals("header", liBaseRestRequest.getAdditionalHttpHeaders().get("header"));
        liBaseRestRequest.setPath("path1");
        Assert.assertEquals("path1", liBaseRestRequest.getPath());
    }
}
