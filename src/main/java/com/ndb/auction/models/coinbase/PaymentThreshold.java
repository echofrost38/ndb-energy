package com.ndb.auction.models.coinbase;

import java.util.Map;

public class PaymentThreshold {
    
    private Map<String, String> overpayment_absolute_threshold;
    private String overpayment_relative_threshold;
    private Map<String, String> underpayment_absolute_threshold;
    private String underpayment_relative_threshold;
    
    public Map<String, String> getOverpayment_absolute_threshold() {
        return overpayment_absolute_threshold;
    }
    public void setOverpayment_absolute_threshold(Map<String, String> overpayment_absolute_threshold) {
        this.overpayment_absolute_threshold = overpayment_absolute_threshold;
    }
    public String getOverpayment_relative_threshold() {
        return overpayment_relative_threshold;
    }
    public void setOverpayment_relative_threshold(String overpayment_relative_threshold) {
        this.overpayment_relative_threshold = overpayment_relative_threshold;
    }
    public Map<String, String> getUnderpayment_absolute_threshold() {
        return underpayment_absolute_threshold;
    }
    public void setUnderpayment_absolute_threshold(Map<String, String> underpayment_absolute_threshold) {
        this.underpayment_absolute_threshold = underpayment_absolute_threshold;
    }
    public String getUnderpayment_relative_threshold() {
        return underpayment_relative_threshold;
    }
    public void setUnderpayment_relative_threshold(String underpayment_relative_threshold) {
        this.underpayment_relative_threshold = underpayment_relative_threshold;
    }

    
}
