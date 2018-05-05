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

import org.junit.Test;

import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.model.response.LiSearch;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/4/16.
 */

public class LiSearchTest {

    private LiMessage liMessage = new LiMessage();
    private LiSearch search = new LiSearch();

    @Test
    public void getLiMessage() {
        LiBaseModelImpl.LiInt id = new LiBaseModelImpl.LiInt();
        id.setValue(100L);
        liMessage.setId(id);
        search.setLiMessage(liMessage);
        assertEquals(liMessage.getId(), search.getLiMessage().getId());

    }

}
