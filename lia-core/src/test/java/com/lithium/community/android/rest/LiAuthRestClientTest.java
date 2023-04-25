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

package com.lithium.community.android.rest;

import android.content.Context;
import android.net.Uri;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import com.lithium.community.android.rest.LiAuthAsyncRequestCallback;
import com.lithium.community.android.rest.LiAuthRestClient;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.TestHelper;
import com.lithium.community.android.auth.LiRefreshTokenRequest;
import com.lithium.community.android.auth.LiSSOAuthorizationRequest;
import com.lithium.community.android.auth.LiSSOTokenRequest;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by saiteja.tokala on 12/15/16.
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest({LiAuthRestClient.class, OkHttpClient.class, Response.class, ResponseBody.class})
public class LiAuthRestClientTest {
    public static final String CLIENT_ID = "clientId";
    public static final String SSO_TOKEN = "ssoToken";
    public static final String STATE = "state";
    public static final String URI = "http://example.com/example/auth/authorize";
    public static final String REDIRECT_URI = "redirectUri";
    public static final String ITS_A_TEST_MESSAGE = "Its A Test message";
    public static final String EXCEPTION_CAUSE = "Exception Cause";
    public static final String ACCESS_CODE = "accessCode";
    public static final String GRANT_TYPE = "grantType";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String REFRESH_TOKEN = "refreshToken";

    @Mock
    private Context mContext;

    @Before
    public void setUpTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        mContext = TestHelper.createMockContext();
        LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
    }


    @Test
    public void authorizeAsyncFailureCallback() {
        LiSSOAuthorizationRequest ssoAuthReq = new LiSSOAuthorizationRequest();
        ssoAuthReq.setClientId(CLIENT_ID);
        ssoAuthReq.setSsoToken(SSO_TOKEN);
        ssoAuthReq.setState(STATE);
        ssoAuthReq.setUri(URI);
        ssoAuthReq.setRedirectUri(REDIRECT_URI);
        LiAuthAsyncRequestCallback callback = new LiAuthAsyncRequestCallback() {
            @Override
            public void onSuccess(Object response) throws LiRestResponseException {
                System.out.println(response);
                Assert.assertEquals(ITS_A_TEST_MESSAGE, ((LiBaseResponse) response).getMessage());
                System.out.println("-----ITS SUCCESS-----" + ((LiBaseResponse) response).getMessage());
            }

            @Override
            public void onError(Exception exception) {
                System.out.println(exception.getMessage());
                Assert.assertEquals("Error authorizeAsync", exception.getMessage());
                System.out.println("-----ITS FAILURE-----" + exception.getMessage());
            }
        };

        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient = spy(new LiAuthRestClient());
        final Call call = mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient(mContext);
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class), isA(OkHttpClient.class));
        LiBaseResponse liBaseResponse = new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body = mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        try {
            doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(response);
        } catch (IOException ignored) {
        }
        final IOException excep = mock(IOException.class);
        when(excep.getMessage()).thenReturn("Error authorizeAsync");
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(call, finalResponse);
                        callback.onFailure(call, excep);
                        return null;

                    }
                }
        ).when(call).enqueue(any(Callback.class));

        try {
            liAuthRestClient.authorizeAsync(mContext, ssoAuthReq, callback);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void accessTokenAsyncSuccess() {
        LiSSOTokenRequest liSSOTokenRequest = new LiSSOTokenRequest();
        liSSOTokenRequest.setAccessCode(ACCESS_CODE);
        liSSOTokenRequest.setRedirectUri(REDIRECT_URI);
        liSSOTokenRequest.setUri(Uri.parse(URI));
        liSSOTokenRequest.setGrantType(GRANT_TYPE);
        liSSOTokenRequest.setClientId(CLIENT_ID);
        liSSOTokenRequest.setClientSecret(CLIENT_SECRET);

        LiAuthAsyncRequestCallback callback = new LiAuthAsyncRequestCallback() {
            @Override
            public void onSuccess(Object response) throws LiRestResponseException {
                System.out.println(response);
                Assert.assertEquals(ITS_A_TEST_MESSAGE, ((LiBaseResponse) response).getMessage());
                System.out.println("-----ITS SUCCESS-----" + ((LiBaseResponse) response).getMessage());
            }

            @Override
            public void onError(Exception exception) {
                System.out.println(exception.getMessage());
                Assert.assertEquals("Error accessTokenAsync", exception.getMessage());
                System.out.println("-----ITS FAILURE-----" + exception.getMessage());
            }
        };

        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient = spy(new LiAuthRestClient());
        final Call call = mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient(mContext);
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class), isA(OkHttpClient.class));
        LiBaseResponse liBaseResponse = new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body = mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        try {
            doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(finalResponse);
        } catch (Exception ignored) {
        }
        final IOException excep = mock(IOException.class);
        when(excep.getMessage()).thenReturn("Error accessTokenAsync");
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(call, finalResponse);
                        callback.onFailure(call, excep);
                        return null;

                    }
                }
        ).when(call).enqueue(any(Callback.class));

        try {
            liAuthRestClient.accessTokenAsync(mContext, liSSOTokenRequest, callback);
        } catch (Exception ignored) {
        }
    }

    @Test
    public void refreshTokenAsyncTest() {
        LiRefreshTokenRequest liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientSecret(CLIENT_SECRET);
        liRefreshTokenRequest.setGrantType(GRANT_TYPE);
        liRefreshTokenRequest.setClientId(CLIENT_ID);
        liRefreshTokenRequest.setRefreshToken(REFRESH_TOKEN);
        liRefreshTokenRequest.setUri(Uri.parse(URI));
        LiAuthAsyncRequestCallback callback = new LiAuthAsyncRequestCallback() {
            @Override
            public void onSuccess(Object response) throws LiRestResponseException {
                System.out.println(response);
                Assert.assertEquals(ITS_A_TEST_MESSAGE, ((LiBaseResponse) response).getMessage());
                System.out.println("-----ITS SUCCESS-----" + ((LiBaseResponse) response).getMessage());
            }

            @Override
            public void onError(Exception exception) {
                System.out.println(exception.getMessage());
                Assert.assertEquals("Error refreshTokenAsync", exception.getMessage());
                System.out.println("-----ITS FAILURE-----" + exception.getMessage());
            }
        };

        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient = spy(new LiAuthRestClient());
        final Call call = mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient(mContext);
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class), isA(OkHttpClient.class));
        LiBaseResponse liBaseResponse = new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body = mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        try {
            doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(finalResponse);
        } catch (Exception ignored) {
        }
        final IOException excep = mock(IOException.class);
        when(excep.getMessage()).thenReturn("Error refreshTokenAsync");
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(call, finalResponse);
                        callback.onFailure(call, excep);
                        return null;

                    }
                }
        ).when(call).enqueue(any(Callback.class));
        try {
            liAuthRestClient.refreshTokenAsync(mContext, liRefreshTokenRequest, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void refreshTokenSyncTest() {
        LiRefreshTokenRequest liRefreshTokenRequest = new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientSecret(CLIENT_SECRET);
        liRefreshTokenRequest.setGrantType(GRANT_TYPE);
        liRefreshTokenRequest.setClientId(CLIENT_ID);
        liRefreshTokenRequest.setRefreshToken(REFRESH_TOKEN);
        liRefreshTokenRequest.setUri(Uri.parse(URI));
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient = spy(new LiAuthRestClient());
        final Call call = mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient(mContext);
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class), isA(OkHttpClient.class));

        LiBaseResponse liBaseResponse = new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response = mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body = mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        try {
            doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(finalResponse);
        } catch (IOException ignored) {
        }
        final IOException excep = mock(IOException.class);
        when(excep.getMessage()).thenReturn(EXCEPTION_CAUSE);
        try {
            when(call.execute()).thenReturn(response);
            LiBaseResponse resp = liAuthRestClient.refreshTokenSync(mContext, liRefreshTokenRequest);
            Assert.assertEquals(resp.getHttpCode(), 200);
            Assert.assertEquals(resp.getMessage(), ITS_A_TEST_MESSAGE);
        } catch (IOException | LiRestResponseException ignored) {
        }
    }
}
