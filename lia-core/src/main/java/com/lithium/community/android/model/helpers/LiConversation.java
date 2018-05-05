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

package com.lithium.community.android.model.helpers;

import com.google.gson.annotations.SerializedName;
import com.lithium.community.android.model.LiBaseModelImpl;

/**
 * This data model represents conversation of user.
 * Created by sumit.pannalall on 11/15/16.
 */

public class LiConversation extends LiBaseModelImpl {
    private String type;
    private boolean solved;

    @SerializedName("last_post_time")
    private LiBaseModelImpl.LiDateInstant lastPostTime;

    public LiDateInstant getLastPostTime() {
        return lastPostTime;
    }

    public void setLastPostTime(LiDateInstant lastPostTime) {
        this.lastPostTime = lastPostTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
