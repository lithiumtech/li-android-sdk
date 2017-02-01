/*
 * LiReplyMessageModel.java
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

import lithium.community.android.sdk.model.response.LiMessage;

/**
 * Request body for POST call to reply to a message.
 * Created by shoureya.kant on 11/22/16.
 */

public class LiReplyMessageModel extends LiBasePostModel {
    private String type;
    private String body;
    private LiMessage parent;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LiMessage getParent() {
        return parent;
    }

    public void setParent(LiMessage parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
