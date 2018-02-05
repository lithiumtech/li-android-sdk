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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.auth.LiRefreshTokenRequest;
import lithium.community.android.sdk.auth.LiSSOAuthorizationRequest;
import lithium.community.android.sdk.auth.LiSSOTokenRequest;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiSDKManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
    private Activity mContext;
    private SharedPreferences mMockSharedPreferences;
    private Resources resource;
    private LiSDKManager liSDKManager;

    @Before
    public void setUpTest() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mContext = Mockito.mock(Activity.class);
        mMockSharedPreferences = Mockito.mock(SharedPreferences.class);
        resource = Mockito.mock(Resources.class);
        Mockito.when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        Mockito.when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        Mockito.when(mContext.getResources()).thenReturn(resource);
        Mockito.when(resource.getBoolean(anyInt())).thenReturn(true);
        liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
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
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
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
        } catch (IOException | LiRestResponseException ignored) {
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
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
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
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
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
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
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
        } catch (IOException | LiRestResponseException ignored) {
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
