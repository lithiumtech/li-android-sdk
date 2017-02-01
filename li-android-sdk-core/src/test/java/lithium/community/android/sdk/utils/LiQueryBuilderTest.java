package lithium.community.android.sdk.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lithium.community.android.sdk.LiSDKManager;
import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.client.manager.LiClientManager;
import lithium.community.android.sdk.queryutil.LiDefaultQueryHelper;
import lithium.community.android.sdk.queryutil.LiQueryBuilder;
import lithium.community.android.sdk.rest.LiRestv2Client;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by kunal.shrivastava on 10/24/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LiDefaultQueryHelper.class)
public class LiQueryBuilderTest {
    private static final String BASE_TEST_QUERY = "SELECT id, body, subject, post_time, kudos.sum(weight), conversation.style FROM TEST";
    private static final String TEST_TYPE = "test";
    private static final String TYPE_HAVING_NO_CONFIG = "noconfig";
    private static final String RESULTANT_TEST_QUERY = "SELECT id, body, subject, post_time, kudos.sum(weight), conversation.style FROM TEST WHERE target.type = 'test' LIMIT 25";
    private Activity mContext;
    private LiSDKManager liSDKManager;
    private SharedPreferences mMockSharedPreferences;
    private Resources resource;
    private String defaultSettings = TestHelper.DEFAULT_QUERY_SETTINGS;

    private LiDefaultQueryHelper liDefaultQueryHelper;
    @Before
    public void setUpTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        mContext = mock(Activity.class);
        mMockSharedPreferences = mock(SharedPreferences.class);
        resource = mock(Resources.class);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        when(mContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);
        when(mMockSharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        when(mContext.getResources()).thenReturn(resource);
        JsonObject jsonObject = new Gson().fromJson(defaultSettings, JsonObject.class);
        liDefaultQueryHelper = mock(LiDefaultQueryHelper.class);
        when(liDefaultQueryHelper.getDefaultSetting()).thenReturn(jsonObject);
        PowerMockito.mockStatic(LiDefaultQueryHelper.class);
        BDDMockito.given(LiDefaultQueryHelper.getInstance()).willReturn(liDefaultQueryHelper);
        liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());

    }
    @Test
    public void getQueryTest() {
        assertEquals(RESULTANT_TEST_QUERY, LiQueryBuilder.getQuery(BASE_TEST_QUERY, TEST_TYPE));
    }

    @Test
    public void getQueryTestForDefaultSettings() {
        assertEquals(RESULTANT_TEST_QUERY, LiQueryBuilder.getQuery(BASE_TEST_QUERY, TEST_TYPE));
    }

    @Test
    public void getQueryTestIfQuerySettingsNotPresent() {
        assertEquals(BASE_TEST_QUERY, LiQueryBuilder.getQuery(BASE_TEST_QUERY,TYPE_HAVING_NO_CONFIG));
    }
}
