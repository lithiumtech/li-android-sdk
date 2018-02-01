/*
 * LiAuthManager.java
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

package lithium.community.android.sdk.auth;

/**
 * Created by Lithium Technologies Inc on 7/10/17.
 * This is the interface that the app developer should implement for getting the device ID via either Firebase or GCM
 * for e.g. via Firebase it is like this: <code>FirebaseInstanceId.getInstance().getToken()</code>
 */
public interface LiDeviceTokenProvider {
    String getDeviceId();
}
