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

package lithium.community.android.sdk.rest;

import lithium.community.android.sdk.exception.LiRestResponseException;

/**
 * Request call back to use for activities that require async response processing.
 */

public interface LiAuthAsyncRequestCallback<T> {

    /**
     * This call back is used when Async auth request is successful.
     *
     * @param response Generic response.
     * @throws LiRestResponseException {@link LiRestResponseException}
     */
    void onSuccess(T response) throws LiRestResponseException;

    /**
     * This call back is used when Async auth request is not successful.
     *
     * @param exception {@link Exception}
     */
    void onError(Exception exception);
}
