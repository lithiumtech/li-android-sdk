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

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiFloatedMessageModel;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.adapters.LiMessageListAdapter;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * This fragment represent a list Messages for a board or just the top messages in a community.
 * <p>
 * Activities containing this fragment MUST implement the {@link LiOnMessageRowClickListener}
 * interface.
 * <p>
 * Usage:
 * Intent i = new Intent(context, LiMessageListActivity.class);
 * i.putExtra(LiSDKConstants.SELECTED_BOARD_ID, boardId);
 * i.putExtra(LiSDKConstants.SELECTED_BOARD_NAME, selectedCategory.getTitle());
 * i.putExtra(LiSDKConstants.APPLY_ARTICLES_COMMON_HEADERS, true/false);
 * <p>
 * {@link LiSDKConstants#SELECTED_BOARD_ID} is required by the activity to know which board the user was on when the board name (from browse all screen)
 * button was clicked.
 * This is also used for deciding whether the screen would show top 25 messages or only the message from the clicked board. Default is null and if null then
 * top 25 message from the community are displayed.
 * {@link LiSDKConstants#SELECTED_BOARD_NAME} is required by the activity to update the actionbar title with the selected board name.
 * {@link LiSDKConstants#APPLY_ARTICLES_COMMON_HEADERS} is required by the activity to show the default headers ("Ask a question and Browse All") or not.
 * Default is false and it will not show the headers
 */
public class LiMessageListFragment extends LiBaseFragment {

    boolean applyHeaders = false;
    private String selectedBoardId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LiMessageListFragment() {
    }

    public static LiMessageListFragment newInstance(Bundle bundle) {
        LiMessageListFragment fragment = new LiMessageListFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void getClient() throws LiRestResponseException {
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedBoardId = bundle.getString(LiSDKConstants.SELECTED_BOARD_ID);
        }
        if (selectedBoardId == null) {
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessagesClientRequestParams(getActivity());
            super.client = LiClientManager.getMessagesClient(liClientRequestParams);
        } else {
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessagesByBoardIdClientRequestParams(getActivity(), selectedBoardId);
            super.client = LiClientManager.getMessagesByBoardIdClient(liClientRequestParams);
        }
        gson = client.getGson();
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {

        if (selectedBoardId == null) {
            applyHeaders = true;
        }
        if (adapter == null) {
            adapter = new LiMessageListAdapter(response, mListener, applyHeaders, getActivity());
        }
        return adapter;
    }

    private void addDummyModelsForHeaders() {
        if (applyHeaders) {
            //add dummy models so that we can show headers even though there are no results
            if (response == null) {
                response = new ArrayList<>();
            }
            response.add(0, new LiBaseModel() {
                @Override
                public LiBaseModel getModel() {
                    return null;
                }
            });
            response.add(1, new LiBaseModel() {
                @Override
                public LiBaseModel getModel() {
                    return null;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        refreshOnResume = true;
        Bundle bundle = getArguments();
        if (bundle != null) {
            applyHeaders = bundle.getBoolean(LiSDKConstants.APPLY_ARTICLES_COMMON_HEADERS, false);
        }

        return setupViews(inflater, container, R.string.li_noArticlesTV, -1, null, View.GONE);
    }

    @Override
    public void refreshUI() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFetchComplete) {
                        if (isErrorWhileFetch) {
                            setupErrorMessage();
                            return;
                        }
                        addDummyModelsForHeaders();
                        if (response != null && !response.isEmpty()) {
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

    @Override
    protected void fetchData() {
        isFetchComplete = false;
        isErrorWhileFetch = false;
        if (!LiSDKManager.isInitialized()
                || !LiUIUtils.isNetworkAvailable(getActivity())) {
            setupErrorMessage();
            return;
        }
        try {
            if (client == null) {
                getClient();
            }
            LiClientRequestParams.LiBeaconPostClientRequestParams beaconParams =
                    new LiClientRequestParams.LiBeaconPostClientRequestParams(getActivity());
            if (selectedBoardId != null) {
                beaconParams.setTargetId(selectedBoardId);
                beaconParams.setTargetType(LiCoreSDKConstants.LI_BEACON_TARGET_TYPE_BOARD);
            }
            LiClientManager.getBeaconClient(beaconParams).processAsync(new LiAsyncRequestCallback() {
                @Override
                public void onSuccess(LiBaseRestRequest request, Object response) throws LiRestResponseException {
                    Log.d(LiSDKConstants.GENERIC_LOG_TAG, "beacon call success");
                }

                @Override
                public void onError(Exception exception) {
                    Log.e(LiSDKConstants.GENERIC_LOG_TAG, "beacon call error: " + exception.getMessage());
                }
            });
            client.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                        final LiGetClientResponse liClientResponse) {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    if (null != liClientResponse) {
                        if (liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_UNAUTHORIZED) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showUserNotLoggedIn();
                                }
                            });
                            return;
                        } else if (liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SERVER_ERROR) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showEmptyView(true);
                                }
                            });
                            return;
                        }

                        response = liClientResponse.getResponse();
                        if (selectedBoardId != null) {
                            getFloatedArticles();
                        } else {
                            isFetchComplete = true;
                            isErrorWhileFetch = false;
                            refreshUI();
                        }
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

    /**
     * Get pinned articles for the board and for that user. And add info to the list of messages to be displayed
     */
    protected void getFloatedArticles() {
        try {
            LiClientRequestParams params = new LiClientRequestParams.LiFloatedMessagesClientRequestParams(getActivity(), selectedBoardId, "global");
            LiClient floatedArticlesClient = LiClientManager.getFloatedMessagesClient(params);
            floatedArticlesClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {

                @Override
                public void onSuccess(LiBaseRestRequest request, final LiGetClientResponse floatedArticlesResponse)
                        throws LiRestResponseException {
                    if (floatedArticlesResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        if (floatedArticlesResponse.getResponse() != null
                                && !floatedArticlesResponse.getResponse().isEmpty()) {
                            Set<Long> tempPinnedArticles = new HashSet<>();
                            for (LiBaseModel item : floatedArticlesResponse.getResponse()) {
                                LiFloatedMessageModel model = (LiFloatedMessageModel) item.getModel();
                                tempPinnedArticles.add(model.getMessage().getId());
                            }
                            List<LiBaseModel> newArticleList = new ArrayList<>(response.size());
                            Iterator<LiBaseModel> iter = response.iterator();
                            while (iter.hasNext()) {
                                LiBaseModel model = iter.next();
                                LiMessage liMessage = (LiMessage) model;
                                Boolean isFloating = tempPinnedArticles.contains(liMessage.getId());
                                liMessage.setFloating(isFloating);
                                if (isFloating) {
                                    newArticleList.add(model);
                                    iter.remove();
                                }
                            }
                            response.addAll(0, newArticleList);
                            newArticleList.clear();
                        }
                    }
                    isFetchComplete = true;
                    isErrorWhileFetch = false;
                    refreshUI();
                }

                @Override
                public void onError(Exception exception) {
                    isFetchComplete = true;
                    isErrorWhileFetch = false;
                    refreshUI();
                }
            });
        } catch (LiRestResponseException e) {
            Log.e(LiSDKConstants.GENERIC_LOG_TAG, e.getMessage());
            isFetchComplete = true;
            isErrorWhileFetch = false;
            refreshUI();
        }
    }
}