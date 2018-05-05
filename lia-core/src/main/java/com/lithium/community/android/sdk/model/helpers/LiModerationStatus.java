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

package com.lithium.community.android.sdk.model.helpers;

import com.lithium.community.android.sdk.utils.LiCoreSDKUtils;

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
        if (LiCoreSDKUtils.checkNotNullAndNotEmpty(moderationStatus, null) != null) {
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
