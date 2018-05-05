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

import android.content.Context;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import com.lithium.community.android.sdk.TestHelper;
import com.lithium.community.android.sdk.manager.LiClientManager;
import com.lithium.community.android.sdk.manager.LiSDKManager;
import com.lithium.community.android.sdk.utils.LiCoreSDKUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.platform.Platform;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * @author kunal.shrivastava, adityasharat
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*", "javax.net.ssl.*"})
@PrepareForTest({LiClientManager.class, LiRestv2Client.class, LiRestClient.class, SSLContext.class, Platform.class,
        Request.class, LiCoreSDKUtils.class, OkHttpClient.class
})
public class LiRestv2ClientTest {

    @Test
    public void testValidateResponse() throws Exception {
        Context context = TestHelper.createMockContext();

        SSLContext sslContext = PowerMockito.mock(SSLContext.class);

        SSLSocketFactory socketFactory = mock(SSLSocketFactory.class);
        when(sslContext.getSocketFactory()).thenReturn(socketFactory);

        PowerMockito.mockStatic(SSLContext.class);
        Mockito.when(SSLContext.getInstance(anyString())).thenReturn(sslContext);

        LiSDKManager.initialize(context, TestHelper.getTestAppCredentials());
        LiRestv2Client.initialize(LiSDKManager.getInstance());
        LiRestv2Client client = LiRestv2Client.getInstance();

        final LiBaseResponse liBaseResponse = mock(LiBaseResponse.class);
        when(liBaseResponse.getHttpCode()).thenReturn(200);

        LiRestv2Client spy = spy(client);
        doReturn(liBaseResponse).when(spy).processSync(isA(LiBaseRestRequest.class));

        String liql = "select * from messages";
        LiRestV2Request request = new LiRestV2Request(context, liql, "message");
        request.addQueryParam("test");

        LiBaseResponse response = spy.processSync(request);

        Assert.assertEquals(200, response.getHttpCode());
        PowerMockito.verifyStatic();
    }
}
