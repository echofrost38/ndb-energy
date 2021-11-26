package com.ndb.auction.payload.statistics;

public class RoundChance {
    
    private int roundNumber;
    private double winRate;
    private double failedRate;
    
    public RoundChance(int number, double winRate, double failedRate) {
        this.roundNumber = number;
        this.winRate = winRate;
        this.failedRate = failedRate;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
    public double getWinRate() {
        return winRate;
    }
    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }
    public double getFailedRate() {
        return failedRate;
    }
    public void setFailedRate(double failedRate) {
        this.failedRate = failedRate;
    }

}
