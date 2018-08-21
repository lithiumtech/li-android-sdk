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
import com.lithium.community.android.model.helpers.LiImageMetaData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
   Created by mahaveer.udabal on 10/07/16.
 */

public class LiImageTest {

    private final Long HEIGHT = 12345L;
    private final String URL = "https://community.com";
    private final String VIEW_HREF = "http://community.lithium.com/t5/user/viewprofilepage/user-id/52295";
    private final Long WIDTH = 234L;
    private final String TITLE = "How to Post Messages";
    private final String DESCRIPTION = "Posting messages";
    @Mock
    private LiImageMetaData metaData;
    private LiImage liImage = new LiImage();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getHeightTest() {
        liImage.setHeight(HEIGHT);
        assertEquals(HEIGHT, liImage.getHeight());
    }

    @Test
    public void getUrlTest() {
        liImage.setUrl(URL);
        assertEquals(URL, liImage.getUrl());
    }

    @Test
    public void getViewHrefTest() {
        liImage.setViewHref(VIEW_HREF);
        assertEquals(VIEW_HREF, liImage.getViewHref());
    }

    @Test
    public void getTitleTest() {
        liImage.setTitle(TITLE);
        assertEquals(TITLE, liImage.getTitle());
    }

    @Test
    public void getDescriptionTest() {
        liImage.setDescription(DESCRIPTION);
        assertEquals(DESCRIPTION, liImage.getDescription());
    }

    @Test
    public void getWidthTest() {
        liImage.setWidth(WIDTH);
        assertEquals(WIDTH, liImage.getWidth());
    }

    @Test
    public void getMetaDataTest() {
        liImage.setMetaData(metaData);
        assertEquals(metaData, liImage.getMetaData());
    }
}
