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
import com.lithium.community.android.model.helpers.LiRanking;
import com.lithium.community.android.model.helpers.LiRankingDisplay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*
  Created by mahaveer.udabal on 10/18/16.
 */

public class LiRankingTest {

    private final String HREF = "Li_Href";
    private final String NAME = "Li_Rank";
    private final Long ID = 12345L;
    @Mock
    LiRankingDisplay lithiumRankingDisplay;
    LiRanking liRanking = new LiRanking();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getHrefTest() {
        liRanking.setHref(HREF);
        assertEquals(HREF, liRanking.getHref());
    }

    @Test
    public void getNameTest() {
        liRanking.setName(NAME);
        assertEquals(NAME, liRanking.getName());
    }

    @Test
    public void getIdTest() {
        liRanking.setId(ID);
        assertEquals(this.ID, liRanking.getId());
    }

    @Test
    public void getLithiumRankingDisplayTest() {
        liRanking.setLithiumRankingDisplay(lithiumRankingDisplay);
        assertNotEquals(null, liRanking.getLithiumRankingDisplay());
    }
}
