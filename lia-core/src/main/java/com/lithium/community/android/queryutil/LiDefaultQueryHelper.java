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

package com.lithium.community.android.queryutil;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lithium.community.android.R;
import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.MessageConstants;

/**
 * This class is use to fetch default LIQL query settings for LIQL calls.
 *
 * @author shoureya.kant, adityasharat
 */
public class LiDefaultQueryHelper {

    private static LiDefaultQueryHelper instance;

    @Nullable
    private JsonObject defaultSetting;

    /**
     * Default private constructor.
     *
     * @param context {@link Context}
     */
    private LiDefaultQueryHelper(@NonNull Context context) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        defaultSetting = getDefaultQueryJSON(context);
    }

    /**
     * Initializes the singleton instance of the Lithium Query Helper
     *
     * @param context {@link Context}
     */
    public static synchronized void initialize(@NonNull Context context) {
        LiCoreSDKUtils.checkNotNull(context, MessageConstants.wasNull("context"));
        if (instance == null) {
            instance = new LiDefaultQueryHelper(context);
        }
    }

    /**
     * @return Instance of this class.
     */
    @Nullable
    public static LiDefaultQueryHelper getInstance() {
        return instance;
    }

    /**
     * Initializes helper class.
     *
     * @param context {@link Context}
     * @return instance of Lithium Query Helper.
     * @deprecated Use {@link #initialize(Context)}
     */
    @Deprecated
    public static synchronized LiDefaultQueryHelper initHelper(Context context) {
        initialize(context);
        return getInstance();
    }

    /**
     * Fetches default settings packaged with the SDK.
     *
     * @param context An Android context.
     * @return default SDK settings.
     */
    @Nullable
    private static JsonObject getDefaultQueryJSON(@NonNull Context context) {
        int rawResId = R.raw.li_default_query_settings;
        String string = LiCoreSDKUtils.getRawString(context, rawResId);
        JsonObject settings = null;

        try {
            settings = new JsonParser().parse(string).getAsJsonObject();
        } catch (Exception e) {
            Log.e("LiDefaultQueryHelper", "Could not parse the default SDK settings");
            e.printStackTrace();
        }

        return settings;
    }

    /**
     * @return default settings
     */
    @Nullable
    public JsonObject getDefaultSetting() {
        return defaultSetting;
    }
}
