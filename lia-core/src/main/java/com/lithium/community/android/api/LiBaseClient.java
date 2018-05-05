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

package com.lithium.community.android.api;

import android.content.Context;

import com.google.gson.Gson;
import com.lithium.community.android.queryutil.LiQueryOrdering;

import com.lithium.community.android.auth.LiAuthConstants;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.queryutil.LiQueryRequestParams;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseResponse;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiClientResponse;
import com.lithium.community.android.rest.LiDeleteClientResponse;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.rest.LiPostClientResponse;
import com.lithium.community.android.rest.LiPutClientResponse;
import com.lithium.community.android.rest.LiRestV2Request;
import com.lithium.community.android.rest.LiRestv2Client;
import com.lithium.community.android.utils.LiCoreSDKConstants;

/**
 * Abstract Client class which provide implementation to basic methods of {@link LiClient}.
 * All clients must extend this class to get implementation of standard methods.
 * Created by kunal.shrivastava on 11/2/16.
 */

abstract class LiBaseClient implements LiClient {

    protected final String type;
    private final LiRestv2Client liRestv2Client;
    private final Class<? extends LiBaseModel> responseClass;
    protected String querySettingsType;
    protected Context context;
    protected LiRestV2Request liRestV2Request;
    protected String basePath;
    protected RequestType requestType;
    protected LiQueryOrdering liQueryOrdering;
    protected LiQueryRequestParams liQueryRequestParams;
    private String imagePath;
    private String imageName;

    public LiBaseClient(Context context, String type, Class<? extends LiBaseModel> responseClass) {
        this.context = context;
        this.type = type;
        this.responseClass = responseClass;
        this.liRestv2Client = LiRestv2Client.getInstance();
    }

    public LiBaseClient(Context context, String basePath, String type, String querySettingsType,
                        Class<? extends LiBaseModel> responseClass, RequestType requestType) {
        this.context = context;
        this.type = type;
        this.querySettingsType = querySettingsType;
        this.basePath = basePath;
        this.responseClass = responseClass;
        this.liRestv2Client = LiRestv2Client.getInstance();
        this.requestType = requestType;
    }


    public LiBaseClient(Context context, String type, String querySettingsType, Class<? extends LiBaseModel> responseClass, RequestType requestType) {
        this.context = context;
        this.type = type;
        this.querySettingsType = querySettingsType;
        this.basePath = String.format(LiAuthConstants.API_PROXY_DEFAULT_SEARCH_BASE_PATH, LiSDKManager.getInstance().getTenantId());
        this.responseClass = responseClass;
        this.liRestv2Client = LiRestv2Client.getInstance();
        this.requestType = requestType;
    }

    public LiBaseClient(Context context, String type, String querySettingsType, Class<? extends LiBaseModel> responseClass,
                        RequestType requestType, String pathParam) {
        this.context = context;
        this.type = type;
        this.querySettingsType = querySettingsType;
        this.basePath = String.format(LiAuthConstants.API_PROXY_DEFAULT_GENERIC_BASE_PATH, LiSDKManager.getInstance().getTenantId(), pathParam);
        this.responseClass = responseClass;
        this.liRestv2Client = LiRestv2Client.getInstance();
        this.requestType = requestType;
    }


    public LiBaseClient(Context context, String basePath, RequestType requestType) {
        this.context = context;
        this.type = null;
        this.querySettingsType = null;
        this.basePath = basePath;
        this.responseClass = null;
        this.liRestv2Client = LiRestv2Client.getInstance();
        this.requestType = requestType;
    }

    public LiBaseClient(Context context, String basePath, RequestType requestType, String imagePath, String imageName) {
        this.context = context;
        this.type = null;
        this.querySettingsType = null;
        this.basePath = basePath;
        this.responseClass = null;
        this.liRestv2Client = LiRestv2Client.getInstance();
        this.requestType = requestType;
        this.imagePath = imagePath;
        this.imageName = imageName;
    }

    /**
     * Abstract method to set LiRestV2Request which will be passed as parameter in
     * {@link LiRestv2Client#processSync(LiBaseRestRequest)} and
     * {@link LiRestv2Client#processAsync(LiBaseRestRequest, LiAsyncRequestCallback)} call.
     */
    public abstract void setLiRestV2Request();

    public abstract String getRequestBody();

    /**
     * This method is for making an async network call.
     * {@link LiClient#processAsync(LiAsyncRequestCallback)}
     */
    @Override
    public void processAsync(final LiAsyncRequestCallback liAsyncRequestCallback) throws LiRestResponseException {

        try {
            setLiRestV2Request();
            this.liRestV2Request.setPath(basePath);
            final LiAsyncRequestCallback callback = new LiAsyncRequestCallback<LiBaseResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiBaseResponse response) throws LiRestResponseException {
                    if (null != response) {
                        if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                            if (requestType.equals(RequestType.GET)) {
                                liAsyncRequestCallback.onSuccess(request, new LiGetClientResponse(response, type, responseClass, getGson()));
                            } else if (requestType.equals(RequestType.DELETE)) {
                                liAsyncRequestCallback.onSuccess(request, new LiDeleteClientResponse(response));
                            } else if (requestType.equals(RequestType.PUT)) {
                                liAsyncRequestCallback.onSuccess(request, new LiPutClientResponse(response));
                            } else {
                                liAsyncRequestCallback.onSuccess(request, new LiPostClientResponse(response));
                            }

                        } else {
                            liAsyncRequestCallback.onError(new Exception(response.getMessage()));
                        }
                    } else {
                        liAsyncRequestCallback.onError(new Exception("Server Error"));
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    liAsyncRequestCallback.onError(e);
                }
            };
            liRestv2Client.processAsync(liRestV2Request, callback);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw LiRestResponseException.runtimeError(e.getMessage());
        }
    }

    /**
     * processSync call to upload files
     *
     * @param liAsyncRequestCallback {@link LiAsyncRequestCallback}
     * @param imagePath              this is the absolute path of the image.
     * @param imageName              this is the name of the image file.
     */
    @Override
    public void processAsync(final LiAsyncRequestCallback liAsyncRequestCallback, final String imagePath, final String imageName)
            throws LiRestResponseException {
        try {
            setLiRestV2Request();
            this.liRestV2Request.setPath(basePath);
            String requestBody = getRequestBody();
            final LiAsyncRequestCallback callback = new LiAsyncRequestCallback<LiBaseResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiBaseResponse response) throws LiRestResponseException {
                    if (null != response) {
                        if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                            if (requestType.equals(RequestType.GET)) {
                                liAsyncRequestCallback.onSuccess(request, new LiGetClientResponse(response, type, responseClass, getGson()));
                            } else {
                                liAsyncRequestCallback.onSuccess(request, new LiPostClientResponse(response));
                            }
                        } else {
                            liAsyncRequestCallback.onError(new Exception(response.getMessage()));
                        }
                    } else {
                        liAsyncRequestCallback.onError(new Exception("Server Error"));
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    liAsyncRequestCallback.onError(e);
                }
            };
            liRestv2Client.uploadProcessAsync(liRestV2Request, callback, imagePath, imageName, requestBody);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw LiRestResponseException.runtimeError(e.getMessage());
        }
    }

    /**
     * This method is for making a sync network call.
     * {@link LiClient#processSync()}
     */
    @Override
    public LiClientResponse processSync() throws LiRestResponseException {
        try {
            setLiRestV2Request();
            this.liRestV2Request.setPath(basePath);
            LiBaseResponse response = liRestv2Client.processSync(liRestV2Request);
            if (null != response) {
                if (response.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                    if (requestType.equals(RequestType.GET)) {
                        return new LiGetClientResponse(response, type, responseClass, getGson());
                    } else if (requestType.equals(RequestType.DELETE)) {
                        return new LiDeleteClientResponse(response);
                    } else if (requestType.equals(RequestType.PUT)) {
                        return new LiPutClientResponse(response);
                    } else {
                        return new LiPostClientResponse(response);
                    }
                } else {
                    throw new RuntimeException("Server Error");
                }
            } else {
                throw new RuntimeException("response empty");
            }
        } catch (RuntimeException e) {
            throw LiRestResponseException.runtimeError(e.getMessage());
        }
    }

    /**
     * Fetches Gson from {@link LiRestv2Client}
     * {@link LiClient#getGson()}
     */
    @Override
    public Gson getGson() {
        if (liRestv2Client != null) {
            return liRestv2Client.getGson();
        } else {
            return null;
        }
    }

    /**
     * Returns type i.e whether message, image, kudo etc..
     */
    public String getType() {
        return type;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Use to set liQueryOrdering in LIQL if any
     */
    public LiClient setOrdering(LiQueryOrdering liQueryOrdering) {
        this.liQueryOrdering = liQueryOrdering;
        return this;
    }

    /**
     * Enum to classify Request Type
     */
    protected enum RequestType {
        GET, POST, DELETE, PUT;
    }
}
