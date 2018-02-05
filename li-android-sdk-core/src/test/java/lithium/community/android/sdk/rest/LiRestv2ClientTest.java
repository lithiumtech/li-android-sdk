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
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import okhttp3.Request;
import okhttp3.internal.platform.Platform;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
/*
  Created by kunal.shrivastava on 12/07/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LiClientManager.class, LiRestv2Client.class, LiRestClient.class, SSLContext.class, Platform.class,
        Request.class, LiCoreSDKUtils.class
})
public class LiRestv2ClientTest {

    private static String liql = "select * from messages";
    private Activity context;

    @Test
    public void testGetInstance() {
//        LiRestv2Client liRestv2Client = LiRestv2Client.getInstance();
//        Assert.assertTrue(liRestv2Client.getGson() != null);
    }

    @Test
    public void testValidateResponse() throws Exception {
        context = Mockito.mock(Activity.class);
        PowerMockito.mockStatic(LiClientManager.class);
        LiClientManager liClientManager = PowerMockito.mock(LiClientManager.class);

        PowerMockito.mockStatic(SSLContext.class);
        SSLContext sslContext = PowerMockito.mock(SSLContext.class);
        when(sslContext.getInstance("SSL")).thenReturn(sslContext);
        Mockito.doNothing().when(sslContext).init(isA(KeyManager[].class), isA(TrustManager[].class),
                isA(SecureRandom.class));
        SSLSocketFactory socketFactory = mock(SSLSocketFactory.class);
        when(sslContext.getSocketFactory()).thenReturn(socketFactory);

        PowerMockito.mockStatic(Platform.class);
        Platform platform = PowerMockito.mock(Platform.class);
        X509TrustManager trustManager = mock(X509TrustManager.class);
        when(platform.trustManager(socketFactory)).thenReturn(trustManager);
        BDDMockito.given(Platform.get()).willReturn(platform);

        BDDMockito.given(SSLContext.getInstance("SSL")).willReturn(sslContext);

        LiRestv2Client liRestv2Client = LiRestv2Client.getInstance();
        final LiBaseResponse liBaseResponse = mock(LiBaseResponse.class);
        when(liBaseResponse.getHttpCode()).thenReturn(200);
        LiRestv2Client liRestv2ClientSpy = spy(LiRestv2Client.class);
        doReturn(liBaseResponse).when(liRestv2ClientSpy).processSync(isA(LiBaseRestRequest.class));

        LiRestV2Request liBaseRestRequest = new LiRestV2Request(context, liql, "message");
        liBaseRestRequest.addQueryParam("test");

        LiBaseResponse liBaseResponse1 = liRestv2ClientSpy.processSync(liBaseRestRequest);

        Assert.assertEquals(200, liBaseResponse1.getHttpCode());
        PowerMockito.verifyStatic();
    }
}

