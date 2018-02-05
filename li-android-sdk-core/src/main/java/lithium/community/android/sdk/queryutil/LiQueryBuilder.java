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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lithium.community.android.sdk.manager.LiSDKManager;

import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_DEFAULT_SDK_SETTINGS;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_FOR_UI_SEARCH;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_MARK_AS_READ;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_SEARCH_QUERYSETTINGS_TYPE;

/**
 * Created by kunal.shrivastava on 10/19/16.
 * A utils to build Liql query from base settings
 */

public class LiQueryBuilder {

    private static final String WHERE = "WHERE";
    private static final String SPACE = " ";
    private static final String ORDER_BY = "ORDER BY";
    private static final String LIMIT = "LIMIT";

    private static volatile LiQuerySetting LI_QUERY_SETTING;

    private static JsonObject getDefault(String client) {
        JsonObject jsonObject = LiDefaultQueryHelper.getInstance().getDefaultSetting();
        return (jsonObject == null ? null : jsonObject.getAsJsonObject(client));
    }

    private static JsonObject getClientJsonSetting(String client, Context context) {

        Log.i("LiQueryBuilder", "Reading client settings from configurations");

        String settingJsonStr = LiSDKManager.getInstance().getFromSecuredPreferences(context, LI_DEFAULT_SDK_SETTINGS);
        JsonObject clientSettings = getDefault(client);
        JsonObject serverSettingJson = null;
        if (settingJsonStr != null && !settingJsonStr.isEmpty()) {
            JsonElement jsonElement = new JsonParser().parse(settingJsonStr);
            if (!jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                serverSettingJson = jsonElement.getAsJsonObject();
            }
        }

        overrideDefaultSettings(clientSettings, serverSettingJson);
        return clientSettings;
    }

    /**
     * Overrides default setting with the settings fetched fron the community.
     *
     * @param clientSettings    Local settings to be used.
     * @param serverSettingJson Settings received from community.
     */
    private static void overrideDefaultSettings(JsonObject clientSettings, JsonObject serverSettingJson) {
        if (serverSettingJson != null && !serverSettingJson.isJsonNull()) {
            if (serverSettingJson.has("response_limit")) {
                int limit = serverSettingJson.get("response_limit").getAsInt();
                clientSettings.addProperty("limit", limit);
            }

            if (serverSettingJson.has("discussion_style")) {
                JsonArray discussionStyleArr = serverSettingJson.get("discussion_style").getAsJsonArray();
                StringBuilder conversationStyleSB = new StringBuilder();
                boolean isFirst = true;
                conversationStyleSB.append("(");
                for (JsonElement styleElem : discussionStyleArr) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        conversationStyleSB.append(", ");
                    }
                    conversationStyleSB.append("'").append(styleElem.getAsString()).append("'");
                }
                conversationStyleSB.append(")");
                if (clientSettings.has("whereClauses")) {
                    JsonArray whereArr = clientSettings.get("whereClauses").getAsJsonArray();
                    for (JsonElement whereElem : whereArr) {
                        JsonObject whereObj = whereElem.getAsJsonObject();
                        if (whereObj.has("key")) {
                            String key = whereObj.get("key").getAsString();
                            if (key.equals("conversation.style")) {
                                whereObj.addProperty("value", conversationStyleSB.toString());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns query setting class.
     *
     * @param baseQuery It is the base query.
     * @param client    It is the client for which settings are to be fetched.
     * @return query setting {@link LiQuerySetting}
     */
    private static LiQuerySetting getQuerySetting(String baseQuery, String client, Context context) {
        Log.i("LiQueryBuilder", "calling getClientJsonSetting to get json setting for client: " + client);
        JsonObject clientSettings = getClientJsonSetting(client, context);
        Gson gson = new Gson();
        try {
            LI_QUERY_SETTING = gson.fromJson(clientSettings, LiQuerySetting.class);
        } catch (Exception exception) {
            Log.e("LiQueryBuilder", "Error parsing client setting json: " + clientSettings.toString());
        }
        return LI_QUERY_SETTING;
    }

    /**
     * Builds the query from base query and query setting.
     *
     * @param baseQuery      It is the base query.
     * @param liQuerySetting {@link LiQuerySetting}
     * @return Complete query.
     */
    private static String buildQuery(String baseQuery, LiQuerySetting liQuerySetting) {
        StringBuilder query = new StringBuilder();
        query.append(baseQuery);
        query.append(SPACE);
        int querySettingWhereClauseSize = liQuerySetting.getWhereClauses().size();
        if (querySettingWhereClauseSize > 0) {
            query.append(WHERE);
            int index = 0;
            for (LiQuerySetting.WhereClause whereClause : liQuerySetting.getWhereClauses()) {
                query.append(SPACE);
                query.append(whereClause.getKey()).append(SPACE)
                        .append(whereClause.getClause()).append(SPACE).append(whereClause.getValue());
                if (index < querySettingWhereClauseSize - 1) {
                    query.append(SPACE).append(whereClause.getOperator());
                }
                index++;
            }
        }
        if (liQuerySetting.getOrdering() != null) {
            query.append(SPACE).append(ORDER_BY).append(SPACE)
                    .append(liQuerySetting.getOrdering().getKey()).append(SPACE)
                    .append(liQuerySetting.getOrdering().getType());
        }

        if (liQuerySetting.getLimit() > 0) {
            query.append(SPACE).append(LIMIT).append(SPACE).append(liQuerySetting.getLimit());
        }

        return query.toString();
    }

    /**
     * Method to be called by activity client to create LIQL query, corresponding to that client. This method read
     * settings for
     * any activity clients, and then create {@link LiQuerySetting} object from settings.
     * Using LiQuerySetting object, this will create a LIQL query to be used by activity client directly
     *
     * @param baseQuery      It is the base LIQL.
     * @param liQuerySetting {@link LiQuerySetting}
     * @return LIQL query to be used by activity client directly.
     */
    public static String getQuery(String baseQuery, LiQuerySetting liQuerySetting) {
        return buildQuery(baseQuery, liQuerySetting);
    }

    /**
     * Method to be called by activity client to create LIQL query, corresponding to that client. This method read
     * settings for
     * any activity clients, and then create {@link LiQuerySetting} object from settings.
     * Using LiQuerySetting object, this will create a LIQL query to be used by activity client directly
     *
     * @param baseQuery It is the base LIQL.
     * @param client    It is the query settings type.
     * @return The complete query with appropriate values populated.
     */
    public static String getQuery(Context context, String baseQuery, String client) {
        Log.i("LiQueryBuilder", "calling getQuerySetting to get query setting of client:: " + client);
        LiQuerySetting liQuerySetting = getQuerySetting(baseQuery, client, context);
        if (liQuerySetting != null) {
            Log.i("LiQueryBuilder", "Fetched Query Setting, calling buildQuery to build query");
            if (client.equals(LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE)) {
                return buildQuery(baseQuery, liQuerySetting) + LI_MARK_AS_READ;
            } else if (client.equals(LI_SEARCH_QUERYSETTINGS_TYPE)) {
                return buildQuery(baseQuery, liQuerySetting) + LI_FOR_UI_SEARCH;
            }
            return buildQuery(baseQuery, liQuerySetting);
        }
        if (client.equals(LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE)) {
            //Temporary addition to figure out if the message has been read by the user
            return (baseQuery + LI_MARK_AS_READ);
        } else if (client.equals(LI_SEARCH_QUERYSETTINGS_TYPE)) {
            return baseQuery + LI_FOR_UI_SEARCH;
        }
        return baseQuery;
    }
}
