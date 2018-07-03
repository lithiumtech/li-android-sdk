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

package com.lithium.community.android.ui.components.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.activities.LiConversationActivity;
import com.lithium.community.android.ui.components.adapters.LiBaseRecyclerAdapter;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.util.List;

/**
 * This fragment is an abstract base class which takes care of getting appropriate LiClient,
 * fetching data, setup the common views and a common error layout.
 * This is to reuse the views that are present in most of the fragments
 * There are abstract methods which need to be implemented by the concrete Fragment for poplulating the data, getting respective API Providers and adapters.
 * The base fragment also consists of the {@link BroadcastReceiver} which receives the login complete event broadcasted by coreSDK
 * Implements {@link android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener} to handle refresh listener
 * which takes care of updating the main content after the user has been logged in successfully.
 */
public abstract class LiBaseFragment extends DialogFragment
        implements SwipeRefreshLayout.OnRefreshListener {
    protected RecyclerView recyclerView;
    protected Gson gson;
    protected LinearLayout liErrorViewContainer;
    protected SwipeRefreshLayout swipeable;
    protected volatile boolean isFetchComplete = false;
    protected boolean isErrorWhileFetch = false;
    protected TextView liErrorTextView;
    protected TextView liErrorBtn;
    protected LiClient client;
    protected boolean refreshOnResume;
    protected int errorBtnTextRes;
    protected View.OnClickListener errorBtnClickListener;
    protected int errorTextRes;
    protected String ssoToken;
    /**
     * Implement the click listeners for the MessageList rows
     * For now there is no behavior on long click of the row
     */
    protected LiOnMessageRowClickListener mListener = new LiOnMessageRowClickListener() {

        @Override
        public void onMessageRowClick(LiBaseModel item) {
            LiMessage message = (LiMessage) item;
            Intent i = new Intent(getActivity(), LiConversationActivity.class);
            i.putExtra(LiSDKConstants.SELECTED_MESSAGE_ID, message.getId());
            getActivity().startActivity(i);
        }

        @Override
        public void onMessageRowLongClick(LiBaseModel item) {
        }
    };
    protected List<LiBaseModel> response;
    LiBaseRecyclerAdapter adapter;
    BroadcastReceiver loginCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                getClient();
                if (!refreshOnResume) {
                    populateData();
                }
            } catch (LiRestResponseException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * BaseFragment doesn't know about which client to be used.
     * Hence this is an abstract method which must be implemented by the concrete class
     *
     * @throws LiRestResponseException
     */
    protected abstract void getClient() throws LiRestResponseException;

    /**
     * This method returns the respective adapter from concrete fragment implementation
     *
     * @return RecyclerView.Adapter
     */
    protected abstract RecyclerView.Adapter getAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (LiSDKManager.isInitialized()) {
                getClient();
            }
            ssoToken = getArguments().getString(LiCoreSDKConstants.LI_SSO_TOKEN, null);
        } catch (LiRestResponseException e) {
            Log.e(LiSDKConstants.GENERIC_LOG_TAG, "Couldn't acquire the LiClient:" + e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (refreshOnResume) {
            isFetchComplete = false;
        }
        Activity activity = getActivity();
        if (activity != null && loginCompleteReceiver != null) {
            activity.unregisterReceiver(loginCompleteReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter loginCompleteIntentFilter =
                new IntentFilter(getString(R.string.li_login_complete_broadcast_intent));
        Activity activity = getActivity();
        if (activity != null) {
            activity.registerReceiver(loginCompleteReceiver,
                    loginCompleteIntentFilter);
        }
        if (refreshOnResume) {
            populateData();
        }
    }

    /**
     * Check if the network is available or not, If the user is logged in or not.
     * If not then refresh the UI else go ahead and fetch the data from server
     */
    protected void populateData() {
        swipeable.setRefreshing(true);

        if (!LiSDKManager.isInitialized() || !LiUIUtils.isNetworkAvailable(getActivity())) {
            setupErrorMessage();
            return;
        }
        //Check if the fetch is already going. This can happen from onResume method callback
        if (isFetchComplete) {
            refreshUI();
        } else {
            fetchData();
        }
    }

    @Override
    public void onRefresh() {
        swipeable.setRefreshing(true);
        fetchData();
    }

    /**
     * Refresh teh UI views after the fetch is complete. If no results then show and empty view.
     * If results are present then set the result in the respective adapter
     */
    public void refreshUI() {
        if (getActivity() != null) {
            Activity activity = getActivity();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFetchComplete && isAdded()) {
                        if (isErrorWhileFetch) {
                            setupErrorMessage();
                            return;
                        }
                        if (response != null && !response.isEmpty() && getActivity() != null) {
                            showEmptyView(false);
                            if (recyclerView.getAdapter() == null) {
                                recyclerView.setAdapter(getAdapter());
                            }
                            adapter.setItems(response);
                            adapter.notifyDataSetChanged();
                        } else {
                            setupErrorMessage();
                        }
                    }
                }
            });
        }
    }

    /**
     * Setup the views for the fragment.
     * This methods takes in the View that needs to be inflated and attach behaviors to the common error layout
     * since this is the common view shared across multiple fragments
     *
     * @param inflater               {@link LayoutInflater}
     * @param container              {@link ViewGroup} container
     * @param errorTextRes           Text Resource for Error {@link TextView} for inflating the error layout
     * @param errorBtnTextRes        Text Resource for Error Button for inflating the error layout
     * @param clickListener          Click listener for the Error Button (if any) else this would be null
     * @param errorBtnVisibilityCode {@link View} Visibility code which will define if the error button needs to be shown or not
     * @return
     */
    @NonNull
    protected View setupViews(LayoutInflater inflater, ViewGroup container,
            int errorTextRes, int errorBtnTextRes,
            android.view.View.OnClickListener clickListener,
            int errorBtnVisibilityCode) {
        View view = inflater.inflate(R.layout.li_base_fragment_message_list, container, false);
        setupViews(view, errorTextRes, errorBtnTextRes, clickListener, errorBtnVisibilityCode);
        return view;
    }

    /**
     * Setup the views for the fragment.
     * This methods takes in the View that needs to be inflated and attach behaviors to the common error layout
     * since this is the common view shared across multiple fragments
     *
     * @param view                   View that needs to be inflated
     * @param errorTextRes           Text Resource for Error {@link TextView} for inflating the error layout
     * @param errorBtnTextRes        Text Resource for Error Button for inflating the error layout
     * @param errorBtnClickListener  Click listener for the Error Button (if any) else this would be null
     * @param errorBtnVisibilityCode {@link View} Visibility code which will define if the error button needs to be shown or not
     */
    @NonNull
    protected void setupViews(View view, int errorTextRes, int errorBtnTextRes,
            android.view.View.OnClickListener errorBtnClickListener,
            int errorBtnVisibilityCode) {
        setRetainInstance(true);
        recyclerView = view.findViewById(R.id.li_supportFragmentRecycler);
        swipeable = view.findViewById(R.id.li_swipeable_items_list);
        swipeable.setOnRefreshListener(this);
        liErrorViewContainer = view.findViewById(R.id.li_errorViewContainer);
        liErrorViewContainer.setVisibility(View.GONE);
        liErrorTextView = view.findViewById(R.id.li_errorTextView);
        liErrorBtn = view.findViewById(R.id.li_errorBtn);
        this.errorBtnTextRes = errorBtnTextRes;
        this.errorBtnClickListener = errorBtnClickListener;
        this.errorTextRes = errorTextRes;
        if (!refreshOnResume) {
            populateData();
        }
    }

    /**
     * Sets up the error message and error layout behavior based upon if the network is available or not.
     * Or if the user is still logged in or not.
     * If Network is not present then show "Internet not available" text with a Retry button
     * If User is not logged in then show "User not logged in" text with a Login button
     */
    protected void setupErrorMessage() {
        if (!LiSDKManager.isInitialized()) {
            liErrorTextView.setText(getString(R.string.li_sdkNotInitialized));
            liErrorBtn.setVisibility(View.GONE);
        } else if (!LiUIUtils.isNetworkAvailable(getActivity())) {
            showNoInternet();
        } else {
            if (liErrorBtn != null && errorBtnTextRes != -1) {
                liErrorBtn.setText(getString(errorBtnTextRes));
                liErrorBtn.setOnClickListener(errorBtnClickListener);
            } else {
                liErrorBtn.setVisibility(View.GONE);
            }
            liErrorTextView.setText(getString(errorTextRes));
        }
        showEmptyView(true);
    }

    private void showNoInternet() {
        liErrorTextView.setText(getString(R.string.li_noInternetAvailable));
        liErrorBtn.setVisibility(View.VISIBLE);
        liErrorBtn.setText(getString(R.string.li_retry));
        liErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
    }

    protected void showUserNotLoggedIn() {
        liErrorTextView.setText(getString(R.string.li_userNotLoggedIn));
        liErrorBtn.setVisibility(View.VISIBLE);
        liErrorBtn.setText(getString(R.string.li_login));
        liErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiSDKManager.getInstance().login(v.getContext(), ssoToken);
            }
        });
        showEmptyView(true);
    }

    /**
     * Shows or hides the error layout
     *
     * @param showEmptyView whether the error layout should be hidden or shown
     */
    protected void showEmptyView(boolean showEmptyView) {
        if (showEmptyView) {
            recyclerView.setVisibility(View.GONE);
            liErrorViewContainer.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            liErrorViewContainer.setVisibility(View.GONE);
        }
        swipeable.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    /**
     * Fetch the data from the server using {@link LiClient}.processAsync method.
     * If the network is not available then show error layout.
     */
    protected void fetchData() {
        isFetchComplete = false;
        isErrorWhileFetch = false;
        if (!LiUIUtils.isNetworkAvailable(getActivity())) {
            setupErrorMessage();
            return;
        }
        try {
            if (client == null) {
                getClient();
            }
            client.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                        LiGetClientResponse liClientResponse) {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }

                    if (null != liClientResponse) {
                        if (liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                            response = liClientResponse.getResponse();
                            isFetchComplete = true;
                            isErrorWhileFetch = false;
                        } else {
                            isFetchComplete = true;
                            isErrorWhileFetch = true;
                        }
                        refreshUI();
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    isFetchComplete = true;
                    isErrorWhileFetch = true;
                    refreshUI();
                }
            });
        } catch (LiRestResponseException exception) {
            if (!isAdded() || getActivity() == null) {
                return;
            }
            isFetchComplete = true;
            isErrorWhileFetch = true;
            refreshUI();
        }
    }

}
