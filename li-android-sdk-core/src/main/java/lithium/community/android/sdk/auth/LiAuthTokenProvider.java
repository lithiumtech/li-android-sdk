/*
 * LiAuthTokenProvider.java
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

import android.net.Uri;

/**
 * Auth token provider interface. Current provider is LiAuthManager.
 */

public interface LiAuthTokenProvider {

    /**
     * provides community Uri.
     * @return community uri
     */
    Uri getCommunityUrl();

    /**
     * provides Access Token.
     * @return access token
     */
    String getNewAuthToken();

    /**
     * provides Refresh Token
     * @return refresh token
     */
    String getRefreshToken();
}
