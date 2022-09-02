/************************************************************************ 
 * Copyright PointCheckout, Ltd.
 * 
 */
package com.ndb.auction.solanaj.api.rpc.types;

/**
 * 
 */
public class RpcTokenAccountConfig {

    /**  */
    private String mint;

    /**
     * 
     *
     * @param mint 
     */
    public RpcTokenAccountConfig(String mint) {
        this.mint = mint;
    }

    /**
     * 
     *
     * @return 
     */
    public String getMint() {
        return mint;
    }

    /**
     * 
     *
     * @param mint 
     */
    public void setMint(String mint) {
        this.mint = mint;
    }

}
