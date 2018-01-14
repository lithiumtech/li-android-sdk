/*
 * Clause.java
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

import lithium.community.android.sdk.manager.LiClientManager;

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
        return "LiQueryClause{" +
                "clause=" + clause +
                ", key=" + key +
                ", value='" + value + '\'' +
                ", operator='" + operator + '\'' +
                '}';
    }
}
