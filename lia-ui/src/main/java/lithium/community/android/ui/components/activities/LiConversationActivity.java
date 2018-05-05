/*
 * LiMessagesActivity.java
 * Created on Dec 21, 2016
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
package lithium.community.android.ui.components.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import lithium.community.android.sdk.ui.components.R;
import lithium.community.android.ui.components.fragments.LiConversationFragment;
import lithium.community.android.ui.components.utils.LiSDKConstants;

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
