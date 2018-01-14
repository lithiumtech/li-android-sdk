/*
 * Ordering.java
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
 * This class specifies fields on which liQueryOrdering clause is allowed for different clients.
 * Created by shoureya.kant on 12/15/16.
 */

public class LiQueryOrdering {

    private ClientOrderingColumn column;
    private Order order;

    public LiQueryOrdering(ClientOrderingColumn column, Order order) {
        this.column = column;
        this.order = order;
    }

    /**
     * @return columns on which liQueryOrdering can be done for a client.
     */
    public ClientOrderingColumn getColumn() {
        return column;
    }

    /**
     * @return LiQueryOrdering {@link Order}
     */
    public Order getOrder() {
        return order;
    }

    /**
     * @param client {@link LiClientManager.Client}
     * @return true or false depending on whether column on which liQueryOrdering is applied is allowed for the same
     * for a client.
     */
    public Boolean isVaild(LiClientManager.Client client) {
        return column.isVaild(client);
    }

    /**
     * Only two types of liQueryOrdering is supported i.e Ascending or Descending.
     */
    public enum Order {
        ASC, DESC;
    }


    /**
     * Specifies parameter on which liQueryOrdering is allowed for LI_ARTICLES_CLIENT.
     */
    public enum Articles implements ClientOrderingColumn {

        POST_TIME("post_time");
        private String value;

        Articles(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public Boolean isVaild(LiClientManager.Client client) {
            return LiClientManager.Client.LI_ARTICLES_CLIENT.equals(client);
        }
    }

    /**
     * Specifies field on which liQueryOrdering is allowed for LI_SEARCH_CLIENT.
     */
    public enum Search implements ClientOrderingColumn {

        POST_TIME("post_time");
        private String value;

        Search(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public Boolean isVaild(LiClientManager.Client client) {
            return LiClientManager.Client.LI_SEARCH_CLIENT.equals(client);
        }
    }

    /**
     * Specifies field on which liQueryOrdering is allowed for LI_QUESTION_CLIENT.
     */
    public enum Question implements ClientOrderingColumn {

        POST_TIME("post_time");
        private String value;

        Question(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public Boolean isVaild(LiClientManager.Client client) {
            return LiClientManager.Client.LI_QUESTIONS_CLIENT.equals(client);
        }
    }

    public interface ClientOrderingColumn {

        /**
         * get value of liQueryOrdering field
         *
         * @return Value of clause.
         */
        String getValue();

        /**
         * Checks if liQueryOrdering filed is correct for the client.
         *
         * @param client {@link LiClientManager.Client}
         * @return If clause is valid for a client.
         */
        Boolean isVaild(LiClientManager.Client client);
    }
}
