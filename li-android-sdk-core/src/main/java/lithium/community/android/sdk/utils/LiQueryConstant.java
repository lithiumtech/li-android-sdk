/*
 * Constant.java
 * Created on Dec 27, 2016
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

package lithium.community.android.sdk.utils;

/**
 * This class has LIQL, TYPE and other constants.
 * Created by kunal.shrivastava on 12/7/16.
 */

public class LiQueryConstant {

    // LiQueryConstant for LiClientManager
    public static final String LI_ARTICLES_CLIENT_BASE_LIQL = "SELECT id, subject, view_href, post_time, kudos.sum(weight), conversation.style, conversation.solved, conversation.last_post_time, user_context, author, parent.id, metrics FROM messages";
    public static final String LI_SUBSCRIPTIONS_CLIENT_BASE_LIQL = "SELECT id, target.author, target.id, target.subject, target.post_time, target.kudos.sum(weight), target.body, target.conversation.style, target.conversation.solved, target.conversation.last_post_time FROM subscriptions";
    public static final String LI_BROWSE_CLIENT_BASE_LIQL = "select id, title, parent.id, parent.title, depth from nodes";
    public static final String LI_SEARCH_CLIENT_BASE_LIQL = "SELECT id, subject, view_href, board.id, post_time, kudos.sum(weight), conversation.style, conversation.solved, conversation.last_post_time, user_context, author FROM messages";
    public static final String LI_MESSAGE_CHILDREN_CLIENT_BASE_LIQL = "SELECT id, body, subject, view_href, post_time, kudos.sum(weight), can_accept_solution, is_solution, user_context.kudo, user_context.can_kudo, user_context.can_reply, user_context.read, user_context.can_delete, author.id, author.avatar, author.login, parent.id, metrics FROM messages";
    public static final String LI_QUESTIONS_CLIENT_BASE_LIQL = "SELECT id, subject, view_href, board.id, post_time, kudos.sum(weight), conversation.style, conversation.solved, author, conversation.last_post_time, user_context, author  FROM messages";
    public static final String LI_CATEGORY_CLIENT_BASE_LIQL = "select id, title, parent.id, parent.title, depth from nodes";
    public static final String LI_ARTICLES_BROWSE_CLIENT_BASE_LIQL = "SELECT id, subject, view_href, post_time, kudos.sum(weight), conversation.style, conversation.solved, conversation.last_post_time, user_context, author, parent.id, metrics FROM messages";
    public static final String LI_USER_DETAILS_CLIENT_BASE_LIQL = "SELECT email, href, last_visit_time, login, id, view_href, avatar from users";
    public static final String LI_MESSAGE_CLIENT_BASE_LIQL = "SELECT id, body, subject, view_href, post_time, kudos.sum(weight), can_accept_solution, is_solution, user_context.kudo, user_context.can_kudo, user_context.can_reply, user_context.can_delete, author.id, author.avatar, author.login, parent.id, metrics FROM messages";
    public static final String LI_FLOATED_MESSAGE_CLIENT_BASE_LIQL = "select * from floated_messages";
    public static final String LI_SDK_SETTINGS_CLIENT_BASE_LIQL = "select additional_information from app_sdk_settings";

    public static final String LI_ACCEPT_SOLUTION_TYPE = "solution_data";
    public static final String LI_KUDO_TYPE = "kudo";
    public static final String LI_POST_QUESTION_TYPE = "message";
    public static final String LI_REPLY_MESSAGE_TYPE = "message";
    public static final String LI_ARTICLES_CLIENT_TYPE = "message";
    public static final String LI_SUBSCRIPTIONS_CLIENT_TYPE = "subscription";
    public static final String LI_BROWSE_CLIENT_TYPE = "node";
    public static final String LI_SEARCH_CLIENT_TYPE = "message";
    public static final String LI_MESSAGE_CHILDREN_CLIENT_TYPE = "message";
    public static final String LI_QUESTIONS_CLIENT_TYPE = "message";
    public static final String LI_CATEGORY_CLIENT_TYPE = "node";
    public static final String LI_ARTICLES_BROWSE_CLIENT_TYPE = "message";
    public static final String LI_USER_DETAILS_CLIENT_TYPE = "user";
    public static final String LI_MESSAGE_CLIENT_TYPE = "message";
    public static final String LI_SDK_SETTINGS_CLIENT_TYPE = "app_sdk_setting";
    public static final String LI_USER_DEVICE_ID_FETCH_TYPE = "user_device_data";
    public static final String LI_APPLICATION_TYPE = "ANDROID";
    public static final String LI_MARK_MESSAGE_CLIENT_TYPE = "message_read";

    public static final String LI_IMAGE_TYPE = "image";
    public static final String LI_FLOATED_MESSAGE_CLIENT_TYPE = "floated_message";
    public static final String LI_ARTICLES_QUERYSETTINGS_TYPE = "article";
    public static final String LI_SUBSCRIPTTION_QUERYSETTINGS_TYPE = "subscription";
    public static final String LI_BROWSE_QUERYSETTINGS_TYPE = "node";
    public static final String LI_BROWSE_BY_DEPTH_QUERYSETTINGS_TYPE = "node_depth";
    public static final String LI_SEARCH_QUERYSETTINGS_TYPE = "search";
    public static final String LI_MESSAGE_CHILDREN_QUERYSETTINGS_TYPE = "message_children";
    public static final String LI_QUESTIONS_QUERYSETTINGS_TYPE = "questions";
    public static final String LI_CATEGORY_QUERYSETTINGS_TYPE = "category";
    public static final String LI_ARTICLES_BROWSE_QUERYSETTINGS_TYPE = "article_browse";
    public static final String LI_USER_DETAILS_QUERYSETTINGS_TYPE = "user";
    public static final String LI_MESSAGE_QUERYSETTINGS_TYPE = "message";
    public static final String LI_MESSAGE_BY_IDS_QUERYSETTINGS_TYPE = "messages_by_ids";
    public static final String LI_FLOATED_MESSAGE_QUERYSETTINGS_TYPE = "floated_message";
    public static final String LI_SDK_SETTINGS_QUERYSETTINGS_TYPE = "app_sdk_setting";
    public static final String LI_GENERIC_TYPE = "generic_get";
    public static final String LI_MARK_ABUSE_TYPE = "abuse_report";

    public static final String LI_MARK_AS_READ = "&api.mark_read=true";

    public static final String LI_INSERT_IMAGE_MACRO = "<p><li-image id=%s width=\"500\" height=\"500\" alt=%s align=\"inline\" size=\"large\" sourcetype=\"new\"></li-image></p>";
    public static final String LI_LINE_SEPARATOR = System.getProperty("line.separator");
    public static final Integer DEFAULT_LIQL_QUERY_LIMIT = 25;
    public static final String LI_MESSAGE_TYPE = "messages";
    public static final String LI_SUBSCRIPTION_TYPE = "subscriptions";
    //Add constant here
}
