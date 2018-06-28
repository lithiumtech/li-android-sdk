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

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lithium.community.android.api.LiClient;
import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.manager.LiClientManager;
import com.lithium.community.android.manager.LiSDKManager;
import com.lithium.community.android.model.request.LiClientRequestParams;
import com.lithium.community.android.model.response.LiMessage;
import com.lithium.community.android.rest.LiAsyncRequestCallback;
import com.lithium.community.android.rest.LiBaseRestRequest;
import com.lithium.community.android.rest.LiGetClientResponse;
import com.lithium.community.android.rest.LiPostClientResponse;
import com.lithium.community.android.ui.R;
import com.lithium.community.android.ui.components.adapters.LiCreateMessageAdapter;
import com.lithium.community.android.ui.components.custom.ui.LiRoundedImageView;
import com.lithium.community.android.ui.components.utils.LiImageHelper;
import com.lithium.community.android.ui.components.utils.LiSDKConstants;
import com.lithium.community.android.ui.components.utils.LiUIUtils;
import com.lithium.community.android.utils.LiCoreSDKConstants;
import com.lithium.community.android.utils.LiImageUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * This fragment extends {@link DialogFragment}
 * Has the responsibility for displayed the Message Authoring UI
 * This fragment is also used for the displaying the Reply UI for a selected message.
 * <p>
 * {@link Boolean} canSelectABoard  is used to define whether the UI to be inflated is for Creating a message
 * or for replying to a selected message
 */
public class LiCreateMessageFragment extends DialogFragment {

    public static final String IMAGE_TYPE = "image/*";
    public static final String COMMUNITY_SUFFIX = "-community";
    public static final String IMAGE_EXTENSION = ".jpg";

    public static final String EXTRA_SELECTED_BOARD = "board";
    public static final String EXTRA_SELECTED_BOARD_ID = "board_id";

    private static final int SELECTED_BOARD_ID_REQUEST = 1001;
    private static final int READ_EXTERNAL_STORAGE_REQUEST = 1002;
    public LiMessage selectedMessage;
    protected RecyclerView recyclerView;
    View mView;
    Picasso pablo;
    String askQuestionBodyText;
    String askQuestionSubjectText;
    LiCreateMessageAdapter adapter;
    File selectedPhotoFile;
    private LiRoundedImageView selectedImageImgView;
    private ImageView askQuestionCameraIcon;
    private String selectedBoard;
    private Long selectedMessageId;
    private String selectedMessageSubject;
    private String selectedBoardId;
    private String selectedImageId;
    private String selectedImageName;
    private ImageView removeSelectedImage;
    private boolean canSelectABoard;
    private TextView selectCategoryBtn;
    private TextView selectCategoryLabel;
    private Menu menu;
    private ProgressBar postQuestionProgBar;
    private String imageDescription;
    private String imageAbsolutePath;
    private Uri outputFileUri;

    private boolean enablePostMenuItem = true;

    public LiCreateMessageFragment() {
    }

    public static LiCreateMessageFragment newInstance(Bundle bundle) {
        LiCreateMessageFragment fragment = new LiCreateMessageFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

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
                        selectedMessage = (LiMessage) originalArticleResponse.getResponse().get(0);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initializeAdapter();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(Exception exception) {
                }
            });
        } catch (LiRestResponseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            selectedImageName = savedInstanceState.getString(MediaStore.EXTRA_MEDIA_TITLE);
            outputFileUri = savedInstanceState.getParcelable(MediaStore.EXTRA_OUTPUT);
            String message = savedInstanceState.getString(Intent.EXTRA_TEXT);
            if (!TextUtils.isEmpty(message)) {
                askQuestionBodyText = message.replaceAll("\\n", "<br />");
            }
            askQuestionSubjectText = savedInstanceState.getString(Intent.EXTRA_TITLE);
            if (outputFileUri != null && !TextUtils.isEmpty(selectedImageName)) {
                handleImageSelection(null);
            }
            if (adapter != null) {
                adapter.setCurrentMessage(message);
                adapter.setCurrentTitle(askQuestionSubjectText);
            }

            String selectedBoard = savedInstanceState.getString(EXTRA_SELECTED_BOARD);
            String selectedBoardId = savedInstanceState.getString(EXTRA_SELECTED_BOARD_ID);
            if (!TextUtils.isEmpty(selectedBoard) && !TextUtils.isEmpty(selectedBoardId)) {
                setSelectedBoard(selectedBoard, selectedBoardId);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MediaStore.EXTRA_OUTPUT, outputFileUri);
        if (!TextUtils.isEmpty(askQuestionBodyText)) {
            outState.putString(Intent.EXTRA_TEXT, askQuestionBodyText.replaceAll("<br />", "\\n"));
        }
        if (!TextUtils.isEmpty(selectedImageName)) {
            outState.putString(MediaStore.EXTRA_MEDIA_TITLE, selectedImageName);
        }
        outState.putString(Intent.EXTRA_TITLE, askQuestionSubjectText);
        if (!TextUtils.isEmpty(selectedBoard)) {
            outState.putString(EXTRA_SELECTED_BOARD, selectedBoard);
        }
        if (!TextUtils.isEmpty(selectedBoardId)) {
            outState.putString(EXTRA_SELECTED_BOARD_ID, LiSDKConstants.LI_BOARD_ID_PREFIX + selectedBoardId);
        }

    }

    /**
     * Set up the views and populated the views if the UI is used a reply to a selected message.
     */
    private void populateData() {
        Bundle bundle = getArguments();
        recyclerView = mView.findViewById(R.id.li_ask_q_recycler);
        canSelectABoard = bundle.getBoolean(LiSDKConstants.ASK_Q_CAN_SELECT_A_BOARD, true);
        boolean updateTitle = bundle.getBoolean(LiSDKConstants.UPDATE_TOOLBAR_TITLE, true);
        selectedMessageSubject = getString(R.string.li_reply_subject_prepender) + bundle.getString(LiSDKConstants.ORIGINAL_MESSAGE_TITLE);
        selectCategoryBtn =
                mView.findViewById(R.id.li_ask_question_select_category_btn);
        selectCategoryLabel =
                mView.findViewById(R.id.li_ask_question_select_category_label);
        selectedImageImgView = mView.findViewById(R.id.li_selected_image_to_upload);
        askQuestionCameraIcon = mView.findViewById(R.id.li_ask_question_camera);
        removeSelectedImage = mView.findViewById(R.id.li_removeSelectedImage);

        if (!canSelectABoard) {
            selectedMessageId = bundle.getLong(LiSDKConstants.SELECTED_MESSAGE_ID);
            selectCategoryLabel.setVisibility(View.GONE);
            selectCategoryBtn.setVisibility(View.GONE);
            if (updateTitle) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().setTitle(selectedMessageSubject);
                    }
                });
            }
            getOriginalMessage();
        } else {
            initializeAdapter();
            selectCategoryLabel.setVisibility(View.VISIBLE);
            selectCategoryBtn.setVisibility(View.VISIBLE);
            selectCategoryBtn.setText(selectedBoard);
            View.OnClickListener l = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBrowseDialog();
                }
            };
            selectCategoryLabel.setOnClickListener(l);
            selectCategoryBtn.setOnClickListener(l);
        }
        askQuestionCameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermsAndHandleImage();
            }
        });

        showSelectedImage(false);
        removeSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageImgView.setImageBitmap(null);
                deleteDownloadedFile();
                showSelectedImage(false);
            }
        });
    }

    public void startImageChooserActivity() {
        // Determine Uri of camera image to save.
        final String randomName = UUID.randomUUID().toString().substring(0, 5) + IMAGE_EXTENSION;
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileDirectory = String.valueOf(android.os.Environment.getExternalStorageDirectory())
                + File.separator
                + LiSDKManager.getInstance().getCredentials().getTenantId()
                + COMMUNITY_SUFFIX
                + File.separator;
        final File communityRoot = new File(fileDirectory);
        communityRoot.mkdirs();
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            selectedPhotoFile = new File(communityRoot, randomName);
        } else {
            selectedPhotoFile = new File(getActivity().getCacheDir(), randomName);
        }
        outputFileUri = Uri.fromFile(selectedPhotoFile);

        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType(IMAGE_TYPE);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.li_select_image_source));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        getActivity().startActivityForResult(chooserIntent, LiSDKConstants.PICK_IMAGE_REQUEST);
    }

    private void initializeAdapter() {
        adapter = new LiCreateMessageAdapter(getActivity(), canSelectABoard, this);
        recyclerView.setAdapter(adapter);
        adapter.setCurrentTitle(askQuestionSubjectText);
        adapter.setCurrentMessage(TextUtils.isEmpty(askQuestionBodyText) ? null : askQuestionBodyText.replaceAll("<br />", "\\n"));
    }

    protected void openBrowseDialog() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(LiSDKConstants.DISPLAY_AS_DIALOG, true);
        LiBrowseFragment liBrowseFragment =
                LiBrowseFragment.newInstance(bundle);
        liBrowseFragment.setTargetFragment(LiCreateMessageFragment.this,
                SELECTED_BOARD_ID_REQUEST);
        liBrowseFragment.show(getFragmentManager(), LiBrowseFragment.class.getName());
    }

    public void focusInlineReply() {
        recyclerView.smoothScrollToPosition(0);
    }

    /**
     * Focus on the Authoring textboxes if in reply mode for a selected message.
     */
    public void focusAuthoring() {
        if (!canSelectABoard) {
            recyclerView.smoothScrollToPosition(1);
        } else {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * This method checks the permission that the app has and asks
     */
    public void checkPermsAndHandleImage() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST);
        } else {
            startImageChooserActivity();
        }
    }

    /**
     * Get the selected board name and id from the {@link LiBrowseFragment} dialog
     *
     * @param selectedBoard   Selected board name
     * @param selectedBoardId Selected board Id
     */
    public void setSelectedBoard(String selectedBoard, String selectedBoardId) {
        this.selectedBoard = selectedBoard;

        this.selectedBoardId = selectedBoardId.substring(
                selectedBoardId.indexOf(LiSDKConstants.LI_BOARD_ID_PREFIX)
                        + LiSDKConstants.LI_BOARD_ID_PREFIX.length(),
                selectedBoardId.length());
        selectCategoryBtn.setText(selectedBoard);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.li_fragment_create_message, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.selectedBoard = bundle.getString(LiSDKConstants.SELECTED_BOARD_NAME);
            this.selectedBoardId = bundle.getString(LiSDKConstants.SELECTED_BOARD_ID);
        }
        pablo = Picasso.with(getActivity());
        postQuestionProgBar = mView.findViewById(R.id.li_post_question_prog_bar);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        populateData();
        return mView;
    }

    /**
     * When the user selects an image from the gallery show the thumbnail in the UI.
     *
     * @param show whether to show the thumbnail or hide it.
     */
    protected void showSelectedImage(boolean show) {
        if (!show) {
            selectedImageImgView.setVisibility(View.GONE);
            selectedImageImgView.setImageBitmap(null);
            selectedImageId = null;
            selectedImageName = null;
            removeSelectedImage.setVisibility(View.GONE);
            askQuestionCameraIcon.setVisibility(View.VISIBLE);
        } else {
            selectedImageImgView.setVisibility(View.VISIBLE);
            removeSelectedImage.setVisibility(View.VISIBLE);
            askQuestionCameraIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        selectedImageImgView.setImageBitmap(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        inflater.inflate(R.menu.li_menu_ask_question, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem postMenuItem = menu.findItem(R.id.li_action_post_question);
        if (postMenuItem != null) {
            postMenuItem.setEnabled(enablePostMenuItem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.li_action_post_question) {

            if (!isFormValid()) {
                return true;
            }
            enableEditing(false);
            uploadImageToCommunity();

            return true;
        } else if (id == android.R.id.home) {
            handleBackButton();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Post reply that was constructed for a selected message.
     *
     * @throws LiRestResponseException
     */
    protected void postReply() throws LiRestResponseException {
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiCreateReplyClientRequestParams(getActivity(), selectedMessageSubject,
                askQuestionBodyText, selectedMessageId, selectedImageId, selectedImageName);
        LiClient replyMessageClient = LiClientManager.getCreateReplyClient(liClientRequestParams);

        replyMessageClient.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
            @Override
            public void onSuccess(LiBaseRestRequest liBaseRestRequest, LiPostClientResponse liPostClientResponse) {
                if (!isAdded() || getActivity() == null) {
                    return;
                }
                if (liPostClientResponse.getResponse().getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                    LiUIUtils.showInAppNotification(getActivity(), R.string.li_replyPostSuccess);
                    deleteDownloadedFile();
                    Intent intent = new Intent(getString(R.string.li_messsage_create_successful));
                    getActivity().sendBroadcast(intent);
                    getActivity().finish();
                } else {
                    LiUIUtils.showInAppNotification(getActivity(), R.string.li_replyPostError);
                }
                enableEditing(true);
            }

            @Override
            public void onError(Exception e) {
                if (isAdded() || getActivity() == null) {
                    enableEditing(true);
                    LiUIUtils.showInAppNotification(getActivity(), R.string.li_replyPostError);
                }
            }
        });
    }

    private void deleteDownloadedFile() {
        if (selectedPhotoFile != null && selectedPhotoFile.exists()) {
            if (selectedPhotoFile.delete()) {
                Log.d(LiSDKConstants.GENERIC_LOG_TAG, "file Deleted :" + selectedPhotoFile.getPath());
            } else {
                if (selectedPhotoFile.isDirectory()) {
                    Log.d(LiSDKConstants.GENERIC_LOG_TAG, "file not Deleted because it's a directory");
                }
                Log.d(LiSDKConstants.GENERIC_LOG_TAG, "file not Deleted :" + selectedPhotoFile.getPath());
            }
        }
    }

    /**
     * Post the message to the community
     *
     * @throws LiRestResponseException
     */
    protected void postQuestion() throws LiRestResponseException {
        LiClientRequestParams liClientRequestParams = new LiClientRequestParams.LiCreateMessageClientRequestParams(getActivity(), askQuestionSubjectText,
                askQuestionBodyText, selectedBoardId, selectedImageId, selectedImageName);
        LiClient postQuestionClient = LiClientManager.getCreateMessageClient(liClientRequestParams);
        postQuestionClient.processAsync(new LiAsyncRequestCallback<LiPostClientResponse>() {
            @Override
            public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                    LiPostClientResponse liClientResponse)
                    throws LiRestResponseException {
                if (!isAdded() || getActivity() == null) {
                    return;
                }
                switch (liClientResponse.getResponse().getHttpCode()) {
                    case LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL:
                        LiUIUtils.showInAppNotification(getActivity(), R.string.li_replyPostSuccess);
                        deleteDownloadedFile();
                        Intent intent = new Intent(getString(R.string.li_messsage_create_successful));
                        getActivity().sendBroadcast(intent);
                        getActivity().finish();
                        break;
                    case LiCoreSDKConstants.HTTP_CODE_FORBIDDEN:
                        LiUIUtils.showInAppNotification(getActivity(), R.string.li_create_message_no_access_error);
                        break;
                    default:
                        LiUIUtils.showInAppNotification(getActivity(), R.string.li_create_message_error);
                        break;
                }
                enableEditing(true);
            }

            @Override
            public void onError(Exception e) {
                if (isAdded() || getActivity() == null) {
                    enableEditing(true);
                    LiUIUtils.showInAppNotification(getActivity(), R.string.li_create_message_error);
                }
            }
        });
    }

    /**
     * Enable or Disable form fields when the user presses Post Question button on the form.
     *
     * @param enable
     */
    protected void enableEditing(final boolean enable) {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(new EditsEnablerRunnable(enable));
        }
    }

    /**
     * Validate if the data entered in the form is valid or not.
     * If not then show appropriate error message.
     * If valid then it returns true.
     *
     * @return returns true if the form data is valid
     */
    protected boolean isFormValid() {
        boolean isFormValid = true;
        int errorResId = -1;
        if (canSelectABoard) {
            if (TextUtils.isEmpty(askQuestionSubjectText)) {
                errorResId = R.string.li_create_message_title_validation;
                isFormValid = false;
            }
        }

        if (isFormValid && TextUtils.isEmpty(askQuestionBodyText)) {
            errorResId = R.string.li_create_message_body_validation;
            isFormValid = false;
        }

        if (isFormValid && !TextUtils.isEmpty(selectedImageId) && TextUtils.isEmpty(askQuestionBodyText)) {
            errorResId = R.string.li_create_message_body_validation;
            isFormValid = false;
        }

        if (isFormValid && canSelectABoard && (selectedBoardId == null || selectedBoardId.isEmpty())) {
            errorResId = R.string.li_create_message_board_not_selected;
            isFormValid = false;
            openBrowseDialog();
        }


        if (!isFormValid) {
            LiUIUtils.showInAppNotification(getActivity(), errorResId);
        }
        return isFormValid;
    }

    /**
     * Handle the flow when the user selects an image in the gallery
     *
     * @param intent Intent that is returned by the android system from gallery image selection
     */
    public void handleImageSelection(Intent intent) {
        if (isAdded() && getActivity() != null) {
            if (intent != null && intent.getData() != null) {
                outputFileUri = intent.getData();
            }

            imageAbsolutePath = LiImageHelper.getPath(getActivity(), outputFileUri);
            if (TextUtils.isEmpty(imageAbsolutePath)) {
                //The code couldn't find any image selected by the user.
                LiUIUtils.showInAppNotification(getActivity(), R.string.li_image_upload_generic_error);
                return;
            }

            selectedImageName = imageAbsolutePath.substring(imageAbsolutePath.lastIndexOf("/") + 1);
            imageDescription = getResources().getString(
                    R.string.li_uploaded_img_default_desc);
            String[] supportedImagFormats = getResources().getStringArray(R.array.li_supported_image_formats);
            boolean isFormatValid = false;
            for (String format : supportedImagFormats) {
                if (imageAbsolutePath.endsWith(format)) {
                    isFormatValid = true;
                    break;
                }
            }
            if (!isFormatValid) {
                LiUIUtils.showInAppNotification(getActivity(), R.string.li_image_format_not_supported);
                showSelectedImage(false);
                return;
            }

            File originalFile = new File(imageAbsolutePath);
            long fileLength = originalFile.length();
            if (fileLength >= LiSDKConstants.LI_MAX_IMAGE_UPLOAD_SIZE) {
                LiUIUtils.showInAppNotification(getActivity(), R.string.li_image_size_too_large);
                showSelectedImage(false);
                return;
            }

            if (originalFile.length() >= LiCoreSDKConstants.LI_MIN_IMAGE_SIZE_TO_COMPRESS) {
                int imageCompressionSize = getResources().getDimensionPixelSize(
                        R.dimen.li_image_compression_size);
                int imageQuality = getResources().getInteger(R.integer.li_image_compression_quality);
                selectedPhotoFile = LiImageUtils.compressImage(
                        imageAbsolutePath, selectedImageName, getActivity(),
                        imageCompressionSize, imageCompressionSize, imageQuality);
                outputFileUri = Uri.fromFile(selectedPhotoFile);
                imageAbsolutePath = selectedPhotoFile.getAbsolutePath();
            }

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    selectedImageImgView.setImageBitmap(bitmap);
                    showSelectedImage(true);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    LiUIUtils.showInAppNotification(getActivity(), R.string.li_imageNotLoaded);
                    showSelectedImage(false);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            selectedImageImgView.setTag(target);
            pablo.load(outputFileUri).resize(50, 50)
                    .onlyScaleDown().into(target);
        }
    }

    protected void uploadImageToCommunity() {
        if (TextUtils.isEmpty(selectedImageName)) {
            post();
            return;
        }

        try {
            LiClientRequestParams liClientRequestParams = new LiClientRequestParams
                    .LiUploadImageClientRequestParams(
                    getActivity(), selectedImageName, imageDescription,
                    selectedImageName, imageAbsolutePath);
            LiClient uploadImage = LiClientManager.getUploadImageClient(liClientRequestParams);
            LiAsyncRequestCallback<LiPostClientResponse> callback = new LiAsyncRequestCallback<LiPostClientResponse>() {
                @Override
                public void onSuccess(LiBaseRestRequest liBaseRestRequest,
                        LiPostClientResponse liPostClientResponse)
                        throws LiRestResponseException {
                    if (liPostClientResponse.getHttpCode() == LiCoreSDKConstants.HTTP_CODE_SUCCESSFUL) {
                        Log.d(LiSDKConstants.GENERIC_LOG_TAG,
                                liPostClientResponse.getResponse().getData().toString());
                        selectedImageId = liPostClientResponse.getResponse().getData()
                                .get("data").getAsJsonObject().get("id").getAsString();
                        post();
                    } else {
                        LiUIUtils.showInAppNotification(getActivity(), R.string.li_imageNotLoaded);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showSelectedImage(false);
                                }
                            });
                        }
                        Log.e(LiSDKConstants.GENERIC_LOG_TAG,
                                liPostClientResponse.getResponse().getMessage());
                    }
                }

                @Override
                public void onError(Exception e) {
                    LiUIUtils.showInAppNotification(getActivity(), R.string.li_create_message_image_upload_error);
                    enableEditing(true);
                    Log.e(LiSDKConstants.GENERIC_LOG_TAG, "Exception - " + e.getMessage());
                }
            };
            uploadImage.processAsync(callback, imageAbsolutePath, selectedImageName);
        } catch (LiRestResponseException e) {
            LiUIUtils.showInAppNotification(getActivity(), R.string.li_create_message_image_upload_error);
            enableEditing(true);
            Log.e(LiSDKConstants.GENERIC_LOG_TAG, "Exception " + e.getMessage());
        }
    }

    protected void post() {
        try {
            if (canSelectABoard) {
                postQuestion();
            } else {
                postReply();
            }
        } catch (LiRestResponseException e) {
            LiUIUtils.showInAppNotification(getActivity(), R.string.li_create_message_image_upload_error);
            enableEditing(true);
            Log.e(LiSDKConstants.GENERIC_LOG_TAG, e.toString());
        }
    }

    /**
     * Handles the back button click behavior.
     * If the user has entered some data which has not been posted yet,
     * then the user will see a confirmation box whether he wants to discard the form data and return back.
     */
    public void handleBackButton() {
        Activity activity = getActivity();
        if (!TextUtils.isEmpty(askQuestionBodyText) || !TextUtils.isEmpty(askQuestionSubjectText) || !TextUtils.isEmpty(selectedImageName)) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Activity activity = getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                        default:
                    }
                }
            };

            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setMessage(getString(R.string.li_are_you_sure))
                        .setPositiveButton(getString(R.string.li_yes), dialogClickListener)
                        .setNegativeButton(R.string.li_no, dialogClickListener).show();
            }
        } else {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * Set the Subject text from {@link LiCreateMessageAdapter} row
     *
     * @param subject Subject text entered by the user in the subject field
     */
    public void setAskQuestionSubject(String subject) {
        this.askQuestionSubjectText = subject;
    }

    /**
     * Set the Body text from {@link LiCreateMessageAdapter} row
     *
     * @param body text entered by the user in the body field
     */
    public void setAskQuestionBody(String body) {
        this.askQuestionBodyText = body.replaceAll("\\n", "<br />");
    }

    private class EditsEnablerRunnable implements Runnable {
        private boolean enable;

        public EditsEnablerRunnable(boolean enable) {
            this.enable = enable;
        }

        @Override
        public void run() {
            postQuestionProgBar.setVisibility(enable ? View.GONE : View.VISIBLE);
            MenuItem item = menu.findItem(R.id.li_action_post_question);
            item.setEnabled(enable);
            selectCategoryBtn.setEnabled(enable);
            selectCategoryLabel.setEnabled(enable);
            removeSelectedImage.setEnabled(enable);
            askQuestionCameraIcon.setEnabled(enable);
            adapter.enableSubjectBodyEditing(enable);
            enablePostMenuItem = enable;
            getActivity().invalidateOptionsMenu();
        }
    }
}