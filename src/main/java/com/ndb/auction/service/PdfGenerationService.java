package com.ndb.auction.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.zxing.WriterException;
import com.ndb.auction.dao.oracle.transactions.bank.BankDepositDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentTransactionDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalDepositDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeDepositDao;
import com.ndb.auction.dao.oracle.user.UserDetailDao;
import com.ndb.auction.dao.oracle.withdraw.BankWithdrawDao;
import com.ndb.auction.dao.oracle.withdraw.CryptoWithdrawDao;
import com.ndb.auction.dao.oracle.withdraw.PaypalWithdrawDao;
import com.ndb.auction.models.transactions.FiatDepositTransaction;
import com.ndb.auction.models.transactions.Transaction;
import com.ndb.auction.models.transactions.bank.BankDepositTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalDepositTransaction;
import com.ndb.auction.models.transactions.stripe.StripeDepositTransaction;
import com.ndb.auction.models.user.UserDetail;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;
import com.ndb.auction.models.withdraw.BaseWithdraw;
import com.ndb.auction.models.withdraw.CryptoWithdraw;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.utils.PdfGeneratorImpl;
import com.ndb.auction.utils.ThirdAPIUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Service
public class PdfGenerationService {
    
    private static int DEPOSIT = 1;
    private static int WITHDRAW = 2;

    @Autowired
    private PdfGeneratorImpl pdfGenerator;
    @Autowired
    private CoinpaymentTransactionDao cryptoDepositDao;
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

    @Autowired
    private TokenAssetService tokenAssetService;

    @Autowired
    private InternalBalanceService balanceService;

    @Autowired
    private ThirdAPIUtils apiUtils;


    @Getter
    @Setter
    @AllArgsConstructor
    private class TransactionDetail {
        private String confirmedAt;
        private long epochTime;
        private String detail;
        private String amount;
        private String status;
        private int type;
    }


    public String generatePdfForMultipleTransactions(int userId, long from, long to) throws WriterException, IOException {
        var userDetail = userDetailDao.selectByUserId(userId);
        var dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        
        var strFromDate = dateFormat.format(new Date(from));
        var strToDate = dateFormat.format(new Date(to));
        
        var qrContent = String.format("Nyyu from %s to %s statement", strFromDate, strToDate);
        pdfGenerator.generateQRCodeImage(qrContent, 240, 240);

        var strDateRange = String.format("%s - %s", strFromDate, strToDate);

        var outgoing = 0.0; var incoming = 0.0;

        // get deposit transactions
        var cryptoDepositList = cryptoDepositDao.selectRange(userId, from, to, "DEPOSIT");
        var transactionDetailList = new ArrayList<TransactionDetail>();
        fillDepositList(transactionDetailList, cryptoDepositList, "Crypto");
        
        var stripeDepositList = stripeDepositDao.selectRange(userId, from, to);
        fillDepositList(transactionDetailList, stripeDepositList, "Credit");

        var paypalDepositList = paypalDepositDao.selectRange(userId, from, to);
        fillDepositList(transactionDetailList, paypalDepositList, "PayPal");

        var bankDepositList = bankDepositDao.selectRange(userId, from, to);
        fillDepositList(transactionDetailList, bankDepositList, "Bank");
            
        // get withdraw transactions
        var cryptoWithdrawList = cryptoWithdrawDao.selectRange(userId, from, to);
        fillWithdrawList(transactionDetailList, cryptoWithdrawList, "Crypto");

        var paypalWithdrawList = paypalWithdrawDao.selectRange(userId, from, to);
        fillWithdrawList(transactionDetailList, paypalWithdrawList, "PayPal");

        var bankWithdrawList = bankWithdrawDao.selectRange(userId, from, to);
        fillWithdrawList(transactionDetailList, bankWithdrawList, "Bank");

        // sort by date
        var sortedTransactionList = transactionDetailList.stream()
            .sorted(Comparator.comparingLong(TransactionDetail::getEpochTime))
            .collect(Collectors.toList());
        
        // get outgoing and incoming within ranged time
        for (TransactionDetail transaction : sortedTransactionList) {
            if(transaction.getType() == DEPOSIT) {
                try {
                    incoming += Double.valueOf(transaction.getAmount());
                } catch (Exception e) {
                }
            } else if(transaction.getType() == WITHDRAW) {
                try {
                    outgoing += Double.valueOf(transaction.getAmount());
                } catch (Exception e) {
                }
            }
        }

        // total balance
        var tokens = tokenAssetService.getAllTokenAssets(null);
        var prices = new ArrayList<Double>();
		for (var token : tokens) {
			var price = apiUtils.getCryptoPriceBySymbol(token.getTokenSymbol());
			prices.add(price);
		}
        var totalBalance = 0.0;
        var i = 0;
        for (var token : tokens) {
            var tempBalance = balanceService.getBalance(userId, token.getTokenSymbol());
            if(tempBalance == null) continue;
            totalBalance += (tempBalance.getFree() + tempBalance.getHold()) * prices.get(i);
        }   

        var data = new HashMap<String, Object>();
        data.put("dateRange", strDateRange);
        data.put("fullname", userDetail.getFirstName() + " " + userDetail.getLastName());
        
        var addr = userDetail.getAddress().split(",");
        if(addr.length > 3) {
            data.put("street", String.format("%s,%s", addr[0], addr[1]));
            data.put("country", addr[addr.length - 1]);
            addr = ArrayUtils.remove(addr, addr.length - 1);
            addr = ArrayUtils.remove(addr, 0);
            addr = ArrayUtils.remove(addr, 0);
        } else {
            data.put("street", String.format("%s,%s", addr[0]));
            data.put("country", addr[addr.length - 1]);
            addr = ArrayUtils.remove(addr, addr.length - 1);
            addr = ArrayUtils.remove(addr, 0);
        }
        
        data.put("address", String.join(",", addr));
        data.put("totalBalance", String.format("%.2f", totalBalance));
        data.put("transactions", sortedTransactionList);
        data.put("outgoings", String.format("%.2f", outgoing));
        data.put("incomes", String.format("%.2f", incoming));

        var pdfFileName = String.format("%s.pdf", "Statements");
        pdfGenerator.generatePdfFile("/statement", data, pdfFileName);

        return pdfFileName;
    }

    private void fillDepositList(List<TransactionDetail> list, List<? extends Transaction> tList, String payment) {
        var dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        list.addAll(tList.stream()
            // .filter(p -> p.getStatus())
            .map(p -> new TransactionDetail(
                dateFormat.format(new Date(p.getConfirmedAt())), 
                p.getConfirmedAt(),
                String.format("%s %s", payment, "Deposit"), 
                String.format("%.2f", p.getDeposited()), 
                p.getStatus() ? "Success" : "Failed",
                DEPOSIT))
            .collect(Collectors.toList()));
    }

    private void fillWithdrawList(List<TransactionDetail> list, List<? extends BaseWithdraw> wList, String payment) {
        var dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        list.addAll(
            wList.stream()
                // .filter(p -> p.getStatus() == 1)
                .map(p -> new TransactionDetail(
                    dateFormat.format(new Date(p.getConfirmedAt())), 
                    p.getConfirmedAt(),
                    String.format("%s %s", payment, "Withdraw"), 
                    String.format("%.2f", p.getWithdrawAmount()), 
                    p.getStatus() == 1 ? "Success" : 
                        p.getStatus() == 2 ? "Denied" : "Pending",
                    WITHDRAW))  
                .collect(Collectors.toList())
        );
    }

    // Getting single or all transactions and generate pdf then return File
    /**
     * @param id transaction id 
     * @param transactionType one of 'withdraw' or 'deposit'
     * @param paymentType one of 'Crypto', 'Credit', 'PayPal' or 'Bank'
     * @return
     * @throws IOException
     * @throws WriterException
     */
    public String generatePdfForSingleTransaction(int id, int userId, String transactionType, String paymentType) throws WriterException, IOException {
        
        var userDetail = userDetailDao.selectByUserId(userId);

        var qrContent = String.format("Nyyu %s %s statement", paymentType, transactionType);
        pdfGenerator.generateQRCodeImage(qrContent, 240, 240);

        switch (transactionType) {
            case "DEPOSIT":
                switch (paymentType) {
                    case "PAYPAL":
                        var ptransaction = (PaypalDepositTransaction) paypalDepositDao.selectById(id, 1);
                        return buildFiatDepositPdf(userDetail, ptransaction, paymentType, ptransaction.getPaypalOrderId());
                    case "CREDIT":
                        var stransaction = (StripeDepositTransaction) stripeDepositDao.selectById(id, 1);
                        return buildFiatDepositPdf(userDetail, stransaction, paymentType, stransaction.getPaymentIntentId());
                    case "CRYPTO":
                        var ctransaction = (CoinpaymentDepositTransaction) cryptoDepositDao.selectById(id);
                        return buildCryptoDepositPdf(userDetail, ctransaction);
                    case "BANK":
                        var btransaction = (BankDepositTransaction) bankDepositDao.selectById(id, 1);
                        return buildFiatDepositPdf(userDetail, btransaction, paymentType, btransaction.getUid());
                    default:
                        return null;
                }
            case "WITHDRAW":
                switch (paymentType) 
                {
                    case "PAYPAL":
                        var pwithdraw = (PaypalWithdraw) paypalWithdrawDao.selectById(id, 1);
                        return buildPayPalWithdrawPdf(userDetail, pwithdraw);
                    case "CRYPTO":
                        var cwithdraw = (CryptoWithdraw) cryptoWithdrawDao.selectById(id, 1);
                        return buildCrytoWithdrawPdf(userDetail, cwithdraw);
                    case "BANK":
                        var bwithdraw = (BankWithdrawRequest) bankWithdrawDao.selectById(id, 1);
                        return buildBankWithdrawPdf(userDetail, bwithdraw);
                }
                break;
            default:
                break;
        }

        return "";    
    }

    private String buildCryptoDepositPdf(UserDetail userDetail, CoinpaymentDepositTransaction transaction) {
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
            
            var addr = userDetail.getAddress().split(",");
            if(addr.length > 3) {
                data.put("street", String.format("%s,%s", addr[0], addr[1]));
                data.put("country", addr[addr.length - 1]);
                addr = ArrayUtils.remove(addr, addr.length - 1);
                addr = ArrayUtils.remove(addr, 0);
                addr = ArrayUtils.remove(addr, 0);
            } else {
                data.put("street", String.format("%s,%s", addr[0]));
                data.put("country", addr[addr.length - 1]);
                addr = ArrayUtils.remove(addr, addr.length - 1);
                addr = ArrayUtils.remove(addr, 0);
            }
            
            data.put("address", String.join(",", addr));
        }

        data.put("datetime", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS").format(transaction.getConfirmedAt()));
        
        // exception for crypto deposit
        if(transaction.getStatus() == null) {
            var _transaction = (CoinpaymentDepositTransaction) transaction;
            data.put("status", _transaction.getDepositStatus() == 0 ? "Pending" : _transaction.getDepositStatus() == 1 ? "Successful" : "Expired"
            );
        } else {
            data.put("status", transaction.getStatus() ? "Successful" : "Pending");
        }
        
        data.put("transactionType", "Deposit");
        data.put("paymentType", payment);
        data.put("paymentId", paymentId);

        data.put("fee", String.format("%.8f", transaction.getFee()));
        data.put("cryptoType", transaction.getCryptoType());

        var date = new Date();
        data.put("currentDate", new SimpleDateFormat("dd/MM/yyyy").format(date));
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
        data.put("bankName", withdraw.getBankName());
        data.put("accountNum", withdraw.getAccountNumber());
        data.put("holder", withdraw.getHolderName());

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
            
            var addr = userDetail.getAddress().split(",");
            if(addr.length > 3) {
                data.put("street", String.format("%s,%s", addr[0], addr[1]));
                data.put("country", addr[addr.length - 1]);
                addr = ArrayUtils.remove(addr, addr.length - 1);
                addr = ArrayUtils.remove(addr, 0);
                addr = ArrayUtils.remove(addr, 0);
            } else {
                data.put("street", String.format("%s,%s", addr[0]));
                data.put("country", addr[addr.length - 1]);
                addr = ArrayUtils.remove(addr, addr.length - 1);
                addr = ArrayUtils.remove(addr, 0);
            }
            
            data.put("address", String.join(",", addr));
        }

        data.put("paymentType", payment);
        data.put("transactionType", "Withdraw");

        data.put("datetime", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS").format(withdraw.getConfirmedAt()));
        
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
        data.put("currentDate", new SimpleDateFormat("dd/MM/yyyy").format(date));
        return data;
    }

}
