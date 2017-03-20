package lithium.community.android.sdk.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.LinkedList;

import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseResponse;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiClientResponse;
import lithium.community.android.sdk.rest.LiGetClientResponse;
import lithium.community.android.sdk.rest.LiRestV2Request;
import lithium.community.android.sdk.rest.LiRestv2Client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by kunal.shrivastava on 12/1/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LiRestv2Client.class, LiBaseClient.class, LiClientManager.class, LiBaseResponse.class, LiMessage.class, LiGetClientResponse.class})
public class LiBaseClientTest {
    private static final String LI_ARTICLES_CLIENT_BASE_LIQL = "SELECT id, subject, post_time, kudos.sum(weight), conversation.style, conversation.solved FROM messages";
    private static final String LI_ARTICLES_CLIENT_TYPE = "message";
    private static final String LI_ARTICLES_QUERYSETTINGS_TYPE = "article";

    private static final String EXPECTED_QUERY = "SELECT id, subject, post_time, kudos.sum(weight), conversation.style, conversation.solved FROM messages WHERE conversation.style in ('forum') AND depth = ## ORDER BY post_time desc LIMIT 50";
    private LiRestv2Client liRestv2Client;
    private LiClientManager liClientManager;
    private LiRestV2Request liRestV2Request;
    private Activity mContext;
    private LiSDKManager liSDKManager;
    private SharedPreferences mMockSharedPreferences;
    private Resources resource;

    @Mock
    private LiRestv2Client liRestv2ClientNew;


    @Captor
    private ArgumentCaptor<LiAsyncRequestCallback> dummyCallbackArgumentCaptor;

    @Before
    public void setUp() throws Exception, LiRestResponseException {

        mContext = Mockito.mock(Activity.class);
        mMockSharedPreferences = Mockito.mock(SharedPreferences.class);
        resource = Mockito.mock(Resources.class);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        liRestv2Client = mock(LiRestv2Client.class);
        MockitoAnnotations.initMocks(this);
        when(liSDKManager.getTenant()).thenReturn("test");
        liClientManager = mock(LiClientManager.class);
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
        PowerMockito.mockStatic(LiClientManager.class);
    }

    @Test
    public void testBaseGetClientCreation() throws LiRestResponseException {
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_CLIENT_BASE_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class);
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.type);
        Assert.assertEquals(LI_ARTICLES_QUERYSETTINGS_TYPE, liClient.querySettingsType);
        PowerMockito.verifyStatic();
    }

    @Test
    public void testSetLiRestV2Request() throws LiRestResponseException {
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_CLIENT_BASE_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class);
        liClient.setLiRestV2Request();
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.liRestV2Request.getType());
        PowerMockito.verifyStatic();
    }

    @Test
    public void testGetGson() throws LiRestResponseException {
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_CLIENT_BASE_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class);
        Assert.assertEquals(null, liClient.getGson());
        PowerMockito.verifyStatic();
    }

    @Test
    public void testGetGsonForNull() throws LiRestResponseException {
        LiRestv2Client liRestv2Client = null;
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_CLIENT_BASE_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class);
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
        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_CLIENT_BASE_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class);
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

        LiBaseClient liClient = new LiBaseGetClient(mContext, LI_ARTICLES_CLIENT_BASE_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class);
        liClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
            @Override
            public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
                Assert.assertEquals(200, response.getHttpCode());
            }

            @Override
            public void onError(Exception exception) {

            }
        });

    }


}