package com.ndb.auction.models.presale;

import java.util.List;

import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalPresaleTransaction;
import com.ndb.auction.models.transactions.stripe.StripePresaleTransaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PresaleOrderPayments {
    private List<CoinpaymentDepositTransaction> coinpaymentTxns;
    private List<StripePresaleTransaction> stripeTxns;
    private List<PaypalPresaleTransaction> paypalTxns;
}
