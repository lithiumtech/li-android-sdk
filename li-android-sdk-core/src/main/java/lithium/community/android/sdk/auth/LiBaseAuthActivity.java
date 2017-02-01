/*
 * LiBaseAuthActivity.java
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

package lithium.community.android.sdk.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.rest.LiAuthRestClient;
import lithium.community.android.sdk.utils.LiUriUtils;

import static lithium.community.android.sdk.auth.LiAuthorizationException.EXTRA_EXCEPTION;
import static lithium.community.android.sdk.auth.LiAuthConstants.LOG_TAG;

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
     * @param data Its the URI received upon Authorization completion.
     */
    protected void extractAuthCode(Uri data) {
        String state = data.getQueryParameter(KEY_STATE);
        service = new LiAuthServiceImpl(this);
        LiSSOAuthorizationRequest request = LiAuthRequestStore.getInstance().getLiOriginalRequest(state);

        if (request == null) {
            Log.e(LOG_TAG, String.format(
                    "Response received for unknown request with state %s", state));
            service.enablePostAuthorizationFlows(LiAuthorizationException.generalEx(
                    LiAuthorizationException.GeneralErrors.SERVER_ERROR.code,
                    String.format(
                            "Response received for unknown request with state %s", state)), false);
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
            response2.setState(data.getQueryParameter("state"));
        }

        try {
            final LiAuthRestClient autheClient = new LiAuthRestClient();
            Gson gson = new Gson();
            String jsonString = gson.toJson(response2);
            response2.setJsonString(jsonString);
            service.handleAuthorizationResponse(response2, autheClient, new LiAuthService.LoginCompleteCallBack() {
                @Override
                public void onLoginComplete(LiAuthorizationException authException, boolean isSuccess) {
                    service.enablePostAuthorizationFlows(null, isSuccess);
                    finish();
                }

            });
        } catch (LiRestResponseException e) {
            String errorMessage = e.getMessage();
            if (responseData != null) {
                responseData.putExtra(LiAuthConstants.LOGIN_RESULT, false);
                errorMessage = responseData.getStringExtra(EXTRA_EXCEPTION);
            }
            service.enablePostAuthorizationFlows(LiAuthorizationException.generalEx(
                    LiAuthorizationException.GeneralErrors.SERVER_ERROR.code,
                    errorMessage), false);
        }
    }
}
