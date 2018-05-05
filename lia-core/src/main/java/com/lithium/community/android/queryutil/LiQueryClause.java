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

package com.lithium.community.android.queryutil;

import com.lithium.community.android.manager.LiClientManager;

/**
 * This class is used to set parameters for the where clause in the LIQL.
 * It caters to only GET calls.
 * Created by kunal.shrivastava on 12/22/16.
 */
public class LiQueryClause {
    private LiQuerySetting.LiWhereClause clause;
    private LiQueryWhereClause.ClientWhereKeyColumn key;
    private String value;
    private String operator;

    public LiQueryClause(LiQuerySetting.LiWhereClause clause, LiQueryWhereClause.ClientWhereKeyColumn key, String value,
            String operator) {
        this.clause = clause;
        this.key = key;
        this.value = value;
        this.operator = operator;
    }

    public LiQuerySetting.LiWhereClause getClause() {
        return clause;
    }

    public LiQueryWhereClause.ClientWhereKeyColumn getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getOperator() {
        return operator;
    }

    public Boolean isValid(LiClientManager.Client client) {
        return key.isVaild(client);
    }

    @Override
    public String toString() {
        return "LiQueryClause{" + "clause=" + clause + ", key=" + key + ", value='" + value + '\'' + ", operator='" + operator + '\'' + '}';
    }
}
