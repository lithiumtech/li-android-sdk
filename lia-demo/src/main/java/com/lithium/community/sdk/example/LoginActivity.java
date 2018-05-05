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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lithium.community.sdk.example.utils.MiscUtils;
import com.lithium.community.sdk.example.utils.ToastUtils;

import lithium.community.android.sdk.auth.LiDeviceTokenProvider;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.ui.components.activities.LiSupportHomeActivity;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

public class LoginActivity extends AppCompatActivity {

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = intent.getBooleanExtra(LiCoreSDKConstants.LOGIN_RESULT, true);
            if (result) {
                start();
            } else {
                Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LiSDKManager.isInitialized()) {
                    if (LiSDKManager.getInstance().isUserLoggedIn()) {
                        start();
                    } else {
                        login();
                    }
                } else {
                    ToastUtils.notInitialized(view.getContext());
                }
            }
        });

        IntentFilter filter = new IntentFilter(getString(lithium.community.android.sdk.ui.components.R.string.li_login_complete_broadcast_intent));
        registerReceiver(receiver, filter);

        if (!areCredentialsProvided()) {
            View layoutErrorMessage = findViewById(R.id.layout_sdk_initialization_error);
            layoutErrorMessage.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.li_action_logout) {
            if (LiSDKManager.isInitialized() && LiSDKManager.getInstance().isUserLoggedIn()) {
                LiSDKManager.getInstance().logout(this);
            }

            return true;
        } else {

            return super.onOptionsItemSelected(item);
        }
    }

    protected boolean areCredentialsProvided() {
        return MiscUtils.areCredentialsProvided(this);
    }

    private void login() {
        LiSDKManager.getInstance().initLoginFlow(this, new LiDeviceTokenProvider() {
            @Override
            public String getDeviceId() {

                return FirebaseInstanceId.getInstance().getToken();
            }
        });
    }

    private void start() {
        if (LiSDKManager.isInitialized()) {
            Intent intent = new Intent(this, LiSupportHomeActivity.class);
            this.startActivity(intent);
        } else {
            ToastUtils.notInitialized(this);
        }
    }
}
