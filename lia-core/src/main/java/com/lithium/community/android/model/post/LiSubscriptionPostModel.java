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

package com.lithium.community.android.model.post;

import com.lithium.community.android.utils.LiQueryConstant;

/**
 * Created by shoureya.kant on 3/30/17.
 */

public class LiSubscriptionPostModel extends LiBasePostModel {

    private final String type = LiQueryConstant.LI_SUBSCRIPTIONS_CLIENT_TYPE;
    private final Target target;

    public LiSubscriptionPostModel(Target target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public Target getTarget() {
        return target;
    }

    /**
     * Use {@link MessageTarget} or {@link BoardTarget} to select a respective target for subscriptions
     */
    public static class Target {
        private final String type;
        private final String id;

        Target(String type, String id) {
            this.type = type;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }

    public static class MessageTarget extends Target {
        public MessageTarget(String id) {
            super("message", id);
        }
    }

    public static class BoardTarget extends Target {
        public BoardTarget(String id) {
            super("board", id);
        }
    }
}
