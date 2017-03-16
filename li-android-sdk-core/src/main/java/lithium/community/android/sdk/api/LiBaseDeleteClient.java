/*
 * LiBaseDeleteClient.java
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
import lithium.community.android.sdk.rest.LiRestV2Request;

/**
 * This client is use to handle DELETE requests.
 * Created by shoureya.kant on 12/27/16.
 */

public class LiBaseDeleteClient extends LiBaseClient {

    public LiBaseDeleteClient(Context context, String basePath) throws LiRestResponseException {
        super(context, basePath, RequestType.DELETE);
    }

    @Override
    public void setLiRestV2Request() {
        this.liRestV2Request = new LiRestV2Request(context);
    }

    @Override
    public String getRequestBody() {
        return null;
    }
}
