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

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

import lithium.community.android.sdk.api.LiBaseDeleteClient;
import lithium.community.android.sdk.api.LiBaseGetClient;
import lithium.community.android.sdk.api.LiBasePostClient;
import lithium.community.android.sdk.api.LiBasePutClient;
import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.helpers.LiAvatar;
import lithium.community.android.sdk.model.helpers.LiBoard;
import lithium.community.android.sdk.model.post.LiAcceptSolutionModel;
import lithium.community.android.sdk.model.post.LiCreateUpdateUserModel;
import lithium.community.android.sdk.model.post.LiGenericPostModel;
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
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_SUBSCRIPTION_TYPE;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_USER_DETAILS_CLIENT_TYPE;


/**
 * This class acts as a manager to hold and refresh the user's auth state. It has all the API providers
 * to interact with the community.
 */
public class LiClientManager {

    public static LiRestv2Client getRestClient() throws LiRestResponseException {
        return LiRestv2Client.getInstance();
    }

    /**
     * This client fetches list of all the articles for that user, it is a basic search which gets top articles (configurable limit) ordered by PostTime or Kudos count.
     * The articles are visible on App home page
     *
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGES_CLIENT);
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", "0");
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_ARTICLES_CLIENT_BASE_LIQL, LiQueryConstant.LI_ARTICLES_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches all articles corresponding to a particular board id for a user.
     * It takes 'boardId' as parameter.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiMessagesClientRequestParams} contains Id of board for which messags are to be fetched.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessagesByBoardIdClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGES_BY_BOARD_ID_CLIENT);
        String boardId = ((LiClientRequestParams.LiMessagesByBoardIdClientRequestParams) liClientRequestParams).getBoardId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", boardId).replaceAll("&&", "0");
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_BROWSE_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches all the settings of the SDK from LIA for that user.
     * It takes 'clientId' as parameter as setting details are governed by 'clientId'.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiSdkSettingsClientRequestParams} contains client id.
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
     * This client fetches list of all subscriptions corresponding to a particular user.
     *
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUserSubscriptionsClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_USER_SUBSCRIPTIONS_CLIENT);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_BASE_LIQL, LI_SUBSCRIPTIONS_CLIENT_TYPE, LiQueryConstant.LI_SUBSCRIPTTION_QUERYSETTINGS_TYPE, LiSubscriptions.class);
    }

    /**
     * This client fetches list of boards for a given category. It takes 'categoryId' as a parameter.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCategoryBoardsClientRequestParams} contains category Id.
     * @return LiClient {@link LiClient}
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
     * This client fetches list of boards for a given category. It takes 'categoryId' as a parameter.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiBoardsByDepthClientRequestParams} Depicts the level upto which data is required.
     * @return LiClient {@link LiClient}
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
     * This client fetches details of child messages (replies or posts in a thread) of a given message node.
     * It takes 'parentId' as a parameter.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiRepliesClientRequestParams} This id of parent message.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @NonNull
    public static LiClient getRepliesClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_REPLIES_CLIENT);
        Long parentId = ((LiClientRequestParams.LiRepliesClientRequestParams) liClientRequestParams).getParentId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(parentId));
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client is used for searching in the community. The search text is passed as parameter as 'query'.
     * The 'query' parameter is compared with body/subject of the message.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiSearchClientRequestParams} This is the string to be searched.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getSearchClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_SEARCH_CLIENT);
        String query = ((LiClientRequestParams.LiSearchClientRequestParams) liClientRequestParams).getQuery();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", query);
        return new LiBaseGetClient(liClientRequestParams.getContext(), String.format(LiQueryConstant.LI_SEARCH_CLIENT_BASE_LIQL, query), LiQueryConstant.LI_SEARCH_CLIENT_TYPE, LiQueryConstant.LI_SEARCH_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches question posted by any author up to a specified depth. It takes 'authorId' and 'depth' as parameters.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiUserMessagesClientRequestParams}  id of the author.
     * @param liClientRequestParams {@link LiClientRequestParams.LiUserMessagesClientRequestParams} level upto which data is required.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUserMessagesClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_USER_MESSAGES_CLIENT);
        Long authorId = ((LiClientRequestParams.LiUserMessagesClientRequestParams) liClientRequestParams).getAuthorId();
        String depth = ((LiClientRequestParams.LiUserMessagesClientRequestParams) liClientRequestParams).getDepth();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(authorId)).replaceAll("&&", depth);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_QUESTIONS_CLIENT_BASE_LIQL, LiQueryConstant.LI_QUESTIONS_CLIENT_TYPE, LiQueryConstant.LI_QUESTIONS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches category details for the user. It selects from nodes where node_type is equal to category.
     *
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getCategoryClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_CATEGORY_CLIENT);
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_CATEGORY_CLIENT_BASE_LIQL, LiQueryConstant.LI_CATEGORY_CLIENT_TYPE, LiQueryConstant.LI_CATEGORY_QUERYSETTINGS_TYPE, LiBrowse.class);
    }

    /**
     * This client fetches all the details of a user. It takes 'userId' as parameter.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiUserDetailsClientRequestParams} The id of the user whose details are to be fetched.
     * @return LiClient LiClient {@link LiClient}
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
     * This client fetches details of a particular message. It takes 'messageId' as parameter.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMessageClientRequestParams} Id of the message.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getMessageClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_MESSAGE_CLIENT);
        Long messageId = ((LiClientRequestParams.LiMessageClientRequestParams) liClientRequestParams).getMessageId();
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(messageId));
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_CLIENT_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches details of all pinned messages of a user.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiFloatedMessagesClientRequestParams} Id of the board and scope for searching the floated messages. Currently it has two scopes ("global", "local")
     * @return LiClient {@link LiClient}
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
     * This client fetches details of desired sets of messages.
     * The message ids of the messages whose details are needed is passes as parameter in a set.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiMessagesByIdsClientRequestParams}
     * @return LiClient {@link LiClient}
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

        return new LiBaseGetClient(liClientRequestParams.getContext(), LiQueryConstant.LI_MESSAGE_CLIENT_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_BY_IDS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client is used to kudo a particular message. The id of the message which has to be kudoed is passed as parameter.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiKudoClientRequestParams} Id of the message to be Kudoed.
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
     * This client is used to Un kudo a particular message. The id of the message which has to be Un kudoed is passed as parameter.
     *
     * @param liClientRequestParams {@LiClientRequestParams.LiUnKudoClientRequestParams} Id of the message to be un-kudoed.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getUnKudoClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_UNKUDO_CLIENT);
        String messageId = ((LiClientRequestParams.LiUnKudoClientRequestParams) liClientRequestParams).getMessageId();
        LiClientRequestParams.LiGenericDeleteQueryParamsClientRequestParams liGenericDeleteQueryParamsClientRequestParams = new LiClientRequestParams.LiGenericDeleteQueryParamsClientRequestParams(liClientRequestParams.getContext(), CollectionsType.MESSAGE, messageId, "/kudos");
        LiBaseDeleteClient liBaseDeleteClient = (LiBaseDeleteClient) getGenericDeleteQueryParamsGetClient(liGenericDeleteQueryParamsClientRequestParams);
        return liBaseDeleteClient;
    }

    /**
     * This client is used to accept an article as solution. The message id of the article to be marked as accepted is passes as parameter.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiAcceptSolutionClientRequestParams}Id of the message to be marked as solution.
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
     * This client is used to post a new question. It takes the subject, body and the id of the board to which the question is to be posted.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCreateMessageClientRequestParams} It is the subject of the message,
     * @param liClientRequestParams It is the body of the message.
     * @param liClientRequestParams The board to which message is attached.
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
     * This method embeds image tag into the Message Body.
     * <p><li-image id=IMAGEID</> width="500" height="500" alt=IAMGEID.png align="inline" size="large" sourcetype="new"></li-image></p>
     * //This is to insert image id in body if any to effectively display image along with post.
     *
     * @param body      Body of the message to which image has to be attached.
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
     * This client is used for replying to a question. It takes the body(reply) and the id of the article to which reply is being made.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiCreateReplyClientRequestParams} Body of reply message.
     * @param liClientRequestParams Id of the message to which reply is made.
     * @return LiClient LiClient {@link LiClient}
     * @throws LiRestResponseException LiClient {@link LiRestResponseException}
     */
    public static LiClient getCreateReplyClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_CREATE_REPLY_CLIENT);
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
        liReplyMessage.setBody(body);
        liReplyMessage.setType(LiQueryConstant.LI_REPLY_MESSAGE_TYPE);
        liReplyMessage.setParent(parent);
        liBasePostClient.postModel = liReplyMessage;
        return liBasePostClient;
    }

    /**
     * This client is use to upload an image to the LIA. It takes image title, description, name and the path where the image is placed.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiUploadImageClientRequestParams}      This is title of the message.
     * @param liClientRequestParams This is description of the image to be uploaded.
     * @param liClientRequestParams Name of the image file.
     * @param liClientRequestParams Absolute path of the image file.
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
     * This client is used to report abuse a message.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiReportAbuseClientRequestParams}The id of message which is to be marked as abusive.
     * @param liClientRequestParams The id of the user marking the message as abusive.
     * @param liClientRequestParams The body of the message
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
     * Fetches 'id' corresponding to device id form the community.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiDeviceIdFetchClientRequestParams} Id registered with notification providers.
     * @param liClientRequestParams Global provide for Push notification. Currently "GCM" and "FIREBASE".
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
     * Updates 'id' in community with the given device id.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiDeviceIdUpdateClientRequestParams}Id registered with notification providers.
     * @param liClientRequestParams Id corresponding to device id in the community.
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
     * Creates new User.
     * @param liClientRequestParams {@link LiClientRequestParams.LiCreateUserParams}general details of user for creating it.
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
     * Use to mark a message read/unread
     * If u give markUnread flag as true it will mark given message as Unread
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMarkMessageParams}general message details.
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
     * Use to mark given comma separated  messageIds read/unread
     * If u give markUnread flag as true it will mark given messages as Unread
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMarkMessagesParams}general message details.
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
     * Use to mark a topic read/unread
     * If u give markUnread flag as true it will mark  messages in given topic as Unread
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiMarkTopicParams}general message details.
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
     * Use to add a subscription
     * @param liClientRequestParams {@link LiClientRequestParams.LiPostSubscriptionParams}general message details.
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getSubscriptionPostClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_SUBSCRIPTION_POST_CLIENT);
        LiMessage target = ((LiClientRequestParams.LiPostSubscriptionParams)liClientRequestParams).getTarget();
        LiBasePostClient liBasePostClient = new LiBasePostClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/subscriptions", LiSDKManager.getInstance().getTenant()));
        LiSubscriptionPostModel liSubscriptionPostModel = new LiSubscriptionPostModel();
        liSubscriptionPostModel.setType(LI_SUBSCRIPTIONS_CLIENT_TYPE);
        liSubscriptionPostModel.setTarget(target);
        liBasePostClient.postModel = liSubscriptionPostModel;
        return liBasePostClient;
    }

    /**
     * Use to delete subscription message.
     * @param liClientRequestParams {@link LiClientRequestParams.LiDeleteSubscriptionParams} It is subscription Id.
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getSubscriptionDeleteClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_SUBSCRIPTION_DELETE_CLIENT);
        String id = ((LiClientRequestParams.LiDeleteSubscriptionParams)liClientRequestParams).getSubscriptionId();
        LiClientRequestParams.LiGenericDeleteQueryParamsClientRequestParams liGenericDeleteQueryParamsClientRequestParams = new LiClientRequestParams.LiGenericDeleteQueryParamsClientRequestParams(liClientRequestParams.getContext(), CollectionsType.SUBSCRIPTION, id);
        LiBaseDeleteClient liBaseDeleteClient = (LiBaseDeleteClient) getGenericDeleteQueryParamsGetClient(liGenericDeleteQueryParamsClientRequestParams);
        return liBaseDeleteClient;
    }

    /**
     * Updates an existing user.
     * @param liClientRequestParams {@link LiClientRequestParams.LiUpdateUserParams}general details of user for updating it.
     * @return {@link LiClient}
     * @throws LiRestResponseException
     */
    public static LiClient getUpdateUserClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_UPDATE_USER_CLIENT);

        LiAvatar avatar = ((LiClientRequestParams.LiUpdateUserParams) liClientRequestParams).getAvatar();
        String biography = ((LiClientRequestParams.LiUpdateUserParams) liClientRequestParams).getBiography();
        String coverImage = ((LiClientRequestParams.LiUpdateUserParams) liClientRequestParams).getCoverImage();
        String email = ((LiClientRequestParams.LiUpdateUserParams) liClientRequestParams).getEmail();
        String firstName = ((LiClientRequestParams.LiUpdateUserParams) liClientRequestParams).getFirstName();
        String lastName = ((LiClientRequestParams.LiUpdateUserParams) liClientRequestParams).getLastName();
        String login = ((LiClientRequestParams.LiUpdateUserParams) liClientRequestParams).getLogin();
        LiBasePutClient liBasePutClient = new LiBasePutClient(liClientRequestParams.getContext(), String.format("/community/2.0/%s/users", LiSDKManager.getInstance().getTenant()));
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
     * This is generic Post client. User can provide own specific path and response body.
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericPostClientRequestParams} Endpoint of API.
     * @param liClientRequestParams Request body as {@link JsonObject}
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericPostClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_POST_CLIENT);
        String path = ((LiClientRequestParams.LiGenericPostClientRequestParams) liClientRequestParams).getPath();
        JsonObject requestBody = ((LiClientRequestParams.LiGenericPostClientRequestParams) liClientRequestParams).getRequestBody();
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
     * This is generic Get client which can take user defined LIQL as parameter
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericLiqlClientRequestParams} Generic LIQL query.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public static LiClient getGenericLiqlGetClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_LIQL_CLIENT);
        String liQuery = ((LiClientRequestParams.LiGenericLiqlClientRequestParams) liClientRequestParams).getLiQuery();
        return new LiBaseGetClient(liClientRequestParams.getContext(), liQuery, null, LiQueryConstant.LI_GENERIC_TYPE, null);
    }

    /**
     * Generic GET Client whose `where clause`, `liQueryOrdering` and `limit` fields can be changed in LIQL,
     * but base LIQL query will be defined by the client type passed in parameter
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericQueryParamsClientRequestParams} {@link LiQueryRequestParams}
     * @return LiClient {@link LiClient}
     */
    public static LiClient getGenericQueryParamsGetClient(LiClientRequestParams liClientRequestParams) {
        liClientRequestParams.validate(Client.LI_GENERIC_QUERY_PARAMS_CLIENT);
        LiQueryRequestParams liQueryRequestParams = ((LiClientRequestParams.LiGenericQueryParamsClientRequestParams) liClientRequestParams).getLiQueryRequestParams();
        return new LiBaseGetClient(liClientRequestParams.getContext(), LiClientConfig.getBaseQuery(liQueryRequestParams.getClient()), LiClientConfig.getType(liQueryRequestParams.getClient()), LiClientConfig.getResponseClass(liQueryRequestParams.getClient()), liQueryRequestParams);
    }

    /**
     * Generic DELETE Client
     *
     * @param liClientRequestParams {@link LiClientRequestParams.LiGenericDeleteQueryParamsClientRequestParams}
     * @return LiClient {@link LiClient}
     */
    public static LiClient getGenericDeleteQueryParamsGetClient(LiClientRequestParams liClientRequestParams) throws LiRestResponseException {
        liClientRequestParams.validate(Client.LI_GENERIC_DELETE_QUERY_PARAMS_CLIENT);
        LiClientRequestParams.LiGenericDeleteQueryParamsClientRequestParams clientRequestParams = (LiClientRequestParams.LiGenericDeleteQueryParamsClientRequestParams) liClientRequestParams;
        Map<String, String> queryRequestParams = clientRequestParams.getLiQueryRequestParams();
        String id = clientRequestParams.getId();
        String extraPathAfterId = clientRequestParams.getSubResourcePath();
        CollectionsType collectionsType = clientRequestParams.getCollectionsType();
        StringBuilder path = new StringBuilder();
        path = path.append(String.format("/community/2.0/%s/%s/%s", LiSDKManager.getInstance().getTenant(), collectionsType.getValue(), id));
        if (extraPathAfterId != null) {
            path=path.append(extraPathAfterId);
        }
        if (queryRequestParams != null && queryRequestParams.size() > 0) {
            path = path.append("?");
            for (String key : queryRequestParams.keySet()) {
                String value = queryRequestParams.get(key);
                if(value!=null) {
                    path = path.append(key).append("=").append(value);
                }
            }
        }
        return new LiBaseDeleteClient(liClientRequestParams.getContext(), path.toString());
    }

    /**
     * Enum of all clients.
     */
    public enum Client {
        LI_MESSAGES_CLIENT,
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
        LI_SUBSCRIPTION_POST_CLIENT,
        LI_SUBSCRIPTION_DELETE_CLIENT,
        LI_ARTICLES_BROWSE_CLIENT,
        LI_MARK_MESSAGE_POST_CLIENT,
        LI_MARK_MESSAGES_POST_CLIENT,
        LI_MARK_TOPIC_POST_CLIENT
    }

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
