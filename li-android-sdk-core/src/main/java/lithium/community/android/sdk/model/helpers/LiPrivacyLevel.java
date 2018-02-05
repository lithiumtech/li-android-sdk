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

package lithium.community.android.sdk.model.helpers;

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