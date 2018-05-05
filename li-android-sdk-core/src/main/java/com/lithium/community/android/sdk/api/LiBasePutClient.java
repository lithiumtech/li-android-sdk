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

package com.lithium.community.android.sdk.api;

import android.content.Context;

import java.util.Map;

import com.lithium.community.android.sdk.exception.LiRestResponseException;
import com.lithium.community.android.sdk.model.post.LiPostModel;
import com.lithium.community.android.sdk.rest.LiBaseRestRequest;
import com.lithium.community.android.sdk.rest.LiRestV2Request;

/**
 * All "put" clients are implemented in same way. They only vary in their requestBody. This class brings all the
 * common codes together
 * and provide a layer of abstraction to all of the "put" clients.
 * Created by shoureya.kant on 12/28/16.
 */

public class LiBasePutClient extends LiBaseClient {
    public LiPostModel postModel;
    private String requestBody;
    private Map<String, String> additionalHttpHeaders;

    public LiBasePutClient(Context context, String basePath) throws LiRestResponseException {
        super(context, basePath, RequestType.PUT);
        this.additionalHttpHeaders = additionalHttpHeaders;
    }

    public LiBasePutClient(Context context, String basePath,
            Map<String, String> additionalHttpHeaders) throws LiRestResponseException {
        super(context, basePath, RequestType.PUT);
        this.additionalHttpHeaders = additionalHttpHeaders;
    }

    /**
     * Sets request body and initializes {@link LiRestV2Request}.
     */
    @Override
    public void setLiRestV2Request() {
        this.requestBody = postModel.toJsonString();
        this.liRestV2Request = new LiRestV2Request(context, LiBaseRestRequest.RestMethod.PUT, requestBody,
                additionalHttpHeaders);
    }

    @Override
    public String getRequestBody() {
        return requestBody;
    }
}
