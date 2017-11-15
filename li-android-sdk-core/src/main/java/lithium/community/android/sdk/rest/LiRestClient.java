/*
 * LiRestClient.java
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

package lithium.community.android.sdk.rest;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lithium.community.android.sdk.R;
import lithium.community.android.sdk.auth.LiAuthConstants;
import lithium.community.android.sdk.auth.LiAuthServiceImpl;
import lithium.community.android.sdk.auth.LiTokenResponse;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;
import lithium.community.android.sdk.utils.LiCoreSDKUtils;
import lithium.community.android.sdk.utils.LiImageUtils;
import lithium.community.android.sdk.utils.LiUriUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;

import static lithium.community.android.sdk.auth.LiAuthConstants.APPLICATION_VERSION_HEADER_VALUE;
import static lithium.community.android.sdk.auth.LiAuthConstants.LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.HTTP_CODE_FORBIDDEN;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.HTTP_CODE_UNAUTHORIZED;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_LOG_TAG;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_VISIT_LAST_ISSUE_TIME_KEY;
import static lithium.community.android.sdk.utils.LiCoreSDKConstants.LI_VISIT_ORIGIN_TIME_KEY;

/**
 * Base rest client. Provides all the generic request response rest call implementation.
 */
public abstract class LiRestClient {

    public static final String TOKEN_REFRESH_TAG = "TOKEN_REFRESH_TAG";
    public static final int SERVER_TIMEOUT = 1000;
    private final Gson gson;
    private OkHttpClient httpClient;

    public LiRestClient() throws LiRestResponseException {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LiBaseModelImpl.LiDateInstant.class, new JsonDeserializer<LiBaseModelImpl.LiDateInstant>() {

            @Override
            public LiBaseModelImpl.LiDateInstant deserialize(JsonElement json, Type typeOfT,
                                                             JsonDeserializationContext context) throws JsonParseException {
                LiBaseModelImpl.LiDateInstant dateInstant = new LiBaseModelImpl.LiDateInstant();
                dateInstant.setValue(json.getAsString());
                return dateInstant;
            }
        }).registerTypeAdapter(LiBaseModelImpl.LiString.class, new JsonDeserializer<LiBaseModelImpl.LiString>() {
            @Override
            public LiBaseModelImpl.LiString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                LiBaseModelImpl.LiString liString = new LiBaseModelImpl.LiString();
                liString.setValue(json.getAsString());
                return liString;
            }
        }).registerTypeAdapter(LiBaseModelImpl.LiBoolean.class, new JsonDeserializer<LiBaseModelImpl.LiBoolean>() {
            @Override
            public LiBaseModelImpl.LiBoolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                LiBaseModelImpl.LiBoolean liBoolean = new LiBaseModelImpl.LiBoolean();
                liBoolean.setValue(json.getAsBoolean());
                return liBoolean;
            }
        }).registerTypeAdapter(LiBaseModelImpl.LiInt.class, new JsonDeserializer<LiBaseModelImpl.LiInt>() {
            @Override
            public LiBaseModelImpl.LiInt deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                LiBaseModelImpl.LiInt liBoolean = new LiBaseModelImpl.LiInt();
                liBoolean.setValue(json.getAsLong());
                return liBoolean;
            }
        });
        gson = gsonBuilder.create();
        ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_1, TlsVersion.TLS_1_2, TlsVersion.TLS_1_0, TlsVersion.SSL_3_0)
                .build();

        try {
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new X509TrustManager[]{
                    trustManager
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(connectionSpec))
                    .connectTimeout(SERVER_TIMEOUT, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustManager);
            this.httpClient = builder.build();
        } catch (Exception e) {
            throw LiRestResponseException.networkError(e.getMessage());
        }

    }

    public Gson getGson() {
        return gson;
    }

    /**
     * Makes Sync Network call.
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @return LiBaseResponse {@link LiBaseResponse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiBaseResponse processSync(@NonNull LiBaseRestRequest baseRestRequest)
            throws LiRestResponseException {
        LiCoreSDKUtils.checkNotNull(baseRestRequest);
        if (baseRestRequest.isAuthenticatedRequest() && LiSDKManager.getInstance().isUserLoggedIn()) {
            if (LiSDKManager.getInstance().getNeedsTokenRefresh()) {
                Log.d(TOKEN_REFRESH_TAG, "Refresh Token expired on device, fetching again: " + LiSDKManager.getInstance().getNewAuthToken());
                LiTokenResponse liTokenResponse = new LiAuthServiceImpl(baseRestRequest.getContext()).performSyncRefreshTokenRequest();
                LiSDKManager.getInstance().persistAuthState(
                        baseRestRequest.getContext(), liTokenResponse);

                Log.d(TOKEN_REFRESH_TAG, "Fetched new refresh token: " + LiSDKManager.getInstance().getNewAuthToken());

            }
        }

        Request request = buildRequest(baseRestRequest);
        OkHttpClient.Builder clientBuilder = getOkHttpClient().newBuilder();
        clientBuilder.interceptors().add(new RefreshAndRetryInterceptor(baseRestRequest.getContext()));
        Response response = null;

        try {
            response = clientBuilder.build().newCall(request).execute();
            if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                setVisitorTime(response, baseRestRequest);
                return new LiBaseResponse(response);
            } else {
                throw new LiRestResponseException(response.code(), "Error processing REST call", response.code());
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
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @param callback        {@link LiAsyncRequestCallback}
     */
    public void processAsync(@NonNull final LiBaseRestRequest baseRestRequest,
                             @NonNull final LiAsyncRequestCallback callback) {
        if (baseRestRequest.isAuthenticatedRequest() && LiSDKManager.getInstance().isUserLoggedIn()) {
            if (LiSDKManager.getInstance().getNeedsTokenRefresh()) {
                try {
                    Log.d(TOKEN_REFRESH_TAG, "Refresh Token expired on device, fetching again: " + LiSDKManager.getInstance().getNewAuthToken());
                    LiSDKManager.getInstance().fetchFreshAccessToken(
                            baseRestRequest.getContext(),
                            new LiAuthServiceImpl.FreshTokenCallBack() {
                                @Override
                                public void onFreshTokenFetched(boolean isFetched) {
                                    if (isFetched) {
                                        Log.d(TOKEN_REFRESH_TAG, "Fetched new refresh token: " + LiSDKManager.getInstance().getNewAuthToken());
                                        enqueueCall(baseRestRequest, callback);
                                    }
                                    else {
                                        callback.onError(LiRestResponseException.networkError("Could not refresh token"));
                                    }
                                }
                            });
                } catch (URISyntaxException | LiRestResponseException e) {
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
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                        setVisitorTime(response, baseRestRequest);
                        callback.onSuccess(baseRestRequest, new LiBaseResponse(response));
                    } else {
                        throw new LiRestResponseException(response.code(), "Error processing REST call", response.code());
                    }
                } catch (LiRestResponseException e) {
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
    public void uploadImageProcessAsync(@NonNull final LiBaseRestRequest baseRestRequest,
                                        @NonNull final LiAsyncRequestCallback callback, final String imagePath, final String imageName, final String requestBody) {
        if (baseRestRequest.isAuthenticatedRequest() && LiSDKManager.getInstance().isUserLoggedIn()) {
            if (LiSDKManager.getInstance().getNeedsTokenRefresh()) {
                try {
                    Log.d(TOKEN_REFRESH_TAG, "Refresh Token expired on device, fetching again: " + LiSDKManager.getInstance().getNewAuthToken());
                    LiSDKManager.getInstance().fetchFreshAccessToken(
                            baseRestRequest.getContext(),
                            new LiAuthServiceImpl.FreshTokenCallBack() {
                                @Override
                                public void onFreshTokenFetched(boolean isFetched) {
                                    Log.d(TOKEN_REFRESH_TAG, "Fetched new refresh token: " + LiSDKManager.getInstance().getNewAuthToken());
                                    uploadEnqueueCall(baseRestRequest, callback, imagePath, imageName, requestBody);
                                }
                            });
                } catch (URISyntaxException | LiRestResponseException e) {
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
    private void uploadEnqueueCall(@NonNull final LiBaseRestRequest baseRestRequest, @NonNull final LiAsyncRequestCallback callback,
                                   String imagePath, String imageName, String imgRequestBody) {

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
        JsonObject imgRequestBodyObject = getGson().fromJson(imgRequestBody, JsonObject.class);
        JsonObject dataObj = imgRequestBodyObject.get("nameValuePairs").getAsJsonObject().get("request").getAsJsonObject().get("data").getAsJsonObject();
        String requestBody = " {\"request\": {\"data\": {\"description\": "+dataObj.get("description")+",\"field\": \"image.content\",\"title\": \"" + imageName + "\",\"type\": \"image\",\"visibility\": \"public\"}}}";

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api.request", requestBody)
                .addFormDataPart("image.content", imageName, MultipartBody.create(MEDIA_TYPE, file))
                .addFormDataPart("payload", "")
                .build();
        Uri.Builder uriBuilder = new Uri.Builder().scheme("https");
        String proxyHost = LiSDKManager.getInstance().getProxyHost();
        Context context = baseRestRequest.getContext();
        uriBuilder.authority(proxyHost);
        uriBuilder.appendEncodedPath(baseRestRequest.getPath());
        if (baseRestRequest.getQueryParams() != null) {
            for (String param : baseRestRequest.getQueryParams().keySet()) {
                LiUriUtils.appendQueryParameterIfNotNull(uriBuilder, param,
                        baseRestRequest.getQueryParams().get(param));
            }
        }
        Request.Builder request = new Request.Builder();
        request.url(HttpUrl.get(URI.create(uriBuilder.build().toString())));
        request.post(multipartBody);
        request = buildRequestHeaders(context, request);

        final Map<String, String> additionalHttpHeaders = baseRestRequest.getAdditionalHttpHeaders();
        if (additionalHttpHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHttpHeaders.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        OkHttpClient clientBuilder = new OkHttpClient.Builder().connectTimeout(SERVER_TIMEOUT, TimeUnit.SECONDS).build();
        Call call = clientBuilder.newCall(request.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                        Log.i("Image response", response.body().toString());
                        callback.onSuccess(baseRestRequest, new LiBaseResponse(response));
                    } else {
                        throw new LiRestResponseException(response.code(), "Error processing REST call", response.code());
                    }
                } catch (LiRestResponseException e) {
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
     * @param response
     * @param baseRestRequest
     */
    private void setVisitorTime(Response response, @NonNull LiBaseRestRequest baseRestRequest) {
        String requestUrl = response.header("http.request");
        if (requestUrl != null && requestUrl.contains("/beacon") && response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
            String visitorLastIssueTime = response.header(LiRequestHeaderConstants.LI_REQUEST_VISIT_LAST_ISSUE_TIME);
            LiSDKManager.getInstance().putInSecuredPreferences(
                    baseRestRequest.getContext(), LI_VISIT_LAST_ISSUE_TIME_KEY, visitorLastIssueTime);
            String visitOriginTime = response.header(LiRequestHeaderConstants.LI_REQUEST_VISIT_ORIGIN_TIME);
            LiSDKManager.getInstance().putInSecuredPreferences(
                    baseRestRequest.getContext(), LI_VISIT_ORIGIN_TIME_KEY, visitOriginTime);
        }
    }

    /**
     * Helper to build okHttp Request from RestRequest
     *
     * @param baseRestRequest {@link LiBaseRestRequest}
     * @return final request that will be sent across network.
     */
    protected Request buildRequest(LiBaseRestRequest baseRestRequest) {
        Uri.Builder uriBuilder = new Uri.Builder().scheme("https");
        Context context = baseRestRequest.getContext();
        uriBuilder.authority(LiSDKManager.getInstance().getProxyHost());
        uriBuilder.appendEncodedPath(baseRestRequest.getPath());
        if (baseRestRequest.getQueryParams() != null) {
            for (String param : baseRestRequest.getQueryParams().keySet()) {
                uriBuilder.appendQueryParameter(param, baseRestRequest.getQueryParams().get(param));
            }
        }
        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(uriBuilder.build().toString())))
                .method(baseRestRequest.getMethod().toString(), baseRestRequest.getRequestBody());
        builder = buildRequestHeaders(context, builder);
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

        if (!TextUtils.isEmpty(LiSDKManager.getInstance().getNewAuthToken())) {
            requestBuilder.header(LiAuthConstants.AUTHORIZATION, LiAuthConstants.BEARER +
                    LiSDKManager.getInstance().getNewAuthToken());
        }
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_CONTENT_TYPE, "application/json");
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_CLIENT_ID, LiSDKManager.getInstance().getLiAppCredentials().getClientKey());
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_APPLICATION_IDENTIFIER,
                LiSDKManager.getInstance().getLiAppCredentials().getClientAppName());
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_APPLICATION_VERSION, APPLICATION_VERSION_HEADER_VALUE);
        requestBuilder.header(LiRequestHeaderConstants.LI_REQUEST_VISITOR_ID,
                LiSDKManager.getInstance().getFromSecuredPreferences(context, LiCoreSDKConstants.LI_VISITOR_ID));

        return requestBuilder;
    }

    @NonNull
    @Deprecated
    /**
     * request header key "lia-sdk-app-info"
     */
    private String buildLSIHeaderString(Context context) {
        JsonObject headerJson = new JsonObject();
        headerJson.addProperty("client_name",
                LiSDKManager.getInstance().getLiAppCredentials().getClientAppName());
        headerJson.addProperty("client_type", "android");
        headerJson.addProperty("client_id",
                LiSDKManager.getInstance().getLiAppCredentials().getClientKey());
        headerJson.addProperty("device_code", android.os.Build.DEVICE);
        headerJson.addProperty("device_model", android.os.Build.MODEL);
        headerJson.addProperty("device_id",
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        headerJson.addProperty("device_api_level", android.os.Build.VERSION.SDK_INT);
        String userId = null;
        if (LiSDKManager.getInstance().getLoggedInUser() != null) {
            userId = LiSDKManager.getInstance().getLoggedInUser().getLoginId();
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
                        if (data.has("statusCode")) {
                            httpCode = data.get("statusCode").getAsInt();
                        }
                    }
                    catch(JsonSyntaxException ex){
                        Log.e(LI_LOG_TAG, "wrong json, not able to parse "+ex.getMessage());
                    }
                    if (httpCode == HTTP_CODE_UNAUTHORIZED || httpCode == HTTP_CODE_FORBIDDEN) {
                        try {
                            LiTokenResponse liTokenResponse = new LiAuthServiceImpl(
                                    context).performSyncRefreshTokenRequest();
                            LiSDKManager.getInstance().persistAuthState(
                                    context, liTokenResponse);

                            LiSDKManager.getInstance().persistAuthState(
                                    context, liTokenResponse);

                            request = request.newBuilder().removeHeader(
                                    LiAuthConstants.AUTHORIZATION).build();

                            request = request.newBuilder().addHeader(
                                    LiAuthConstants.AUTHORIZATION, LiAuthConstants.BEARER +
                                            LiSDKManager.getInstance().getNewAuthToken()).build();
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
