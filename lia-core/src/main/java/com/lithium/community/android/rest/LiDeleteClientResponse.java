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
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.response.LiBrowse;

import java.util.List;
import java.util.Map;

/**
 * Wrapper for LiBaseResponse to DELETE response.
 * Created by kunal.shrivastava on 5/4/17.
 */

public class LiDeleteClientResponse implements LiClientResponse<LiBaseResponse> {
    private LiBaseResponse liBaseResponse;

    public LiDeleteClientResponse(final LiBaseResponse liBaseResponse) {
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
