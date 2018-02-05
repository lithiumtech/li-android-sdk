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

package lithium.community.android.sdk.api;

import android.content.Context;

import lithium.community.android.sdk.exception.LiRestResponseException;
import lithium.community.android.sdk.rest.LiRestV2Request;

/**
 * This client is use to handle DELETE requests.
 * Created by shoureya.kant on 12/27/16.
 */

public class LiBaseDeleteClient extends LiBaseClient {

    public LiBaseDeleteClient(Context context, String basePath) throws LiRestResponseException {
        super(context, basePath, RequestType.DELETE);
    }

    @Override
    public void setLiRestV2Request() {
        this.liRestV2Request = new LiRestV2Request(context);
    }

    @Override
    public String getRequestBody() {
        return null;
    }
}
