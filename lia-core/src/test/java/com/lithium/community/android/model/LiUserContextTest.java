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

package com.lithium.community.android.model;

import org.junit.Test;

import com.lithium.community.android.model.helpers.LiUserContext;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiUserContextTest {

    private final Boolean kudo = true;
    private final Boolean read = true;
    private final boolean canKudo = true;
    private final boolean canReply = true;
    private final boolean isSubscribed = true;


    private LiUserContext liUserContext = new LiUserContext();
    @Test
    public void getParamsTest() {
        liUserContext.setKudo(kudo);
        liUserContext.setRead(read);
        liUserContext.setCanKudo(canKudo);
        liUserContext.setCanReply(canReply);
        liUserContext.setSubscribed(isSubscribed);
        assertEquals(kudo, liUserContext.getKudo());
        assertEquals(read, liUserContext.getRead());
        assertEquals(canKudo, liUserContext.isCanKudo());
        assertEquals(canReply, liUserContext.isCanReply());
        assertEquals(isSubscribed, liUserContext.isSubscribed());
    }
}
