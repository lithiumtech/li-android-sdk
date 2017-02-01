/*
 * SystemClock.java
 * Created on Dec 27, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.utils;

/**
 * Default implementation of clock which wraps {@code System.currentTimeMillis}.
 */
public class LiSystemClock implements LiClock {
    public static final LiSystemClock INSTANCE = new LiSystemClock();

    private LiSystemClock() {
    }

    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
