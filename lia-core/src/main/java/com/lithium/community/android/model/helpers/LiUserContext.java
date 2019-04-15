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
 * This data model provides permissions a user has.
 * Created by shoureya.kant on 11/11/16.
 */

public class LiUserContext extends LiBaseModelImpl {

    private Boolean kudo;

    private Boolean read;

    @SerializedName("can_kudo")
    private boolean canKudo;

    @SerializedName("can_reply")
    private boolean canReply;

    @SerializedName("can_delete")
    private boolean canDelete;

    @SerializedName("is_subscribed")
    private boolean isSubscribed;

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanReply() {
        return canReply;
    }

    public void setCanReply(boolean canReply) {
        this.canReply = canReply;
    }

    public boolean isCanKudo() {
        return canKudo;
    }

    public void setCanKudo(boolean canKudo) {
        this.canKudo = canKudo;
    }

    public Boolean getKudo() {
        return kudo;
    }

    public void setKudo(Boolean kudo) {
        this.kudo = kudo;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }
}
