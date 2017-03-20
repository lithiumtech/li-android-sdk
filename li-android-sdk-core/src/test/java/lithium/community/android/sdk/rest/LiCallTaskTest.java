package lithium.community.android.sdk.rest;

/**
 * Created by shoureya.kant on 12/5/16.
 */
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Arrays;

import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.exception.LiRestResponseException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Call.class, Response.class, Request.class, Protocol.class, LiClientManager.class, Gson.class})
public class LiCallTaskTest{

    private Call call;
    private LiBaseResponse liBaseResponse;
    private LiCallTask callTask;
    private Response response;
    private ResponseBody body;
    private Request request;
    private Protocol protocol;
    private LiClientManager liClientManager;
    private LiRestv2Client liRestClient;
    private Gson gson;
    private LiBaseRestRequest restRequest;
    private LiAsyncRequestCallback responseCallback;

    @Before
    public void setUp() throws LiRestResponseException, IOException {

        gson = PowerMockito.mock(Gson.class);
        restRequest = PowerMockito.mock(LiBaseRestRequest.class);
        responseCallback = PowerMockito.mock(LiAsyncRequestCallback.class);
        liRestClient = PowerMockito.mock(LiRestv2Client.class);
        byte[] bytes = new byte[100];
        Arrays.fill(bytes,(byte)0);
        body = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), bytes);
        request = PowerMockito.mock(Request.class);
        protocol = PowerMockito.mock(Protocol.class);
        response = PowerMockito.mock(Response.class);
        Response.Builder builder = new Response.Builder();
        builder.code(200);
        builder.message("test");
        builder.body(body);
        builder.request(request);
        builder.protocol(protocol);
        response = builder.build();
        PowerMockito.mockStatic(LiClientManager.class);
        liClientManager = mock(LiClientManager.class);
        when(liClientManager.getRestClient()).thenReturn(liRestClient);
        when(liRestClient.getGson()).thenReturn(gson);
        when(gson.fromJson(anyString(), (Class<Object>) any())).thenReturn(null);
        call = PowerMockito.mock(Call.class);
        callTask = new LiCallTask(call);
        when(call.execute()).thenReturn(response);
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void executeTest() throws LiRestResponseException, IOException {
      Assert.assertEquals(200, callTask.execute().getHttpCode());
        PowerMockito.verifyStatic();
    }
    @Test(expected = LiRestResponseException.class)
    public void testException() throws LiRestResponseException, IOException {
        when(call.execute()).thenThrow(new IOException());
        callTask.execute();
    }

}
