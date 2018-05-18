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

import java.util.ArrayList;
import java.util.List;

import com.lithium.community.android.manager.LiClientManager.Client;

import static com.lithium.community.android.utils.LiQueryConstant.DEFAULT_LIQL_QUERY_LIMIT;

/**
 * This class is used to process the where clause, liQueryOrdering and limit of the LIQL query.
 * It takes from user the fields for where clause and completes the query. It also handles liQueryOrdering.
 * Created by kunal.shrivastava on 12/22/16.
 */

public class LiQueryRequestParams {

    private Client client;
    private List<LiQueryOrdering> liQueryOrdering;
    private LiQueryWhereClause liQueryWhereClause;
    private int limit;

    private LiQueryRequestParams(Builder builder) {
        this.client = builder.client;
        this.liQueryOrdering = builder.liQueryOrdering;
        this.liQueryWhereClause = builder.liQueryWhereClause;
        this.limit = builder.limit;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public Client getClient() {
        return client;
    }

    /**
     * Computes where clause, liQueryOrdering and limit.
     *
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

        List<LiQuerySetting.Ordering> orderings = null;
        if (this.liQueryOrdering != null && liQueryOrdering.size() > 0) {
            orderings = new ArrayList<>();
            for(LiQueryOrdering queryOrdering : liQueryOrdering) {
                LiQuerySetting.Ordering ordering = new LiQuerySetting.Ordering();
                ordering.setKey(queryOrdering.getColumn().getValue());
                ordering.setType(queryOrdering.getOrder().name());
                orderings.add(ordering);
            }
        }

        if (limit < 1) {
            limit = DEFAULT_LIQL_QUERY_LIMIT;
        }
        LiQuerySetting liQuerySetting = new LiQuerySetting(whereClauses, orderings, limit);
        return liQuerySetting;
    }

    /**
     * Builder for {@link LiQueryRequestParams}
     */
    public static class Builder {

        private Client client;
        private List<LiQueryOrdering> liQueryOrdering;
        private LiQueryWhereClause liQueryWhereClause;
        private int limit;

        public Builder setClient(Client client) {
            this.client = client;
            return this;
        }

        public Builder setLiQueryOrdering(List<LiQueryOrdering> liQueryOrdering) {
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
                    throw new RuntimeException(String.format("Invalid liQueryClause !!! LiQueryClause of %s used in %s",
                            liQueryClause.getKey().getClient(), client));
                }
            }
            if(liQueryOrdering != null) {
                for(LiQueryOrdering queryOrdering : liQueryOrdering) {
                    if (!queryOrdering.isVaild(client)) {
                        throw new RuntimeException(
                                String.format("Invalid liQueryOrdering !!! Use LiQueryOrdering of  %s", client));
                    }
                }
            }
            return new LiQueryRequestParams(this);
        }
    }
}