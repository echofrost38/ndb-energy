package com.ndb.auction.web3;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;

import com.ndb.auction.contracts.NdbWallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;


@Service
public class NdbWalletService {
    
    // RPC URLs

    // Contract address


    // Configuration
    // private final Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed.binance.org/"));
    // private final Web3j web3j = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));
    private final Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161"));
    private final String password = "aacae1e3f6d8229ad02525a1c952c51925430ad5df73d062c78d769089dba1d4";
    private final String contractAddress = "0x81c10Eeacf1cCcdA36D954e9e1b24f41D8318b21";
    // private final String contractAddress = "0x736680D21e2B0C63813FEBc4432891579C28EEe8";

    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("300000");  

    // private final int decimal = 12;
    // private final BigInteger bDecimal = new BigInteger("1000000000000");

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

    public TransactionReceipt createWalletWithEmail(String email, String tokenType, String privateKey) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                System.out.println("Creating new wallet...");
                receipt = ndbWallet.createWalletWithEmail(email, tokenType, privateKey).send();
                System.out.println("New wallet is created: " + tokenType);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt createWalletWithId(String id, String tokenType, String privateKey) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                System.out.println("Creating new wallet...");
                receipt = ndbWallet.createWalletWithId(id, tokenType, privateKey).send();
                System.out.println("New wallet is created: " + tokenType);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public BigInteger getDecimals() {
        BigInteger decimal = null;
        try {
            if (ndbWallet != null) {
                decimal = ndbWallet.decimals().send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return decimal;
    }

    public TransactionReceipt increaseHoldBalanceWithEmail(String email, String tokenType, BigInteger amount) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                receipt = ndbWallet.increaseHoldBalanceWithEmail(email, tokenType, amount).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt decreaseHoldBalanceWithId(String id, String tokenType, BigInteger amount) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                receipt = ndbWallet.decreaseHoldBalanceWithId(id, tokenType, amount).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public BigInteger getHoldBalanceWithEmail(String email, String tokenType) {
        BigInteger holdBalance = null;
        try {
            if (ndbWallet != null) {
                holdBalance = ndbWallet.getHoldBalanceWithEmail(email, tokenType).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return holdBalance;
    }

    public BigInteger getHoldBalanceWithId(String id, String tokenType) {
        BigInteger holdBalance = null;
        try {
            if (ndbWallet != null) {
                holdBalance = ndbWallet.getHoldBalanceWithId(id, tokenType).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return holdBalance;
    }

    public String getOwner() {
        String owner = null;
        try {
            if (ndbWallet != null) {
                owner = ndbWallet.getOwner().send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return owner;
    }

    public String getPrivateKeyWithEmail(String email, String tokenType) {
        String privateKey = null;
        try {
            if (ndbWallet != null) {
                privateKey = ndbWallet.getPrivateKeyWithEmail(email, tokenType).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return privateKey;
    }

    public String getPrivateKeyWithId(String id, String tokenType) {
        String privateKey = null;
        try {
            if (ndbWallet != null) {
                privateKey = ndbWallet.getPrivateKeyWithId(id, tokenType).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return privateKey;
    }

    public TransactionReceipt decreaseHoldBalanceWithEmail(String email, String tokenType, BigInteger amount) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                receipt = ndbWallet.decreaseHoldBalanceWithEmail(email, tokenType, amount).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt increaseHoldBalanceWithId(String id, String tokenType, BigInteger amount) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                receipt = ndbWallet.increaseHoldBalanceWithId(id, tokenType, amount).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt setHoldBalanceWithEmail(String email, String tokenType, BigInteger amount) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                receipt = ndbWallet.setHoldBalanceWithEmail(email, tokenType, amount).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt setHoldBalanceWithId(String id, String tokenType, BigInteger amount) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                receipt = ndbWallet.setHoldBalanceWithId(id, tokenType, amount).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    public TransactionReceipt transferOwnerShip(String newOwner) {
        TransactionReceipt receipt = null;
        try {
            if (ndbWallet != null) {
                receipt = ndbWallet.transferOwnership(newOwner).send();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receipt;
    }

    // Generate wallet address!
    public String generateWalletAddress(String id, String tType) {
        String seed = UUID.randomUUID().toString();
        String address = null;
        
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

            String sPrivatekeyInHex = privateKeyInDec.toString(16);
            WalletFile wallet = Wallet.createLight(seed, ecKeyPair);
            address = wallet.getAddress(); 

            Credentials credentials = Credentials.create(password);
            
            

            // save to database
            // createWalletWithId(id, tokenType, privateKey);

        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException | CipherException e) {
            e.printStackTrace();
        }
        return "0x" + address;
    }

    @SuppressWarnings("deprecation")
    public BigInteger getWalletBalance(String token, String network, String address) {
        String seed = UUID.randomUUID().toString();
        try {
            // ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            
            // BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

            // String sPrivatekeyInHex = privateKeyInDec.toString(16);
            // WalletFile wallet = Wallet.createLight(seed, ecKeyPair);
            // address = wallet.getAddress(); 
            String privateKey = "05a30ce0d427acfc6a22588d5377f8346fb6cd1adfc6eda37411b6d2adeb11b9";
            Credentials credentials = Credentials.create(privateKey);
            
            ERC20 erc20 = ERC20.load("0xB8c77482e45F1F44dE1745F52C74426C631bDD52", web3j, credentials, gasPrice, gasLimit);

            return erc20.balanceOf("0x" + address).send();

            // save to database
            // createWalletWithId(id, tokenType, privateKey);

        } catch (Exception e) {
            e.printStackTrace();
            return BigInteger.valueOf(-1);
        }
    }

    public Boolean transferFunds(String token, String network, String address, long amount) {
        try {
            String privateKey = "ac3ffc57465944b1309dcde8fc8ffa2dc417deaafe42f99538414aee7df957f1";
            Credentials credentials = Credentials.create(privateKey);
            
            @SuppressWarnings("deprecation")
            ERC20 erc20 = ERC20.load("0x81c10Eeacf1cCcdA36D954e9e1b24f41D8318b21", web3j, credentials, gasPrice, gasLimit);

            BigInteger _amount = BigInteger.valueOf(amount * 100);
            erc20.transfer(address, _amount).send();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
