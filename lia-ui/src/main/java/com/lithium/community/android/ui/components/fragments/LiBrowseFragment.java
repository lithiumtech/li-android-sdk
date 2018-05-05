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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiBrowse;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiClientResponse;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.activities.LiMessageListActivity;
import com.lithium.community.android.ui.components.adapters.LiBrowseAdapter;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This fragment takes care of displaying the Categories, Subcategories and Boards present inside a Category or a Subcategor
 * This extends {@link LiBaseFragment} for reusing the Common view like Error Layout, Login layout, etc.
 * There are two servers calls in this fragment.
 * One is two fetch all the Categories and Subcategories present in a particular community.
 * Second call is for the boards that are present inside a Category or Subcategory.
 * <p>
 * This fragment can be used as a full screen as well as a {@link android.support.v4.app.DialogFragment}
 * <p>
 * For using it as a dialog this flag needs to be set: {@link LiSDKConstants}.DISPLAY_AS_DIALOG in the bundle.
 * Default is false i.e. the browse screen will be full screen
 * <p>
 * If the title needs to be update this flag needs to be set: {@link LiSDKConstants}.UPDATE_TOOLBAR_TITLE. Default is true.
 */
public class LiBrowseFragment extends LiBaseFragment {
    boolean displayAsDialog;
    List<LiBaseModel> flattenedResponse;
    /**
     * naturally sort the categories.
     */
    Comparator<LiBaseModel> categoryComparator = new Comparator<LiBaseModel>() {
        @Override
        public int compare(LiBaseModel o1, LiBaseModel o2) {
            LiBrowse cat1 = (LiBrowse) o1.getModel();
            LiBrowse cat2 = (LiBrowse) o2.getModel();
            return cat1.getTitle().compareToIgnoreCase(cat2.getTitle());
        }
    };
    private LiBrowse currentCategory;
    private Map<LiBrowse, List<LiBaseModel>> categoryTree;
    private Stack<LiBrowse> parentNode;
    private boolean updateTitle;
    /**
     * Handles depth change when the user clicks on a particular category or clicks back button (or level up icon)
     */
    DepthChangeListener depthChangeListener = new DepthChangeListener() {
        @Override
        public void handleDepthChange(boolean increment) {
            if (increment) {
                swipeable.setRefreshing(true);
                getSubCatAndBoards();
            } else {
                if (!parentNode.isEmpty()) {
                    currentCategory = parentNode.pop();
                    if (currentCategory == null) {
                        populateRootLevel();
                    } else {
                        getSubCatAndBoards();
                    }
                } else {
                    swipeable.setRefreshing(false);
                }
            }
            setActionBarTitle();
        }
    };
    /**
     * Listener to handle Click on a Category/Subcategory/Board.
     * If Category is clicked, the subcateogories will be shown and also the boards in it.
     * If Boards then open {@link LiMessageListActivity} to show the Message inside that board.
     * <p>
     * If the fragment is displayed as a dialog then when the user clicks on the Board
     * it basically dismisses the dialog and passes on the selected board id to the target fragment
     */
    protected LiOnMessageRowClickListener mListener = new LiOnMessageRowClickListener() {
        @Override
        public void onMessageRowClick(LiBaseModel item) {
            if (swipeable.isRefreshing()) {
                return;
            }
            LiBrowse selectedCategory = (LiBrowse) item.getModel();
            if (selectedCategory.getId().startsWith(LiSDKConstants.LI_BOARD_ID_PREFIX)) {

                String boardId = getBoardId(selectedCategory);
                //if displaying as a dialog then dismiss the dialog returning the selected category
                //else go the screen where we can show articles present in the board.
                if (!displayAsDialog) {
                    Intent i = new Intent(getActivity(), LiMessageListActivity.class);
                    i.putExtra(LiSDKConstants.SELECTED_BOARD_ID, boardId);
                    i.putExtra(LiSDKConstants.SELECTED_BOARD_NAME, selectedCategory.getTitle());
                    getActivity().startActivity(i);
                } else {
                    LiCreateMessageFragment target = (LiCreateMessageFragment) getTargetFragment();
                    target.setSelectedBoard(selectedCategory.getTitle(), selectedCategory.getId());
                    dismiss();
                }
            } else {
                parentNode.push(currentCategory);
                currentCategory = selectedCategory;
                depthChangeListener.handleDepthChange(true);
            }
        }

        @Override
        public void onMessageRowLongClick(LiBaseModel item) {
            //do nothing
        }
    };

    public LiBrowseFragment() {
    }

    public static LiBrowseFragment newInstance(Bundle bundle) {
        LiBrowseFragment fragment = new LiBrowseFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    /**
     * This method handles the back button request.
     * If the the current level of the categories is at the root, it will return true and the request is propagated to the activity
     *
     * @return
     */
    public boolean handleBackButton() {
        boolean exit = parentNode == null || parentNode.isEmpty();
        if (!exit) {
            depthChangeListener.handleDepthChange(false);
        }
        return exit;
    }

    @Override
    public void onRefresh() {
        if (!displayAsDialog) {
            swipeable.setRefreshing(true);
            fetchData();
        } else {
            swipeable.setRefreshing(false);
        }
    }

    private void setActionBarTitle() {
        if (!displayAsDialog) {
            if (updateTitle && isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String title = getActivity().getString(R.string.li_title_activity_browse);
                        if (currentCategory != null) {
                            title = currentCategory.getTitle();
                        }
                        getActivity().setTitle(title);
                    }
                });
            }
        }
    }

    /**
     * show subcategories for selected category and also fetch boards for that category
     */
    protected void getSubCatAndBoards() {
        List<LiBaseModel> subCategories = categoryTree.get(currentCategory);
        if (parentNode.isEmpty()) {
            subCategories = getRootLevelCategories(flattenedResponse);
        }
        boolean clearExisting = true;
        if (subCategories != null && !subCategories.isEmpty()) {
            response.clear();
            response.addAll(subCategories);
            clearExisting = false;
        }
        if (currentCategory != null) {
            getBoardsAtALevel(currentCategory.getId(), clearExisting);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setNegativeButton(getString(R.string.li_cancel_dialog), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                }).setCancelable(true);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.li_browse_fragment_node_list, null, false);
        setupViews(v, R.string.li_noCategoriesTV, -1, null, View.GONE);
        View chooseCategoryHeader = v.findViewById(R.id.li_choose_category_header_container);
        chooseCategoryHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!parentNode.isEmpty()) {
                    depthChangeListener.handleDepthChange(false);
                } else {
                    dismiss();
                }

            }
        });
        b.setView(v);
        // request a window without the title
        Dialog dialog = b.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return b.create();
    }

    /**
     * extract BoardID from the general ID format ("board:<boardid>")
     *
     * @return
     */
    @NonNull
    protected String getBoardId(LiBrowse selectedCategory) {
        String selectedCategoryId = selectedCategory.getId();
        return selectedCategoryId.substring(
                selectedCategoryId.indexOf(LiSDKConstants.LI_BOARD_ID_PREFIX)
                        + LiSDKConstants.LI_BOARD_ID_PREFIX.length(),
                selectedCategoryId.length());
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        if (adapter == null) {
            adapter = new LiBrowseAdapter(response, mListener, getActivity(), depthChangeListener, displayAsDialog, this);
        }
        return adapter;
    }

    @Override
    protected void getClient() throws LiRestResponseException {
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiCategoryClientRequestParams(getActivity());
        client = LiClientManager.getCategoryClient(liClientRequestParams);
        gson = client.getGson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        displayAsDialog = bundle.getBoolean(LiSDKConstants.DISPLAY_AS_DIALOG, false);
        updateTitle = bundle.getBoolean(LiSDKConstants.UPDATE_TOOLBAR_TITLE, true);
        refreshOnResume = false;
        //display as dialog then handle the view creation in onCreateDialog method.
        if (displayAsDialog) {
            updateTitle = false;
            return inflater.inflate(R.layout.li_base_fragment_message_list, container, false);
        } else {
            return setupViews(inflater, container, R.string.li_noCategoriesTV, -1, null, View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        super.onDestroyView();
    }

    @Override
    protected void fetchData() {
        isFetchComplete = false;
        isErrorWhileFetch = false;
        parentNode = new Stack<>();
        if (!LiSDKManager.isInitialized()
                || !LiUIUtils.isNetworkAvailable(getActivity())) {
            setupErrorMessage();
            return;
        }
        try {
            if (client == null) {
                getClient();
            }
            //community level category fetch, hence appropriate beacon call
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

            client.processAsync(new LiAsyncRequestCallback<LiClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                        LiClientResponse liClientResponse) {
                    if (isAdded() && getActivity() != null && null != liClientResponse) {
                        if (liClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                            //get for community level categories
                            categoryTree = liClientResponse.getTransformedResponse();
                            flattenedResponse = (List<LiBaseModel>) liClientResponse.getResponse();
                            populateRootLevel();
                        } else {
                            isFetchComplete = true;
                            isErrorWhileFetch = true;
                            refreshUI();
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    isFetchComplete = true;
                    isErrorWhileFetch = true;
                    refreshUI();
                }
            });
        } catch (LiRestResponseException exception) {
            isFetchComplete = true;
            isErrorWhileFetch = true;
            refreshUI();
        }
    }

    private void populateRootLevel() {
        List<LiBaseModel> categories = getRootLevelCategories(flattenedResponse);
        response = new ArrayList<>(categories);
        //get boards present at this level
        String rootCategoryId = LiSDKConstants.LI_CATEGORY_ID_PREFIX + LiSDKManager.getInstance().getTenantId();
        getBoardsAtALevel(rootCategoryId, false);
    }

    /**
     * This method gets the root level categories by finding out the lowest value of depth of a node and builds a list
     *
     * @param responseModel flattened list of all the nodes
     * @return returns {@link List<LiBaseModel>} root level categories
     */
    private List<LiBaseModel> getRootLevelCategories(List<LiBaseModel> responseModel) {
        List<LiBaseModel> rootLevelCats = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        if (responseModel != null) {
            for (LiBaseModel baseModel : responseModel) {
                LiBrowse liBrowse = (LiBrowse) baseModel.getModel();
                int currentDepth = liBrowse.getDepth();
                if (currentDepth < min) {
                    rootLevelCats = new ArrayList<>();
                    rootLevelCats.add(liBrowse);
                    min = currentDepth;
                } else if (currentDepth == min) {
                    rootLevelCats.add(liBrowse);
                } else {
                    continue;
                }
            }
            Collections.sort(rootLevelCats, categoryComparator);
        }
        return rootLevelCats;
    }

    /**
     * Get boards at a particular level for a category
     * Clear existing if we don't want to show sub categories for whatever reason
     *
     * @param selectedCategoryId
     * @param clearExisting
     */
    private void getBoardsAtALevel(String selectedCategoryId, final boolean clearExisting) {
        if (getActivity() == null) {
            return;
        }
        try {
            LiClientRequestParams liCategoryBoardsClientRequestParams = new LiClientRequestParams.LiCategoryBoardsClientRequestParams(getActivity(),
                    selectedCategoryId);
            LiClient browseClient = LiClientManager.getCategoryBoardsClient(liCategoryBoardsClientRequestParams);

            if (parentNode.isEmpty()) {
                LiClientRequestParams liBoardsByDepthClientRequestParams = new LiClientRequestParams.LiBoardsByDepthClientRequestParams(getActivity(), 1);
                browseClient = LiClientManager.getBoardsByDepthClient(liBoardsByDepthClientRequestParams);
            } else {
                LiClientRequestParams.LiBeaconPostClientRequestParams beaconParams = new LiClientRequestParams.LiBeaconPostClientRequestParams(getActivity());
                beaconParams.setTargetType(LiCoreSDKConstants.LI_BEACON_TARGET_TYPE_NODE);
                beaconParams.setTargetId(selectedCategoryId);
                //beacon call for selected category or for community level
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

            browseClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request, LiGetClientResponse boardResponse)
                        throws LiRestResponseException {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    if (null != boardResponse && boardResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        if (null != boardResponse.getResponse()
                                && !boardResponse.getResponse().isEmpty()) {
                            //clear existing items if there are no categories, just boards
                            if (clearExisting) {
                                response.clear();
                            }
                            if (response == null) {
                                response = new ArrayList<>();
                            }
                            List<LiBaseModel> items = boardResponse.getResponse();
                            Collections.sort(items, categoryComparator);
                            response.addAll(items);
                        } else if (response == null || response.isEmpty()
                                || categoryTree.get(currentCategory) == null
                                || categoryTree.get(currentCategory).isEmpty()) {
                            //even if there are no row we still go inside one level and show an empty folder
                            if (response == null) {
                                response = new ArrayList<>();
                            } else if (clearExisting) {
                                response.clear();
                            }
                        }
                        isFetchComplete = true;
                        isErrorWhileFetch = false;
                    } else {
                        isFetchComplete = true;
                        isErrorWhileFetch = false;
                    }
                    refreshUI();
                }

                @Override
                public void onError(Exception exception) {
                    refreshUI();
                }
            });
        } catch (LiRestResponseException e) {
            Log.e("GET Boards", e.getMessage());
        }
    }

    public interface DepthChangeListener {
        void handleDepthChange(boolean increment);
    }
}