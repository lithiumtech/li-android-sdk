/*
 * LiSearchFragment.java
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

package lithium.community.android.sdk.ui.components.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.manager.LiClientManager;
import lithium.community.android.sdk.model.request.LiClientRequestParams;
import lithium.community.android.sdk.rest.LiAsyncRequestCallback;
import lithium.community.android.sdk.rest.LiBaseRestRequest;
import lithium.community.android.sdk.ui.components.R;
import lithium.community.android.sdk.ui.components.adapters.LiMessageListAdapter;
import lithium.community.android.sdk.ui.components.utils.LiSDKConstants;

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