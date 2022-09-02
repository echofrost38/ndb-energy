/************************************************************************ 
 * Copyright PointCheckout, Ltd.
 * 
 */
package com.ndb.auction.solanaj.api.ws.listener;


/**
 * 
 */
public interface TransactionEventListener {
    
    /**
     * 
     *
     * @param signature 
     */
    public void onTransactiEvent(String signature);
}
