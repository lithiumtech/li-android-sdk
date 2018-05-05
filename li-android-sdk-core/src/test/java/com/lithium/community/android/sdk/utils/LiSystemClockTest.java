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

package com.lithium.community.android.sdk.utils;

import com.lithium.community.android.sdk.utils.LiSystemClock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by kunal.shrivastava on 12/1/16.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({LiSystemClock.class, System.class})
public class LiSystemClockTest {
    private long currentTime;

    @Before
    public void setUp() {
        currentTime = 1L;
    }

    @Test
    public void testGetCurrentTimeMillis() {
        PowerMockito.mockStatic(System.class);
        BDDMockito.given(System.currentTimeMillis()).willReturn(currentTime);
        long output = LiSystemClock.INSTANCE.getCurrentTimeMillis();
        PowerMockito.verifyStatic();
        Assert.assertEquals(1L, output);
    }


}
