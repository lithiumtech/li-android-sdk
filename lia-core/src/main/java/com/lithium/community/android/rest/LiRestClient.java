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

package com.lithium.community.android.rest;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;
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
import com.lithium.community.android.R;
import com.lithium.community.android.auth.LiAuthConstants;
import com.lithium.community.android.auth.LiAuthServiceImpl;
import com.lithium.community.android.auth.LiTokenResponse;
import com.lithium.community.android.exception.LiInitializationException;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModelImpl;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKUtils;
import com.lithium.community.android.utils.LiImageUtils;
import com.lithium.community.android.utils.LiUriUtils;
import com.lithium.community.android.utils.MessageConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.lithium.community.android.auth.LiAuthConstants.LOG_TAG;
import static com.lithium.community.android.utils.LiCoreSDKConstants.HTTP_CODE_FORBIDDEN;
import static com.lithium.community.android.utils.LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL;
import static com.lithium.community.android.utils.LiCoreSDKConstants.HTTP_CODE_UNAUTHORIZED;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_LOG_TAG;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_VISIT_LAST_ISSUE_TIME_KEY;
import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_VISIT_ORIGIN_TIME_KEY;
import static com.lithium.community.android.utils.LiCoreSDKUtils.addLSIRequestHeaders;

/**
 * Base rest client. Provides all the generic REST implementation.
 */
public abstract class LiRestClient {

    public static final String TOKEN_REFRESH_TAG = "TOKEN_REFRESH_TAG";
    public static final int SERVER_TIMEOUT = 2000;

    private final Gson gson;

    private LiSDKManager sdkManager;
    private OkHttpClient httpClient;
    private Call currentNetworkCall;

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
        clientBuilder.retryOnConnectionFailure(false).interceptors().add(new RefreshAndRetryInterceptor(baseRestRequest.getContext(), sdkManager));
        Response response = null;

        try {
            currentNetworkCall = clientBuilder.build().newCall(request);
            response = currentNetworkCall.execute();
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
        clientBuilder.retryOnConnectionFailure(false);
        clientBuilder.interceptors().add(new RefreshAndRetryInterceptor(baseRestRequest.getContext(), sdkManager));
        currentNetworkCall = clientBuilder.build().newCall(request);
        currentNetworkCall.enqueue(new Callback() {
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

        Uri.Builder uriBuilder = sdkManager.getCredentials().getCommunityUri().buildUpon();
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
        OkHttpClient clientBuilder = new OkHttpClient.Builder().connectTimeout(SERVER_TIMEOUT, TimeUnit.SECONDS).build();
        currentNetworkCall = clientBuilder.newCall(request.build());
        currentNetworkCall.enqueue(new Callback() {
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
    private static class RefreshAndRetryInterceptor implements Interceptor {

        private static final int MAX_TRIES = 4; // One actual call + 3 Retries

        private WeakReference<Context> context;
        private LiSDKManager sdk;

        RefreshAndRetryInterceptor(Context context, LiSDKManager sdk) {
            this.context = new WeakReference<>(context);
            this.sdk = sdk;
        }

        @Override
        public Response intercept(final Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            int currentCount = 0;
            Response response = null;

            while (currentCount < MAX_TRIES
                    && (response == null
                    || ((response.code() != HTTP_CODE_SUCCESSFUL) && (response.code() != HttpURLConnection.HTTP_CREATED)))) {
                boolean proceed = false;

                if (response == null) {
                    proceed = true;
                } else if (response.code() != HTTP_CODE_SUCCESSFUL && response.code() != HttpURLConnection.HTTP_CREATED) {

                    try {
                        int httpCode = response.code();
                        String responseStr = "";
                        //Some server may specify retry-after
                        //1. either in http-date format, 'EEE, dd MM yyyy HH:mm:ss zzz', ex: 'Wed, 21 Jul 2018 07:28:00 GMT'
                        //2. or in seconds
                        String retryAfter = response.header("Retry-After");
                        long retryDuration = 0;
                        if (!TextUtils.isEmpty(retryAfter)) {
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                                Date d = format.parse(retryAfter);
                                retryDuration = d.getTime() - System.currentTimeMillis();
                                if (retryDuration < 0) {
                                    retryDuration = 0;
                                }
                            } catch (ParseException pe) {
                                try {
                                    retryDuration = Long.parseLong(retryAfter) * 1000; //seconds * milliseconds
                                } catch (NumberFormatException nfe) {
                                    retryDuration = 0;
                                }
                            }
                        }

                        if (retryDuration == 0) {
                            // if server didn't specify anything or specified something erroneous
                            retryDuration = (long) (Math.pow(2, currentCount - 1) * 2000);
                        }
                        try {
                            responseStr = response.body().string();
                        } catch (IllegalStateException ise) {
                            //This could happen for non-transient failures which are not happening below.
                            //unless retried, this could be read twice (second time on the closed stream) which leads to this exception.
                            responseStr = "";
                        }
                        synchronized (this) {
                            Thread.sleep(retryDuration);
                            // first time reaching this point, currentCount will always be 1, hence currentCount-1.
                            // so first time it waits for 2^0 = 2 seconds
                            // second time it waits for 2^1 = 4 seconds
                            // third time it waits for 2^2 = 8 seconds
                            // next time perhaps!, God help.
                        }

                        try {
                            JsonObject data = new Gson().fromJson(responseStr, JsonObject.class);
                            if (data != null && data.has("http_code")) {
                                httpCode = data.get("http_code").getAsInt();
                            }
                        } catch (JsonSyntaxException ex) {
                            Log.e(LI_LOG_TAG, "wrong json, not able to parse " + ex.getMessage());
                        }

                        switch (httpCode) {
                            //Not including 3XX codes as Lia doesn't handle re-directions

                            //The below auth error codes needs to check refresh tokens before retry.
                            case HTTP_CODE_UNAUTHORIZED:
                                //{@link HttpURLConnection.HTTP_UNAUTHORIZED} 401 - Unauthorized access, because of authentication.
                            case HTTP_CODE_FORBIDDEN: {
                                //{@link HttpURLConnection.HTTP_FORBIDDEN} 403 - Forbidden access, because of authentication again.

                                try {
                                    Context context = this.context.get();
                                    if (context != null) {
                                        LiTokenResponse liTokenResponse = new LiAuthServiceImpl(context, sdk).performSyncRefreshTokenRequest();
                                        sdk.persistAuthState(context, liTokenResponse);

                                        request = request.newBuilder().removeHeader(LiAuthConstants.AUTHORIZATION).build();
                                        request = request.newBuilder()
                                                .addHeader(LiAuthConstants.AUTHORIZATION, LiAuthConstants.BEARER + sdk.getAuthToken())
                                                .build();
                                        proceed = true;
                                    }
                                } catch (LiRestResponseException e) {
                                    Log.e(LOG_TAG, "Error making rest call for refresh token", e);
                                }
                                break;
                            }

                            //As described under https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
                            //Retries needs to be done for all the selected cases below -
                            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                                //{@link HttpURLConnection.HTTP_INTERNAL_ERROR} 500 - Internal server error, this could succeed for unsafe &
                                // non idempotent calls on retrying.
                                proceed = !((request.method() + "").equalsIgnoreCase("GET"));
                                break;

                            case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                                //{@link HttpURLConnection.HTTP_CLIENT_TIMEOUT} 408 - Client-Timeout, retry should be made
                            case HttpURLConnection.HTTP_BAD_GATEWAY:
                                //{@link HttpURLConnection.HTTP_BAD_GATEWAY} 502 - Bad Gateway, An auxiliary dependency could get solved
                            case HttpURLConnection.HTTP_UNAVAILABLE:
                                //{@link HttpURLConnection.HTTP_UNAVAILABLE} 503 - Currently unavailable, this is temporary denial of service
                            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                                //{@link HttpURLConnection.HTTP_GATEWAY_TIMEOUT} 504 - Gateway-Timeout, usually dependencies on Auxiliary services
                                proceed = true;
                                break;

                            default:
                                proceed = false;
                        }

                    } catch (InterruptedException e) {
                        proceed = false; // operation was incomplete do not proceed
                    }
                }

                if (proceed) {
                    Request requestWithUserAgent = request.newBuilder()
                            .header("User-Agent", System.getProperty("http.agent"))
                            .build();
                    response = chain.proceed(requestWithUserAgent);
                    currentCount++;
                } else {
                    break;
                }
            }
            return response;
        }
    }

    public void cancel() {
        if (currentNetworkCall != null && !currentNetworkCall.isCanceled()) {
            currentNetworkCall.cancel();
        }
    }
}