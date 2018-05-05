/*
 * LiSDKConstants.java
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

package com.lithium.community.android.ui.components.utils;

/**
 * Created by sumit.pannalall on 10/2/16.
 */
public class LiSDKConstants {
    public static final String GENERIC_LOG_TAG = "LI_SDK_UI";
    public static final String APPLY_ARTICLES_COMMON_HEADERS = "APPLY_ARTICLES_COMMON_HEADERS";
    //Following constants act as keys for the Bundle when we want to share things using intents
    public static final String ASK_Q_CAN_SELECT_A_BOARD = "ASK_Q_CAN_SELECT_A_BOARD";
    public static final String ASK_Q_SELECTED_BOARD = "ASK_Q_SELECTED_CATEGORY";
    public static final String ASK_Q_SELECTED_BOARD_ID = "ASK_Q_SELECTED_CATEGORY_ID";
    public static final String SELECTED_MESSAGE_ID = "SELECTED_MESSAGE_ID";
    public static final String DISPLAY_AS_DIALOG = "DISPLAY_AS_DIALOG";
    public static final String LI_SEARCH_QUERY = "LI_SEARCH_QUERY";
    public static final String SELECTED_BOARD_ID = "SELECTED_BOARD_ID";
    public static final String SELECTED_BOARD_NAME = "SELECTED_BOARD_NAME";
    public static final String UPDATE_TOOLBAR_TITLE = "UPDATE_TOOLBAR_TITLE";
    public static final String ORIGINAL_MESSAGE_TITLE = "ORIGINAL_MESSAGE_TITLE";
    public static final String FROM_ASK_Q_FLOW = "FROM_ASK_Q_FLOW";

    public static final int PICK_IMAGE_REQUEST = 1001;
    //Actual values that come as IDs, values, etc
    public static final String LI_BOARD_ID_PREFIX = "board:";
    public static final String LI_CATEGORY_ID_PREFIX = "community:";
    public static final String LI_QUESTIONS_UNREAD_REPLY_COUNT = "LI_QUESTIONS_UNREAD_REPLY_COUNT";
    public static final long LI_MAX_IMAGE_UPLOAD_SIZE = 15 * 1024 * 1024; //15MB
    public static final String REPORT_ABUSE_AUTHOR_ID = "REPORT_ABUSE_AUTHOR_ID";
    public static final String REPORT_ABUSE_MESSAGE_ID = "REPORT_ABUSE_MESSAGE_ID";
}
