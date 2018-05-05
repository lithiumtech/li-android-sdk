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
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.Gson;
import com.lithium.community.android.auth.LiRefreshTokenRequest;
import com.lithium.community.android.auth.LiSSOAuthorizationRequest;
import com.lithium.community.android.auth.LiSSOTokenRequest;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.io.IOException;
import java.net.URI;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lithium.community.android.utils.LiCoreSDKConstants.LI_LOG_TAG;
import static com.lithium.community.android.utils.LiCoreSDKUtils.addLSIRequestHeaders;

/**
 * This client is used for catering to Authorization request.
 * Created by saiteja.tokala on 12/2/16.
 */

public class LiAuthRestClient {

    /**
     * Makes async call to fetch Auth Code.
     *
     * @param ssoAuthorizationRequest {@link LiSSOAuthorizationRequest}
     * @param callback                {@link LiAuthAsyncRequestCallback}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public void authorizeAsync(@NonNull Context context,
            @NonNull final LiSSOAuthorizationRequest ssoAuthorizationRequest,
            @NonNull final LiAuthAsyncRequestCallback callback) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(ssoAuthorizationRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(),
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                                gson.toJson(ssoAuthorizationRequest)));
        addLSIRequestHeaders(context, builder);
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
                    checkResponse(response, callback, "Error authorizeAsync");
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Wrapping response to LiBaseResponse.
     *
     * @param response {@link Response}
     * @return LiBaseResponse {@link LiBaseResponse}
     * @throws IOException             {@link IOException}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    @NonNull
    @VisibleForTesting
    LiBaseResponse getLiBaseResponseFromResponse(Response response) throws IOException {
        return new LiBaseResponse(response);
    }

    /**
     * Returns OkHttp call.
     *
     * @param request {@link Request}
     * @param client  {@link OkHttpClient}
     * @return Call {@link Call}
     */
    @VisibleForTesting
    Call getCall(Request request, OkHttpClient client) {
        return client.newCall(request);
    }

    /**
     * Returns OkHttpClient.
     *
     * @return OkHttpClient {@link OkHttpClient}
     */
    @NonNull
    @VisibleForTesting
    OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }

    /**
     * Makes Asyn call to fetch Tokens.
     *
     * @param ssoTokenRequest {@link LiSSOTokenRequest}
     * @param callback        {@link LiAuthAsyncRequestCallback}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public void accessTokenAsync(@NonNull Context context, @NonNull final LiSSOTokenRequest ssoTokenRequest,
            @NonNull final LiAuthAsyncRequestCallback callback) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(ssoTokenRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(),
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                                gson.toJson(ssoTokenRequest)));
        addLSIRequestHeaders(context, builder);
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
                    checkResponse(response, callback, "Error accessTokenAsync");
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Makes Async call to fetch fresh Access token from Refresh Token.
     *
     * @param refreshTokenRequest {@link LiRefreshTokenRequest}
     * @param callback            {@link LiAuthAsyncRequestCallback}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public void refreshTokenAsync(@NonNull Context context, @NonNull final LiRefreshTokenRequest refreshTokenRequest,
            @NonNull final LiAuthAsyncRequestCallback callback) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(refreshTokenRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(),
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                                gson.toJson(refreshTokenRequest)));
        addLSIRequestHeaders(context, builder);
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
                    checkResponse(response, callback, "Error refreshTokenAsync");
                } catch (Exception e) {
                    callback.onError(e);
                }
            }
        });
    }

    private void checkResponse(Response response, @NonNull LiAuthAsyncRequestCallback callback, String error) throws LiRestResponseException, IOException {
        if (response != null) {
            if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                try {
                    callback.onSuccess(getLiBaseResponseFromResponse(response));
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                    throw LiRestResponseException.runtimeError(ex.getMessage());
                }
            } else {
                Log.e(LI_LOG_TAG, error);
                callback.onError(new LiRestResponseException(response.code(), error, response.code()));
            }
        } else {
            Log.e(LI_LOG_TAG, error);
            callback.onError(new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR,
                    error, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR));
        }
    }

    /**
     * Makes Sync call to fetch fresh Access token from Refresh Token.
     *
     * @param refreshTokenRequest {@link LiRefreshTokenRequest}
     * @return LiBaseResponse {@link LiBaseResponse}
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    public LiBaseResponse refreshTokenSync(@NonNull Context context,
            @NonNull final LiRefreshTokenRequest refreshTokenRequest) throws LiRestResponseException {

        Gson gson = new Gson();

        Request.Builder builder = new Request.Builder()
                .url(HttpUrl.get(URI.create(String.valueOf(refreshTokenRequest.getUri()))))
                .method(LiBaseRestRequest.RestMethod.POST.name(),
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                                gson.toJson(refreshTokenRequest)));
        addLSIRequestHeaders(context, builder);
        builder.addHeader("client-id", refreshTokenRequest.getClientId());
        Request request = builder.build();

        OkHttpClient client = getOkHttpClient();

        Response response = null;

        try {
            response = getCall(request, client).execute();
            if (response != null) {
                if (response.code() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL && response.body() != null) {
                    return getLiBaseResponseFromResponse(response);
                } else {
                    throw new LiRestResponseException(response.code(), "Error refreshTokenSync", response.code());
                }
            } else {
                throw new LiRestResponseException(LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR,
                        "Error refreshTokenSync", LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            }
        } catch (Exception e) {
            Log.e("LiAuthRestClient", "Error making rest call", e);
            throw LiRestResponseException.networkError(e.getMessage());
        }
    }
}
