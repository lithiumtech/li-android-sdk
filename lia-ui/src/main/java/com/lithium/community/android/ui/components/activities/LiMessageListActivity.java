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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.fragments.LiMessageListFragment;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.utils.LiCoreSDKConstants;

/**
 * This activity is responsible for painting the the article list on the screen.
 * The articles belong to a given board (board id) which is provided via the Intent which starts this activity
 * <p>
 * The activity inflates {@link LiMessageListFragment} for the actual list.
 * <pre>
 * Usage:
 *            Intent i = new Intent(context, LiMessageListActivity.class);
 *            i.putExtra(LiSDKConstants.SELECTED_BOARD_ID, boardId);
 *            i.putExtra(LiSDKConstants.SELECTED_BOARD_NAME, selectedCategory.getTitle());
 *            i.putExtra(LiSDKConstants.APPLY_ARTICLES_COMMON_HEADERS, true/false);
 * </pre>
 * {@link LiCoreSDKConstants}.SELECTED_BOARD_ID is required by the activity to know which board the user was on when the board name (from browse all screen)
 * button was clicked.
 * This is also used for deciding whether the screen would show top 25 messages or only the message from the clicked board. Default is null and if null then
 * top 25 message from the community are displayed.
 * {@link LiCoreSDKConstants}.SELECTED_BOARD_NAME} is required by the activity to update the actionbar title with the selected board name.
 * {@link LiCoreSDKConstants}.APPLY_ARTICLES_COMMON_HEADERS} is required by the activity to show the default headers ("Ask a question and Browse All") or not
 * . Default is false and it will not show the headers
 */
public class LiMessageListActivity extends AppCompatActivity {
    SearchView searchView;
    Menu menu;
    private String selectedBoardId;
    private String selectedBoardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_activity_message_list);
        Toolbar toolbar = findViewById(R.id.li_toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        //get the board id and board name from the intent
        if (bundle != null) {
            selectedBoardName = bundle.getString(LiSDKConstants.SELECTED_BOARD_NAME);
            selectedBoardId = bundle.getString(LiSDKConstants.SELECTED_BOARD_ID);
        }

        //update the
        if (selectedBoardName != null) {
            getSupportActionBar().setTitle(selectedBoardName);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle fragmentBundle = new Bundle();
        //add all the intent extras from activity to the Article Fragment
        if (bundle != null) {
            fragmentBundle.putAll(bundle);
        }

        LiMessageListFragment fragment = LiMessageListFragment.newInstance(fragmentBundle);
        ft.replace(R.id.li_articles_fragment_container, fragment, fragment.getClass().getName());
        ft.commit();

        FloatingActionButton floatingActionButton =
                findViewById(R.id.li_ask_question_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAskQuestionScreen();
            }
        });
    }

    /**
     * When the user clicks on the Floating Action Button open the Ask a Question workflow
     */
    protected void startAskQuestionScreen() {
        Intent i = new Intent(this, LiCreateMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, true);
        i.putExtra(LiSDKConstants.SELECTED_BOARD_ID, selectedBoardId);
        i.putExtra(LiSDKConstants.SELECTED_BOARD_NAME, selectedBoardName);
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.li_action_search) {
            //Opens Search Activity for the user to search in the community
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.li_menu_articles_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.li_action_search).getActionView();
        this.menu = menu;
        //inflate the search view
        if (searchView != null) {
            ComponentName cn = new ComponentName(this, LiSearchActivity.class);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
            searchView.setIconifiedByDefault(true);
            searchView.setQueryRefinementEnabled(true);
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setSubmitButtonEnabled(true);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            searchView.clearFocus();
        }
    }
}
