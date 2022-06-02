package com.ndb.auction.payload.request.coinpayments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CoinpaymentCreateTransaction {
    private double amount; // usd 
    private String currency1; // usd
    private String currency2; // crypto
    private String buyerEmail; 
    private String ipnUrl;

    @Override
    public String toString() {
        return "cmd=create_transaction&amount=" + amount + "&currency1=" + currency1 + "&currency2" + currency2 + "&buyer_email=" + buyerEmail + "&ipn_url=" + ipnUrl;
    }
}
