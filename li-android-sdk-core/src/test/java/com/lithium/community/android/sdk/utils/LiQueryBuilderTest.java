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

package com.lithium.community.android.sdk.utils;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.lithium.community.android.sdk.TestHelper;
import com.lithium.community.android.sdk.manager.LiSDKManager;
import com.lithium.community.android.sdk.queryutil.LiDefaultQueryHelper;
import com.lithium.community.android.sdk.queryutil.LiQueryBuilder;

import static junit.framework.Assert.assertEquals;

/**
 * Created by kunal.shrivastava on 10/24/16.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest(LiDefaultQueryHelper.class)
public class LiQueryBuilderTest {
    private static final String BASE_TEST_QUERY = "SELECT id, body, subject, post_time, kudos.sum(weight), conversation.style FROM TEST";
    private static final String TEST_TYPE = "test";
    private static final String TYPE_HAVING_NO_CONFIG = "noconfig";
    private static final String RESULTANT_TEST_QUERY = "SELECT id, body, subject, post_time, kudos.sum(weight), conversation.style FROM TEST WHERE target.type"
            + " = 'test' LIMIT 25";

    private Context mContext;

    @Before
    public void setUpTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        mContext = TestHelper.createMockContext();
        LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
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
