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

package com.lithium.community.android.api;

import com.google.gson.Gson;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.queryutil.LiQueryOrdering;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiClientResponse;
import com.lithium.community.android.rest.LiRestClient;

/**
 * Created by kunal.shrivastava on 11/2/16.
 * Client interface to be implement by all api clients
 */

public interface LiClient {

    /**
     * Method to make client request an async call
     *
     * @param callback {@link LiAsyncRequestCallback}
     * @throws LiRestResponseException
     */
    public void processAsync(LiAsyncRequestCallback callback) throws LiRestResponseException;

    /**
     * Method to make client request to upload an image in a  async call
     *
     * @param callback  {@link LiAsyncRequestCallback}
     * @param imageName This is name of the image file.
     * @param imagePath This is absolute path of image.
     *                  Note: the imageName and the filename in the absolute path param above must be equal.
     * @throws LiRestResponseException
     */
    public void processAsync(LiAsyncRequestCallback callback, String imagePath, String imageName) throws LiRestResponseException;

    /**
     * Method to make client request a sync call
     *
     * @return LiBaseResponse This is OkHttp reponse wrapped into LiBaseResponse.
     * @throws LiRestResponseException
     */
    public LiClientResponse processSync() throws LiRestResponseException;

    /**
     * Method to return {@link LiRestClient#gson}
     *
     * @return Gson object
     */
    public Gson getGson();

    /**
     * Method to return type
     *
     * @return String depicting type.
     */
    public String getType();

    /**
     * Method to get Request Type
     *
     * @return RequestType whether  GET, POST, DELETE, PUT.
     */
    public LiBaseClient.RequestType getRequestType();

    /**
     * Method to set Ordering in LIQL where clause.
     *
     * @param liQueryOrdering {@link LiQueryOrdering}
     */
    public LiClient setOrdering(LiQueryOrdering liQueryOrdering);
}

