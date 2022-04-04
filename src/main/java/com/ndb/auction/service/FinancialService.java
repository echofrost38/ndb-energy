package com.ndb.auction.service;

import com.ndb.auction.dao.oracle.transactions.bank.BankDepositDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentAuctionDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentPresaleDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentWalletDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalDepositDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeAuctionDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeDepositDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripePresaleDao;
import com.ndb.auction.dao.oracle.withdraw.CryptoWithdrawDao;
import com.ndb.auction.models.transactions.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinancialService extends BaseService {
    
    @Autowired
    private CryptoWithdrawDao cryptoWithdarwDao;

    @Autowired
    private StripeAuctionDao stripeAuctionDao;

    @Autowired
    private CoinpaymentAuctionDao coinpaymentAuctionDao;

    @Autowired
    private StripePresaleDao stripePresaleDao;

    @Autowired
    private CoinpaymentPresaleDao coinpaymentPresaleDao;

    @Autowired
    private PaypalDepositDao paypalDepositDao;

    @Autowired
    private CoinpaymentWalletDao coinpaymentWalletDao;

    @Autowired
    private StripeDepositDao stripeDepositDao;

    @Autowired
    private BankDepositDao bankDepositDao;

    // statements
    public Statement selectStatements(int userId, long from, long to) {
        Statement statement = new Statement();
        
        // fill one by one
        statement.setCryptoWithdraws(cryptoWithdarwDao.selectRange(userId, from, to));
        statement.setPaypalWithdraws(paypalWithdrawDao.selectRange(userId, from, to));

        statement.setStripeAuctionTxns(stripeAuctionDao.selectRange(userId, from, to));
        statement.setPaypalAuctionTxns(paypalAuctionDao.selectRange(userId, from, to));
        statement.setCoinpaymentAuctionTxns(coinpaymentAuctionDao.selectRange(userId, from, to));
        
        statement.setStripePresaleTxns(stripePresaleDao.selectRange(userId, from, to));
        statement.setPaypalPresaleTxns(paypalPresaleDao.selectRange(userId, from, to));
        statement.setCoinpaymentPresaleTxns(coinpaymentPresaleDao.selectRange(userId, from, to));
        
        statement.setPaypalDepositTxns(paypalDepositDao.selectRange(userId, from, to));
        statement.setCoinpaymentWalletTxns(coinpaymentWalletDao.selectRange(userId, from, to));
        statement.setStripeDepositTxns(stripeDepositDao.selectRange(userId, from, to));
        statement.setBankDepositTxns(bankDepositDao.selectRange(userId, from, to));
        return statement;
    }

}
