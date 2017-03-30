package lithium.community.android.sdk.model.request;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.model.helpers.LiAvatar;
import lithium.community.android.sdk.model.response.LiMessage;
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

        public LiMessagesClientRequestParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_MESSAGES_CLIENT;
        }

    }

    //Request params class for LiMessagesByBoardIdClient
    public static class LiMessagesByBoardIdClientRequestParams extends LiClientRequestParams {
        private String boardId;

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

        public LiUserSubscriptionsClientRequestParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_USER_SUBSCRIPTIONS_CLIENT;
        }

    }

    //Request params class for LiCategoryBoardsClient
    public static class LiCategoryBoardsClientRequestParams extends LiClientRequestParams {
        private String categoryId;

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

        public LiCategoryClientRequestParams(Context context) {
            super(context);
            this.client = LiClientManager.Client.LI_CATEGORY_CLIENT;
        }

    }

    //Request params class for LiUserDetailsClient
    public static class LiUserDetailsClientRequestParams extends LiClientRequestParams {
        private String userId;

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

    //Request params class for LiAcceptSolutionClient
    public static class LiAcceptSolutionClientRequestParams extends LiClientRequestParams {
        private Long messageId;

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

    //Request params class for LiCreateReplyClient
    public static class LiCreateReplyClientRequestParams extends LiClientRequestParams {
        private String body;
        private Long messageId;
        private String imageId;
        private String imageName;

        public LiCreateReplyClientRequestParams(Context context, String body, Long messageId, String imageId, String imageName) {
            super(context);
            this.body = body;
            this.messageId = messageId;
            this.imageId = imageId;
            this.imageName = imageName;
            this.client = LiClientManager.Client.LI_CREATE_REPLY_CLIENT;
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

    //Request params class for LiAcceptSolutionClient
    public static class LiMarkAbuseClientRequestParams extends LiClientRequestParams {
        private String messageId;
        private String userId;
        private String body;

        public LiMarkAbuseClientRequestParams(Context context, String messageId, String userId, String body) {
            super(context);
            this.messageId = messageId;
            this.userId = userId;
            this.body = body;
            this.client = LiClientManager.Client.LI_MARK_ABUSE_CLIENT;
        }

        public String getMessageId() {
            return messageId;
        }

        public LiMarkAbuseClientRequestParams setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public String getUserId() {
            return userId;
        }

        public LiMarkAbuseClientRequestParams setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public String getBody() {
            return body;
        }

        public LiMarkAbuseClientRequestParams setBody(String body) {
            this.body = body;
            return this;
        }
    }

    //Request params class for LiDeviceIdFetchClient
    public static class LiDeviceIdFetchClientRequestParams extends LiClientRequestParams {
        private String deviceId;
        private String pushNotificationProvider;

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

        private LiMessage target;

        public LiPostSubscriptionParams(Context context, LiMessage target) {
            super(context);
            this.target = target;
        }
        public LiMessage getTarget() {
            return target;
        }
    }

    public static class LiDeleteSubscriptionParams extends LiClientRequestParams {

        private String subscriptionId;

        public LiDeleteSubscriptionParams(Context context, String subscriptionId) {
            super(context);
            this.subscriptionId = subscriptionId;
        }
        public String getSubscriptionId() {
            return subscriptionId;
        }
    }

    //Request params class for LiCreateUserClient
    public static class LiCreateUserParams extends LiClientRequestParams {
        private LiAvatar avatar;
        private String biography;
        private String coverImage;
        private String email;
        private String firstName;
        private String lastName;
        private String login;
        private String password;

        public LiCreateUserParams(Context context, String email, String login) {
            super(context);
            this.email = email;
            this.login = login;
        }
        public LiAvatar getAvatar() {
            return avatar;
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

        public LiCreateUserParams setAvatar(LiAvatar avatar) {
            this.avatar = avatar;
            return this;
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
    }

    //Request params class for LiUpdateUserClient
    public static class LiUpdateUserParams extends LiClientRequestParams {
        private LiAvatar avatar;
        private String biography;
        private String coverImage;
        private String email;
        private String firstName;
        private String lastName;
        private String login;

        public LiUpdateUserParams(Context context) {
            super(context);
        }
        public LiAvatar getAvatar() {
            return avatar;
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


        public LiUpdateUserParams setAvatar(LiAvatar avatar) {
            this.avatar = avatar;
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
    }

    //Request params class for LiGenericPostClient
    public static class LiGenericPostClientRequestParams extends LiClientRequestParams {
        private String path;
        private JsonObject requestBody;

        public LiGenericPostClientRequestParams(Context context, String path, JsonObject requestBody) {
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

        public JsonObject getRequestBody() {
            return requestBody;
        }

        public LiGenericPostClientRequestParams setRequestBody(JsonObject requestBody) {
            this.requestBody = requestBody;
            return this;
        }
    }

    //Request params class for LiGenericLiqlClient
    public static class LiGenericLiqlClientRequestParams extends LiClientRequestParams {
        private String liQuery;

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

    //Request params class for LiGenericQueryParamsClient
    public static class LiGenericQueryParamsClientRequestParams extends LiClientRequestParams {
        private LiQueryRequestParams liQueryRequestParams;

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
}

