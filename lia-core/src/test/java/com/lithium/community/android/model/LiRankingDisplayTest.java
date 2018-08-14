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

package com.lithium.community.android.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.helpers.LiImage;
import com.lithium.community.android.model.helpers.LiRankingDisplay;

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
        liRankingDisplay.setBold(BOLD);
        assertEquals(true, liRankingDisplay.getBold());
    }

    @Test
    public void getColorTest() {
        liRankingDisplay.setColor(COLOR);
        assertEquals(COLOR, liRankingDisplay.getColor());
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
