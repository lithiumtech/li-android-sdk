/*
 * UriUtils.java
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

package lithium.community.android.sdk.utils;

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
