package lithium.community.android.sdk.rest;

import android.net.Uri;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import lithium.community.android.sdk.auth.LiRefreshTokenRequest;
import lithium.community.android.sdk.auth.LiSSOAuthorizationRequest;
import lithium.community.android.sdk.auth.LiSSOTokenRequest;
import lithium.community.android.sdk.exception.LiRestResponseException;
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

//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.spy;
//import static org.mockito.Mockito.when;


/**
 * Created by saiteja.tokala on 12/15/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LiAuthRestClient.class,OkHttpClient.class,Response.class,ResponseBody.class})
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


    @Test
    public void authorizeAsyncFailureCallback() throws LiRestResponseException, IOException {
        LiSSOAuthorizationRequest ssoAuthReq=new LiSSOAuthorizationRequest();
        ssoAuthReq.setClientId(CLIENT_ID);
        ssoAuthReq.setSsoToken(SSO_TOKEN);
        ssoAuthReq.setState(STATE);
        ssoAuthReq.setUri(URI);
        ssoAuthReq.setRedirectUri(REDIRECT_URI);
        LiAuthAsyncRequestCallback callback=new LiAuthAsyncRequestCallback() {
            @Override
            public void onSuccess(Object response) throws LiRestResponseException {
                System.out.println(response);
                Assert.assertEquals(ITS_A_TEST_MESSAGE,((LiBaseResponse)response).getMessage());
                System.out.println("-----ITS SUCCESS-----"+((LiBaseResponse)response).getMessage());
            }

            @Override
            public void onError(Exception exception) {
                System.out.println(exception.getMessage());
                Assert.assertEquals(EXCEPTION_CAUSE,exception.getMessage());
                System.out.println("-----ITS FAILURE-----"+exception.getMessage());
            }
        };

        OkHttpClient okHttpClient=mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient=spy(new LiAuthRestClient());
        final Call call=mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class),isA(OkHttpClient.class));
        LiBaseResponse liBaseResponse=new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response=mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body=mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(response);
        final IOException excep=mock(IOException.class);
        when(excep.getMessage()).thenReturn(EXCEPTION_CAUSE);
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

        liAuthRestClient.authorizeAsync(ssoAuthReq,callback);
    }

    @Test
    public void accessTokenAsyncSuccess() throws IOException, LiRestResponseException {
        LiSSOTokenRequest liSSOTokenRequest=new LiSSOTokenRequest();
        liSSOTokenRequest.setAcceesCode(ACCESS_CODE);
        liSSOTokenRequest.setRedirectUri(REDIRECT_URI);
        liSSOTokenRequest.setUri(Uri.parse(URI));
        liSSOTokenRequest.setGrantType(GRANT_TYPE);
        liSSOTokenRequest.setClientId(CLIENT_ID);
        liSSOTokenRequest.setClientSecret(CLIENT_SECRET);

        LiAuthAsyncRequestCallback callback=new LiAuthAsyncRequestCallback() {
            @Override
            public void onSuccess(Object response) throws LiRestResponseException {
                System.out.println(response);
                Assert.assertEquals(ITS_A_TEST_MESSAGE,((LiBaseResponse)response).getMessage());
                System.out.println("-----ITS SUCCESS-----"+((LiBaseResponse)response).getMessage());
            }

            @Override
            public void onError(Exception exception) {
                System.out.println(exception.getMessage());
                Assert.assertEquals(EXCEPTION_CAUSE,exception.getMessage());
                System.out.println("-----ITS FAILURE-----"+exception.getMessage());
            }
        };

        OkHttpClient okHttpClient=mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient=spy(new LiAuthRestClient());
        final Call call=mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class),isA(OkHttpClient.class));
        LiBaseResponse liBaseResponse=new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response=mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body=mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(finalResponse);
        final IOException excep=mock(IOException.class);
        when(excep.getMessage()).thenReturn(EXCEPTION_CAUSE);
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

        liAuthRestClient.accessTokenAsync(liSSOTokenRequest,callback);
    }

    @Test
    public void refreshTokenAsyncTest() throws IOException, LiRestResponseException {
        LiRefreshTokenRequest liRefreshTokenRequest=new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientSecret(CLIENT_SECRET);
        liRefreshTokenRequest.setGrantType(GRANT_TYPE);
        liRefreshTokenRequest.setClientId(CLIENT_ID);
        liRefreshTokenRequest.setRefreshToken(REFRESH_TOKEN);
        liRefreshTokenRequest.setUri(Uri.parse(URI));
        LiAuthAsyncRequestCallback callback=new LiAuthAsyncRequestCallback() {
            @Override
            public void onSuccess(Object response) throws LiRestResponseException {
                System.out.println(response);
                Assert.assertEquals(ITS_A_TEST_MESSAGE,((LiBaseResponse)response).getMessage());
                System.out.println("-----ITS SUCCESS-----"+((LiBaseResponse)response).getMessage());
            }

            @Override
            public void onError(Exception exception) {
                System.out.println(exception.getMessage());
                Assert.assertEquals(EXCEPTION_CAUSE,exception.getMessage());
                System.out.println("-----ITS FAILURE-----"+exception.getMessage());
            }
        };

        OkHttpClient okHttpClient=mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient=spy(new LiAuthRestClient());
        final Call call=mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class),isA(OkHttpClient.class));
        LiBaseResponse liBaseResponse=new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response=mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body=mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(finalResponse);
        final IOException excep=mock(IOException.class);
        when(excep.getMessage()).thenReturn(EXCEPTION_CAUSE);
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
        liAuthRestClient.refreshTokenAsync(liRefreshTokenRequest,callback);
    }

    @Test
    public void refreshTokenSyncTest() throws IOException, LiRestResponseException {
        LiRefreshTokenRequest liRefreshTokenRequest=new LiRefreshTokenRequest();
        liRefreshTokenRequest.setClientSecret(CLIENT_SECRET);
        liRefreshTokenRequest.setGrantType(GRANT_TYPE);
        liRefreshTokenRequest.setClientId(CLIENT_ID);
        liRefreshTokenRequest.setRefreshToken(REFRESH_TOKEN);
        liRefreshTokenRequest.setUri(Uri.parse(URI));
        OkHttpClient okHttpClient=mock(OkHttpClient.class);
        LiAuthRestClient liAuthRestClient=spy(new LiAuthRestClient());
        final Call call=mock(Call.class);
        doReturn(okHttpClient).when(liAuthRestClient).getOkHttpClient();
        doReturn(call).when(liAuthRestClient).getCall(isA(Request.class),isA(OkHttpClient.class));

        LiBaseResponse liBaseResponse=new LiBaseResponse();
        liBaseResponse.setMessage(ITS_A_TEST_MESSAGE);
        liBaseResponse.setHttpCode(200);
        Response response=mock(Response.class);
        when(response.isSuccessful()).thenReturn(true);
        ResponseBody body=mock(ResponseBody.class);
        doReturn(body).when(response).body();
        final Response finalResponse = response;
        doReturn(liBaseResponse).when(liAuthRestClient).getLiBaseResponseFromResponse(finalResponse);
        final IOException excep=mock(IOException.class);
        when(excep.getMessage()).thenReturn(EXCEPTION_CAUSE);
        when(call.execute()).thenReturn(response);
        LiBaseResponse resp = liAuthRestClient.refreshTokenSync(liRefreshTokenRequest);
        Assert.assertEquals(resp.getHttpCode(),200);
        Assert.assertEquals(resp.getMessage(),ITS_A_TEST_MESSAGE);

    }
}
