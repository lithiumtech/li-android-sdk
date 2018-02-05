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

package lithium.community.android.sdk.model.post;

import lithium.community.android.sdk.model.response.LiMessage;
import lithium.community.android.sdk.model.response.LiUser;

/**
 * Data Model for marking a message as abusive.
 * Created by shoureya.kant on 2/22/17.
 */
public class LiMarkAbuseModel extends LiBasePostModel {

    private String type;
    private LiUser reporter;
    private LiMessage message;
    private String body;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LiUser getReporter() {
        return reporter;
    }

    public void setReporter(LiUser reporter) {
        this.reporter = reporter;
    }

    public LiMessage getMessage() {
        return message;
    }

    public void setMessage(LiMessage message) {
        this.message = message;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
