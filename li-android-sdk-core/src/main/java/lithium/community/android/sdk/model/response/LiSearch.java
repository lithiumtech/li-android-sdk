/*
 * LiSearch.java
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
 * Data Model for GET call to search in app.
 * Created by kunal.shrivastava on 10/28/16.
 */

public class LiSearch extends LiBaseModelImpl implements LiBaseMessageModel {
    private LiMessage liMessage;

    public LiMessage getLiMessage() {
        return liMessage;
    }

    public void setLiMessage(LiMessage liMessage) {
        this.liMessage = liMessage;
    }
}
