/*
 * PrivacyLevel.java
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

/**
 * This enum depicts privacy levels.
 */
public enum LiPrivacyLevel {

    PUBLIC, PRIVATE, UNKNOWN;

    public static LiPrivacyLevel getExternalSource(String privacyLevelStr) {
        if (privacyLevelStr == null || privacyLevelStr.isEmpty()) {
            return UNKNOWN;
        }
        for (LiPrivacyLevel type : LiPrivacyLevel.values()) {
            if (type.name().equalsIgnoreCase(privacyLevelStr)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}