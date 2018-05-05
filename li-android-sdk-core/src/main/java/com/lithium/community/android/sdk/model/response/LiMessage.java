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

package com.lithium.community.android.sdk.model.response;

import com.google.gson.annotations.SerializedName;
import com.lithium.community.android.sdk.model.LiBaseModelImpl;
import com.lithium.community.android.sdk.model.helpers.LiBoard;
import com.lithium.community.android.sdk.model.helpers.LiConversation;
import com.lithium.community.android.sdk.model.helpers.LiKudoMetrics;
import com.lithium.community.android.sdk.model.helpers.LiMessageMetrics;
import com.lithium.community.android.sdk.model.helpers.LiModerationStatus;
import com.lithium.community.android.sdk.model.helpers.LiReplies;
import com.lithium.community.android.sdk.model.helpers.LiUserContext;

/**
 * A message represents any kind of post made to the community using one of the Lithium conversation styles.
 *
 * @link http://community.lithium.com/t5/Developer-Documentation/bd-p/dev-doc-portal?section=commv2&collection=messages
 */
public class LiMessage extends LiBaseModelImpl implements LiTargetModel {

    private String href;

    @SerializedName("view_href")
    private String viewHref;

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

    @SerializedName("replies")
    private LiReplies liReplies;

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
        return viewHref;
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

    public LiReplies getLiReplies() {
        return liReplies;
    }

    public void setLiReplies(LiReplies liReplies) {
        this.liReplies = liReplies;
    }
}
