/*
 * LiKudoMetrics.java
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

package lithium.community.android.sdk.model.helpers;

import lithium.community.android.sdk.model.LiBaseModelImpl;

/**
 * Kudo count metrics
 */

public class LiKudoMetrics extends LiBaseModelImpl {

    private LiSum sum;

    public LiSum getSum() {
        return sum;
    }

    public void setSum(LiSum sum) {
        this.sum = sum;
    }

}
