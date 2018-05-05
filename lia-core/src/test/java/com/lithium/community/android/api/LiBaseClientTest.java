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

package com.lithium.community.android.api;

import android.content.Context;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.LinkedList;

import com.lithium.community.android.api.LiBaseClient;
import com.lithium.community.android.api.LiBaseGetClient;
import com.lithium.community.android.TestHelper;

import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiClientResponse;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.rest.LiRestv2Client;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author kunal.shrivastava
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest({LiRestv2Client.class, LiBaseClient.class, LiClientManager.class, LiBaseResponse.class, LiMessage.class, LiGetClientResponse.class})
public class LiBaseClientTest {
    private static final String LI_ARTICLES_LIQL = "SELECT id, subject, post_time, kudos.sum(weight), conversation.style, conversation.solved FROM messages";
    private static final String LI_ARTICLES_CLIENT_TYPE = "message";
    private static final String LI_ARTICLES_QUERY_SETTINGS_TYPE = "article";

    private LiRestv2Client liRestv2Client;
    private Context mContext;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        mContext = TestHelper.createMockContext();

        //noinspection deprecation for test
        LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        liRestv2Client = mock(LiRestv2Client.class);

        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
        PowerMockito.mockStatic(LiClientManager.class);
    }

    @Test
    public void testBaseGetClientCreation() throws LiRestResponseException {
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERY_SETTINGS_TYPE,
                LiMessage.class);
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.type);
        Assert.assertEquals(LI_ARTICLES_QUERY_SETTINGS_TYPE, liClient.querySettingsType);
        PowerMockito.verifyStatic();
    }

    @Test
    public void testSetLiRestV2Request() throws LiRestResponseException {
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERY_SETTINGS_TYPE,
                LiMessage.class);
        liClient.setLiRestV2Request();
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.liRestV2Request.getType());
        PowerMockito.verifyStatic();
    }

    @Test
    public void testGetGson() throws LiRestResponseException {
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERY_SETTINGS_TYPE,
                LiMessage.class);
        Assert.assertEquals(null, liClient.getGson());
        PowerMockito.verifyStatic();
    }

    @Test
    public void testGetGsonForNull() throws LiRestResponseException {
        LiRestv2Client liRestv2Client = null;
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERY_SETTINGS_TYPE, LiMessage.class);
        Assert.assertEquals(null, liClient.getGson());
        PowerMockito.verifyStatic();
    }

    @Test
    public void testProcessSync() throws LiRestResponseException {
        LiBaseResponse liBaseResponse = mock(LiBaseResponse.class);
        ArrayList<LiBaseModel> arrayList = new ArrayList<>();

        LiMessage liMessage = new LiMessage();
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue("test");
        liMessage.setBody(liString);
        arrayList.add(liMessage);

        PowerMockito.when(liRestv2Client.processSync(isA(LiBaseRestRequest.class))).thenReturn(liBaseResponse);
        PowerMockito.when(liBaseResponse.toEntityList(eq(LI_ARTICLES_CLIENT_TYPE), eq(LiMessage.class), isA(Gson.class))).thenReturn(arrayList);
        PowerMockito.when(liBaseResponse.getHttpCode()).thenReturn(200);
        PowerMockito.when(liBaseResponse.getMessage()).thenReturn("success");
        PowerMockito.when(liBaseResponse.getStatus()).thenReturn("success");
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERY_SETTINGS_TYPE, LiMessage.class);
        LiClientResponse response = liClient.processSync();
        LinkedList<LiMessage> responseList = (LinkedList<LiMessage>) response.getResponse();
        Assert.assertEquals(200, response.getHttpCode());
        Assert.assertEquals("success", response.getStatus());
        Assert.assertEquals("success", response.getMessage());
        PowerMockito.verifyStatic();
    }

    @Test
    public void testProcessAsync() throws LiRestResponseException {

        final LiBaseRestRequest liBaseRestRequest = mock(LiBaseRestRequest.class);
        LiBaseClient liBaseClient = mock(LiBaseClient.class);
        final LiGetClientResponse liGetClientResponse = mock(LiGetClientResponse.class);
        when(liGetClientResponse.getHttpCode()).thenReturn(200);
        final LiBaseResponse liBaseResponse = mock(LiBaseResponse.class);
        when(liBaseResponse.getHttpCode()).thenReturn(200);
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        LiAsyncRequestCallback<LiBaseResponse> callback = (LiAsyncRequestCallback<LiBaseResponse>) invocation.getArguments()[1];
                        callback.onSuccess(liBaseRestRequest, liBaseResponse);
                        return null;
                    }
                }
        ).when(liRestv2Client).processAsync(isA(LiBaseRestRequest.class), any(LiAsyncRequestCallback.class));

        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERY_SETTINGS_TYPE,
                LiMessage.class);
        liClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
            @Override
            public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
                if (response != null && response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                    Assert.assertEquals(200, response.getHttpCode());
                }
            }

            @Override
            public void onError(Exception exception) {

            }
        });

    }


}