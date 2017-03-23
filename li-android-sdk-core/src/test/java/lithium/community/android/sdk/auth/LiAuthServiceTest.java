package lithium.community.android.sdk.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
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
import lithium.community.android.sdk.utils.LiSystemClock;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
 * Created by saiteja.tokala on 12/8/16.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(PowerMockRunner.class)
@PrepareForTest({Activity.class,LiAuthService.class,LiAuthServiceImpl.class,LiAuthRestClient.class,OkHttpClient.class,Response.class,Gson.class,JsonObject.class,JsonElement.class,LiSystemClock.class,LiRestv2Client.class,LiBaseGetClient.class,LiRestV2Request.class})
public class LiAuthServiceTest {

    public static final String COMMUNITY_URI = "http://testuri.com";
    public static final String TEST_CLIENT_SECRET = "Ghx+iRez0TXCPkjedCNZXgmCr+S1tWfi1znbzXKu+AY=";
    public static final String TEST_CLIENT_KEY = "mj2gw0IYuoo33m0rKxuX4KpxUfsy7Q0rcBJhq34GHgs=";
    public static final boolean DEFERRED_LOGIN = false;
    public static final String RANDOM_STATE = "randomState";
    public static final String SSO_TOKEN = null;
    public static final String SSO_TOKEN1 = "SSoToken";
    public static final String TEST_AUTH_CODE = "testAuthCode";
    public static final String TEST_API_PROXY_HOST = "testApiProxyHost";
    public static final String TEST_TENANT_ID = "testTenantId";
    public static final String TEST_USER_ID = "testUserId";
    public static final Long TEST_EXPIRES_IN = 86400L;
    public static final String TEST_LITHIUM_USER_ID = "testLithiumUserId";
    public static final String TEST_REFRESH_TOKEN = "testRefreshToken";
    public static final String TEST_TOKEN_TYPE = "testTokenType";
    public static final String TEST_ACCES_TOKEN = "testAccesToken";
    public static final long TEST_EXPIRES_AT = 86400001L;
    public static final String SSO_AUTH_URL = "SSOAuthUrl";
    public static final String ACCESS_TOKEN = "o5IV0yIiNDj/5lNJ6doJh08LX6SsDwtkDXDVmhGvRtI=";


    @Before
    public void setupRequest(){


    }

    @Test
    public void startLoginFlowTest() throws MalformedURLException, LiRestResponseException, URISyntaxException, IllegalAccessException {
        LiSDKManager liSDKManager;
        LiSSOAuthorizationRequest authorizationRequest;

        SharedPreferences mMockSharedPreferences;

        Resources resource;


        Activity mContext;

        mContext = mock(Activity.class);
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_KEY).setClientSecret(TEST_CLIENT_SECRET).setCommunityUri(COMMUNITY_URI).build();
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        liSDKManager = LiSDKManager.init(mContext,liAppCredentials);

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
        LiAuthService liAuthService = new LiAuthServiceImpl(mContext, null);
        liAuthService=spy(liAuthService);
        doNothing().when(liAuthService).dispose();
        doNothing().when(liAuthService).performAuthorizationRequest(authorizationRequest);
        liAuthService.startLoginFlow();
        verify(liAuthService,times(1)).performAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        verify(liAuthService,times(0)).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
    }

    @Test
    public void startLoginFlowSSOTest() throws MalformedURLException, LiRestResponseException, URISyntaxException, IllegalAccessException {
        LiSDKManager liSDKManager;


        SharedPreferences mMockSharedPreferences;

        Resources resource;
        LiSSOAuthorizationRequest authorizationRequest;


        Activity mContext;
        mContext = mock(Activity.class);
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_KEY).setClientSecret(TEST_CLIENT_SECRET).setCommunityUri(COMMUNITY_URI).build();
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        liSDKManager = LiSDKManager.init(mContext,liAppCredentials);


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
        LiAuthService liAuthService = new LiAuthServiceImpl(mContext, SSO_TOKEN1);
        liAuthService=spy(liAuthService);
        doNothing().when(liAuthService).dispose();
        doNothing().when(liAuthService).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        liAuthService.startLoginFlow();
        verify(liAuthService,times(0)).performAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        verify(liAuthService,times(1)).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
    }

    @Test
    public void performAuthorizationRequestTest() throws Exception {
        LiSDKManager liSDKManager;


        SharedPreferences mMockSharedPreferences;

        Resources resource;
        LiSSOAuthorizationRequest authorizationRequest;

        Activity mContext;
        mContext = mock(Activity.class);
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_KEY).setClientSecret(TEST_CLIENT_SECRET).setCommunityUri(COMMUNITY_URI).build();
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        liSDKManager = LiSDKManager.init(mContext,liAppCredentials);


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
        LiAuthService liAuthService = new LiAuthServiceImpl(mContext, SSO_TOKEN);
        //liAuthService=spy(liAuthService);

        liAuthService = spy(liAuthService);
        doNothing().when(liAuthService).dispose();
        when(resource.getBoolean(anyInt())).thenReturn(true);
//        CustomTabsIntent customTabsIntent=new CustomTabsIntent.Builder(null).build();
//        PowerMockito.doReturn(customTabsIntent).when(liAuthService,
//                "createCustomTabsIntent");
        doNothing().when(liAuthService,
                "checkIfDisposed");
        liAuthService.startLoginFlow();
        doNothing().when(mContext).startActivity(isA(Intent.class));
        verify(mContext, Mockito.atLeastOnce()).startActivity(isA(Intent.class));
        verify(liAuthService,times(1)).performAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
        verify(liAuthService,times(0)).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));

    }
    @Test
    public void aperformAuthorizationRequestSSOTest() throws Exception {
        LiSDKManager liSDKManager;


        SharedPreferences mMockSharedPreferences;

        Resources resource;

        LiSSOAuthorizationRequest authorizationRequest;
        Activity mContext;
        mContext = mock(Activity.class);
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_KEY).setClientSecret(TEST_CLIENT_SECRET).setCommunityUri(COMMUNITY_URI).build();
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        liSDKManager = LiSDKManager.init(mContext,liAppCredentials);



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
        LiAuthService liAuthService = new LiAuthServiceImpl(mContext, SSO_TOKEN1);
        liAuthService = spy(liAuthService);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        doNothing().when(liAuthService,
                "checkIfDisposed");
        doNothing().when(liAuthService,"dispose");

        LiAuthorizationException liAuthorizationException=null;
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        System.out.println("enablepostauhfloaw");
                        return null;

                    }
                }
        ).when(liAuthService).enablePostAuthorizationFlows(eq(liAuthorizationException),anyBoolean());
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        System.out.println("enablepostauhfloaw with exp");
                        return null;

                    }
                }
        ).when(liAuthService).enablePostAuthorizationFlows(isA(LiAuthorizationException.class),anyBoolean());
        LiAuthRestClient liAuthRestClient=new LiAuthRestClient();
        PowerMockito.doReturn(liAuthRestClient).when(liAuthService,"getLiAuthRestClient");
        liAuthRestClient= spy(liAuthRestClient);
        final LiBaseResponse liBaseResponse= mock(LiBaseResponse.class);
        when(liBaseResponse.getHttpCode()).thenReturn(200);
        JsonObject dummyJsonData=mock(JsonObject.class);
        when(liBaseResponse.getData()).thenReturn(dummyJsonData);
        JsonElement jsonStringForBaseResponse=mock(JsonElement.class);
        when(liBaseResponse.getData().get("data")).thenReturn(jsonStringForBaseResponse);

        when(liBaseResponse.getData().get("response")).thenReturn(jsonStringForBaseResponse);

        when(liBaseResponse.getData().get("response").getAsJsonObject()).thenReturn(dummyJsonData);

        when(liBaseResponse.getData().get("response").getAsJsonObject().get("data")).thenReturn(jsonStringForBaseResponse);

        LiSSOAuthResponse liSSOAuthResponse= mock(LiSSOAuthResponse.class);
        when(liSSOAuthResponse.getState()).thenReturn(RANDOM_STATE);
        when(liSSOAuthResponse.getAuthCode()).thenReturn(TEST_AUTH_CODE);
        when(liSSOAuthResponse.getApiProxyHost()).thenReturn(TEST_API_PROXY_HOST);
        when(liSSOAuthResponse.getTenantId()).thenReturn(TEST_TENANT_ID);
        when(liSSOAuthResponse.getUserId()).thenReturn(TEST_USER_ID);
        when(liSSOAuthResponse.getJsonString()).thenReturn("{\"code\":\"testAuthCode\",\"proxy-host\":\"testApiProxyHost\",\"state\":\"randomState\",\"tenant-id\":\"testTenantId\",\"user-id\":\"testUserId\"}");
        PowerMockito.doReturn(liSSOAuthResponse).when(liAuthService,"getLiSSOAuthResponse",liBaseResponse);
        OkHttpClient okHttpClient= mock(OkHttpClient.class);
        PowerMockito.whenNew(OkHttpClient.class).withNoArguments().thenReturn(okHttpClient);
        Call call= mock(Call.class);

        final Response response= mock(Response.class);
        when(okHttpClient.newCall(isA(Request.class))).thenReturn(call);

        PowerMockito.whenNew(LiBaseResponse.class).withArguments(response).thenReturn(liBaseResponse);

        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(isA(Call.class),response);
                        //callback.onFailure(isA(Call.class),new IOException("OnError"));
                        return null;

                    }
                }
        ).when(call).enqueue(any(Callback.class));

        LiTokenResponse liTokenResponse= mock(LiTokenResponse.class);
        when(liTokenResponse.getUserId()).thenReturn(TEST_USER_ID);
        when(liTokenResponse.getExpiresIn()).thenReturn(TEST_EXPIRES_IN);
        when(liTokenResponse.getLithiumUserId()).thenReturn(TEST_LITHIUM_USER_ID);
        when(liTokenResponse.getRefreshToken()).thenReturn(TEST_REFRESH_TOKEN);
        when(liTokenResponse.getTokenType()).thenReturn(TEST_TOKEN_TYPE);
        when(liTokenResponse.getAccessToken()).thenReturn(TEST_ACCES_TOKEN);
        when(liTokenResponse.getJsonString()).thenReturn("{\"lithiumUserId\":\"testLithiumUserId\",\"access_token\":\"testAccesToken\",\"refresh_token\":\"testRefreshToken\",\"expires_in\":86400,\"userId\":\"testUserId\",\"token_type\":\"testTokenType\",\"expiresAt\":86400001}");
        when(liTokenResponse.getExpiresAt()).thenReturn(TEST_EXPIRES_AT);


        Gson gson= mock(Gson.class);
        PowerMockito.whenNew(Gson.class).withNoArguments().thenReturn(gson);
        when(gson.fromJson(isA(JsonElement.class),eq(LiSSOAuthResponse.class))).thenReturn(liSSOAuthResponse);
        when(gson.fromJson(isA(JsonElement.class),eq(LiTokenResponse.class))).thenReturn(liTokenResponse);

        when(gson.toJson(isA(LiSSOAuthorizationRequest.class))).thenReturn("LiSSOAuthorizationRequestString");
        when(gson.toJson(isA(LiSSOTokenRequest.class))).thenReturn("LiSSOTokenRequestString");

        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        SharedPreferences.Editor editor= mock(SharedPreferences.Editor.class);
        when(mMockSharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(),anyString())).thenReturn(editor);
        when(editor.commit()).thenReturn(true);

        PowerMockito.doAnswer( new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                LiAuthService.LoginCompleteCallBack callback = (LiAuthService.LoginCompleteCallBack) invocation.getArguments()[0];
                callback.onLoginComplete(null,true);
                return null;
            }
        }).when(liAuthService).getUserAfterTokenResponse(isA(LiAuthService.LoginCompleteCallBack.class));

        doNothing().when(mContext).startActivity(isA(Intent.class));

        doNothing().when(mContext).startActivity(isA(Intent.class));
        when(mContext.getString(anyInt())).thenReturn("test");

        liAuthService.startLoginFlow();



//        verify(liAuthService,times(0)).performAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));
//        verify(liAuthService,times(1)).performSSOAuthorizationRequest(isA(LiSSOAuthorizationRequest.class));

    }

    @Test
    public void performSyncRefreshTokenRequestTest() throws MalformedURLException, URISyntaxException, LiRestResponseException {
        LiSDKManager liSDKManager;


        SharedPreferences mMockSharedPreferences;

        Resources resource;

        LiSSOAuthorizationRequest authorizationRequest;
        Activity mContext;
        mContext = mock(Activity.class);
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_KEY).setClientSecret(TEST_CLIENT_SECRET).setCommunityUri(COMMUNITY_URI).build();
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        liSDKManager = LiSDKManager.init(mContext,liAppCredentials);



        LiAuthServiceImpl liAuthService=new LiAuthServiceImpl(mContext);
        liAuthService=spy(liAuthService);
        LiAuthRestClient liAuthRestClient=spy(new LiAuthRestClient());
        doReturn(liAuthRestClient).when(liAuthService).getLiAuthRestClient();


        String refreshtokenResponse="{\n" +
                "  \"data\": {\n" +
                "    \"response\": {\n" +
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
                "    }\n" +
                "  }\n" +
                "}";
        Gson gson=new Gson();
        LiBaseResponse liBaseResponse=gson.fromJson(refreshtokenResponse,LiBaseResponse.class);
        doReturn(liBaseResponse).when(liAuthRestClient).refreshTokenSync(isA(LiRefreshTokenRequest.class));

        LiTokenResponse tokenResponse = liAuthService.performSyncRefreshTokenRequest();
        assert (tokenResponse.getAccessToken().equals(ACCESS_TOKEN));

    }

    @Test
    public void performRefreshTokenRequestTest() throws Exception {

        LiSDKManager liSDKManager;


        SharedPreferences mMockSharedPreferences;

        Resources resource;

        LiSSOAuthorizationRequest authorizationRequest;
        Activity mContext;
        mContext = mock(Activity.class);
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_KEY).setClientSecret(TEST_CLIENT_SECRET).setCommunityUri(COMMUNITY_URI).build();
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        liSDKManager = LiSDKManager.init(mContext,liAppCredentials);



        LiAuthServiceImpl liAuthService=new LiAuthServiceImpl(mContext);
        liAuthService=spy(liAuthService);
        LiAuthRestClient liAuthRestClient=spy(new LiAuthRestClient());
        doReturn(liAuthRestClient).when(liAuthService).getLiAuthRestClient();


        String refreshtokenResponse="{\n" +
                "  \"data\": {\n" +
                "    \"response\": {\n" +
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
                "    }\n" +
                "  }\n" +
                "}";
        Gson gson=new Gson();
        LiBaseResponse liBaseResponse=gson.fromJson(refreshtokenResponse,LiBaseResponse.class);
        doReturn(liBaseResponse).when(liAuthRestClient).refreshTokenSync(isA(LiRefreshTokenRequest.class));

        LiAuthService.LiTokenResponseCallback callback=new LiAuthService.LiTokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable LiTokenResponse response, @Nullable Exception ex) {
                assert (response.getAccessToken().equals(ACCESS_TOKEN));
            }
        };
        OkHttpClient okHttpClient= mock(OkHttpClient.class);
        PowerMockito.whenNew(OkHttpClient.class).withNoArguments().thenReturn(okHttpClient);
        Call call= mock(Call.class);
        final Response response= mock(Response.class);
        when(okHttpClient.newCall(isA(Request.class))).thenReturn(call);
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(isA(Call.class),response);
                        //callback.onFailure(isA(Call.class),new IOException("OnError"));
                        return null;

                    }
                }
        ).when(call).enqueue(any(Callback.class));
        PowerMockito.whenNew(LiBaseResponse.class).withArguments(response).thenReturn(liBaseResponse);
        liAuthService.performRefreshTokenRequest(callback);
    }

    @Ignore
    @Test
    public void handleAuthorizationResponseTest() throws Exception {
        LiSDKManager liSDKManager;
        SharedPreferences mMockSharedPreferences;
        Resources resource;
        LiSSOAuthorizationRequest authorizationRequest;
        Activity mContext;
        mContext = Mockito.mock(Activity.class);
        mMockSharedPreferences = Mockito.mock(SharedPreferences.class);
        resource = Mockito.mock(Resources.class);
        Mockito.when(resource.getBoolean(anyInt())).thenReturn(true);
        Mockito.when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        Mockito.when(mContext.getResources()).thenReturn(resource);
        liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        mContext = mock(Activity.class);
        LiAppCredentials liAppCredentials = new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_KEY).setClientSecret(TEST_CLIENT_SECRET).setCommunityUri(COMMUNITY_URI).build();
//        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
  //      when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
    //    when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
//        SharedPreferences.Editor editor=mock(SharedPreferences.Editor.class);
//        when(mMockSharedPreferences.edit()).thenReturn(editor);
        liSDKManager = LiSDKManager.init(mContext,liAppCredentials);
        LiAuthServiceImpl liAuthService=new LiAuthServiceImpl(mContext);
        liAuthService=spy(liAuthService);
        LiAuthRestClient liAuthRestClient=spy(new LiAuthRestClient());
        doReturn(liAuthRestClient).when(liAuthService).getLiAuthRestClient();
        String refreshtokenResponse="{\n" +
                "  \"data\": {\n" +
                "    \"response\": {\n" +
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
                "    }\n" +
                "  }\n" +
                "}";
        Gson gson=new Gson();
        LiBaseResponse liBaseResponse=gson.fromJson(refreshtokenResponse,LiBaseResponse.class);
        doReturn(liBaseResponse).when(liAuthRestClient).refreshTokenSync(isA(LiRefreshTokenRequest.class));

        LiAuthService.LiTokenResponseCallback callback=new LiAuthService.LiTokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable LiTokenResponse response, @Nullable Exception ex) {
                assert (response.getAccessToken().equals(ACCESS_TOKEN));
            }
        };
        OkHttpClient okHttpClient= mock(OkHttpClient.class);
        PowerMockito.whenNew(OkHttpClient.class).withNoArguments().thenReturn(okHttpClient);
        Call call= mock(Call.class);
        final Response response= mock(Response.class);
        when(okHttpClient.newCall(isA(Request.class))).thenReturn(call);
        doAnswer(
                new Answer<Void>() {
                    @Override
                    public Void answer(final InvocationOnMock invocation) throws Throwable {
                        Callback callback = (Callback) invocation.getArguments()[0];
                        callback.onResponse(isA(Call.class),response);
                        //callback.onFailure(isA(Call.class),new IOException("OnError"));
                        return null;

                    }
                }
        ).when(call).enqueue(any(Callback.class));
        PowerMockito.whenNew(LiBaseResponse.class).withArguments(response).thenReturn(liBaseResponse);
        LiSSOAuthResponse liSSOAuthResponse= mock(LiSSOAuthResponse.class);
        when(liSSOAuthResponse.getState()).thenReturn(RANDOM_STATE);
        when(liSSOAuthResponse.getAuthCode()).thenReturn(TEST_AUTH_CODE);
        when(liSSOAuthResponse.getApiProxyHost()).thenReturn(TEST_API_PROXY_HOST);
        when(liSSOAuthResponse.getTenantId()).thenReturn(TEST_TENANT_ID);
        when(liSSOAuthResponse.getUserId()).thenReturn(TEST_USER_ID);
        when(liSSOAuthResponse.getJsonString()).thenReturn("{\"code\":\"testAuthCode\",\"proxy-host\":\"testApiProxyHost\",\"state\":\"randomState\",\"tenant-id\":\"testTenantId\",\"user-id\":\"testUserId\"}");
        PowerMockito.doReturn(liSSOAuthResponse).when(liAuthService,"getLiSSOAuthResponse",liBaseResponse);


        LiAuthService.LoginCompleteCallBack loginCallBack=new LiAuthService.LoginCompleteCallBack() {
            @Override
            public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                Assert.assertEquals(true,isSuccess);
            }
        };

        liAuthService.handleAuthorizationResponse(liSSOAuthResponse,liAuthRestClient,loginCallBack);
    }


}
