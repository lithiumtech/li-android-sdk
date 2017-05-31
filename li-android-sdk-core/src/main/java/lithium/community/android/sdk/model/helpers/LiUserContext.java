/*
 * LiUserContext.java
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

package lithium.community.android.sdk.model.helpers;

import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.model.LiBaseModelImpl;

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
}
