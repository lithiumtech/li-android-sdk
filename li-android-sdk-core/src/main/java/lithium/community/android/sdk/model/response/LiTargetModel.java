/*
 * LiBaseMessageModel.java
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

import lithium.community.android.sdk.model.helpers.LiBoard;

/**
 * Created by kunal.shrivastava on 11/4/16.
 * Interface to be implemented by all the models which is having LiMessage variable
 */

public interface LiTargetModel {
    public LiMessage getLiMessage();

    public LiBoard getLiBoard();
}
