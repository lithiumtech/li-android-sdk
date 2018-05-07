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

package com.lithium.community.android.example.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * @author adityasharat
 */
public class ToastUtils {

    public static void notInitialized(@NonNull Context context) {
        Toast.makeText(context, "Li SDK has not been initialized. ", Toast.LENGTH_SHORT).show();
    }

    public static void initializationError(@NonNull Context context, @NonNull String message) {
        Toast.makeText(context, "Li SDK initialization failed. ERROR: " + message, Toast.LENGTH_SHORT).show();
    }
}
