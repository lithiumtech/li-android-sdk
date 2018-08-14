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

package com.lithium.community.android.model.post;

import org.junit.Test;

import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.post.LiMarkAbuseModel;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.model.response.LiUser;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 2/22/17.
 */
public class LiMarkAbuseModelTest {

    private Long messageId = 10L;
    private Long userId = 100L;

    @Test
    public void testMarkAbuseModel() {

        LiUser liUser = new LiUser();
        liUser.setId(100L);

        LiMessage liMessage = new LiMessage();
        liMessage.setId(10);

        LiMarkAbuseModel liMarkAbuseModel = new LiMarkAbuseModel();
        liMarkAbuseModel.setReporter(liUser);
        liMarkAbuseModel.setMessage(liMessage);
        liMarkAbuseModel.setType("mark_abuse");
        liMarkAbuseModel.setBody("This is abusive");

        assertEquals("mark_abuse", liMarkAbuseModel.getType());
        assertEquals("This is abusive", liMarkAbuseModel.getBody());
        assertEquals(messageId, liMarkAbuseModel.getMessage().getId());
        assertEquals(userId, liMarkAbuseModel.getReporter().getId());
    }
}
