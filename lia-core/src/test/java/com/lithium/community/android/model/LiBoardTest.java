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

import org.junit.Test;

import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.helpers.LiBoard;

import static org.junit.Assert.assertEquals;

/*
   Created by mahaveer.udabal on 10/5/16.
 */

public class LiBoardTest {

    private final Boolean BLOG = true;
    private final String ID = "Li12345";
    private final String INTERACTION_STYLE = "Li_Style";
    private final String TITLE = "Li_Title";
    private final String SHORT_TITLE = "Li_Short_Title";
    private final String DESCRIPTION = "Li_Description";
    private LiBoard liBoard = new LiBoard();

    @Test
    public void getBlogTest() {
        LiBaseModelImpl.LiBoolean liBoolean = new LiBaseModelImpl.LiBoolean();
        liBoolean.setValue(BLOG);
        liBoard.setBlog(liBoolean);
        assertEquals(BLOG, liBoard.getBlog());
    }

    @Test
    public void getIdTest() {
        liBoard.setId(ID);
        assertEquals(ID, liBoard.getId());
    }

    @Test
    public void getInteractionStyleTest() {
        liBoard.setInteractionStyle(INTERACTION_STYLE);
        assertEquals(INTERACTION_STYLE, liBoard.getInteractionStyle());
    }

    @Test
    public void getTitleTest() {
        liBoard.setTitle(TITLE);
        assertEquals(TITLE, liBoard.getTitle());
    }

    @Test
    public void getShortTitleTest() {
        liBoard.setShortTitle(SHORT_TITLE);
        assertEquals(SHORT_TITLE, liBoard.getShortTitle());
    }

    @Test
    public void getDescriptionTest() {
        liBoard.setDescription(DESCRIPTION);
        assertEquals(DESCRIPTION, liBoard.getDescription());
    }
}
