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

package com.lithium.community.android.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.lithium.community.android.notification.FirebaseTokenProvider;

/**
 * This interface is used by the SDK to get the device token to
 * register the device to receive push notifications from the
 * Community via Firebase Cloud Messaging.
 *
 * @deprecated Use {@link FirebaseTokenProvider} instead.
 */
@Deprecated
public interface LiDeviceTokenProvider {

    /**
     * Return a fully qualified device token generated by the Firebase App.
     *
     * @return The device token.
     */
    String getDeviceId();

    class Wrapper extends FirebaseTokenProvider {

        public LiDeviceTokenProvider provider;

        public Wrapper(LiDeviceTokenProvider provider) {
            this.provider = provider;
        }

        @Nullable
        public static LiDeviceTokenProvider getWrappedProvider(FirebaseTokenProvider wrapper) {
            if (wrapper instanceof LiDeviceTokenProvider.Wrapper) {
                return ((LiDeviceTokenProvider.Wrapper) wrapper).provider;
            } else {
                return null;
            }
        }

        @NonNull
        @Override
        public FirebaseInstanceId getFirebaseInstanceId() {
            throw new UnsupportedOperationException(getClass().getSimpleName());
        }

        @NonNull
        @Override
        public String getAuthorizedEntity() {
            throw new UnsupportedOperationException(getClass().getSimpleName());
        }

        @NonNull
        @Override
        public String getDeviceToken() {
            return provider.getDeviceId();
        }
    }
}
