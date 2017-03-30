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

import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.model.LiBaseModelImpl;

/**
 * Data Model for GET call to fetch messages a user is subscribed to.
 * Created by saiteja.tokala on 10/21/16.
 */

public class LiSubscriptions extends LiBaseModelImpl implements LiBaseMessageModel {

    private String type;
    @SerializedName("id")
    private Long id;
    @SerializedName("target")
    private LiMessage liMessage;
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

    //We use this jackson trick to have just pure values instead of lithium objects
    //the downside is serialization again will produce something we cannot deserialize
    public void setId(LiBaseModelImpl.LiInt result) {
        this.id = result.getValue();
    }

    public LiMessage getLiMessage() {
        return liMessage;
    }

    public void setLiMessage(LiMessage result) {
        this.liMessage = result;
    }
}
