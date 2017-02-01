/*
 * LiDefaultQueryHelper.java
 * Created on Jan 12, 2017
 *
 * Copyright 2017 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
 */

package lithium.community.android.sdk.queryutil;

import android.app.Activity;
import android.content.Context;
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

    private static LiDefaultQueryHelper _helperInstance;
    private JsonObject defaultSetting;

    /**
     * private constructor.
     * @param context {@link Context}
     */
    private LiDefaultQueryHelper(Context context){
        defaultSetting = getDefaultQueryJSON(context);
    }

    /**
     * Initializes helper class.
     * @param context {@link Context}
     * @return Singleton instance of this class.
     */
    public static synchronized LiDefaultQueryHelper initHelper(Context context){
        if(_helperInstance == null){
            _helperInstance = new LiDefaultQueryHelper(context);
        }
        return _helperInstance;
    }

    /**
     * @return Instance of this class.
     */
    public static LiDefaultQueryHelper getInstance() {
        if (_helperInstance == null) {
            throw new NoSuchPropertyException("Helper not intialized. Call init method first");
        }
        return _helperInstance;
    }

    /**
     * @return default settings
     */
    public JsonObject getDefaultSetting() {
        return defaultSetting;
    }

    /**
     * Fetches default settings from 'raw' package
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
            defaultQuerySettingsJson = new JsonParser()
                    .parse(defaultJsonString).getAsJsonObject();
        } catch (Exception e) {

        }
        return defaultQuerySettingsJson;
    }
}
