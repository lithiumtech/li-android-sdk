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

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.fragments.LiConversationFragment;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;

/**
 * This class is responsible for displaying the Message reply thread.
 * The activity inflates {@link LiConversationFragment} for the actual thread
 * Usage:
 * <pre>
 *         Intent i = new Intent(mActivity, LiConversationActivity.class);
 *         i.putExtra(LiSDKConstants.SELECTED_MESSAGE_ID, message.getId());
 * </pre>
 * {@link LiSDKConstants}.SELECTED_MESSAGE_ID It requires a message/topic id to display the conversation.
 */
public class LiConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_activity_conversation);
        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle fragmentBundle = new Bundle();
        if (bundle != null) {
            fragmentBundle.putAll(bundle);
        }
        LiConversationFragment fragment = LiConversationFragment.newInstance(fragmentBundle);
        ft.replace(R.id.li_message_fragment_container, fragment, fragment.getClass().getName());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
