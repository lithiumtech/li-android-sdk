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

import org.json.JSONException;
import org.junit.Test;

import com.lithium.community.android.sdk.model.helpers.LiAvatar;

import static org.junit.Assert.assertEquals;

/**
 * Created by shoureya.kant on 12/5/16.
 */

public class LiAvatarTest {

    private static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";
    private static final String MESSAGE_IMAGE_URL = "MESSAGE_IMAGE_URL";
    private String profile = "profile";
    private String message = "This is Avatar";

    private LiAvatar liAvatar = new LiAvatar();

    @Test
    public void getParamsTest() throws JSONException {
        liAvatar.setProfile(profile);
        liAvatar.setMessage(message);
        assertEquals(profile, liAvatar.getProfile());
        assertEquals(message, liAvatar.getMessage());
        assertEquals("{\"MESSAGE_IMAGE_URL\":\"This is Avatar\",\"PROFILE_IMAGE_URL\":\"profile\"}",
                liAvatar.serialize().toString());
        LiAvatar liAvatar1 = new LiAvatar();
        liAvatar1 = LiAvatar.deserialize(liAvatar.serialize());
        assertEquals(profile, liAvatar1.getProfile());
        assertEquals(message, liAvatar1.getMessage());
    }
}
