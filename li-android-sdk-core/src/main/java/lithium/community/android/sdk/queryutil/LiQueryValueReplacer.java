/*
 * Replacer.java
 * Created on Dec 27, 2016
 *
 * Copyright 2016 Lithium Technologies, Inc.
 * San Francisco, California, U.S.A.  All Rights Reserved.
 *
 * This software is the  confidential and proprietary information
 * of  Lithium  Technologies,  Inc.  ("Confidential Information")
 * You shall not disclose such Confidential Information and shall
 * use  it  only in  accordance  with  the terms of  the  license
 * agreement you entered into with Lithium.
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
