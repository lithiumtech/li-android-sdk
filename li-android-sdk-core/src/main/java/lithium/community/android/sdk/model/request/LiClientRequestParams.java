package lithium.community.android.sdk.model.request;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.queryutil.LiQueryRequestParams;

public class LiClientRequestParams {

    LiClientManager.Client client;
    Context context;

    public LiClientRequestParams(Context context) {
        this.context = context;
    }

    public LiClientManager.Client getClient() {
        return client;
    }

    public Context getContext() {
        return context;
    }

    public LiClientRequestParams setContext(Context context) {
        this.context = context;
        return this;
    }

    public void validate(LiClientManager.Client client) {
        if (client != null && !client.equals(this.client)) {
            throw new IllegalArgumentException("Arguments mismatch:: Expected: " + this.client + "Found: " + client);
        }
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
    }

    //Request params class for LiMessagesClient
    public static class LiMessagesClientRequestParams extends LiClientRequestParams {
        /**
         * Builds the parameters for {@link LiClientManager#getMessagesClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         */
        public LiMessagesClientRequestParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_MESSAGES_CLIENT;
        }

    }

    //Request params class for LiMessagesByBoardIdClient
    public static class LiMessagesByBoardIdClientRequestParams extends LiClientRequestParams {
        private String boardId;

        /**
         * Builds the parameters for {@link LiClientManager#getMessagesByBoardIdClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param boardId the ID of the board from which to pull messages (required)
         */
        public LiMessagesByBoardIdClientRequestParams(Context context, String boardId) {
            super(context);
            this.boardId = boardId;
            this.client = LiClientManager.Client.LI_MESSAGES_BY_BOARD_ID_CLIENT;
        }

        public String getBoardId() {
            return boardId;
        }

        public LiMessagesByBoardIdClientRequestParams setBoardId(String boardId) {
            this.boardId = boardId;
            return this;
        }
    }

    //Request params class for LiSdkSettingsClient
    public static class LiSdkSettingsClientRequestParams extends LiClientRequestParams {
        private String clientId;

        /**
         * Builds the parameters for {@link LiClientManager#getSdkSettingsClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param clientId the client ID for the app generated in Community Admin > System > API Apps (required)
         */

        public LiSdkSettingsClientRequestParams(Context context, String clientId) {
            super(context);
            this.clientId = clientId;
            this.client = LiClientManager.Client.LI_SDK_SETTINGS_CLIENT;
        }

        public String getClientId() {
            return clientId;
        }

        public LiSdkSettingsClientRequestParams setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
    }

    //Request params class for LiUserSubscriptionsClient
    public static class LiUserSubscriptionsClientRequestParams extends LiClientRequestParams {

        /**
         * Builds the parameters for {@link LiClientManager#getUserSubscriptionsClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         */
        public LiUserSubscriptionsClientRequestParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_USER_SUBSCRIPTIONS_CLIENT;
        }

    }

    //Request params class for LiCategoryBoardsClient
    public static class LiCategoryBoardsClientRequestParams extends LiClientRequestParams {
        private String categoryId;

        /**
         * Builds the parameters for {@link LiClientManager#getCategoryBoardsClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param categoryId the ID of the category from which to fetch a board list (required)
         */
        public LiCategoryBoardsClientRequestParams(Context context, String categoryId) {
            super(context);
            this.categoryId = categoryId;
            this.client = LiClientManager.Client.LI_CATEGORY_BOARDS_CLIENT;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public LiCategoryBoardsClientRequestParams setCategoryId(String categoryId) {
            this.categoryId = categoryId;
            return this;
        }
    }

    //Request params class for LiBoardsByDepthClient
    public static class LiBoardsByDepthClientRequestParams extends LiClientRequestParams {
        private int depth;

        /**
         * Builds the parameters for {@link LiClientManager#getBoardsByDepthClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param depth the depth from which to pull boards from the Community structure. 0 is the highest level in the structure. (required)
         */
        public LiBoardsByDepthClientRequestParams(Context context, int depth) {
            super(context);
            this.depth = depth;
            this.client = LiClientManager.Client.LI_BOARDS_BY_DEPTH_CLIENT;
        }

        public int getDepth() {
            return depth;
        }

        public LiBoardsByDepthClientRequestParams setDepth(int depth) {
            this.depth = depth;
            return this;
        }
    }

    //Request params class for LiRepliesClient
    public static class LiRepliesClientRequestParams extends LiClientRequestParams {
        private Long parentId;

        /**
         * Builds the parameters for {@link LiClientManager#getRepliesClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param parentId the ID of the parent message (required)
         */
        public LiRepliesClientRequestParams(Context context, Long parentId) {
            super(context);
            this.parentId = parentId;
            this.client = LiClientManager.Client.LI_REPLIES_CLIENT;
        }

        public Long getParentId() {
            return parentId;
        }

        public LiRepliesClientRequestParams setParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }
    }

    //Request params class for LiSearchClient
    public static class LiSearchClientRequestParams extends LiClientRequestParams {
        private String query;

        /**
         * Builds the parameters for {@link LiClientManager#getSearchClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param query the search string to query. The query is compared against the body and subject of messages. (required)
         */
        public LiSearchClientRequestParams(Context context, String query) {
            super(context);
            this.query = query;
            this.client = LiClientManager.Client.LI_SEARCH_CLIENT;
        }

        public String getQuery() {
            return query;
        }

        public LiSearchClientRequestParams setQuery(String query) {
            this.query = query;
            return this;
        }
    }

    //Request params class for LiUserMessagesClient
    public static class LiUserMessagesClientRequestParams extends LiClientRequestParams {
        private Long authorId;
        private String depth;

        /**
         * Builds the parameters for {@link LiClientManager#getUserMessagesClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param authorId the ID of the user for whom to get messages
         * @param depth the location of messages in a thread, where 0 equals a topic message, 1 is a first-level reply or comment, and so on (required)
         */

        public LiUserMessagesClientRequestParams(Context context, Long authorId, String depth) {
            super(context);
            this.authorId = authorId;
            this.depth = depth;
            this.client = LiClientManager.Client.LI_USER_MESSAGES_CLIENT;
        }

        public Long getAuthorId() {
            return authorId;
        }

        public LiUserMessagesClientRequestParams setAuthorId(Long authorId) {
            this.authorId = authorId;
            return this;
        }

        public String getDepth() {
            return depth;
        }

        public LiUserMessagesClientRequestParams setDepth(String depth) {
            this.depth = depth;
            return this;
        }
    }

    //Request params class for LiCategoryClient
    public static class LiCategoryClientRequestParams extends LiClientRequestParams {

        /**
         * Builds the parameters for {@link LiClientManager#getCategoryClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         */
        public LiCategoryClientRequestParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_CATEGORY_CLIENT;
        }

    }

    //Request params class for LiUserDetailsClient
    public static class LiUserDetailsClientRequestParams extends LiClientRequestParams {
        private String userId;

        /**
         * Builds the parameters for {@link LiClientManager#getUserDetailsClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param userId the ID of the user for which to fetch details (required)
         */
        public LiUserDetailsClientRequestParams(Context context, String userId) {
            super(context);
            this.userId = userId;
            this.client = LiClientManager.Client.LI_USER_DETAILS_CLIENT;
        }

        public String getUserId() {
            return userId;
        }

        public LiUserDetailsClientRequestParams setUserId(String userId) {
            this.userId = userId;
            return this;
        }
    }

    //Request params class for LiMessageClient
    public static class LiMessageClientRequestParams extends LiClientRequestParams {
        private Long messageId;

        /**
         * Builds the parameters for {@link LiClientManager#getMessageClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to retrieve (required)
         */
        public LiMessageClientRequestParams(Context context, Long messageId) {
            super(context);
            this.messageId = messageId;
            this.client = LiClientManager.Client.LI_MESSAGE_CLIENT;
        }

        public Long getMessageId() {
            return messageId;
        }

        public LiMessageClientRequestParams setMessageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }
    }

    //Request params class for LiFloatedMessagesClient
    public static class LiFloatedMessagesClientRequestParams extends LiClientRequestParams {
        private String boardId;
        private String scope;

        /**
         * Builds the parameters for {@link LiClientManager#getFloatedMessagesClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         * @param boardId the ID of the board from which to pull floated (or 'pinned') messages
         * @param scope the scope of floated messages to retreive. Supported value is 'local'. Local scope retrieves messages that the user in context floated/pinned, rather than an administrator who might have pinned a message to the top of a board globally for the community.
         */
        public LiFloatedMessagesClientRequestParams(Context context, String boardId, String scope) {
            super(context);
            this.boardId = boardId;
            this.scope = scope;
            this.client = LiClientManager.Client.LI_FLOATED_MESSAGES_CLIENT;
        }

        public String getBoardId() {
            return boardId;
        }

        public LiFloatedMessagesClientRequestParams setBoardId(String boardId) {
            this.boardId = boardId;
            return this;
        }

        public String getScope() {
            return scope;
        }

        public LiFloatedMessagesClientRequestParams setScope(String scope) {
            this.scope = scope;
            return this;
        }
    }

    //Request params class for LiMessagesByIdsClient
    public static class LiMessagesByIdsClientRequestParams extends LiClientRequestParams {
        private Set<String> messageIds;

        /**
         * Builds the parameters for {@link LiClientManager#getMessagesByIdsClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param messageIds - the IDs of the messages to retrieve, passed as a set (required)
         */
        public LiMessagesByIdsClientRequestParams(Context context, Set<String> messageIds) {
            super(context);
            this.messageIds = messageIds;
            this.client = LiClientManager.Client.LI_MESSAGES_BY_ID_CLIENT;
        }

        public Set<String> getMessageIds() {
            return messageIds;
        }

        public LiMessagesByIdsClientRequestParams setMessageIds(Set<String> messageIds) {
            this.messageIds = messageIds;
            return this;
        }
    }

    //Request params class for LiKudoClient
    public static class LiKudoClientRequestParams extends LiClientRequestParams {
        private String messageId;

        /**
         *  Builds the parameters for {@link LiClientManager#getKudoClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to kudo (required)
         */
        public LiKudoClientRequestParams(Context context, String messageId) {
            super(context);
            this.messageId = messageId;
            this.client = LiClientManager.Client.LI_KUDO_CLIENT;
        }

        public String getMessageId() {
            return messageId;
        }

        public LiKudoClientRequestParams setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }
    }

    //Request params class for LiUnKudoClient
    public static class LiUnKudoClientRequestParams extends LiClientRequestParams {
        private String messageId;

        /**
         *  Builds the parameters for {@link LiClientManager#getUnKudoClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to unkudo (required)
         */
        public LiUnKudoClientRequestParams(Context context, String messageId) {
            super(context);
            this.messageId = messageId;
            this.client = LiClientManager.Client.LI_UNKUDO_CLIENT;
        }

        public String getMessageId() {
            return messageId;
        }

        public LiUnKudoClientRequestParams setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }
    }

    //Request params class for LiMessageDeleteClient
    public static class LiMessageDeleteClientRequestParams extends LiClientRequestParams {
        private String messageId;
        //if this flag is set it will Delete Replies also
        private boolean includeReplies;

        /**
         * Builds the parameters for {@link LiClientManager#getMessageDeleteClient}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to delete (required)
         */
        public LiMessageDeleteClientRequestParams(Context context, String messageId) {
            super(context);
            this.messageId = messageId;
            this.client = LiClientManager.Client.LI_MESSAGE_DELETE_CLIENT;
        }

        /**
         * Builds the parameters for {@link LiClientManager#getMessageDeleteClient}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to delete (required)
         * @param includeReplies whether or not to delete associated replies/comments (optional)
         */
        public LiMessageDeleteClientRequestParams(Context context, String messageId, boolean includeReplies) {
            super(context);
            this.messageId = messageId;
            this.includeReplies = includeReplies;
            this.client = LiClientManager.Client.LI_MESSAGE_DELETE_CLIENT;
        }

        public boolean isIncludeReplies() {
            return includeReplies;
        }

        public void setIncludeReplies(boolean includeReplies) {
            this.includeReplies = includeReplies;
        }

        public String getMessageId() {
            return messageId;
        }

        public LiMessageDeleteClientRequestParams setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }
    }


    //Request params class for LiAcceptSolutionClient
    public static class LiAcceptSolutionClientRequestParams extends LiClientRequestParams {
        private Long messageId;

        /**
         * Builds the parameters for {@link LiClientManager#getAcceptSolutionClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to accept as a solution (required)
         */
        public LiAcceptSolutionClientRequestParams(Context context, Long messageId) {
            super(context);
            this.messageId = messageId;
            this.client = LiClientManager.Client.LI_ACCEPT_SOLUTION_CLIENT;
        }

        public Long getMessageId() {
            return messageId;
        }

        public LiAcceptSolutionClientRequestParams setMessageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }
    }

    //Request params class for LiCreateMessageClient
    public static class LiCreateMessageClientRequestParams extends LiClientRequestParams {
        private String subject;
        private String body;
        private String boardId;
        private String imageId;
        private String imageName;

        /**
         * Builds the parameters for {@link LiClientManager#getCreateMessageClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param subject the subject of the message (required)
         * @param body the body of the message (optional)
         * @param boardId the board ID of the board in which to post the message (required)
         * @param imageId the ID of the image included with the message, if one exists (optional)
         * @param imageName the file of the image included with the message, if one exists (optional)
         */
        public LiCreateMessageClientRequestParams(Context context, String subject, String body, String boardId, String imageId, String imageName) {
            super(context);
            this.subject = subject;
            this.body = body;
            this.boardId = boardId;
            this.imageId = imageId;
            this.imageName = imageName;
            this.client = LiClientManager.Client.LI_CREATE_MESSAGE_CLIENT;
        }

        public String getSubject() {
            return subject;
        }

        public LiCreateMessageClientRequestParams setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public String getBody() {
            return body;
        }

        public LiCreateMessageClientRequestParams setBody(String body) {
            this.body = body;
            return this;
        }

        public String getBoardId() {
            return boardId;
        }

        public LiCreateMessageClientRequestParams setBoardId(String boardId) {
            this.boardId = boardId;
            return this;
        }

        public String getImageId() {
            return imageId;
        }

        public LiCreateMessageClientRequestParams setImageId(String imageId) {
            this.imageId = imageId;
            return this;
        }

        public String getImageName() {
            return imageName;
        }

        public LiCreateMessageClientRequestParams setImageName(String imageName) {
            this.imageName = imageName;
            return this;
        }
    }

    //Request params class for LiUpdateMessageClient
    public static class LiUpdateMessageClientRequestParams extends LiClientRequestParams {

        private String messageId;
        private String subject;
        private String body;

        /**
         * Builds the parameters for {@link LiClientManager#getUpdateMessageClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to update (optional)
         * @param subject the subject of the message (optional)
         * @param body the body of the message (optional)
         */
        public LiUpdateMessageClientRequestParams(Context context, String messageId, String subject, String body){
            super(context);
            this.messageId = messageId;
            this.body = body;
            this.subject = subject;
            this.client = LiClientManager.Client.LI_UPDATE_MESSAGE_CLIENT;
        }

        public String getMessageId(){
            return messageId;
        }

        public LiUpdateMessageClientRequestParams setMessageId(String messageId){
            this.messageId = messageId;
            return this;
        }

        public String getBody(){
            return body;
        }

        public LiUpdateMessageClientRequestParams setBody(String body){
            this.body = body;
            return this;
        }

        public String getSubject(){
            return subject;
        }

        public LiUpdateMessageClientRequestParams setSubject(String subject){
            this.subject = subject;
            return this;
        }
    }

    //Request params class for LiCreateReplyClient
    public static class LiCreateReplyClientRequestParams extends LiClientRequestParams {
        private String subject;
        private String body;
        private Long messageId;
        private String imageId;
        private String imageName;

        /**
         * Builds the parameters for {@link LiClientManager#getCreateReplyClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param body the body of the reply/comment (optional)
         * @param messageId the ID of the parent message (required)
         * @param imageId the ID of the image included with the reply/comment, if one exists (optional)
         * @param imageName the filename of the image included with the reply/comment, if one exists (optional)
         */
        public LiCreateReplyClientRequestParams(Context context, String subject, String body, Long messageId, String imageId, String imageName) {
            super(context);
            this.subject = subject;
            this.body = body;
            this.messageId = messageId;
            this.imageId = imageId;
            this.imageName = imageName;
            this.client = LiClientManager.Client.LI_CREATE_REPLY_CLIENT;
        }

        public String getSubject() {
            return subject;
        }

        public LiCreateReplyClientRequestParams setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public String getBody() {
            return body;
        }

        public LiCreateReplyClientRequestParams setBody(String body) {
            this.body = body;
            return this;
        }

        public Long getMessageId() {
            return messageId;
        }

        public LiCreateReplyClientRequestParams setMessageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }

        public String getImageId() {
            return imageId;
        }

        public LiCreateReplyClientRequestParams setImageId(String imageId) {
            this.imageId = imageId;
            return this;
        }

        public String getImageName() {
            return imageName;
        }

        public LiCreateReplyClientRequestParams setImageName(String imageName) {
            this.imageName = imageName;
            return this;
        }
    }

    //Request params class for LiUploadImageClient
    public static class LiUploadImageClientRequestParams extends LiClientRequestParams {
        private String title;
        private String description;
        private String imageName;
        private String path;

        /**
         * Builds the parameters for {@link LiClientManager#getUploadImageClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param title the title of the image (required)
         * @param description the description for the image (required)
         * @param imageName the filename of the image (required)
         * @param path the path to the image (required)
         */
        public LiUploadImageClientRequestParams(Context context, String title, String description, String imageName, String path) {
            super(context);
            this.title = title;
            this.description = description;
            this.imageName = imageName;
            this.path = path;
            this.client = LiClientManager.Client.LI_UPLOAD_IMAGE_CLIENT;
        }

        public String getTitle() {
            return title;
        }

        public LiUploadImageClientRequestParams setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public LiUploadImageClientRequestParams setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getImageName() {
            return imageName;
        }

        public LiUploadImageClientRequestParams setImageName(String imageName) {
            this.imageName = imageName;
            return this;
        }

        public String getPath() {
            return path;
        }

        public LiUploadImageClientRequestParams setPath(String path) {
            this.path = path;
            return this;
        }
    }

    //Request params class for LiReportAbuseClient
    public static class LiReportAbuseClientRequestParams extends LiClientRequestParams {
        private String messageId;
        private String userId;
        private String body;


        /**
         * Builds the parameters for {@link LiClientManager#getReportAbuseClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param messageId the ID of the message to report (required)
         * @param userId the ID of the user making the abuse report (required)
         * @param body the body of the message being reported (required)
         */
        public LiReportAbuseClientRequestParams(Context context, String messageId, String userId, String body) {
            super(context);
            this.messageId = messageId;
            this.userId = userId;
            this.body = body;
            this.client = LiClientManager.Client.LI_MARK_ABUSE_CLIENT;
        }

        public String getMessageId() {
            return messageId;
        }

        public LiReportAbuseClientRequestParams setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public String getUserId() {
            return userId;
        }

        public LiReportAbuseClientRequestParams setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getBody() {
            return body;
        }

        public LiReportAbuseClientRequestParams setBody(String body) {
            this.body = body;
            return this;
        }
    }

    //Request params class for LiDeviceIdFetchClient
    public static class LiDeviceIdFetchClientRequestParams extends LiClientRequestParams {
        private String deviceId;
        private String pushNotificationProvider;

        /**
         * Builds the parameters for {@link LiClientManager#getDeviceIdFetchClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param deviceId the device ID registered with the push notificaiton provider (required)
         * @param pushNotificationProvider the Global provider for push notification. Support values: "GCM" and "FIREBASE" (required)
         */
        public LiDeviceIdFetchClientRequestParams(Context context, String deviceId, String pushNotificationProvider) {
            super(context);
            this.deviceId = deviceId;
            this.pushNotificationProvider = pushNotificationProvider;
            this.client = LiClientManager.Client.LI_DEVICE_ID_FETCH_CLIENT;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public LiDeviceIdFetchClientRequestParams setDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public String getPushNotificationProvider() {
            return pushNotificationProvider;
        }

        public LiDeviceIdFetchClientRequestParams setPushNotificationProvider(String pushNotificationProvider) {
            this.pushNotificationProvider = pushNotificationProvider;
            return this;
        }
    }

    //Request params class for LiDeviceIdUpdateClient
    public static class LiDeviceIdUpdateClientRequestParams extends LiClientRequestParams {
        private String deviceId;
        private String id;

        /**
         * Builds the parameters for {@link LiClientManager#getDeviceIdUpdateClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param deviceId the device ID registered with the push notification provider (required)
         * @param id the ID corresponding to device ID in the community (required)
         */
        public LiDeviceIdUpdateClientRequestParams(Context context, String deviceId, String id) {
            super(context);
            this.deviceId = deviceId;
            this.id = id;
            this.client = LiClientManager.Client.LI_DEVICE_ID_UPDATE_CLIENT;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public LiDeviceIdUpdateClientRequestParams setDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public String getId() {
            return id;
        }

        public LiDeviceIdUpdateClientRequestParams setId(String id) {
            this.id = id;
            return this;
        }
    }

    //Request params for LiSubscriptionPost client
    public static class LiPostSubscriptionParams extends LiClientRequestParams {

        private LiBaseModel target;
        private String targetId;
        private String targetType;
        /**
         * Builds the parameters for {@link LiClientManager#getSubscriptionPostClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param target the ID of the target of the subscription, either a message ID or a board ID (required)
         */
        public LiPostSubscriptionParams(Context context, LiBaseModel target) {
            super(context);
            this.target = target;
            this.client = LiClientManager.Client.LI_SUBSCRIPTION_POST_CLIENT;
        }
        /**
         * Builds the parameters for {@link LiClientManager#getSubscriptionPostClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param targetId the ID of the target of the subscription, either a message ID or a board ID (required)
         * @param targetType the type of the target of the subscription, either a "message" or a "board" (required)
         */
        public LiPostSubscriptionParams(Context context, String targetId, String targetType) {
            super(context);
            this.target = target;
            this.client = LiClientManager.Client.LI_SUBSCRIPTION_POST_CLIENT;
        }
        public LiBaseModel getTarget() {
            return target;
        }
        public String getTargetId() {
            return targetId;
        }

        public String getTargetType() {
            return targetType;
        }
    }

    //Request params for LiMarkMessagePost client
    public static class LiMarkMessageParams extends LiClientRequestParams {

        private String userId;
        private String messageId;
        private boolean markUnread;

        /**
         * Builds the parameters for {@link LiClientManager#getMarkMessagePostClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param userId the ID of the user marking the message as read or unread (required)
         * @param messageId the ID of the message being marked read or unread (required)
         * @param markUnread pass 'true' to mark the message as unread, pass 'false' to mark as read (required)
         */
        public LiMarkMessageParams(Context context, String userId, String messageId, boolean markUnread) {
            super(context);
            this.userId = userId;
            this.messageId = messageId;
            this.markUnread = markUnread;
            this.client = LiClientManager.Client.LI_MARK_MESSAGE_POST_CLIENT;
        }

        public String getUserId() {
            return userId;
        }

        public String getMessageId() {
            return messageId;
        }

        public boolean isMarkUnread() {
            return markUnread;
        }
    }

    //Request params for LiMarkMessagesPost client
    public static class LiMarkMessagesParams extends LiClientRequestParams {

        private String userId;
        private String messageIds;
        private boolean markUnread;

        /**
         * Builds the parameters for {@link LiClientManager#getMarkMessagesPostClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param userId the ID of the user marking the messages as read or unread (required)
         * @param messageIds the IDs of the messages being marked. Pass in a comma-separated list (required)
         * @param markUnread pass 'true' to mark the messages as unread, pass 'false' to mark as read (required)
         */
        public LiMarkMessagesParams(Context context, String userId, String messageIds, boolean markUnread) {
            super(context);
            this.userId = userId;
            this.messageIds = messageIds;
            this.markUnread = markUnread;
            this.client = LiClientManager.Client.LI_MARK_MESSAGES_POST_CLIENT;
        }

        public String getUserId() {
            return userId;
        }

        public String getMessageIds() {
            return messageIds;
        }

        public boolean isMarkUnread() {
            return markUnread;
        }
    }

    //Request params for LiMarkTopicPost client
    public static class LiMarkTopicParams extends LiClientRequestParams {

        private String userId;
        private String topicId;
        private boolean markUnread;

        /**
         * Builds the parameters for {@link LiClientManager#getMarkTopicPostClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param userId the ID of the user marking the messages as read or unread (required)
         * @param topicId the ID of the topic being marked (required)
         * @param markUnread pass 'true' to mark the topic message and all of its replies/comments as unread, pass 'false' to mark as read (required)
         */
        public LiMarkTopicParams(Context context, String userId, String topicId, boolean markUnread) {
            super(context);
            this.userId = userId;
            this.topicId = topicId;
            this.markUnread = markUnread;
            this.client = LiClientManager.Client.LI_MARK_TOPIC_POST_CLIENT;
        }

        public String getUserId() {
            return userId;
        }

        public String getTopicId() {
            return topicId;
        }

        public boolean isMarkUnread() {
            return markUnread;
        }
    }


    public static class LiDeleteSubscriptionParams extends LiClientRequestParams {

        private String subscriptionId;

        public LiDeleteSubscriptionParams(Context context, String subscriptionId) {
            super(context);
            this.subscriptionId = subscriptionId;
            this.client = LiClientManager.Client.LI_SUBSCRIPTION_DELETE_CLIENT;
        }
        public String getSubscriptionId() {
            return subscriptionId;
        }
    }

    //Request params class for LiCreateUserClient
    public static class LiCreateUserParams extends LiClientRequestParams {
        private String avatarUrl;
        private String avatarImageId;
        private String avatarExternal;
        private String avatarInternal;
        private String biography;
        private String coverImage;
        private String email;
        private String firstName;
        private String lastName;
        private String login;
        private String password;

        /**
         * Builds the parameters for {@link LiClientManager#getCreateUserClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param email the email address of the user being created (required)
         * @param login the login of the user being created (required)
         */
        public LiCreateUserParams(Context context, String email, String login) {
            super(context);
            this.email = email;
            this.login = login;
            this.client = LiClientManager.Client.LI_CREATE_USER_CLIENT;
        }

        public String getBiography() {
            return biography;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public LiCreateUserParams setBiography(String biography) {
            this.biography = biography;
            return this;
        }

        public LiCreateUserParams setCoverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public LiCreateUserParams setEmail(String email) {
            this.email = email;
            return this;
        }

        public LiCreateUserParams setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public LiCreateUserParams setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public LiCreateUserParams setLogin(String login) {
            this.login = login;
            return this;
        }

        public LiCreateUserParams setPassword(String password) {
            this.password = password;
            return this;
        }
        public String getAvatarUrl() {
            return avatarUrl;
        }

        public LiCreateUserParams setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public String getAvatarImageId() {
            return avatarImageId;
        }

        public LiCreateUserParams setAvatarImageId(String avatarImageId) {
            this.avatarImageId = avatarImageId;
            return this;
        }

        public String getAvatarExternal() {
            return avatarExternal;
        }

        public LiCreateUserParams setAvatarExternal(String avatarExternal) {
            this.avatarExternal = avatarExternal;
            return this;
        }

        public String getAvatarInternal() {
            return avatarInternal;
        }

        public LiCreateUserParams setAvatarInternal(String avatarInternal) {
            this.avatarInternal = avatarInternal;
            return this;
        }
    }

    //Request params class for LiUpdateUserClient
    public static class LiUpdateUserParams extends LiClientRequestParams {
        private String avatarUrl;
        private String avatarImageId;
        private String avatarExternal;
        private String avatarInternal;
        private String biography;
        private String coverImage;
        private String email;
        private String firstName;
        private String lastName;
        private String login;
        private String id;

        /**
         * Builds the parameters for {@link LiClientManager#getUpdateUserClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         */
        public LiUpdateUserParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_UPDATE_USER_CLIENT;
        }

        public String getBiography() {
            return biography;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getLogin() {
            return login;
        }

        public String getId() {
            return id;
        }

        public LiUpdateUserParams setId(String id) {
            this.id = id;
            return this;
        }

        public LiUpdateUserParams setBiography(String biography) {
            this.biography = biography;
            return this;
        }

        public LiUpdateUserParams setCoverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public LiUpdateUserParams setEmail(String email) {
            this.email = email;
            return this;
        }

        public LiUpdateUserParams setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public LiUpdateUserParams setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public LiUpdateUserParams setLogin(String login) {
            this.login = login;
            return this;
        }
        public String getAvatarUrl() {
            return avatarUrl;
        }

        public LiUpdateUserParams setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public String getAvatarImageId() {
            return avatarImageId;
        }

        public LiUpdateUserParams setAvatarImageId(String avatarImageId) {
            this.avatarImageId = avatarImageId;
            return this;
        }

        public String getAvatarExternal() {
            return avatarExternal;
        }

        public LiUpdateUserParams setAvatarExternal(String avatarExternal) {
            this.avatarExternal = avatarExternal;
            return this;
        }

        public String getAvatarInternal() {
            return avatarInternal;
        }

        public LiUpdateUserParams setAvatarInternal(String avatarInternal) {
            this.avatarInternal = avatarInternal;
            return this;
        }
    }

    public static class LiBeaconPostClientRequestParams extends LiClientRequestParams {
        private String targetType;
        private String targetId;

        /**
         * Builds the params for {@link LiClientManager#getBeaconClient(LiClientRequestParams)}
         * @param context Android context
         */
        public LiBeaconPostClientRequestParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_BEACON_CLIENT;
            this.targetId = targetId;
            this.targetType = targetType;
        }
        public String getTargetType() {
            return targetType;
        }

        public LiBeaconPostClientRequestParams setTargetType(String targetType) {
            this.targetType = targetType;
            return this;
        }

        public String getTargetId() {
            return targetId;
        }

        public LiBeaconPostClientRequestParams setTargetId(String targetId) {
            this.targetId = targetId;
            return this;
        }
    }

    //Request params class for LiGenericPostClient
    public static class LiGenericPostClientRequestParams extends LiClientRequestParams {
        private String path;
        private JsonElement requestBody;
        private Map<String, String> additionalHttpHeaders;

        /**
         * Builds the parameters for {@link LiClientManager#getGenericPostClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         * @param path the endpoint path. Begin path after the /community/2.0/<tenant_ID>/ portion of the URI. This first portion of the URL is generated automatically for you. For example, for the endpoint /community/2.0/<tenant_id>/messages, pass "messages"
         * @param requestBody a {@link JsonObject} representing the request body
         */
        public LiGenericPostClientRequestParams(Context context, String path, JsonElement requestBody, Map<String, String> additionalHttpHeaders) {
            super(context);
            this.path = path;
            this.requestBody = requestBody;
            this.client = LiClientManager.Client.LI_GENERIC_POST_CLIENT;
            this.additionalHttpHeaders = additionalHttpHeaders;
        }

        /**
         * Builds the parameters for {@link LiClientManager#getGenericPostClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         * @param path the endpoint path. Begin path after the /community/2.0/<tenant_ID>/ portion of the URI. This first portion of the URL is generated automatically for you. For example, for the endpoint /community/2.0/<tenant_id>/messages, pass "messages"
         * @param requestBody a {@link JsonObject} representing the request body
         */
        public LiGenericPostClientRequestParams(Context context, String path, JsonElement requestBody) {
            super(context);
            this.path = path;
            this.requestBody = requestBody;
            this.client = LiClientManager.Client.LI_GENERIC_POST_CLIENT;
        }

        public String getPath() {
            return path;
        }

        public LiGenericPostClientRequestParams setPath(String path) {
            this.path = path;
            return this;
        }

        public JsonElement getRequestBody() {
            return requestBody;
        }

        public LiGenericPostClientRequestParams setRequestBody(JsonObject requestBody) {
            this.requestBody = requestBody;
            return this;
        }
        public Map<String, String> getAdditionalHttpHeaders() {
            return additionalHttpHeaders;
        }

        public LiGenericPostClientRequestParams setAdditionalHttpHeaders(Map<String, String> additionalHttpHeaders) {
            this.additionalHttpHeaders = additionalHttpHeaders;
            return this;
        }
    }

    //Request params class for LiGenericPutClient
    public static class LiGenericPutClientRequestParams extends LiClientRequestParams {
        private String path;
        private JsonObject requestBody;
        private Map<String, String> additionalHttpHeaders;

        /**
         * Builds the parameters for {@link LiClientManager#getGenericPutClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         * @param path the endpoint path. Begin path after the /community/2.0/<tenant_ID>/ portion of the URI. This first portion of the URL is generated automatically for you. For example, for the endpoint /community/2.0/<tenant_id>/messages, pass "messages"
         * @param requestBody a {@link JsonObject} representing the request body
         */
        public LiGenericPutClientRequestParams(Context context, String path, JsonObject requestBody) {
            super(context);
            this.path = path;
            this.requestBody = requestBody;
            this.client = LiClientManager.Client.LI_GENERIC_PUT_CLIENT;
        }

        /**
         * Builds the parameters for {@link LiClientManager#getGenericPutClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         * @param path the endpoint path. Begin path after the /community/2.0/<tenant_ID>/ portion of the URI. This first portion of the URL is generated automatically for you. For example, for the endpoint /community/2.0/<tenant_id>/messages, pass "messages"
         * @param requestBody a {@link JsonObject} representing the request body
         */
        public LiGenericPutClientRequestParams(Context context, String path, JsonObject requestBody, Map<String, String> additionalHttpHeaders) {
            super(context);
            this.path = path;
            this.requestBody = requestBody;
            this.client = LiClientManager.Client.LI_GENERIC_PUT_CLIENT;
            this.additionalHttpHeaders = additionalHttpHeaders;
        }


        public String getPath() {
            return path;
        }

        public LiGenericPutClientRequestParams setPath(String path) {
            this.path = path;
            return this;
        }

        public JsonObject getRequestBody() {
            return requestBody;
        }

        public LiGenericPutClientRequestParams setRequestBody(JsonObject requestBody) {
            this.requestBody = requestBody;
            return this;
        }
        public Map<String, String> getAdditionalHttpHeaders() {
            return additionalHttpHeaders;
        }

        public LiGenericPutClientRequestParams setAdditionalHttpHeaders(Map<String, String> additionalHttpHeaders) {
            this.additionalHttpHeaders = additionalHttpHeaders;
            return this;
        }
    }
    //Request params class for LiGenericLiqlClient
    public static class LiGenericLiqlClientRequestParams extends LiClientRequestParams {
        private String liQuery;

        /**
         * Builds the parameters for {@link LiClientManager#getGenericLiqlGetClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         * @param liQuery the LiQL query to run, e.g. "SELECT subject, body FROM messages LIMIT 10"
         */
        public LiGenericLiqlClientRequestParams(Context context, String liQuery) {
            super(context);
            this.liQuery = liQuery;
            this.client = LiClientManager.Client.LI_GENERIC_LIQL_CLIENT;
        }

        public String getLiQuery() {
            return liQuery;
        }

        public LiGenericLiqlClientRequestParams setLiQuery(String liQuery) {
            this.liQuery = liQuery;
            return this;
        }
    }

    //Request params class for LiGenericNoLiqlClient
    public static class LiGenericNoLiqlClientRequestParams extends LiClientRequestParams {
        private String queryParams;
        private String pathParam;

        /**
         * Builds the parameters for {@link LiClientManager#getGenericLiqlGetClient(LiClientRequestParams)}.
         *
         * @param context the Android context
         * @param queryParams request query params for e.g. "method=post&board.id=someBoard"
         * @param pathParam path param other than "search" for e.g. "allowed" to check if certain action is allowed on a resource or not
         */
        public LiGenericNoLiqlClientRequestParams(Context context, String queryParams, String pathParam) {
            super(context);
            this.queryParams = queryParams;
            this.pathParam = pathParam;
            this.client = LiClientManager.Client.LI_GENERIC_LIQL_CLIENT;
        }

        public String getQueryParams() {
            return queryParams;
        }
        public String getPathParam() {
            return pathParam;
        }

        public LiGenericNoLiqlClientRequestParams setQueryParams(String queryParams) {
            this.queryParams = queryParams;
            return this;
        }
        public LiGenericNoLiqlClientRequestParams setPathParam(String pathParam) {
            this.pathParam = pathParam;
            return this;
        }
    }

    //Request params class for LiGenericQueryParamsClient
    public static class LiGenericQueryParamsClientRequestParams extends LiClientRequestParams {
        private LiQueryRequestParams liQueryRequestParams;

        /**
         * Builds the parameters for {@link LiClientManager#getGenericQueryParamsGetClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param liQueryRequestParams the customized WHERE, ORDER-BY, and LIMIT definitions for the API provider. Build 'liQueryRequestParams' with {@link LiQueryRequestParams#getBuilder()}. (required)
         */
        public LiGenericQueryParamsClientRequestParams(Context context, LiQueryRequestParams liQueryRequestParams) {
            super(context);
            this.liQueryRequestParams = liQueryRequestParams;
            this.client = LiClientManager.Client.LI_GENERIC_QUERY_PARAMS_CLIENT;
        }

        public LiQueryRequestParams getLiQueryRequestParams() {
            return liQueryRequestParams;
        }

        public LiGenericQueryParamsClientRequestParams setLiQueryRequestParams(LiQueryRequestParams liQueryRequestParams) {
            this.liQueryRequestParams = liQueryRequestParams;
            return this;
        }
    }

    //Request params class for LiGenericDeleteQueryParamsClient
    public static class LiGenericDeleteClientRequestParams extends LiClientRequestParams {
        private Map<String, String> liQueryRequestParams;
        private String id;
        private LiClientManager.CollectionsType collectionsType;
        //This will be appended after id in the delete url
        private String subResourcePath;

        /**
         * Builds the parameters for {@link LiClientManager#getGenericQueryDeleteClient(LiClientRequestParams)}.
         *
         * @param context the Android context (required)
         * @param collectionsType the collection type of the item being deleted (required)
         * @param id the ID of the item being deleted (required)
         */
        public LiGenericDeleteClientRequestParams(Context context, LiClientManager.CollectionsType collectionsType, String id) {
            super(context);
            this.id = id;
            this.collectionsType = collectionsType;
            this.client = LiClientManager.Client.LI_GENERIC_DELETE_QUERY_PARAMS_CLIENT;
        }

        public LiGenericDeleteClientRequestParams(Context context, LiClientManager.CollectionsType collectionsType, String id, Map<String, String> liQueryRequestParams) {
            this(context, collectionsType, id);
            this.liQueryRequestParams = liQueryRequestParams;
        }

        public LiGenericDeleteClientRequestParams(Context context, LiClientManager.CollectionsType collectionsType, String id, String subResourcePath) {
            this(context, collectionsType, id);
            this.subResourcePath = subResourcePath;
        }

        public LiGenericDeleteClientRequestParams(Context context, LiClientManager.CollectionsType collectionsType, String id, String subResourcePath, Map<String, String> liQueryRequestParams) {
            this(context, collectionsType, id, liQueryRequestParams);
            this.subResourcePath = subResourcePath;
        }


        public Map<String, String> getLiQueryRequestParams() {
            return liQueryRequestParams;
        }

        public String getId() {
            return id;
        }

        public LiClientManager.CollectionsType getCollectionsType() {
            return collectionsType;
        }

        public String getSubResourcePath() {
            return subResourcePath;
        }
    }
}

