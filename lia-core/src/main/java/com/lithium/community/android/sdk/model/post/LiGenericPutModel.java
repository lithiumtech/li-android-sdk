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

package com.lithium.community.android.sdk.model.post;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Request body for making any PUT call. It requires parameter as JsonObject.
 * Created by shoureya.kant on 4/11/17.
 */
public class LiGenericPutModel extends LiBasePostModel {

    private JsonObject data;

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    @Override
    public JsonObject toJson() {
        return data.getAsJsonObject();
    }

    @Override
    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
