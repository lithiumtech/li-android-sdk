/*
 * LiPostQuestionModel.java
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

import lithium.community.android.sdk.model.helpers.LiBoard;

/**
 * Request body for POST call to post a question.
 * Created by shoureya.kant on 11/21/16.
 */

public class LiPostMessageModel extends LiBasePostModel {

    private String type;
    private String subject;
    private String body;
    private LiBoard board;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LiBoard getBoard() {
        return board;
    }

    public void setBoard(LiBoard board) {
        this.board = board;
    }
}
