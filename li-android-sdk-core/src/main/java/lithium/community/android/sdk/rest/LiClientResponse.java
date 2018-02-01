/*
 * LiClientResponse.java
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

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.response.LiBrowse;

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
