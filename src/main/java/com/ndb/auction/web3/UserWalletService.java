package com.ndb.auction.web3;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.contracts.UserWallet;
import com.ndb.auction.models.user.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;

@Service
public class UserWalletService implements EnvironmentAware {

    @Override
    public void setEnvironment(Environment environment) {
        this.rpcServer = environment.getProperty("RPC_SERVER");
        this.password = environment.getProperty("WALLET_SECRET");
        this.contractAddress = environment.getProperty("INTERNAL_WALLET");
        this.web3j = Web3j.build(new HttpService(this.rpcServer));
    }

    private Web3j web3j;
    // private final Web3j web3j = Web3j.build(new HttpService("HTTP://127.0.0.1:7545"));
    private String rpcServer;
    private String password;
    private String contractAddress;

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

    public TransactionReceipt addNewUser(String id, String email, String name) {
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

    public TransactionReceipt addFreeAmount(String id, String crypto, double _amount) {
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

    public TransactionReceipt addHoldAmount(String id, String crypto, double _amount) {
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

    public TransactionReceipt makeHold(String id, String crypto, double _amount) {
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

    public TransactionReceipt releaseHold(String id, String crypto, double _amount) {
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

    public String getUserName(String id) {
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

    public Wallet getWalletById(String id, String crypto) {
        Wallet wallet = null;
        try {
            Tuple2<BigInteger, BigInteger> tuple2 = userWallet.getWalletById(id, crypto).send();
            
            BigInteger bFree = tuple2.component1();
            double free = bFree.divide(bDecimal).doubleValue();

            BigInteger bHold = tuple2.component2();
            double hold = bHold.divide(bDecimal).doubleValue();
            
            wallet = new Wallet(crypto, free, hold);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return wallet;
    }

    public List<Wallet> getWallets(String id) {
        List<Wallet> wallets = new ArrayList<Wallet>();
        try {
            Tuple3<List<String>, List<BigInteger>, List<BigInteger>> tuple3 = userWallet.getWallets(id).send();
            List<String> keyList = tuple3.component1();
            List<BigInteger> freeList = tuple3.component2();
            List<BigInteger> holdList = tuple3.component3();
            int len = keyList.size();
            for(int i = 0; i < len; i++) {
                BigInteger bFree = freeList.get(i);
                double free = bFree.divide(bDecimal).doubleValue();

                BigInteger bHold = holdList.get(i);
                double hold = bHold.divide(bDecimal).doubleValue();

                Wallet wallet = new Wallet(keyList.get(i), free, hold);
                wallets.add(wallet);
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
            System.out.println(credentials.getAddress());
            userWallet = UserWallet.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
            System.out.println(userWallet.getContractAddress());
        }
        catch(Exception e) {
            
        }
        return userWallet;
    }

}
