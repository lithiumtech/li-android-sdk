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

package lithium.community.android.sdk.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.rest.LiAuthRestClient;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;
import lithium.community.android.sdk.utils.LiUriUtils;

import static lithium.community.android.sdk.auth.LiAuthConstants.LOG_TAG;
import static lithium.community.android.sdk.auth.LiAuthorizationException.EXTRA_EXCEPTION;

/**
 * It is a base activity use to extract Auth code and save access token.
 * Created by sumit.pannalall on 12/8/16.
 */

public class LiBaseAuthActivity extends AppCompatActivity {
    private static final String KEY_STATE = "state";
    protected LiAuthService service;

    /**
     * This method extracts auth code from URI received post authorization process.
     * The auth code will be used to fetch access Tokens.
     *
     * @param data Its the URI received upon Authorization completion.
     */
    protected void extractAuthCode(Uri data) {
        String state = data.getQueryParameter(KEY_STATE);
        service = new LiAuthServiceImpl(this);
        LiSSOAuthorizationRequest request = LiAuthRequestStore.getInstance().getLiOriginalRequest(state);

        if (request == null) {
            Log.e(LOG_TAG, String.format(
                    "Response received for unknown request with state %s", state));
            service.enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
            finish();
            return;
        }

        Intent responseData = null;
        LiSSOAuthResponse response2 = new LiSSOAuthResponse();
        if (data.getQueryParameterNames().contains(LiAuthorizationException.PARAM_ERROR)) {
            String error = data.getQueryParameter(LiAuthorizationException.PARAM_ERROR);
            LiAuthorizationException ex = LiAuthorizationException.fromOAuthTemplate(
                    LiAuthorizationException.AuthorizationRequestErrors.byString(error),
                    error,
                    data.getQueryParameter(LiAuthorizationException.PARAM_ERROR_DESCRIPTION),
                    LiUriUtils.parseUriIfAvailable(
                            data.getQueryParameter(LiAuthorizationException.PARAM_ERROR_URI)));
            responseData = ex.toIntent();
        } else {

            response2.setAuthCode(data.getQueryParameter("code"));
            response2.setUserId(data.getQueryParameter("user-id"));
            response2.setApiProxyHost(data.getQueryParameter("proxy-host"));
            response2.setTenantId(data.getQueryParameter("tenant-id"));
            response2.setState(state);
        }

        try {
            final LiAuthRestClient autheClient = new LiAuthRestClient();
            Gson gson = new Gson();
            String jsonString = gson.toJson(response2);
            response2.setJsonString(jsonString);
            service.handleAuthorizationResponse(response2, autheClient, new LiAuthService.LoginCompleteCallBack() {
                @Override
                public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                    service.enablePostAuthorizationFlows(isSuccess, LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL);
                    finish();
                }

            });
        } catch (LiRestResponseException e) {
            String errorMessage = e.getMessage();
            if (responseData != null) {
                responseData.putExtra(LiAuthConstants.LOGIN_RESULT, false);
                errorMessage = responseData.getStringExtra(EXTRA_EXCEPTION);
            }
            service.enablePostAuthorizationFlows(false, LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR);
        }
    }
}
