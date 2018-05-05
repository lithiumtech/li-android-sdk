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

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Its a Util class with operations related to Uri.
 */
public final class LiUriUtils {

    public static URL buildUrlFromString(String uri) throws MalformedURLException {
        return new URL(uri);
    }

    public static Uri parseUriIfAvailable(@Nullable String uri) {
        if (uri == null) {
            return null;
        }

        return Uri.parse(uri);
    }

    public static void appendQueryParameterIfNotNull(
            @NonNull Uri.Builder uriBuilder,
            @NonNull String paramName,
            @Nullable Object value) {
        if (value == null) {
            return;
        }

        String valueStr = value.toString();
        if (valueStr == null) {
            return;
        }

        uriBuilder.appendQueryParameter(paramName, value.toString());
    }

    public static Uri getUri(
            @NonNull JSONObject json,
            @NonNull String field)
            throws JSONException {
        LiCoreSDKUtils.checkNotNull(json, "json must not be null");
        LiCoreSDKUtils.checkNotNull(field, "field must not be null");

        String value = json.getString(field);
        if (value == null) {
            throw new JSONException("field \"" + field + "\" is mapped to a null value");
        }
        return Uri.parse(value);
    }

    public static String getDomainName(@NonNull Uri uri) {
        LiCoreSDKUtils.checkNotNull(uri);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static String reverseDomainName(Uri authorizeUri) {
        String domainName = getDomainName(authorizeUri);
        String[] strings = domainName.split("\\.");
        StringBuffer buffer = new StringBuffer(strings[strings.length - 1]);
        buffer.append(".");
        for (int i = strings.length - 2; i > 0; i--) {
            buffer.append(strings[i]).append(".");
        }
        buffer.append(strings[0]);
        return buffer.toString();
    }

    public static Long getLongQueryParameter(@NonNull Uri uri, @NonNull String param) {
        String valueStr = uri.getQueryParameter(param);
        if (valueStr != null) {
            return Long.parseLong(valueStr);
        }
        return null;
    }

}
