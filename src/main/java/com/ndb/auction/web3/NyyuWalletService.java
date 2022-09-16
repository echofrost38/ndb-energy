package com.ndb.auction.web3;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tron.trident.core.key.KeyPair;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import com.ndb.auction.dao.oracle.transactions.bank.BankDepositDao;
import com.ndb.auction.dao.oracle.transactions.coinpayment.CoinpaymentTransactionDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalDepositDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalPresaleDao;
import com.ndb.auction.dao.oracle.transactions.paypal.PaypalTransactionDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeDepositDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripePresaleDao;
import com.ndb.auction.dao.oracle.transactions.stripe.StripeTransactionDao;
import com.ndb.auction.dao.oracle.wallet.NyyuWalletDao;
import com.ndb.auction.dao.oracle.wallet.NyyuWalletTransactionDao;
import com.ndb.auction.dao.oracle.withdraw.BankWithdrawDao;
import com.ndb.auction.dao.oracle.withdraw.CryptoWithdrawDao;
import com.ndb.auction.dao.oracle.withdraw.PaypalWithdrawDao;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.transactions.bank.BankDepositTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentTransaction;
import com.ndb.auction.models.transactions.paypal.PaypalTransaction;
import com.ndb.auction.models.transactions.stripe.StripeTransaction;
import com.ndb.auction.models.transactions.wallet.NyyuWalletTransaction;
import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.models.withdraw.BankWithdrawRequest;
import com.ndb.auction.models.withdraw.CryptoWithdraw;
import com.ndb.auction.models.withdraw.PaypalWithdraw;
import com.ndb.auction.payload.response.BalanceTrack;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.solanaj.data.SolanaAccount;
import com.ndb.auction.utils.Utilities;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NyyuWalletService extends BaseService {
    
    private final NyyuWalletDao nyyuWalletDao;
    private final Utilities util;

    // deposit dao
    private final CoinpaymentTransactionDao cryptoDepositDao;
    private final PaypalTransactionDao paypalDepositDao;
    private final StripeTransactionDao stripeDepositDao;
    private final BankDepositDao bankDepositDao;

    private final NyyuWalletTransactionDao walletTransactionDao;

    // withdrawal
    private final PaypalWithdrawDao paypalWithdrawDao;
    private final CryptoWithdrawDao cryptoWithdrawDao;
    private final BankWithdrawDao bankWithdrawDao;
    
    @Value("${bsc.json.rpc}")
    private String bscNetwork;
    @Value("${nyyu.wallet.password}")
    private  String password;

    private String walletRegisterEndpoint = "/wallet";

    private String generateBEP20Address(int userId) {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            WalletFile wallet = Wallet.createStandard(password, keyPair);

            String address = "0x"+ wallet.getAddress();
            NyyuWallet nyyuWallet = new NyyuWallet();
            nyyuWallet.setUserId(userId);
            nyyuWallet.setPublicKey(address);

            // encrypt private key!!
            var plainPrivKey = keyPair.getPrivateKey().toString(16);
            var encryptedPrivKey = util.encrypt(plainPrivKey);
            if(encryptedPrivKey == null) {
                // failed to encrypt
                throw new UnauthorizedException("Cannot create Nyyu wallet.", "wallet");
            }

            nyyuWallet.setPrivateKey(encryptedPrivKey);
            nyyuWallet.setNetwork("BEP20");
            
            var registered = nyyuPayService.sendNyyuPayRequest(walletRegisterEndpoint, nyyuWallet.getPublicKey());
            nyyuWallet.setNyyuPayRegistered(registered);
            nyyuWalletDao.insertOrUpdate(nyyuWallet);
            
            if(registered) {
                return address;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // generate TRON wallet 
    private String generateTronWallet(int userId) {
        // create new tron wallet key pair
        KeyPair keyPair = KeyPair.generate();
        var nyyuWallet = NyyuWallet.builder()
            .userId(userId)
            .publicKey(keyPair.toBase58CheckAddress())
            .privateKey(keyPair.toPrivateKey())
            .network("TRC20")
            .build();

        var encryptedKey = util.encrypt(nyyuWallet.getPrivateKey());
        if(encryptedKey == null) {
            // failed to encrypt
            throw new UnauthorizedException("Cannot create Nyyu wallet.", "wallet");
        }
        nyyuWallet.setPrivateKey(encryptedKey);

        var registered = nyyuPayService.sendNyyuPayRequest(walletRegisterEndpoint, nyyuWallet.getPublicKey());
        nyyuWallet.setNyyuPayRegistered(registered);
        nyyuWalletDao.insertOrUpdate(nyyuWallet);

        if(registered) return keyPair.toBase58CheckAddress();
        return null;
    }

    // generate solana wallet
    private String generateSolanaWallet(int userId) {
        var solAccount =  new SolanaAccount();
        var encryptedKey = util.encrypt(solAccount.getSecretKey());
        if(encryptedKey == null) {
            // failed to encrypt
            throw new UnauthorizedException("Cannot create Nyyu wallet.", "wallet");
        }

        var nyyuWallet = NyyuWallet.builder()
            .userId(userId)
            .publicKey(solAccount.getPublicKey().toBase58())
            .privateKey(encryptedKey)
            .network("SOL")
            .build();

        var registered = nyyuPayService.sendNyyuPayRequest(walletRegisterEndpoint, nyyuWallet.getPublicKey());
        nyyuWallet.setNyyuPayRegistered(registered);
        nyyuWalletDao.insertOrUpdate(nyyuWallet);

        if(registered) return nyyuWallet.getPublicKey();
        return null;
    }

    public String generateNyyuWallet(String network, int userId) {
        switch(network) {
            case "BEP20": 
                return generateBEP20Address(userId);
            case "ERC20":
                return generateBEP20Address(userId);
            case "TRC20": 
                return generateTronWallet(userId);
            case "SOL": 
                return generateSolanaWallet(userId);
        }
        return "";
    }

    public String registerNyyuWallet(NyyuWallet wallet) {
        try {
            var registered = nyyuPayService.sendNyyuPayRequest(walletRegisterEndpoint, wallet.getPublicKey());
            wallet.setNyyuPayRegistered(registered);
            nyyuWalletDao.insertOrUpdate(wallet);
            
            if(registered) {
                return wallet.getPublicKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public NyyuWallet selectByUserId(int userId, String network) {
        network = network.equals("ERC20") ? "BEP20" : network;
        return nyyuWalletDao.selectByUserId(userId, network);
    }

    public NyyuWallet selectByAddress(String address){
        return nyyuWalletDao.selectByAddress(address);
    }

    /// test purchase
    public int updatePrivateKeys() {
        var nyyuWalletList = nyyuWalletDao.selectAll();
        for (var wallet : nyyuWalletList) {
            var encryptedKey = util.encrypt(wallet.getPrivateKey());
            wallet.setPrivateKey(encryptedKey);
            nyyuWalletDao.updatePrivateKey(wallet);
        }
        return nyyuWalletList.size();
    }

    // Fetch all transactions and return balance history
    public List<BalanceTrack> fetchBalanceHistory(int userId) {
        var list = new LinkedList<BalanceTrack>();
        // 1. deposit txns
        // 1A) Crypto deposit 
        var cryptoDepositTxns = cryptoDepositDao.selectByOrderTypeByUser(userId, 1, "DEPOSIT");
        for (CoinpaymentDepositTransaction txn : cryptoDepositTxns) {
            var track = BalanceTrack.builder()
                .userId(userId)
                .txnType(BalanceTrack.DEPOSIT)
                .gatewayType("CRYPTO")
                .txnId(txn.getId())
                .sourceAmount(txn.getCryptoAmount())
                .sourceType(txn.getCryptoType())
                .usdAmount(txn.getAmount())
                .createdAt(txn.getCreatedAt())
                .updatedAt(txn.getConfirmedAt())
                .build();
            list.push(track);
        }

        // 1B) Stripe Deposit
        var stripeDepositTxns = stripeDepositDao.selectByUser(userId, 1, null);
        for (StripeTransaction txn : stripeDepositTxns) {
            var track = BalanceTrack.builder()
                .userId(userId)
                .txnType(BalanceTrack.DEPOSIT)
                .gatewayType("STRIPE")
                .txnId(txn.getId())
                .sourceAmount(txn.getFiatAmount())
                .sourceType(txn.getFiatType())
                .usdAmount(txn.getUsdAmount())
                .createdAt(txn.getCreatedAt())
                .updatedAt(txn.getUpdatedAt())
                .build();
            list.push(track);
        }

        // 1C) Paypal Deposit
        var paypalDepositTxns = paypalDepositDao.selectByUser(userId, 1, null);
        for (PaypalTransaction txn : paypalDepositTxns) {
            var track = BalanceTrack.builder()
                .userId(userId)
                .txnType(BalanceTrack.DEPOSIT)
                .gatewayType("PAYPAL")
                .txnId(txn.getId())
                .sourceAmount(txn.getFiatAmount())
                .sourceType(txn.getFiatType())
                .usdAmount(txn.getUsdAmount())
                .createdAt(txn.getCreatedAt())
                .updatedAt(txn.getUpdatedAt())
                .build();
            list.push(track);
        }

        // 1D) Bank Deposit
        @SuppressWarnings("unchecked")
        var bankDepositTxns = (List<BankDepositTransaction>) bankDepositDao.selectByUser(userId, null, 1);
        for (BankDepositTransaction txn : bankDepositTxns) {
            var track = BalanceTrack.builder()
                .userId(userId)
                .txnType(BalanceTrack.DEPOSIT)
                .gatewayType("BANK")
                .txnId(txn.getId())
                .sourceAmount(txn.getFiatAmount())
                .sourceType(txn.getFiatType())
                .usdAmount(txn.getUsdAmount())
                .createdAt(txn.getCreatedAt())
                .updatedAt(txn.getConfirmedAt())
                .build();
            list.push(track);
        }

        // 2. purchase txns
        var walletPurchaseTxns = walletTransactionDao.selectByUserId(userId);
        for (NyyuWalletTransaction txn : walletPurchaseTxns) {
            var track = BalanceTrack.builder()
                .userId(userId)
                .txnType(BalanceTrack.PURCHASE)
                .gatewayType("PRESALE")
                .txnId(txn.getId())
                .sourceAmount(txn.getAmount())
                .sourceType(txn.getAssetType())
                .usdAmount(txn.getUsdAmount())
                .createdAt(txn.getCreatedAt())
                .updatedAt(txn.getCreatedAt())
                .build();
            list.push(track);
        }

        // 3. withdraw txns
        // 3A) bank withdrawal
        var bankWithdrawals = bankWithdrawDao.selectByUser(userId, 1);
        for (BankWithdrawRequest request : bankWithdrawals) {
            if(request.getStatus() != 1) continue;
            var track = BalanceTrack.builder()
                .userId(userId)
                .txnType(BalanceTrack.WITHDRAW)
                .gatewayType("BANK")
                .txnId(request.getId())
                .sourceAmount(request.getTokenAmount())
                .sourceType(request.getSourceToken())
                .usdAmount(request.getWithdrawAmount())
                .createdAt(request.getRequestedAt())
                .updatedAt(request.getConfirmedAt())
                .build();
            list.push(track);
        }

        return null;
    }
}
