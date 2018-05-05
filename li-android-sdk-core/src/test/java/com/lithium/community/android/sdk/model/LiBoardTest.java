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

package com.lithium.community.android.sdk.model;

import org.junit.Test;

import com.lithium.community.android.sdk.model.LiBaseModelImpl;
import com.lithium.community.android.sdk.model.helpers.LiBoard;

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
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(ID);
        liBoard.setId(liString);
        assertEquals(ID, liBoard.getId());
    }

    @Test
    public void getInteractionStyleTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(INTERACTION_STYLE);
        liBoard.setInteractionStyle(liString);
        assertEquals(INTERACTION_STYLE, liBoard.getInteractionStyle());
    }

    @Test
    public void getTitleTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(TITLE);
        liBoard.setTitle(liString);
        assertEquals(TITLE, liBoard.getTitle());
    }

    @Test
    public void getShortTitleTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(SHORT_TITLE);
        liBoard.setShortTitle(liString);
        assertEquals(SHORT_TITLE, liBoard.getShortTitle());
    }

    @Test
    public void getDescriptionTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(DESCRIPTION);
        liBoard.setDescription(liString);
        assertEquals(DESCRIPTION, liBoard.getDescription());
    }
}
