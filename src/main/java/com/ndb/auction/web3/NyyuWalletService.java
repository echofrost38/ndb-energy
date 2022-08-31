package com.ndb.auction.web3;

import com.ndb.auction.dao.oracle.wallet.NyyuWalletDao;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.utils.Utilities;

import lombok.RequiredArgsConstructor;

import com.paymennt.solanaj.data.SolanaAccount;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tron.trident.core.key.KeyPair;
import org.web3j.crypto.*;

@Service
@RequiredArgsConstructor
public class NyyuWalletService extends BaseService {
    
    private final NyyuWalletDao nyyuWalletDao;
    private final Utilities util;
    
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
        return nyyuWalletDao.selectByUserId(userId, network);
    }

    public NyyuWallet selectByAddress(String address){
        return nyyuWalletDao.selectByAddress(address);
    }

    public int updatePrivateKeys() {
        var nyyuWalletList = nyyuWalletDao.selectAll();
        for (var wallet : nyyuWalletList) {
            var encryptedKey = util.encrypt(wallet.getPrivateKey());
            wallet.setPrivateKey(encryptedKey);
            nyyuWalletDao.updatePrivateKey(wallet);
        }
        return nyyuWalletList.size();
    }
}
