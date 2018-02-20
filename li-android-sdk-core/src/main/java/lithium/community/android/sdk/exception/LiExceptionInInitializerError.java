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

package lithium.community.android.sdk.exception;

import android.support.annotation.NonNull;

/**
 * This exception is sugar for {@link ExceptionInInitializerError} exceptions
 * which may be thrown by static component initializer in the SDK.
 *
 * @author adityasharat
 */
public class LiExceptionInInitializerError extends ExceptionInInitializerError {

    @NonNull
    private final String name;

    @NonNull
    private final String message;

    /**
     * Default public constructor.
     *
     * @param cause The error or exceptions because of which the component could not be initialized.
     * @param name  The name of the component which could not be initialized.
     */
    public LiExceptionInInitializerError(@NonNull Throwable cause, @NonNull String name) {
        super(cause);
        this.name = name;
        this.message = String.format("%s failed to initialize.", this.name);
    }

    /**
     * The pretty message for the exception.
     *
     * @return the exception message.
     */
    @NonNull
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * The name of the component which failed to initialize.
     *
     * @return The name of the component which failed to initialize.
     */
    @NonNull
    public String getName() {
        return name;
    }
}
