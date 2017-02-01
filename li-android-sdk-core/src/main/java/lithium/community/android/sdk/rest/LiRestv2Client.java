/*
 * LiRestv2Client.java
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

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;

/**
 * Rest v2 client for community. Always expects LiQL queries in requests
 */
public class LiRestv2Client extends LiRestClient {

    private static final String LOG_TAG = "LiRestv2Client";

    private LiRestv2Client() throws LiRestResponseException {
        super();
    }

    /**
     * Returns a singleton instance of this class.
     *
     * @return Instance of this class.
     */
    public static LiRestv2Client getInstance() {
        return LiRestv2ClientInitializer.LI_RESTV_2_CLIENT;
    }

    /**
     * Makes Sync call.
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @throws LiRestResponseException
     */
    @Override
    public LiBaseResponse processSync(@NonNull LiBaseRestRequest baseRestRequest) throws LiRestResponseException {

        LiCoreSDKUtils.checkNotNull(baseRestRequest);
        LiRestV2Request restV2Request = getLiRestV2Request(baseRestRequest);
        LiBaseResponse response = super.processSync(restV2Request);
        return validateResponse(response);
    }

    /**
     * Wraps LiBaseRestRequest to LiRestV2Request.
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @return LiRestV2Request {@link LiRestV2Request}
     * @throws LiRestResponseException
     */
    @NonNull
    private LiRestV2Request getLiRestV2Request(@NonNull LiBaseRestRequest baseRestRequest) throws LiRestResponseException {
        LiCoreSDKUtils.checkNotNull(baseRestRequest);

        if (baseRestRequest instanceof LiBaseRestRequest == false) {
            Log.e(LOG_TAG, "Invalid rest v2 request");
            throw LiRestResponseException.illegalArgumentError("Rest v2 request should pass a liql query request parameter");
        }

        LiRestV2Request restV2Request = (LiRestV2Request) baseRestRequest;
        return restV2Request;
    }

    /**
     * Makes Async call.
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callBack {@link LiAsyncRequestCallback}
     */
    @Override
    public void processAsync(@NonNull LiBaseRestRequest baseRestRequest, @NonNull LiAsyncRequestCallback callBack) {

        LiCoreSDKUtils.checkNotNull(baseRestRequest, callBack);

        if (baseRestRequest instanceof LiBaseRestRequest == false) {
            Log.e(LOG_TAG, "Invalid rest v2 request");
            callBack.onError(LiRestResponseException.illegalArgumentError("Rest v2 request should pass a liql query request parameter"));
        }

        LiRestV2Request restV2Request = null;
        try {
            restV2Request = getLiRestV2Request(baseRestRequest);
        } catch (LiRestResponseException e) {
            callBack.onError(e);
        }
        super.processAsync(restV2Request, callBack);
    }

    /**
     * Makes Async call for uploading an image.
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callBack {@link LiAsyncRequestCallback}
     * @param imagePath Absolute path of the image file.
     * @param imageName Name of the image file.
     * @param requestBody request body for post call to upload image.
     */
    public void uploadProcessAsync(@NonNull LiBaseRestRequest baseRestRequest,
                                   @NonNull LiAsyncRequestCallback callBack, String imagePath, String imageName, String requestBody) {

        LiCoreSDKUtils.checkNotNull(baseRestRequest, callBack);

        if (baseRestRequest instanceof LiBaseRestRequest == false) {
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

    /**
     * Initializer for LiRestV2Client.
     */
    private static class LiRestv2ClientInitializer {
        private static final LiRestv2Client LI_RESTV_2_CLIENT;

        static {
            LiRestv2Client tempLiRestv2Client = null;
            try {
                tempLiRestv2Client = new LiRestv2Client();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Failed to initialized LiRestV2Client: " + e);
            }
            LI_RESTV_2_CLIENT = tempLiRestv2Client;
        }

    }
}
