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

package lithium.community.android.sdk.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lithium.community.android.sdk.TestHelper;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.queryutil.LiDefaultQueryHelper;
import lithium.community.android.sdk.queryutil.LiQueryBuilder;

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
    private static final String BASE_TEST_QUERY
            = "SELECT id, body, subject, post_time, kudos.sum(weight), conversation.style FROM TEST";
    private static final String TEST_TYPE = "test";
    private static final String TYPE_HAVING_NO_CONFIG = "noconfig";
    private static final String RESULTANT_TEST_QUERY
            = "SELECT id, body, subject, post_time, kudos.sum(weight), conversation.style FROM TEST WHERE target.type"
            + " = 'test' LIMIT 25";
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
        when(LiDefaultQueryHelper.initHelper(mContext)).thenReturn(liDefaultQueryHelper);
        BDDMockito.given(LiDefaultQueryHelper.getInstance()).willReturn(liDefaultQueryHelper);

        liSDKManager = LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());

    }

    @Test
    public void getQueryTest() {
        assertEquals(RESULTANT_TEST_QUERY, LiQueryBuilder.getQuery(mContext, BASE_TEST_QUERY, TEST_TYPE));
    }

    @Test
    public void getQueryTestForDefaultSettings() {
        assertEquals(RESULTANT_TEST_QUERY, LiQueryBuilder.getQuery(mContext, BASE_TEST_QUERY, TEST_TYPE));
    }

    @Test
    public void getQueryTestIfQuerySettingsNotPresent() {
        assertEquals(BASE_TEST_QUERY, LiQueryBuilder.getQuery(mContext, BASE_TEST_QUERY, TYPE_HAVING_NO_CONFIG));
    }
}
