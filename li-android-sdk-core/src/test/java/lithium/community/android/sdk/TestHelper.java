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

package lithium.community.android.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import lithium.community.android.sdk.auth.LiAppCredentials;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Contains common test values which are useful across all tests.
 */
public class TestHelper {


    public static final String TEST_CLIENT_NAME = "test";
    public static final String TEST_CLIENT_ID = "mj2gw0IYuoo33m0rKxuX4KpxUfsy7Q0rcBJhq34GHgs=";
    public static final String TEST_API_GATEWAY_HOST = "api.qa.aws.lcloud.com";
    public static final String TEST_TENANT_ID = "test";
    public static final String TEST_CLIENT_SECRET = "test_client_secret";
    public static final String TEST_COMMUNITY_URL = "http://community.lithium.com";
    public static final String DEFAULT_QUERY_SETTINGS = "{\n" +
            "  \"article\": {\n" +
            "    \"liDataSource\": \"messages\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"in\",\n" +
            "        \"key\": \"conversation.style\",\n" +
            "        \"value\": \"('forum', 'blog')\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"depth\",\n" +
            "        \"value\": \"##\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"ordering\": {\n" +
            "      \"key\": \"conversation.last_post_time\",\n" +
            "      \"type\": \"desc\"\n" +
            "    },\n" +
            "    \"limit\": \"50\"\n" +
            "  },\n" +
            "  \"test\": {\n" +
            "    \"liDataSource\": \"test\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"target.type\",\n" +
            "        \"value\": \"'test'\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"limit\": \"25\"\n" +
            "  },\n" +
            "  \"node\": {\n" +
            "    \"liDataSource\": \"nodes\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"in\",\n" +
            "        \"key\": \"conversation_style\",\n" +
            "        \"value\": \"('forum', 'blog')\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"parent.id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"and\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"limit\": \"25\"\n" +
            "  },\n" +
            "  \"node_depth\": {\n" +
            "    \"liDataSource\": \"nodes\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"in\",\n" +
            "        \"key\": \"conversation_style\",\n" +
            "        \"value\": \"('forum', 'blog')\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"depth\",\n" +
            "        \"value\": \"##\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"limit\": \"25\"\n" +
            "  },\n" +
            "  \"search\": {\n" +
            "    \"liDataSource\": \"messages\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"matches\",\n" +
            "        \"key\": \"body\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"OR\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"matches\",\n" +
            "        \"key\": \"subject\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"OR\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"matches\",\n" +
            "        \"key\": \"tags.text\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"OR\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"matches\",\n" +
            "        \"key\": \"labels.text\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"in\",\n" +
            "        \"key\": \"conversation.style\",\n" +
            "        \"value\": \"('forum', 'blog')\",\n" +
            "        \"operator\": \"OR\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"depth\",\n" +
            "        \"value\": \"0\",\n" +
            "        \"operator\": \"and\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"ordering\": {\n" +
            "      \"key\": \"conversation.last_post_time\",\n" +
            "      \"type\": \"desc\"\n" +
            "    },\n" +
            "    \"limit\": \"25\"\n" +
            "  },\n" +
            "  \"subscription\": {\n" +
            "    \"liDataSource\": \"subscriptions\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"target.type\",\n" +
            "        \"value\": \"'message'\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"limit\": \"25\"\n" +
            "  },\n" +
            "  \"message_children\": {\n" +
            "    \"liDataSource\": \"messages\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"topic.id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"ordering\": {\n" +
            "      \"key\": \"post_time\",\n" +
            "      \"type\": \"asc\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"questions\": {\n" +
            "    \"liDataSource\": \"messages\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"author.id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"and\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"depth\",\n" +
            "        \"value\": \"&&\",\n" +
            "        \"operator\": \"and\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"ordering\": {\n" +
            "      \"key\": \"conversation.last_post_time\",\n" +
            "      \"type\": \"desc\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"category\": {\n" +
            "    \"liDataSource\": \"nodes\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"node_type\",\n" +
            "        \"value\": \"'category'\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"article_browse\": {\n" +
            "    \"liDataSource\": \"messages\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"board.id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"depth\",\n" +
            "        \"value\": \"&&\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"ordering\": {\n" +
            "      \"key\": \"conversation.last_post_time\",\n" +
            "      \"type\": \"desc\"\n" +
            "    },\n" +
            "    \"limit\": \"25\"\n" +
            "  },\n" +
            "  \"message\": {\n" +
            "    \"liDataSource\": \"messages\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"user\": {\n" +
            "    \"liDataSource\": \"users\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"messages_by_ids\": {\n" +
            "    \"liDataSource\": \"messages\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"in\",\n" +
            "        \"key\": \"id\",\n" +
            "        \"value\": \"(##)\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"floated_message\": {\n" +
            "    \"liDataSource\": \"floated_message\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"message.board.id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"AND\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"scope\",\n" +
            "        \"value\": \"'&&'\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"app_sdk_setting\": {\n" +
            "    \"liDataSource\": \"app_sdk_settings\",\n" +
            "    \"whereClauses\": [\n" +
            "      {\n" +
            "        \"clause\": \"equals\",\n" +
            "        \"key\": \"client_id\",\n" +
            "        \"value\": \"'##'\",\n" +
            "        \"operator\": \"or\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    public static LiAppCredentials getTestAppCredentials() {
        return new LiAppCredentials.Builder()
                .setClientName(TEST_CLIENT_NAME)
                .setClientKey(TEST_CLIENT_ID)
                .setClientSecret(TEST_CLIENT_SECRET)
                .setCommunityUri(TEST_COMMUNITY_URL)
                .setApiGatewayUri(TEST_API_GATEWAY_HOST)
                .setTenantId(TEST_TENANT_ID)
                .build();
    }

    public static Context createMockContext() {

        final Map<String, String> map = new HashMap<>();

        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).then(new Answer<SharedPreferences.Editor>() {
            @Override
            public SharedPreferences.Editor answer(InvocationOnMock invocation) throws Throwable {
                String key = invocation.getArgumentAt(0, String.class);
                String value = invocation.getArgumentAt(1, String.class);
                map.put(key, value);
                return editor;
            }
        });
        when(editor.remove(anyString())).then(new Answer<SharedPreferences.Editor>() {
            @Override
            public SharedPreferences.Editor answer(InvocationOnMock invocation) throws Throwable {
                String key = invocation.getArgumentAt(0, String.class);
                map.remove(key);
                return editor;
            }
        });

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.getString(anyString(), anyString())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                String key = invocation.getArgumentAt(0, String.class);
                return map.get(key);
            }
        });
        when(preferences.edit()).thenReturn(editor);

        InputStream inputStream = new ByteArrayInputStream(DEFAULT_QUERY_SETTINGS.getBytes(StandardCharsets.UTF_8));

        Resources resource = mock(Resources.class);
        when(resource.getBoolean(anyInt())).thenReturn(true);
        when(resource.openRawResource(anyInt())).thenReturn(inputStream);

        Context context = mock(Context.class);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(preferences);
        when(context.getResources()).thenReturn(resource);

        return context;
    }
}
