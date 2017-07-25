/*
 * LiSubscriptions.java
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

package lithium.community.android.sdk.model.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.helpers.LiBoard;

/**
 * Data Model for GET call to fetch messages a user is subscribed to.
 * Created by saiteja.tokala on 10/21/16.
 */

public class LiSubscriptions extends LiBaseModelImpl implements LiTargetModel {

    private String type;
    @SerializedName("id")
    private Long id;
    @SerializedName("target")
    private JsonObject targetObject;
    @SerializedName("subscriber")
    private LiUser user;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public LiUser getUser() {
        return user;
    }
    public void setUser(LiUser user) {
        this.user = user;
    }
    public Long getId() {
        return id;
    }

    public void setId(LiBaseModelImpl.LiInt result) {
        this.id = result.getValue();
    }

    public LiMessage getLiMessage() {
        if(targetObject.has("type") && targetObject.get("type").getAsString().equals("message")) {
            try {
                return LiClientManager.getRestClient().getGson().fromJson(targetObject.toString(), LiMessage.class);
            } catch (LiRestResponseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public LiBoard getLiBoard() {
        if(targetObject.has("type") && targetObject.get("type").getAsString().equals("board")) {
            try {
                return LiClientManager.getRestClient().getGson().fromJson(targetObject.toString(), LiBoard.class);
            } catch (LiRestResponseException e) {
                return null;
            }
        } else {
            return null;
        }
    }
    public JsonObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(JsonObject targetObject) {
        this.targetObject = targetObject;
    }
}
