package com.ndb.auction.web3;

import java.math.BigInteger;

import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Service
public class NDBCoinService {
    
    private final Web3j bscTestNet = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545/"));
    private final String ndbContract = "0x2A90DBBcf6f19fdAE1E07Cec39ee0c591c6110Af";
    
    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("300000");  
    private final BigInteger decimals = new BigInteger("1000000");

    public boolean transferNDB(String address, Double amount) {
        try {
            String privateKey = "56eab44f1e2a9de8a79f8bd42003d270bd7f58d4242b954a94c1bf5d8f8fdc55";
            Credentials credentials = Credentials.create(privateKey);    

            @SuppressWarnings("deprecation")
            ERC20 ndbToken = ERC20.load(ndbContract, bscTestNet, credentials, gasPrice, gasLimit);
            Long lamount = (long) (amount * 1000000);
            BigInteger _amount = BigInteger.valueOf(lamount);
            _amount = _amount.multiply(decimals);
            ndbToken.transfer(address, _amount).send();

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
