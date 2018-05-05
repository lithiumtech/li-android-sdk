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

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.response.LiBrowse;
/*
 * Created by shoureya.kant on 11/21/16.
 */

/**
 * Wrapper for LiBaseResponse to POST response. Represents the response to a POST action.
 * The class includes methods to get the response, and to get specific details about
 * the response like the HTTP code and status, and the JSON object if one is included.
 */


public class LiPostClientResponse implements LiClientResponse<LiBaseResponse> {
    private LiBaseResponse liBaseResponse;

    public LiPostClientResponse(final LiBaseResponse liBaseResponse) {
        this.liBaseResponse = liBaseResponse;
    }

    public LiBaseResponse getResponse() {
        return liBaseResponse;
    }

    public Map<LiBrowse, List<LiBaseModel>> getTransformedResponse() {
        return null;
    }

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
