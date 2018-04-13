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

package lithium.community.android.sdk.rest;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lithium.community.android.sdk.R;
import lithium.community.android.sdk.auth.LiAuthConstants;
import lithium.community.android.sdk.auth.LiAuthServiceImpl;
import lithium.community.android.sdk.auth.LiTokenResponse;
import lithium.community.android.sdk.exception.LiInitializationException;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiImageUtils;
import lithium.community.android.sdk.utils.LiUriUtils;
import lithium.community.android.sdk.utils.MessageConstants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static lithium.community.android.sdk.auth.LiAuthConstants.LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.HTTP_CODE_FORBIDDEN;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.HTTP_CODE_UNAUTHORIZED;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_VISIT_LAST_ISSUE_TIME_KEY;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_VISIT_ORIGIN_TIME_KEY;
import static lithium.community.android.sdk.utils.LiCoreSDKUtils.addLSIRequestHeaders;

/**
 * Base rest client. Provides all the generic REST implementation.
 */
public abstract class LiRestClient {

    public static final String TOKEN_REFRESH_TAG = "TOKEN_REFRESH_TAG";
    public static final int SERVER_TIMEOUT = 2000;

    private final Gson gson;

    private LiSDKManager sdkManager;
    private OkHttpClient httpClient;

    /**
     * Default public constructor.
     *
     * @param manager the Li SDK Manager
     * @throws LiInitializationException if Rest Client failed to initialize successfully.
     */
    public LiRestClient(@NonNull LiSDKManager manager) throws LiInitializationException {
        this.sdkManager = LiCoreSDKUtils.checkNotNull(manager, MessageConstants.wasNull("manager"));

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LiBaseModelImpl.LiDateInstant.class, new JsonDeserializer<LiBaseModelImpl.LiDateInstant>() {
            @Override
            public LiBaseModelImpl.LiDateInstant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                LiBaseModelImpl.LiDateInstant dateInstant = new LiBaseModelImpl.LiDateInstant();
                dateInstant.setValue(json.getAsString());
                return dateInstant;
            }
        });
        gsonBuilder.registerTypeAdapter(LiBaseModelImpl.LiString.class, new JsonDeserializer<LiBaseModelImpl.LiString>() {
            @Override
            public LiBaseModelImpl.LiString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
                liString.setValue(json.getAsString());
                return liString;
            }
        });
        gsonBuilder.registerTypeAdapter(LiBaseModelImpl.LiBoolean.class, new JsonDeserializer<LiBaseModelImpl.LiBoolean>() {
            @Override
            public LiBaseModelImpl.LiBoolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                LiBaseModelImpl.LiBoolean liBoolean = new LiBaseModelImpl.LiBoolean();
                liBoolean.setValue(json.getAsBoolean());
                return liBoolean;
            }
        });
        gsonBuilder.registerTypeAdapter(LiBaseModelImpl.LiInt.class, new JsonDeserializer<LiBaseModelImpl.LiInt>() {
            @Override
            public LiBaseModelImpl.LiInt deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                LiBaseModelImpl.LiInt liBoolean = new LiBaseModelImpl.LiInt();
                liBoolean.setValue(json.getAsLong());
                return liBoolean;
            }
        });

        gson = gsonBuilder.create();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(SERVER_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Deprecated public constructor.
     *
     * @throws LiInitializationException if Rest Client failed to initialize successfully.
     * @throws IllegalArgumentException  if the SDK is not initialized.
     * @deprecated Use {@link #LiRestClient(LiSDKManager)} instead
     */
    @Deprecated
    public LiRestClient() throws LiInitializationException {
        this(LiCoreSDKUtils.checkNotNull(LiSDKManager.getInstance(), MessageConstants.wasNull("Li SDK Manager")));
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * Makes Sync Network call.
     *
     * @param baseRestRequest A REST request.
     * @return LiBaseResponse A valid response.
     * @throws LiRestResponseException if the request failed or the response was invalid.
     */
    public LiBaseResponse processSync(@NonNull LiBaseRestRequest baseRestRequest) throws LiRestResponseException {
        LiCoreSDKUtils.checkNotNull(baseRestRequest, MessageConstants.wasNull("baseRestRequest"));
        if (baseRestRequest.isAuthenticatedRequest() && sdkManager.isUserLoggedIn()) {
            if (sdkManager.getNeedsTokenRefresh()) {
                Log.d(TOKEN_REFRESH_TAG, "Refresh Token expired on device, fetching again: " + sdkManager.getAuthToken());
                LiTokenResponse liTokenResponse = new LiAuthServiceImpl(baseRestRequest.getContext(), sdkManager).performSyncRefreshTokenRequest();
                sdkManager.persistAuthState(baseRestRequest.getContext(), liTokenResponse);
                Log.d(TOKEN_REFRESH_TAG, "Fetched new refresh token: " + sdkManager.getAuthToken());
            }
        }

        Request request = buildRequest(baseRestRequest);
        OkHttpClient.Builder clientBuilder = getOkHttpClient().newBuilder();
        clientBuilder.interceptors().add(new RefreshAndRetryInterceptor(baseRestRequest.getContext()));
        Response response = null;

        try {
            response = clientBuilder.build().newCall(request).execute();
            if (response != null) {
                if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                    setVisitorTime(response, baseRestRequest);
                    return new LiBaseResponse(response);
                } else {
                    throw new LiRestResponseException(response.code(), "Error processing REST call", response.code());
                }
            } else {
                throw new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR, "Error processing REST call",
                        LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making rest call", e);
            throw LiRestResponseException.networkError(e.getMessage());
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    /**
     * Makes Async Network call.
     *
     * @param baseRestRequest A REST request.
     * @param callback        A callback to be called once the request is completed.
     */
    public void processAsync(@NonNull final LiBaseRestRequest baseRestRequest, @NonNull final LiAsyncRequestCallback callback) {
        if (baseRestRequest.isAuthenticatedRequest() && sdkManager.isUserLoggedIn()) {
            if (sdkManager.getNeedsTokenRefresh()) {
                try {
                    Log.d(TOKEN_REFRESH_TAG, "Refresh Token expired on device, fetching again: " + sdkManager.getAuthToken());
                    sdkManager.fetchFreshAccessToken(baseRestRequest.getContext(), new LiAuthServiceImpl.FreshTokenCallBack() {
                        @Override
                        public void onFreshTokenFetched(boolean isFetched) {
                            if (isFetched) {
                                Log.d(TOKEN_REFRESH_TAG, "Fetched new refresh token: " + sdkManager.getAuthToken());
                                enqueueCall(baseRestRequest, callback);
                            } else {
                                callback.onError(LiRestResponseException.networkError("Could not refresh token"));
                            }
                        }
                    });
                } catch (LiRestResponseException e) {
                    callback.onError(e);
                }
            } else {
                enqueueCall(baseRestRequest, callback);
            }
        } else {
            enqueueCall(baseRestRequest, callback);
        }
    }

    /**
     * Makes network call.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callback        {@link LiAsyncRequestCallback}
     */
    private void enqueueCall(@NonNull final LiBaseRestRequest baseRestRequest, @NonNull final LiAsyncRequestCallback callback) {
        Request request = buildRequest(baseRestRequest);
        OkHttpClient.Builder clientBuilder = getOkHttpClient().newBuilder();
        clientBuilder.interceptors().add(new RefreshAndRetryInterceptor(baseRestRequest.getContext()));
        Call call = clientBuilder.build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {

                try {
                    if (response != null) {
                        if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                            setVisitorTime(response, baseRestRequest);
                            callback.onSuccess(baseRestRequest, new LiBaseResponse(response));
                        } else {
                            throw new LiRestResponseException(response.code(), "Error processing REST call", response.code());
                        }
                    } else {
                        throw new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR, "Error processing REST call",
                                LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
                    }
                } catch (Exception e) {
                    callback.onError(e);
                } finally {
                    if (response != null && response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    /**
     * Makes Async Call for uploading an image to the community.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callback        {@link LiAsyncRequestCallback}
     * @param imagePath       Absolute path of the image file.
     * @param imageName       Name of the image file.
     * @param requestBody     Request body of image upload API.
     */
    public void uploadImageProcessAsync(@NonNull final LiBaseRestRequest baseRestRequest, @NonNull final LiAsyncRequestCallback callback,
                                        final String imagePath, final String imageName, final String requestBody) {
        if (baseRestRequest.isAuthenticatedRequest() && sdkManager.isUserLoggedIn()) {
            if (sdkManager.getNeedsTokenRefresh()) {
                try {
                    Log.d(TOKEN_REFRESH_TAG, "Refresh Token expired on device, fetching again: " + sdkManager.getAuthToken());
                    sdkManager.fetchFreshAccessToken(baseRestRequest.getContext(), new LiAuthServiceImpl.FreshTokenCallBack() {
                        @Override
                        public void onFreshTokenFetched(boolean isFetched) {
                            Log.d(TOKEN_REFRESH_TAG, "Fetched new refresh token: " + sdkManager.getAuthToken());
                            uploadEnqueueCall(baseRestRequest, callback, imagePath, imageName, requestBody);
                        }
                    });
                } catch (LiRestResponseException e) {
                    callback.onError(e);
                }
            } else {
                uploadEnqueueCall(baseRestRequest, callback, imagePath, imageName, requestBody);
            }
        }
    }

    /**
     * Makes network call.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callback        {@link LiAsyncRequestCallback}
     * @param imagePath       Absolute path of the image file.
     * @param imageName       Name of the image file.
     * @param imgRequestBody  Request body of image upload API.
     */
    private void uploadEnqueueCall(@NonNull final LiBaseRestRequest baseRestRequest, @NonNull final LiAsyncRequestCallback callback, String imagePath,
                                   String imageName, String imgRequestBody) {

        final MediaType MEDIA_TYPE = MediaType.parse("image/*");
        File originalFile = new File(imagePath);
        final File file;
        final boolean isCompressed;
        if (originalFile.length() >= LiCoreSDKConstants.LI_MIN_IMAGE_SIZE_TO_COMPRESS) {
            isCompressed = true;
            Context context = baseRestRequest.getContext();
            int imageCompressionSize = context.getResources().getDimensionPixelSize(R.dimen.li_image_compression_size);
            int imageQuality = context.getResources().getInteger(R.integer.li_image_compression_quality);
            file = LiImageUtils.compressImage(imagePath, imageName, context, imageCompressionSize, imageCompressionSize, imageQuality);
        } else {
            isCompressed = false;
            file = new File(imagePath);
        }

        Uri.Builder uriBuilder = new Uri.Builder().scheme("https");
        String proxyHost = sdkManager.getApiGatewayHost();

        uriBuilder.authority(proxyHost);
        uriBuilder.appendEncodedPath(baseRestRequest.getPath());
        if (baseRestRequest.getQueryParams() != null) {
            for (String param : baseRestRequest.getQueryParams().keySet()) {
                LiUriUtils.appendQueryParameterIfNotNull(uriBuilder, param, baseRestRequest.getQueryParams().get(param));
            }
        }

        JsonObject imgRequestBodyObject = getGson().fromJson(imgRequestBody, JsonObject.class);
        JsonObject dataObj = imgRequestBodyObject.get("nameValuePairs").getAsJsonObject().get("request").getAsJsonObject().get("data").getAsJsonObject();
        String requestBody = " {\"request\": {\"data\": {\"description\": " + dataObj.get("description")
                + ",\"field\": \"image.content\",\"title\": \"" + imageName
                + "\",\"type\": \"image\",\"visibility\": \"public\"}}}";

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api.request", requestBody)
                .addFormDataPart("image.content", imageName, MultipartBody.create(MEDIA_TYPE, file))
                .addFormDataPart("payload", "")
                .build();

        Request.Builder request = new Request.Builder();
        request.url(HttpUrl.get(URI.create(uriBuilder.build().toString())));
        request.post(multipartBody);
        request = buildRequestHeaders(baseRestRequest.getContext(), request);

        final Map<String, String> additionalHttpHeaders = baseRestRequest.getAdditionalHttpHeaders();
        if (additionalHttpHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHttpHeaders.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        request.header(LiRequestHeaderConstants.LI_REQUEST_AUTH_SERVICE_KEY, LiRequestHeaderConstants.LI_REQUEST_AUTH_SERVICE_VALUE);
        OkHttpClient clientBuilder = new OkHttpClient.Builder().connectTimeout(SERVER_TIMEOUT, TimeUnit.SECONDS).build();
        Call call = clientBuilder.newCall(request.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) {

                try {
                    if (response != null) {
                        if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                            Log.i("Image response", response.body().toString());
                            callback.onSuccess(baseRestRequest, new LiBaseResponse(response));
                        } else {
                            throw new LiRestResponseException(response.code(), "Error processing REST call", response.code());
                        }
                    } else {
                        throw new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR, "Error processing REST call",
                                LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
                    }
                } catch (Exception e) {
                    callback.onError(e);
                } finally {
                    if (response != null && response.body() != null) {
                        response.body().close();
                    }
                    if (isCompressed) {
                        boolean isDeleted = file.delete();
                        Log.i("Image", isDeleted + "");
                    }
                }
            }
        });

    }

    /**
     * This method checks the response if it is a response from beacon API and a HTTP 200.
     * If it is then it saves the visitor origin time and last issue time in shared preferences
     *
     * @param response
     * @param baseRestRequest
     */
    private void setVisitorTime(Response response, @NonNull LiBaseRestRequest baseRestRequest) {
        String requestUrl = response.header("http.request");
        if (requestUrl != null && requestUrl.contains("/beacon") && response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
            String visitorLastIssueTime = response.header(LiRequestHeaderConstants.LI_REQUEST_VISIT_LAST_ISSUE_TIME);
            sdkManager.putInSecuredPreferences(baseRestRequest.getContext(), LI_VISIT_LAST_ISSUE_TIME_KEY, visitorLastIssueTime);
            String visitOriginTime = response.header(LiRequestHeaderConstants.LI_REQUEST_VISIT_ORIGIN_TIME);
            sdkManager.putInSecuredPreferences(baseRestRequest.getContext(), LI_VISIT_ORIGIN_TIME_KEY, visitOriginTime);
        }
    }

    /**
     * Helper to build okHttp Request from RestRequest
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @return final request that will be sent across network.
     */
    protected Request buildRequest(LiBaseRestRequest baseRestRequest) {
        Uri.Builder uriBuilder = sdkManager.getCredentials().getCommunityUri().buildUpon();
        uriBuilder.appendEncodedPath(baseRestRequest.getPath());
        if (baseRestRequest.getQueryParams() != null) {
            for (String param : baseRestRequest.getQueryParams().keySet()) {
                uriBuilder.appendQueryParameter(param, baseRestRequest.getQueryParams().get(param));
            }
        }
        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(uriBuilder.build().toString())))
                .method(baseRestRequest.getMethod().toString(), baseRestRequest.getRequestBody());
        builder = buildRequestHeaders(baseRestRequest.getContext(), builder);
        // Adding addition headers
        final Map<String, String> additionalHttpHeaders = baseRestRequest.getAdditionalHttpHeaders();
        if (additionalHttpHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHttpHeaders.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return builder.build();
    }

    @NonNull
    private Request.Builder buildRequestHeaders(Context context, Request.Builder requestBuilder) {

        if (!TextUtils.isEmpty(sdkManager.getAuthToken())) {
            requestBuilder.header(LiAuthConstants.AUTHORIZATION, LiAuthConstants.BEARER + sdkManager.getAuthToken());
        }
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_CONTENT_TYPE, "application/json");
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_CLIENT_ID, sdkManager.getCredentials().getClientKey());
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_AUTH_SERVICE_KEY, LiRequestHeaderConstants.LI_REQUEST_AUTH_SERVICE_VALUE);
        addLSIRequestHeaders(context, requestBuilder);

        return requestBuilder;
    }

    /**
     * request header key "lia-sdk-app-info"
     */
    @NonNull
    @Deprecated
    private String buildLSIHeaderString(Context context) {
        JsonObject headerJson = new JsonObject();
        headerJson.addProperty("client_name", sdkManager.getCredentials().getClientName());
        headerJson.addProperty("client_type", "android");
        headerJson.addProperty("client_id", sdkManager.getCredentials().getClientKey());
        headerJson.addProperty("device_code", android.os.Build.DEVICE);
        headerJson.addProperty("device_model", android.os.Build.MODEL);
        headerJson.addProperty("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        headerJson.addProperty("device_api_level", android.os.Build.VERSION.SDK_INT);
        String userId = null;
        if (sdkManager.getLoggedInUser() != null) {
            userId = sdkManager.getLoggedInUser().getLoginId();
        }
        headerJson.addProperty("user_id", userId);
        return headerJson.toString();
    }

    public String asString(LiBaseResponse response) throws IOException {
        byte[] bytes = asBytes(response); // will also compute responseCharSet
        String responseAsString = new String(bytes);
        return responseAsString;
    }

    /**
     * @return byte[] for entire response
     * @throws IOException
     */
    public byte[] asBytes(LiBaseResponse response) throws IOException {
        return consume(response);
    }

    public JSONObject asJSONObject(LiBaseResponse response) throws JSONException, IOException {
        return new JSONObject(asString(response));
    }

    /**
     * Fully consume response entity content and closes content stream
     * Must be called before returning control to the UI thread
     *
     * @throws IOException
     */
    public byte[] consume(LiBaseResponse response) throws IOException {
        byte[] responseAsBytes = null;
        if (response != null) {
            JsonObject body = response.getData();
            if (body != null) {
                responseAsBytes = body.toString().getBytes(Charset.forName("UTF-8"));
            } else {
                responseAsBytes = new byte[0];
            }
        }
        return responseAsBytes;
    }

    /**
     * Returns okHttp Client
     */
    private OkHttpClient getOkHttpClient() {
        return httpClient;
    }

    /**
     * Interceptor to intercept if Token has expired. It then fetches new token and re tries.
     */
    private class RefreshAndRetryInterceptor implements Interceptor {
        private int maxTries = 2;
        private Context context;

        public RefreshAndRetryInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(final Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            int currentCount = 0;
            Response response = null;
            while (currentCount < maxTries
                    && (response == null || (response.code() != HTTP_CODE_SUCCESSFUL))) {
                boolean proceed = false;
                if (response == null) {
                    proceed = true;
                } else if (response.code() != HTTP_CODE_SUCCESSFUL) {
                    int httpCode = response.code();
                    JsonObject data;
                    String responseStr = response.body().string();
                    try {
                        data = new Gson().fromJson(responseStr, JsonObject.class);
                        if (data != null && data.has("statusCode")) {
                            httpCode = data.get("statusCode").getAsInt();
                        }
                    } catch (JsonSyntaxException ex) {
                        Log.e(LI_LOG_TAG, "wrong json, not able to parse " + ex.getMessage());
                    }
                    if (httpCode == HTTP_CODE_UNAUTHORIZED || httpCode == HTTP_CODE_FORBIDDEN) {
                        try {
                            LiTokenResponse liTokenResponse = new LiAuthServiceImpl(context, sdkManager).performSyncRefreshTokenRequest();
                            sdkManager.persistAuthState(context, liTokenResponse);

                            request = request.newBuilder().removeHeader(LiAuthConstants.AUTHORIZATION).build();

                            request = request.newBuilder().addHeader(LiAuthConstants.AUTHORIZATION, LiAuthConstants.BEARER + sdkManager.getAuthToken())
                                    .build();
                        } catch (LiRestResponseException e) {
                            Log.e(LOG_TAG, "Error making rest call for refresh token", e);
                        }
                    }
                    proceed = true;
                }

                if (proceed) {
                    response = chain.proceed(request);
                    currentCount++;
                }
            }
            return response;
        }
    }
}
