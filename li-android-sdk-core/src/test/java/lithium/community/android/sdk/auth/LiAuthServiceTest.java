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

package lithium.community.android.sdk.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.api.LiBaseGetClient;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.rest.LiAuthRestClient;
import lithium.community.android.sdk.rest.LiBaseResponse;
import lithium.community.android.sdk.rest.LiRestV2Request;
import lithium.community.android.sdk.rest.LiRestv2Client;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiSystemClock;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author saiteja.tokala
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PowerMockIgnore({"javax.crypto.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({Activity.class, LiAuthService.class, LiAuthServiceImpl.class, LiAuthRestClient.class,
        OkHttpClient.class, Response.class, Gson.class, JsonObject.class, JsonElement.class, LiSystemClock.class,
        LiRestv2Client.class, LiBaseGetClient.class, LiRestV2Request.class, OkHttpClient.class})
public class LiAuthServiceTest {

    public static final String RANDOM_STATE = "randomState";
    public static final String SSO_TOKEN = null;
    public static final String SSO_TOKEN1 = "SSoToken";
    public static final String TEST_AUTH_CODE = "testAuthCode";
    public static final String TEST_API_PROXY_HOST = "testApiProxyHost";
    public static final String TEST_TENANT_ID = "testTenantId";
    public static final String TEST_USER_ID = "testUserId";
    public static final String ACCESS_TOKEN = "o5IV0yIiNDj/5lNJ6doJh08LX6SsDwtkDXDVmhGvRtI=";

    public static final Long TEST_EXPIRES_IN = 86400L;
    public static final String TEST_LITHIUM_USER_ID = "testLithiumUserId";
    public static final String TEST_REFRESH_TOKEN = "testRefreshToken";
    public static final String TEST_TOKEN_TYPE = "testTokenType";
    public static final String TEST_ACCES_TOKEN = "testAccesToken";
    public static final long TEST_EXPIRES_AT = 86400001L;
    public static final String SSO_AUTH_URL = "SSOAuthUrl";


    @Mock
    private Context mContext;
    private LiAppCredentials liAppCredentials;


    @Before
    public void setupRequest() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
        mContext = TestHelper.createMockContext();
        liAppCredentials = TestHelper.getTestAppCredentials();
        LiSDKManager.init(mContext, liAppCredentials);
        LiRestv2Client liRestv2Client = PowerMockito.mock(LiRestv2Client.class);
        when(liRestv2Client.getGson()).thenReturn(new Gson());
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
    }

    @Test
    public void startLoginFlowTest() throws MalformedURLException, LiRestResponseException, URISyntaxException, IllegalAccessException {
        LiSSOAuthorizationRequest authorizationRequest;
        authorizationRequest = new LiSSOAuthorizationRequest();
        authorizationRequest.setClientId(liAppCredentials.getClientKey());
        authorizationRequest.setRedirectUri(liAppCredentials.getRedirectUri());
        authorizationRequest.setState(RANDOM_STATE);
        authorizationRequest.setUri(String.valueOf(liAppCredentials.getAuthorizeUri()));
        Field field = PowerMockito.field(LiSystemClock.class, "INSTANCE");
        LiSystemClock mock = mock(LiSystemClock.class);
        field.set(LiSystemClock.class, mock);
        when(mock.getCurrentTimeMillis()).thenReturn(1L);
        //
        LiAuthService liAuthService = new LiAuthServiceImpl(mContext);
        liAuthService = spy(liAuthService);
        doNothing().when(liAuthService).dispose();
        doNothing().when(liAuthService).performAuthorizationRequest(authorizationRequest);
        liAuthService.startLoginFlow();
        verify(liAuthService, times(1)).performAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        verify(liAuthService, times(0)).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
    }

    @Test
    public void startLoginFlowSSOTest() throws MalformedURLException, LiRestResponseException, URISyntaxException,
            IllegalAccessException {

        LiSSOAuthorizationRequest authorizationRequest;
        authorizationRequest = new LiSSOAuthorizationRequest();
        authorizationRequest.setClientId(liAppCredentials.getClientKey());
        authorizationRequest.setRedirectUri(liAppCredentials.getRedirectUri());
        authorizationRequest.setState(RANDOM_STATE);
        authorizationRequest.setUri(String.valueOf(liAppCredentials.getAuthorizeUri()));
        Field field = PowerMockito.field(LiSystemClock.class, "INSTANCE");
        LiSystemClock mock = mock(LiSystemClock.class);
        field.set(LiSystemClock.class, mock);
        when(mock.getCurrentTimeMillis()).thenReturn(1L);
        //
        LiAuthService liAuthService = new LiAuthServiceImpl(mContext);
        liAuthService = spy(liAuthService);
        doNothing().when(liAuthService).dispose();
        doNothing().when(liAuthService).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        liAuthService.startLoginFlow(SSO_TOKEN1);
        verify(liAuthService, times(0)).performAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        verify(liAuthService, times(1)).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
    }

    @Test
    public void performAuthorizationRequestTest() throws Exception {
        LiSSOAuthorizationRequest authorizationRequest;
        authorizationRequest = new LiSSOAuthorizationRequest();
        authorizationRequest.setClientId(liAppCredentials.getClientKey());
        authorizationRequest.setRedirectUri(liAppCredentials.getRedirectUri());
        authorizationRequest.setState(RANDOM_STATE);
        authorizationRequest.setUri(String.valueOf(liAppCredentials.getAuthorizeUri()));
        Field field = PowerMockito.field(LiSystemClock.class, "INSTANCE");
        LiSystemClock mock = mock(LiSystemClock.class);
        field.set(LiSystemClock.class, mock);
        when(mock.getCurrentTimeMillis()).thenReturn(1L);
        //
        LiAuthService liAuthService = new LiAuthServiceImpl(mContext);
        //liAuthService=spy(liAuthService);

        liAuthService = spy(liAuthService);
        doNothing().when(liAuthService).dispose();
        doNothing().when(liAuthService, "checkIfDisposed");
        liAuthService.startLoginFlow(SSO_TOKEN);
        doNothing().when(mContext).startActivity(isA(Intent.class));
        verify(mContext, Mockito.atLeastOnce()).startActivity(isA(Intent.class));
        verify(liAuthService, times(1)).performAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        verify(liAuthService, times(0)).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));

    }

    @Test
    public void aperformAuthorizationRequestSSOTest() throws Exception {
        LiSSOAuthorizationRequest authorizationRequest;
        authorizationRequest = new LiSSOAuthorizationRequest();
        authorizationRequest.setClientId(liAppCredentials.getClientKey());
        authorizationRequest.setRedirectUri(liAppCredentials.getRedirectUri());
        authorizationRequest.setState(RANDOM_STATE);
        authorizationRequest.setUri(String.valueOf(liAppCredentials.getAuthorizeUri()));

        Field field = PowerMockito.field(LiSystemClock.class, "INSTANCE");
        LiSystemClock mock = mock(LiSystemClock.class);

        field.set(LiSystemClock.class, mock);
        when(mock.getCurrentTimeMillis()).thenReturn(1L);

        LiAuthService liAuthService = new LiAuthServiceImpl(mContext);
        liAuthService = spy(liAuthService);
        doNothing().when(liAuthService, "checkIfDisposed");
        doNothing().when(liAuthService, "dispose");

        LiAuthRestClient liAuthRestClient = new LiAuthRestClient();
        PowerMockito.doReturn(liAuthRestClient).when(liAuthService, "getLiAuthRestClient");
        spy(liAuthRestClient);

        final LiBaseResponse liBaseResponse = mock(LiBaseResponse.class);
        when(liBaseResponse.getHttpCode()).thenReturn(200);

        JsonObject dummyJsonData = mock(JsonObject.class);
        when(liBaseResponse.getData()).thenReturn(dummyJsonData);

        JsonElement jsonStringForBaseResponse = mock(JsonElement.class);
        when(liBaseResponse.getData().get("data")).thenReturn(jsonStringForBaseResponse);
        when(liBaseResponse.getData().get("response")).thenReturn(jsonStringForBaseResponse);
        when(liBaseResponse.getData().get("response").getAsJsonObject()).thenReturn(dummyJsonData);
        when(liBaseResponse.getData().get("response").getAsJsonObject().get("data")).thenReturn(jsonStringForBaseResponse);

        LiSSOAuthResponse liSSOAuthResponse = mock(LiSSOAuthResponse.class);
        when(liSSOAuthResponse.getState()).thenReturn(RANDOM_STATE);
        when(liSSOAuthResponse.getAuthCode()).thenReturn(TEST_AUTH_CODE);
        when(liSSOAuthResponse.getApiProxyHost()).thenReturn(TEST_API_PROXY_HOST);
        when(liSSOAuthResponse.getTenantId()).thenReturn(TEST_TENANT_ID);
        when(liSSOAuthResponse.getUserId()).thenReturn(TEST_USER_ID);
        when(liSSOAuthResponse.getJsonString()).thenReturn("{\"code\":\"testAuthCode\",\"proxy-host\":\"testApiProxyHost\",\"state\":\"randomState\","
                + "\"tenant-id\":\"testTenantId\",\"user-id\":\"testUserId\"}");

        PowerMockito.doReturn(liSSOAuthResponse).when(liAuthService, "getLiSSOAuthResponse", liBaseResponse);
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        PowerMockito.whenNew(OkHttpClient.class).withNoArguments().thenReturn(okHttpClient);
        final Call call = mock(Call.class);

        final Response response = mock(Response.class);
        when(response.code()).thenReturn(200);
        ResponseBody body = mock(ResponseBody.class);
        when(response.body()).thenReturn(body);
        when(okHttpClient.newCall(isA(Request.class))).thenReturn(call);

        PowerMockito.whenNew(LiBaseResponse.class).withArguments(response).thenReturn(liBaseResponse);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                Callback callback = (Callback) invocation.getArguments()[0];
                callback.onResponse(call, response);
                return null;
            }
        }).when(call).enqueue(any(Callback.class));

        LiTokenResponse liTokenResponse = mock(LiTokenResponse.class);
        when(liTokenResponse.getUserId()).thenReturn(TEST_USER_ID);
        when(liTokenResponse.getExpiresIn()).thenReturn(TEST_EXPIRES_IN);
        when(liTokenResponse.getLithiumUserId()).thenReturn(TEST_LITHIUM_USER_ID);
        when(liTokenResponse.getRefreshToken()).thenReturn(TEST_REFRESH_TOKEN);
        when(liTokenResponse.getTokenType()).thenReturn(TEST_TOKEN_TYPE);
        when(liTokenResponse.getAccessToken()).thenReturn(TEST_ACCES_TOKEN);
        when(liTokenResponse.getJsonString()).thenReturn("{\"lithiumUserId\":\"testLithiumUserId\",\"access_token\":\"testAccesToken\","
                + "\"refresh_token\":\"testRefreshToken\",\"expires_in\":86400,\"userId\":\"testUserId\","
                + "\"token_type\":\"testTokenType\",\"expiresAt\":86400001}");
        when(liTokenResponse.getExpiresAt()).thenReturn(TEST_EXPIRES_AT);


        Gson gson = mock(Gson.class);
        PowerMockito.whenNew(Gson.class).withNoArguments().thenReturn(gson);
        when(gson.fromJson(isA(JsonElement.class), eq(LiSSOAuthResponse.class))).thenReturn(liSSOAuthResponse);
        when(gson.fromJson(isA(JsonElement.class), eq(LiTokenResponse.class))).thenReturn(liTokenResponse);

        when(gson.toJson(isA(LiSSOAuthorizationRequest.class))).thenReturn("LiSSOAuthorizationRequestString");
        when(gson.toJson(isA(LiSSOTokenRequest.class))).thenReturn("LiSSOTokenRequestString");


        PowerMockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) {
                LiAuthService.LoginCompleteCallBack callback = (LiAuthService.LoginCompleteCallBack) invocation.getArguments()[0];
                callback.onLoginComplete(null, true);
                return null;
            }
        }).when(liAuthService).getUserAfterTokenResponse(isA(LiAuthService.LoginCompleteCallBack.class));

        doNothing().when(mContext).startActivity(isA(Intent.class));
        doNothing().when(mContext).startActivity(isA(Intent.class));
        liAuthService.startLoginFlow(SSO_TOKEN1);
    }

    @Test
    public void performSyncRefreshTokenRequestTest() throws LiRestResponseException {
        LiAuthServiceImpl liAuthService = new LiAuthServiceImpl(mContext);
        liAuthService = spy(liAuthService);
        LiAuthRestClient liAuthRestClient = spy(new LiAuthRestClient());
        doReturn(liAuthRestClient).when(liAuthService).getLiAuthRestClient();


        String refreshtokenResponse = "{\n" +
                "  \"data\": {\n" +
                "      \"status\": \"success\",\n" +
                "      \"message\": \"OK\",\n" +
                "      \"http_code\": 200,\n" +
                "      \"data\": {\n" +
                "        \"access_token\": \"" + ACCESS_TOKEN + "\",\n" +
                "        \"expires_in\": 86400,\n" +
                "        \"lithiumUserId\": \"2d8c95ed-21dc-4ba6-ab9f-d3eff9c928ce\",\n" +
                "        \"refresh_token\": \"NJIjGeQP3rsdE2Wz46gFExlWdLpwv1kRompX5szrnXY=\",\n" +
                "        \"token_type\": \"bearer\"\n" +
                "      }\n" +
                "  }\n" +
                "}";
        Gson gson = new Gson();
        LiBaseResponse liBaseResponse = gson.fromJson(refreshtokenResponse, LiBaseResponse.class);
        doReturn(liBaseResponse).when(liAuthRestClient).refreshTokenSync(isA(Context.class),
                isA(LiRefreshTokenRequest.class));

        long atLeast = LiCoreSDKUtils.addCurrentTime(86400L);
        LiTokenResponse tokenResponse = liAuthService.performSyncRefreshTokenRequest();
        assert (tokenResponse.getAccessToken().equals(ACCESS_TOKEN));
        Assert.assertEquals(tokenResponse.getExpiresIn(), 86400L);
        Assert.assertTrue("expiry in", tokenResponse.getExpiresAt() >= atLeast);
    }

    @Test
    public void performRefreshTokenRequestTest() {
        LiAuthServiceImpl liAuthService = new LiAuthServiceImpl(mContext);
        liAuthService = spy(liAuthService);
        LiAuthRestClient liAuthRestClient = spy(new LiAuthRestClient());
        doReturn(liAuthRestClient).when(liAuthService).getLiAuthRestClient();


        String refreshtokenResponse = "{\n" +
                "  \"data\": {\n" +
                "      \"status\": \"success\",\n" +
                "      \"message\": \"OK\",\n" +
                "      \"http_code\": 200,\n" +
                "      \"data\": {\n" +
                "        \"access_token\": \"" + ACCESS_TOKEN + "\",\n" +
                "        \"expires_in\": 86400,\n" +
                "        \"lithiumUserId\": \"2d8c95ed-21dc-4ba6-ab9f-d3eff9c928ce\",\n" +
                "        \"refresh_token\": \"NJIjGeQP3rsdE2Wz46gFExlWdLpwv1kRompX5szrnXY=\",\n" +
                "        \"token_type\": \"bearer\"\n" +
                "      }\n" +
                "  }\n" +
                "}";
        Gson gson = new Gson();
        LiBaseResponse liBaseResponse = gson.fromJson(refreshtokenResponse, LiBaseResponse.class);

        LiAuthService.LiTokenResponseCallback callback = new LiAuthService.LiTokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable LiTokenResponse response, @Nullable Exception ex) {
                assert (response.getAccessToken().equals(ACCESS_TOKEN));
            }
        };
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        try {
            PowerMockito.whenNew(OkHttpClient.class).withNoArguments().thenReturn(okHttpClient);
        } catch (Exception ignored) {
        }
        final Call call = mock(Call.class);
        final Response response = mock(Response.class);
        when(response.code()).thenReturn(200);
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), refreshtokenResponse);
        when(response.body()).thenReturn(responseBody);
        when(okHttpClient.newCall(isA(Request.class))).thenReturn(call);
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(call, response);
                        //callback.onFailure(isA(Call.class),new IOException("OnError"));
                        return null;

                    }
                }
        ).when(call).enqueue(any(Callback.class));
        try {
            PowerMockito.whenNew(LiBaseResponse.class).withArguments(response).thenReturn(liBaseResponse);
            liAuthService.performRefreshTokenRequest(callback);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void handleAuthorizationResponseTest() throws Exception {
        LiAuthServiceImpl liAuthService = new LiAuthServiceImpl(mContext);
        liAuthService = spy(liAuthService);
        LiAuthRestClient liAuthRestClient = spy(new LiAuthRestClient());
        doReturn(liAuthRestClient).when(liAuthService).getLiAuthRestClient();
        String refreshtokenResponse = "{\n" +
                "\t\"response\": {\n" +
                "\t\t\"httpCode\": 200,\n" +
                "\t\t\"message\": \"OK\",\n" +
                "\t\t\"status\": \"success\",\n" +
                "\t\t\"data\": {\n" +
                "\t\t\t\"lithiumUserId\": \"5e2ec7e8-1e46-40a0-a313-0034033ed685\",\n" +
                "\t\t\t\"userId\": \"567\",\n" +
                "\t\t\t\"access_token\": \"PCU3EUAaYjPIfDIanJyJKAyC+NgEp7cUNWkJP5Y6n18=\",\n" +
                "\t\t\t\"expires_in\": 86400,\n" +
                "\t\t\t\"refresh_token\": \"PkiCYDHEI/u54hUVME63QKC4pKxTxI1ifs9w2Tzsfuc=\",\n" +
                "\t\t\t\"token_type\": \"bearer\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";
        Gson gson = new Gson();
        LiBaseResponse liBaseResponse = gson.fromJson(refreshtokenResponse, LiBaseResponse.class);
        doReturn(liBaseResponse).when(liAuthRestClient).refreshTokenSync(mContext, isA(LiRefreshTokenRequest.class));

        new LiAuthService.LiTokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable LiTokenResponse response, @Nullable Exception ex) {
                assert (response.getAccessToken().equals(ACCESS_TOKEN));
            }
        };
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        PowerMockito.whenNew(OkHttpClient.class).withNoArguments().thenReturn(okHttpClient);
        Call call = mock(Call.class);
        final Response response = mock(Response.class);
        when(okHttpClient.newCall(isA(Request.class))).thenReturn(call);
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(isA(Call.class), response);
                        return null;
                    }
                }
        ).when(call).enqueue(any(Callback.class));
        PowerMockito.whenNew(LiBaseResponse.class).withArguments(response).thenReturn(liBaseResponse);
        LiSSOAuthResponse liSSOAuthResponse = mock(LiSSOAuthResponse.class);
        when(liSSOAuthResponse.getState()).thenReturn(RANDOM_STATE);
        when(liSSOAuthResponse.getAuthCode()).thenReturn(TEST_AUTH_CODE);
        when(liSSOAuthResponse.getApiProxyHost()).thenReturn(TEST_API_PROXY_HOST);
        when(liSSOAuthResponse.getTenantId()).thenReturn(TEST_TENANT_ID);
        when(liSSOAuthResponse.getUserId()).thenReturn(TEST_USER_ID);
        when(liSSOAuthResponse.getJsonString()).thenReturn("{\"code\":\"testAuthCode\",\"proxy-host\":\"testApiProxyHost\",\"state\":\"randomState\","
                + "\"tenant-id\":\"testTenantId\",\"user-id\":\"testUserId\"}");
        PowerMockito.doReturn(liSSOAuthResponse).when(liAuthService, "getLiSSOAuthResponse", liBaseResponse);


        LiAuthService.LoginCompleteCallBack loginCallBack = new LiAuthService.LoginCompleteCallBack() {
            @Override
            public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                Assert.assertEquals(true, isSuccess);
            }
        };

        liAuthService.handleAuthorizationResponse(liSSOAuthResponse, liAuthRestClient, loginCallBack);
    }
}
