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
import android.support.annotation.Nullable;

/**
 * This exception in line with {@link ExceptionInInitializerError} but
 * is not a type of runtime exception and is required to be caught
 * which may be thrown by component initializer in the SDK.
 *
 * @author adityasharat
 */
public class LiInitializationException extends Exception {

    @NonNull
    private final String name;

    /**
     * Default public constructor.
     *
     * @param name  The name of the component which could not be initialized.
     * @param cause The error or exceptions because of which the component could not be initialized.
     */
    public LiInitializationException(@NonNull String name, @Nullable Throwable cause) {
        super(String.format("%s failed to initialize.", name), cause);
        this.name = name;
    }

    /**
     * Overridden public constructor to be used if the cause of the initialization
     * exceptions is not known.
     *
     * @param name The name of the component which could not be initialized.
     */
    public LiInitializationException(@NonNull String name) {
        this(name, null);
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
