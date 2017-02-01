/*
 * LiPostKudoModel.java
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
 * Created by shoureya.kant on 10/26/16.
 * <p>
 * Model for kudo post request
 */

public class LiPostKudoModel extends LiBasePostModel {
    private String type;
    private LiMessage message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LiMessage getMessage() {
        return message;
    }

    public void setMessage(LiMessage message) {
        this.message = message;
    }
}
