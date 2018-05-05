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

/**
 * Extracts response parameters from LiBaseResponse
 * Created by shoureya.kant on 11/21/16.
 */
public interface LiClientResponse<T> {

    /**
     * Returns base response caste to type specified.
     */
    public T getResponse();

    /**
     * Returns map containing information of parent and all its child nodes.
     */
    public Map<LiBrowse, List<LiBaseModel>> getTransformedResponse();

    /**
     * Returns status.
     */
    public String getStatus();

    /**
     * Returns message.
     */
    public String getMessage();

    /**
     * Returns Http Code.
     */
    public int getHttpCode();

    /**
     * Returns Json Object.
     */
    public JsonObject getJsonObject();
}
