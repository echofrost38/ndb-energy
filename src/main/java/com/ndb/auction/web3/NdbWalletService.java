package com.ndb.auction.web3;

import java.math.BigInteger;

import com.ndb.auction.contracts.NdbWallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

@Service
public class NdbWalletService {
    
    // Configuration
    private final Web3j web3j = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));
    // private final Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));
    private final String password = "05a30ce0d427acfc6a22588d5377f8346fb6cd1adfc6eda37411b6d2adeb11b9";
    private final String contractAddress = "0x736680D21e2B0C63813FEBc4432891579C28EEe8";

    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("300000");  

    private final int decimal = 12;
    private final BigInteger bDecimal = new BigInteger("1000000000000");

    private NdbWallet ndbWallet;

    @Autowired
    public NdbWalletService() {
        this.ndbWallet = null;
        this.ndbWallet = loadTraderContract(password);
    }

    @SuppressWarnings("deprecation")
	private NdbWallet loadTraderContract(String _password) {
        NdbWallet ndbWallet = null;
        try {
            Credentials credentials = Credentials.create(_password);
            System.out.println(credentials.getAddress());
            ndbWallet = NdbWallet.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
            System.out.println(ndbWallet.getContractAddress());
        }
        catch(Exception e) {
            
        }
        return ndbWallet;
    }

    /// Create new account!
    public TransactionReceipt createAccount(String id, String email) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                System.out.println("Adding User...");
                receipt = ndbWallet.createAccount(id, email).send();
                System.out.println("Successfully Added New User: " + email);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    

}
