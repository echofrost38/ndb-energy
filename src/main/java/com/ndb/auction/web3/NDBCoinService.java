package com.ndb.auction.web3;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import com.ndb.auction.config.Web3jConfig;
import com.ndb.auction.contracts.NDBreferral;
import com.ndb.auction.contracts.NDBcoinV4;
import com.ndb.auction.dao.oracle.balance.CryptoBalanceDao;
import com.ndb.auction.dao.oracle.wallet.NyyuDepositDao;
import com.ndb.auction.dao.oracle.wallet.NyyuWalletDao;
import com.ndb.auction.exceptions.ReferralException;
import com.ndb.auction.models.balance.CryptoBalance;
import com.ndb.auction.models.wallet.NyyuDeposit;
import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.schedule.ScheduledTasks;

import com.ndb.auction.service.TokenAssetService;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.FastRawTransactionManager;
import java.text.DecimalFormat;

@Service
@Slf4j
public class NDBCoinService {

    @Value("${ndb.private.key}")
    private String ndbKey;

    @Value("${bsc.json.rpc}")
    private String bscNetwork;

    @Value("${bsc.json.chainid}")
    private long bscChainId;

    @Value("${ndb.token.addr}")
    private String ndbTokenContract;
    @Value("${ndb.referral.addr}")
    private String ndbReferralContract;

    @Value("${pancakev2.rpc}")
    private String pancakev2RPC;

    @Autowired
    private ScheduledTasks schedule;

    @Autowired
    private NyyuWalletDao nyyuWalletDao;

    @Autowired
    private CryptoBalanceDao balanceDao;

    @Autowired
    private NyyuDepositDao nyyuDepositDao;

    @Autowired
    public TokenAssetService tokenAssetService;

    private Credentials ndbCredential;
    private NDBcoinV4 ndbToken;
    private NDBreferral ndbReferral;
    private FastRawTransactionManager txMananger;

    private Web3j BEP20NET = Web3j.build(new HttpService(bscNetwork));

    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("800000");
    private final BigInteger decimals = new BigInteger("1000000000000");
    private final BigInteger m_decimals = new BigInteger("100000000");
    private final String ZERO="0x0000000000000000000000000000000000000000";
    private final double multipler = 10000.0;

    private static final DecimalFormat df = new DecimalFormat("0.00");
    
    @SuppressWarnings("deprecation")
    @PostConstruct
    public void init()  {
        try {
            Web3jConfig web3jConfig = new Web3jConfig();
            BEP20NET = Web3j.build(web3jConfig.buildService(bscNetwork));
            //  Web3j web3j = Web3j.build(new HttpService(bscNetwork));
            ndbCredential = Credentials.create(ndbKey);
            txMananger = new FastRawTransactionManager(BEP20NET, ndbCredential, bscChainId);
            ndbToken = NDBcoinV4.load(ndbTokenContract, BEP20NET, txMananger, gasPrice, gasLimit);
            ndbReferral = NDBreferral.load(ndbReferralContract, BEP20NET, txMananger, gasPrice, gasLimit);
            ndbToken.transferEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
                    .subscribe(event -> {
                        handleEvent(event);
                    }, error -> {
                        System.out.println("Error: " + error);
                    });
        } catch (Exception ex){
            System.out.println("INIT WEB3 : " + ex.getMessage());
        }
    }

    public double getBalanceOf(String wallet){
        try{
            BigInteger balance = ndbToken.balanceOf(wallet).send();
            return balance.divide(decimals).doubleValue();
        } catch (Exception e){
            System.out.println("getBalance : " + e.getMessage());
            return 0;
        }
    }

    private void handleEvent(NDBcoinV4.TransferEventResponse event) throws IOException {
        // create new withdraw transaction record
        BigInteger blockNumber = event.log.getBlockNumber();
        String txnHash = event.log.getTransactionHash();
        Double amount = event.value.divide(decimals).doubleValue();
        // withdrawService.updateTxn(from, to, value, blockNumber.toString(), txnHash);
        NyyuWallet nyyuWallet= nyyuWalletDao.selectByAddress(event.to);
        if (nyyuWallet!=null){
            NyyuDeposit deposit = new NyyuDeposit();
            deposit.setUserId(nyyuWallet.getUserId());
            deposit.setTxnHash(event.log.getTransactionHash());
            deposit.setAmount(amount);
            deposit.setWalletAddress(event.to);
            nyyuDepositDao.insert(deposit);
            // Sync NDB balance between Nyyu wallet and NDB internal balance
            int tokenId = tokenAssetService.getTokenIdBySymbol("NDB");
            CryptoBalance internalBalance = balanceDao.selectById(nyyuWallet.getUserId(),tokenId);
        }

        // add to unconfirmed list
        schedule.addPendingTxn(txnHash, blockNumber);
        System.out.println("Pending: " + txnHash);
    }

    public String activeReferrer(String address , Double rate){
        try {
            BigInteger _rate = BigInteger.valueOf(rate.longValue());
            TransactionReceipt receipt = ndbReferral.activeReferrer(address,_rate).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ReferralException(e.getMessage());
        }
    }

    public long getUserEarning(String address){
        try {
            long earning= ndbReferral.getEarning(address).send().divide(decimals).longValue();
            return earning;
        } catch (Exception e) {
            throw new ReferralException(e.getMessage());
        }
    }

    public String recordReferral(String user , String referrer){
        try {
            TransactionReceipt receipt = ndbReferral.recordReferral(user,referrer).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ReferralException(e.getMessage());
        }
    }

    public int lockingTimeRemain(String userAddress){
        try {
            int lockingTime= ndbReferral.lockingTimeRemain(userAddress).send().intValue();
            return lockingTime;
        } catch (Exception e) {
            throw new ReferralException(e.getMessage());
        }
    }

    public String updateReferrerRate(String referrer , Double rate){
        try {
            BigInteger _rate = BigInteger.valueOf(rate.longValue());
            TransactionReceipt receipt = ndbReferral.updateReferrerRate(referrer,_rate).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ReferralException(e.getMessage());
        }
    }

    public String updateReferrer(String old , String current){
        try {
            TransactionReceipt receipt = ndbReferral.updateReferrer(old,current).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new ReferralException(e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public boolean isActiveReferrer(String referrer) throws ExecutionException, InterruptedException {
        Tuple2<BigInteger, BigInteger> result = ndbReferral.referredUsers(referrer).sendAsync().get();
        if (result.getValue1().intValue()>0)
            return true;
        else
            return false;
    }

    public boolean isReferralRecorded(String userWallet,String referrerWallet) {
        try {
            String result = ndbReferral.referrers(userWallet).sendAsync().get();
            if (result.equals(referrerWallet)) return true;
            else return false;
        } catch (Exception e){
            throw new ReferralException(e.getMessage());
        }
    }

    public String getTotalSupply() throws ExecutionException, InterruptedException {
        BigInteger total =  ndbToken.totalSupply().sendAsync().get();
        return total.divide(decimals).toString();
    }

    public String getMarketCap() throws ExecutionException, InterruptedException, URISyntaxException, IOException {
        BigInteger total =  ndbToken.getCirculatingSupply().sendAsync().get();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .uri(URI.create(pancakev2RPC+ndbTokenContract))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        JSONObject json = new JSONObject(responseBody);
        Double price = Double.parseDouble(json.getJSONObject("data").getString("price"));
        if (price.equals(0.0))
            price=0.01; //ICO price
        Double marketcap = total.divide(decimals).doubleValue()*price;
        return df.format(marketcap).toString();
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
            //Web3j web3j = Web3j.build(new HttpService(bscNetwork));
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

    public String transferNDB(int userId, String address, Double amount) {
        try {

            Web3j web3j = Web3j.build(new HttpService(bscNetwork));
            ndbCredential = Credentials.create(ndbKey);
            @SuppressWarnings("deprecation")
            ERC20 ndbToken = ERC20.load(ndbTokenContract, web3j, ndbCredential, gasPrice, gasLimit);
            
            // avoid decimals
            amount *= multipler;
            BigInteger _amount = BigInteger.valueOf(amount.longValue());
            _amount = _amount.multiply(m_decimals);

            // create
            TransactionReceipt receipt = ndbToken.transfer(address, _amount).send();

            log.info("receipt hash: {}", receipt.getTransactionHash());
            log.info("receipt status: {}", receipt.getStatus());

            return receipt.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
