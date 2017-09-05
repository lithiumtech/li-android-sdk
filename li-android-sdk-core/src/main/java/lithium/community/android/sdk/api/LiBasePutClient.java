/*
 * LiBasePutClient.java
 * Created on Dec 28, 2016
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

import java.util.Map;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.post.LiPostModel;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiRestV2Request;

/**
 * All "put" clients are implemented in same way. They only vary in their requestBody. This class brings all the common codes together
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

    public LiBasePutClient(Context context, String basePath, Map<String, String> additionalHttpHeaders) throws LiRestResponseException {
        super(context, basePath, RequestType.PUT);
        this.additionalHttpHeaders = additionalHttpHeaders;
    }

    /**
     * Sets request body and initializes {@link LiRestV2Request}.
     */
    @Override
    public void setLiRestV2Request() {
        this.requestBody = postModel.toJsonString();
        this.liRestV2Request = new LiRestV2Request(context, LiBaseRestRequest.RestMethod.PUT, requestBody, additionalHttpHeaders);
    }

    @Override
    public String getRequestBody() {
        return requestBody;
    }
}
