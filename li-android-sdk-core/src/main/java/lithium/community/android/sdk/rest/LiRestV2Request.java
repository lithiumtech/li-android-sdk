/*
 * LiRestV2Request.java
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

package lithium.community.android.sdk.rest;

import android.content.Context;

import java.util.Map;

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
        super(context, RestMethod.GET, null, null, true);
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
