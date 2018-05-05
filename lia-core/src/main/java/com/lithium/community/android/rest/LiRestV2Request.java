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

package com.lithium.community.android.rest;

import android.content.Context;

import java.util.Map;

import com.lithium.community.android.manager.LiSDKManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Rest v2 request. Wraps LiQL query construct.
 */
public class LiRestV2Request extends LiBaseRestRequest {

    private final String type;

    /**
     * Preparing rest v2 request for GET call.
     */
    public LiRestV2Request(Context context, String liql, String type, Map<String, String> additionalHttpHeaders,
            boolean isAuthenticatedRequest) {
        super(context, RestMethod.GET, null, additionalHttpHeaders, isAuthenticatedRequest);
        addQueryParam(liql);
        this.type = type;
    }

    /**
     * Preparing rest v2 request for GET call when no additional headers are present.
     */
    public LiRestV2Request(Context context, String queryParams, String type) {
        super(context, RestMethod.GET, null, null, LiSDKManager.getInstance().isUserLoggedIn());
        addQueryParam(queryParams);
        this.type = type;
    }

    /**
     * Preparing rest v2 request for POST call.
     */
    public LiRestV2Request(Context context, RestMethod restMethod, String requestBody,
            Map<String, String> additionalHttpHeaders) {
        super(context, restMethod, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody),
                additionalHttpHeaders, true);
        this.type = null;
    }

    /**
     * Preparing rest v2 request for DELETE call.
     */
    public LiRestV2Request(Context context) {
        super(context, RestMethod.DELETE, null, null, true);
        type = null;
    }

    public String getType() {
        return type;
    }

}
