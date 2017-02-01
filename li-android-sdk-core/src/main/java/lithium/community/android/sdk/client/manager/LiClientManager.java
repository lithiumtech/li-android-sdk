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

package lithium.community.android.sdk.client.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import lithium.community.android.sdk.LiSDKManager;
import lithium.community.android.sdk.api.LiBaseDeleteClient;
import lithium.community.android.sdk.api.LiBaseGetClient;
import lithium.community.android.sdk.api.LiBasePostClient;
import lithium.community.android.sdk.api.LiBasePutClient;
import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.LiBoard;
import lithium.community.android.sdk.model.post.LiAcceptSolution;
import lithium.community.android.sdk.model.post.LiGenericPostModel;
import lithium.community.android.sdk.model.post.LiPostKudoModel;
import lithium.community.android.sdk.model.post.LiPostMessageModel;
import lithium.community.android.sdk.model.post.LiReplyMessageModel;
import lithium.community.android.sdk.model.post.LiUploadImageModel;
import lithium.community.android.sdk.model.post.LiUserDeviceData;
import lithium.community.android.sdk.model.post.LiUserDeviceIdUpdate;
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
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiQueryConstant;

import static lithium.community.android.sdk.utils.LiQueryConstant.LI_INSERT_IMAGE_MACRO;
import static lithium.community.android.sdk.utils.LiQueryConstant.LI_LINE_SEPARATOR;


/**
 * This class acts as a manager to hold and refresh the user's auth state. It has all the API providers
 * to interact with the community.
 */
public class LiClientManager {

    private static LiAuthManager liAuthManager;
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);

    /**
     * private constructor.
     */
    private LiClientManager() {
    }

    /**
     * Returns a singleton instance of this class.
     *
     * @return Instance of this class.
     */
    public static LiClientManager getInstance() {
        if (isInitialized.get()) {
            return LiClientManagerInitializer.clientManager;
        } else {
            Log.e("LiSDK", "Client manager not initialised. First initialize client manager");
            return null;
        }
    }

    /**
     * Initializes LiClientManager.
     * @param context {@link Context}
     * @return Instance of the class.
     * @throws URISyntaxException
     */
    public static LiClientManager init(Context context) throws URISyntaxException {
        LiCoreSDKUtils.checkNotNull(context);
        if (isInitialized.compareAndSet(false, true)) {
            LiClientManagerInitializer.clientManager.liAuthManager = new LiAuthManager(context);
        }
        return LiClientManagerInitializer.clientManager;
    }

    public LiAuthManager getLiAuthManager() {
        return liAuthManager;
    }

    public LiRestv2Client getRestClient() throws LiRestResponseException {
        return LiRestv2Client.getInstance();
    }

    // Methods to return client object

    /**
     * Provide GET Client whose where clause, liQueryOrdering and limit fields can be changed in LIQL.
     * @param liQueryRequestParams {@link LiQueryRequestParams}
     * @return LiClient {@link LiClient}
     */
    public LiClient getClient(LiQueryRequestParams liQueryRequestParams) {
        return new LiBaseGetClient(LiClientConfig.getBaseQuery(liQueryRequestParams.getClient()), LiClientConfig.getType(liQueryRequestParams.getClient()), LiClientConfig.getResponseClass(liQueryRequestParams.getClient()), liQueryRequestParams);
    }

    /**
     * This client fetches list of all the articles for that user, it is a basic search which gets top articles (configurable limit) ordered by PostTime or Kudos count.
     * The articles are visible on App home page
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getMessagesClient() throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", "0");
        return new LiBaseGetClient(LiQueryConstant.LI_ARTICLES_CLIENT_BASE_LIQL, LiQueryConstant.LI_ARTICLES_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches all the settings of the SDK from LIA for that user.
     * It takes 'clientId' as parameter as setting details are governed by 'clientId'.
     * @param clientId This is client id.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getSdkSettingsClient(final String clientId) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", clientId);
        return new LiBaseGetClient(LiQueryConstant.LI_SDK_SETTINGS_CLIENT_BASE_LIQL, LiQueryConstant.LI_SDK_SETTINGS_CLIENT_TYPE, LiQueryConstant.LI_SDK_SETTINGS_QUERYSETTINGS_TYPE, LiAppSdkSettings.class).setReplacer(liQueryValueReplacer);
    }

    /**
     *  This client fetches all articles corresponding to a particular board id for a user.
     *  It takes 'boardId' as parameter.
     * @param boardId This is Id of board of which messags are to be fetched.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getMessagesClient(final String boardId) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", boardId).replaceAll("&&", "0");
        return new LiBaseGetClient(LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_ARTICLES_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_ARTICLES_BROWSE_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches list of all subscriptions corresponding to a particular user.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getUserSubscriptionsClient() throws LiRestResponseException {
        return new LiBaseGetClient(LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_BASE_LIQL, LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_TYPE, LiQueryConstant.LI_SUBSCRIPTTION_QUERYSETTINGS_TYPE, LiSubscriptions.class);
    }

    /**
     * This client fetches list of boards for a given category. It takes 'categoryId' as a parameter.
     * @param categoryId This is category Id.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getCategoryBoardsClient(@NonNull final String categoryId) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", categoryId);
        return new LiBaseGetClient(LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_BROWSE_QUERYSETTINGS_TYPE, LiBrowse.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches list of boards for a given category. It takes 'categoryId' as a parameter.
     * @param depth Depicts the level upto which data is required.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getBoardsByDepthClient(@NonNull final int depth) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(depth));
        return new LiBaseGetClient(LiQueryConstant.LI_BROWSE_CLIENT_BASE_LIQL, LiQueryConstant.LI_BROWSE_CLIENT_TYPE, LiQueryConstant.LI_BROWSE_BY_DEPTH_QUERYSETTINGS_TYPE, LiBrowse.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches details of child messages (replies or posts in a thread) of a given message node.
     * It takes 'parentId' as a parameter.
     * @param parentId This id of parent message.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @NonNull
    public LiClient getRepliesClient(@NonNull final Long parentId) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(parentId));
        return new LiBaseGetClient(LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CHILDREN_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client is used for searching in the community. The search text is passed as parameter as 'query'.
     * The 'query' parameter is compared with body/subject of the message.
     * @param query This is the string to be searched.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getSearchClient(final String query) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", query);
        return new LiBaseGetClient(String.format(LiQueryConstant.LI_SEARCH_CLIENT_BASE_LIQL, query), LiQueryConstant.LI_SEARCH_CLIENT_TYPE, LiQueryConstant.LI_SEARCH_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches question posted by any author up to a specified depth. It takes 'authorId' and 'depth' as parameters.
     * @param authorId The id of the author.
     * @param depth Depicts the level upto which data is required.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getUserMessagesClient(@NonNull final Long authorId, final String depth) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(authorId)).replaceAll("&&", depth);
        return new LiBaseGetClient(LiQueryConstant.LI_QUESTIONS_CLIENT_BASE_LIQL, LiQueryConstant.LI_QUESTIONS_CLIENT_TYPE, LiQueryConstant.LI_QUESTIONS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches category details for the user. It selects from nodes where node_type is equal to category.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getCategoryClient() throws LiRestResponseException {
        return new LiBaseGetClient(LiQueryConstant.LI_CATEGORY_CLIENT_BASE_LIQL, LiQueryConstant.LI_CATEGORY_CLIENT_TYPE, LiQueryConstant.LI_CATEGORY_QUERYSETTINGS_TYPE, LiBrowse.class);
    }

    /**
     * This client fetches all the details of a user. It takes 'userId' as parameter.
     * @param userId The id of the user whose details are to be fetched.
     * @return LiClient LiClient {@link LiClient}
     * @throws LiRestResponseException LiClient {@link LiRestResponseException}
     */
    public LiClient getUserDetailsClient(final String userId) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", userId);
        return new LiBaseGetClient(LiQueryConstant.LI_USER_DETAILS_CLIENT_BASE_LIQL, LiQueryConstant.LI_USER_DETAILS_CLIENT_TYPE, LiQueryConstant.LI_USER_DETAILS_QUERYSETTINGS_TYPE, LiUser.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches details of a particular message. It takes 'messageId' as parameter.
     * @param messageId Id of the message.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getMessageClient(@NonNull final Long messageId) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(messageId));
        return new LiBaseGetClient(LiQueryConstant.LI_MESSAGE_CLIENT_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches details of all pinned messages of a user.
     * @param boardId Id of the board.
     * @param scope It defines scope for searching the floated messages. Currently it has two scopes ("global", "local").
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getFloatedMessagesClient(final String boardId, final String scope) throws LiRestResponseException {
        LiQueryValueReplacer liQueryValueReplacer = new LiQueryValueReplacer();
        liQueryValueReplacer.replaceAll("##", String.valueOf(boardId)).replaceAll("&&", scope);
        return new LiBaseGetClient(LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_BASE_LIQL, LiQueryConstant.LI_FLOATED_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_FLOATED_MESSAGE_QUERYSETTINGS_TYPE, LiFloatedMessageModel.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client fetches details of desired sets of messages.
     * The message ids of the messages whose details are needed is passes as parameter in a set.
     * @param messageIds It is a set of message ids.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getMessagesByIdsClient(@NonNull Set<String> messageIds) throws LiRestResponseException {
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

        return new LiBaseGetClient(LiQueryConstant.LI_MESSAGE_CLIENT_BASE_LIQL, LiQueryConstant.LI_MESSAGE_CLIENT_TYPE, LiQueryConstant.LI_MESSAGE_BY_IDS_QUERYSETTINGS_TYPE, LiMessage.class).setReplacer(liQueryValueReplacer);
    }

    /**
     * This client is used to kudo a particular message. The id of the message which has to be kudoed is passed as parameter.
     * @param messageId Id of the message to be Kudoed.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getKudoClient(String messageId) throws LiRestResponseException {
        LiBasePostClient liBasePostClient = new LiBasePostClient(String.format("/community/2.0/%s/messages/%s/kudos", LiClientManager.getInstance().getLiAuthManager().getTenant(), messageId));
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
     * @param messageId Id of the message to be un-kudoed.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getUnKudoClient(String messageId) throws LiRestResponseException {
        LiBaseDeleteClient liBaseDeleteClient = new LiBaseDeleteClient(String.format("/community/2.0/%s/messages/%s/kudos", LiClientManager.getInstance().getLiAuthManager().getTenant(), messageId));
        return liBaseDeleteClient;
    }

    /**
     * This client is used to accept an article as solution. The message id of the article to be marked as accepted is passes as parameter.
     * @param messageId Id of the message to be marked as solution.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getAcceptSolutionClient(Long messageId) throws LiRestResponseException {
        LiBasePostClient liBasePostClient = new LiBasePostClient(String.format("/community/2.0/%s/solutions_data", LiClientManager.getInstance().getLiAuthManager().getTenant()));
        LiAcceptSolution liAcceptSolution = new LiAcceptSolution();
        liAcceptSolution.setType(LiQueryConstant.LI_ACCEPT_SOLUTION_TYPE);
        liAcceptSolution.setMessageid(String.valueOf(messageId));
        liBasePostClient.postModel = liAcceptSolution;
        return liBasePostClient;
    }

    /**
     * This client is used to post a new question. It takes the subject, body and the id of the board to which the question is to be posted.
     * @param subject It is the subject of the message,
     * @param body It is the body of the message.
     * @param boardId The board to which message is attached.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getCreateMessageClient(String subject, String body, String boardId,
                                           String imageId, String imageName) throws LiRestResponseException {

        LiBasePostClient liBasePostClient = new LiBasePostClient(String.format("/community/2.0/%s/messages", LiClientManager.getInstance().getLiAuthManager().getTenant()));
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
     * @param body Body of the message to which image has to be attached.
     * @param imageId Id of the image received from community.
     * @param imageName Name of the image.
     * @return Message Body with embedded image tag.
     */
    private String embedImageTag(String body, String imageId, String imageName) {
        if(!TextUtils.isEmpty(imageId)){
            if (body == null) {
                body = "<p>&nbsp;</p>";
            }
            body = body + LI_LINE_SEPARATOR + String.format(LI_INSERT_IMAGE_MACRO, imageId, imageName);
        }
        return body;
    }

    /**
     * This client is used for replying to a question. It takes the body(reply) and the id of the article to which reply is being made.
     * @param body Body of reply message.
     * @param messageId Id of the message to which reply is made.
     * @return LiClient LiClient {@link LiClient}
     * @throws LiRestResponseException LiClient {@link LiRestResponseException}
     */
    public LiClient getCreateReplyClient(String body, Long messageId, String imageId, String imageName) throws LiRestResponseException {

        LiBasePostClient liBasePostClient = new LiBasePostClient(
                String.format("/community/2.0/%s/messages",
                        LiClientManager.getInstance().getLiAuthManager().getTenant()));
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
     * @param title This is title of the message.
     * @param description This is description of the image to be uploaded.
     * @param imageName Name of the image file.
     * @param path Absolute path of the image file.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getUploadImageClient(String title, String description, String imageName, String path) throws LiRestResponseException {

        LiBasePostClient liBasePostClient = new LiBasePostClient(
                String.format("/community/2.0/%s/images",
                        LiClientManager.getInstance().getLiAuthManager().getTenant()), path, imageName);
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
     * Fetches 'id' corresponding to device id form the community.
     * @param deviceId  Id registered with notification providers.
     * @param pushNotificationProvider Global provide for Push notification. Currently "GCM" and "FIREBASE".
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getDeviceIdFetchClient(String deviceId, String pushNotificationProvider) throws LiRestResponseException {
        LiBasePostClient liBasePostClient = new LiBasePostClient(
                String.format("/community/2.0/%s/user_device_data",
                        LiClientManager.getInstance().getLiAuthManager().getTenant()));
        LiUserDeviceData liUserDeviceData = new LiUserDeviceData();
        liUserDeviceData.setType(LiQueryConstant.LI_USER_DEVICE_ID_FETCH_TYPE);
        liUserDeviceData.setDeviceId(deviceId);
        liUserDeviceData.setClientId(LiSDKManager.getInstance().getLiAppCredentials().getClientKey());
        liUserDeviceData.setApplicationType(LiQueryConstant.LI_APPLICATION_TYPE);
        liUserDeviceData.setPushNotificationProvider(pushNotificationProvider);
        liBasePostClient.postModel = liUserDeviceData;
        return liBasePostClient;
    }

    /**
     * Updates 'id' in community with the given device id.
     * @param deviceId Id registered with notification providers.
     * @param id Id corresponding to device id in the community.
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient getDeviceIdUpdateClient(String deviceId, String id) throws LiRestResponseException {

        String path = "/community/2.0/%s/user_device_data/" + id;
        LiBasePutClient liBasePostClient = new LiBasePutClient(
                String.format(path,
                        LiClientManager.getInstance().getLiAuthManager().getTenant()));
        LiUserDeviceIdUpdate liUserDeviceIdUpdate = new LiUserDeviceIdUpdate();
        liUserDeviceIdUpdate.setType(LiQueryConstant.LI_USER_DEVICE_ID_FETCH_TYPE);
        liUserDeviceIdUpdate.setDeviceId(deviceId);
        liBasePostClient.postModel = liUserDeviceIdUpdate;
        return liBasePostClient;
    }

    /**
     * This is generic Post client. User can provide own specific path and response body.
     * @param path Endpoint of API.
     * @param requestBody Request body as {@link JsonObject}
     * @return {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient createGenericPostClient(String path, JsonObject requestBody) throws LiRestResponseException {
        String requestPath = "/community/2.0/%s/" + path;
        LiBasePostClient liBasePostClient = new LiBasePostClient(
                String.format(requestPath,
                        LiClientManager.getInstance().getLiAuthManager().getTenant()));

        LiGenericPostModel genericPostModel = new LiGenericPostModel();
        genericPostModel.setData(requestBody);
        liBasePostClient.postModel = genericPostModel;
        return liBasePostClient;
    }

    /**
     * This is generic Get client. User can provide own LIQL.
     * @param liQuery Generic LIQL query.
     * @return LiClient {@link LiClient}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiClient createGenericGetClient(String liQuery) throws LiRestResponseException {
        return new LiBaseGetClient(liQuery, null, LiQueryConstant.LI_GENERIC_TYPE, null);
    }

    /**
     * Initialized for LiClientManager.
     */
    private static class LiClientManagerInitializer {
        private static final LiClientManager clientManager = new LiClientManager();
    }

    /**
     * Enum of all clients.
     */
    public enum Client {
        LI_ARTICLES_CLIENT,
        LI_SUBSCRIPTION_CLIENT,
        LI_BROWSE_CLIENT,
        LI_SEARCH_CLIENT,
        LI_MESSAGE_CHILDREN_CLIENT,
        LI_QUESTIONS_CLIENT,
        LI_CATEGORY_CLIENT,
        LI_ARTICLES_BROWSE_CLIENT,
        LI_USER_DETAILS_CLIENT,
        LI_MESSAGE_CLIENT,
        LI_FLOATED_MESSAGE_CLIENT,
        LI_SDK_SETTINGS_CLIENT
    }
}
