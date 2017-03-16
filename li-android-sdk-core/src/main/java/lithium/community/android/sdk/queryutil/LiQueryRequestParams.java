/*
 * QueryRequestParams.java
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

import java.util.ArrayList;

import lithium.community.android.sdk.manager.LiClientManager.Client;

import static lithium.community.android.sdk.utils.LiQueryConstant.DEFAULT_LIQL_QUERY_LIMIT;

/**
 * This class is used to process the where clause, liQueryOrdering and limit of the LIQL query.
 * It takes from user the fields for where clause and completes the query. It also handles liQueryOrdering.
 * Created by kunal.shrivastava on 12/22/16.
 */

public class LiQueryRequestParams {

    private Client client;
    private LiQueryOrdering liQueryOrdering;
    private LiQueryWhereClause liQueryWhereClause;
    private int limit;

    private LiQueryRequestParams(Builder builder) {
        this.client = builder.client;
        this.liQueryOrdering = builder.liQueryOrdering;
        this.liQueryWhereClause = builder.liQueryWhereClause;
        this.limit = builder.limit;
    }

    public Client getClient() {
        return client;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Computes where clause, liQueryOrdering and limit.
     * @return Model representing setting for API provider query.
     */
    public LiQuerySetting getQuerySetting() {

        ArrayList<LiQuerySetting.WhereClause> whereClauses = new ArrayList();
        for (LiQueryClause liQueryClause : liQueryWhereClause.getLiQueryClauses()) {
            LiQuerySetting.WhereClause whereClause = new LiQuerySetting.WhereClause();
            whereClause.setClause(liQueryClause.getClause());
            whereClause.setKey(liQueryClause.getKey().getValue());
            whereClause.setValue(liQueryClause.getValue());
            whereClause.setOperator(liQueryClause.getOperator());
            whereClauses.add(whereClause);
        }

        LiQuerySetting.Ordering ordering = null;
        if (this.liQueryOrdering != null) {
            ordering = new LiQuerySetting.Ordering();
            ordering.setKey(this.liQueryOrdering.getColumn().getValue());
            ordering.setType(this.liQueryOrdering.getOrder().name());
        }

        if (limit<1) {
            limit = DEFAULT_LIQL_QUERY_LIMIT;
        }
        LiQuerySetting liQuerySetting = new LiQuerySetting(whereClauses, ordering, limit);
        return liQuerySetting;
    }

    /**
     * Builder for {@link LiQueryRequestParams}
     */
    public static class Builder {

        private Client client;
        private LiQueryOrdering liQueryOrdering;
        private LiQueryWhereClause liQueryWhereClause;
        private int limit;

        public Builder setClient(Client client) {
            this.client = client;
            return this;
        }

        public Builder setLiQueryOrdering(LiQueryOrdering liQueryOrdering) {
            this.liQueryOrdering = liQueryOrdering;
            return this;
        }

        public Builder setLiQueryWhereClause(LiQueryWhereClause liQueryWhereClause) {
            this.liQueryWhereClause = liQueryWhereClause;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public LiQueryRequestParams build() {
            for (LiQueryClause liQueryClause : liQueryWhereClause.getLiQueryClauses()) {
                if (!liQueryClause.isValid(client)) {
                    throw new RuntimeException(String.format("Invalid liQueryClause !!! LiQueryClause of %s used in %s", liQueryClause.getKey().getClient(), client));
                }
            }
            if(!liQueryOrdering.isVaild(client)) {
                throw new RuntimeException(String.format("Invalid liQueryOrdering !!! Use LiQueryOrdering of  %s", client));
            }
            return new LiQueryRequestParams(this);
        }
    }
}
