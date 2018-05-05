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

package com.lithium.community.android.sdk.model.response;

import com.lithium.community.android.sdk.model.LiBaseModelImpl;

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
