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

package com.lithium.community.android.ui.components.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.fragments.LiCreateMessageFragment;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;

/**
 * This class is responsible for displaying the Ask a Question screen.
 * The activity inflates {@link LiCreateMessageFragment} for the actual elements that post a question.
 * <p>
 * This class also serves as a screen for replying to a particular message.
 * Usage:
 * <pre>
 *                 Intent i = new Intent(view.getContext(), LiCreateMessageActivity.class);
 *                 Bundle bundle = new Bundle();
 *                 bundle.putString(LiSDKConstants.ORIGINAL_MESSAGE_TITLE, originalMessage.getSubject());
 *                 bundle.putBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, true);
 *                 i.putExtra(LiSDKConstants.SELECTED_BOARD_ID, selectedBoardId);
 *                 i.putExtra(LiSDKConstants.SELECTED_BOARD_NAME, selectedBoardName);
 * </pre>
 * <p>
 * {@link LiCoreSDKConstants}.ASK_Q_CAN_SELECT_A_BOARD is required by the activity to let the user select a board or not, while creating a message
 * When user replies to an existing message in a conversation, the board selection is disabled and it is the other way round when the user is creating new
 * message
 * If this key is not passed in the bundle default value will be true i.e. the user will be able to select a board.
 * <p>
 * {@link LiCoreSDKConstants}.SELECTED_BOARD_ID is required by the activity to know which board the user was on when the create message button was clicked.
 * {@link LiCoreSDKConstants}.SELECTED_BOARD_NAME is required by the activity to update the selected board TextView with the selected board name.
 * <p>
 * {@link LiCoreSDKConstants}.ORIGINAL_MESSAGE_TITLE is required by the activity to update the actionbar title with the message title the user clicked to
 * reply. This is used in Reply Message mode of the screen
 */
public class LiCreateMessageActivity extends AppCompatActivity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1002;
    protected TextView errorTextView;
    protected View errorViewContainer;
    protected TextView errorBtn;
    // listener for login flow to be completed and then refresh accordingly.
    BroadcastReceiver loginCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            populateData();
        }
    };
    LiCreateMessageFragment fragment;
    String ssoToken;
    View.OnClickListener mLoginBtnClickList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startLoginFlow();
        }
    };
    protected BroadcastReceiver networkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isNotConnected = intent.getBooleanExtra("EXTRA_NO_CONNECTIVITY", false);
            if (!isNotConnected) {
                configureLayout();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(errorTextView.getWindowToken(), 0);
        }
        unregisterReceiver(networkBroadcastReceiver);

        //unregister login complete listener
        unregisterReceiver(loginCompleteReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //register the login complete listener
        IntentFilter loginCompleteIntentFilter =
                new IntentFilter(getString(R.string.li_login_complete_broadcast_intent));
        registerReceiver(loginCompleteReceiver,
                loginCompleteIntentFilter);
        registerReceiver(networkBroadcastReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_activity_create_message);
        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        errorViewContainer = findViewById(R.id.li_errorViewContainer);
        errorTextView = findViewById(R.id.li_errorTextView);
        errorBtn = findViewById(R.id.li_errorBtn);
        Bundle actBundle = getIntent().getExtras();
        if (actBundle != null) {
            ssoToken = actBundle.getString(LiCoreSDKConstants.LI_SSO_TOKEN, null);
        }
        FragmentManager fm = getSupportFragmentManager();
        Bundle fragmentBundle = new Bundle();
        if (actBundle != null) {
            fragmentBundle.putAll(actBundle);
        }
        Fragment f = fm.findFragmentById(R.id.li_create_message_fragment_container);
        if (f instanceof LiCreateMessageFragment) {
            fragment = (LiCreateMessageFragment) f;
            fragment.setArguments(fragmentBundle);
        } else {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = LiCreateMessageFragment.newInstance(fragmentBundle);
            fragment.setArguments(fragmentBundle);
            ft.replace(R.id.li_create_message_fragment_container, fragment, fragment.getClass().getName());
            ft.commit();
        }
        configureLayout();
    }

    private void configureLayout() {
        if (!LiSDKManager.isInitialized()) {
            showSDKNotInitialized();
        } else if (!LiUIUtils.isNetworkAvailable(this)) {
            showNoInternetViews();
        } else if (!LiSDKManager.getInstance().isUserLoggedIn()) {
            showUserNotLoggedIn();
        } else {
            populateData();
        }
    }

    private void showSDKNotInitialized() {
        showHideContentViews(false);
        errorViewContainer.setVisibility(View.VISIBLE);
        errorBtn.setVisibility(View.GONE);
        errorTextView.setText(getString(R.string.li_sdkNotInitialized));
    }

    private void populateData() {
        showHideContentViews(true);
        errorViewContainer.setVisibility(View.GONE);
    }

    protected void showNoInternetViews() {
        showHideContentViews(false);
        errorViewContainer.setVisibility(View.VISIBLE);
        errorBtn.setVisibility(View.GONE);
        errorTextView.setText(getString(R.string.li_noInternetAvailable));
    }

    protected void startLoginFlow() {
        LiSDKManager.getInstance().login(this, ssoToken);

    }

    protected void showUserNotLoggedIn() {
        showHideContentViews(false);
        errorViewContainer.setVisibility(View.VISIBLE);
        errorTextView.setText(getString(R.string.li_userNotLoggedIn));
        errorBtn.setOnClickListener(mLoginBtnClickList);
    }


    protected void showHideContentViews(boolean showContent) {
        int visibilityVal = View.GONE;
        if (showContent) {
            visibilityVal = View.VISIBLE;
        }
        findViewById(R.id.li_create_message_fragment_container).setVisibility(visibilityVal);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                    fragment.startImageChooserActivity();
                } else {
                    LiUIUtils.showInAppNotification(this, R.string.li_image_read_no_permission);
                }
            }
            break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        //when user presses back let the fragment handle the behavior if user has entered anything in the post form
        fragment.handleBackButton();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LiSDKConstants.PICK_IMAGE_REQUEST) {
            fragment.handleImageSelection(data);
        }
    }
}
