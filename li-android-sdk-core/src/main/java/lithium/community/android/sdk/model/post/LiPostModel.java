/*
 * IPostModel.java
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

package lithium.community.android.sdk.model.post;

import com.google.gson.JsonObject;

/**
 * Created by kunal.shrivastava on 10/26/16.
 * <p>
 * Interface for Post Model class
 */

public interface LiPostModel {

    /**
     * Method to create JsonObject of the class, for post request
     *
     * @return
     */
    public JsonObject toJson();

    /**
     * Method to create JsonString of class, for post request
     *
     * @return
     */
    public String toJsonString();
}
