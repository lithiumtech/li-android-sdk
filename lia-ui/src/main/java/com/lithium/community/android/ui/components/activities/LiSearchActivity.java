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
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.fragments.LiSearchFragment;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;

import java.util.ArrayList;

/**
 * This class is responsible for displaying the Search screen.
 * The activity inflates {@link LiSearchFragment} for the actual list of nodes (Categories, Sub Categories and boards).
 */
public class LiSearchActivity extends AppCompatActivity {
    public static final int VOICE_RECOGNITION_CODE = 1;
    InputMethodManager imm;
    private String query;
    private ImageButton searchMic;
    private ImageButton searchClear;
    private EditText searchInput;

    // Receives the intent with the speech-to-text information and sets it to the InputText
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VOICE_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchInput.setText(text.get(0));
                    doSearch();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_activity_search);
        Intent intent = getIntent();
        imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE
        );
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
        searchClear = findViewById(R.id.li_search_clear);

        ImageButton backButton = findViewById(R.id.li_search_act_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchInput = findViewById(R.id.li_search_text);
        searchInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        searchMic = findViewById(R.id.li_search_mic);
        // Gets the event of pressing search button on soft keyboard
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                }
                return true;
            }
        };

        searchInput.setOnEditorActionListener(searchListener);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!"".equals(searchInput.getText().toString())) {
                    query = searchInput.getText().toString();

                    searchMic.setVisibility(View.GONE);
                    searchClear.setVisibility(View.VISIBLE);
                } else {
                    searchMic.setVisibility(View.VISIBLE);
                    searchClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!"".equals(searchInput.getText().toString())) {
                    query = searchInput.getText().toString();
                }
            }
        });

        searchMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMic.isSelected()) {
                    searchInput.setText("");
                    query = "";
                    searchMic.setSelected(Boolean.FALSE);
                } else {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");

                    LiSearchActivity.this.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
                }
            }
        });

        searchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMic.setVisibility(View.VISIBLE);
                searchClear.setVisibility(View.GONE);
                searchInput.setText("");
            }
        });
        searchInput.requestFocus();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doSearch();
        }
    }

    private void doSearch() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString(LiSDKConstants.LI_SEARCH_QUERY, query);
        LiSearchFragment fragment = LiSearchFragment.newInstance(fragmentBundle);
        ft.replace(R.id.li_search_fragment_container, fragment, fragment.getClass().getName());
        ft.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
