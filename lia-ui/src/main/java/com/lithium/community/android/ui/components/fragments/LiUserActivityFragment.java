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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.lithium.community.android.ui.components.activities.LiCreateMessageActivity;
import com.lithium.community.android.ui.components.adapters.LiMessageListAdapter;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lithium.community.android.sdk.api.LiClient;
import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.manager.LiSDKManager;
import lithium.community.android.sdk.model.LiBaseModel;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.model.response.LiTargetModel;
import lithium.community.android.sdk.model.response.LiUser;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.rest.LiGetClientResponse;
import lithium.community.android.sdk.ui.components.R;
import lithium.community.android.sdk.utils.LiCoreSDKConstants;

/**
 * This fragment extends {@link LiBaseFragment} and displays Messages that are authored by the logged in user.
 * Also it displays all the Subscribed Messages for that user.
 */
public class LiUserActivityFragment extends LiBaseFragment {
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LiUserActivityFragment() {
    }

    public static LiUserActivityFragment newInstance(Bundle bundle) {
        LiUserActivityFragment fragment = new LiUserActivityFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        if (adapter == null) {
            adapter = new LiMessageListAdapter(response, mListener, false, getActivity());
        }
        return adapter;
    }

    @Override
    protected void getClient() throws LiRestResponseException {
        LiUser user = LiSDKManager.getInstance().getLoggedInUser();
        if (user == null) {
            return;
        }
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiUserMessagesClientRequestParams(getActivity(), user.getId(), "0");
        client = LiClientManager.getUserMessagesClient(liClientRequestParams);
        gson = client.getGson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        refreshOnResume = true;
        return setupViews(inflater, container, R.string.li_noQuestionsTV,
                R.string.li_askAQuestion_all_caps,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), LiCreateMessageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, true);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                }, View.VISIBLE);
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
                showUserNotLoggedIn();
                return;
            }
            //Beacon call for user
            if (LiSDKManager.getInstance().getLoggedInUser() != null) {
                LiClientRequestParams.LiBeaconPostClientRequestParams beaconParams = new LiClientRequestParams.LiBeaconPostClientRequestParams(getActivity());
                beaconParams.setTargetType(LiCoreSDKConstants.LI_BEACON_TARGET_TYPE_USER);
                beaconParams.setTargetId(String.valueOf(LiSDKManager.getInstance().getLoggedInUser().getId()));
                LiClientManager.getBeaconClient(beaconParams).processAsync(new LiAsyncRequestCallback() {
                    @Override
                    public void onSuccess(LiBaseRestRequest liBaseRestRequest, Object o) throws LiRestResponseException {
                        Log.d(LiSDKConstants.GENERIC_LOG_TAG, "beacon call success");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(LiSDKConstants.GENERIC_LOG_TAG, "beacon call error: " + e.getMessage());
                    }
                });
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
                        }
                        getSubscriptions();
                    } else {
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

    /**
     * Get all the subscribed Messages for the logged in user.
     * Gets a list of {@link lithium.community.android.sdk.model.response.LiSubscriptions} and extracts {@link LiMessage}
     * that gets added to the {@link List<LiMessage>} response
     */
    public void getSubscriptions() {
        try {
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiUserSubscriptionsClientRequestParams(getActivity());
            LiClient subscriptionsClient = LiClientManager.getUserSubscriptionsClient(liClientRequestParams);
            subscriptionsClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiGetClientResponse subscriptionResponse)
                        throws LiRestResponseException {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    if (subscriptionResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        if (null != subscriptionResponse.getResponse()
                                && !subscriptionResponse.getResponse().isEmpty()) {
                            if (response == null) {
                                response = new ArrayList<>();
                            }
                            if (!isFetchComplete) {
                                addSubscriptions(subscriptionResponse.getResponse());
                            }
                        }

                        fetchNewReplyCount();
                        isFetchComplete = true;
                        isErrorWhileFetch = false;
                    } else {
                        isFetchComplete = true;
                        isErrorWhileFetch = true;
                    }
                    refreshUI();
                }

                @Override
                public void onError(Exception exception) {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    isFetchComplete = true;
                    isErrorWhileFetch = true;
                    refreshUI();
                }
            });
        } catch (LiRestResponseException e) {
            if (!isAdded() || getActivity() == null) {
                return;
            }
            Log.e("Subscriptions", e.getMessage());
        }
    }

    private void addSubscriptions(List<LiBaseModel> subscriptionResponse) {
        Set<Long> existingIds = new HashSet<>(response.size());
        for (LiBaseModel responseModel : response) {
            LiMessage responseMessage = ((LiTargetModel) responseModel).getLiMessage();
            existingIds.add(responseMessage.getId());
        }
        //Check if the subscription list contains any message authored by the user himself
        for (LiBaseModel liBaseModel : subscriptionResponse) {
            LiMessage subscriptionMessage = ((LiTargetModel) liBaseModel).getLiMessage();
            if (!existingIds.contains(subscriptionMessage.getId())) {
                response.add(subscriptionMessage);
            }
        }
    }

    /**
     * Fetches the count of new replies for each Message and updates the info
     * so that the {@link LiMessageListAdapter} can populate respective UI view for new reply count
     *
     * @throws LiRestResponseException
     */
    protected void fetchNewReplyCount() throws LiRestResponseException {
        if (response != null && !response.isEmpty() && isAdded() && getActivity() != null) {
            String newReplyQuery = buildNewReplyQuery(response);
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiGenericLiqlClientRequestParams(getActivity(), newReplyQuery);
            LiClient genericClient =
                    LiClientManager.getGenericLiqlGetClient(liClientRequestParams);
            genericClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                        LiGetClientResponse liGetClientResponse)
                        throws LiRestResponseException {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    if (liGetClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Gson gson = new Gson();
                        Map<Long, Integer> articleIdCountMap = new HashMap<>();
                        int unreadOverallCount = 0;
                        if (liGetClientResponse.getJsonObject().has("data") && liGetClientResponse.getJsonObject()
                                .get("data").getAsJsonObject().has("items")) {
                            JsonArray messageElements = liGetClientResponse.getJsonObject()
                                    .get("data").getAsJsonObject().get("items").getAsJsonArray();
                            for (JsonElement messageElement : messageElements) {
                                LiMessage message = gson.fromJson(messageElement.getAsJsonObject(), LiMessage.class);
                                int articleUnreadCount = 0;
                                if (!message.getUserContext().getRead()) {
                                    unreadOverallCount++;
                                    if (articleIdCountMap.containsKey(message.getId())) {
                                        articleUnreadCount = articleIdCountMap.get(message.getParent().getId());
                                    }
                                    articleUnreadCount++;
                                    articleIdCountMap.put(message.getParent().getId(), articleUnreadCount);
                                }
                            }
                            if (unreadOverallCount > 0) {
                                for (LiBaseModel liBaseModel : response) {
                                    LiMessage message = ((LiTargetModel)
                                            liBaseModel.getModel()).getLiMessage();
                                    if (articleIdCountMap.containsKey(message.getId())) {
                                        message.setUnreadReplyCount(articleIdCountMap.get(message.getId()));
                                    }
                                }
                                refreshUI();
                            }
                            if (!isAdded() || getActivity() == null) {
                                return;
                            }
                            Intent intent = new Intent(getString(R.string.li_unread_questions_overall_count));
                            intent.putExtra(LiSDKConstants.LI_QUESTIONS_UNREAD_REPLY_COUNT, unreadOverallCount);
                            getActivity().sendBroadcast(intent);
                        }
                    } else {
                        Log.e(LiSDKConstants.GENERIC_LOG_TAG, "could not fetch new reply count");
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(LiSDKConstants.GENERIC_LOG_TAG, "could not fetch new reply count");
                }
            });
        }
    }

    /**
     * Builds the query for fetching count of new replies.
     *
     * @param response
     * @return String LiQL for fetch new reply count for the set of Messages.
     */
    private String buildNewReplyQuery(List<LiBaseModel> response) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (LiBaseModel liBaseModel : response) {
            LiMessage message = ((LiTargetModel)
                    liBaseModel.getModel()).getLiMessage();
            if (!isFirst) {
                sb.append(",");
            } else {
                isFirst = false;
            }
            sb.append("'");
            sb.append(message.getId());
            sb.append("'");
        }

        return "SELECT id, user_context.read, parent FROM messages WHERE parent.id in (" +
                sb.toString() +
                ")";
    }
}