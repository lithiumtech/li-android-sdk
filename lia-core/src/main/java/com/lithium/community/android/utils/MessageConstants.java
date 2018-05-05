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

package com.lithium.community.android.utils;

import android.support.annotation.NonNull;

/**
 * This class hosts all messages and message templates. The value
 * of the messages and templates can be modified by the client. All
 * messages and templates are stored in the {@code public static}
 * fields.
 *
 * @author adityasharat
 */
public final class MessageConstants {

    public static String errorArgumentWasNull = "%s was null";
    public static String errorStringWasEmpty = "%s was empty";

    /**
     * Default private constructor so that an instance of this
     * class cannot be created.
     */
    private MessageConstants() {
    }

    /**
     * Returns the error message which states that a named
     * argument was {@code null}.
     *
     * @param name the name of the argument.
     * @return the error message.
     */
    public static String wasNull(@NonNull String name) {
        return String.format(errorArgumentWasNull, name);
    }

    /**
     * Returns the error message which states that a named
     * {@link String} argument was {@code null} or empty.
     *
     * @param name the name of the argument.
     * @return the error message.
     */
    public static String wasEmpty(@NonNull String name) {
        return String.format(errorStringWasEmpty, name);
    }
}
