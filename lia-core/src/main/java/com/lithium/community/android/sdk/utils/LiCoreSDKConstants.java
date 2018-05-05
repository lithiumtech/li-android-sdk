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

/**
 * Created by sumit.pannalall on 1/16/17.
 */

public class LiCoreSDKConstants {
    public static final String LI_SHARED_PREFERENCES_NAME = "LiSharedStatePreference";
    public static final String LI_AUTH_STATE = "LI_AUTH_STATE";
    public static final String LI_DEFAULT_SDK_SETTINGS = "LiDefaultSDKSettings";
    public static final String LI_VISITOR_ID = "LI_VISITOR_ID";
    public static final String LI_VISIT_ORIGIN_TIME_KEY = "LI_VISIT_ORIGIN_TIME_KEY";
    public static final String LI_VISIT_LAST_ISSUE_TIME_KEY = "LI_VISIT_LAST_ISSUE_TIME_KEY";
    public static final String LI_DEVICE_ID = "Li_device_id";
    public static final String LI_RECEIVER_DEVICE_ID = "Li_receivedDeviceId";
    public static final String LI_SSO_TOKEN = "LI_SSO_TOKEN";
    public static final String LI_BEACON_TARGET_TYPE_BOARD = "board";
    public static final String LI_BEACON_TARGET_TYPE_USER = "user";
    public static final String LI_BEACON_TARGET_TYPE_CONVERSATION = "conversation";
    public static final String LI_BEACON_TARGET_TYPE_CATEGORY = "category";
    public static final String LI_BEACON_TARGET_TYPE_NODE = "node";

    public static final String LOGIN_RESULT = "LOGIN_RESULT";
    public static final String LOGIN_RESULT_CODE = "LOGIN_RESULT_CODE";
    public static final int HTTP_CODE_SUCCESSFUL = 200;
    public static final int HTTP_CODE_FORBIDDEN = 403;
    public static final int HTTP_CODE_UNAUTHORIZED = 401;
    public static final int HTTP_CODE_SERVER_ERROR = 500;
    public static final int HTTP_CODE_SERVICE_UNAVAILABLE = 503;
    public static final long LI_MIN_IMAGE_SIZE_TO_COMPRESS = 1024 * 1024;

    public static final String LI_LOG_TAG = "LiSdk";
    public static final String LI_DEBUG_LOG_TAG = "LiSdkDebug";
    public static final String LI_ERROR_LOG_TAG = "LiSdkError";

    /**
     * Default private constructor so that an instance of this class cannot be created.
     */
    private LiCoreSDKConstants() {
    }
}
