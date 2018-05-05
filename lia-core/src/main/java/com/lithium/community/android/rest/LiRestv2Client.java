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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import com.lithium.community.android.exception.LiInitializationException;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.MessageConstants;

/**
 * Rest v2 client for community. Always expects LiQL queries in requests
 */
public class LiRestv2Client extends LiRestClient {

    private static final String LOG_TAG = "LiRestv2Client";

    private static LiRestv2Client instance;

    private LiRestv2Client(@NonNull LiSDKManager manager) throws LiInitializationException {
        super(manager);
    }

    /**
     * <p>
     * Initializes the Rest Client singleton. If the initialization is successful {@link #getInstance()} will
     * return an initialized SDK Manager. This function may or may not create a new instance or reinitialize
     * with subsequent calls.
     * </p>
     *
     * @param manager A Li SDK manager
     * @throws LiInitializationException if the Rest client fails to initialize.
     */
    public static void initialize(@NonNull LiSDKManager manager) throws LiInitializationException {
        instance = new LiRestv2Client(manager);
    }

    /**
     * Returns a singleton instance of this class.
     *
     * @return Instance of this class.
     */
    @Nullable
    public static LiRestv2Client getInstance() {
        if (instance == null) {
            try {
                // TODO: remove this in next major version.
                //noinspection ConstantConditions to support backwards compatibility
                initialize(LiSDKManager.getInstance());
            } catch (LiInitializationException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Makes Sync call.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @throws LiRestResponseException
     */
    @Override
    public LiBaseResponse processSync(@NonNull LiBaseRestRequest baseRestRequest) throws LiRestResponseException {
        LiCoreSDKUtils.checkNotNull(baseRestRequest, MessageConstants.wasNull("baseRestRequest"));
        LiRestV2Request restV2Request = getLiRestV2Request(baseRestRequest);
        LiBaseResponse response = super.processSync(restV2Request);

        return validateResponse(response);
    }

    /**
     * Wraps LiBaseRestRequest to LiRestV2Request.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @return LiRestV2Request {@link LiRestV2Request}
     * @throws LiRestResponseException
     */
    @NonNull
    private LiRestV2Request getLiRestV2Request(@NonNull LiBaseRestRequest baseRestRequest) throws LiRestResponseException {
        LiCoreSDKUtils.checkNotNull(baseRestRequest, MessageConstants.wasNull("baseRestRequest"));
        if (!(baseRestRequest instanceof LiBaseRestRequest)) {
            Log.e(LOG_TAG, "Invalid rest v2 request");
            throw LiRestResponseException.illegalArgumentError("Rest v2 request should pass a liql query request parameter");
        }

        return (LiRestV2Request) baseRestRequest;
    }

    /**
     * Makes Async call.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callBack        {@link LiAsyncRequestCallback}
     */
    @Override
    public void processAsync(@NonNull LiBaseRestRequest baseRestRequest, @NonNull LiAsyncRequestCallback callBack) {

        LiCoreSDKUtils.checkNotNull(baseRestRequest, MessageConstants.wasNull("baseRestRequest"));
        LiCoreSDKUtils.checkNotNull(callBack, MessageConstants.wasNull("callback"));

        if (!(baseRestRequest instanceof LiBaseRestRequest)) {
            Log.e(LOG_TAG, "Invalid rest v2 request");
            callBack.onError(LiRestResponseException.illegalArgumentError("Rest v2 request should pass a liql query request parameter"));
        }

        LiRestV2Request restV2Request = null;
        try {
            restV2Request = getLiRestV2Request(baseRestRequest);
        } catch (LiRestResponseException e) {
            callBack.onError(e);
        }
        if (restV2Request != null) {
            super.processAsync(restV2Request, callBack);
        }
    }

    /**
     * Makes Async call for uploading an image.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callBack        {@link LiAsyncRequestCallback}
     * @param imagePath       Absolute path of the image file.
     * @param imageName       Name of the image file.
     * @param requestBody     request body for post call to upload image.
     */
    public void uploadProcessAsync(@NonNull LiBaseRestRequest baseRestRequest, @NonNull LiAsyncRequestCallback callBack, String imagePath,
                                   String imageName, String requestBody) {

        LiCoreSDKUtils.checkNotNull(baseRestRequest, MessageConstants.wasNull("baseRestRequest"));
        LiCoreSDKUtils.checkNotNull(callBack, MessageConstants.wasNull("callBack"));

        if (!(baseRestRequest instanceof LiBaseRestRequest)) {
            Log.e(LOG_TAG, "Invalid rest v2 request");
            callBack.onError(LiRestResponseException.illegalArgumentError("Rest v2 request should pass a liql query request parameter"));
        }

        LiRestV2Request restV2Request = null;
        try {
            restV2Request = getLiRestV2Request(baseRestRequest);
        } catch (LiRestResponseException e) {
            callBack.onError(e);
        }

        super.uploadImageProcessAsync(baseRestRequest, callBack, imagePath, imageName, requestBody);
    }

    /**
     * Validating the network response.
     *
     * @param response {@link LiBaseResponse}
     * @return LiBaseResponse {@link LiBaseResponse}
     * @throws LiRestResponseException
     */
    private LiBaseResponse validateResponse(LiBaseResponse response) throws LiRestResponseException {
        String jsonStr = null;
        try {
            jsonStr = asString(response);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error deserializing json");
            throw LiRestResponseException.jsonSerializationError("Error deserializing rest response");
        }
        Gson gson = getGson();
        JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
        if (jsonObject.has("http_code")) {
            int httpResponeCode = jsonObject.get("http_code").getAsInt();
            if (httpResponeCode == 200 || httpResponeCode == 201) {
                response.setData(jsonObject);
                return response;
            } else {
                Log.e(LOG_TAG, "Error response from server");
                throw LiRestResponseException.fromJson(jsonObject.getAsString(), gson);
            }
        } else {
            Log.e(LOG_TAG, "Error response from server");
            throw LiRestResponseException.fromJson(jsonObject.getAsString(), gson);
        }
    }
}
