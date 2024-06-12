package com.ndb.auction.web3;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import com.ndb.auction.config.Web3jConfig;
import com.ndb.auction.contracts.NDBcoinV3;
import com.ndb.auction.contracts.NDBreferral;
import com.ndb.auction.schedule.ScheduledTasks;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import java.text.DecimalFormat;
@Service
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

    @Value("${ndb.referral.treasury}")
    private String referralTreasury;

    @Value("${pancakev2.rpc}")
    private String pancakev2RPC;

    @Autowired
    private ScheduledTasks schedule;

    private Credentials ndbCredential;
    private NDBcoinV3 ndbToken;
    private NDBreferral ndbReferral;
    private FastRawTransactionManager txMananger;

    private Web3j BEP20NET = Web3j.build(new HttpService(bscNetwork));

    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("600000");
    private final BigInteger decimals = new BigInteger("1000000000000");
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @PostConstruct
    public void init() throws IOException {
        try {
           // BEP20NET = Web3j.build(new HttpService(bscNetwork));
            Web3jConfig web3jConfig = new Web3jConfig();
            BEP20NET = Web3j.build(web3jConfig.buildService(bscNetwork));
            //  Web3j web3j = Web3j.build(new HttpService(bscNetwork));
            ndbCredential = Credentials.create(ndbKey);
            txMananger = new FastRawTransactionManager(BEP20NET, ndbCredential, bscChainId);
            ndbToken = NDBcoinV3.load(ndbTokenContract, BEP20NET, txMananger, gasPrice, gasLimit);
            ndbReferral = NDBreferral.load(ndbReferralContract, BEP20NET, txMananger,gasPrice,gasLimit);
            ndbToken.transferEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
                    .subscribe(event -> {
                        handleEvent(event);
                    }, error -> {
                        System.out.println("Error: " + error);
                    });
            // ndbReferral.activeReferrerEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            //         .subscribe(event -> {
            //             handleActiveReferrer(event);
            //         }, error -> {
            //             System.out.println("Error: " + error);
            //         });
            // ndbReferral.referralRecordedEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            //         .subscribe(event -> {
            //             handleRecordReferrer(event);
            //         }, error -> {
            //             System.out.println("Error: " + error);
            //         });
            // ndbReferral.referralCommissionRecordedEventFlowable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST)
            //         .subscribe(event -> {
            //             handlereferralCommissionRecorded(event);
            //         }, error -> {
            //             System.out.println("Error: " + error);
            //         });
        }catch(Exception ex){
            String m="1";
        }


    }



    private void handlereferralCommissionRecorded(NDBreferral.ReferralCommissionRecordedEventResponse event) {
        System.out.println("referralCommissionRecorded : "+event.referrer +" commission: " + event.commission);
    }

    private void handleRecordReferrer(NDBreferral.ReferralRecordedEventResponse event) {
        System.out.println("RecordReferrer user: "+event.user +" referrer: " + event.referrer);
    }

    private void handleActiveReferrer(NDBreferral.ActiveReferrerEventResponse event) {
        System.out.println("ActiveReferrer "+event.referrer +" Status: " + event.status);
    }

    private void handleEvent(NDBcoinV3.TransferEventResponse event) throws IOException {
        // create new withdraw transaction record
        String from = event.from;
        String to = event.to;
        long lvalue = event.value.divide(decimals).longValue();
        Double value = ((double)lvalue) / 10000.0;
        BigInteger blockNumber = event.log.getBlockNumber();
        String txnHash = event.log.getTransactionHash();
        // withdrawService.updateTxn(from, to, value, blockNumber.toString(), txnHash);

        // add to unconfirmed list
        schedule.addPendingTxn(txnHash, event);

        System.out.println("Pending: " + txnHash);
    }

    public boolean isAvailableReferral(String wallet) throws ExecutionException, InterruptedException {
        BigInteger value = ndbToken.balanceOf(wallet).sendAsync().get();
        if (value.signum()==1)
            return true;
        else
            return false ;
    }

    public String payCommission(String _referrer,long _commission) throws Exception {
        BigInteger commission = BigInteger.valueOf(_commission).multiply(decimals);
        String transferResponse = ndbToken.transferFrom(referralTreasury,_referrer,commission).send().getTransactionHash();
        return transferResponse;
    }

    public String activeReferrer(String _address,String _referralCode) throws Exception {
        String transactionResponse = ndbReferral.activeReferrer(_address,_referralCode ).send().getTransactionHash();
        return transactionResponse;
    }

    public String getReferrerActive(String _address) throws ExecutionException, InterruptedException {
        String referrerActive = ndbReferral.referrersActive(_address).sendAsync().get();
        return referrerActive;
    }

    public String getReferrerWalletByActiveCode(String _code) throws ExecutionException, InterruptedException {
        String referrerActive = ndbReferral.referrersCodeActive(_code).sendAsync().get();
        return referrerActive;
    }

    public String getReferrerByUserWallet(String _address) throws ExecutionException, InterruptedException {
        String referrerAddress = ndbReferral.referrers(_address).sendAsync().get();
        return referrerAddress;
    }

    public String recordReferral(String _user,String _referrer) throws Exception {
        String transactionResponse = ndbReferral.recordReferral(_user,_referrer).send().getTransactionHash();
        return transactionResponse;
    }

    public String recordReferralCommission(String _referrer,long _commission) throws Exception {
        BigInteger commission = BigInteger.valueOf(_commission);
        String transactionResponse = ndbReferral.recordReferralCommission(_referrer,commission).send().getTransactionHash();
        return transactionResponse;
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
        int responseStatusCode = response.statusCode();
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

    public void checkReferral(String hash) throws IOException {
        Optional<TransactionReceipt> transactionReceipt =
                BEP20NET.ethGetTransactionReceipt(hash).send().getTransactionReceipt();
        int m =1 ;
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
            @SuppressWarnings("deprecation")
            ERC20 ndbToken = ERC20.load(ndbTokenContract, web3j, ndbCredential, gasPrice, gasLimit);
            
            BigInteger _amount = BigInteger.valueOf(amount.longValue());
            _amount = _amount.multiply(decimals);

            // create
            TransactionReceipt receipt = ndbToken.transfer(address, _amount).send();
            return receipt.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
