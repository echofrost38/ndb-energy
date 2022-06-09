package com.ndb.auction.web3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.ndb.auction.dao.oracle.withdraw.TokenDao;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.withdraw.Token;
import com.ndb.auction.payload.NetworkMetadata;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import lombok.extern.slf4j.Slf4j;

/**
 * This service will handle all token withdrawals except NDB 
 * (A) Supported Network
 * 0. BTC 
 * 1. ETH  https://bsc-dataseed.binance.org/
 * 2. BSC  https://mainnet.infura.io/v3/03021c5a727a40eb8d086a4d46d32ec7
 * 3. TRC
 * 4. SOL
 * 
 * (B) Supported Crypto
 * 1. ETH
 *  1) BSC: 0x2170ed0880ac9a755fd29b2688956bd959f933f8
 * 2. BNB
 *  1) ETH: 0xB8c77482e45F1F44dE1745F52C74426C631bDD52
 * 3. USDC
 *  1) ETH: 0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48
 *  2) BSC: 0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d
 * 4. USDT
 *  1) ETH: 0xdac17f958d2ee523a2206206994597c13d831ec7
 *  2) BSC: 0x55d398326f99059ff775485246999027b3197955
 * 5. DAI
 *  1) ETH: 0x6b175474e89094c44da98b954eedeac495271d0f
 *  2) BSC: 0x1af3f329e8be154074d8769d1ffa4ee058b1dbc3
 * 6. DOGE
 *  1) BSC: 0xba2ae424d960c26247dd6c32edc70b295c744c43
 * 7. SHIB
 *  1) ETH: 0x95ad61b0a150d79219dcf64e1e6cc01f0b64c4ce
 *  2) BSC: 0x2859e4544c4bb03966803b044a93563bd2d0dd4d
 * 8. BUSD 
 *  1) ETH: 0x4Fabb145d64652a948d72533023f6E7A623C7C53
 *  2) BSC: 0xe9e7cea3dedca5984780bafc599bd69add087d56
 */
@Service
@Slf4j
public class WithdrawWalletService {

    @Value("${wallet.seed}")
    private String SEED_PHRASE;
    
    // JSON RPC
    @Value("${bsc.json.rpc}")
    private String BSC_JSON_RPC;;
    
    @Value("${eth.json.rpc}")
    private String ETH_JSON_RPC;

    private Map<String, NetworkMetadata> networkMetadataMap;

    // web3 instances
    private final BigInteger gasPrice = new BigInteger("10000000000");
    private final BigInteger gasLimit = new BigInteger("800000");
    
    // token address for each network
    private List<Token> tokenList;

    // singleton
    @Autowired
    private TokenDao tokenDao;
    
    @Autowired
    private MessageSource messageSource;

    private synchronized void fillTokenMap() {
        tokenList = tokenDao.selectAll();
    }

    public static <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }

    @PostConstruct
    public void init() {
        
        var networkList = new ArrayList<NetworkMetadata>();
        networkList.add(NetworkMetadata.builder()
            .network("ERC20")
            .jsonRpc(ETH_JSON_RPC)
            .build());
        networkList.add(NetworkMetadata.builder()
            .network("BEP20")
            .jsonRpc(BSC_JSON_RPC)
            .build());
        networkMetadataMap = networkList.stream()
            .collect(Collectors.toMap(NetworkMetadata::getNetwork, Function.identity()));
    }

    // getting balance 
    public double getBalance(String network, String tokenSymbol) {
        try {
            // bitcoin processing 
            if(network.equals("BTC") && tokenSymbol.equals("BTC")) {
                NetworkParameters params = MainNetParams.get();
                var seed = new DeterministicSeed(SEED_PHRASE, null, "", 1409478661L);
                Wallet wallet = Wallet.fromSeed(params, seed, Script.ScriptType.P2WPKH);
                var addr = wallet.currentReceiveAddress();
                log.info(addr.toString());
                log.info(wallet.getBalance().toPlainString());
                return 0.0;
            }

            if(tokenList == null || tokenList.size() == 0) fillTokenMap();
            var netMetadata = networkMetadataMap.get(network);
            if(netMetadata == null) {
                String msg = messageSource.getMessage("no_network", null, Locale.ENGLISH);
                throw new UserNotFoundException(msg, "network");
            }
            Web3j web3 = Web3j.build(new HttpService(netMetadata.getJsonRpc())); 
            
            int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0,0};
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(SEED_PHRASE, null));
            Bip32ECKeyPair  derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);
            Credentials credentials = Credentials.create(derivedKeyPair);
            credentials.getAddress();
            
            // get balance
            if(network.equals("ERC20") && tokenSymbol.equals("ETH")) {
                EthGetBalance ethBalance = web3
                    .ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
                var weiBalance = ethBalance.getBalance();
                return Convert.fromWei(weiBalance.toString(), Unit.ETHER).doubleValue();
            } else if (network.equals("BEP20") && tokenSymbol.equals("BNB")) {
                EthGetBalance ethBalance = web3
                    .ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
                var weiBalance = ethBalance.getBalance();
                return Convert.fromWei(weiBalance.toString(), Unit.ETHER).doubleValue();
            } else {
                var tokenMetadata = tokenList.stream()
                    .filter(token -> token.getNetwork().equals(network) && token.getTokenSymbol().equals(tokenSymbol))
                    .collect(toSingleton());
                @SuppressWarnings("deprecation")
                ERC20 erc20 = ERC20.load(
                    tokenMetadata.getAddress(), 
                    web3, 
                    credentials, 
                    gasPrice, 
                    gasLimit
                );
                var balance = erc20.balanceOf(credentials.getAddress()).sendAsync().get();
                return Convert.fromWei(balance.toString(), Unit.ETHER).doubleValue();
            }
        } catch (Exception e) {
            String msg = messageSource.getMessage("unknown", null, Locale.ENGLISH);
            throw new UserNotFoundException(msg, "network");
        }
    }

    // transfer
    public String withdrawToken(String network, String tokenSymbol, String address, double amount) {
        try {
            if(tokenList == null || tokenList.size() == 0) fillTokenMap();
            var netMetadata = networkMetadataMap.get(network);
            if(netMetadata == null) {
                String msg = messageSource.getMessage("no_network", null, Locale.ENGLISH);
                throw new UserNotFoundException(msg, "network");
            }
            Web3j web3 = Web3j.build(new HttpService(netMetadata.getJsonRpc())); 

            int[] derivationPath = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0,0};
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(SEED_PHRASE, null));
            Bip32ECKeyPair  derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath);
            Credentials credentials = Credentials.create(derivedKeyPair);

            if(!network.equals("ERC20") || !network.equals("BEP20")) {
                return "Failed";
            }

            if(network.equals("ERC20") && tokenSymbol.equals("ETH")) {
                var bAmount = Convert.toWei(String.valueOf(amount), Convert.Unit.ETHER);
                TransactionReceipt txnReceipt = Transfer.sendFunds(
                    web3, credentials, address, bAmount, Convert.Unit.WEI).sendAsync().get();
                return txnReceipt.getTransactionHash();
            } else if (network.equals("BEP20") && tokenSymbol.equals("BNB")) {
                var bAmount = Convert.toWei(String.valueOf(amount), Convert.Unit.ETHER);
                TransactionReceipt txnReceipt = Transfer.sendFunds(
                    web3, credentials, address, bAmount, Convert.Unit.WEI).sendAsync().get();
                return txnReceipt.getTransactionHash();
            } else {
                var bAmount = Convert.toWei(String.valueOf(amount), Convert.Unit.ETHER);
                Token tokenMetadata = tokenList.stream()
                    .filter(token -> token.getNetwork().equals(network) && token.getTokenSymbol().equals(tokenSymbol))
                    .collect(toSingleton());
                @SuppressWarnings("deprecation")
                ERC20 erc20 = ERC20.load(
                    tokenMetadata.getAddress(), 
                    web3, 
                    credentials, 
                    gasPrice, 
                    gasLimit
                );
                TransactionReceipt receipt = erc20.transfer(address, bAmount.toBigInteger()).send();
                return receipt.getTransactionHash();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }
}
