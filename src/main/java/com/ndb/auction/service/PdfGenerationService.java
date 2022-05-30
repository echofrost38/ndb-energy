package com.ndb.auction.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ndb.auction.dao.oracle.transactions.bank.BankDepositDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentWalletDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalDepositDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeDepositDao;
import com.ndb.auction.dao.oracle.user.UserDetailDao;
import com.ndb.auction.dao.oracle.withdraw.BankWithdrawDao;
import com.ndb.auction.dao.oracle.withdraw.CryptoWithdrawDao;
import com.ndb.auction.dao.oracle.withdraw.PaypalWithdrawDao;
import com.ndb.auction.models.transactions.FiatDepositTransaction;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.bank.BankDepositTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentWalletTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.user.UserDetail;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.CryptoWithdraw;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.utils.PdfGeneratorImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfGenerationService {
    
    @Autowired
    private PdfGeneratorImpl pdfGenerator;
    @Autowired
    private CoinpaymentWalletDao cryptoDepositDao;
    @Autowired
    private StripeDepositDao stripeDepositDao;
    @Autowired
    private PaypalDepositDao paypalDepositDao;
    @Autowired
    private BankDepositDao bankDepositDao;
    @Autowired
    private CryptoWithdrawDao cryptoWithdrawDao;    
    @Autowired
    private PaypalWithdrawDao paypalWithdrawDao;
    @Autowired
    private BankWithdrawDao bankWithdrawDao;

    @Autowired
    private UserDetailDao userDetailDao;

    // Getting single or all transactions and generate pdf then return File
    /**
     * @param id transaction id 
     * @param transactionType one of 'withdraw' or 'deposit'
     * @param paymentType one of 'Crypto', 'Credit', 'PayPal' or 'Bank'
     * @return
     */
    public String generatePdfForSingleTransaction(int id, int userId, String transactionType, String paymentType) {
        
        var userDetail = userDetailDao.selectByUserId(userId);

        switch (transactionType) {
            case "DEPOSIT":
                switch (paymentType) {
                    case "PAYPAL":
                        var ptransaction = (PaypalDepositTransaction) paypalDepositDao.selectById(id);
                        return buildFiatDepositPdf(userDetail, ptransaction, paymentType, ptransaction.getPaypalOrderId());
                    case "CREDIT":
                        var stransaction = (StripeDepositTransaction) stripeDepositDao.selectById(id);
                        return buildFiatDepositPdf(userDetail, stransaction, paymentType, stransaction.getPaymentIntentId());
                    case "CRYPTO":
                        var ctransaction = (CoinpaymentWalletTransaction) cryptoDepositDao.selectById(id);
                        return buildCryptoDepositPdf(userDetail, ctransaction);
                    case "BANK":
                        var btransaction = (BankDepositTransaction) bankDepositDao.selectById(id);
                        return buildFiatDepositPdf(userDetail, btransaction, paymentType, btransaction.getUid());
                    default:
                        return null;
                }
            case "WITHDRAW":
                switch (paymentType) 
                {
                    case "PAYPAL":
                        var pwithdraw = (PaypalWithdraw) paypalWithdrawDao.selectById(id);
                        return buildPayPalWithdrawPdf(userDetail, pwithdraw);
                    case "CRYPTO":
                        var cwithdraw = (CryptoWithdraw) cryptoWithdrawDao.selectById(id);
                        return buildCrytoWithdrawPdf(userDetail, cwithdraw);
                    case "BANK":
                        var bwithdraw = (BankWithdrawRequest) bankWithdrawDao.selectById(id);
                        return buildBankWithdrawPdf(userDetail, bwithdraw);
                }
                break;
            default:
                break;
        }

        return "";    
    }

    private String buildCryptoDepositPdf(UserDetail userDetail, CoinpaymentWalletTransaction transaction) {
        if(transaction == null) return null;
        var data = buildDepositCommon(userDetail, transaction, "CRYPTO", transaction.getDepositAddress());

        data.put("fiatAmount", transaction.getCryptoAmount() + transaction.getFee());
        data.put("fiatType", String.format("%s(%s)", 
            transaction.getCryptoType(), 
            transaction.getNetwork())
        );
        data.put("fee", String.format("%.8f", transaction.getFee()));
        data.put("cryptoType", transaction.getCryptoType());
        data.put("deposited", String.format("%.8f", transaction.getCryptoAmount()));

        var pdfFileName = String.format("%s-%s-%d.pdf", "Crypto","Deposit", transaction.getId());
        pdfGenerator.generatePdfFile("/single", data, pdfFileName);
        return pdfFileName;
    }

    private String buildFiatDepositPdf(UserDetail userDetail, FiatDepositTransaction transaction, String payment, String paymentId) {
        if(transaction == null) return null;
        var data = buildDepositCommon(userDetail, transaction, payment, paymentId);

        // cent.... 
        if(payment.equals("CREDIT")) {
            data.put("fiatAmount", String.format("%.2f", transaction.getFiatAmount() / 100));    
        } else {
            data.put("fiatAmount", String.format("%.2f", transaction.getFiatAmount()));
        }
        data.put("fiatType", transaction.getFiatType());
        data.put("converted", String.format("%.8f", transaction.getDeposited() + transaction.getFee()));
        data.put("deposited", String.format("%.8f", transaction.getDeposited()));
        
        var pdfFileName = String.format("%s-%s-%d.pdf", payment, "Deposit", transaction.getId());
        pdfGenerator.generatePdfFile("/single", data, pdfFileName);
        return pdfFileName;
    }

    private Map<String, Object> buildDepositCommon(UserDetail userDetail, Transaction transaction, String payment, String paymentId) {
        var data = new HashMap<String, Object>();

        if(userDetail == null) {
            data.put("fullname", "");
            data.put("address", "");
        } else {
            data.put("fullname", userDetail.getFirstName() + ' ' + userDetail.getLastName());
            data.put("address", userDetail.getAddress());
        }

        data.put("datetime", new Timestamp(transaction.getConfirmedAt()));
        data.put("status", transaction.getStatus() ? "Successful" : "Pending");
        data.put("transactionType", "Deposit");
        data.put("paymentType", payment);
        data.put("paymentId", paymentId);

        data.put("fee", String.format("%.8f", transaction.getFee()));
        data.put("cryptoType", transaction.getCryptoType());

        var date = new Date();
        data.put("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
        return data;
    }

    private String buildPayPalWithdrawPdf(UserDetail userDetail, PaypalWithdraw withdraw) {
        if(withdraw == null) return null;
        var data = buildWithdrawCommon(userDetail, withdraw, "PAYPAL", withdraw.getReceiver());

        data.put("cryptoType", withdraw.getTargetCurrency());

        var pdfFileName = String.format("%s-%s-%d.pdf", "PayPal", "Withdraw", withdraw.getId());
        pdfGenerator.generatePdfFile("/single", data, pdfFileName);
        return pdfFileName;

    }

    private String buildCrytoWithdrawPdf(UserDetail userDetail, CryptoWithdraw withdraw) {
        if(withdraw == null) return null;
        var data = buildWithdrawCommon(userDetail, withdraw, "CRYPTO", withdraw.getDestination());

        data.put("cryptoType", withdraw.getSourceToken());
        data.put("desType", String.format("%s (%s)", withdraw.getSourceToken(), withdraw.getNetwork()));
        var pdfFileName = String.format("%s-%s-%d.pdf", "Crypto", "Withdraw", withdraw.getId());
        pdfGenerator.generatePdfFile("/single", data, pdfFileName);
        return pdfFileName;
    }

    private String buildBankWithdrawPdf(UserDetail userDetail, BankWithdrawRequest withdraw) {
        if(withdraw == null) return null;
        var data = buildWithdrawCommon(userDetail, withdraw, "BANK", withdraw.getAccountNumber());

        data.put("cryptoType", withdraw.getTargetCurrency());

        var pdfFileName = String.format("%s-%s-%d.pdf", "BANK", "Withdraw", withdraw.getId());
        pdfGenerator.generatePdfFile("/single", data, pdfFileName);
        return pdfFileName;
    }

    private Map<String, Object> buildWithdrawCommon(UserDetail userDetail, BaseWithdraw withdraw, String payment, String paymentId) {
        var data = new HashMap<String, Object>();

        if(userDetail == null) {
            data.put("fullname", "");
            data.put("address", "");
        } else {
            data.put("fullname", userDetail.getFirstName() + ' ' + userDetail.getLastName());
            data.put("address", userDetail.getAddress());
        }

        data.put("paymentType", payment);
        data.put("transactionType", "Withdraw");

        data.put("datetime", new Timestamp(withdraw.getConfirmedAt()));
        
        /// it is source token and token amount to withdraw
        data.put("fiatAmount", withdraw.getTokenAmount()); 
        data.put("fiatType", withdraw.getSourceToken());
        data.put("status", withdraw.getStatus() == 1 ? "Successful" : withdraw.getStatus() == 2 ? "Denied" : "Pending");

        // target currency and amount
        data.put("converted", String.format("%.2f", withdraw.getWithdrawAmount() + withdraw.getFee()));
        data.put("fee", String.format("%.2f", withdraw.getFee()));
        data.put("deposited", String.format("%.2f", withdraw.getWithdrawAmount()));
        data.put("paymentId", paymentId);

        data.put("reason", withdraw.getDeniedReason());

        var date = new Date();
        data.put("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
        return data;
    }

}
