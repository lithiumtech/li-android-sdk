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

package com.lithium.community.android.api;

import android.content.Context;

import java.util.Map;

import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.model.post.LiPostModel;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiRestV2Request;

/**
 * All "post" clients are implemented in same way. They only vary in their requestBody. This class brings all the
 * common codes together
 * and provide a layer of abstraction to all of the "post" clients.
 * Created by kunal.shrivastava on 10/19/16.
 */

public class LiBasePostClient extends LiBaseClient {
    public LiPostModel postModel;
    private String requestBody;
    private Map<String, String> additionalHttpHeaders;

    /**
     * Constructor used for normal POST call.
     *
     * @param basePath Api end point.
     * @throws LiRestResponseException
     */
    public LiBasePostClient(Context context, String basePath) throws LiRestResponseException {
        super(context, basePath, RequestType.POST);
    }

    /**
     * Constructor used for normal POST call.
     *
     * @param basePath Api end point.
     * @throws LiRestResponseException
     */
    public LiBasePostClient(Context context, String basePath,
            Map<String, String> additionalHttpHeaders) throws LiRestResponseException {
        super(context, basePath, RequestType.POST);
        this.additionalHttpHeaders = additionalHttpHeaders;
    }

    /**
     * Constructor used for making POST call to upload image.
     *
     * @param basePath  Api end point.
     * @param imagePath Absolute path of the image.
     * @param imageName Name of the image file.
     * @throws LiRestResponseException
     */
    public LiBasePostClient(Context context, String basePath, String imagePath,
            String imageName) throws LiRestResponseException {
        super(context, basePath, RequestType.POST, imagePath, imageName);
    }

    /**
     * Sets request body and initializes {@link LiRestV2Request}.
     */
    @Override
    public void setLiRestV2Request() {
        this.requestBody = postModel.toJsonString();
        this.liRestV2Request = new LiRestV2Request(context, LiBaseRestRequest.RestMethod.POST, requestBody,
                additionalHttpHeaders);
    }

    @Override
    public String getRequestBody() {
        return requestBody;
    }
}
