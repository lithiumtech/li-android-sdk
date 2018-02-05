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

package lithium.community.android.sdk.model.response;

import lithium.community.android.sdk.model.helpers.LiBoard;

/**
 * Created by kunal.shrivastava on 11/4/16.
 * Interface to be implemented by all the models which is having LiMessage variable
 */

public interface LiTargetModel {
    public LiMessage getLiMessage();

    public LiBoard getLiBoard();
}
