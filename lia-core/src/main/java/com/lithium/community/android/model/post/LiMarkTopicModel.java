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

package com.lithium.community.android.model.post;

import com.google.gson.annotations.SerializedName;

/**
 * Created by saiteja.tokala on 4/03/17.
 */

public class LiMarkTopicModel extends LiBasePostModel {

    private String type;
    private String user;
    @SerializedName("topic_id")
    private String topicId;
    @SerializedName("mark_unread")
    private boolean markUnread;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public boolean isMarkUnread() {
        return markUnread;
    }

    public void setMarkUnread(boolean markUnread) {
        this.markUnread = markUnread;
    }
}
