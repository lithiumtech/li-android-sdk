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

package com.lithium.community.android.api;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.lithium.community.android.api.LiBaseGetClient;
import com.lithium.community.android.TestHelper;

import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.queryutil.LiDefaultQueryHelper;
import com.lithium.community.android.rest.LiRestv2Client;

import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author kunal.shrivastava
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*"})
@PrepareForTest({LiRestv2Client.class, LiClientManager.class, LiDefaultQueryHelper.class})
public class LiBaseGetClientTest {

    private static final String LI_ARTICLES_LIQL = "SELECT id, subject, post_time, kudos.sum(weight), conversation.style, conversation.solved FROM messages";
    private static final String LI_ARTICLES_CLIENT_TYPE = "message";
    private static final String LI_ARTICLES_QUERY_SETTINGS_TYPE = "article";
    private static final String EXPECTED_QUERY = "SELECT id, subject, post_time, kudos.sum(weight), conversation.style, conversation.solved FROM messages WHERE"
            + " conversation.style in ('forum', 'blog') AND depth = ## ORDER BY conversation.last_post_time desc LIMIT 50";

    private LiBaseGetClient liClient;

    @Before
    public void setUp() throws Exception {
        Context mContext = TestHelper.createMockContext();
        //noinspection deprecation for test
        LiSDKManager.init(mContext, TestHelper.getTestAppCredentials());
        LiRestv2Client liRestv2Client = mock(LiRestv2Client.class);
        PowerMockito.mockStatic(LiRestv2Client.class);
        BDDMockito.given(LiRestv2Client.getInstance()).willReturn(liRestv2Client);
        liClient = new LiBaseGetClient(mContext, LI_ARTICLES_LIQL, LI_ARTICLES_CLIENT_TYPE, LI_ARTICLES_QUERY_SETTINGS_TYPE, LiMessage.class);
    }

    @Test
    public void testBaseGetClientCreation() throws LiRestResponseException {
        Assert.assertEquals(LI_ARTICLES_CLIENT_TYPE, liClient.type);
        Assert.assertEquals(LI_ARTICLES_QUERY_SETTINGS_TYPE, liClient.querySettingsType);
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
