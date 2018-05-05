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

package com.lithium.community.android.sdk.model.helpers;

import com.lithium.community.android.sdk.model.LiBaseModelImpl;

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
