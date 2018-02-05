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

package lithium.community.android.sdk.queryutil;

import java.util.HashMap;

/**
 * This class is used to replace special characters with actual values passed as parameters.
 * Created by kunal.shrivastava on 12/7/16.
 */
public class LiQueryValueReplacer {
    private HashMap<String, String> replacer = new HashMap<>();

    /**
     * Populates replacer map with character to replave and the value to be added.
     *
     * @param regex       The regex expression which are to be replaced.
     * @param replacement Text to be added.
     * @return {@link LiQueryValueReplacer}
     */
    public LiQueryValueReplacer replaceAll(String regex, String replacement) {
        replacer.put(regex, replacement);
        return this;
    }

    /**
     * Returns replacement map.
     *
     * @return HashMap with key as character to be replaced and value is the value which replaces the character.
     */
    public HashMap<String, String> getReplacementMap() {
        return replacer;
    }
}
