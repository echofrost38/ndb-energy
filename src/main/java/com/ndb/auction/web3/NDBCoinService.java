package com.ndb.auction.web3;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import com.ndb.auction.contracts.NDBcoin;
import com.ndb.auction.schedule.ScheduledTasks;
import com.ndb.auction.service.payment.WithdrawService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.contracts.eip20.generated.ERC20.TransferEventResponse;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

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
    private NDBcoin ndbToken;
    private FastRawTransactionManager txMananger;

    private final Web3j BEP20NET = Web3j.build(new HttpService(bscNetwork));

    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("300000");  
    private final BigInteger decimals = new BigInteger("1000000000000");

    @PostConstruct
    public void init() throws IOException {
        ndbCredential = Credentials.create(ndbKey);
        txMananger = new FastRawTransactionManager(BEP20NET, ndbCredential, 56);
        ndbToken = NDBcoin.load(ndbTokenContract, BEP20NET, txMananger, new DefaultGasProvider());
        ndbToken.transferEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            .subscribe(event -> {
                handleEvent(event);
            }, error -> {
                System.out.println("Error: " + error);
            });
    }

    private void handleEvent(NDBcoin.TransferEventResponse event) throws IOException {
        // create new withdraw transaction record
        String from = event.from;
        String to = event.to;
        long lvalue = event.value.divide(decimals).longValue();
        Double value = ((double)lvalue) / 10000.0;
        BigInteger blockNumber = event.log.getBlockNumber();
        String txnHash = event.log.getTransactionHash();
        // withdrawService.updateTxn(from, to, value, blockNumber.toString(), txnHash);

        // add to unconfirmed list
        schedule.addPendingTxn(txnHash, blockNumber);
        System.out.println("Pending: " + txnHash);
    }

    public String getTotalSupply() throws ExecutionException, InterruptedException {
        BigInteger total =  ndbToken.totalSupply().sendAsync().get();
        return total.divide(decimals).toString();
    }

    public String getCirculatingSupply() throws ExecutionException, InterruptedException {
        BigInteger total =  ndbToken.getCirculatingSupply().sendAsync().get();
        return total.divide(decimals).toString();
    }

    public NDBCoinService() {

        
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
