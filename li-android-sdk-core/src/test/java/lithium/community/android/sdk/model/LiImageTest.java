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
import lithium.community.android.sdk.model.helpers.LiImageMetaData;

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
        LiBaseModelImpl.LiInteger liInteger = new LiBaseModelImpl.LiInteger();
        liInteger.setValue(HEIGHT);
        liImage.setHeight(liInteger);
        assertEquals(HEIGHT, liImage.getHeight());
        assertTrue(liImage.getHeightAsLiInteger() instanceof LiBaseModelImpl.LiInteger);
        assertEquals(HEIGHT, liImage.getHeightAsLiInteger().getValue());
    }

    @Test
    public void getUrlTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(URL);
        liImage.setUrl(liString);
        assertEquals(URL, liImage.getUrl());
        assertTrue(liImage.getUrlAsLiString() instanceof LiBaseModelImpl.LiString);
        assertEquals(URL, liImage.getUrlAsLiString().getValue());
    }

    @Test
    public void getViewHrefTest() {
        liImage.setViewHref(VIEW_HREF);
        assertEquals(VIEW_HREF, liImage.getViewHref());
    }

    @Test
    public void getTitleTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(TITLE);
        liImage.setTitle(liString);
        assertEquals(TITLE, liImage.getTitle());
        assertTrue(liImage.getTitleAsLiString() instanceof LiBaseModelImpl.LiString);
        assertEquals(TITLE, liImage.getTitleAsLiString().getValue());
    }

    @Test
    public void getDescriptionTest() {
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(DESCRIPTION);
        liImage.setDescription(liString);
        assertEquals(DESCRIPTION, liImage.getDescription());
        assertTrue(liImage.getDescriptionAsLiString() instanceof LiBaseModelImpl.LiString);
        assertEquals(DESCRIPTION, liImage.getDescriptionAsLiString().getValue());
    }

    @Test
    public void getWidthTest() {
        LiBaseModelImpl.LiInteger liInteger = new LiBaseModelImpl.LiInteger();
        liInteger.setValue(WIDTH);
        liImage.setWidth(liInteger);
        assertEquals(WIDTH, liImage.getWidth());
        assertTrue(liImage.getWidthAsLiInteger() instanceof LiBaseModelImpl.LiInteger);
        assertEquals(WIDTH, liImage.getWidthAsLiInteger().getValue());
    }

    @Test
    public void getMetaDataTest() {
        liImage.setMetaData(metaData);
        assertEquals(metaData, liImage.getMetaData());
    }
}
