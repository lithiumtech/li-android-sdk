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

package lithium.community.android.sdk.rest;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by kunal.shrivastava on 12/8/16.
 */

public class LiBaseResponseTest {


    private LiBaseResponse liBaseResponse;

    @Before
    public void setUp() {
        liBaseResponse = new LiBaseResponse();
    }

    @Test
    public void testGetData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", "test");
        liBaseResponse.setData(jsonObject);
        JsonObject jsonObject1 = liBaseResponse.getData();
        Assert.assertEquals(jsonObject.get("test"), jsonObject1.get("test"));
    }

    @Test
    public void testGetStatus() {
        liBaseResponse.setStatus("success");
        Assert.assertEquals("success", liBaseResponse.getStatus());
    }

    @Test
    public void testGetMessage() {
        liBaseResponse.setMessage("success");
        Assert.assertEquals("success", liBaseResponse.getMessage());
    }

    @Test
    public void testGetHttpCode() {
        liBaseResponse.setHttpCode(100);
        Assert.assertEquals(100, liBaseResponse.getHttpCode());
    }
}
