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
     *
     * @param client {@link Client}
     * @return Base query.
     */
    public static String getBaseQuery(Client client) {
        switch (client) {
            case LI_ARTICLES_BROWSE_CLIENT:
                return LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL;
            case LI_BROWSE_CLIENT:
                return LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL;
            case LI_ARTICLES_CLIENT:
                return LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL;
            case LI_CATEGORY_CLIENT:
                return LiQueryConstant.LI_CATEGORY_CLIENT_BASE_LIQL;
            case LI_FLOATED_MESSAGES_CLIENT:
                return LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_BASE_LIQL;
            case LI_MESSAGE_CHILDREN_CLIENT:
                return LiQueryConstant.LI_MESSAGE_CONVERSATION_BASE_LIQL;
            case LI_MESSAGE_CLIENT:
                return LiQueryConstant.LI_MESSAGE_CONVERSATION_BASE_LIQL;
            case LI_QUESTIONS_CLIENT:
                return LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL;
            case LI_SDK_SETTINGS_CLIENT:
                return LiQueryConstant.LI_SDK_SETTINGS_CLIENT_BASE_LIQL;
            case LI_SEARCH_CLIENT:
                return LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL;
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
     *
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
     *
     * @param client {@link Client}
     * @return data model
     */
    public static Class<? extends LiBaseModel> getResponseClass(Client client) {
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
