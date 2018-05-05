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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.adapters.LiMessageListAdapter;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;

/**
 * This fragment extends {@link LiBaseFragment} and displays the Search results for a given keyword.
 * This fragment requires the search query from the parent screen i.e. either LiSearchActivity or custom code
 * Usage:
 * <pre>
 *   String searchQuery = getArguments().getString(LiSDKConstants.LI_SEARCH_QUERY);
 *   </pre>
 * <p>
 * {@link LiSDKConstants}.LI_SEARCH_QUERY search query provided by the calling code
 */
public class LiSearchFragment extends LiBaseFragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LiSearchFragment() {
    }

    public static LiSearchFragment newInstance(Bundle bundle) {
        LiSearchFragment fragment = new LiSearchFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void getClient() throws LiRestResponseException {
        String searchQuery = getArguments().getString(LiSDKConstants.LI_SEARCH_QUERY);
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiSearchClientRequestParams(getActivity(), searchQuery);
        client = LiClientManager.getSearchClient(liClientRequestParams);
        gson = client.getGson();
        LiClientRequestParams.LiBeaconPostClientRequestParams beaconParams = new LiClientRequestParams.LiBeaconPostClientRequestParams(getActivity());
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

    @Override
    protected RecyclerView.Adapter getAdapter() {
        if (adapter == null) {
            adapter = new LiMessageListAdapter(response, mListener, false, getActivity());
        }
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        refreshOnResume = true;
        return setupViews(inflater, container, R.string.li_noSearchResultsTV, -1, null, View.GONE);
    }
}