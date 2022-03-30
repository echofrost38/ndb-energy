package com.ndb.auction.models;

public class CirculatingSupply {
    private final String circulatingSupply;
    public CirculatingSupply(String _circulatingSupply) {
        this.circulatingSupply = _circulatingSupply;
    }
    public String getTotalSupply() {
        return circulatingSupply;
    }
}

