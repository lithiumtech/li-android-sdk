/*
 * LiBasePostClient.java
 * Created on Dec 27, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.api;

import android.content.Context;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.post.LiPostModel;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiRestV2Request;

/**
 * All "post" clients are implemented in same way. They only vary in their requestBody. This class brings all the common codes together
 * and provide a layer of abstraction to all of the "post" clients.
 * Created by kunal.shrivastava on 10/19/16.
 */

public class LiBasePostClient extends LiBaseClient {
    public LiPostModel postModel;
    private String requestBody;

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
     * Constructor used for making POST call to upload image.
     *
     * @param basePath  Api end point.
     * @param imagePath Absolute path of the image.
     * @param imageName Name of the image file.
     * @throws LiRestResponseException
     */
    public LiBasePostClient(Context context, String basePath, String imagePath, String imageName) throws LiRestResponseException {
        super(context, basePath, RequestType.POST, imagePath, imageName);
    }

    /**
     * Sets request body and initializes {@link LiRestV2Request}.
     */
    @Override
    public void setLiRestV2Request() {
        this.requestBody = postModel.toJsonString();
        this.liRestV2Request = new LiRestV2Request(context, LiBaseRestRequest.RestMethod.POST, requestBody);
    }

    @Override
    public String getRequestBody() {
        return requestBody;
    }
}
