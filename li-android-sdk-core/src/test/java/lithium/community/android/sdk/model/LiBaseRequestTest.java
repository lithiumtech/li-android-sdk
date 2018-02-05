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

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import lithium.community.android.sdk.rest.LiBaseRestRequest;
import okhttp3.RequestBody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/*
  Created by mahaveer.udabal on 10/13/16.
 */

public class LiBaseRequestTest {

    private final String PATH = "Li_Path";
    @Mock
    private RequestBody requestBody;
    private LiBaseRestRequest.RestMethod method;
    private Map<String, String> additionalHttpHeaders;
    private Map<String, String> queryParams;
    private LiBaseRestRequest liBaseRestRequest;
    private Activity mContext;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        additionalHttpHeaders = new HashMap<String, String>();
        additionalHttpHeaders.put("add_header_key1", "add_header_value1");
        queryParams = new HashMap<String, String>();
        queryParams.put("add_param_key1", "add_param_value1");
        mContext = mock(Activity.class);
    }


    @Test
    public void liBaseRestRequestWithPathTest() {
        liBaseRestRequest = new LiBaseRestRequest(mContext, method.GET, PATH, requestBody, additionalHttpHeaders, true);
        assertThat(liBaseRestRequest.getPath()).isEqualTo(PATH);
        assertThat(liBaseRestRequest.getMethod()).isEqualTo(method.GET);
        assertThat(liBaseRestRequest.getRequestBody()).isEqualTo(requestBody);
        assertThat(liBaseRestRequest.isAuthenticatedRequest()).isEqualTo(true);
        assertThat(liBaseRestRequest.getAdditionalHttpHeaders()).isEqualTo(additionalHttpHeaders);
        liBaseRestRequest.addRequestHeader("add_header_key2", "add_header_value2");
        assertThat(liBaseRestRequest.getAdditionalHttpHeaders().containsKey("add_header_key2"));
    }

    @Test
    public void liBaseRestRequestNullTest() {
        liBaseRestRequest = new LiBaseRestRequest(mContext, method.GET, null, null, additionalHttpHeaders, true);
        assertThat(liBaseRestRequest.getPath()).isEqualTo(null);
        assertThat(liBaseRestRequest.getMethod()).isEqualTo(method.GET);
        assertThat(liBaseRestRequest.getRequestBody()).isEqualTo(null);
        assertThat(liBaseRestRequest.isAuthenticatedRequest()).isEqualTo(true);
        assertThat(liBaseRestRequest.getAdditionalHttpHeaders()).isEqualTo(additionalHttpHeaders);
        liBaseRestRequest.addRequestHeader("add_header_key2", "add_header_value2");
        assertThat(liBaseRestRequest.getAdditionalHttpHeaders().containsKey("add_header_key2"));
    }

    @Test
    public void liBaseRestRequestWithOutPathTest() {
        liBaseRestRequest = new LiBaseRestRequest(mContext, method.GET, requestBody, additionalHttpHeaders, true);
        assertThat(liBaseRestRequest.getMethod()).isEqualTo(method.GET);
        assertThat(liBaseRestRequest.getRequestBody()).isEqualTo(requestBody);
        assertThat(liBaseRestRequest.isAuthenticatedRequest()).isEqualTo(true);
        assertThat(liBaseRestRequest.getAdditionalHttpHeaders()).isEqualTo(additionalHttpHeaders);
        liBaseRestRequest.addRequestHeader("add_header_key2", "add_header_value2");
        assertThat(liBaseRestRequest.getAdditionalHttpHeaders().containsKey("add_header_key2"));
    }

}
