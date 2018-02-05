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

package lithium.community.android.sdk.model;

import org.junit.Test;

import lithium.community.android.sdk.model.helpers.LiImageMetaData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
   Created by mahaveer.udabal on 10/18/16.
 */

public class LiImageMetaDataTest {

    private final String FORMAT = "JPEG";
    private final Long SIZE = 10L;
    private LiImageMetaData liImageMetaData = new LiImageMetaData();

    @Test
    public void getFormatTest() {
        LiBaseModelImpl.LiString format = new LiBaseModelImpl.LiString();
        format.setValue(this.FORMAT);
        liImageMetaData.setFormat(format);
        assertEquals(FORMAT, liImageMetaData.getFormat());
        assertTrue(liImageMetaData.getFormatAsLithiumString() instanceof LiBaseModelImpl.LiString);
        assertEquals(FORMAT, liImageMetaData.getFormatAsLithiumString().getValue());
    }

    @Test
    public void getSizeTest() {
        LiBaseModelImpl.LiLong liInteger = new LiBaseModelImpl.LiLong();
        liInteger.setValue(this.SIZE);
        liImageMetaData.setSize(liInteger);
        assertEquals(this.SIZE, liImageMetaData.getSize());
        assertTrue(liImageMetaData.getSizeAsLithiumLong() instanceof LiBaseModelImpl.LiLong);
        assertEquals(this.SIZE, liImageMetaData.getSizeAsLithiumLong().getValue());
    }
}
