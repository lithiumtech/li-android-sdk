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

import org.junit.Assert;
import org.junit.Test;

import lithium.community.android.sdk.queryutil.LiQueryValueReplacer;

/**
 * Created by kunal.shrivastava on 12/7/16.
 */

public class LiQueryValueReplacerTest {

    @Test
    public void testGetReplacer() {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", "test");
        Assert.assertEquals("test", liQueryValueReplacer.getReplacementMap().get("##"));
    }
}
