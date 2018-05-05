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

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.adapters.LiSupportHomeViewPagerAdapter;
import com.lithium.community.android.ui.components.fragments.LiCreateMessageFragment;
import com.lithium.community.android.ui.components.fragments.LiMessageListFragment;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKConstants;

/**
 * The main entry point for Support action workflow.
 * The activity inflates {@link LiMessageListFragment}
 * and {@link LiCreateMessageFragment} as two different tabs
 */
public class LiSupportHomeActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_TITLE = LiSupportHomeActivity.class.getCanonicalName() + ".PARAM_TITLE";

    private static final int ARTICLES_LIST_FRAGMENT_INDEX = 0;
    private static final int QUESTIONS_LIST_FRAGMENT_INDEX = 1;
    FragmentManager fragmentManager;
    boolean showSearchView = true;
    ViewPager supportHomeViewPager;
    TabLayout tabLayout;
    LiSupportHomeViewPagerAdapter adapter;
    SearchView searchView;
    Menu menu;
    BroadcastReceiver createMessageSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TabLayout.Tab tab = tabLayout.getTabAt(QUESTIONS_LIST_FRAGMENT_INDEX);
            tab.select();
        }
    };
    //listen to the new replies count and update the Tab text
    BroadcastReceiver questionsTabUnreadReplyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TabLayout.Tab tab = tabLayout.getTabAt(QUESTIONS_LIST_FRAGMENT_INDEX);
            int count = intent.getExtras().getInt(LiSDKConstants.LI_QUESTIONS_UNREAD_REPLY_COUNT);
            String tabText = getString(R.string.li_support_my_questions);
            if (count > 0) {
                tabText = getString(R.string.li_support_my_questions) + " " + getString(R.string.li_article_list_separator);
            }
            tab.setText(tabText);
        }
    };
    private String ssoToken;

    @Override
    protected void onStart() {
        fragmentManager = getSupportFragmentManager();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (searchView != null) {
            searchView.clearFocus();
            menu.findItem(R.id.li_action_search).collapseActionView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_activity_support_home);
        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        supportHomeViewPager = findViewById(R.id.li_support_viewpager);
        tabLayout = findViewById(R.id.li_support_home_tabs);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = null;
        if (bundle != null) {
            ssoToken = bundle.getString(LiCoreSDKConstants.LI_SSO_TOKEN, null);
            title = bundle.getString(EXTRA_PARAM_TITLE);
        }

        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.li_support_activity_title);
        }
        setTitle(title);

        FloatingActionButton fab = findViewById(R.id.li_support_home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), LiCreateMessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, true);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        Bundle articleFragmentBundle = new Bundle();
        articleFragmentBundle.putBoolean(LiSDKConstants.APPLY_ARTICLES_COMMON_HEADERS, true);
        articleFragmentBundle.putString(LiCoreSDKConstants.LI_SSO_TOKEN, ssoToken);
        Bundle questionsFragmentBundle = new Bundle();
        questionsFragmentBundle.putString(LiCoreSDKConstants.LI_SSO_TOKEN, ssoToken);
        adapter = new LiSupportHomeViewPagerAdapter(this, getSupportFragmentManager(),
                articleFragmentBundle, questionsFragmentBundle);

        supportHomeViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(supportHomeViewPager);
        IntentFilter questionsTabUnreadReplyFilter =
                new IntentFilter(getString(R.string.li_unread_questions_overall_count));
        registerReceiver(questionsTabUnreadReplyReceiver, questionsTabUnreadReplyFilter);

        IntentFilter createMessageSuccessFilter =
                new IntentFilter(getString(R.string.li_messsage_create_successful));

        registerReceiver(createMessageSuccessReceiver, createMessageSuccessFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(questionsTabUnreadReplyReceiver);
        unregisterReceiver(createMessageSuccessReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.li_support_activity_menu, menu);
        if (showSearchView) {

            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.li_action_search).getActionView();
            if (searchView != null) {
                ComponentName cn = new ComponentName(this, LiSearchActivity.class);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
                searchView.setSubmitButtonEnabled(true);
                searchView.setMaxWidth(Integer.MAX_VALUE);
                searchView.setIconifiedByDefault(true);
                searchView.setQueryRefinementEnabled(true);
                searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            Intent intent = new Intent(getApplicationContext(), LiSearchActivity.class);
                            Bundle bundle = new Bundle();
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
            }

        } else {
            MenuItem item = menu.findItem(R.id.li_action_search);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.li_action_search) {
            return onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}