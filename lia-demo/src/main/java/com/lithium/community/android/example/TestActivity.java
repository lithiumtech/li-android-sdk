package com.lithium.community.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiDeleteClientResponse;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.rest.LiPostClientResponse;
import com.lithium.community.android.rest.LiPutClientResponse;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity implements ListView.OnItemClickListener {

    private GetCallback getCallback = new GetCallback();
    private PutCallback putCallback = new PutCallback();
    private PostCallback postCallback = new PostCallback();
    private BrowseCallback browseCallback = new BrowseCallback();
    private static List<String> providers = new ArrayList<>();

    static {
        try {
            providers.clear();
            Class clazz = Class.forName(LiClientManager.class.getName());
            if (clazz.getName().equalsIgnoreCase(LiClientManager.class.getName())) {
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getReturnType().equals(LiClient.class)) {
                        providers.add(method.getName());
                    }
                }
            }
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        ListView list = findViewById(R.id.list);
        list.setOnItemClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, providers);
        list.setAdapter(adapter);
        /*findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiCategoryClientRequestParams(getBaseContext());
                try {
                    LiClient client = LiClientManager.getCategoryClient(liClientRequestParams);

                    client.processAsync(getCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
        });

        getProviders();

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("body", "This is an edited reply body");
                requestBody.addProperty("type", "message");
                requestBody.addProperty("Id", "64");
                LiClientRequestParams liClientRequestParams =
                        new LiClientRequestParams.LiGenericPutClientRequestParams(getBaseContext(), "messages", requestBody);
                LiClient genericPutClient = null;
                try {
                    genericPutClient = LiClientManager.getGenericPutClient(liClientRequestParams);
                    genericPutClient.processAsync(putCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject parent = new JsonObject();
                parent.addProperty("id", 64);
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("body", "This is an edited reply body");
                requestBody.add("parent", parent);
                requestBody.addProperty("subject", "fdf");
                requestBody.addProperty("type", "message");
                JsonObject requestBodyReply = new JsonObject();
                requestBodyReply.add("data", requestBody);
                LiClientRequestParams liClientRequestParams =
                        new LiClientRequestParams.LiGenericPostClientRequestParams(getBaseContext(), "messages", requestBodyReply );
                LiClient genericPostClient = null;
                try {
                    genericPostClient = LiClientManager.getGenericPostClient(liClientRequestParams);
                    genericPostClient.processAsync(postCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "" + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createMessage() {
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams
                .LiCreateMessageClientRequestParams(
                getBaseContext(), "Automated message Delete",
                "message posted body and delete", "testBoard", "null",
                "null");
        LiClient createMessageClient = null;

        try {
            createMessageClient = LiClientManager.getCreateMessageClient(liClientRequestParams);
            createMessageClient.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                        deleteMessage(response.getResponse().getData().getAsJsonObject("data").get("id").getAsString());
                        showToast("Message deleted");
                    }
                }

                @Override
                public void onError(Exception exception) {
                        showToast("" + exception.getMessage());
                }
            });
        } catch (LiRestResponseException e) {
            e.printStackTrace();
        }
    }

    private void editMessage(String id) {
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams
                .LiUpdateMessageClientRequestParams(
                getBaseContext(), id, "Edit subject",
                "message posted body and delete");
        LiClient createMessageClient = null;

        try {
            createMessageClient = LiClientManager.getUpdateMessageClient(liClientRequestParams);
            createMessageClient.processAsync(new LiAsyncRequestCallback<LiPutClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiPutClientResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                        response.getResponse().getData().get("id");
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } catch (LiRestResponseException e) {
            e.printStackTrace();
        }
    }

    private void getMessage(long id) {
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessageClientRequestParams(
                getBaseContext(), id);
        LiClient createMessageClient = null;

        try {
            createMessageClient = LiClientManager.getMessageClient(liClientRequestParams);
            createMessageClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                        showToast("" + response.getJsonObject());
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        } catch (LiRestResponseException e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(String id) {
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessageDeleteClientRequestParams(
                getBaseContext(), id);
        LiClient createMessageClient = null;

        try {
            createMessageClient = LiClientManager.getMessageDeleteClient(liClientRequestParams);
            createMessageClient.processAsync(new LiAsyncRequestCallback<LiDeleteClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiDeleteClientResponse response) throws LiRestResponseException {
                    if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                        showToast("" + response.getJsonObject());
                    }
                }

                @Override
                public void onError(Exception exception) {
                    showToast("" + exception.getMessage());
                }
            });
        } catch (LiRestResponseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final String method = providers.get(i);
        Toast.makeText(this, method, Toast.LENGTH_SHORT).show();
        switch (method) {
            case "getGenericPostClient" : {
                JsonObject parent = new JsonObject();
                parent.addProperty("id", 64);
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("body", "This is an edited reply body");
                requestBody.add("parent", parent);
                requestBody.addProperty("subject", "fdf");
                requestBody.addProperty("type", "message");
                JsonObject requestBodyReply = new JsonObject();
                requestBodyReply.add("data", requestBody);
                LiClientRequestParams liClientRequestParams =
                        new LiClientRequestParams.LiGenericPostClientRequestParams(getBaseContext(), "messages", requestBodyReply);
                LiClient genericPostClient = null;
                try {
                    genericPostClient = LiClientManager.getGenericPostClient(liClientRequestParams);
                    genericPostClient.processAsync(postCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            case "getGenericPutClient" : {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("body", "This is an edited reply body");
                requestBody.addProperty("type", "message");
                requestBody.addProperty("Id", "64");
                LiClientRequestParams liClientRequestParams =
                        new LiClientRequestParams.LiGenericPutClientRequestParams(getBaseContext(), "messages", requestBody);
                LiClient genericPutClient = null;
                try {
                    genericPutClient = LiClientManager.getGenericPutClient(liClientRequestParams);
                    genericPutClient.processAsync(putCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            case "getCategoryClient" : {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiCategoryClientRequestParams(getBaseContext());
                try {
                    LiClient client = LiClientManager.getCategoryClient(liClientRequestParams);

                    client.processAsync(getCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            case "getBoardsByDepthClient" : {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiBoardsByDepthClientRequestParams(getBaseContext(), 2);
                LiClient browseClient = null;
                try {
                    browseClient = LiClientManager.getBoardsByDepthClient(liClientRequestParams);
                    browseClient.processAsync(browseCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            case "getCategoryBoardsClient" : {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiCategoryBoardsClientRequestParams(getBaseContext(), "testCat");
                LiClient browseClient = null;
                try {
                    browseClient = LiClientManager.getCategoryBoardsClient(liClientRequestParams);
                    browseClient.processAsync(browseCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }

            }
                break;
            case "getCreateMessageClient" : {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams
                        .LiCreateMessageClientRequestParams(
                                getBaseContext(),"TestAutomationPost",
                                "message posted body", "testBoard", "null",
                                "null");
                LiClient createMessageClient = null;
                try {
                    createMessageClient = LiClientManager.getCreateMessageClient(liClientRequestParams);
                    createMessageClient.processAsync(postCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            case "getCreateReplyClient" : {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams
                        .LiCreateReplyClientRequestParams(getBaseContext(),"ufjc","Automation Reply", (long) 70,"","");
                LiClient createReplyClient = null;
                try {
                    createReplyClient = LiClientManager.getCreateReplyClient(liClientRequestParams);
                    createReplyClient.processAsync(postCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            case "getFloatedMessagesClient" : {  /* needs work */
                //LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiFloatedMessagesClientRequestParams(getBaseContext(),);
            }
                break;
            case "getMessageClient" : {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessageClientRequestParams(getBaseContext(),(long) 64);
                LiClient getMessageClient = null;
                try {
                    getMessageClient = LiClientManager.getMessageClient(liClientRequestParams);
                    getMessageClient.processAsync(getCallback);
                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            case "getMessageDeleteClient" : {
                createMessage();
               // postMessageClient.get
               // LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessageDeleteClientRequestParams(getBaseContext(),)
            }
                break;
            case "getMessagesByBoardIdClient" : {
                LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessagesByBoardIdClientRequestParams(getBaseContext(),"testBoard");
                LiClient getMessageByBoardID = null;

                try {
                    getMessageByBoardID = LiClientManager.getMessagesByBoardIdClient(liClientRequestParams);
                    getMessageByBoardID.processAsync(getCallback);

                } catch (LiRestResponseException e) {
                    e.printStackTrace();
                }
            }
                break;
            default:
        }
    }

    private class GetCallback implements LiAsyncRequestCallback<LiGetClientResponse> {

        @Override
        public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                showToast("" + response.getJsonObject());
            } else {
                showToast("" + response.getJsonObject());
            }
        }

        @Override
        public void onError(Exception exception) {
            showToast("" + exception.getMessage());
        }
    }

    private class PutCallback implements LiAsyncRequestCallback<LiPutClientResponse> {
        @Override
        public void onSuccess(LiBaseRestRequest request, LiPutClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                showToast("" + response.getJsonObject());
            } else {
                showToast("" + response.getJsonObject());
            }
        }

        @Override
        public void onError(Exception exception) {
            showToast("" + exception.getMessage());
        }
    }

    private class PostCallback implements LiAsyncRequestCallback<LiPostClientResponse> {

        @Override
        public void onSuccess(LiBaseRestRequest request, LiPostClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                showToast("" + response.getJsonObject());
            } else {
                showToast("" + response.getJsonObject());
            }
        }

        @Override
        public void onError(Exception exception) {
            showToast("" + exception.getMessage());
        }
    }

    private class BrowseCallback implements LiAsyncRequestCallback<LiGetClientResponse> {

        @Override
        public void onSuccess(LiBaseRestRequest request, LiGetClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                showToast("" + response.getJsonObject());
                showToast("" + response.getResponse());
            } else {
                showToast("" + response.getJsonObject());
            }
        }

        @Override
        public void onError(Exception exception) {

        }
    }

    private class DeleteCallback implements LiAsyncRequestCallback<LiDeleteClientResponse> {

        @Override
        public void onSuccess(LiBaseRestRequest request, LiDeleteClientResponse response) throws LiRestResponseException {
            if (response.getHttpCode() == HttpURLConnection.HTTP_OK) {
                showToast("" + response.getJsonObject());
            } else {
                showToast("" + response.getJsonObject());
            }
        }

        @Override
        public void onError(Exception exception) {
            showToast("" + exception.getMessage());
        }
    }

}



