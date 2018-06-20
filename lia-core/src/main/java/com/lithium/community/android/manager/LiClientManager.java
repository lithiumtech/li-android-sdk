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

package com.lithium.community.android.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lithium.community.android.api.LiBaseDeleteClient;
import com.lithium.community.android.api.LiBaseGetClient;
import com.lithium.community.android.api.LiBasePostClient;
import com.lithium.community.android.api.LiBasePutClient;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.model.helpers.LiAvatar;
import com.lithium.community.android.model.helpers.LiBoard;
import com.lithium.community.android.model.post.LiAcceptSolutionModel;
import com.lithium.community.android.model.post.LiCreateUpdateUserModel;
import com.lithium.community.android.model.post.LiGenericPostModel;
import com.lithium.community.android.model.post.LiGenericPutModel;
import com.lithium.community.android.model.post.LiMarkAbuseModel;
import com.lithium.community.android.model.post.LiMarkMessageModel;
import com.lithium.community.android.model.post.LiMarkMessagesModel;
import com.lithium.community.android.model.post.LiMarkTopicModel;
import com.lithium.community.android.model.post.LiPostKudoModel;
import com.lithium.community.android.model.post.LiPostLogoutModel;
import com.lithium.community.android.model.post.LiPostMessageModel;
import com.lithium.community.android.model.post.LiReplyMessageModel;
import com.lithium.community.android.model.post.LiSubscriptionPostModel;
import com.lithium.community.android.model.post.LiUploadImageModel;
import com.lithium.community.android.model.post.LiUserDeviceDataModel;
import com.lithium.community.android.model.post.LiUserDeviceIdUpdateModel;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiAppSdkSettings;
import com.lithium.community.android.model.response.LiBrowse;
import com.lithium.community.android.model.response.LiFloatedMessageModel;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.model.response.LiSubscriptions;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.queryutil.LiClientConfig;
import com.lithium.community.android.queryutil.LiQueryRequestParams;
import com.lithium.community.android.queryutil.LiQueryValueReplacer;
import com.lithium.community.android.rest.LiRequestHeaderConstants;
import com.lithium.community.android.rest.LiRestv2Client;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiQueryConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * This class acts as a manager to hold and refresh the user's authentication state. It includes all of the API
 * providers
 * to interact with the community.
 */
public class LiClientManager {

    private static final String API_PATH_PREFIX = "%s/api/2.0/%s";

    public static LiRestv2Client getRestClient() {
        return LiRestv2Client.getInstance();
    }

    /**
     * Fetches a list of all the articles for the user in context ordered by post time or kudos count. Create
     * parameters with {@link LiClientRequestParams.LiMessagesClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiMessagesClientRequestParams} the Android context (required)
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MESSAGES_CLIENT);
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", "0");

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL,
                LiQueryConstant.LI_ARTICLES_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches all articles on the specified board, authored by the user in context. Create parameters with
     * {@link LiClientRequestParams.LiMessagesByBoardIdClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiMessagesByBoardIdClientRequestParams}
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesByBoardIdClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MESSAGES_BY_BOARD_ID_CLIENT);
        String boardId = ((LiClientRequestParams.LiMessagesByBoardIdClientRequestParams) params).getBoardId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", boardId).replaceAll("&&", "0");

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL,
                LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_BROWSE_QUERYSETTINGS_TYPE, LiMessage.class)
                .setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches all the SDK settings from the Community app. Create parameters with {@link LiClientRequestParams.LiSdkSettingsClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiSdkSettingsClientRequestParams}
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getSdkSettingsClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_SDK_SETTINGS_CLIENT);
        String clientId = ((LiClientRequestParams.LiSdkSettingsClientRequestParams) params).getClientId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", clientId);

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_SDK_SETTINGS_CLIENT_BASE_LIQL,
                LiQueryConstant.LI_SDK_SETTINGS_CLIENT_TYPE, LiQueryConstant.LI_SDK_SETTINGS_QUERYSETTINGS_TYPE, LiAppSdkSettings.class)
                .setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches a list of all Community subscriptions for the user in context. Create parameters with
     * {@link LiClientRequestParams.LiUserSubscriptionsClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiUserSubscriptionsClientRequestParams} (required)
     * @return LiSubscription {@link LiSubscriptions}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUserSubscriptionsClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_USER_SUBSCRIPTIONS_CLIENT);

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_BASE_LIQL, LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_TYPE,
                LiQueryConstant.LI_SUBSCRIPTTION_QUERYSETTINGS_TYPE, LiSubscriptions.class);
    }

    /**
     * Fetches a list of boards for a given category, along with board and category details. Create parameters with
     * {@link LiClientRequestParams.LiCategoryBoardsClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiCategoryBoardsClientRequestParams}
     * @return LiBrowse {@link LiBrowse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getCategoryBoardsClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_CATEGORY_BOARDS_CLIENT);
        String categoryId = ((LiClientRequestParams.LiCategoryBoardsClientRequestParams) params).getCategoryId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", categoryId);

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_BROWSE_CLIENT_TYPE,
                LiQueryConstant.LI_BROWSE_QUERYSETTINGS_TYPE, LiBrowse.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches boards at a specific depth in the Community Structure hierarchy. 0 is the highest level in the
     * structure. Create parameters with {@link LiClientRequestParams.LiBoardsByDepthClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiBoardsByDepthClientRequestParams}.
     * @return LiBrowse {@link LiBrowse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getBoardsByDepthClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_BOARDS_BY_DEPTH_CLIENT);
        int depth = ((LiClientRequestParams.LiBoardsByDepthClientRequestParams) params).getDepth();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(depth));

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_BROWSE_CLIENT_TYPE,
                LiQueryConstant.LI_BROWSE_BY_DEPTH_QUERYSETTINGS_TYPE, LiBrowse.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details of child messages of the specified message. Child messages include replies comments, as well
     * as nested replies or comments. Create parameters with {@link LiClientRequestParams.LiRepliesClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiRepliesClientRequestParams}.
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @NonNull
    public static LiClient getRepliesClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_REPLIES_CLIENT);
        Long parentId = ((LiClientRequestParams.LiRepliesClientRequestParams) params).getParentId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(parentId));

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_MESSAGE_CONVERSATION_BASE_LIQL,
                LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE, LiMessage.class)
                .setReplacer(liQueryValueReplacer);
    }

    /**
     * Performs a keyword search in the community. Create parameters with {@link LiClientRequestParams.LiSearchClientRequestParams}.
     *
     * @param params the text string to search, passed as 'query'. The 'query' parameter is compared
     *               with the body and subject of the message. (required)
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getSearchClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_SEARCH_CLIENT);
        String query = ((LiClientRequestParams.LiSearchClientRequestParams) params).getQuery();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", query);

        return new LiBaseGetClient(params.getContext(), String.format(LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, query),
                LiQueryConstant.LI_SEARCH_CLIENT_TYPE, LiQueryConstant.LI_SEARCH_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches messages posted by a specified author up to a specified depth in the Community Structure hierarchy.
     * Crate parameters with {@link LiClientRequestParams.LiUserMessagesClientRequestParams}.
     *
     * @param params the depth of the message, where the a topic message = 0, the first reply = 1, and so on (required).
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUserMessagesClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_USER_MESSAGES_CLIENT);
        Long authorId = ((LiClientRequestParams.LiUserMessagesClientRequestParams) params).getAuthorId();
        String depth = ((LiClientRequestParams.LiUserMessagesClientRequestParams) params).getDepth();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(authorId)).replaceAll("&&", depth);

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, LiQueryConstant.LI_QUESTIONS_CLIENT_TYPE,
                LiQueryConstant.LI_QUESTIONS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches category details available to the user in context. This method respects the Hide from Lists and Menus
     * setting in Community Admin. Create parameters with {@link LiClientRequestParams.LiCategoryClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiCategoryClientRequestParams}.
     * @return LiBrowse {@link LiBrowse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getCategoryClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_CATEGORY_CLIENT);
        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_CATEGORY_CLIENT_BASE_LIQL, LiQueryConstant.LI_CATEGORY_CLIENT_TYPE,
                LiQueryConstant.LI_CATEGORY_QUERYSETTINGS_TYPE, LiBrowse.class);
    }

    /**
     * Fetches all details about the specified user. Create parameters with {@link LiClientRequestParams.LiUserDetailsClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiUserDetailsClientRequestParams}.
     * @return LUser {@link LiUser}
     * @throws LiRestResponseException LiClient {@link LiRestResponseException}
     */
    public static LiClient getUserDetailsClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_USER_DETAILS_CLIENT);
        String userId = ((LiClientRequestParams.LiUserDetailsClientRequestParams) params).getUserId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", userId);
        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_USER_DETAILS_CLIENT_BASE_LIQL, LiQueryConstant.LI_USER_DETAILS_CLIENT_TYPE,
                LiQueryConstant.LI_USER_DETAILS_QUERYSETTINGS_TYPE, LiUser.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details of the specified message. Create parameters with {@link LiClientRequestParams.LiMessageClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiMessageClientRequestParams}.
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessageClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MESSAGE_CLIENT);
        Long messageId = ((LiClientRequestParams.LiMessageClientRequestParams) params).getMessageId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(messageId));

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_MESSAGE_CONVERSATION_BASE_LIQL,
                LiQueryConstant.LI_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details of all floated (or "pinned") of the specified board for the user in context.
     * Create parameters with {@link LiClientRequestParams.LiFloatedMessagesClientRequestParams}.
     *
     * @param params the scope for searching the floated messages. Supported scopes: "global", "local"
     *               (required)
     * @return LiFloatedMessageModel {@link LiFloatedMessageModel}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getFloatedMessagesClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_FLOATED_MESSAGES_CLIENT);
        String boardId = ((LiClientRequestParams.LiFloatedMessagesClientRequestParams) params).getBoardId();
        String scope = ((LiClientRequestParams.LiFloatedMessagesClientRequestParams) params).getScope();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(boardId)).replaceAll("&&", scope);

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_BASE_LIQL,
                LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_FLOATED_MESSAGE_QUERYSETTINGS_TYPE, LiFloatedMessageModel.class)
                .setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details for the specified set of messages. Create parameters with {@link LiClientRequestParams.LiMessagesByIdsClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiMessagesByIdsClientRequestParams}.
     * @return LiMessages {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesByIdsClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MESSAGES_BY_ID_CLIENT);
        Set<String> messageIds = ((LiClientRequestParams.LiMessagesByIdsClientRequestParams) params).getMessageIds();
        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String id : messageIds) {
            if (!isFirst) {
                sb.append(",");
            } else {
                isFirst = false;
            }
            sb.append("'");
            sb.append(id);
            sb.append("'");
        }
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", sb.toString());

        return new LiBaseGetClient(params.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CLIENT_TYPE,
                LiQueryConstant.LI_MESSAGE_BY_IDS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Kudos the specified message for the user in context. Create parameters with {@link LiClientRequestParams.LiKudoClientRequestParams}.
     * Uses {@link LiPostKudoModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params the ID of the message to kudo (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getKudoClient(LiClientRequestParams params) throws LiRestResponseException {

        params.validate(Client.LI_KUDO_CLIENT);
        String messageId = ((LiClientRequestParams.LiKudoClientRequestParams) params).getMessageId();
        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), String.format("messages/%s/kudos", messageId));
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        LiPostKudoModel liPostKudoModel = new LiPostKudoModel();
        LiMessage liMessage = new LiMessage();
        LiBaseModelImpl.LiInt liInt = new LiBaseModelImpl.LiInt();
        liInt.setValue(Long.valueOf(messageId));
        liMessage.setId(liInt);
        liPostKudoModel.setType(LiQueryConstant.LI_KUDO_TYPE);
        liPostKudoModel.setMessage(liMessage);
        liBasePostClient.postModel = liPostKudoModel;

        return liBasePostClient;
    }

    /**
     * Unkudos the specified message for the user in context. Create parameters with {@link LiClientRequestParams.LiUnKudoClientRequestParams}.
     * Uses {@link LiPostKudoModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params the ID of the message to unkudoed (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUnKudoClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_UNKUDO_CLIENT);
        String messageId = ((LiClientRequestParams.LiUnKudoClientRequestParams) params).getMessageId();
        LiClientRequestParams.LiGenericDeleteClientRequestParams requestParams = new LiClientRequestParams.LiGenericDeleteClientRequestParams(
                params.getContext(), CollectionsType.MESSAGE, messageId, "/kudos");

        return getGenericQueryDeleteClient(requestParams);
    }

    /**
     * Marks a message as an accepted solution. Create parameters with {@link LiClientRequestParams.LiAcceptSolutionClientRequestParams}.
     * Uses {@link LiAcceptSolutionModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params the ID of the message to mark as a solution (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getAcceptSolutionClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_ACCEPT_SOLUTION_CLIENT);
        Long messageId = ((LiClientRequestParams.LiAcceptSolutionClientRequestParams) params).getMessageId();
        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "solutions_data");
        LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);
        LiAcceptSolutionModel liAcceptSolutionModel = new LiAcceptSolutionModel();
        liAcceptSolutionModel.setType(LiQueryConstant.LI_ACCEPT_SOLUTION_TYPE);
        liAcceptSolutionModel.setMessageid(String.valueOf(messageId));
        liBasePostClient.postModel = liAcceptSolutionModel;

        return liBasePostClient;
    }

    /**
     * Posts a new message to the community. Create parameters with {@link LiClientRequestParams.LiCreateMessageClientRequestParams}.
     * Uses {@link LiPostMessageModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiCreateMessageClientRequestParams}
     *               the subject of the message (required)
     *               the body of the message (required)
     *               the image filename (if an image is included in the message)
     *               the image ID (if an image is included in the message)
     *               the ID of the board where the message will be posted (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getCreateMessageClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_CREATE_MESSAGE_CLIENT);
        final String subject = ((LiClientRequestParams.LiCreateMessageClientRequestParams) params).getSubject();
        final String boardId = ((LiClientRequestParams.LiCreateMessageClientRequestParams) params).getBoardId();
        final String imageId = ((LiClientRequestParams.LiCreateMessageClientRequestParams) params).getImageId();
        final String imageName = ((LiClientRequestParams.LiCreateMessageClientRequestParams) params).getImageName();

        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "messages");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        LiPostMessageModel liPostMessageModel = new LiPostMessageModel();
        LiBoard liBoard = new LiBoard();
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(boardId);
        liBoard.setId(liString);
        liPostMessageModel.setType(LiQueryConstant.LI_POST_QUESTION_TYPE);
        liPostMessageModel.setSubject(subject);

        String body = ((LiClientRequestParams.LiCreateMessageClientRequestParams) params).getBody();
        body = embedImageTag(body, imageId, imageName);
        liPostMessageModel.setBody(body);
        liPostMessageModel.setBoard(liBoard);
        liBasePostClient.postModel = liPostMessageModel;

        return liBasePostClient;
    }

    /**
     * Updates an existing message by ID. Crate parameters with {@link LiClientRequestParams.LiUpdateMessageClientRequestParams}.
     * Uses {@link LiPostMessageModel} to build the request body. The model is converted to a JsonObject, which is then used in the PUT call.
     *
     * @param params {@link LiClientRequestParams.LiUpdateMessageClientRequestParams}
     *               the Android context (required)
     *               the message subject
     *               the message body
     *               the message ID (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUpdateMessageClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_UPDATE_MESSAGE_CLIENT);
        final String subject = ((LiClientRequestParams.LiUpdateMessageClientRequestParams) params).getSubject();
        final String body = ((LiClientRequestParams.LiUpdateMessageClientRequestParams) params).getBody();
        final String messageId = ((LiClientRequestParams.LiUpdateMessageClientRequestParams) params).getMessageId();

        final String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), String.format("messages/%s", messageId));
        final LiBasePutClient liBasePutClient = new LiBasePutClient(params.getContext(), path);
        LiPostMessageModel liPostMessageModel = new LiPostMessageModel();
        liPostMessageModel.setType(LiQueryConstant.LI_POST_QUESTION_TYPE);
        liPostMessageModel.setBody(body);
        liPostMessageModel.setSubject(subject);
        liBasePutClient.postModel = liPostMessageModel;

        return liBasePutClient;
    }

    /**
     * Embeds an image tag into the message body.
     *
     * @param body      Body of the message to which image has to be attached
     * @param imageId   Id of the image received from community.
     * @param imageName Name of the image.
     * @return Message Body with embedded image tag.
     */
    private static String embedImageTag(String body, String imageId, String imageName) {
        if (!TextUtils.isEmpty(imageId)) {
            if (body == null) {
                body = "<p>&nbsp;</p>";
            }
            body = body + LiQueryConstant.LI_LINE_SEPARATOR + String.format(LiQueryConstant.LI_INSERT_IMAGE_MACRO, imageId, imageName);
        }
        return body;
    }

    /**
     * Creates a reply or comment. Create parameters with {@link LiClientRequestParams.LiCreateReplyClientRequestParams}.
     * Uses {@link LiReplyMessageModel} to build request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiCreateReplyClientRequestParams}
     *               the Android context (required)
     *               the message ID (required)
     *               the message body (required)
     *               the image filename (if the message includes an image)
     *               the image ID (if the message includes an image)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException LiClient {@link LiRestResponseException}
     */
    public static LiClient getCreateReplyClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_CREATE_REPLY_CLIENT);
        final String subject = ((LiClientRequestParams.LiCreateReplyClientRequestParams) params).getSubject();
        final Long messageId = ((LiClientRequestParams.LiCreateReplyClientRequestParams) params).getMessageId();
        final String imageId = ((LiClientRequestParams.LiCreateReplyClientRequestParams) params).getImageId();
        final String imageName = ((LiClientRequestParams.LiCreateReplyClientRequestParams) params).getImageName();
        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "messages");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        final LiReplyMessageModel liReplyMessage = new LiReplyMessageModel();
        LiMessage parent = new LiMessage();
        LiBaseModelImpl.LiInt liId = new LiBaseModelImpl.LiInt();
        liId.setValue(Long.valueOf(messageId));
        parent.setId(liId);

        String body = ((LiClientRequestParams.LiCreateReplyClientRequestParams) params).getBody();
        body = embedImageTag(body, imageId, imageName);
        liReplyMessage.setSubject(subject);
        liReplyMessage.setBody(body);
        liReplyMessage.setType(LiQueryConstant.LI_REPLY_MESSAGE_TYPE);
        liReplyMessage.setParent(parent);
        liBasePostClient.postModel = liReplyMessage;

        return liBasePostClient;
    }

    /**
     * Uploads an image to the community on behalf of the user in context. The image is placed into the user's public album.
     * Create parameters with {@link LiClientRequestParams.LiUploadImageClientRequestParams}.
     * Uses {@link LiUploadImageModel} to build request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiUploadImageClientRequestParams}
     *               the Android context (required)
     *               the image title (required)
     *               the image filename (required)
     *               the absolute path to the image (required)
     *               Note: the image filename and the filename in the absolute path param above must
     *               be equal.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUploadImageClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_UPLOAD_IMAGE_CLIENT);

        final String title = ((LiClientRequestParams.LiUploadImageClientRequestParams) params).getTitle();
        final String description = ((LiClientRequestParams.LiUploadImageClientRequestParams) params).getDescription();
        final String imageName = ((LiClientRequestParams.LiUploadImageClientRequestParams) params).getImageName();
        final String path = ((LiClientRequestParams.LiUploadImageClientRequestParams) params).getPath();
        String urlPath = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "images");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), urlPath, path, imageName);

        LiUploadImageModel uploadImageModel = new LiUploadImageModel();
        uploadImageModel.setType(LiQueryConstant.LI_IMAGE_TYPE);
        uploadImageModel.setTitle(title);
        uploadImageModel.setDescription(description);
        uploadImageModel.setField("image.content");
        uploadImageModel.setVisibility("public");
        liBasePostClient.postModel = uploadImageModel;

        return liBasePostClient;
    }

    /**
     * Creates an abuse report for the specified message. Create parameters with {@link LiClientRequestParams.LiReportAbuseClientRequestParams}.
     * Uses the {@link LiMarkAbuseModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiReportAbuseClientRequestParams}
     *               the Android context (required)
     *               the ID of message to report (required)
     *               the message body of the message being reported (required)
     *               the ID of the user making the abuse report (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getReportAbuseClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MARK_ABUSE_CLIENT);

        final String messageId = ((LiClientRequestParams.LiReportAbuseClientRequestParams) params).getMessageId();
        final String userId = ((LiClientRequestParams.LiReportAbuseClientRequestParams) params).getUserId();
        final String body = ((LiClientRequestParams.LiReportAbuseClientRequestParams) params).getBody();
        final String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "abuse_reports");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        LiMarkAbuseModel liMarkAbuseModel = new LiMarkAbuseModel();
        liMarkAbuseModel.setType(LiQueryConstant.LI_MARK_ABUSE_TYPE);

        LiMessage liMessage = new LiMessage();

        LiBaseModelImpl.LiInt liInt = new LiBaseModelImpl.LiInt();
        liInt.setValue(Long.valueOf(messageId));
        liMessage.setId(liInt);

        LiUser liUser = new LiUser();
        liUser.setId(Long.valueOf(userId));
        liMarkAbuseModel.setMessage(liMessage);
        liMarkAbuseModel.setReporter(liUser);
        liMarkAbuseModel.setBody(body);
        liBasePostClient.postModel = liMarkAbuseModel;

        return liBasePostClient;
    }

    /**
     * Fetches the ID corresponding to device ID from the community. Create parameters with {@link LiClientRequestParams.LiDeviceIdFetchClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiDeviceIdFetchClientRequestParams}
     *               the ID registered with notification provider
     *               the Global provider for Push notification. Currently "GCM" and "FIREBASE".
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getDeviceIdFetchClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_DEVICE_ID_FETCH_CLIENT);

        final String deviceId = ((LiClientRequestParams.LiDeviceIdFetchClientRequestParams) params).getDeviceId();
        final String pushNotificationProvider = ((LiClientRequestParams.LiDeviceIdFetchClientRequestParams) params).getPushNotificationProvider();

        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "user_device_data");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        final LiUserDeviceDataModel liUserDeviceDataModel = new LiUserDeviceDataModel();
        liUserDeviceDataModel.setType(LiQueryConstant.LI_USER_DEVICE_ID_FETCH_TYPE);
        liUserDeviceDataModel.setDeviceId(deviceId);
        liUserDeviceDataModel.setClientId(LiSDKManager.getInstance().getCredentials().getClientKey());
        liUserDeviceDataModel.setApplicationType(LiQueryConstant.LI_APPLICATION_TYPE);
        liUserDeviceDataModel.setPushNotificationProvider(pushNotificationProvider);
        liBasePostClient.postModel = liUserDeviceDataModel;

        return liBasePostClient;
    }

    /**
     * Updates the device ID in community with the given device ID. Create parameters with {@link LiClientRequestParams.LiDeviceIdUpdateClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiDeviceIdUpdateClientRequestParams}
     *               the Android context (required)
     *               the device ID registered with the push notification provider (required)
     *               the ID corresponding to device ID in the community (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getDeviceIdUpdateClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_DEVICE_ID_UPDATE_CLIENT);
        String deviceId = ((LiClientRequestParams.LiDeviceIdUpdateClientRequestParams) params).getDeviceId();
        String id = ((LiClientRequestParams.LiDeviceIdUpdateClientRequestParams) params).getDeviceId();

        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "user_device_data/" + id);
        LiBasePutClient liBasePostClient = new LiBasePutClient(params.getContext(), path);
        LiUserDeviceIdUpdateModel liUserDeviceIdUpdateModel = new LiUserDeviceIdUpdateModel();
        liUserDeviceIdUpdateModel.setType(LiQueryConstant.LI_USER_DEVICE_ID_FETCH_TYPE);
        liUserDeviceIdUpdateModel.setDeviceId(deviceId);
        liBasePostClient.postModel = liUserDeviceIdUpdateModel;

        return liBasePostClient;
    }

    /**
     * Creates new user account. Create parameters with {@link LiClientRequestParams.LiCreateUserParams}.
     * Uses {@link LiCreateUpdateUserModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiCreateUserParams}
     *               the Android context (required)
     *               the user's login (required)
     *               the user's password (required)
     *               the user's email (required)
     *               the user's avatar
     *               the user's biography
     *               the user's first name
     *               the user's last name
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getCreateUserClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_CREATE_USER_CLIENT);

        final LiClientRequestParams.LiCreateUserParams liCreateUserParams = ((LiClientRequestParams.LiCreateUserParams) params);
        final LiAvatar avatar = new LiAvatar();

        if (!TextUtils.isEmpty(liCreateUserParams.getAvatarUrl())) {
            avatar.setUrl(liCreateUserParams.getAvatarUrl());
        }
        if (!TextUtils.isEmpty(liCreateUserParams.getAvatarExternal())) {
            avatar.setExternal(liCreateUserParams.getAvatarExternal());
        }
        if (!TextUtils.isEmpty(liCreateUserParams.getAvatarInternal())) {
            avatar.setInternal(liCreateUserParams.getAvatarInternal());
        }
        if (!TextUtils.isEmpty(liCreateUserParams.getAvatarImageId())) {
            avatar.setImage(liCreateUserParams.getAvatarImageId());
        }

        final String biography = ((LiClientRequestParams.LiCreateUserParams) params).getBiography();
        final String coverImage = ((LiClientRequestParams.LiCreateUserParams) params).getCoverImage();
        final String email = ((LiClientRequestParams.LiCreateUserParams) params).getEmail();
        final String firstName = ((LiClientRequestParams.LiCreateUserParams) params).getFirstName();
        final String lastName = ((LiClientRequestParams.LiCreateUserParams) params).getLastName();
        final String login = ((LiClientRequestParams.LiCreateUserParams) params).getLogin();
        final String password = ((LiClientRequestParams.LiCreateUserParams) params).getPassword();

        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "users");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        final LiCreateUpdateUserModel liCreateUpdateUserModel = new LiCreateUpdateUserModel();
        liCreateUpdateUserModel.setType(LiQueryConstant.LI_USER_DETAILS_CLIENT_TYPE);
        liCreateUpdateUserModel.setAvatar(avatar);
        liCreateUpdateUserModel.setBiography(biography);
        liCreateUpdateUserModel.setCoverImage(coverImage);
        liCreateUpdateUserModel.setEmail(email);
        liCreateUpdateUserModel.setFirstName(firstName);
        liCreateUpdateUserModel.setLastName(lastName);
        liCreateUpdateUserModel.setLogin(login);
        liCreateUpdateUserModel.setPassword(password);
        liBasePostClient.postModel = liCreateUpdateUserModel;

        return liBasePostClient;
    }

    /**
     * Mark a single message as read or unread Create parameters with {@link LiClientRequestParams.LiMarkMessageParams}.
     * Uses the {@link LiMarkMessageModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiMarkMessageParams}
     *               the Android context (required)
     *               the ID of the user marking the message (required)
     *               the ID of the message (required)
     *               whether to mark the message read or unread. Pass 'markUnread' as 'true' to mark the message as read. (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getMarkMessagePostClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MARK_MESSAGE_POST_CLIENT);

        final String userId = ((LiClientRequestParams.LiMarkMessageParams) params).getUserId();
        final String messageId = ((LiClientRequestParams.LiMarkMessageParams) params).getMessageId();
        final boolean markUnread = ((LiClientRequestParams.LiMarkMessageParams) params).isMarkUnread();

        final String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "messages_read");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        final LiMarkMessageModel liMarkMessageModel = new LiMarkMessageModel();
        liMarkMessageModel.setType(LiQueryConstant.LI_MARK_MESSAGE_CLIENT_TYPE);
        liMarkMessageModel.setUser(userId);
        liMarkMessageModel.setMessageId(messageId);
        liMarkMessageModel.setMarkUnread(markUnread);
        liBasePostClient.postModel = liMarkMessageModel;

        return liBasePostClient;
    }

    /**
     * Marks a set of messages (not necessarily in the same thread) as read or unread. Compare this to getMarkTopicPostClient.
     * Create parameters with {@link LiClientRequestParams.LiMarkMessagesParams}.
     * Uses the {@link LiMarkMessagesModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiMarkMessagesParams}
     *               the Android context
     *               the user marking the messages (required)
     *               the IDs of the messages to mark in a comma-separated list (required)
     *               whether to mark the messages read or unread. Pass 'markUnread' as 'true' to mark the messages as read. (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getMarkMessagesPostClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MARK_MESSAGES_POST_CLIENT);

        final String userId = ((LiClientRequestParams.LiMarkMessagesParams) params).getUserId();
        final String messageIds = ((LiClientRequestParams.LiMarkMessagesParams) params).getMessageIds();
        final boolean markUnread = ((LiClientRequestParams.LiMarkMessagesParams) params).isMarkUnread();

        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "messages_read");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        final LiMarkMessagesModel liMarkMessagesModel = new LiMarkMessagesModel();
        liMarkMessagesModel.setType(LiQueryConstant.LI_MARK_MESSAGE_CLIENT_TYPE);
        liMarkMessagesModel.setUser(userId);
        liMarkMessagesModel.setMessageIds(messageIds);
        liMarkMessagesModel.setMarkUnread(markUnread);
        liBasePostClient.postModel = liMarkMessagesModel;

        return liBasePostClient;
    }

    /**
     * Marks a topic message and all its replies as read or unread. Compare this to getMarkMessagesPostClient. Create parameters with
     * {@link LiClientRequestParams.LiMarkTopicParams}. Uses the {@link LiMarkTopicModel} to build the request body. The model is converted to a JsonObject,
     * which is then used in the POST call.
     *
     * @param params {@link LiClientRequestParams.LiMarkTopicParams}
     *               the Android context (required)
     *               the ID of the topic message to mark (required)
     *               whether to mark the messages read or unread. Pass 'markUnread' as 'true' to mark
     *               the messages as read. (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getMarkTopicPostClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MARK_TOPIC_POST_CLIENT);

        final String userId = ((LiClientRequestParams.LiMarkTopicParams) params).getUserId();
        final String topicId = ((LiClientRequestParams.LiMarkTopicParams) params).getTopicId();
        final boolean markUnread = ((LiClientRequestParams.LiMarkTopicParams) params).isMarkUnread();
        final String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "messages_read");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);

        LiMarkTopicModel liMarkTopicModel = new LiMarkTopicModel();
        liMarkTopicModel.setType(LiQueryConstant.LI_MARK_MESSAGE_CLIENT_TYPE);
        liMarkTopicModel.setUser(userId);
        liMarkTopicModel.setTopicId(topicId);
        liMarkTopicModel.setMarkUnread(markUnread);
        liBasePostClient.postModel = liMarkTopicModel;

        return liBasePostClient;
    }


    /**
     * <p>
     * Creates a subscription to the specified target (a board or message). Create parameters with {@link LiClientRequestParams.LiPostSubscriptionParams}.
     * </p>
     * <p>
     * The parameters can either be a target which is a {@link LiBaseModel} in the form of {@link LiMessage} or {@link LiBrowse} which was selected to be
     * subscribed to and needs to be passed in the constructor Alternatively in the parameters "message/boardId" and "type" need to be passed in the
     * constructor. "type" is either "message" or "board" depending upon what was selected. If both are set then the target takes precedence and string
     * parameters are ignored.
     * </p>
     * <p>
     * Uses the {@link LiSubscriptionPostModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     * </p>
     *
     * @param params {@link LiClientRequestParams.LiPostSubscriptionParams}
     *               the Android context
     *               the target of the subscription -- a board or message (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getSubscriptionPostClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_SUBSCRIPTION_POST_CLIENT);
        LiClientRequestParams.LiPostSubscriptionParams liPostSubscriptionParams = ((LiClientRequestParams.LiPostSubscriptionParams) params);
        LiSubscriptionPostModel.Target target = liPostSubscriptionParams.getTarget();
        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), "subscriptions");
        LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);
        LiSubscriptionPostModel liSubscriptionPostModel = new LiSubscriptionPostModel(target);
        liBasePostClient.postModel = liSubscriptionPostModel;

        return liBasePostClient;
    }

    private static String extractBoardId(String boardId) {
        String finalBoardId = boardId;
        String boardPrefix = "board:";
        if (finalBoardId.contains(boardPrefix)) {
            finalBoardId = finalBoardId.substring(finalBoardId.indexOf(boardPrefix) + boardPrefix.length(), finalBoardId.length());
        }

        return finalBoardId;
    }

    /**
     * Deletes a subscription.
     *
     * @param params {@link LiClientRequestParams.LiDeleteSubscriptionParams}
     *               the Android context
     *               the subscription ID
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getSubscriptionDeleteClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_SUBSCRIPTION_DELETE_CLIENT);
        String id = ((LiClientRequestParams.LiDeleteSubscriptionParams) params).getSubscriptionId();
        LiClientRequestParams.LiGenericDeleteClientRequestParams outputParams
                = new LiClientRequestParams.LiGenericDeleteClientRequestParams(params.getContext(), CollectionsType.SUBSCRIPTION, id);

        return getGenericQueryDeleteClient(outputParams);
    }

    /**
     * <p>
     * Updates an existing user. Create parameters with {@link LiClientRequestParams.LiUpdateUserParams}. Uses {@link LiCreateUpdateUserModel} to build the
     * request body. The model is converted to a JsonObject, which is then used in the POST call.
     * </p>
     *
     * @param params {@link LiClientRequestParams.LiUpdateUserParams}
     *               the Android context (required)
     *               the user's avatar image url
     *               the user's avatar image external url
     *               the user's avatar image internal url
     *               the user's avatar image id
     *               the user's biography
     *               the user's cover image
     *               the user's email
     *               the user's first name
     *               the user's last name
     *               the user's login
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getUpdateUserClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_UPDATE_USER_CLIENT);

        final LiClientRequestParams.LiUpdateUserParams liUpdateUserParams = (LiClientRequestParams.LiUpdateUserParams) params;

        final LiAvatar avatar = new LiAvatar();

        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarUrl())) {
            avatar.setUrl(liUpdateUserParams.getAvatarUrl());
        }
        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarExternal())) {
            avatar.setExternal(liUpdateUserParams.getAvatarExternal());
        }
        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarInternal())) {
            avatar.setInternal(liUpdateUserParams.getAvatarInternal());
        }
        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarImageId())) {
            avatar.setImage(liUpdateUserParams.getAvatarImageId());
        }

        final String biography = liUpdateUserParams.getBiography();
        final String coverImage = liUpdateUserParams.getCoverImage();
        final String email = liUpdateUserParams.getEmail();
        final String firstName = liUpdateUserParams.getFirstName();
        final String lastName = liUpdateUserParams.getLastName();
        final String login = liUpdateUserParams.getLogin();
        final String id = liUpdateUserParams.getId();

        final String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), String.format("users/%s", id));
        final LiBasePutClient liBasePutClient = new LiBasePutClient(params.getContext(), path);

        final LiCreateUpdateUserModel liCreateUpdateUserModel = new LiCreateUpdateUserModel();
        liCreateUpdateUserModel.setType(LiQueryConstant.LI_USER_DETAILS_CLIENT_TYPE);
        liCreateUpdateUserModel.setAvatar(avatar);
        liCreateUpdateUserModel.setBiography(biography);
        liCreateUpdateUserModel.setCoverImage(coverImage);
        liCreateUpdateUserModel.setEmail(email);
        liCreateUpdateUserModel.setFirstName(firstName);
        liCreateUpdateUserModel.setLastName(lastName);
        liCreateUpdateUserModel.setLogin(login);
        liBasePutClient.postModel = liCreateUpdateUserModel;

        return liBasePutClient;
    }

    /**
     * This is generic Post client. Provide a path to a Community API v1 or v2 endpoint and a request body. Create parameters with
     * {@link LiClientRequestParams.LiGenericPostClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiGenericPostClientRequestParams}
     *               the Android context
     *               the path to a Community v1 or v2 endpoint
     *               the request body as a {@link JsonObject}
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericPostClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_GENERIC_POST_CLIENT);

        final String path = ((LiClientRequestParams.LiGenericPostClientRequestParams) params).getPath();
        final JsonElement requestBody = ((LiClientRequestParams.LiGenericPostClientRequestParams) params).getRequestBody();

        final Map<String, String> additionalHeaders = ((LiClientRequestParams.LiGenericPostClientRequestParams) params).getAdditionalHttpHeaders();

        final String requestPath = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), path);
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), requestPath, additionalHeaders);

        final LiGenericPostModel genericPostModel = new LiGenericPostModel();
        genericPostModel.setData(requestBody);
        liBasePostClient.postModel = genericPostModel;

        return liBasePostClient;
    }

    /**
     * This is a beacon client which is used to send information to the community backend for analytics aka LSI
     *
     * @param params {@link LiClientRequestParams.LiBeaconPostClientRequestParams}
     */
    public static LiClient getBeaconClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_BEACON_CLIENT);
        LiClientRequestParams.LiBeaconPostClientRequestParams beaconParams = ((LiClientRequestParams.LiBeaconPostClientRequestParams) params);
        String targetType = beaconParams.getTargetType();
        String targetId = beaconParams.getTargetId();
        Context context = beaconParams.getContext();
        JsonObject requestBody = new JsonObject();
        if (!TextUtils.isEmpty(targetId) && !TextUtils.isEmpty(targetType)) {
            JsonObject targetJsonObject = new JsonObject();
            targetJsonObject.addProperty("type", targetType);
            targetJsonObject.addProperty("id", targetId);
            requestBody.add("target", targetJsonObject);
        }
        Map<String, String> additionalHeaders = new HashMap<>();
        String visitOriginTime = LiSDKManager.getInstance().getFromSecuredPreferences(context, LiCoreSDKConstants.LI_VISIT_ORIGIN_TIME_KEY);
        if (!TextUtils.isEmpty(visitOriginTime)) {
            additionalHeaders.put(LiRequestHeaderConstants.LI_REQUEST_VISIT_ORIGIN_TIME, visitOriginTime);
        }
        String visitorLastIssuedTime = LiSDKManager.getInstance().getFromSecuredPreferences(context, LiCoreSDKConstants.LI_VISIT_LAST_ISSUE_TIME_KEY);
        if (!TextUtils.isEmpty(visitorLastIssuedTime)) {
            additionalHeaders.put(LiRequestHeaderConstants.LI_REQUEST_VISIT_LAST_ISSUE_TIME, visitorLastIssuedTime);
        }

        LiClientRequestParams outputParams = new LiClientRequestParams.LiGenericPostClientRequestParams(context, "beacon", requestBody, additionalHeaders);

        return LiClientManager.getGenericPostClient(outputParams);
    }

    /**
     * This is generic PUT client. Provide the path to a Community API v1 or v2 endpoint and a request body. Create parameters with
     * {@link LiClientRequestParams.LiGenericPutClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiGenericPutClientRequestParams}
     *               the Android context  (required)
     *               the path to a Community v1 or v2 endpoint (required)
     *               the request body as a {@link JsonObject} (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericPutClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_GENERIC_PUT_CLIENT);
        String path = ((LiClientRequestParams.LiGenericPutClientRequestParams) params).getPath();
        JsonObject requestBody = ((LiClientRequestParams.LiGenericPutClientRequestParams) params).getRequestBody();

        String requestPath = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), path);
        LiBasePutClient liBasePutClient = new LiBasePutClient(params.getContext(), requestPath);
        LiGenericPutModel genericPutModel = new LiGenericPutModel();
        genericPutModel.setData(requestBody);
        liBasePutClient.postModel = genericPutModel;

        return liBasePutClient;
    }

    /**
     * This is generic Get client. Provide a LiQL query. Create parameters with
     * {@link LiClientRequestParams.LiGenericLiqlClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiGenericLiqlClientRequestParams}
     *               the Android context (required)
     *               a custom LiQL query (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericLiqlGetClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_GENERIC_LIQL_CLIENT);
        String liQuery = ((LiClientRequestParams.LiGenericLiqlClientRequestParams) params).getLiQuery();

        return new LiBaseGetClient(params.getContext(), liQuery, null, LiQueryConstant.LI_GENERIC_TYPE, null);
    }

    /**
     * This is generic Get client. Provide a LiQL query. Create parameters with
     * {@link LiClientRequestParams.LiGenericLiqlClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiGenericLiqlClientRequestParams}
     *               the Android context (required)
     *               a custom LiQL query (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericNoLiqlGetClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_GENERIC_LIQL_CLIENT);
        String pathParam = ((LiClientRequestParams.LiGenericNoLiqlClientRequestParams) params).getPathParam();
        String queryParams = ((LiClientRequestParams.LiGenericNoLiqlClientRequestParams) params).getQueryParams();

        return new LiBaseGetClient(params.getContext(), queryParams, null, LiQueryConstant.LI_GENERIC_TYPE, null, pathParam);
    }


    /**
     * Creates custom WHERE clause, ORDER BY, and/or LIMIT parameters to a LIQL queries used by a Lithium API provider. Create parameters with
     * {@link LiClientRequestParams.LiGenericQueryParamsClientRequestParams}.
     *
     * @param params {@link LiClientRequestParams.LiGenericQueryParamsClientRequestParams} the Android context
     * @return LiClient {@link LiClient}
     */
    public static LiClient getGenericQueryParamsGetClient(LiClientRequestParams params) {
        params.validate(Client.LI_GENERIC_QUERY_PARAMS_CLIENT);
        LiQueryRequestParams liQueryRequestParams = ((LiClientRequestParams.LiGenericQueryParamsClientRequestParams) params).getLiQueryRequestParams();

        return new LiBaseGetClient(params.getContext(), LiClientConfig.getBaseQuery(liQueryRequestParams.getClient()),
                LiClientConfig.getType(liQueryRequestParams.getClient()), LiClientConfig.getResponseClass(liQueryRequestParams.getClient()),
                liQueryRequestParams);
    }

    /**
     * This is a generic DELETE client.
     *
     * @param params {@link LiClientRequestParams.LiGenericDeleteClientRequestParams}
     * @return LiClient {@link LiClient}
     */
    public static LiClient getGenericQueryDeleteClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_GENERIC_DELETE_QUERY_PARAMS_CLIENT);
        LiClientRequestParams.LiGenericDeleteClientRequestParams clientRequestParams = (LiClientRequestParams.LiGenericDeleteClientRequestParams) params;
        Map<String, String> queryRequestParams = clientRequestParams.getLiQueryRequestParams();
        String id = clientRequestParams.getId();
        String extraPathAfterId = clientRequestParams.getSubResourcePath();
        CollectionsType collectionsType = clientRequestParams.getCollectionsType();
        StringBuilder path = new StringBuilder();
        path = path.append(String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(), String.format("%s/%s", collectionsType.getValue(), id)));
        if (extraPathAfterId != null) {
            path = path.append(extraPathAfterId);
        }
        if (queryRequestParams != null && queryRequestParams.size() > 0) {
            path = path.append("?");
            for (String key : queryRequestParams.keySet()) {
                String value = queryRequestParams.get(key);
                if (value != null) {
                    path = path.append(key).append("=").append(value);
                }
            }
        }

        return new LiBaseDeleteClient(params.getContext(), path.toString());
    }

    /**
     * Deletes the specified message. Create parameters with {@link LiClientRequestParams.LiMessageDeleteClientRequestParams}. Optionally pass the
     * 'includeReplies' parameters as 'true' to delete replies or comments associated with the message.
     *
     * @param params {@link LiClientRequestParams.LiMessageDeleteClientRequestParams}
     *               the Android context
     *               the ID of the message to delete
     *               whether or not to delete replies/comments to the message
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessageDeleteClient(LiClientRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_MESSAGE_DELETE_CLIENT);
        String messageId = ((LiClientRequestParams.LiMessageDeleteClientRequestParams) params).getMessageId();
        boolean includeReplies = ((LiClientRequestParams.LiMessageDeleteClientRequestParams) params).isIncludeReplies();
        LiClientRequestParams.LiGenericDeleteClientRequestParams deleteParams;
        if (includeReplies) {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("delete_message.include_replies", "true");
            deleteParams = new LiClientRequestParams.LiGenericDeleteClientRequestParams(params.getContext(), CollectionsType.MESSAGE, messageId, requestParams);
        } else {
            deleteParams = new LiClientRequestParams.LiGenericDeleteClientRequestParams(params.getContext(), CollectionsType.MESSAGE, messageId);
        }
        return getGenericQueryDeleteClient(deleteParams);
    }

    public static LiClient getLogoutClient(LiClientRequestParams.LiLogoutRequestParams params) throws LiRestResponseException {
        params.validate(Client.LI_LOGOUT_CLIENT);

        String path = String.format(API_PATH_PREFIX, LiSDKManager.getInstance().getTenantId(),"auth/signout");
        final LiBasePostClient liBasePostClient = new LiBasePostClient(params.getContext(), path);
        LiPostLogoutModel model = new LiPostLogoutModel(params.getDeviceId());
        liBasePostClient.postModel = model;

        return liBasePostClient;
    }

    /**
     * Enum of all clients.
     */
    public enum Client {
        LI_MESSAGES_CLIENT,
        LI_MESSAGE_DELETE_CLIENT,
        LI_MESSAGES_BY_BOARD_ID_CLIENT,
        LI_SDK_SETTINGS_CLIENT,
        LI_USER_SUBSCRIPTIONS_CLIENT,
        LI_CATEGORY_BOARDS_CLIENT,
        LI_BOARDS_BY_DEPTH_CLIENT,
        LI_REPLIES_CLIENT,
        LI_SEARCH_CLIENT,
        LI_USER_MESSAGES_CLIENT,
        LI_USER_DETAILS_CLIENT,
        LI_MESSAGE_CLIENT,
        LI_FLOATED_MESSAGES_CLIENT,
        LI_MESSAGES_BY_ID_CLIENT,
        LI_KUDO_CLIENT,
        LI_UNKUDO_CLIENT,
        LI_ACCEPT_SOLUTION_CLIENT,
        LI_CREATE_MESSAGE_CLIENT,
        LI_CREATE_REPLY_CLIENT,
        LI_UPLOAD_IMAGE_CLIENT,
        LI_MARK_ABUSE_CLIENT,
        LI_DEVICE_ID_FETCH_CLIENT,
        LI_DEVICE_ID_UPDATE_CLIENT,
        LI_GENERIC_POST_CLIENT,
        LI_BEACON_CLIENT,
        LI_GENERIC_LIQL_CLIENT,
        LI_GENERIC_QUERY_PARAMS_CLIENT,
        LI_GENERIC_DELETE_QUERY_PARAMS_CLIENT,
        LI_ARTICLES_CLIENT,
        LI_SUBSCRIPTION_CLIENT,
        LI_BROWSE_CLIENT,
        LI_MESSAGE_CHILDREN_CLIENT,
        LI_QUESTIONS_CLIENT,
        LI_CATEGORY_CLIENT,
        LI_CREATE_USER_CLIENT,
        LI_UPDATE_USER_CLIENT,
        LI_POST_SUBSCRIPTION_CLIENT,
        LI_DELETE_SUBSCRIPTION_CLIENT,
        LI_GENERIC_PUT_CLIENT,
        LI_SUBSCRIPTION_POST_CLIENT,
        LI_SUBSCRIPTION_DELETE_CLIENT,
        LI_ARTICLES_BROWSE_CLIENT,
        LI_MARK_MESSAGE_POST_CLIENT,
        LI_MARK_MESSAGES_POST_CLIENT,
        LI_MARK_TOPIC_POST_CLIENT,
        LI_UPDATE_MESSAGE_CLIENT,
        LI_LOGOUT_CLIENT
    }

    /**
     * Enum of all collection types.
     */
    public enum CollectionsType {
        MESSAGE(LiQueryConstant.LI_MESSAGE_TYPE),
        SUBSCRIPTION(LiQueryConstant.LI_SUBSCRIPTION_TYPE);

        private final String value;

        CollectionsType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
