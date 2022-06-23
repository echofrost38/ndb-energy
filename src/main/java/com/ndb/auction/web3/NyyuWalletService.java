package com.ndb.auction.web3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;

@Service
@Slf4j
public class NyyuWalletService {
    @Value("${bsc.json.rpc}")
    private String bscNetwork;
    @Value("${nyyu.wallet.password}")
    private  String password;
    public Tuple2<String,String> generateBEP20Address() {
        Web3j web3j = Web3j.build(new HttpService(bscNetwork));
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            WalletFile wallet = Wallet.createStandard(password, keyPair);

            System.out.println("Private key: " + keyPair.getPrivateKey().toString(16));
            System.out.println("Account: " + wallet.getAddress());
            return new Tuple2<>(wallet.getAddress(),keyPair.getPrivateKey().toString(16));
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }
}
