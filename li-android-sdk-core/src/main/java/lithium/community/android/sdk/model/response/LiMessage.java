/*
 * LiMessage.java
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

package lithium.community.android.sdk.model.response;

import com.google.gson.annotations.SerializedName;

import lithium.community.android.sdk.model.LiBaseModelImpl;
import lithium.community.android.sdk.model.helpers.LiBoard;
import lithium.community.android.sdk.model.helpers.LiConversation;
import lithium.community.android.sdk.model.helpers.LiKudoMetrics;
import lithium.community.android.sdk.model.helpers.LiMessageMetrics;
import lithium.community.android.sdk.model.helpers.LiModerationStatus;
import lithium.community.android.sdk.model.helpers.LiUserContext;

/**
 * A message represents any kind of post made to the community using one of the Lithium conversation styles.
 *
 * @link http://community.lithium.com/t5/Developer-Documentation/bd-p/dev-doc-portal?section=commv2&collection=messages
 */
public class LiMessage extends LiBaseModelImpl implements LiTargetModel {

    private String href;
    private String view_href;
    private LiBoard board;

    @SerializedName("parent")
    private LiMessage parent;

    private LiMessage root;
    private Boolean isFloating;

    @SerializedName("board_id")
    private Long boardId;
    private Long id;
    private Boolean readOnly;
    private Boolean deleted;

    @SerializedName("post_time")
    private LiBaseModelImpl.LiDateInstant postTime;
    private String subject;
    private String body;
    private String teaser;

    @SerializedName("author")
    private LiUser author;

    @SerializedName("metrics")
    private LiMessageMetrics messageMetrics;

    @SerializedName("kudos")
    private LiKudoMetrics kudoMetrics;

    @SerializedName("is_solution")
    private Boolean isAcceptedSolution;
    private LiModerationStatus moderationStatus;

    @SerializedName("last_edit_time")
    private LiBaseModelImpl.LiDateInstant lastEditTime;

    @SerializedName("can_accept_solution")
    private Boolean canAcceptSolution;

    @SerializedName("user_context")
    private LiUserContext userContext;

    @SerializedName("conversation")
    private LiConversation liConversation;

    private Boolean acceptedOnUI;

    private Integer unreadReplyCount;

    public LiMessage getParent() {
        return parent;
    }

    public void setParent(LiMessage parent) {
        this.parent = parent;
    }

    public int getUnreadReplyCount() {
        return (unreadReplyCount == null ? 0 : unreadReplyCount);
    }

    public void setUnreadReplyCount(Integer unreadReplyCount) {
        this.unreadReplyCount = unreadReplyCount;
    }

    public Boolean isAcceptedOnUI() {
        return acceptedOnUI;
    }

    public void setAcceptedOnUI(Boolean acceptedOnUI) {
        this.acceptedOnUI = acceptedOnUI;
    }

    public Boolean isFloating() {
        return isFloating;
    }

    public void setFloating(Boolean floating) {
        isFloating = floating;
    }

    public LiConversation getLiConversation() {
        return liConversation;
    }

    public void setLiConversation(LiConversation liConversation) {
        this.liConversation = liConversation;
    }

    public Boolean getCanAcceptSolution() {
        return canAcceptSolution;
    }

    public void setCanAcceptSolution(Boolean canAcceptSolution) {
        this.canAcceptSolution = canAcceptSolution;
    }

    public LiUserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(LiUserContext userContext) {
        this.userContext = userContext;
    }

    //We use this jackson trick to have just pure values instead of lithium objects
    //the downside is serialization again will produce something we cannot deserialize
    public void setReadOnly(LiBaseModelImpl.LiBoolean result) {
        this.readOnly = result.getValue();
    }

    public void setDeleted(LiBaseModelImpl.LiBoolean result) {
        this.deleted = result.getValue();
    }

    public void setTeaser(LiBaseModelImpl.LiString result) {
        this.teaser = result.getValue();
    }

    public String getRootId() {
        if (root != null) {
            // /message/id/35 (returns 35)
            return root.href.split("/")[3];
        } else {
            return null;
        }
    }

    public void setBoardId(LiBaseModelImpl.LiInt result) {
        this.boardId = result.getValue();
    }

    public void setLastEditTime(LiBaseModelImpl.LiDate result) {
        lastEditTime = result.getValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(LiBaseModelImpl.LiInt result) {
        this.id = result.getValue();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(LiBaseModelImpl.LiString result) {
        subject = result.getValue();
    }

    public String getBody() {
        return body;
    }

    public void setBody(LiBaseModelImpl.LiString result) {
        this.body = result.getValue();
    }

    public LiBaseModelImpl.LiDateInstant getPostTime() {
        return postTime;
    }

    public void setPostTime(LiBaseModelImpl.LiDateInstant result) {
        postTime = result;
    }

    public String getViewHref() {
        return view_href;
    }

    public void setIsAcceptedSolution(Boolean isAcceptedSolution) {
        this.isAcceptedSolution = isAcceptedSolution;
    }

    public Boolean isAcceptedSolution() {
        return isAcceptedSolution;
    }

    public LiModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(LiModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public LiUser getAuthor() {
        return author;
    }

    public void setAuthor(LiUser author) {
        this.author = author;
    }

    public LiMessageMetrics getMessageMetrics() {
        return messageMetrics;
    }

    public void setMessageMetrics(LiMessageMetrics messageMetrics) {
        this.messageMetrics = messageMetrics;
    }

    public LiBoard getBoard() {
        return board;
    }

    public void setBoard(LiBoard board) {
        this.board = board;
    }

    public LiKudoMetrics getKudoMetrics() {
        return kudoMetrics;
    }

    public void setKudoMetrics(LiKudoMetrics kudoMetrics) {
        this.kudoMetrics = kudoMetrics;
    }

    @Override
    public LiMessage getLiMessage() {
        return this;
    }

    @Override
    public LiBoard getLiBoard() {
        return null;
    }
}
