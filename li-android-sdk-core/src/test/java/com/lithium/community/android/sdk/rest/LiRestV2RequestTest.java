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

package com.lithium.community.android.sdk.rest;

import android.app.Activity;

import com.lithium.community.android.sdk.rest.LiBaseRestRequest;
import com.lithium.community.android.sdk.rest.LiRestV2Request;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/*
   Created by mahaveer.udabal on 10/18/16.
 */

public class LiRestV2RequestTest {

    private final String LI_QL = "Select * from Customers";
    private final String TYPE = "getRequest";
    private final String requestBody = "request";
    private Map<String, String> mParams;
    @Mock
    private Activity mContext;

    @Before
    public void setUp() {
        mParams = new HashMap<String, String>();
        mParams.put("Q", LI_QL);
        MockitoAnnotations.initMocks(this);
        mContext = mock(Activity.class);
    }

    @Test
    public void liRestV2RequestWithAdditionalHeadersTest() {
        LiRestV2Request liRestV2Request = new LiRestV2Request(mContext, LI_QL, TYPE, mParams, true);
        assertEquals(TYPE, liRestV2Request.getType());
    }

    @Test
    public void liRestV2RequestNullTest() {
        LiRestV2Request liRestV2Request = new LiRestV2Request(mContext, null, null, null, false);
        assertEquals(null, liRestV2Request.getType());
    }

    @Test
    public void liRestV2RequestWithOutAdditionalHeadersTest() {
        LiRestV2Request liRestV2Request = new LiRestV2Request(mContext, LI_QL, TYPE);
        assertEquals(TYPE, liRestV2Request.getType());
    }

    @Test
    public void deleteRequestTest() {
        LiRestV2Request liRestV2Request = new LiRestV2Request(mContext);
        assertEquals(null, liRestV2Request.getType());
    }

    @Test
    public void postRequestTest() {
        LiRestV2Request liRestV2Request = new LiRestV2Request(mContext, LiBaseRestRequest.RestMethod.POST, requestBody,
                null);
        assertEquals(null, liRestV2Request.getType());
    }
}
