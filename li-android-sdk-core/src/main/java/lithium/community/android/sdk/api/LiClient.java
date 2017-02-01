/*
 * LiClient.java
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

package lithium.community.android.sdk.api;

import com.google.gson.Gson;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.queryutil.LiQueryOrdering;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiClientResponse;

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
     * Method to make client request a sync call
     *
     * @return LiBaseResponse This is OkHttp reponse wrapped into LiBaseResponse.
     * @throws LiRestResponseException
     */
    public LiClientResponse processSync() throws LiRestResponseException;

    /**
     * Method to make client request to upload an image in a  async call
     *
     * @param callback {@link LiAsyncRequestCallback}
     * @param imageName This is name of the image file.
     * @param imagePath This is absolute path of image.
     * @throws LiRestResponseException
     */
    public void processAsync(LiAsyncRequestCallback callback, String imagePath, String imageName) throws LiRestResponseException;

    /**
     * Method to return {@link lithium.community.android.sdk.rest.LiRestClient#gson}
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
     * @param liQueryOrdering {@link LiQueryOrdering}
     */
    public LiClient setOrdering(LiQueryOrdering liQueryOrdering);
}

