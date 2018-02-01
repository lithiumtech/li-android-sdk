/*
 * BasePostModel.java
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kunal.shrivastava on 10/26/16.
 * <p>
 * Base class for post request model, implement {@link LiPostModel}. This class provides implementation of
 * toJsonString() method and toJson() method,
 * to help in serialization and deserialization of post model object. All post request model must extend this base
 * class,
 * to take advantage of serialization and deserialization.
 */

public class LiBasePostModel implements LiPostModel {

    @Override
    public JsonObject toJson() {
        Gson gson = new Gson();
        Data data = new Data(this);
        JsonElement jsonElement = gson.toJsonTree(data);
        return jsonElement.getAsJsonObject();
    }

    @Override
    public String toJsonString() {
        Gson gson = new Gson();
        Data data = new Data(this);
        return gson.toJson(data);
    }

    protected class Data {
        private LiPostModel data;

        Data(LiPostModel data) {
            this.data = data;
        }
    }
}
