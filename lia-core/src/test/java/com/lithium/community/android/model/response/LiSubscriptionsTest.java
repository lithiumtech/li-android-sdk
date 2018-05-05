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

package com.lithium.community.android.model.response;

import android.content.Context;

import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import com.lithium.community.android.TestHelper;
import com.lithium.community.android.auth.LiAppCredentials;
import com.lithium.community.android.exception.LiInitializationException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.response.LiSubscriptions;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiSubscriptionsTest {

    private final Long id = 100L;
    private LiSubscriptions subscriptions = new LiSubscriptions();

    @Before
    public void before() throws LiInitializationException {
        Context context = TestHelper.createMockContext();
        LiAppCredentials credentials = TestHelper.getTestAppCredentials();
        LiSDKManager.initialize(context, credentials);
    }

    @Test
    public void getIdTest() {
        LiBaseModelImpl.LiInt liId = new LiBaseModelImpl.LiInt();
        liId.setValue(id);
        subscriptions.setId(liId);
        assertEquals(id, subscriptions.getId());
    }

    @Test
    public void getTargetMessageTest() {
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("id", id);
        messageObject.addProperty("type", "message");
        subscriptions.setTargetObject(messageObject);
        assertEquals(id, subscriptions.getLiMessage().getId());
    }

    @Test
    public void getTargetBoardTest() {
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("id", id);
        messageObject.addProperty("type", "board");
        subscriptions.setTargetObject(messageObject);
        assertEquals(id, Long.valueOf(subscriptions.getLiBoard().getId()));
    }

}
