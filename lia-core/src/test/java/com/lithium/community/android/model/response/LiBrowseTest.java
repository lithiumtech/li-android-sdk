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

package com.lithium.community.android.model.response;

import com.lithium.community.android.model.response.LiBrowse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/4/16.
 */

public class LiBrowseTest {

    private final String id = "100";
    private final String title = "browse test";
    private LiBrowse liParent = new LiBrowse();
    private LiBrowse liBrowse = new LiBrowse();

    @Test
    public void getId() {
        liBrowse.setId(id);
        assertEquals(id, liBrowse.getId());
    }

    @Test
    public void getTitle() {
        liBrowse.setTitle(title);
        assertEquals(title, liBrowse.getTitle());
    }

    @Test
    public void getParent() {
        liParent.setId("99");
        liBrowse.setParent(liParent);
        assertEquals("99", liBrowse.getParent().getId());

    }
}
