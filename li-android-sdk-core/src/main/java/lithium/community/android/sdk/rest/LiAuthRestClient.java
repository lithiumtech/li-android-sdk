/*
 * LiAuthRestClient.java
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

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;

import lithium.community.android.sdk.auth.LiRefreshTokenRequest;
import lithium.community.android.sdk.auth.LiSSOAuthorizationRequest;
import lithium.community.android.sdk.auth.LiSSOTokenRequest;
import lithium.community.android.sdk.exception.LiRestResponseException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This client is used for catering to Authorization request.
 * Created by saiteja.tokala on 12/2/16.
 */

public class LiAuthRestClient {

    /**
     * Makes async call to fetch Auth Code.
     * @param ssoAuthorizationRequest {@link LiSSOAuthorizationRequest}
     * @param callback {@link LiAuthAsyncRequestCallback}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public void authorizeAsync(@NonNull final LiSSOAuthorizationRequest ssoAuthorizationRequest,
                               @NonNull final LiAuthAsyncRequestCallback callback) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(ssoAuthorizationRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(), RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(ssoAuthorizationRequest)));

        Request request = builder.build();

        OkHttpClient client = getOkHttpClient();

        getCall(request, client).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    callback.onSuccess(getLiBaseResponseFromResponse(response));
                } catch (LiRestResponseException e) {
                    throw new RuntimeException(e.getMessage());
                }

            }
        });
    }

    /**
     * Wrapping response to LiBaseResponse.
     * @param response {@link Response}
     * @return LiBaseResponse {@link LiBaseResponse}
     * @throws IOException {@link IOException}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @NonNull
    @VisibleForTesting
    LiBaseResponse getLiBaseResponseFromResponse(Response response) throws IOException, LiRestResponseException {
        return new LiBaseResponse(response);
    }

    /**
     * Returns OkHttp call.
     * @param request {@link Request}
     * @param client {@link OkHttpClient}
     * @return Call {@link Call}
     */
    @VisibleForTesting
    Call getCall(Request request, OkHttpClient client) {
        return client.newCall(request);
    }

    /**
     * Returns OkHttpClient.
     * @return OkHttpClient {@link OkHttpClient}
     */
    @NonNull
    @VisibleForTesting
    OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }

    /**
     * Makes Asyn call to fetch Tokens.
     * @param ssoTokenRequest {@link LiSSOTokenRequest}
     * @param callback {@link LiAuthAsyncRequestCallback}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public void accessTokenAsync(@NonNull final LiSSOTokenRequest ssoTokenRequest,
                                 @NonNull final LiAuthAsyncRequestCallback callback) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(ssoTokenRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(), RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(ssoTokenRequest)));

        builder.addHeader("client-id", ssoTokenRequest.getClientId());
        Request request = builder.build();

        OkHttpClient client = getOkHttpClient();

        getCall(request, client).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    callback.onSuccess(getLiBaseResponseFromResponse(response));
                } catch (LiRestResponseException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    /**
     * Makes Async call to fetch fresh Access token from Refresh Token.
     * @param refreshTokenRequest {@link LiRefreshTokenRequest}
     * @param callback {@link LiAuthAsyncRequestCallback}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public void refreshTokenAsync(@NonNull final LiRefreshTokenRequest refreshTokenRequest,
                                  @NonNull final LiAuthAsyncRequestCallback callback) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(refreshTokenRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(), RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(refreshTokenRequest)));

        builder.addHeader("client-id", refreshTokenRequest.getClientId());
        Request request = builder.build();

        OkHttpClient client = getOkHttpClient();

        getCall(request, client).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    callback.onSuccess(getLiBaseResponseFromResponse(response));
                } catch (LiRestResponseException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
    }

    /**
     * Makes Sync call to fetch fresh Access token from Refresh Token.
     * @param refreshTokenRequest {@link LiRefreshTokenRequest}
     * @return LiBaseResponse {@link LiBaseResponse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiBaseResponse refreshTokenSync(@NonNull final LiRefreshTokenRequest refreshTokenRequest) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(refreshTokenRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(), RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(refreshTokenRequest)));

        builder.addHeader("client-id", refreshTokenRequest.getClientId());
        Request request = builder.build();

        OkHttpClient client = getOkHttpClient();

        Response response = null;

        try {
            response = getCall(request, client).execute();
            return getLiBaseResponseFromResponse(response);
        } catch (IOException e) {
            Log.e("LiAuthRestClient", "Error making rest call", e);
            throw LiRestResponseException.networkError(e.getMessage());
        }

    }
}
