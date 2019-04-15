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

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lithium.community.android.auth.LiDeviceTokenProvider;
import com.lithium.community.android.callback.Callback;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.adapters.LiSupportHomeViewPagerAdapter;
import com.lithium.community.android.ui.components.fragments.LiBaseFragment;
import com.lithium.community.android.ui.components.fragments.LiCreateMessageFragment;
import com.lithium.community.android.ui.components.fragments.LiMessageListFragment;
import com.lithium.community.android.ui.components.fragments.LiProgressFragment;
import com.lithium.community.android.ui.components.fragments.LiUserActivityFragment;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * The main entry point for Support action workflow.
 * The activity inflates {@link LiMessageListFragment}
 * and {@link LiCreateMessageFragment} as two different tabs
 */
public class LiSupportHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    DrawerLayout drawerLayout;
    private boolean isLoginReceiverRegistered = false;
    private LiProgressFragment progressFragment = null;

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
        drawerLayout = findViewById(R.id.li_main_drawer);
        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle drawerToggle = new NavigationListener(this);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        updateNavigationView();
    }

    private void updateNavigationView() {
        NavigationView nv = drawerLayout.findViewById(R.id.li_main_navigation_view);
        nv.setNavigationItemSelectedListener(this);
        boolean isUserLoggedIn = LiSDKManager.getInstance().isUserLoggedIn();
        LiUser user = LiSDKManager.getInstance().getLoggedInUser();
        isUserLoggedIn = isUserLoggedIn && (user != null);
        Menu menu = nv.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            int id = item.getItemId();
            boolean visibility = (isUserLoggedIn && (id != R.id.li_main_navigation_menu_login)
                    || (!isUserLoggedIn && (id == R.id.li_main_navigation_menu_login)));
            item.setVisible(visibility);
        }
        View headerView = nv.getHeaderView(0);
        final ImageView icon = headerView.findViewById(R.id.li_main_navigation_header_icon);
        final TextView title = headerView.findViewById(R.id.li_main_navigation_header_title);
        final TextView subTitle = headerView.findViewById(R.id.li_main_navigation_header_sub_title);

        subTitle.setText(LiSDKManager.getInstance().getCredentials().getCommunityUri().toString());

        if (isUserLoggedIn) {
            title.setText(String.format("%1$s\n<%2$s>", user.getLogin(), user.getEmail()));
            String url = user.getAvatar() != null ? user.getAvatar().getProfile() : null;
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    icon.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    icon.setImageDrawable(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            icon.setTag(target);
            Picasso.with(this).load(url).resize(80, 80)
                    .onlyScaleDown().into(target);
        } else {
            icon.setImageResource(R.mipmap.ic_launcher);
            title.setText(R.string.li_main_navigation_header_title);
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        isLoginReceiverRegistered = true;
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        isLoginReceiverRegistered = false;
        super.unregisterReceiver(receiver);
    }

    private class NavigationListener extends ActionBarDrawerToggle {

        public NavigationListener(Activity context) {
            super(context, drawerLayout, R.string.li_main_navigation_drawer_open, R.string.li_main_navigation_drawer_close);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            updateNavigationView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(questionsTabUnreadReplyReceiver);
        unregisterReceiver(createMessageSuccessReceiver);
        if (isLoginReceiverRegistered) {
            unregisterReceiver(receiver);
        }
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
        if (i == R.id.li_action_search) {
            return onSearchRequested();
        } else if (i == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawer(Gravity.START);
            } else {
                drawerLayout.openDrawer(Gravity.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = intent.getBooleanExtra(LiCoreSDKConstants.LOGIN_RESULT, false);
            unregisterReceiver(receiver);
            if (!result) {
                Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void login() {
        IntentFilter filter = new IntentFilter(getString(R.string.li_login_complete_broadcast_intent));
        registerReceiver(receiver, filter);
        LiSDKManager.getInstance().login(this);
    }

    private void logout() {
        LiSDKManager.getInstance().logout(this, new LogoutCallback());
        showProgress(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.li_main_navigation_menu_login) {
            drawerLayout.closeDrawer(Gravity.START);
            login();
        } else if (item.getItemId() == R.id.li_main_navigation_menu_logout) {
            logout();
        }
        return false;
    }

    private class LogoutCallback implements Callback<Void, Throwable, Throwable> {

        @Override
        public void success(Void v) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);
                    LiUIUtils.showInAppNotification(LiSupportHomeActivity.this, R.string.li_info_logout_succeeded);
                    finish();
                    startActivity(new Intent(LiSupportHomeActivity.this, LiSupportHomeActivity.class));
                }
            });
        }

        @Override
        public void failure(Throwable t) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LiUIUtils.showInAppNotification(LiSupportHomeActivity.this, R.string.li_warning_logout_failed);
                    showProgress(false);
                }
            });
        }

        @Override
        public void abort(Throwable throwable) {
            LiUIUtils.showInAppNotification(LiSupportHomeActivity.this, R.string.li_warning_logout_cancelled);
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            progressFragment = new LiProgressFragment();
            Bundle b = new Bundle();
            b.putString(LiProgressFragment.EXTRA_TITLE, getString(R.string.li_logout_progress_title));
            b.putString(LiProgressFragment.EXTRA_MESSAGE, getString(R.string.li_logout_progress_message));
            progressFragment.setArguments(b);
            progressFragment.show(getSupportFragmentManager(), "logout");
        } else {
            if (progressFragment != null && !progressFragment.getDialog().isShowing()) {
                progressFragment.dismiss();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int currentFragment = -1;
        if (supportHomeViewPager != null) {
            currentFragment = supportHomeViewPager.getCurrentItem();
        }
        if (adapter != null && currentFragment != -1) {
            Object fragment = adapter.instantiateItem(supportHomeViewPager, currentFragment);
            if (fragment instanceof LiBaseFragment) {
                ((LiBaseFragment) fragment).cancelNetworkCalls();
            }
        }
    }
}