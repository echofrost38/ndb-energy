/************************************************************************ 
 * Copyright PointCheckout, Ltd.
 * 
 */
package com.ndb.auction.solanaj.api.rpc.types;


/**
 * 
 */
public class RpcStatusConfig {

    /**  */
    private boolean searchTransactionHistory = true;

    /**
     * 
     *
     * @return 
     */
    public boolean isSearchTransactionHistory() {
        return searchTransactionHistory;
    }

    /**
     * 
     *
     * @param searchTransactionHistory 
     */
    public void setSearchTransactionHistory(boolean searchTransactionHistory) {
        this.searchTransactionHistory = searchTransactionHistory;
    }

}
