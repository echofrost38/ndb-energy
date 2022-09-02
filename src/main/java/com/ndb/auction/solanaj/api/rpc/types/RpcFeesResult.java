/************************************************************************ 
 * Copyright PointCheckout, Ltd.
 * 
 */
package com.ndb.auction.solanaj.api.rpc.types;


/**
 * 
 */
public class RpcFeesResult extends RpcResultObject {

    /**  */
    private long value;

    /**
     * 
     *
     * @return 
     */
    public long getValue() {
        return value;
    }

    /**
     * 
     *
     * @param value 
     */
    public void setValue(long value) {
        this.value = value;
    }

}
