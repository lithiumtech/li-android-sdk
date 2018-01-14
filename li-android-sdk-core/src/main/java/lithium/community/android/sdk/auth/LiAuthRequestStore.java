/*
 * LiAuthRequestStore.java
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

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static lithium.community.android.sdk.auth.LiAuthConstants.LOG_TAG;

class LiAuthRequestStore {
    private static LiAuthRequestStore sInstance;
    private Map<String, LiSSOAuthorizationRequest> mLiRequests = new HashMap<>();

    private LiAuthRequestStore() {
    }

    public static synchronized LiAuthRequestStore getInstance() {
        if (sInstance == null) {
            sInstance = new LiAuthRequestStore();
        }
        return sInstance;
    }

    /**
     * Adding request for a particular state
     *
     * @param request {@link LiSSOAuthorizationRequest}
     */
    public void addAuthRequest(LiSSOAuthorizationRequest request) {
        Log.v(LOG_TAG, String.format("Adding pending intent for state %s", request.getState()));
        mLiRequests.put(request.getState(), request);
    }

    /**
     * @param state This is key depicting state of mLiRequests.
     * @return Authorization request for the given state (SSO case).
     */
    public LiSSOAuthorizationRequest getLiOriginalRequest(String state) {
        Log.v(LOG_TAG, String.format("Retrieving original request for state %s", state));
        return mLiRequests.remove(state);
    }
}
