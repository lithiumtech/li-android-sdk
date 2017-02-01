/*
 * LiModerationStatus.java
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

package lithium.community.android.sdk.model;

import lithium.community.android.sdk.utils.LiCoreSDKUtils;

/**
 * The moderation status of the message.
 */
public enum LiModerationStatus {
    /**
     * The item has not yet been moderated.
     */
    UNMODERATED,

    /**
     * The item has been flagged for moderator attention.
     */
    FLAGGED,

    /**
     * The item has been moderated and approved
     */
    APPROVED,

    /**
     * The item has been moderated and rejected
     */
    REJECTED,

    /**
     * The item is not moderated, and is in the process of uploading.
     */
    UPLOADING,

    /**
     * The item is in an error state. Typically this is because an upload failed.
     */
    ERROR,

    /**
     * The item has been deleted
     */
    DELETED,

    /**
     * The item is in an unknown state.
     */
    UNKNOWN,

    /**
     * The item has been marked undecided
     */
    MARKED_UNDECIDED,

    /**
     * The item has been marked for approval
     */
    MARKED_APPROVED,

    /**
     * The item has been marked for rejection
     */
    MARKED_REJECTED;

    public static LiModerationStatus fromString(String moderationStatus) {
        if (LiCoreSDKUtils.checkNullOrNotEmpty(moderationStatus, null) != null) {
            return LiModerationStatus.UNKNOWN;
        }

        for (LiModerationStatus status : LiModerationStatus.values()) {
            if (status.name().equalsIgnoreCase(moderationStatus)) {
                return status;
            }
        }

        return LiModerationStatus.UNKNOWN;
    }
}
