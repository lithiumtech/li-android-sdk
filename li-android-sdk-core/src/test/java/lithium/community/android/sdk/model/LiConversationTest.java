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

package lithium.community.android.sdk.model;

import org.junit.Test;

import lithium.community.android.sdk.model.helpers.LiConversation;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiConversationTest {

    private final String type = "conversation";
    private final boolean solved = true;

    LiConversation liConversation = new LiConversation();

    @Test
    public void getParamsTest() {
        liConversation.setType(type);
        liConversation.setSolved(solved);
        assertEquals(type, liConversation.getType());
        assertEquals(solved, liConversation.isSolved());
    }
}
