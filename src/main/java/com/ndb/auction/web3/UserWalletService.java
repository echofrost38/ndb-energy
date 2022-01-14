package com.ndb.auction.web3;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.contracts.UserWallet;
import com.ndb.auction.models.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;

@Service
public class UserWalletService {

    private final Web3j web3j = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));
    // private final Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));
    private final String password = "05a30ce0d427acfc6a22588d5377f8346fb6cd1adfc6eda37411b6d2adeb11b9";
    private final String contractAddress = "0x736680D21e2B0C63813FEBc4432891579C28EEe8";

    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("300000");  

    private final int decimal = 12;
    private final BigInteger bDecimal = new BigInteger("1000000000000");

    private UserWallet userWallet;

    @Autowired
    public UserWalletService() {
        this.userWallet = null;
        this.userWallet = loadTraderContract(password);
    }

    public TransactionReceipt addNewUser(int id, String email, String name) {
        TransactionReceipt receipt = null;
        try {
            if (userWallet != null) {
                System.out.println("Adding User...");
                receipt = userWallet.addNewUser(id, email, name).send();
                System.out.println("Successfully Added New User: " + name);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt addFreeAmount(int id, String crypto, long _amount) {
        TransactionReceipt receipt = null;
        try {
            if(userWallet != null) {
                System.out.println("Adding Free Amount...");
                
                // processing decimal
                BigInteger amount = BigDecimal.valueOf(_amount).toBigInteger();
                amount = amount.pow(decimal);
                receipt = userWallet.addFreeAmount(id, crypto, amount).send();
                System.out.println("Added Free amount: " + amount);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt addHoldAmount(int id, String crypto, long _amount) {
        TransactionReceipt receipt = null;
        try {
            if(userWallet != null) {
                System.out.println("Adding Hold Amount...");

                // processing decimal
                BigInteger amount = BigDecimal.valueOf(_amount).toBigInteger();
                amount = amount.pow(decimal);
                receipt = userWallet.addHoldAmount(id, crypto, amount).send();
                System.out.println("Successfully Added Hold Amount: " + receipt.getLogs());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt makeHold(int id, String crypto, long _amount) {
        TransactionReceipt receipt = null;
        try {
            if(userWallet != null) {
                System.out.println("Making Hold...");

                // processing decimal
                BigInteger amount = BigDecimal.valueOf(_amount).toBigInteger();
                amount = amount.pow(decimal);
                receipt = userWallet.makeHold(id, crypto, amount).send();
                System.out.println("Successfully Made Hold: " + receipt.getLogs());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt releaseHold(int id, String crypto, long _amount) {
        TransactionReceipt receipt = null;
        try {
            if(userWallet != null) {
                System.out.println("Release Hold...");

                // processing decimal
                BigInteger amount = BigDecimal.valueOf(_amount).toBigInteger();
                amount = amount.pow(decimal);
                receipt = userWallet.releaseHold(id, crypto, amount).send();
                System.out.println("Successfully Released Hold: " + amount);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public String getUserName(int id) {
        String result = "";
        try {
            if(userWallet != null) {
                result = userWallet.getUserName(id).send();
                System.out.println(result);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public Wallet getWalletById(int id, String crypto) {
        Wallet wallet = null;
        try {
            Tuple2<BigInteger, BigInteger> tuple2 = userWallet.getWalletById(id, crypto).send();
            
            BigInteger bFree = tuple2.component1();
            int free = bFree.divide(bDecimal).intValue();

            BigInteger bHold = tuple2.component2();
            int hold = bHold.divide(bDecimal).intValue();
            
            // wallet = new Wallet(crypto, free, hold);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return wallet;
    }

    public List<Wallet> getWallets(int id) {
        List<Wallet> wallets = new ArrayList<>();
        try {
            Tuple3<List<String>, List<BigInteger>, List<BigInteger>> tuple3 = userWallet.getWallets(id).send();
            List<String> keyList = tuple3.component1();
            List<BigInteger> freeList = tuple3.component2();
            List<BigInteger> holdList = tuple3.component3();
            int len = keyList.size();
            for(int i = 0; i < len; i++) {
                BigInteger bFree = freeList.get(i);
                int free = bFree.divide(bDecimal).intValue();

                BigInteger bHold = holdList.get(i);
                int hold = bHold.divide(bDecimal).intValue();

                // Wallet wallet = new Wallet(keyList.get(i), free, hold);
                // wallets.add(wallet);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return wallets;
    }

    @SuppressWarnings("deprecation")
	private UserWallet loadTraderContract(String _password) {
        UserWallet userWallet = null;
        try {
            Credentials credentials = Credentials.create(_password);
            userWallet = UserWallet.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
        }
        catch(Exception e) {
            
        }
        return userWallet;
    }

}
