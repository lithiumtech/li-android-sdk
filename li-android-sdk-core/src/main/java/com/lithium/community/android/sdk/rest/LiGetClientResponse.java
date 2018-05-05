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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import com.lithium.community.android.sdk.exception.LiRestResponseException;
import com.lithium.community.android.sdk.model.LiBaseModel;
import com.lithium.community.android.sdk.model.response.LiBrowse;
import com.lithium.community.android.sdk.utils.LiCoreSDKUtils;

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

    public LiGetClientResponse(final LiBaseResponse liBaseResponse, final String type,
            final Class<? extends LiBaseModel> baseModelClass,
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
     *
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
