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

package com.lithium.community.android.sdk.model.post;

import com.lithium.community.android.sdk.model.helpers.LiBoard;

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