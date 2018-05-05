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

package com.lithium.community.android.model.response;

import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.helpers.LiBoard;

/**
 * Data Model for GET call to search in app.
 * Created by kunal.shrivastava on 10/28/16.
 */

public class LiSearch extends LiBaseModelImpl implements LiTargetModel {
    private LiMessage liMessage;

    public LiMessage getLiMessage() {
        return liMessage;
    }

    public void setLiMessage(LiMessage liMessage) {
        this.liMessage = liMessage;
    }

    @Override
    public LiBoard getLiBoard() {
        return null;
    }
}
