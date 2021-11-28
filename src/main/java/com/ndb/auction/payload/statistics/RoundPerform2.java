package com.ndb.auction.payload.statistics;

public class RoundPerform2 {
    
    private Integer roundNumber;
    private Double min;
    private Double max;
    private Double std;
    
    public RoundPerform2(int round, double min, double max, double std) {
        this.roundNumber = round;
        this.min = min;
        this.max = max;
        this.std = std;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }
    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }
    public Double getMin() {
        return min;
    }
    public void setMin(Double min) {
        this.min = min;
    }
    public Double getMax() {
        return max;
    }
    public void setMax(Double max) {
        this.max = max;
    }
    public Double getStd() {
        return std;
    }
    public void setStd(Double std) {
        this.std = std;
    }

    
}
