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

import com.lithium.community.android.model.response.LiFloatedMessageModel;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.model.response.LiUser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiFloatedMessageModelTest {

    private final String id = "100";
    private final String href = "href";
    private final String scope = "scope";
    private LiUser user = new LiUser();
    private LiMessage message = new LiMessage();
    private LiFloatedMessageModel liFloatedMessageModel = new LiFloatedMessageModel();

    @Test
    public void getParamsTest() {

        liFloatedMessageModel.setId(id);
        liFloatedMessageModel.setHref(href);
        liFloatedMessageModel.setScope(scope);
        user.setHref(href);
        message.setIsAcceptedSolution(true);
        liFloatedMessageModel.setUser(user);
        liFloatedMessageModel.setMessage(message);
        assertEquals(id, liFloatedMessageModel.getId());
        assertEquals(href, liFloatedMessageModel.getHref());
        assertEquals(scope, liFloatedMessageModel.getScope());
        assertEquals(href, liFloatedMessageModel.getUser().getHref());
        assertEquals(true, liFloatedMessageModel.getMessage().isAcceptedSolution());
    }
}
