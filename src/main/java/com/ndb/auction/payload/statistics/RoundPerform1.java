package com.ndb.auction.payload.statistics;

public class RoundPerform1 {
    
    private int roundNumber;
    private double tokenPrice;
    private double soldAmount;

    public RoundPerform1 (int number, double tokenPrice, double soldAmount) {
        this.roundNumber = number;
        this.tokenPrice = tokenPrice;
        this.soldAmount = soldAmount;
    }
    
    public int getRoundNumber() {
        return roundNumber;
    }
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
    public double getTokenPrice() {
        return tokenPrice;
    }
    public void setTokenPrice(double tokenPrice) {
        this.tokenPrice = tokenPrice;
    }
    public double getSoldAmount() {
        return soldAmount;
    }
    public void setSoldAmount(double soldAmount) {
        this.soldAmount = soldAmount;
    }
    
}
