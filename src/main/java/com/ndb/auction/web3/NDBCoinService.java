package com.ndb.auction.web3;

import java.io.IOException;
import java.math.BigInteger;

import javax.annotation.PostConstruct;

import com.ndb.auction.schedule.ScheduledTasks;
import com.ndb.auction.service.payment.WithdrawService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.contracts.eip20.generated.ERC20.TransferEventResponse;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

@Service
public class NDBCoinService {
    
    @Value("${ndb.private.key}")
    private String ndbKey;

    @Value("${bsc.json.rpc}")
    private String bscNetwork;

    @Value("${ndb.token.addr}")
    private String ndbTokenContract;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private ScheduledTasks schedule;

    private Credentials ndbCredential;

    private final Web3j BEP20NET = Web3j.build(new HttpService(bscNetwork));

    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("300000");  
    private final BigInteger decimals = new BigInteger("100000000");

    @PostConstruct
    public void init() {
        Credentials ndbCredential = Credentials.create(ndbKey); 

        @SuppressWarnings("deprecation")
        ERC20 ndbToken = ERC20.load(ndbTokenContract, BEP20NET, ndbCredential, gasPrice, gasLimit);
        
        ndbToken.transferEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                handleEvent(event);
            }, error -> {
                System.out.println("Error: " + error);
            });
    }

    public NDBCoinService() {

        
    }

    private void handleEvent(TransferEventResponse event) throws IOException {
        // create new withdraw transaction record
        String from = event._from;
        String to = event._to;
        long lvalue = event._value.divide(decimals).longValue();
        Double value = ((double)lvalue) / 10000.0;
        BigInteger blockNumber = event.log.getBlockNumber();
        String txnHash = event.log.getTransactionHash();
        // withdrawService.updateTxn(from, to, value, blockNumber.toString(), txnHash);

        // add to unconfirmed list
        schedule.addPendingTxn(txnHash, blockNumber);
        System.out.println("Pending: " + txnHash);
    }

    public boolean checkConfirmation(BigInteger targetNumber) {
        // check confirm or not
        try {
            BigInteger latestNumber = BEP20NET.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock().getNumber();
            if(latestNumber.subtract(targetNumber).longValue() > 12L) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // no such block
            return false;
        }
        return false;
    }

    public boolean transferNDB(int userId, String address, Double amount) {
        try {

            @SuppressWarnings("deprecation")
            ERC20 ndbToken = ERC20.load(ndbTokenContract, BEP20NET, ndbCredential, gasPrice, gasLimit);
            Long lamount = (long) (amount * 10000);
            BigInteger _amount = BigInteger.valueOf(lamount);
            _amount = _amount.multiply(decimals);
            
            // create
            TransactionReceipt receipt = ndbToken.transfer(address, _amount).send();
            String transactionHash = receipt.getTransactionHash();
            // withdrawService.createNewWithdrawTxn(withTxn);

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
