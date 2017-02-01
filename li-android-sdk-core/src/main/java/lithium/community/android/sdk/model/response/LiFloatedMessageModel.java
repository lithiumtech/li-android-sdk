/*
 * LiFloatedMessageModel.java
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

import lithium.community.android.sdk.model.LiBaseModelImpl;

/**
 * Data Model for GET call to fetch pinned message for a user.
 * Created by shoureya.kant on 12/1/16.
 */

public class LiFloatedMessageModel extends LiBaseModelImpl {

    private String id;
    private String href;
    private LiUser user;
    private LiMessage message;
    private String scope;

    public LiMessage getMessage() {
        return message;
    }

    public void setMessage(LiMessage message) {
        this.message = message;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LiUser getUser() {
        return user;
    }

    public void setUser(LiUser user) {
        this.user = user;
    }
}
