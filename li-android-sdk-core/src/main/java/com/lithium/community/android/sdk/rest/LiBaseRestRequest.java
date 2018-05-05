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

import android.content.Context;
import android.text.TextUtils;

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
    private String path;
    private Context context;
    private boolean isAuthenticatedRequest = false;

    public LiBaseRestRequest(Context context, RestMethod method, String path, RequestBody requestBody,
            Map<String, String> additionalHttpHeaders, boolean isAuthenticatedRequest) {
        this.context = context;
        this.method = method;
        this.path = path;
        this.requestBody = requestBody;
        this.additionalHttpHeaders = additionalHttpHeaders;
        this.isAuthenticatedRequest = isAuthenticatedRequest;
        this.queryParams = new HashMap<>();
    }


    public LiBaseRestRequest(Context context, RestMethod method, RequestBody requestBody,
            Map<String, String> additionalHttpHeaders, boolean isAuthenticatedRequest) {
        this.context = context;
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
     *
     * @param queryParameters
     */
    public void addQueryParam(String queryParameters) {
        if (TextUtils.isEmpty(queryParameters)) {
            return;
        }
        if (queryParameters.toUpperCase().startsWith("SELECT ")) {
            if (queryParameters.contains("&")) {
                String[] queryParamsArr = queryParameters.split("&");
                queryParams.put("q", queryParamsArr[0]);
                for (int i = 1; i < queryParamsArr.length; i++) {
                    String[] qParams = queryParamsArr[i].split("=");
                    String key = qParams[0];
                    String value = (qParams.length > 1) ? qParams[1] : null;
                    queryParams.put(key, value);
                }
            } else {
                queryParams.put("q", queryParameters);
            }
        } else {
            String[] queryParamsArr = queryParameters.split("&");
            for (String temp : queryParamsArr) {
                String[] qParams = temp.split("=");
                String key = qParams[0];
                String value = (qParams.length > 1) ? qParams[1] : null;
                queryParams.put(key, value);
            }
        }
    }

    public void addRequestHeader(String header, String value) {
        additionalHttpHeaders.put(header, value);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Enumeration for all HTTP methods.
     */
    public enum RestMethod {
        GET, POST, PUT, DELETE, HEAD, PATCH
    }
}
