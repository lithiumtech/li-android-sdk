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
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.model.response.LiTargetModel;
import com.lithium.community.android.model.response.LiUser;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.activities.LiCreateMessageActivity;
import com.lithium.community.android.ui.components.adapters.LiConversationAdapter;
import com.lithium.community.android.ui.components.custom.ui.LiRoundedImageView;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * This fragment extends {@link LiBaseFragment} to display the
 * conversation that is present in the community for a selected message
 * Usage:
 * Intent i = new Intent(context, LiConversationActivity.class);
 * i.putExtra(LiSDKConstants.SELECTED_MESSAGE_ID, message.getId());
 * <p>
 * {@link LiSDKConstants#SELECTED_MESSAGE_ID} It requires a message/topic id to display the conversation.
 */
public class LiConversationFragment extends LiBaseFragment implements LiConversationAdapter.ActionInProgressListener {
    LiMessage originalMessage;
    private Long selectedMessageId;
    private boolean updateTitle;

    public LiConversationFragment() {
    }

    public static LiConversationFragment newInstance(Bundle bundle) {
        LiConversationFragment fragment = new LiConversationFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        if (adapter == null) {
            adapter = new LiConversationAdapter(response, mListener, getActivity(), recyclerView, this);
            ((LiConversationAdapter)adapter).setActionInProgressListener(this);
        }
        return adapter;
    }

    @Override
    protected void getClient() throws LiRestResponseException {
        Bundle bundle = getArguments();
        selectedMessageId = bundle.getLong(LiSDKConstants.SELECTED_MESSAGE_ID);
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiRepliesClientRequestParams(getActivity(), selectedMessageId);
        client = LiClientManager.getRepliesClient(liClientRequestParams);
        gson = client.getGson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.li_fragment_conversation, container, false);
        refreshOnResume = true;
        setupViews(view, R.string.li_noMessagesTV, -1, null, View.GONE);
        setHasOptionsMenu(true);
        LinearLayout footerLayout = view.findViewById(R.id.li_conversation_footer);
        LiRoundedImageView footerUserImage = view.findViewById(R.id.li_user_image);
        Bundle bundle = getArguments();
        updateTitle = bundle.getBoolean(LiSDKConstants.UPDATE_TOOLBAR_TITLE, true);
        LiUser user = LiSDKManager.getInstance().getLoggedInUser();
        TypedArray typedArrForBrowse = getActivity().getTheme().obtainStyledAttributes(
                new int[]{R.attr.li_theme_messageUserAvatarIcon});
        int avatarDefaultIcon = typedArrForBrowse.getResourceId(0, -1);
        typedArrForBrowse.recycle();
        if (user != null && user.getAvatar() != null && user.getAvatar().getMessage() != null) {
            Picasso.with(getActivity())
                    .load(user.getAvatar().getMessage())
                    .error(avatarDefaultIcon)
                    .into(footerUserImage);
        } else {
            Picasso.with(getActivity())
                    .load(avatarDefaultIcon)
                    .into(footerUserImage);
        }

        footerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFetchComplete) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, false);
                    bundle.putLong(LiSDKConstants.SELECTED_MESSAGE_ID, selectedMessageId);
                    bundle.putString(LiSDKConstants.ORIGINAL_MESSAGE_TITLE, originalMessage.getSubject());
                    Intent i = new Intent(view.getContext(), LiCreateMessageActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });

        return view;
    }

    @Override
    protected void fetchData() {
        isFetchComplete = false;
        isErrorWhileFetch = false;
        if (!LiSDKManager.isInitialized() || !LiUIUtils.isNetworkAvailable(getActivity())) {
            setupErrorMessage();
            return;
        }
        try {
            if (client == null) {
                getClient();
            }
            LiClientRequestParams.LiBeaconPostClientRequestParams beaconParams = new LiClientRequestParams.LiBeaconPostClientRequestParams(getActivity());
            beaconParams.setTargetType(LiCoreSDKConstants.LI_BEACON_TARGET_TYPE_CONVERSATION);
            beaconParams.setTargetId(String.valueOf(selectedMessageId));
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

            //Fetch the conversation for the selected message
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
                        if (response != null && !response.isEmpty()) {
                            isFetchComplete = true;
                            isErrorWhileFetch = false;
                            originalMessage = ((LiTargetModel)
                                    response.get(0).getModel()).getLiMessage();
                            if (updateTitle) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //first check if the context is not null and then update the title
                                        if (getActivity() != null) {
                                            getActivity().setTitle(originalMessage.getSubject());
                                        }
                                    }
                                });
                            }
                            refreshUI();
                        } else {
                            getOriginalMessage();
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
     * This method gets the original message that was clicked and adds the result to the list of
     * Replies that are present for the Original message
     */
    private void getOriginalMessage() {
        try {
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiMessageClientRequestParams(getActivity(), selectedMessageId);
            LiClient messageClient = LiClientManager.getMessageClient(liClientRequestParams);
            messageClient.processAsync(new LiAsyncRequestCallback<LiGetClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest request,
                        LiGetClientResponse originalArticleResponse)
                        throws LiRestResponseException {
                    if (!isAdded() || getActivity() == null) {
                        return;
                    }
                    if (originalArticleResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        if (null != originalArticleResponse.getResponse()
                                && !originalArticleResponse.getResponse().isEmpty()) {
                            if (response == null) {
                                response = new ArrayList<>();
                            }
                            isFetchComplete = true;
                            isErrorWhileFetch = false;
                            originalMessage =
                                    (LiMessage) originalArticleResponse.getResponse().get(0);
                            if (updateTitle) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getActivity().setTitle(originalMessage.getSubject());
                                    }
                                });
                            }

                            response.add(0, originalMessage);
                        } else {
                            isFetchComplete = true;
                            isErrorWhileFetch = true;
                        }
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
            Log.e("Subscriptions", e.getMessage());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.li_menu_messages, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.li_action_share) {
            final Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("text/plain");
            if (response != null && !response.isEmpty()
                    && response.get(0) != null && originalMessage != null) {
                LiMessage originalMessage = (LiMessage) response.get(0).getModel();
                intent.putExtra(Intent.EXTRA_SUBJECT, originalMessage.getSubject());
                intent.putExtra(Intent.EXTRA_TEXT, originalMessage.getViewHref());

                startActivity(Intent.createChooser(intent, getString(R.string.li_share_via)));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void actionInProgress(boolean inProgress) {
        getActivity().runOnUiThread(new UiAction(inProgress));
    }

    private class UiAction implements Runnable {

        private final boolean inProgress;

        private UiAction(boolean inProgress) {
            this.inProgress = inProgress;
        }

        @Override
        public void run() {
            setRefreshing(inProgress);
        }
    }
}