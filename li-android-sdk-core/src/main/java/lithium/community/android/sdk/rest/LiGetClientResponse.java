/*
 * LiGetClientResponse.java
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
 *
 * Created by kunal.shrivastava on 11/3/16.
 */

package lithium.community.android.sdk.rest;

import com.google.gson.Gson;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.response.LiBrowse;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;

/**
 * Wrapper for LiBaseResponse to GET response. Represents the response to a GET action.
 * The class includes methods to get the response (transformed to the corresponding data model),
 * and to get details like the HTTP code and status of the response.
 */

public class LiGetClientResponse implements LiClientResponse<List<LiBaseModel>> {
    Gson gson;
    private LiBaseResponse liBaseResponse;
    private String type;
    private Class<? extends LiBaseModel> baseModelClass;

    public LiGetClientResponse(final LiBaseResponse liBaseResponse, final String type, final Class<? extends LiBaseModel> baseModelClass,
                               final Gson gson) {
        this.liBaseResponse = liBaseResponse;
        this.type = type;
        this.baseModelClass = baseModelClass;
        this.gson = gson;
    }

    /**
     * Transforms LibaseResponse to corresponding data model.
     */
    public List<LiBaseModel> getResponse() {
        try {
            return liBaseResponse.toEntityList(type, baseModelClass, gson);
        } catch (LiRestResponseException e) {
            return null;
        }
    }

    /**
     * Returns map containing information of parent and all its child nodes.
     * @return Map with key as parent node and value as list of children.
     */
    public Map<LiBrowse, List<LiBaseModel>> getTransformedResponse() {
        List<LiBaseModel> response = null;
        try {
            response = liBaseResponse.toEntityList(type, baseModelClass, gson);
            return LiCoreSDKUtils.getTransformedResponse(response);
        } catch (LiRestResponseException e) {
            return null;
        }
    }

    //Below methods return general http fields.

    @Override
    public String getStatus() {
        return liBaseResponse.getStatus();
    }

    @Override
    public String getMessage() {
        return liBaseResponse.getMessage();
    }

    @Override
    public int getHttpCode() {
        return liBaseResponse.getHttpCode();
    }

    @Override
    public JsonObject getJsonObject() {
        return liBaseResponse.getData();
    }
}
