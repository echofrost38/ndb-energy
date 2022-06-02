package com.ndb.auction.models.transactions;

import java.util.List;

import com.ndb.auction.models.transactions.bank.BankDepositTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalPresaleTransaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.transactions.stripe.StripePresaleTransaction;
import com.ndb.auction.models.withdraw.CryptoWithdraw;
import com.ndb.auction.models.withdraw.PaypalWithdraw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Statement {
    // withdraw
    private List<CryptoWithdraw> cryptoWithdraws;
    private List<PaypalWithdraw> paypalWithdraws;

    // deposit
    // 1) Bid
    private List<StripeAuctionTransaction> stripeAuctionTxns;
    private List<PaypalAuctionTransaction> paypalAuctionTxns;
    private List<CoinpaymentDepositTransaction> coinpaymentAuctionTxns;
    
    // 2) Presale
    private List<StripePresaleTransaction> stripePresaleTxns;
    private List<PaypalPresaleTransaction> paypalPresaleTxns;
    private List<CoinpaymentDepositTransaction> coinpaymentPresaleTxns;

    // 3) Wallet
    private List<PaypalDepositTransaction> paypalDepositTxns;
    private List<CoinpaymentDepositTransaction> coinpaymentDepositTxns;
    private List<StripeDepositTransaction> stripeDepositTxns;
    private List<BankDepositTransaction> bankDepositTxns;
}
