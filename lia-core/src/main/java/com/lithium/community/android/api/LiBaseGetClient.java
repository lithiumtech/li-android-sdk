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

package com.lithium.community.android.api;

import android.content.Context;

import com.lithium.community.android.exception.LiRestResponseException;
import com.lithium.community.android.model.LiBaseModel;
import com.lithium.community.android.queryutil.LiQueryBuilder;
import com.lithium.community.android.queryutil.LiQueryRequestParams;
import com.lithium.community.android.queryutil.LiQueryValueReplacer;
import com.lithium.community.android.rest.LiRestV2Request;
import com.lithium.community.android.utils.LiQueryConstant;

import java.util.Iterator;
import java.util.Map;

/**
 * All "get" clients are implemented in same way. They only vary in their TYPE and LIQL query. This class brings all
 * the common codes together
 * and provide a layer of abstraction to all of the "get" clients.
 * Created by kunal.shrivastava on 10/19/16.
 */

public class LiBaseGetClient extends LiBaseClient {

    private final String activityLIQL;
    private LiQueryValueReplacer liQueryValueReplacer;
    private LiQueryRequestParams liQueryRequestParams;

    public LiBaseGetClient(Context context, String activityLIQL, String type, String querySettingsType,
            Class<? extends LiBaseModel> responseClass) throws LiRestResponseException {
        super(context, type, querySettingsType, responseClass, RequestType.GET);
        this.activityLIQL = activityLIQL;
    }

    public LiBaseGetClient(Context context, String activityLIQL, String type, String querySettingsType,
            Class<? extends LiBaseModel> responseClass, String pathParam) throws LiRestResponseException {
        super(context, type, querySettingsType, responseClass, RequestType.GET, pathParam);
        this.activityLIQL = activityLIQL;
    }

    public LiBaseGetClient(Context context, String activityLIQL, String type,
            Class<? extends LiBaseModel> responseClass, LiQueryRequestParams liQueryRequestParams) {
        super(context, type, responseClass);
        this.activityLIQL = activityLIQL;
        this.liQueryRequestParams = liQueryRequestParams;
    }

    /**
     * Initialize {@link LiRestV2Request} with LIQL and type.
     * {@link LiBaseClient#setLiRestV2Request()}
     */
    @Override
    public void setLiRestV2Request() {
        this.liRestV2Request = new LiRestV2Request(context, getLiqlQuery(), type);
    }

    /**
     * Request body is always null for GET Client.
     *
     * @return null
     */
    @Override
    public String getRequestBody() {
        return null;
    }

    /**
     * Method to return LIQL query for a particular client based on client type and base query.
     *
     * @return Complete query.
     */
    protected String getLiqlQuery() {

        if (LiQueryConstant.LI_GENERIC_TYPE.equals(querySettingsType)) {
            return activityLIQL;
        } else if (liQueryRequestParams != null) {
            return LiQueryBuilder.getQuery(activityLIQL, liQueryRequestParams.getQuerySetting());
        } else {
            LiQueryBuilder liQueryBuilder = new LiQueryBuilder();
            String query = liQueryBuilder.getQuery(context, activityLIQL, this.querySettingsType);
            if (liQueryValueReplacer != null) {
                Iterator it = liQueryValueReplacer.getReplacementMap().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pair = (Map.Entry) it.next();
                    query = query.replaceAll(pair.getKey(), pair.getValue());
                }
            }
            if (liQueryOrdering != null) {
                int startOfOrderByClause = query.indexOf("ORDER BY");
                int startOfLimitClause = query.indexOf("LIMIT");
                int length = query.length();
                String newOrderClause = " ORDER BY " + liQueryOrdering.getColumn() + " " + liQueryOrdering.getOrder()
                        + " ";

                if (startOfOrderByClause > 0 && startOfLimitClause > 0) {
                    query = query.substring(0, startOfOrderByClause - 1) + newOrderClause + query.substring(
                            startOfLimitClause, length);
                } else if (startOfOrderByClause < 0 && startOfLimitClause > 0) {
                    query = query.substring(0, startOfLimitClause - 1) + newOrderClause + query.substring(
                            startOfLimitClause, length);
                } else if (startOfOrderByClause > 0 && startOfLimitClause < 0) {
                    query = query.substring(0, startOfOrderByClause - 1) + newOrderClause;
                } else {
                    query = query + newOrderClause;
                }
            }
            return query;
        }
    }

    /**
     * Method to add liQueryValueReplacer to replace special characters with actual values
     *
     * @param liQueryValueReplacer {@link LiQueryValueReplacer}
     * @return this
     */
    public LiClient setReplacer(LiQueryValueReplacer liQueryValueReplacer) {
        this.liQueryValueReplacer = liQueryValueReplacer;
        return this;
    }
}
