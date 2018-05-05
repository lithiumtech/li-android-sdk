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

package com.lithium.community.sdk.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import lithium.community.android.sdk.auth.LiAppCredentials;
import lithium.community.android.sdk.exception.LiInitializationException;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.sdk.example.utils.MiscUtils;

public class InitializationActivity extends AppCompatActivity {

    private ViewModel model;

    private View layoutSdkCredentialsError;
    private Button btnInitialize;
    private Button btnReset;
    private TextInputEditText fieldClientName;
    private TextInputEditText fieldClientId;
    private TextInputEditText fieldClientSecret;
    private TextInputEditText fieldTenantId;
    private TextInputEditText fieldCommunityUrl;
    private View tipRestartApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lithium.community.sdk.example.R.layout.activity_initialization);
        model = new ViewModel();
        setFieldsOfViews();
        updateViews();
    }

    private void setFieldsOfViews() {
        fieldClientName = findViewById(lithium.community.sdk.example.R.id.field_client_name);
        fieldClientId = findViewById(lithium.community.sdk.example.R.id.field_client_id);
        fieldClientSecret = findViewById(lithium.community.sdk.example.R.id.field_client_secret);
        fieldTenantId = findViewById(lithium.community.sdk.example.R.id.field_tenant_id);
        fieldCommunityUrl = findViewById(lithium.community.sdk.example.R.id.field_community_url);

        layoutSdkCredentialsError = findViewById(lithium.community.sdk.example.R.id.layout_sdk_credentials_error);
        tipRestartApp = findViewById(lithium.community.sdk.example.R.id.tip_restart_app);

        btnInitialize = findViewById(lithium.community.sdk.example.R.id.btn_initialize_sdk);
        btnReset = findViewById(lithium.community.sdk.example.R.id.btn_reset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        btnInitialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LiSDKManager.isInitialized()) {
                    proceed();
                } else {
                    initialize();
                }
            }
        });
    }

    private void updateViews() {
        fieldClientName.setText(model.getClientName());
        fieldClientId.setText(model.getClientId());
        fieldClientSecret.setText(model.getClientSecret());
        fieldTenantId.setText(model.getTenantId());
        fieldCommunityUrl.setText(model.getCommunityUrl());

        if (model.areCredentialsProvided()) {
            layoutSdkCredentialsError.setVisibility(View.INVISIBLE);
        } else {
            layoutSdkCredentialsError.setVisibility(View.VISIBLE);
        }

        if (model.isInitialized()) {
            persistAndUpdate();
            btnInitialize.setText(lithium.community.sdk.example.R.string.action_proceed);
            tipRestartApp.setVisibility(View.VISIBLE);
            btnReset.setEnabled(false);
            fieldClientName.setEnabled(false);
            fieldClientId.setEnabled(false);
            fieldClientSecret.setEnabled(false);
            fieldTenantId.setEnabled(false);
            fieldCommunityUrl.setEnabled(false);
        } else {
            btnInitialize.setText(lithium.community.sdk.example.R.string.action_initialize);
            tipRestartApp.setVisibility(View.INVISIBLE);
            btnReset.setEnabled(true);
            fieldClientName.setEnabled(true);
            fieldClientId.setEnabled(true);
            fieldClientSecret.setEnabled(true);
            fieldTenantId.setEnabled(true);
            fieldCommunityUrl.setEnabled(true);
        }
    }

    private void reset() {
        model.reset();
        updateViews();
    }

    private void initialize() {
        model.update();
        if (model.areCredentialsProvided()) {
            try {
                LiAppCredentials credentials = new LiAppCredentials(
                        model.getClientName(),
                        model.getClientId(),
                        model.getClientSecret(),
                        model.getTenantId(),
                        model.getCommunityUrl(),
                        MiscUtils.getInstanceId(this)
                );
                LiSDKManager.initialize(this, credentials);
                LiSDKManager.getInstance().syncWithCommunity(this);
            } catch (LiInitializationException e) {
                Log.e(MainApplication.class.getSimpleName(), "Lithium SDK initialization failed.");
                e.printStackTrace();
            }
        }
        updateViews();
    }

    private void proceed() {
        startActivity(new Intent(this, DevLoginActivity.class));
    }

    private void persistAndUpdate() {
        // TODO: save the credentials in shared preferences
        // TODO: logout only if credentials have changes since last session.
        if (LiSDKManager.isInitialized() && LiSDKManager.getInstance().isUserLoggedIn()) {
            LiSDKManager.getInstance().logout(this);
        }
    }

    class ViewModel {

        private String clientName;
        private String clientId;
        private String clientSecret;
        private String tenantId;
        private String communityUrl;

        ViewModel() {
            reset();
        }

        void reset() {
            // TODO: use credentials from shared preferences if available
            clientName = MiscUtils.sanitize(getString(lithium.community.sdk.example.R.string.clientAppName));
            clientId = MiscUtils.sanitize(getString(lithium.community.sdk.example.R.string.clientId));
            clientSecret = MiscUtils.sanitize(getString(lithium.community.sdk.example.R.string.clientSecret));
            tenantId = MiscUtils.sanitize(getString(lithium.community.sdk.example.R.string.communityTenantId));
            communityUrl = MiscUtils.sanitize(getString(lithium.community.sdk.example.R.string.communityURL));
        }

        boolean areCredentialsProvided() {
            return !TextUtils.isEmpty(clientName) && !TextUtils.isEmpty(clientId) &&
                    !TextUtils.isEmpty(clientSecret) && !TextUtils.isEmpty(tenantId) &&
                    !TextUtils.isEmpty(communityUrl);
        }

        boolean isInitialized() {
            return LiSDKManager.isInitialized();
        }

        String getClientName() {
            return clientName;
        }

        String getClientId() {
            return clientId;
        }

        String getClientSecret() {
            return clientSecret;
        }

        String getTenantId() {
            return tenantId;
        }

        String getCommunityUrl() {
            return communityUrl;
        }

        void update() {
            clientName = MiscUtils.sanitize(fieldClientName.getText().toString());
            clientId = MiscUtils.sanitize(fieldClientId.getText().toString());
            clientSecret = MiscUtils.sanitize(fieldClientSecret.getText().toString());
            tenantId = MiscUtils.sanitize(fieldTenantId.getText().toString());
            communityUrl = MiscUtils.sanitize(fieldCommunityUrl.getText().toString());
        }
    }
}
