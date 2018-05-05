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

package com.lithium.community.android.sdk.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import com.lithium.community.android.sdk.exception.LiRestResponseException;
import com.lithium.community.android.sdk.manager.LiSDKManager;
import com.lithium.community.android.sdk.rest.LiAuthRestClient;
import com.lithium.community.android.sdk.utils.LiCoreSDKConstants;
import com.lithium.community.android.sdk.utils.LiUriUtils;

/**
 * It is a base activity use to extract Auth code and save access token.
 * Created by sumit.pannalall on 12/8/16.
 */

public class LiBaseAuthActivity extends AppCompatActivity {

    private static final String KEY_STATE = "state";

    @Nullable
    protected LiAuthService service;

    /**
     * This method extracts auth code from URI received post authorization process.
     * The auth code will be used to fetch access Tokens.
     *
     * @param data Its the URI received upon Authorization completion.
     */
    protected void extractAuthCode(Uri data) {
        String state = data.getQueryParameter(KEY_STATE);
        LiSDKManager manager = LiSDKManager.getInstance();
        if (LiSDKManager.isInitialized() && manager != null) {
            service = new LiAuthServiceImpl(this, manager);
            LiSSOAuthorizationRequest request = LiAuthRequestStore.getInstance().getLiOriginalRequest(state);
            if (request == null) {
                Log.e(LiAuthConstants.LOG_TAG, String.format("Response received for unknown request with state %s", state));
                service.enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
                finish();
                return;
            }

            Intent intent = null;
            LiSSOAuthResponse response = new LiSSOAuthResponse();
            if (data.getQueryParameterNames().contains(LiAuthorizationException.PARAM_ERROR)) {
                String error = data.getQueryParameter(LiAuthorizationException.PARAM_ERROR);
                LiAuthorizationException ex = LiAuthorizationException.fromOAuthTemplate(LiAuthorizationException.AuthorizationRequestErrors.byString(error),
                        error, data.getQueryParameter(LiAuthorizationException.PARAM_ERROR_DESCRIPTION),
                        LiUriUtils.parseUriIfAvailable(data.getQueryParameter(LiAuthorizationException.PARAM_ERROR_URI)));
                intent = ex.toIntent();
            } else {
                response.setAuthCode(data.getQueryParameter("code"));
                response.setUserId(data.getQueryParameter("user-id"));
                response.setApiProxyHost(data.getQueryParameter("proxy-host"));
                response.setTenantId(data.getQueryParameter("tenant-id"));
                response.setState(state);
            }

            try {
                final LiAuthRestClient authClient = new LiAuthRestClient();
                Gson gson = new Gson();
                String jsonString = gson.toJson(response);
                response.setJsonString(jsonString);
                service.handleAuthorizationResponse(response, authClient, new LiAuthService.LoginCompleteCallBack() {
                    @Override
                    public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                        Log.d(LiCoreSDKConstants.LI_DEBUG_LOG_TAG, "Login complete. It was " + String.valueOf(isSuccess));
                        service.enablePostAuthorizationFlows(isSuccess, LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL);
                        finish();
                    }

                });
            } catch (LiRestResponseException e) {
                if (intent != null) {
                    intent.putExtra(LiAuthConstants.LOGIN_RESULT, false);
                }
                service.enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            }
        }
    }
}
