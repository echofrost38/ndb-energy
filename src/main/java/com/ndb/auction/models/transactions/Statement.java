package com.ndb.auction.models.transactions;

import java.util.List;

import com.ndb.auction.models.transactions.bank.BankDepositTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentAuctionTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentPresaleTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentWalletTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalAuctionTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalPresaleTransaction;
import com.ndb.auction.models.transactions.stripe.StripeAuctionTransaction;
import com.ndb.auction.models.transactions.stripe.StripePresaleTransaction;
import com.ndb.auction.models.transactions.stripe.StripeWalletTransaction;
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
    private List<CoinpaymentAuctionTransaction> coinpaymentAuctionTxns;
    
    // 2) Presale
    private List<StripePresaleTransaction> stripePresaleTxns;
    private List<PaypalPresaleTransaction> paypalPresaleTxns;
    private List<CoinpaymentPresaleTransaction> coinpaymentPresaleTxns;

    // 3) Wallet
    private List<PaypalDepositTransaction> paypalDepositTxns;
    private List<CoinpaymentWalletTransaction> coinpaymentWalletTxns;
    private List<StripeWalletTransaction> stripeDepositTxns;
    private List<BankDepositTransaction> bankDepositTxns;
}
