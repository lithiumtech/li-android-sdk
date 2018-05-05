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

package com.lithium.community.android.exception;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKUtils;

/**
 * Wrapper rest api error responses.
 *
 * @see <ahref="http://community.lithium.com/t5/Developer-Documentation/bd-p/dev-doc-portal?section=commv2&v2.main
 * =errorresponses"
 * Rest v2 error responses </a>
 */

public class LiRestResponseException extends Exception {

    /**
     * The http error code
     */
    private final int httpCode;

    /**
     * The error code describing
     */
    private final int liErrorCode;

    /**
     * The error message.
     */
    @Nullable
    private final String error;

    /**
     * Instantiates an authorization request with optional root cause information.
     */
    public LiRestResponseException(int httpCode, @Nullable String error, int code) {
        super(error);
        this.httpCode = httpCode;
        this.error = error;
        this.liErrorCode = code;
    }

    /**
     * Reconstructs an {@link LiRestResponseException} from the JSON
     */
    public static LiRestResponseException fromJson(@NonNull String jsonStr, Gson gson) {
        LiCoreSDKUtils.checkNotNullAndNotEmpty(jsonStr, "jsonStr cannot be null or empty");
        JsonObject error = gson.fromJson(jsonStr, JsonObject.class);
        JsonObject data = error.get("data").getAsJsonObject();
        return new LiRestResponseException(error.get("http_code").getAsInt(),
                error.get("message").getAsString(),
                data.get("code").getAsInt());
    }

    /**
     * wrapping network error
     *
     * @param errorDescription Description for network error.
     * @return networkError wrapped into LiRestResponseException.
     */
    public static LiRestResponseException networkError(@Nullable String errorDescription) {
        return new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVICE_UNAVAILABLE, errorDescription,
                LiSDKErrorCodes.NETWORK_ERROR);
    }

    /**
     * wrapping json serialization error
     *
     * @param errorDescription Description for json serialization error.
     * @return jsonSerializationError wrapped into LiRestResponseException.
     */
    public static LiRestResponseException jsonSerializationError(@Nullable String errorDescription) {
        return new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR,
                errorDescription, LiSDKErrorCodes.JSON_SERIALIZATION_ERROR);
    }

    /**
     * wrapping illegal argument error
     *
     * @param errorDescription Description for illegal argument error.
     * @return illegalArgumentError wrapped into LiRestResponseException.
     */
    public static LiRestResponseException illegalArgumentError(@Nullable String errorDescription) {
        return new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR, errorDescription,
                LiSDKErrorCodes.ILLEGAL_ARG_ERROR);
    }

    /**
     * wrapping json runtime error
     *
     * @param errorDescription Description for runtime error.
     * @return runtimeError wrapped into LiRestResponseException.
     */
    public static LiRestResponseException runtimeError(@Nullable String errorDescription) {
        return new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVICE_UNAVAILABLE, errorDescription,
                LiSDKErrorCodes.RUNTIME_ERROR);
    }

    /**
     * wrapping json syntax error
     *
     * @param errorDescription Description for json syntax error.
     * @return jsonSyntaxError wrapped into LiRestResponseException.
     */
    public static LiRestResponseException jsonSyntaxError(@Nullable String errorDescription) {
        return new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR, errorDescription,
                LiSDKErrorCodes.JSON_SYNTAX_ERROR);
    }

    /**
     * Exceptions are considered to be equal if their {@link #httpCode type} and {@link #liErrorCode code}
     * are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || (obj.getClass() != this.getClass())) {
            return false;
        }

        LiRestResponseException other = (LiRestResponseException) obj;
        return this.httpCode == other.httpCode && this.liErrorCode == other.liErrorCode;
    }

    //Returns general network parameters

    public int getHttpCode() {
        return httpCode;
    }

    public int getLiErrorCode() {
        return liErrorCode;
    }

    public String getError() {
        return error;
    }

}
