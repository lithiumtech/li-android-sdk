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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lithium.community.android.sdk.model.helpers.LiImage;
import lithium.community.android.sdk.model.helpers.LiRankingDisplay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*
  Created by mahaveer.udabal on 10/18/16.
 */

public class LiRankingDisplayTest {

    private final Boolean BOLD = true;
    private final String COLOR = "RED";
    @Mock
    private LiImage leftImage, rightImage, threadImage;
    private LiRankingDisplay liRankingDisplay = new LiRankingDisplay();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getBoldTest() {
        LiBaseModelImpl.LiBoolean bold = new LiBaseModelImpl.LiBoolean();
        bold.setValue(this.BOLD);
        liRankingDisplay.setBold(bold);
        assertEquals(true, liRankingDisplay.getBold());
        assertTrue(liRankingDisplay.getBoldAsLithiumBoolean() instanceof LiBaseModelImpl.LiBoolean);
        assertEquals(BOLD, liRankingDisplay.getBoldAsLithiumBoolean().getValue());
    }

    @Test
    public void getColorTest() {
        LiBaseModelImpl.LiString color = new LiBaseModelImpl.LiString();
        color.setValue(this.COLOR);
        liRankingDisplay.setColor(color);
        assertEquals(COLOR, liRankingDisplay.getColor());
        assertTrue(liRankingDisplay.getColorAsLithiumString() instanceof LiBaseModelImpl.LiString);
        assertEquals(COLOR, liRankingDisplay.getColorAsLithiumString().getValue());
    }

    @Test
    public void getLeftImageTest() {
        liRankingDisplay.setLeftImage(leftImage);
        LiImage liImage = liRankingDisplay.getLeftImage();
        assertNotEquals(null, liImage);
    }

    @Test
    public void getRightImageTest() {
        liRankingDisplay.setRightImage(rightImage);
        LiImage liImage = liRankingDisplay.getRightImage();
        assertNotEquals(null, liImage);
    }

    @Test
    public void getThreadImageTest() {
        liRankingDisplay.setThreadImage(threadImage);
        LiImage liImage = liRankingDisplay.getThreadImage();
        assertNotEquals(null, liImage);
    }
}
