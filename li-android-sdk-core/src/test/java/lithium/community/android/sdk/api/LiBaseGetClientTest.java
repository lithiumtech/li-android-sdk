package lithium.community.android.sdk.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.queryutil.LiDefaultQueryHelper;
import lithium.community.android.sdk.rest.LiRestv2Client;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by kunal.shrivastava on 12/1/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LiRestv2Client.class, LiClientManager.class, LiDefaultQueryHelper.class})
public class LiBaseGetClientTest {
    private static final String LI_ARTICLES_CLIENT_BASE_LIQL = "SELECT id, subject, post_time, kudos.sum(weight), conversation.style, conversation.solved FROM messages";
    private static final String LI_ARTICLES_CLIENT_TYPE = "message";
    private static final String LI_ARTICLES_QUERYSETTINGS_TYPE = "article";

    private static final String EXPECTED_QUERY = "SELECT id, subject, post_time, kudos.sum(weight), conversation.style, conversation.solved FROM messages";
    private LiBaseGetClient liClient;
    private LiRestv2Client liRestv2Client;
    private Activity mContext;
    private LiSDKManager liSDKManager;
    private SharedPreferences mMockSharedPreferences;
    private Resources resource;
    private String defaultSettings = TestHelper.DEFAULT_QUERY_SETTINGS;

    private LiDefaultQueryHelper liDefaultQueryHelper;

    @Before
    public void setUp() throws Exception {

        mContext = Mockito.mock(Activity.class);
        mMockSharedPreferences = Mockito.mock(SharedPreferences.class);
        resource = Mockito.mock(Resources.class);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        JsonObject jsonObject = new Gson().fromJson(defaultSettings, JsonObject.class);
        liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        liRestv2Client = mock(LiRestv2Client.class);
        when(liSDKManager.getTenant()).thenReturn("test");
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
        liClient = new LiBaseGetClient(mContext, LI_ARTICLES_CLIENT_BASE_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class);


    }

    @Test
    public void testBaseGetClientCreation() throws LiRestResponseException {
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.type);
        Assert.assertEquals(LI_ARTICLES_QUERYSETTINGS_TYPE, liClient.querySettingsType);
        PowerMockito.verifyStatic();
    }

    @Test
    public void testGetLiqlQuery() {
        Assert.assertEquals(EXPECTED_QUERY, liClient.getLiqlQuery());
    }

    @Test
    public void testSetLiRestV2Request() {
        liClient.setLiRestV2Request();
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.liRestV2Request.getType());
    }

    @Test
    public void testGetGson() {
        Assert.assertEquals(null, liClient.getGson());
    }

    @Test
    public void testGetRequestBody() {
        Assert.assertEquals(null, liClient.getRequestBody());
    }
}
