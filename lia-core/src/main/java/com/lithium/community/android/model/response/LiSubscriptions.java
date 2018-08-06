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

package com.lithium.community.android.model.response;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.helpers.LiBoard;

/**
 * Data Model for GET call to fetch messages a user is subscribed to.
 * Created by saiteja.tokala on 10/21/16.
 */

public class LiSubscriptions extends LiBaseModelImpl implements LiTargetModel {

    private String type;
    @SerializedName("id")
    private Long id;
    @SerializedName("target")
    private LiMessage targetObject;
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
        if (targetObject != null) {
            return targetObject;
        } else {
            return null;
        }
    }

    public LiBoard getLiBoard() {
        return null;//subscriptions will not have board
    }

    public LiMessage getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(LiMessage targetObject) {
        this.targetObject = targetObject;
    }

    public static LiSubscriptions fromJson(String json) {
        return new GsonBuilder().create().fromJson(json, LiSubscriptions.class);
    }
}
