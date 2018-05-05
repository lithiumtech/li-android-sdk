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
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.fragments.LiBrowseFragment;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;

/**
 * This class is responsible for displaying the Nodes present in a community.
 * Nodes implies Categories, subcategories and boards that maybe present in a community.
 * <p>
 * The activity inflates {@link LiBrowseFragment} for the actual list of nodes (Categories, Sub Categories and boards).
 */
public class LiBrowseActivity extends AppCompatActivity {
    LiBrowseFragment fragment;
    SearchView searchView;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_activity_browse);
        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle fragmentBundle = new Bundle();
        fragment = LiBrowseFragment.newInstance(fragmentBundle);
        ft.replace(R.id.li_browse_fragment_container, fragment, fragment.getClass().getName());
        ft.commit();
        FloatingActionButton fab = findViewById(R.id.li_browse_home_fab);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            if (fragment.handleBackButton()) {
                finish();
            }
        } else if (i == R.id.li_action_search) {
            return onSearchRequested();
        }

        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        //when user presses back let the fragment handle the behavior if user has entered anything in the post form
        if (fragment.handleBackButton()) {
            finish();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.li_menu_browse, menu);
        this.menu = menu;
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.li_action_search).getActionView();
        //inflate the search view
        if (searchView != null) {
            ComponentName cn = new ComponentName(this, LiSearchActivity.class);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
            searchView.setSubmitButtonEnabled(true);
            searchView.setIconifiedByDefault(true);
            searchView.setMaxWidth(Integer.MAX_VALUE);
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

        return true;
    }
}
