/*
 * LiImageMetaData.java
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

import lithium.community.android.sdk.model.LiBaseModelImpl;

/**
 * Media image meta data model.
 */
public class LiImageMetaData extends LiBaseModelImpl {

    private String format;
    private Long size;

    public String getFormat() {
        return format;
    }

    public void setFormat(LiBaseModelImpl.LiString format) {
        this.format = format.getValue();
    }

    public LiBaseModelImpl.LiString getFormatAsLithiumString() {
        final LiBaseModelImpl.LiString ret = new LiBaseModelImpl.LiString();
        ret.setValue(getFormat());
        return ret;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(LiBaseModelImpl.LiLong size) {
        this.size = size.getValue();
    }

    public LiBaseModelImpl.LiLong getSizeAsLithiumLong() {
        final LiBaseModelImpl.LiLong ret = new LiBaseModelImpl.LiLong();
        ret.setValue(getSize());
        return ret;
    }
}
