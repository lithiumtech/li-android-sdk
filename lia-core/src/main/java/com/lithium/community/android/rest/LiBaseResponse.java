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

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Base response object that encapsulates Lithium community json response
 */
public class LiBaseResponse {
    private static final String DATA = "data";
    private static final String TYPE = "type";
    private static final String ITEMS = "items";
    private static final String ITEM = "item";
    private static final String STATUS = "status";
    private String status;
    private String message;
    private int code;
    private JsonObject data;

    public LiBaseResponse() {

    }

    /**
     * Wrapping OkHttp response to LiBaseResponse.
     *
     * @param response {@link Response}
     * @throws IOException If the reponse if not a valid JSON Object string.
     */
    public LiBaseResponse(Response response) throws IOException {

        code = response.code();
        String responseStr = response.body().string();
        try {
            data = LiClientManager.getRestClient().getGson().fromJson(responseStr, JsonObject.class);
            if (data.has(STATUS)) {
                status = data.get(STATUS).getAsString();
            } else {
                status = response.isSuccessful() ? "success" : "error";
            }
            message = response.message();
        } catch (Exception ex) {
            Log.e(LiCoreSDKConstants.LI_ERROR_LOG_TAG, "Response deserialization failed");
            ex.printStackTrace();
            code = LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR;
            status = "error";
            message = ex.getMessage();
        }
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpCode() {
        return code;
    }

    public void setHttpCode(int httpCode) {
        this.code = httpCode;
    }

    /**
     * Deserialize response from given type and LiBaseModelImpl class
     *
     * @throws LiRestResponseException
     */
    public List<LiBaseModel> toEntityList(final String type, final Class<? extends LiBaseModel> baseModelClass,
            final Gson gson) throws LiRestResponseException {
        final String objectNamePlural = type + "s";
        return singleEntityOrListFromJson(data, objectNamePlural, type, baseModelClass, gson);
    }

    private List<LiBaseModel> singleEntityOrListFromJson(final JsonElement node, final String objectNamePlural,
            final String objectName, final Class<? extends LiBaseModel> baseModelClass,
            final Gson gson) throws LiRestResponseException {
        if (node != null && node.getAsJsonObject().has(DATA)) {
            JsonObject response = node.getAsJsonObject().get(DATA).getAsJsonObject();
            if (!response.has(TYPE)) {
                throw LiRestResponseException.jsonSerializationError("Required data type not found in response");
            }

            String type = response.get(TYPE).getAsString();
            if (!type.equalsIgnoreCase(objectName) && !type.equalsIgnoreCase(objectNamePlural)) {
                throw LiRestResponseException.illegalArgumentError("Required object type not found in response");
            }

            if (type.equalsIgnoreCase(objectNamePlural) && response.get(ITEMS).isJsonArray()) {
                JsonArray jsonArray = response.get(ITEMS).getAsJsonArray();
                List<LiBaseModel> elementList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject element = jsonArray.get(i).getAsJsonObject();
                    try {
                        elementList.add(gson.fromJson(element, baseModelClass));
                    } catch (IllegalStateException | JsonSyntaxException exception) {
                        throw LiRestResponseException.jsonSerializationError(
                                "Bad json response:" + exception.getMessage());
                    }
                }
                return elementList;
            } else if (type.equalsIgnoreCase(objectName) && response.get(ITEM).isJsonObject()) {
                final List<LiBaseModel> objects = new ArrayList<>();
                objects.add(gson.fromJson(response.get(ITEM).getAsJsonObject(), baseModelClass));
                return objects;
            } else {
                throw LiRestResponseException.jsonSerializationError("Unable to parse " + objectName + " or " + objectNamePlural + " from json:" + node);
            }
        } else {
            throw LiRestResponseException.jsonSerializationError("Server Error. Please check logs");
        }
    }

}
