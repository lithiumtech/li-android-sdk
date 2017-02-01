/*
 * LiBaseRestRequest.java
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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Base rest request to community
 */
public class LiBaseRestRequest {

    /**
     * application/json media type
     */
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * utf_8 charset
     */
    public static final String UTF_8 = StandardCharsets.UTF_8.name();
    private final RestMethod method;
    private final RequestBody requestBody;
    private final Map<String, String> additionalHttpHeaders;
    private final Map<String, String> queryParams;
    protected String path;
    private boolean isAuthenticatedRequest = false;

    public LiBaseRestRequest(RestMethod method, String path, RequestBody requestBody,
                             Map<String, String> additionalHttpHeaders, boolean isAuthenticatedRequest) {
        this.method = method;
        this.path = path;
        this.requestBody = requestBody;
        this.additionalHttpHeaders = additionalHttpHeaders;
        this.isAuthenticatedRequest = isAuthenticatedRequest;
        this.queryParams = new HashMap<>();
    }


    public LiBaseRestRequest(RestMethod method, RequestBody requestBody,
                             Map<String, String> additionalHttpHeaders, boolean isAuthenticatedRequest) {
        this.method = method;
        this.requestBody = requestBody;
        this.additionalHttpHeaders = additionalHttpHeaders;
        this.isAuthenticatedRequest = isAuthenticatedRequest;
        this.queryParams = new HashMap<>();
    }

    public RestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Map<String, String> getAdditionalHttpHeaders() {
        return additionalHttpHeaders;
    }

    //Checks whether request if from an authenticated user.
    public boolean isAuthenticatedRequest() {
        return isAuthenticatedRequest;
    }

    /**
     * This method segregates query parameters which may be present afte the 'q' param which takes in LIQL.
     * @param param It is the query parameter 'q' which taken in LIQL as value.
     * @param value It is the LIQL for above parameter 'q'.
     */
    public void addQueryParam(String param, String value) {
        if (param.equals("q") && (value != null) && value.contains("&")) {
            String[] values = value.split("&");
            queryParams.put(param, value);
            boolean isFirst = true;
            for (String valueStr : values) {
                if (isFirst) {
                    isFirst = false;
                    queryParams.put(param, valueStr);
                }
                else {
                    String[] qParams = valueStr.split("=");
                    if (qParams.length > 1) {
                        queryParams.put(qParams[0],qParams[1]);
                    }
                    else {
                        queryParams.put(qParams[0], "");
                    }
                }
            }
        }
        else {
            queryParams.put(param, value);
        }
    }

    public void addRequestHeader(String header, String value) {
        additionalHttpHeaders.put(header, value);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    /**
     * Enumeration for all HTTP methods.
     */
    public enum RestMethod {
        GET, POST, PUT, DELETE, HEAD, PATCH
    }
}
