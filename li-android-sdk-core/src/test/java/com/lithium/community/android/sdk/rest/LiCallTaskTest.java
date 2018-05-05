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

package com.lithium.community.android.sdk.rest;

/**
 * Created by shoureya.kant on 12/5/16.
 */

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import com.lithium.community.android.sdk.exception.LiRestResponseException;
import com.lithium.community.android.sdk.manager.LiClientManager;
import com.lithium.community.android.sdk.rest.LiCallTask;
import com.lithium.community.android.sdk.rest.LiRestv2Client;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Call.class, Response.class, Request.class, Protocol.class, LiClientManager.class, Gson.class})
public class LiCallTaskTest {

    private Call call;
    private LiCallTask callTask;

    @Before
    public void setUp() throws IOException {

        LiRestv2Client liRestClient = PowerMockito.mock(LiRestv2Client.class);

        String json = "{\n" +
                "  \"status\": \"success\",\n" +
                "  \"message\": \"\",\n" +
                "  \"http_code\": 200,\n" +
                "  \"data\": {\n" +
                "    \"type\": \"messages\",\n" +
                "    \"list_item_type\": \"message\",\n" +
                "    \"size\": 1,\n" +
                "    \"items\": [\n" +
                "      {\n" +
                "        \"type\": \"message\",\n" +
                "        \"conversation\": {\n" +
                "          \"type\": \"conversation\",\n" +
                "          \"last_post_time\": \"2016-06-22T08:46:02.737-07:00\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"metadata\": {}\n" +
                "}";
        byte[] bytes = json.getBytes();

        ResponseBody body = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), bytes);
        Request request = PowerMockito.mock(Request.class);

        Protocol protocol = PowerMockito.mock(Protocol.class);

        Response.Builder builder = new Response.Builder();
        builder.code(200);
        builder.message("test");
        builder.body(body);
        builder.request(request);
        builder.protocol(protocol);
        Response response = builder.build();

        PowerMockito.mockStatic(LiClientManager.class);

        LiClientManager liClientManager = mock(LiClientManager.class);
        when(liClientManager.getRestClient()).thenReturn(liRestClient);
        when(liRestClient.getGson()).thenReturn(new Gson());

        call = PowerMockito.mock(Call.class);
        callTask = new LiCallTask(call);

        when(call.execute()).thenReturn(response);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void executeTest() throws LiRestResponseException {
        Assert.assertEquals(200, callTask.execute().getHttpCode());
        PowerMockito.verifyStatic();
    }

    @Test(expected = LiRestResponseException.class)
    public void testException() throws LiRestResponseException, IOException {
        when(call.execute()).thenThrow(new IOException());
        callTask.execute();
    }

}
