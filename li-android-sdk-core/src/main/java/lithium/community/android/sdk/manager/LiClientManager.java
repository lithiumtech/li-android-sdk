/*
 * LiClientManager.java
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

package lithium.community.android.sdk.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lithium.community.android.sdk.api.LiBaseDeleteClient;
import lithium.community.android.sdk.api.LiBaseGetClient;
import lithium.community.android.sdk.api.LiBasePostClient;
import lithium.community.android.sdk.api.LiBasePutClient;
import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.helpers.LiAvatar;
import lithium.community.android.sdk.model.helpers.LiBoard;
import lithium.community.android.sdk.model.post.LiAcceptSolutionModel;
import lithium.community.android.sdk.model.post.LiCreateUpdateUserModel;
import lithium.community.android.sdk.model.post.LiGenericPostModel;
import lithium.community.android.sdk.model.post.LiGenericPutModel;
import lithium.community.android.sdk.model.post.LiMarkAbuseModel;
import lithium.community.android.sdk.model.post.LiMarkMessageModel;
import lithium.community.android.sdk.model.post.LiMarkMessagesModel;
import lithium.community.android.sdk.model.post.LiMarkTopicModel;
import lithium.community.android.sdk.model.post.LiPostKudoModel;
import lithium.community.android.sdk.model.post.LiPostMessageModel;
import lithium.community.android.sdk.model.post.LiReplyMessageModel;
import lithium.community.android.sdk.model.post.LiSubscriptionPostModel;
import lithium.community.android.sdk.model.post.LiUploadImageModel;
import lithium.community.android.sdk.model.post.LiUserDeviceDataModel;
import lithium.community.android.sdk.model.post.LiUserDeviceIdUpdateModel;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.model.response.LiAppSdkSettings;
import lithium.community.android.sdk.model.response.LiBrowse;
import lithium.community.android.sdk.model.response.LiFloatedMessageModel;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.model.response.LiSubscriptions;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.queryutil.LiClientConfig;
import lithium.community.android.sdk.queryutil.LiQueryRequestParams;
import lithium.community.android.sdk.queryutil.LiQueryValueReplacer;
import lithium.community.android.sdk.rest.LiRestv2Client;
import lithium.community.android.sdk.utils.LiQueryConstant;

import static lithium.community.android.sdk.utils.LiQueryConstant.LI_INSERT_IMAGE_MACRO;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_LINE_SEPARATOR;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_MARK_MESSAGE_CLIENT_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_MESSAGE_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_POST_QUESTION_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_SUBSCRIPTION_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_USER_DETAILS_CLIENT_TYPE;


/**
 * This class acts as a manager to hold and refresh the user's authentication state. It includes all of the API providers
 * to interact with the community.
 */
public class LiClientManager {

    public static LiRestv2Client getRestClient() throws LiRestResponseException {
        return LiRestv2Client.getInstance();
    }

    /**
     * Fetches a list of all the articles for the user in context ordered by post time or kudos count. Create parameters with {@LiClientRequestParams.LiMessagesClientRequestParams}.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiMessagesClientRequestParams} the Android context (required)
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGES_CLIENT);
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", "0");
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, LiQueryConstant.LI_ARTICLES_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches all articles on the specified board, authored by the user in context. Create parameters with {@LiClientRequestParams.LiMessagesByBoardIdClientRequestParams}.
     *
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiMessagesByBoardIdClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the board from which to fetch the messages (required)
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesByBoardIdClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGES_BY_BOARD_ID_CLIENT);
        String boardId = ((LiClientRequestParams.LiMessagesByBoardIdClientRequestParams) liClientRequestParams).getBoardId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", boardId).replaceAll("&&", "0");
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_BROWSE_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches all the SDK settings from the Community app. Create parameters with {@LiClientRequestParams.LiSdkSettingsClientRequestParams}.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiSdkSettingsClientRequestParams} the Android context (required)
     * @param liClientRequestParams the client ID (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getSdkSettingsClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_SDK_SETTINGS_CLIENT);
        String clientId = ((LiClientRequestParams.LiSdkSettingsClientRequestParams) liClientRequestParams).getClientId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", clientId);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_SDK_SETTINGS_CLIENT_BASE_LIQL, LiQueryConstant.LI_SDK_SETTINGS_CLIENT_TYPE, LiQueryConstant.LI_SDK_SETTINGS_QUERYSETTINGS_TYPE, LiAppSdkSettings.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches a list of all Community subscriptions for the user in context. Create parameters with {@LiClientRequestParams.LiUserSubscriptionsClientRequestParams}.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiUserSubscriptionsClientRequestParams} the Android context (required)
     * @return LiSubscription {@link LiSubscriptions}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUserSubscriptionsClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_USER_SUBSCRIPTIONS_CLIENT);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_BASE_LIQL, LI_SUBSCRIPTIONS_CLIENT_TYPE, LiQueryConstant.LI_SUBSCRIPTTION_QUERYSETTINGS_TYPE, LiSubscriptions.class);
    }

    /**
     * Fetches a list of boards for a given category, along with board and category details. Create parameters with {@link LiClientRequestParams.LiCategoryBoardsClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCategoryBoardsClientRequestParams} the Android context (required)
     * @param liClientRequestParams the category ID (required)
     * @return LiBrowse {@link LiBrowse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getCategoryBoardsClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_CATEGORY_BOARDS_CLIENT);
        String categoryId = ((LiClientRequestParams.LiCategoryBoardsClientRequestParams) liClientRequestParams).getCategoryId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", categoryId);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_BROWSE_QUERYSETTINGS_TYPE, LiBrowse.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches boards at a specific depth in the Community Structure hierarchy. 0 is the highest level in the structure. Create parameters with {@link LiClientRequestParams.LiBoardsByDepthClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiBoardsByDepthClientRequestParams} the Android context (required)
     * @param liClientRequestParams the depth of boards to include in the query (required)
     * @return LiBrowse {@link LiBrowse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getBoardsByDepthClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_BOARDS_BY_DEPTH_CLIENT);
        int depth = ((LiClientRequestParams.LiBoardsByDepthClientRequestParams) liClientRequestParams).getDepth();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(depth));
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_BROWSE_BY_DEPTH_QUERYSETTINGS_TYPE, LiBrowse.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details of child messages of the specified message. Child messages include replies comments, as well as nested replies or comments. Create parameters with {@link LiClientRequestParams.LiRepliesClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiRepliesClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the parent message (required)
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @NonNull
    public static LiClient getRepliesClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_REPLIES_CLIENT);
        Long parentId = ((LiClientRequestParams.LiRepliesClientRequestParams) liClientRequestParams).getParentId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(parentId));
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_CONVERSATION_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Performs a keyword search in the community. Create parameters with {@link LiClientRequestParams.LiSearchClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiSearchClientRequestParams} the Android context (required)
     * @param liClientRequestParams the text string to search, passed as 'query'. The 'query' parameter is compared with the body and subject of the message. (required)
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getSearchClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_SEARCH_CLIENT);
        String query = ((LiClientRequestParams.LiSearchClientRequestParams) liClientRequestParams).getQuery();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", query);
        return new LiBaseGetClient(liClientRequestParams.getContext(), String.format(LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, query), LiQueryConstant.LI_SEARCH_CLIENT_TYPE, LiQueryConstant.LI_SEARCH_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches messages posted by a specified author up to a specified depth in the Community Structure hierarchy. Crate parameters with {@link LiClientRequestParams.LiUserMessagesClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiUserMessagesClientRequestParams}  the Android context (required)
     * @param liClientRequestParams the ID of the author (required)
     * @param liClientRequestParams the depth of the message, where the a topic message = 0, the first reply = 1, and so on (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUserMessagesClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_USER_MESSAGES_CLIENT);
        Long authorId = ((LiClientRequestParams.LiUserMessagesClientRequestParams) liClientRequestParams).getAuthorId();
        String depth = ((LiClientRequestParams.LiUserMessagesClientRequestParams) liClientRequestParams).getDepth();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(authorId)).replaceAll("&&", depth);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, LiQueryConstant.LI_QUESTIONS_CLIENT_TYPE, LiQueryConstant.LI_QUESTIONS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches category details available to the user in context. This method respects the Hide from Lists and Menus setting in Community Admin.
     * Create parameters with {@link LiClientRequestParams.LiCategoryClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCategoryClientRequestParams} the Android context (required)
     * @return LiBrowse {@link LiBrowse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getCategoryClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_CATEGORY_CLIENT);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_CATEGORY_CLIENT_BASE_LIQL, LiQueryConstant.LI_CATEGORY_CLIENT_TYPE, LiQueryConstant.LI_CATEGORY_QUERYSETTINGS_TYPE, LiBrowse.class);
    }

    /**
     * Fetches all details about the specified user. Create parameters with {@link LiClientRequestParams.LiUserDetailsClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiUserDetailsClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the user (required)
     * @return LUser {@link LiUser}
     * @throws LiRestResponseException LiClient {@link LiRestResponseException}
     */
    public static LiClient getUserDetailsClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_USER_DETAILS_CLIENT);
        String userId = ((LiClientRequestParams.LiUserDetailsClientRequestParams) liClientRequestParams).getUserId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", userId);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_USER_DETAILS_CLIENT_BASE_LIQL, LI_USER_DETAILS_CLIENT_TYPE, LiQueryConstant.LI_USER_DETAILS_QUERYSETTINGS_TYPE, LiUser.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details of the specified message. Create parameters with {@link LiClientRequestParams.LiMessageClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMessageClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the message (required)
     * @return LiMessage {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessageClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGE_CLIENT);
        Long messageId = ((LiClientRequestParams.LiMessageClientRequestParams) liClientRequestParams).getMessageId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(messageId));
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_CONVERSATION_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details of all floated (or "pinned") of the specified board for the user in context.
     * Create parameters with {@LiClientRequestParams.LiFloatedMessagesClientRequestParams}.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiFloatedMessagesClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the board (required)
     * @param liClientRequestParams the scope for searching the floated messages. Supported scopes: "global", "local" (required)
     * @return LiFloatedMessageModel {@link LiFloatedMessageModel}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getFloatedMessagesClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_FLOATED_MESSAGES_CLIENT);
        String boardId = ((LiClientRequestParams.LiFloatedMessagesClientRequestParams) liClientRequestParams).getBoardId();
        String scope = ((LiClientRequestParams.LiFloatedMessagesClientRequestParams) liClientRequestParams).getScope();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(boardId)).replaceAll("&&", scope);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_BASE_LIQL, LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_FLOATED_MESSAGE_QUERYSETTINGS_TYPE, LiFloatedMessageModel.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Fetches details for the specified set of messages. Create parameters with {@LiClientRequestParams.LiMessagesByIdsClientRequestParams}.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiMessagesByIdsClientRequestParams} the Android context (required)
     * @param liClientRequestParams the IDs of the messages, passed as a set (required)
     * @return LiMessages {@link LiMessage}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesByIdsClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGES_BY_ID_CLIENT);
        Set<String> messageIds = ((LiClientRequestParams.LiMessagesByIdsClientRequestParams) liClientRequestParams).getMessageIds();
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

        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_LIST_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_BY_IDS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * Kudos the specified message for the user in context.
     * Create parameters with {@link LiClientRequestParams.LiKudoClientRequestParams}.
     * Uses {@link LiPostKudoModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiKudoClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the message to kudo (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getKudoClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_KUDO_CLIENT);
        String messageId = ((LiClientRequestParams.LiKudoClientRequestParams) liClientRequestParams).getMessageId();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/messages/%s/kudos", LiSDKManager.getInstance().getTenant(), messageId));
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
     * Unkudos the specified message for the user in context.
     * Create parameters with {@link LiClientRequestParams.LiUnKudoClientRequestParams}.
     * Uses {@link LiPostKudoModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiUnKudoClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the message to unkudoed (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUnKudoClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_UNKUDO_CLIENT);
        String messageId = ((LiClientRequestParams.LiUnKudoClientRequestParams) liClientRequestParams).getMessageId();
        LiClientRequestParams.LiGenericDeleteClientRequestParams liGenericDeleteClientRequestParams = new LiClientRequestParams.LiGenericDeleteClientRequestParams(liClientRequestParams.getContext(), CollectionsType.MESSAGE, messageId, "/kudos");
        LiBaseDeleteClient liBaseDeleteClient = (LiBaseDeleteClient) getGenericQueryDeleteClient(liGenericDeleteClientRequestParams);
        return liBaseDeleteClient;
    }

    /**
     * Marks a message as an accepted solution.
     * Create parameters with {@link LiClientRequestParams.LiAcceptSolutionClientRequestParams}.
     * Uses {@link LiAcceptSolutionModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiAcceptSolutionClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of the message to mark as a solution (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getAcceptSolutionClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_ACCEPT_SOLUTION_CLIENT);
        Long messageId = ((LiClientRequestParams.LiAcceptSolutionClientRequestParams) liClientRequestParams).getMessageId();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/solutions_data", LiSDKManager.getInstance().getTenant()));
        LiAcceptSolutionModel liAcceptSolutionModel = new LiAcceptSolutionModel();
        liAcceptSolutionModel.setType(LiQueryConstant.LI_ACCEPT_SOLUTION_TYPE);
        liAcceptSolutionModel.setMessageid(String.valueOf(messageId));
        liBasePostClient.postModel = liAcceptSolutionModel;
        return liBasePostClient;
    }

    /**
     * Posts a new message to the community.
     * Create parameters with {@link LiClientRequestParams.LiCreateMessageClientRequestParams}.
     * Uses {@link LiPostMessageModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCreateMessageClientRequestParams} the Android context (required)
     * @param liClientRequestParams the subject of the message (required)
     * @param liClientRequestParams the body of the message (required)
     * @param liClientRequestParams the image filename (if an image is included in the message)
     * @param liClientRequestParams the image ID (if an image is included in the message)
     * @param liClientRequestParams the ID of the board where the message will be posted (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getCreateMessageClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_CREATE_MESSAGE_CLIENT);
        String subject = ((LiClientRequestParams.LiCreateMessageClientRequestParams) liClientRequestParams).getSubject();
        String body = ((LiClientRequestParams.LiCreateMessageClientRequestParams) liClientRequestParams).getBody();
        String boardId = ((LiClientRequestParams.LiCreateMessageClientRequestParams) liClientRequestParams).getBoardId();
        String imageId = ((LiClientRequestParams.LiCreateMessageClientRequestParams) liClientRequestParams).getImageId();
        String imageName = ((LiClientRequestParams.LiCreateMessageClientRequestParams) liClientRequestParams).getImageName();

        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/messages", LiSDKManager.getInstance().getTenant()));
        LiPostMessageModel liPostMessageModel = new LiPostMessageModel();
        LiBoard liBoard = new LiBoard();
        LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
        liString.setValue(boardId);
        liBoard.setId(liString);
        liPostMessageModel.setType(LiQueryConstant.LI_POST_QUESTION_TYPE);
        liPostMessageModel.setSubject(subject);
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
     * @param liClientRequestParams {@link LiClientRequestParams.LiUpdateMessageClientRequestParams} the Android context (required)
     * @param liClientRequestParams the message subject
     * @param liClientRequestParams the message body
     * @param liClientRequestParams the message ID (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUpdateMessageClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_UPDATE_MESSAGE_CLIENT);
        String subject = ((LiClientRequestParams.LiUpdateMessageClientRequestParams) liClientRequestParams).getSubject();
        String body = ((LiClientRequestParams.LiUpdateMessageClientRequestParams) liClientRequestParams).getBody();
        String messageId = ((LiClientRequestParams.LiUpdateMessageClientRequestParams) liClientRequestParams).getMessageId();

        LiBasePutClient liBasePutClient = new LiBasePutClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/messages/%s", LiSDKManager.getInstance().getTenant(), messageId));
        LiPostMessageModel liPostMessageModel = new LiPostMessageModel();
        liPostMessageModel.setType(LI_POST_QUESTION_TYPE);
        liPostMessageModel.setBody(body);
        liPostMessageModel.setSubject(subject);
        liBasePutClient.postModel = liPostMessageModel;
        return liBasePutClient;
    }
    /**
     * Embeds an image tag into the message body.
     * <p><li-image id=IMAGEID</> width="500" height="500" alt=IAMGEID.png align="inline" size="large" sourcetype="new"></li-image></p>
     * //This is to insert image id in body if any to effectively display image along with post.
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
            body = body + LI_LINE_SEPARATOR + String.format(LI_INSERT_IMAGE_MACRO, imageId, imageName);
        }
        return body;
    }

    /**
     * Creates a reply or comment.
     * Create parameters with {@link LiClientRequestParams.LiCreateReplyClientRequestParams}.
     * Uses {@link LiReplyMessageModel} to build request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCreateReplyClientRequestParams} the Android context (required)
     * @param liClientRequestParams the message ID (required)
     * @param liClientRequestParams the message body (required)
     * @param liClientRequestParams the image filename (if the message includes an image)
     * @param liClientRequestParams the image ID (if the message includes an image)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException LiClient {@link LiRestResponseException}
     */
    public static LiClient getCreateReplyClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_CREATE_REPLY_CLIENT);
        String subject = ((LiClientRequestParams.LiCreateReplyClientRequestParams) liClientRequestParams).getSubject();
        String body = ((LiClientRequestParams.LiCreateReplyClientRequestParams) liClientRequestParams).getBody();
        Long messageId = ((LiClientRequestParams.LiCreateReplyClientRequestParams) liClientRequestParams).getMessageId();
        String imageId = ((LiClientRequestParams.LiCreateReplyClientRequestParams) liClientRequestParams).getImageId();
        String imageName = ((LiClientRequestParams.LiCreateReplyClientRequestParams) liClientRequestParams).getImageName();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(),
                String.format("/community/2.0/%s/messages",
                        LiSDKManager.getInstance().getTenant()));
        LiReplyMessageModel liReplyMessage = new LiReplyMessageModel();
        LiMessage parent = new LiMessage();
        LiBaseModelImpl.LiInt liId = new LiBaseModelImpl.LiInt();
        liId.setValue(Long.valueOf(messageId));
        parent.setId(liId);
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
     * @param liClientRequestParams {@link LiClientRequestParams.LiUploadImageClientRequestParams} the Android context (required)
     * @param liClientRequestParams the image title (required)
     * @param liClientRequestParams the image filename (required)
     * @param liClientRequestParams the absolute path to the image (required)
     * Note: the image filename and the filename in the absolute path param above must be equal.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUploadImageClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_UPLOAD_IMAGE_CLIENT);
        String title = ((LiClientRequestParams.LiUploadImageClientRequestParams) liClientRequestParams).getTitle();
        String description = ((LiClientRequestParams.LiUploadImageClientRequestParams) liClientRequestParams).getDescription();
        String imageName = ((LiClientRequestParams.LiUploadImageClientRequestParams) liClientRequestParams).getImageName();
        String path = ((LiClientRequestParams.LiUploadImageClientRequestParams) liClientRequestParams).getPath();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(),
                String.format("/community/2.0/%s/images",
                        LiSDKManager.getInstance().getTenant()), path, imageName);
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
     * Creates an abuse report for the specified message.
     * Create parameters with {@link LiClientRequestParams.LiReportAbuseClientRequestParams}.
     * Uses the {@link LiMarkAbuseModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * Added 1.0.2
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiReportAbuseClientRequestParams} the Android context (required)
     * @param liClientRequestParams the ID of message to report (required)
     * @param liClientRequestParams the message body of the message being reported (required)
     * @param liClientRequestParams the ID of the user making the abuse report (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getReportAbuseClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MARK_ABUSE_CLIENT);
        String messageId = ((LiClientRequestParams.LiReportAbuseClientRequestParams) liClientRequestParams).getMessageId();
        String userId = ((LiClientRequestParams.LiReportAbuseClientRequestParams) liClientRequestParams).getUserId();
        String body = ((LiClientRequestParams.LiReportAbuseClientRequestParams) liClientRequestParams).getBody();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/abuse_reports", LiSDKManager.getInstance().getTenant()));
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
     * Fetches the ID corresponding to device ID from the community.
     * Create parameters with {@link LiClientRequestParams.LiDeviceIdFetchClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiDeviceIdFetchClientRequestParams} the ID registered with notification provider
     * @param liClientRequestParams the Global provider for Push notification. Currently "GCM" and "FIREBASE".
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getDeviceIdFetchClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_DEVICE_ID_FETCH_CLIENT);
        String deviceId = ((LiClientRequestParams.LiDeviceIdFetchClientRequestParams) liClientRequestParams).getDeviceId();
        String pushNotificationProvider = ((LiClientRequestParams.LiDeviceIdFetchClientRequestParams) liClientRequestParams).getPushNotificationProvider();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(),
                String.format("/community/2.0/%s/user_device_data",
                        LiSDKManager.getInstance().getTenant()));
        LiUserDeviceDataModel liUserDeviceDataModel = new LiUserDeviceDataModel();
        liUserDeviceDataModel.setType(LiQueryConstant.LI_USER_DEVICE_ID_FETCH_TYPE);
        liUserDeviceDataModel.setDeviceId(deviceId);
        liUserDeviceDataModel.setClientId(LiSDKManager.getInstance().getLiAppCredentials().getClientKey());
        liUserDeviceDataModel.setApplicationType(LiQueryConstant.LI_APPLICATION_TYPE);
        liUserDeviceDataModel.setPushNotificationProvider(pushNotificationProvider);
        liBasePostClient.postModel = liUserDeviceDataModel;
        return liBasePostClient;
    }

    /**
     * Updates the device ID in community with the given device ID. Create parameters with {@link LiClientRequestParams.LiDeviceIdUpdateClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiDeviceIdUpdateClientRequestParams} the Android context (required)
     * @param liClientRequestParams the device ID registered with the push notification provider (required)
     * @param liClientRequestParams the ID corresponding to device ID in the community (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getDeviceIdUpdateClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_DEVICE_ID_UPDATE_CLIENT);
        String deviceId = ((LiClientRequestParams.LiDeviceIdUpdateClientRequestParams) liClientRequestParams).getDeviceId();
        String id = ((LiClientRequestParams.LiDeviceIdUpdateClientRequestParams) liClientRequestParams).getDeviceId();
        String path = "/community/2.0/%s/user_device_data/" + id;
        LiBasePutClient liBasePostClient = new LiBasePutClient(liClientRequestParams.getContext(),
                String.format(path,
                        LiSDKManager.getInstance().getTenant()));
        LiUserDeviceIdUpdateModel liUserDeviceIdUpdateModel = new LiUserDeviceIdUpdateModel();
        liUserDeviceIdUpdateModel.setType(LiQueryConstant.LI_USER_DEVICE_ID_FETCH_TYPE);
        liUserDeviceIdUpdateModel.setDeviceId(deviceId);
        liBasePostClient.postModel = liUserDeviceIdUpdateModel;
        return liBasePostClient;
    }

    /**
     * Creates new user account.
     * Create parameters with {@link LiClientRequestParams.LiCreateUserParams}.
     * Uses {@link LiCreateUpdateUserModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * Added 1.1.0
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCreateUserParams} the Android context (required)
     * @param liClientRequestParams the user's login (required)
     * @param liClientRequestParams the user's password (required)
     * @param liClientRequestParams the user's email (required)
     * @param liClientRequestParams the user's avatar
     * @param liClientRequestParams the user's biography
     * @param liClientRequestParams the user's first name
     * @param liClientRequestParams the user's last name
     *
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getCreateUserClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_CREATE_USER_CLIENT);

        LiAvatar avatar = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getAvatar();
        String biography = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getBiography();
        String coverImage = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getCoverImage();
        String email = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getEmail();
        String firstName = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getFirstName();
        String lastName = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getLastName();
        String login = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getLogin();
        String password = ((LiClientRequestParams.LiCreateUserParams) liClientRequestParams).getPassword();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/users", LiSDKManager.getInstance().getTenant()));
        LiCreateUpdateUserModel liCreateUpdateUserModel = new LiCreateUpdateUserModel();
        liCreateUpdateUserModel.setType(LI_USER_DETAILS_CLIENT_TYPE);
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
     * Mark a single message as read or unread
     * Create parameters with {@link LiClientRequestParams.LiMarkMessageParams}.
     * Uses the {@link LiMarkMessageModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * Added 1.1.0
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMarkMessageParams} the Android context (required)
     * @param liClientRequestParams the ID of the user marking the message (required)
     * @param liClientRequestParams the ID of the message (required)
     * @param liClientRequestParams whether to mark the message read or unread. Pass 'markUnread' as 'true' to mark the message as read. (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getMarkMessagePostClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MARK_MESSAGE_POST_CLIENT);
        String userId = ((LiClientRequestParams.LiMarkMessageParams) liClientRequestParams).getUserId();
        String messageId = ((LiClientRequestParams.LiMarkMessageParams) liClientRequestParams).getMessageId();
        boolean markUnread = ((LiClientRequestParams.LiMarkMessageParams) liClientRequestParams).isMarkUnread();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/messages_read", LiSDKManager.getInstance().getTenant()));
        LiMarkMessageModel liMarkMessageModel = new LiMarkMessageModel();
        liMarkMessageModel.setType(LI_MARK_MESSAGE_CLIENT_TYPE);
        liMarkMessageModel.setUser(userId);
        liMarkMessageModel.setMessageId(messageId);
        liMarkMessageModel.setMarkUnread(markUnread);
        liBasePostClient.postModel = liMarkMessageModel;
        return liBasePostClient;
    }

    /**
     * Marks a set of messages (not necessarily in the same thread) as read or unread. Compare this to getMarkTopicPostClient.
     * Create parameters with {@link lithium.community.android.sdk.model.request.LiClientRequestParams.LiMarkMessagesParams}.
     * Uses the {@link LiMarkMessagesModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * Added 1.1.0
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMarkMessagesParams} the Android context
     * @param liClientRequestParams the user marking the messages (required)
     * @param liClientRequestParams the IDs of the messages to mark in a comma-separated list (required)
     * @param liClientRequestParams whether to mark the messages read or unread. Pass 'markUnread' as 'true' to mark the messages as read. (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getMarkMessagesPostClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MARK_MESSAGES_POST_CLIENT);
        String userId = ((LiClientRequestParams.LiMarkMessagesParams) liClientRequestParams).getUserId();
        String messageIds = ((LiClientRequestParams.LiMarkMessagesParams) liClientRequestParams).getMessageIds();
        boolean markUnread = ((LiClientRequestParams.LiMarkMessagesParams) liClientRequestParams).isMarkUnread();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/messages_read", LiSDKManager.getInstance().getTenant()));
        LiMarkMessagesModel liMarkMessagesModel = new LiMarkMessagesModel();
        liMarkMessagesModel.setType(LI_MARK_MESSAGE_CLIENT_TYPE);
        liMarkMessagesModel.setUser(userId);
        liMarkMessagesModel.setMessageIds(messageIds);
        liMarkMessagesModel.setMarkUnread(markUnread);
        liBasePostClient.postModel = liMarkMessagesModel;
        return liBasePostClient;
    }

    /**
     * Marks a topic message and all its replies as read or unread. Compare this to getMarkMessagesPostClient.
     * Create parameters with {@link LiClientRequestParams.LiMarkTopicParams}.
     * Uses the {@link LiMarkTopicModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * Added 1.1.0
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMarkTopicParams} the Android context (required)
     * @param liClientRequestParams the ID of the topic message to mark (required)
     * @param liClientRequestParams whether to mark the messages read or unread. Pass 'markUnread' as 'true' to mark the messages as read. (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getMarkTopicPostClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MARK_TOPIC_POST_CLIENT);
        String userId = ((LiClientRequestParams.LiMarkTopicParams) liClientRequestParams).getUserId();
        String topicId = ((LiClientRequestParams.LiMarkTopicParams) liClientRequestParams).getTopicId();
        boolean markUnread = ((LiClientRequestParams.LiMarkTopicParams) liClientRequestParams).isMarkUnread();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/messages_read", LiSDKManager.getInstance().getTenant()));
        LiMarkTopicModel liMarkTopicModel = new LiMarkTopicModel();
        liMarkTopicModel.setType(LI_MARK_MESSAGE_CLIENT_TYPE);
        liMarkTopicModel.setUser(userId);
        liMarkTopicModel.setTopicId(topicId);
        liMarkTopicModel.setMarkUnread(markUnread);
        liBasePostClient.postModel = liMarkTopicModel;
        return liBasePostClient;
    }


    /**
     * Creates a subscription to the specified target (a board or message).
     * Create parameters with {@link LiClientRequestParams.LiPostSubscriptionParams}.
     *
     * The parameters can either be a target which is a {@link LiBaseModel} in the form of {@link LiMessage} or {@link LiBrowse}
     * which was selected to be subscribed to and needs to be passed in the constructor
     *
     * Alternatively in the parameters "message/boardId" and "type" need to be passed in the constructor.
     * "type" is either "message" or "board" depending upon what was selected.
     *
     * If both are set then the target takes precedence and string parameters are ignored.
     *
     * Uses the {@link LiSubscriptionPostModel} to build the request body.
     * The model is converted to a JsonObject, which is then used in the POST call.
     *
     * Added 1.0.1
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiPostSubscriptionParams} the Android context (required)
     * @param liClientRequestParams the target of the subscription -- a board or message (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getSubscriptionPostClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_SUBSCRIPTION_POST_CLIENT);
        LiClientRequestParams.LiPostSubscriptionParams liPostSubscriptionParams =
                ((LiClientRequestParams.LiPostSubscriptionParams)liClientRequestParams);
        LiBaseModel liBaseModel = liPostSubscriptionParams.getTarget();
        LiSubscriptionPostModel.Target target = new LiSubscriptionPostModel.Target();
        String targetID = null;
        String targetType = null;
        if (liBaseModel != null) {
            if (liBaseModel.getModel() instanceof LiMessage) {
                LiMessage message = (LiMessage) liBaseModel.getModel();
                targetType = "message";
                targetID = String.valueOf(message.getId());

            } else if (liBaseModel.getModel() instanceof LiBrowse) {
                LiBrowse board = (LiBrowse) liBaseModel.getModel();
                targetType = "board";
                targetID = extractBoardId(board.getId());
            }
        }
        else {
            targetType = liPostSubscriptionParams.getTargetType();
            targetID = liPostSubscriptionParams.getTargetId();
        }
        target.setType(targetType);
        target.setId(targetID);
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/subscriptions", LiSDKManager.getInstance().getTenant()));
        LiSubscriptionPostModel liSubscriptionPostModel = new LiSubscriptionPostModel();
        liSubscriptionPostModel.setType(LI_SUBSCRIPTIONS_CLIENT_TYPE);
        liSubscriptionPostModel.setTarget(target);
        liBasePostClient.postModel = liSubscriptionPostModel;
        return liBasePostClient;
    }

    private static String extractBoardId(String boardId) {
        String finalBoardId = boardId;
        String boardPrefix = "board:";
        if (finalBoardId.contains(boardPrefix)) {
            finalBoardId = finalBoardId.substring(
                    finalBoardId.indexOf(boardPrefix) + boardPrefix.length(),
                    finalBoardId.length());
        }

        return finalBoardId;
    }
    /**
     * Deletes a subscription.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiDeleteSubscriptionParams} the Android context
     * @param liClientRequestParams the subscription ID
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getSubscriptionDeleteClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_SUBSCRIPTION_DELETE_CLIENT);
        String id = ((LiClientRequestParams.LiDeleteSubscriptionParams)liClientRequestParams).getSubscriptionId();
        LiClientRequestParams.LiGenericDeleteClientRequestParams liGenericDeleteClientRequestParams = new LiClientRequestParams.LiGenericDeleteClientRequestParams(liClientRequestParams.getContext(), CollectionsType.SUBSCRIPTION, id);
        LiBaseDeleteClient liBaseDeleteClient = (LiBaseDeleteClient) getGenericQueryDeleteClient(liGenericDeleteClientRequestParams);
        return liBaseDeleteClient;
    }

    /**
     * Updates an existing user. Create parameters with {@link LiClientRequestParams.LiUpdateUserParams}.
     * Uses {@link LiCreateUpdateUserModel} to build the request body. The model is converted to a JsonObject, which is then used in the POST call.
     *
     * Added 1.1.0
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiUpdateUserParams} the Android context (required)
     * @param liClientRequestParams the user's avatar image url
     * @param liClientRequestParams the user's avatar image external url
     * @param liClientRequestParams the user's avatar image internal url
     * @param liClientRequestParams the user's avatar image id
     * @param liClientRequestParams the user's biography
     * @param liClientRequestParams the user's cover image
     * @param liClientRequestParams the user's email
     * @param liClientRequestParams the user's first name
     * @param liClientRequestParams the user's last name
     * @param liClientRequestParams the user's login
     *
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getUpdateUserClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_UPDATE_USER_CLIENT);
        LiClientRequestParams.LiUpdateUserParams liUpdateUserParams = (LiClientRequestParams.LiUpdateUserParams) liClientRequestParams;

        LiAvatar avatar = new LiAvatar();
        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarUrl())) {
            avatar.setUrl(liUpdateUserParams.getAvatarUrl());
        }
        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarExternalUrl())) {
            avatar.setExternal(liUpdateUserParams.getAvatarExternalUrl());
        }
        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarInternalUrl())) {
            avatar.setInternal(liUpdateUserParams.getAvatarInternalUrl());
        }
        if (!TextUtils.isEmpty(liUpdateUserParams.getAvatarImageId())) {
            avatar.setImage(liUpdateUserParams.getAvatarImageId());
        }

        String biography = liUpdateUserParams.getBiography();
        String coverImage = liUpdateUserParams.getCoverImage();
        String email = liUpdateUserParams.getEmail();
        String firstName = liUpdateUserParams.getFirstName();
        String lastName = liUpdateUserParams.getLastName();
        String login = liUpdateUserParams.getLogin();
        String id = liUpdateUserParams.getId();
        LiBasePutClient liBasePutClient = new LiBasePutClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/users/%s", LiSDKManager.getInstance().getTenant(), id));
        LiCreateUpdateUserModel liCreateUpdateUserModel = new LiCreateUpdateUserModel();
        liCreateUpdateUserModel.setType(LI_USER_DETAILS_CLIENT_TYPE);
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
     * This is generic Post client. Provide a path to a Community API v1 or v2 endpoint and a request body.
     * Create parameters with {@link LiClientRequestParams.LiGenericPostClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericPostClientRequestParams} the Android context
     * @param liClientRequestParams the path to a Community v1 or v2 endpoint
     * @param liClientRequestParams the request body as a {@link JsonObject}
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericPostClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_POST_CLIENT);
        String path = ((LiClientRequestParams.LiGenericPostClientRequestParams) liClientRequestParams).getPath();
        JsonElement requestBody = ((LiClientRequestParams.LiGenericPostClientRequestParams) liClientRequestParams).getRequestBody();
        String requestPath = "/community/2.0/%s/" + path;
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(),
                String.format(requestPath,
                        LiSDKManager.getInstance().getTenant()));

        LiGenericPostModel genericPostModel = new LiGenericPostModel();
        genericPostModel.setData(requestBody);
        liBasePostClient.postModel = genericPostModel;
        return liBasePostClient;
    }

    /**
     * This is a beacon client which is used to send information to the community backend for analytics aka LSI
     * @param liClientRequestParams {@link LiClientRequestParams.LiBeaconPostClientRequestParams}
     */
    public static LiClient getBeaconClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_BEACON_CLIENT);
        LiClientRequestParams.LiBeaconPostClientRequestParams liBeaconPostClientRequestParams =
                ((LiClientRequestParams.LiBeaconPostClientRequestParams) liClientRequestParams);
        String targetType = liBeaconPostClientRequestParams.getTargetType();
        String targetId = liBeaconPostClientRequestParams.getTargetId();
        Context context = liBeaconPostClientRequestParams.getContext();
        JsonObject requestBody = new JsonObject();
        if (!TextUtils.isEmpty(targetId) && !TextUtils.isEmpty(targetType)) {
            JsonObject targetJsonObject = new JsonObject();
            targetJsonObject.addProperty("type", targetType);
            targetJsonObject.addProperty("id", targetId);
            requestBody.add("target", targetJsonObject);
        }

        LiClientRequestParams params = new LiClientRequestParams.LiGenericPostClientRequestParams(context, "beacon", requestBody);
        return LiClientManager.getGenericPostClient(params);
    }

    /**
     * This is generic PUT client. Provide the path to a Community API v1 or v2 endpoint and a request body.
     * Create parameters with {@link LiClientRequestParams.LiGenericPutClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericPutClientRequestParams} the Android context (required)
     * @param liClientRequestParams the path to a Community v1 or v2 endpoint (required)
     * @param liClientRequestParams the request body as a {@link JsonObject} (required)
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericPutClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_PUT_CLIENT);
        String path = ((LiClientRequestParams.LiGenericPutClientRequestParams) liClientRequestParams).getPath();
        JsonObject requestBody = ((LiClientRequestParams.LiGenericPutClientRequestParams) liClientRequestParams).getRequestBody();
        String requestPath = "/community/2.0/%s/" + path;
        LiBasePutClient liBasePutClient = new LiBasePutClient(liClientRequestParams.getContext(),
                String.format(requestPath,
                        LiSDKManager.getInstance().getTenant()));
        LiGenericPutModel genericPutModel = new LiGenericPutModel();
        genericPutModel.setData(requestBody);
        liBasePutClient.postModel = genericPutModel;
        return liBasePutClient;
    }

    /**
     * This is generic Get client. Provide a LiQL query. Create parameters with {@link LiClientRequestParams.LiGenericLiqlClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericLiqlClientRequestParams} the Android context (required)
     * @param liClientRequestParams a custom LiQL query (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericLiqlGetClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_LIQL_CLIENT);
        String liQuery = ((LiClientRequestParams.LiGenericLiqlClientRequestParams) liClientRequestParams).getLiQuery();
        return new LiBaseGetClient(liClientRequestParams.getContext(), liQuery, null, LiQueryConstant.LI_GENERIC_TYPE, null);
    }

    /**
     * This is generic Get client. Provide a LiQL query. Create parameters with {@link LiClientRequestParams.LiGenericLiqlClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericLiqlClientRequestParams} the Android context (required)
     * @param liClientRequestParams a custom LiQL query (required)
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericNoLiqlGetClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_LIQL_CLIENT);
        String pathParam = ((LiClientRequestParams.LiGenericNoLiqlClientRequestParams) liClientRequestParams).getPathParam();
        String queryParams = ((LiClientRequestParams.LiGenericNoLiqlClientRequestParams) liClientRequestParams).getQueryParams();
        return new LiBaseGetClient(liClientRequestParams.getContext(), queryParams, null, LiQueryConstant.LI_GENERIC_TYPE, null, pathParam);
    }


    /**
     * Creates custom WHERE clause, ORDER BY, and/or LIMIT parameters to a LIQL queries used by a Lithium API provider.
     * Create parameters with {@link LiClientRequestParams.LiGenericQueryParamsClientRequestParams}.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericQueryParamsClientRequestParams} the Android context
     *
     * @return LiClient {@link LiClient}
     */
    public static LiClient getGenericQueryParamsGetClient(LiClientRequestParams liClientRequestParams) {
        liClientRequestParams.validate(Client.LI_GENERIC_QUERY_PARAMS_CLIENT);
        LiQueryRequestParams liQueryRequestParams = ((LiClientRequestParams.LiGenericQueryParamsClientRequestParams) liClientRequestParams).getLiQueryRequestParams();
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiClientConfig.getBaseQuery(liQueryRequestParams.getClient()), LiClientConfig.getType(liQueryRequestParams.getClient()), LiClientConfig.getResponseClass(liQueryRequestParams.getClient()), liQueryRequestParams);
    }

    /**
     * This is a generic DELETE client.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericDeleteClientRequestParams}
     * @return LiClient {@link LiClient}
     */
    public static LiClient getGenericQueryDeleteClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_DELETE_QUERY_PARAMS_CLIENT);
        LiClientRequestParams.LiGenericDeleteClientRequestParams clientRequestParams = (LiClientRequestParams.LiGenericDeleteClientRequestParams) liClientRequestParams;
        Map<String, String> queryRequestParams = clientRequestParams.getLiQueryRequestParams();
        String id = clientRequestParams.getId();
        String extraPathAfterId = clientRequestParams.getSubResourcePath();
        CollectionsType collectionsType = clientRequestParams.getCollectionsType();
        StringBuilder path = new StringBuilder();
        path = path.append(String.format("/community/2.0/%s/%s/%s", LiSDKManager.getInstance().getTenant(), collectionsType.getValue(), id));
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
        return new LiBaseDeleteClient(liClientRequestParams.getContext(), path.toString());
    }

    /**
     * Deletes the specified message. Create parameters with {@link lithium.community.android.sdk.model.request.LiClientRequestParams.LiMessageDeleteClientRequestParams}.
     * Optionally pass the 'includeReplies' parameters as 'true' to delete replies or comments associated with the message.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMessageDeleteClientRequestParams} the Android context
     * @param liClientRequestParams the ID of the message to delete
     * @param liClientRequestParams whether or not to delete replies/comments to the message
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessageDeleteClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGE_DELETE_CLIENT);
        String messageId = ((LiClientRequestParams.LiMessageDeleteClientRequestParams) liClientRequestParams).getMessageId();
        boolean includeReplies = ((LiClientRequestParams.LiMessageDeleteClientRequestParams) liClientRequestParams).isIncludeReplies();
        LiClientRequestParams.LiGenericDeleteClientRequestParams liGenericDeleteClientRequestParams;
        if (includeReplies) {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("delete_message.include_replies", "true");
            liGenericDeleteClientRequestParams = new LiClientRequestParams.LiGenericDeleteClientRequestParams(liClientRequestParams.getContext(), CollectionsType.MESSAGE, messageId, requestParams);
        } else {
            liGenericDeleteClientRequestParams = new LiClientRequestParams.LiGenericDeleteClientRequestParams(liClientRequestParams.getContext(), CollectionsType.MESSAGE, messageId);
        }
        LiBaseDeleteClient liBaseDeleteClient = (LiBaseDeleteClient) getGenericQueryDeleteClient(liGenericDeleteClientRequestParams);
        return liBaseDeleteClient;
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
        LI_UPDATE_MESSAGE_CLIENT
    }
    /**
     * Enum of all collection types.
     */
    public enum CollectionsType {
        MESSAGE(LI_MESSAGE_TYPE),
        SUBSCRIPTION(LI_SUBSCRIPTION_TYPE);

        private final String value;

        CollectionsType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
