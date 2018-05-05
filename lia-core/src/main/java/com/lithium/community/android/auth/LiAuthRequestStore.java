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

package com.lithium.community.android.auth;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

class LiAuthRequestStore {

    private static LiAuthRequestStore instance;

    private Map<String, LiSSOAuthorizationRequest> mLiRequests = new HashMap<>();

    private LiAuthRequestStore() {
    }

    public static synchronized LiAuthRequestStore getInstance() {
        if (instance == null) {
            instance = new LiAuthRequestStore();
        }
        return instance;
    }

    /**
     * Adding request for a particular state
     *
     * @param request {@link LiSSOAuthorizationRequest}
     */
    public void addAuthRequest(LiSSOAuthorizationRequest request) {
        Log.v(LiAuthConstants.LOG_TAG, String.format("Adding pending intent for state %s", request.getState()));
        mLiRequests.put(request.getState(), request);
    }

    /**
     * @param state This is key depicting state of mLiRequests.
     * @return Authorization request for the given state (SSO case).
     */
    public LiSSOAuthorizationRequest getLiOriginalRequest(String state) {
        Log.v(LiAuthConstants.LOG_TAG, String.format("Retrieving original request for state %s", state));
        return mLiRequests.remove(state);
    }
}
