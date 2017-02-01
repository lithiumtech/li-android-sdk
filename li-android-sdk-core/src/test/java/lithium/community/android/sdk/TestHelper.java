/*
 * Copyright 2015 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lithium.community.android.sdk;

import android.app.Activity;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import lithium.community.android.sdk.auth.LiAppCredentials;
import lithium.community.android.sdk.client.manager.LiClientManager;

/**
 * Contains common test values which are useful across all tests.
 */
public class TestHelper {

    /**
     * For requesting an authorization code.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-3.1.1"> "The OAuth 2.0
     * Authorization Framework" (RFC 6749), Section 3.1.1</a>
     */
    public static final String CODE = "code";

    /**
     * For requesting an access token via an implicit grant.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-3.1.1"> "The OAuth 2.0
     * Authorization Framework" (RFC 6749), Section 3.1.1</a>
     */
    public static final String TOKEN = "token";

    /**
     * For requesting an OpenID Conenct ID Token.
     *
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#IDToken">
     * "OpenID Connect Core 1.0", Section 2</a>
     */
    public static final String ID_TOKEN = "id_token";

    public static final String APPLICATION_TYPE_NATIVE = "native";
    public static final String TEST_CLIENT_ID = "mj2gw0IYuoo33m0rKxuX4KpxUfsy7Q0rcBJhq34GHgs=";
    public static final String TEST_STATE = "$TAT3";
    public static final String TEST_APP_SCHEME = "com.test.app";
    public static final Uri TEST_APP_REDIRECT_URI = Uri.parse(TEST_APP_SCHEME + ":/oidc_callback");
    public static final String TEST_SCOPE = "openid email";
    public static final Uri TEST_IDP_AUTH_ENDPOINT =
            Uri.parse("https://testidp.example.com/authorize");
    public static final Uri TEST_IDP_TOKEN_ENDPOINT =
            Uri.parse("https://testidp.example.com/token");
    public static final Uri TEST_IDP_REGISTRATION_ENDPOINT =
            Uri.parse("https://testidp.example.com/token");

    public static final String TEST_CODE_VERIFIER = "0123456789_0123456789_0123456789_0123456789";
    public static final String TEST_AUTH_CODE = "zxcvbnmjk";
    public static final String TEST_ACCESS_TOKEN = "aaabbbccc";
    public static final Long TEST_ACCESS_TOKEN_EXPIRATION_TIME = 120000L; // two minutes
    public static final String TEST_ID_TOKEN = "abc.def.ghi";
    public static final String TEST_REFRESH_TOKEN = "asdfghjkl";

    public static final Long TEST_CLIENT_SECRET_EXPIRES_AT = 78L;
    public static final String TEST_CLIENT_SECRET = "test_client_secret";

    public static final String TEST_COMMUNITY_URL = "http://community.lithium.com";

    public static final String TEST_EMAIL_ADDRESS = "test@example.com";
    private static final String SSO_TOKEN = "sso_token";

    public static final String DEFAULT_QUERY_SETTINGS = "{\n" +
            "\t\"article\": {\n" +
            "\t\t\"liDataSource\": \"messages\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"in\",\n" +
            "\t\t\t\"key\": \"conversation.style\",\n" +
            "\t\t\t\"value\": \"('forum', 'blog')\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"depth\",\n" +
            "\t\t\t\"value\": \"##\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}],\n" +
            "\t\t\"ordering\": {\n" +
            "\t\t\t\"key\": \"conversation.last_post_time\",\n" +
            "\t\t\t\"type\": \"desc\"\n" +
            "\t\t},\n" +
            "\t\t\"limit\": \"50\"\n" +
            "\t},\n" +
            "\t\"test\": {\n" +
            "\t\t\"liDataSource\": \"test\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"target.type\",\n" +
            "\t\t\t\"value\": \"'test'\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}],\n" +
            "\t\t\"limit\": \"25\"\n" +
            "\t},\n" +
            "\t\"node\": {\n" +
            "\t\t\"liDataSource\": \"nodes\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"in\",\n" +
            "\t\t\t\"key\": \"conversation_style\",\n" +
            "\t\t\t\"value\": \"('forum', 'blog')\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"parent.id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"and\"\n" +
            "\t\t}],\n" +
            "\t\t\"limit\": \"25\"\n" +
            "\t},\n" +
            "\t\"search\": {\n" +
            "\t\t\"liDataSource\": \"messages\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"matches\",\n" +
            "\t\t\t\"key\": \"body\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"OR\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"matches\",\n" +
            "\t\t\t\"key\": \"subject\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"OR\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"matches\",\n" +
            "\t\t\t\"key\": \"tags.text\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"OR\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"matches\",\n" +
            "\t\t\t\"key\": \"labels.text\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"in\",\n" +
            "\t\t\t\"key\": \"conversation.style\",\n" +
            "\t\t\t\"value\": \"('forum', 'blog')\",\n" +
            "\t\t\t\"operator\": \"OR\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"depth\",\n" +
            "\t\t\t\"value\": \"0\",\n" +
            "\t\t\t\"operator\": \"and\"\n" +
            "\t\t}],\n" +
            "\t\t\"ordering\": {\n" +
            "\t\t\t\"key\": \"conversation.last_post_time\",\n" +
            "\t\t\t\"type\": \"desc\"\n" +
            "\t\t},\n" +
            "\t\t\"limit\": \"25\"\n" +
            "\t},\n" +
            "\t\"subscription\": {\n" +
            "\t\t\"liDataSource\": \"subscriptions\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"target.type\",\n" +
            "\t\t\t\"value\": \"'message'\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}],\n" +
            "\t\t\"limit\": \"25\"\n" +
            "\t},\n" +
            "\t\"message_children\": {\n" +
            "\t\t\"liDataSource\": \"messages\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"parent.id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}]\n" +
            "\t},\n" +
            "\t\"questions\": {\n" +
            "\t\t\"liDataSource\": \"messages\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"author.id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"and\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"depth\",\n" +
            "\t\t\t\"value\": \"&&\",\n" +
            "\t\t\t\"operator\": \"and\"\n" +
            "\t\t}],\n" +
            "\t\t\"ordering\": {\n" +
            "\t\t\t\"key\": \"conversation.last_post_time\",\n" +
            "\t\t\t\"type\": \"desc\"\n" +
            "\t\t}\n" +
            "\t},\n" +
            "\t\"category\": {\n" +
            "\t\t\"liDataSource\": \"nodes\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"node_type\",\n" +
            "\t\t\t\"value\": \"'category'\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}]\n" +
            "\t},\n" +
            "\t\"article_browse\": {\n" +
            "\t\t\"liDataSource\": \"messages\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"board.id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"depth\",\n" +
            "\t\t\t\"value\": \"&&\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}],\n" +
            "\t\t\"ordering\": {\n" +
            "\t\t\t\"key\": \"conversation.last_post_time\",\n" +
            "\t\t\t\"type\": \"desc\"\n" +
            "\t\t},\n" +
            "\t\t\"limit\": \"25\"\n" +
            "\t},\n" +
            "\t\"message\": {\n" +
            "\t\t\"liDataSource\": \"messages\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}]\n" +
            "\t},\n" +
            "\t\"user\": {\n" +
            "\t\t\"liDataSource\": \"users\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}]\n" +
            "\t},\n" +
            "\t\"messages_by_ids\": {\n" +
            "\t\t\"liDataSource\": \"messages\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"in\",\n" +
            "\t\t\t\"key\": \"id\",\n" +
            "\t\t\t\"value\": \"(##)\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}]\n" +
            "\t},\n" +
            "\t\"floated_message\": {\n" +
            "\t\t\"liDataSource\": \"floated_message\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"message.board.id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"AND\"\n" +
            "\t\t}, {\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"scope\",\n" +
            "\t\t\t\"value\": \"'&&'\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}]\n" +
            "\t},\n" +
            "\t\"app_sdk_setting\": {\n" +
            "\t\t\"liDataSource\": \"app_sdk_settings\",\n" +
            "\t\t\"whereClauses\": [{\n" +
            "\t\t\t\"clause\": \"equals\",\n" +
            "\t\t\t\"key\": \"client_id\",\n" +
            "\t\t\t\"value\": \"'##'\",\n" +
            "\t\t\t\"operator\": \"or\"\n" +
            "\t\t}]\n" +
            "\t}\n" +
            "}";

    public static LiAppCredentials getTestAppCredentials() throws MalformedURLException {
        return new LiAppCredentials.Builder().setClientKey(TEST_CLIENT_ID)
                .setClientSecret(TEST_CLIENT_SECRET)
                .setCommunityUri(TEST_COMMUNITY_URL)
                .setDeferredLogin(true).build();
    }

    public static LiAppCredentials getTestAppSSOCredentials() throws MalformedURLException {
        return new LiAppCredentials.Builder(SSO_TOKEN).setClientKey(TEST_CLIENT_ID)
                .setClientSecret(TEST_CLIENT_SECRET)
                .setCommunityUri(TEST_COMMUNITY_URL)
                .setDeferredLogin(true).build();
    }

    public static LiClientManager getTestLiaClientManager(Activity context) throws MalformedURLException, URISyntaxException {
        LiClientManager liClientManger = LiClientManager.init(context);
        LiClientManager instance = liClientManger.getInstance();
        return instance;
    }
}
