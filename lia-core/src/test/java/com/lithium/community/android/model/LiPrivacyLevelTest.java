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

import com.lithium.community.android.model.helpers.LiPrivacyLevel;

import static org.junit.Assert.assertEquals;

/*
   Created by mahaveer.udabal on 10/5/16.
 */

public class LiPrivacyLevelTest {

    @Test
    public void getExternalSourceTest() {
        assertEquals(LiPrivacyLevel.UNKNOWN, LiPrivacyLevel.getExternalSource("test"));
        assertEquals(LiPrivacyLevel.PRIVATE, LiPrivacyLevel.getExternalSource("private"));
        assertEquals(LiPrivacyLevel.PUBLIC, LiPrivacyLevel.getExternalSource("public"));
        assertEquals(LiPrivacyLevel.UNKNOWN, LiPrivacyLevel.getExternalSource(""));
        assertEquals(LiPrivacyLevel.UNKNOWN, LiPrivacyLevel.getExternalSource(null));
    }
}
