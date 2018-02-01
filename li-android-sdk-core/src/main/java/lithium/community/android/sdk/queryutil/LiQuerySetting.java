/*
 * QuerySetting.java
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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by kunal.shrivastava on 10/18/16.
 * Model represents setting for API provider query
 */

public class LiQuerySetting {

    private static final String WHERE_CONDITION = "where-condition";
    private static final String ORDER = "order";
    private static final String LIMIT = "limit";

    private ArrayList<WhereClause> whereClauses;
    private Ordering ordering;
    private int limit;

    public LiQuerySetting(ArrayList<WhereClause> whereClauses, Ordering ordering, int limit) {
        this.whereClauses = whereClauses;
        this.ordering = ordering;
        this.limit = limit;
    }

    public ArrayList<WhereClause> getWhereClauses() {
        return (ArrayList<WhereClause>) whereClauses.clone();
    }

    public Ordering getOrdering() {
        return ordering;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        return "LiQuerySetting{" + ", whereClauses=" + whereClauses + ", orderObject=" + ordering + ", limit=" + limit + '}';
    }

    /**
     * All allowed conditions in where clause.
     */
    public enum LiWhereClause {

        @SerializedName("equals")
        EQUALS("="),

        @SerializedName("not-equals")
        NOT_EQUALS("!="),

        @SerializedName("greater-than")
        GREATER_THAN(">"),

        @SerializedName("greater-than-equals")
        GREATER_THAN_EQUALS(">="),

        @SerializedName("less-than")
        LESS_THAN("<"),

        @SerializedName("less-than-equal")
        LESS_THAN_EQUAL("<="),

        @SerializedName("in")
        IN("in"),

        @SerializedName("matches")
        MATCHES("matches");

        private final String value;

        private LiWhereClause(String str) {
            value = str;
        }

        public String getValue() {
            return value;
        }

        public boolean equals(String value) {
            return (value == null) ? false : this.value.equals(value);
        }

        public String toString() {
            return this.value.toString();
        }

    }

    /**
     * Allowed cluses on LIQL.
     */
    protected enum LiClauses {

        @SerializedName("where")
        WHERE("where"),

        @SerializedName("order")
        ORDER("order"),

        @SerializedName("limit")
        LIMIT("limit");

        private final String value;

        private LiClauses(String str) {
            value = str;
        }

        public boolean equals(String value) {
            return (value == null) ? false : this.value.equals(value);
        }

        public String toString() {
            return this.value.toString();
        }
    }

    /**
     * Class for adding where clause.
     */
    protected static class WhereClause {
        private LiWhereClause clause;
        private String key;
        private String value;
        private String operator;

        public LiWhereClause getClause() {
            return clause;
        }

        public void setClause(LiWhereClause clause) {
            this.clause = clause;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        @Override
        public String toString() {
            return "LiQueryWhereClause{" + "clause=" + clause + ", key='" + key + '\'' + ", value='" + value + '\'' + ", operator='" + operator + '\'' + '}';
        }
    }

    /**
     * Class for adding ordering clause.
     */
    protected static class Ordering {
        private String key;
        private String type;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "LiQueryOrdering{" + "key='" + key + '\'' + ", type='" + type + '\'' + '}';
        }
    }
}