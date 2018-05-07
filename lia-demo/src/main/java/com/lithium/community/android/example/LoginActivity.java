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

package com.lithium.community.android.example;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lithium.community.android.auth.LiDeviceTokenProvider;
import com.lithium.community.android.example.utils.MiscUtils;
import com.lithium.community.android.example.utils.ToastUtils;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.ui.components.activities.LiSupportHomeActivity;
import com.lithium.community.android.utils.LiCoreSDKConstants;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin = null;
    private Button btnAnonymous = null;
    private TextView textDescription = null;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
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
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnAnonymous = findViewById(R.id.btn_launch_anonymous);
        btnAnonymous.setOnClickListener(this);
        textDescription = findViewById(R.id.activity_description);

        IntentFilter filter = new IntentFilter(getString(R.string.li_login_complete_broadcast_intent));
        registerReceiver(receiver, filter);

        if (!areCredentialsProvided() || !LiSDKManager.isInitialized()) {
            View layoutErrorMessage = findViewById(R.id.layout_sdk_initialization_error);
            layoutErrorMessage.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        reset();
    }

    private void reset() {
        boolean isInitialized = LiSDKManager.isInitialized();
        btnAnonymous.setEnabled(isInitialized);
        btnLogin.setEnabled(isInitialized);
        textDescription.setVisibility(isInitialized ? View.VISIBLE : View.GONE);
        if (isInitialized) {
            String tenant = LiSDKManager.getInstance().getCredentials().getTenantId();
            String details = String.format(getString(R.string.support_activity_description), tenant);
            if (LiSDKManager.getInstance().isUserLoggedIn()) {
                LiUser user = LiSDKManager.getInstance().getLoggedInUser();
                details = String.format(getString(R.string.user_details), user.getLogin(), user.getEmail(), tenant);
            }
            textDescription.setText(details);
            boolean isUserLoggedIn = LiSDKManager.getInstance().isUserLoggedIn();
            btnLogin.setText(isUserLoggedIn ? R.string.browse : R.string.login);
            btnAnonymous.setVisibility(isUserLoggedIn ? View.GONE : View.VISIBLE);
            invalidateOptionsMenu();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.li_action_logout);
            boolean isUserLoggedIn = LiSDKManager.isInitialized() && LiSDKManager.getInstance().isUserLoggedIn();
            if (item != null) {
                item.setVisible(isUserLoggedIn);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.li_action_logout) {
            boolean isUserLoggedIn = LiSDKManager.isInitialized() && LiSDKManager.getInstance().isUserLoggedIn();
            if (isUserLoggedIn) {
                LiSDKManager.getInstance().logout(this);
                reset();
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
            intent.putExtra(LiSupportHomeActivity.EXTRA_PARAM_TITLE, getString(R.string.app_name));
            this.startActivity(intent);
        } else {
            ToastUtils.notInitialized(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (LiSDKManager.getInstance().isUserLoggedIn()) {
                    start();
                } else {
                    login();
                }
                break;
            case R.id.btn_launch_anonymous:
                start();
                break;
        }
    }
}
