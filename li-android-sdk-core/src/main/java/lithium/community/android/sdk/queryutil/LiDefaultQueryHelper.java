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

package lithium.community.android.sdk.queryutil;

import android.content.Context;
import android.util.Log;
import android.util.NoSuchPropertyException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lithium.community.android.sdk.R;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;

/**
 * This class is use to fetch default query settings for LIQL calls from the 'raw' package of the core SDK.
 * Created by shoureya.kant on 1/12/17.
 */

public class LiDefaultQueryHelper {

    private static LiDefaultQueryHelper instance;
    private JsonObject defaultSetting;

    /**
     * private constructor.
     *
     * @param context {@link Context}
     */
    private LiDefaultQueryHelper(Context context) {
        defaultSetting = getDefaultQueryJSON(context);
    }

    /**
     * Initializes helper class.
     *
     * @param context {@link Context}
     * @return Singleton instance of this class.
     */
    public static synchronized LiDefaultQueryHelper initHelper(Context context) {
        if (instance == null) {
            instance = new LiDefaultQueryHelper(context);
        }
        return instance;
    }

    /**
     * @return Instance of this class.
     */
    public static LiDefaultQueryHelper getInstance() {
        if (instance == null) {
            throw new NoSuchPropertyException("Helper not intialized. Call init method first");
        }
        return instance;
    }

    /**
     * Fetches default settings from 'raw' package
     *
     * @param context {@link Context}
     * @return default settings in JsonObject form.
     */
    private static JsonObject getDefaultQueryJSON(Context context) {
        int rawResId = R.raw.li_default_query_settings;
        JsonObject defaultQuerySettingsJson = null;
        String defaultJsonString = null;

        defaultJsonString = LiCoreSDKUtils.getRawString(context, rawResId);

        try {
            // Parse the data into jsonobject to get original data in form of json.
            defaultQuerySettingsJson = new JsonParser().parse(defaultJsonString).getAsJsonObject();
        } catch (Exception e) {
            Log.e("LiDefaultQueryHelper", "Could not parse default query settings");
        }
        return defaultQuerySettingsJson;
    }

    /**
     * @return default settings
     */
    public JsonObject getDefaultSetting() {
        return defaultSetting;
    }
}
