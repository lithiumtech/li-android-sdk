/*
 * LiPostClientResponse.java
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
 * Wrapper for LiBaseResponse to POST response.
 * Created by shoureya.kant on 11/21/16.
 */

public class LiPostClientResponse implements LiClientResponse<LiBaseResponse> {
    private LiBaseResponse liBaseResponse;

    public LiPostClientResponse(final LiBaseResponse liBaseResponse) {
        this.liBaseResponse = liBaseResponse;
    }

    public LiBaseResponse getResponse() {
        return liBaseResponse;
    }

    public Map<LiBrowse, List<LiBaseModel>> getTransformedResponse() {
        return null;
    }

    @Override
    public String getStatus() {
        return liBaseResponse.getStatus();
    }

    @Override
    public String getMessage() {
        return liBaseResponse.getMessage();
    }

    @Override
    public int getHttpCode() {
        return liBaseResponse.getHttpCode();
    }

    @Override
    public JsonObject getJsonObject(){
        return liBaseResponse.getData();
    }
}
