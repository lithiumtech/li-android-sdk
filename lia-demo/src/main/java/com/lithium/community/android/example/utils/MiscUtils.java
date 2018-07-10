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

package com.lithium.community.android.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lithium.community.android.example.R;

import java.util.UUID;

/**
 * @author adityasharat
 */
public class MiscUtils {

    private static final String DEFAULT_SHARED_PREFERENCES = "default";
    private static final String INSTANCE_ID = "LI_APP_INSTANCE_ID";

    public static boolean areCredentialsProvided(@NonNull Context context) {
        boolean result = true;
        String undefined = context.getString(R.string.undefined);
        String[] properties = new String[]{
                context.getString(R.string.li_client_name),
                context.getString(R.string.li_client_id),
                context.getString(R.string.li_client_secret),
                context.getString(R.string.li_tenant_id),
                context.getString(R.string.li_community_url)
        };

        for (String property : properties) {
            if (TextUtils.isEmpty(property) || property.equals(undefined)) {
                result = false;
                break;
            }
        }

        return result;
    }

    public static String getInstanceId(Context context) {
        String instanceId;

        // get it from the shared preference
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        instanceId = preferences.getString(INSTANCE_ID, null);

        if (TextUtils.isEmpty(instanceId)) { // if it is null
            instanceId = UUID.randomUUID().toString();  // create a new instance id
            preferences.edit().putString(INSTANCE_ID, instanceId).apply(); // save it in shared preferences
        }

        return instanceId;
    }

    @Nullable
    public static String sanitize(@Nullable String string) {
        if (string == null || TextUtils.isEmpty(string.trim()) || string.trim().equals("undefined") || string.trim().equals("null")) {
            return null;
        } else {
            return string.trim();
        }
    }

}
