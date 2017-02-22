package lithium.community.android.sdk.client.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;

import lithium.community.android.sdk.LiSDKManager;
import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.auth.LiAppCredentials;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.queryutil.LiQueryClause;
import lithium.community.android.sdk.queryutil.LiQueryOrdering;
import lithium.community.android.sdk.queryutil.LiQueryRequestParams;
import lithium.community.android.sdk.queryutil.LiQuerySetting;
import lithium.community.android.sdk.queryutil.LiQueryWhereClause;
import lithium.community.android.sdk.rest.LiRestv2Client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
  Created by kunal.shrivastava on 11/31/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LiRestv2Client.class)
public class LiClientManagerTest {

    private static final String LI_ACCEPT_SOLUTION_TYPE = "solution_data";
    private static final String LI_KUDO_TYPE = "kudo";
    private static final String LI_POST_QUESTION_TYPE = "message";
    private static final String LI_REPLY_MESSAGE_TYPE = "message";
    private static final String LI_ARTICLES_CLIENT_TYPE = "message";
    private static final String LI_SUBSCRIPTIONS_CLIENT_TYPE = "subscription";
    private static final String LI_BROWSE_CLIENT_TYPE = "node";
    private static final String LI_SEARCH_CLIENT_TYPE = "message";
    private static final String LI_MESSAGE_CHILDREN_CLIENT_TYPE = "message";
    private static final String LI_QUESTIONS_CLIENT_TYPE = "message";
    private static final String LI_CATEGORY_CLIENT_TYPE = "node";
    private static final String LI_ARTICLES_BROWSE_CLIENT_TYPE = "message";
    private static final String LI_USER_DETAILS_CLIENT_TYPE = "user";
    private static final String LI_MESSAGE_CLIENT_TYPE = "message";
    public static final String LI_GENERIC_TYPE = "generic_get";
    public static final String LI_SDK_SETTINGS_CLIENT_TYPE = "app_sdk_setting";

    private static final String GET = "GET";
    private static final String POST = "POST";


    @Mock
    private Activity mContext;

    private SharedPreferences mMockSharedPreferences;

    private LiClientManager liClientManger;

    private Resources resource;

    private LiRestv2Client liRestv2Client;

    private LiSDKManager liSDKManager;

    @Before
    public void setUpTest() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mContext = mock(Activity.class);
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        liClientManger = LiClientManager.init(mContext);
        liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        liRestv2Client = PowerMockito.mock(LiRestv2Client.class);
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitNullContext() throws MalformedURLException, URISyntaxException {
        LiClientManager.init(null);
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testInitNullSdkInitParams() throws MalformedURLException, URISyntaxException {
//        LiClientManager.init(mContext);
//    }

    @Test
    public void testGetInstance() throws MalformedURLException, URISyntaxException {
        LiClientManager instance = liClientManger.getInstance();
        assertEquals(liClientManger, instance);
    }

    @Test
    public void testSingletonGetInstance() throws MalformedURLException, URISyntaxException {
        LiClientManager instance = liClientManger.getInstance();
        assertEquals(liClientManger, instance);
        LiClientManager liClientManger2 = LiClientManager.init(mContext);
        assertEquals(liClientManger2, instance);
    }

    @Test
    public void testGetArticlesClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getMessagesClient();
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetArticlesClientWithId() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getMessagesClient("1");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetSubscriptionClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getUserSubscriptionsClient();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_SUBSCRIPTIONS_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetBrowseClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getCategoryBoardsClient("1");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_BROWSE_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetMessageChildrenClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getRepliesClient(new Long("1"));
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_MESSAGE_CHILDREN_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetSearchClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getSearchClient("test");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_SEARCH_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testgetQuestionsClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getUserMessagesClient(new Long("1"), "test");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_QUESTIONS_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetCategoryClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getCategoryClient();
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_CATEGORY_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetUserDetailsClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getUserDetailsClient("test");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_USER_DETAILS_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetMessageClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getMessageClient(new Long("1"));
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_MESSAGE_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetMessagesByIdsClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getMessagesByIdsClient(new HashSet<String>(Arrays.asList("a", "b")));
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_MESSAGE_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetKudoClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getKudoClient("1");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(POST, ""+liClient.getRequestType());
    }

    @Test
    public void testGetAcceptSolutionClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getAcceptSolutionClient(new Long("1"));
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(POST, ""+liClient.getRequestType());
    }

    @Test
    public void testGetPostQuestionClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getCreateMessageClient("test", "test", "test", "test", "test");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(POST, ""+liClient.getRequestType());
    }

    @Test
    public void testGetReplyMessageClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getCreateReplyClient("test", new Long("1"), null , "");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(POST, ""+liClient.getRequestType());
    }

    @Test
    public void testGetMarkAbuseClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getMarkAbuseClient("test", "test", "test");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(POST, ""+liClient.getRequestType());
    }

    @Test
    public void testGenericPostClient() throws LiRestResponseException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("test_v", "test_p");
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.createGenericPostClient("test_path", requestBody);
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(POST, ""+liClient.getRequestType());
    }

    @Test
    public void testGenericGetClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.createGenericGetClient("test query");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testGetSdkSettingsClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getSdkSettingsClient("clientId");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(LI_SDK_SETTINGS_CLIENT_TYPE, liClient.getType());
        Assert.assertEquals(GET, ""+liClient.getRequestType());
    }

    @Test
    public void testUploadImageClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiClient liClient = instance.getUploadImageClient("test", "test", "test", "test");
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals(null, liClient.getType());
        Assert.assertEquals(POST, ""+liClient.getRequestType());
    }

    @Test
    public void testGetClient() throws LiRestResponseException {
        LiClientManager instance = liClientManger.getInstance();
        LiQueryRequestParams.Builder builder = LiQueryRequestParams.getBuilder();
        builder.setClient(LiClientManager.Client.LI_ARTICLES_CLIENT);
        builder.setLimit(10);
        LiQueryOrdering liQueryOrdering = new LiQueryOrdering(LiQueryOrdering.Articles.POST_TIME, LiQueryOrdering.Order.ASC);
        LiQueryClause liQueryClause = new LiQueryClause(LiQuerySetting.LiWhereClause.EQUALS, LiQueryWhereClause.Articles.DEPTH, "1", "AND");
        LiQueryWhereClause liQueryWhereClause = new LiQueryWhereClause();
        liQueryWhereClause.addClause(liQueryClause);
        builder.setLiQueryOrdering(liQueryOrdering);
        builder.setLiQueryWhereClause(liQueryWhereClause);
        LiQueryRequestParams liQueryRequestParams = builder.build();
        LiClient liClient = instance.getClient(liQueryRequestParams);
        liClient.processSync();
        PowerMockito.verifyStatic();
        Assert.assertEquals("message", liClient.getType());
    }

}
