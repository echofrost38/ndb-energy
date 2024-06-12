package com.ndb.auction.web3;

import com.ndb.auction.dao.oracle.wallet.NyyuWalletDao;
import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.*;

@Service
public class NyyuWalletService extends BaseService {
    @Autowired
    private NyyuWalletDao nyyuWalletDao;
    @Value("${bsc.json.rpc}")
    private String bscNetwork;
    @Value("${nyyu.wallet.password}")
    private  String password;
    @Value("${bsc.json.chainid}")
    private String chainId;

    @Transactional
    public String generateBEP20Address(int userId) {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            WalletFile wallet = Wallet.createStandard(password, keyPair);

            String address = "0x"+ wallet.getAddress();
            NyyuWallet nyyuWallet = new NyyuWallet();
            nyyuWallet.setUserId(userId);
            nyyuWallet.setPublicKey(address);
            nyyuWallet.setPrivateKey(keyPair.getPrivateKey().toString(16));
            nyyuWallet.setNetwork(chainId);
            nyyuWalletDao.insertOrUpdate(nyyuWallet);
            
            nyyuPayService.sendAddressRequest(nyyuWallet.getPublicKey());
            
            System.out.println("Private key: " + keyPair.getPrivateKey().toString(16));
            System.out.println("Account: " + address);
            return address;
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    public NyyuWallet selectByUserId(int userId) {
        return nyyuWalletDao.selectByUserId(userId);
    }

    public NyyuWallet selectByAddress(String address){
        return nyyuWalletDao.selectByAddress(address);
    }
}
