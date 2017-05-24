/*
 * LiBaseResponse.java
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.LiBaseModel;
import okhttp3.Response;

/**
 * Base response object that encapsulates Lithium community json response
 */
public class LiBaseResponse {
    private static final String DATA = "data";
    private static final String TYPE = "type";
    private static final String ITEMS = "items";
    private static final String ITEM = "item";
    private String status;
    private String message;

    @SerializedName("http_code")
    private int httpCode;

    private JsonObject data;

    public LiBaseResponse() {

    }

    /**
     * Wrapping OkHttp response to LiBaseResponse.
     * @param response {@link Response}
     * @throws IOException
     * @throws LiRestResponseException
     */
    public LiBaseResponse(Response response) throws IOException, LiRestResponseException {

        httpCode = response.code();
        String responseStr = response.body().string()+"}";
        try {
            data = LiClientManager.getRestClient().getGson().fromJson(responseStr, JsonObject.class);
            if (httpCode == 500) {
                if (data.has("statusCode")) {
                    httpCode = data.get("statusCode").getAsInt();
                }
            }
            status = response.isSuccessful() ? "success" : "error";
            message = response.message();
        }
        catch(JsonSyntaxException ex){
//            throw LiRestResponseException.jsonSyntaxError("Improper Json syntax received in response");
            httpCode = 500;
            status = "error";
            message = response.message();
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
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
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
                                                         final String objectName, final Class<? extends LiBaseModel> baseModelClass, final Gson gson) throws LiRestResponseException {
        if (node.getAsJsonObject().has(DATA)) {
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
                        throw LiRestResponseException.jsonSerializationError("Bad json response:" + exception.getMessage());
                    }
                }
                return elementList;
            } else if (type.equalsIgnoreCase(objectName) && response.get(ITEM).isJsonObject()) {
                final List<LiBaseModel> objects = new ArrayList<>();
                objects.add(gson.fromJson(response.get(ITEM).getAsJsonObject(), baseModelClass));
                return objects;
            } else {
                throw LiRestResponseException.jsonSerializationError("Unable to parse " + objectName + " or " +
                        objectNamePlural + " from json:" + node);
            }
        } else {
            throw LiRestResponseException.jsonSerializationError("Server Error. Please check logs");
        }
    }

}
