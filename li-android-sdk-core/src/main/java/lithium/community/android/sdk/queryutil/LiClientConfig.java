/*
 * ClientConfig.java
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

package lithium.community.android.sdk.queryutil;

import lithium.community.android.sdk.manager.LiClientManager.Client;
import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.response.LiAppSdkSettings;
import lithium.community.android.sdk.model.response.LiBrowse;
import lithium.community.android.sdk.model.response.LiFloatedMessageModel;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.model.response.LiSubscriptions;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.utils.LiQueryConstant;

/**
 * This is config class for all the api providers(client).
 * It provides base LIQL query, client type and the data model for a given client.
 * Created by kunal.shrivastava on 12/22/16.
 */

public class LiClientConfig {

    /**
     * Returns base LIQL for a client.
     * @param client {@link Client}
     * @return Base query.
     */
    public static String getBaseQuery(Client client) {
        switch (client) {
            case LI_ARTICLES_BROWSE_CLIENT:
                return LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_BASE_LIQL;
            case LI_BROWSE_CLIENT:
                return LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL;
            case LI_ARTICLES_CLIENT:
                return LiQueryConstant.LI_ARTICLES_CLIENT_BASE_LIQL;
            case LI_CATEGORY_CLIENT:
                return LiQueryConstant.LI_CATEGORY_CLIENT_BASE_LIQL;
            case LI_FLOATED_MESSAGES_CLIENT:
                return LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_BASE_LIQL;
            case LI_MESSAGE_CHILDREN_CLIENT:
                return LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_BASE_LIQL;
            case LI_MESSAGE_CLIENT:
                return LiQueryConstant.LI_MESSAGE_CLIENT_BASE_LIQL;
            case LI_QUESTIONS_CLIENT:
                return LiQueryConstant.LI_QUESTIONS_CLIENT_BASE_LIQL;
            case LI_SDK_SETTINGS_CLIENT:
                return LiQueryConstant.LI_SDK_SETTINGS_CLIENT_BASE_LIQL;
            case LI_SEARCH_CLIENT:
                return LiQueryConstant.LI_SEARCH_CLIENT_BASE_LIQL;
            case LI_SUBSCRIPTION_CLIENT:
                return LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_BASE_LIQL;
            case LI_USER_DETAILS_CLIENT:
                return LiQueryConstant.LI_USER_DETAILS_CLIENT_BASE_LIQL;
            default:
                throw new RuntimeException("Invalid client type");
        }
    }

    /**
     * Returns client type.
     * @param client {@link Client}
     * @return Query type.
     */
    public static String getType(Client client) {
        switch (client) {
            case LI_ARTICLES_BROWSE_CLIENT:
                return LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_TYPE;
            case LI_BROWSE_CLIENT:
                return LiQueryConstant.LI_BROWSE_CLIENT_TYPE;
            case LI_ARTICLES_CLIENT:
                return LiQueryConstant.LI_ARTICLES_CLIENT_TYPE;
            case LI_CATEGORY_CLIENT:
                return LiQueryConstant.LI_CATEGORY_CLIENT_TYPE;
            case LI_FLOATED_MESSAGES_CLIENT:
                return LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_TYPE;
            case LI_MESSAGE_CHILDREN_CLIENT:
                return LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_TYPE;
            case LI_MESSAGE_CLIENT:
                return LiQueryConstant.LI_MESSAGE_CLIENT_TYPE;
            case LI_QUESTIONS_CLIENT:
                return LiQueryConstant.LI_QUESTIONS_CLIENT_TYPE;
            case LI_SDK_SETTINGS_CLIENT:
                return LiQueryConstant.LI_SDK_SETTINGS_CLIENT_TYPE;
            case LI_SEARCH_CLIENT:
                return LiQueryConstant.LI_SEARCH_CLIENT_TYPE;
            case LI_SUBSCRIPTION_CLIENT:
                return LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_TYPE;
            case LI_USER_DETAILS_CLIENT:
                return LiQueryConstant.LI_USER_DETAILS_CLIENT_TYPE;
            default:
                throw new RuntimeException("Invalid client type");
        }
    }

    /**
     * Returns data model for a client.
     * @param client {@link Client}
     * @return data model
     */
    public static Class<? extends LiBaseModel>  getResponseClass(Client client) {
        switch (client) {
            case LI_ARTICLES_BROWSE_CLIENT:
                return LiMessage.class;
            case LI_BROWSE_CLIENT:
                return LiBrowse.class;
            case LI_ARTICLES_CLIENT:
                return LiMessage.class;
            case LI_CATEGORY_CLIENT:
                return LiBrowse.class;
            case LI_FLOATED_MESSAGES_CLIENT:
                return LiFloatedMessageModel.class;
            case LI_MESSAGE_CHILDREN_CLIENT:
                return LiMessage.class;
            case LI_MESSAGE_CLIENT:
                return LiMessage.class;
            case LI_QUESTIONS_CLIENT:
                return LiMessage.class;
            case LI_SDK_SETTINGS_CLIENT:
                return LiAppSdkSettings.class;
            case LI_SEARCH_CLIENT:
                return LiMessage.class;
            case LI_SUBSCRIPTION_CLIENT:
                return LiSubscriptions.class;
            case LI_USER_DETAILS_CLIENT:
                return LiUser.class;
            default:
                throw new RuntimeException("Invalid client type");
        }
    }
}
