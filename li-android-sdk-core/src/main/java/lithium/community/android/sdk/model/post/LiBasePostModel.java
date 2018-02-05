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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by kunal.shrivastava on 10/26/16.
 * <p>
 * Base class for post request model, implement {@link LiPostModel}. This class provides implementation of
 * toJsonString() method and toJson() method,
 * to help in serialization and deserialization of post model object. All post request model must extend this base
 * class,
 * to take advantage of serialization and deserialization.
 */

public class LiBasePostModel implements LiPostModel {

    @Override
    public JsonObject toJson() {
        Gson gson = new Gson();
        Data data = new Data(this);
        JsonElement jsonElement = gson.toJsonTree(data);
        return jsonElement.getAsJsonObject();
    }

    @Override
    public String toJsonString() {
        Gson gson = new Gson();
        Data data = new Data(this);
        return gson.toJson(data);
    }

    protected class Data {
        private LiPostModel data;

        Data(LiPostModel data) {
            this.data = data;
        }
    }
}
