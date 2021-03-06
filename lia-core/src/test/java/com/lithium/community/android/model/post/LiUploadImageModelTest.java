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

package com.lithium.community.android.model.post;

import com.lithium.community.android.model.post.LiUploadImageModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiUploadImageModelTest {

    private final String type = "Image";
    private final String title = "getUploadImageClient";
    private final String field = "image.content";
    private final String visibility = "public";
    private final String description = "TestImage";

    private LiUploadImageModel liUploadImageModel = new LiUploadImageModel();

    @Test
    public void getParamsTest() {
        liUploadImageModel.setType(type);
        liUploadImageModel.setVisibility(visibility);
        liUploadImageModel.setField(field);
        liUploadImageModel.setVisibility(visibility);
        liUploadImageModel.setDescription(description);
        liUploadImageModel.setTitle(title);
        assertEquals(type, liUploadImageModel.getType());
        assertEquals(title, liUploadImageModel.getTitle());
        assertEquals(field, liUploadImageModel.getField());
        assertEquals(visibility, liUploadImageModel.getVisibility());
        assertEquals(description, liUploadImageModel.getDescription());
        assertEquals(
                "{\"nameValuePairs\":{\"request\":{\"data\":{\"type\":\"Image\",\"title\":\"getUploadImageClient\","
                        + "\"field\":\"image.content\",\"visibility\":\"public\",\"description\":\"TestImage\"}}}}",
                liUploadImageModel.toJsonString());

    }
}
