package com.ndb.auction.contracts;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class NDBReferral extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b5061187d806100206000396000f3fe608060405234801561001057600080fd5b50600436106101425760003560e01c806379547628116100b85780638da5cb5b1161007c5780638da5cb5b146103655780639691b12d14610376578063a2f0822414610389578063b3162faa1461039c578063c12a50c6146103af578063daa80bb3146103f557600080fd5b8063795476281461031c57806379ba50971461032f5780637c24543d146103375780638129fc1c1461034a5780638b3f577f1461035257600080fd5b806352c043c11161010a57806352c043c1146102825780635bf11d4a14610295578063602b386e146102b55780636d44a3b2146102d55780636d70f7ae146102e8578063715018a61461031457600080fd5b80630c7f7b6b1461014757806313e7c9d81461015c5780631b4a3ebe146101945780634a3b68cc146101ce5780634a9fefc71461020f575b600080fd5b61015a6101553660046114f7565b61041e565b005b61017f61016a366004611530565b60026020526000908152604090205460ff1681565b60405190151581526020015b60405180910390f35b6101c06101a2366004611530565b6001600160a01b031660009081526005602052604090205460ff1690565b60405190815260200161018b565b6101f76101dc366004611530565b6003602052600090815260409020546001600160a01b031681565b6040516001600160a01b03909116815260200161018b565b61025a61021d366004611530565b6001600160a01b0380821660009081526003602090815260408083205490931680835260059091529190205460065460ff91821691169193909250565b604080516001600160a01b039094168452602084019290925260ff169082015260600161018b565b61015a610290366004611530565b610689565b6101c06102a3366004611530565b60046020526000908152604090205481565b6102c86102c3366004611530565b6106fd565b60405161018b919061154d565b61015a6102e33660046115a8565b610776565b61017f6102f6366004611530565b6001600160a01b031660009081526002602052604090205460ff1690565b61015a6107f4565b61015a61032a3660046115d6565b610874565b61015a6108f8565b61015a61034536600461162e565b6109ed565b61015a610ae5565b61015a6103603660046114f7565b610b36565b6000546001600160a01b03166101f7565b61015a610384366004611663565b610ed0565b61015a61039736600461162e565b610fe9565b61015a6103aa3660046116a4565b6110df565b6103dc6103bd366004611530565b6005602052600090815260409020805460019091015460ff9091169082565b6040805160ff909316835260208301919091520161018b565b6101c0610403366004611530565b6001600160a01b031660009081526004602052604090205490565b3360009081526002602052604090205460ff166104565760405162461bcd60e51b815260040161044d906116bf565b60405180910390fd5b6001600160a01b0382166104ac5760405162461bcd60e51b815260206004820152601960248201527f5f7573657220697320746865207a65726f206164647265737300000000000000604482015260640161044d565b6001600160a01b0381166104d25760405162461bcd60e51b815260040161044d90611703565b806001600160a01b0316826001600160a01b0316036105335760405162461bcd60e51b815260206004820152601b60248201527f5f7573657220697320657175616c20746f205f72656665727265720000000000604482015260640161044d565b6001600160a01b03828116600090815260036020526040902054161561059b5760405162461bcd60e51b815260206004820152601f60248201527f5f757365722068617320616c7265616479206265656e20726566657272656400604482015260640161044d565b6001600160a01b03811660009081526005602052604090205460ff166106035760405162461bcd60e51b815260206004820152601760248201527f5265666572726572206e6f742061637469766520796574000000000000000000604482015260640161044d565b6001600160a01b03808216600081815260056020908152604080832060020180546001810182559084528284200180549588166001600160a01b0319968716811790915580845260039092528083208054909516841790945592519192917ff61ccbe316daff56654abed758191f9a4dcac526d43747a50a2d545c0ca64d829190a35050565b6000546001600160a01b031633146106b35760405162461bcd60e51b815260040161044d9061173a565b600180546001600160a01b0319166001600160a01b0383169081179091556040517f906a1c6bd7e3091ea86693dd029a831c19049ce77f1dce2ce0bab1cacbabce2290600090a250565b6001600160a01b03811660009081526005602090815260409182902060020180548351818402810184019094528084526060939283018282801561076a57602002820191906000526020600020905b81546001600160a01b0316815260019091019060200180831161074c575b50505050509050919050565b6000546001600160a01b031633146107a05760405162461bcd60e51b815260040161044d9061173a565b6001600160a01b038216600081815260026020526040808220805460ff191685151590811790915590519092917f966c160e1c4dbc7df8d69af4ace01e9297c3cf016397b7914971f2fbfa32672d91a35050565b6000546001600160a01b0316331461081e5760405162461bcd60e51b815260040161044d9061173a565b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319908116909155600180549091169055565b6000546001600160a01b0316331461089e5760405162461bcd60e51b815260040161044d9061173a565b6108b26001600160a01b0384168284611151565b604080518381526001600160a01b03831660208201527f605022943465a74fc0f43f0f842fd8163e45145ade2fea43728ab758e317ba6d910160405180910390a1505050565b6001546001600160a01b031633146109895760405162461bcd60e51b815260206004820152604860248201527f596f75206d757374206265206e6f6d696e6174656420617320706f74656e746960448201527f616c206f776e6572206265666f726520796f752063616e20616363657074206f6064820152670776e6572736869760c41b608482015260a40161044d565b600154600080546040516001600160a01b0393841693909116917fb532073b38c83145e3e5135377a08bf9aab55bc0fd7c1179cd4fb995d2a5159c91a360018054600080546001600160a01b03199081166001600160a01b03841617909155169055565b3360009081526002602052604090205460ff16610a1c5760405162461bcd60e51b815260040161044d906116bf565b6001600160a01b038216610a425760405162461bcd60e51b815260040161044d90611703565b60008160ff1611610a845760405162461bcd60e51b815260206004820152600c60248201526b52617465206973207a65726f60a01b604482015260640161044d565b6001600160a01b038216600081815260056020908152604091829020805460ff191660ff861690811790915591519182527f719a65f5451c353953daae53c6a31ed24315a4f03b27b41f346172c7f125afca91015b60405180910390a25050565b60008054336001600160a01b0319909116811782556006805460ff1916600a179055604051909182917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a350565b3360009081526002602052604090205460ff16610b655760405162461bcd60e51b815260040161044d906116bf565b806001600160a01b0316826001600160a01b031603610bc65760405162461bcd60e51b815260206004820152601960248201527f5f6f6c6420697320657175616c20746f205f63757272656e7400000000000000604482015260640161044d565b6001600160a01b038216610c1c5760405162461bcd60e51b815260206004820181905260248201527f6e657720726566657272657220697320746865207a65726f2061646472657373604482015260640161044d565b6001600160a01b038116610c7e5760405162461bcd60e51b8152602060048201526024808201527f63757272656e7420726566657272657220697320746865207a65726f206164646044820152637265737360e01b606482015260840161044d565b6001600160a01b03811660009081526005602052604090205460ff1615610ce75760405162461bcd60e51b815260206004820152601860248201527f5f63757272656e74207265666572726572206578697374730000000000000000604482015260640161044d565b6001600160a01b03808316600090815260056020526040808220928416825290208154815460ff191660ff9091161781556001808301549082015560028083018054610d36928401919061145c565b5050506001600160a01b03811660009081526005602090815260408083208151606081018352815460ff168152600182015481850152600282018054845181870281018701865281815292959394860193830182828015610dc057602002820191906000526020600020905b81546001600160a01b03168152600190910190602001808311610da2575b50505091909252505050604081015190915060005b8151811015610e52578360036000848481518110610df557610df561176f565b60200260200101516001600160a01b03166001600160a01b0316815260200190815260200160002060006101000a8154816001600160a01b0302191690836001600160a01b03160217905550610e4b8160010190565b9050610dd5565b506001600160a01b0384166000908152600560205260408120805460ff191681556001810182905590610e8860028301826114ac565b5050826001600160a01b0316846001600160a01b03167fc40302e3b5897f6966b131753cb09f65aa712ae82e3f49b189d089d5694256e360405160405180910390a350505050565b3360009081526002602052604090205460ff16610eff5760405162461bcd60e51b815260040161044d906116bf565b6001600160a01b03831615801590610f1f57506001600160a01b03821615155b8015610f2b5750600081115b15610fe4576001600160a01b038316600090815260056020526040902060010154610f5690826111a3565b6001600160a01b03808516600090815260056020908152604080832060010194909455918516815260049091522054610f8f90826111a3565b6001600160a01b038084166000908152600460209081526040918290209390935551838152908516917f91badd4ef769cf56b7db0b350b95c9fb6d973e6e37d28a51fed219cc7d53184f910160405180910390a25b505050565b3360009081526002602052604090205460ff166110185760405162461bcd60e51b815260040161044d906116bf565b6001600160a01b03821661103e5760405162461bcd60e51b815260040161044d90611703565b60008160ff16116110805760405162461bcd60e51b815260206004820152600c60248201526b52617465206973207a65726f60a01b604482015260640161044d565b6001600160a01b0382166000818152600560209081526040808320805460ff191660ff87169081178255600190910193909355519182527fb2a60f125657306a9287a8eafa2344a7fe2976d44c293ddbb26f032992c5549c9101610ad9565b6000546001600160a01b031633146111095760405162461bcd60e51b815260040161044d9061173a565b6006805460ff191660ff83169081179091556040519081527f161ac3d6084899bc7d75453abbf3ccc9a2977f8ffdc6d75a337d1251725bdb589060200160405180910390a150565b604080516001600160a01b038416602482015260448082018490528251808303909101815260649091019091526020810180516001600160e01b031663a9059cbb60e01b179052610fe4908490611209565b6000806111b08385611785565b9050838110156112025760405162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015260640161044d565b9392505050565b600061125e826040518060400160405280602081526020017f5361666542455032303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b03166112db9092919063ffffffff16565b805190915015610fe4578080602001905181019061127c91906117ab565b610fe45760405162461bcd60e51b815260206004820152602a60248201527f5361666542455032303a204245503230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b606482015260840161044d565b60606112ea84846000856112f2565b949350505050565b6060824710156113535760405162461bcd60e51b815260206004820152602660248201527f416464726573733a20696e73756666696369656e742062616c616e636520666f6044820152651c8818d85b1b60d21b606482015260840161044d565b6001600160a01b0385163b6113aa5760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e7472616374000000604482015260640161044d565b600080866001600160a01b031685876040516113c691906117f8565b60006040518083038185875af1925050503d8060008114611403576040519150601f19603f3d011682016040523d82523d6000602084013e611408565b606091505b5091509150611418828286611423565b979650505050505050565b60608315611432575081611202565b8251156114425782518084602001fd5b8160405162461bcd60e51b815260040161044d9190611814565b82805482825590600052602060002090810192821561149c5760005260206000209182015b8281111561149c578254825591600101919060010190611481565b506114a89291506114cd565b5090565b50805460008255906000526020600020908101906114ca91906114cd565b50565b5b808211156114a857600081556001016114ce565b6001600160a01b03811681146114ca57600080fd5b6000806040838503121561150a57600080fd5b8235611515816114e2565b91506020830135611525816114e2565b809150509250929050565b60006020828403121561154257600080fd5b8135611202816114e2565b6020808252825182820181905260009190848201906040850190845b8181101561158e5783516001600160a01b031683529284019291840191600101611569565b50909695505050505050565b80151581146114ca57600080fd5b600080604083850312156115bb57600080fd5b82356115c6816114e2565b915060208301356115258161159a565b6000806000606084860312156115eb57600080fd5b83356115f6816114e2565b925060208401359150604084013561160d816114e2565b809150509250925092565b803560ff8116811461162957600080fd5b919050565b6000806040838503121561164157600080fd5b823561164c816114e2565b915061165a60208401611618565b90509250929050565b60008060006060848603121561167857600080fd5b8335611683816114e2565b92506020840135611693816114e2565b929592945050506040919091013590565b6000602082840312156116b657600080fd5b61120282611618565b60208082526024908201527f4f70657261746f723a2063616c6c6572206973206e6f7420746865206f70657260408201526330ba37b960e11b606082015260800190565b6020808252601d908201527f5f726566657272657220697320746865207a65726f2061646472657373000000604082015260600190565b6020808252818101527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604082015260600190565b634e487b7160e01b600052603260045260246000fd5b600082198211156117a657634e487b7160e01b600052601160045260246000fd5b500190565b6000602082840312156117bd57600080fd5b81516112028161159a565b60005b838110156117e35781810151838201526020016117cb565b838111156117f2576000848401525b50505050565b6000825161180a8184602087016117c8565b9190910192915050565b60208152600082518060208401526118338160408501602087016117c8565b601f01601f1916919091016040019291505056fea2646970667358221220d7b7434db1b27cd8f2e2d2218dd3759b3f2eda5fb5507034e4b5874144ea8d5f64736f6c634300080d0033";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_ACTIVEREFERRER = "activeReferrer";

    public static final String FUNC_DRAINBEP20TOKEN = "drainBEP20Token";

    public static final String FUNC_GETEARNING = "getEarning";

    public static final String FUNC_GETREFERRER = "getReferrer";

    public static final String FUNC_GETREFERRERRATE = "getReferrerRate";

    public static final String FUNC_GETUSERS = "getUsers";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_ISOPERATOR = "isOperator";

    public static final String FUNC_NOMINATEPOTENTIALOWNER = "nominatePotentialOwner";

    public static final String FUNC_OPERATORS = "operators";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RECORDREFERRAL = "recordReferral";

    public static final String FUNC_RECORDREFERRALCOMMISSION = "recordReferralCommission";

    public static final String FUNC_REFERREDUSERS = "referredUsers";

    public static final String FUNC_REFERRERS = "referrers";

    public static final String FUNC_REFERRERSEARNING = "referrersEarning";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETJOINREFERRALCOMMISSIONRATE = "setJoinReferralCommissionRate";

    public static final String FUNC_UPDATEOPERATOR = "updateOperator";

    public static final String FUNC_UPDATEREFERRER = "updateReferrer";

    public static final String FUNC_UPDATEREFERRERRATE = "updateReferrerRate";

    public static final Event DRAINBEP20TOKEN_EVENT = new Event("DrainBEP20Token", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event OPERATORUPDATED_EVENT = new Event("OperatorUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bool>(true) {}));
    ;

    public static final Event OWNERCHANGED_EVENT = new Event("OwnerChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event OWNERNOMINATED_EVENT = new Event("OwnerNominated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event REFERRALCOMMISSIONRECORDED_EVENT = new Event("ReferralCommissionRecorded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event REFERRALRECORDED_EVENT = new Event("ReferralRecorded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event REFERRERACTIVE_EVENT = new Event("ReferrerActive", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event REFERRERRATEUPDATED_EVENT = new Event("ReferrerRateUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event REFERRERUPDATED_EVENT = new Event("ReferrerUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event SETJOINREFERRALCOMMISSIONRATE_EVENT = new Event("SetJoinReferralCommissionRate", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint16>() {}));
    ;

    @Deprecated
    protected NDBReferral(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NDBReferral(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected NDBReferral(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected NDBReferral(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<DrainBEP20TokenEventResponse> getDrainBEP20TokenEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DRAINBEP20TOKEN_EVENT, transactionReceipt);
        ArrayList<DrainBEP20TokenEventResponse> responses = new ArrayList<DrainBEP20TokenEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DrainBEP20TokenEventResponse typedResponse = new DrainBEP20TokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DrainBEP20TokenEventResponse> drainBEP20TokenEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DrainBEP20TokenEventResponse>() {
            @Override
            public DrainBEP20TokenEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DRAINBEP20TOKEN_EVENT, log);
                DrainBEP20TokenEventResponse typedResponse = new DrainBEP20TokenEventResponse();
                typedResponse.log = log;
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DrainBEP20TokenEventResponse> drainBEP20TokenEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DRAINBEP20TOKEN_EVENT));
        return drainBEP20TokenEventFlowable(filter);
    }

    public List<OperatorUpdatedEventResponse> getOperatorUpdatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OPERATORUPDATED_EVENT, transactionReceipt);
        ArrayList<OperatorUpdatedEventResponse> responses = new ArrayList<OperatorUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OperatorUpdatedEventResponse typedResponse = new OperatorUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.operator = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.status = (Boolean) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OperatorUpdatedEventResponse> operatorUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OperatorUpdatedEventResponse>() {
            @Override
            public OperatorUpdatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OPERATORUPDATED_EVENT, log);
                OperatorUpdatedEventResponse typedResponse = new OperatorUpdatedEventResponse();
                typedResponse.log = log;
                typedResponse.operator = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.status = (Boolean) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OperatorUpdatedEventResponse> operatorUpdatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OPERATORUPDATED_EVENT));
        return operatorUpdatedEventFlowable(filter);
    }

    public List<OwnerChangedEventResponse> getOwnerChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERCHANGED_EVENT, transactionReceipt);
        ArrayList<OwnerChangedEventResponse> responses = new ArrayList<OwnerChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnerChangedEventResponse typedResponse = new OwnerChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.prevOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnerChangedEventResponse> ownerChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnerChangedEventResponse>() {
            @Override
            public OwnerChangedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERCHANGED_EVENT, log);
                OwnerChangedEventResponse typedResponse = new OwnerChangedEventResponse();
                typedResponse.log = log;
                typedResponse.prevOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnerChangedEventResponse> ownerChangedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERCHANGED_EVENT));
        return ownerChangedEventFlowable(filter);
    }

    public List<OwnerNominatedEventResponse> getOwnerNominatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERNOMINATED_EVENT, transactionReceipt);
        ArrayList<OwnerNominatedEventResponse> responses = new ArrayList<OwnerNominatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnerNominatedEventResponse typedResponse = new OwnerNominatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnerNominatedEventResponse> ownerNominatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnerNominatedEventResponse>() {
            @Override
            public OwnerNominatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERNOMINATED_EVENT, log);
                OwnerNominatedEventResponse typedResponse = new OwnerNominatedEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnerNominatedEventResponse> ownerNominatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERNOMINATED_EVENT));
        return ownerNominatedEventFlowable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.prevOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.prevOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public List<ReferralCommissionRecordedEventResponse> getReferralCommissionRecordedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REFERRALCOMMISSIONRECORDED_EVENT, transactionReceipt);
        ArrayList<ReferralCommissionRecordedEventResponse> responses = new ArrayList<ReferralCommissionRecordedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReferralCommissionRecordedEventResponse typedResponse = new ReferralCommissionRecordedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.referrer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.commission = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReferralCommissionRecordedEventResponse> referralCommissionRecordedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ReferralCommissionRecordedEventResponse>() {
            @Override
            public ReferralCommissionRecordedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REFERRALCOMMISSIONRECORDED_EVENT, log);
                ReferralCommissionRecordedEventResponse typedResponse = new ReferralCommissionRecordedEventResponse();
                typedResponse.log = log;
                typedResponse.referrer = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.commission = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReferralCommissionRecordedEventResponse> referralCommissionRecordedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REFERRALCOMMISSIONRECORDED_EVENT));
        return referralCommissionRecordedEventFlowable(filter);
    }

    public List<ReferralRecordedEventResponse> getReferralRecordedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REFERRALRECORDED_EVENT, transactionReceipt);
        ArrayList<ReferralRecordedEventResponse> responses = new ArrayList<ReferralRecordedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReferralRecordedEventResponse typedResponse = new ReferralRecordedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.referrer = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReferralRecordedEventResponse> referralRecordedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ReferralRecordedEventResponse>() {
            @Override
            public ReferralRecordedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REFERRALRECORDED_EVENT, log);
                ReferralRecordedEventResponse typedResponse = new ReferralRecordedEventResponse();
                typedResponse.log = log;
                typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.referrer = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReferralRecordedEventResponse> referralRecordedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REFERRALRECORDED_EVENT));
        return referralRecordedEventFlowable(filter);
    }

    public List<ReferrerActiveEventResponse> getReferrerActiveEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REFERRERACTIVE_EVENT, transactionReceipt);
        ArrayList<ReferrerActiveEventResponse> responses = new ArrayList<ReferrerActiveEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReferrerActiveEventResponse typedResponse = new ReferrerActiveEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.referrer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.rate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReferrerActiveEventResponse> referrerActiveEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ReferrerActiveEventResponse>() {
            @Override
            public ReferrerActiveEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REFERRERACTIVE_EVENT, log);
                ReferrerActiveEventResponse typedResponse = new ReferrerActiveEventResponse();
                typedResponse.log = log;
                typedResponse.referrer = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.rate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReferrerActiveEventResponse> referrerActiveEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REFERRERACTIVE_EVENT));
        return referrerActiveEventFlowable(filter);
    }

    public List<ReferrerRateUpdatedEventResponse> getReferrerRateUpdatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REFERRERRATEUPDATED_EVENT, transactionReceipt);
        ArrayList<ReferrerRateUpdatedEventResponse> responses = new ArrayList<ReferrerRateUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReferrerRateUpdatedEventResponse typedResponse = new ReferrerRateUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.referrer = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.rate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReferrerRateUpdatedEventResponse> referrerRateUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ReferrerRateUpdatedEventResponse>() {
            @Override
            public ReferrerRateUpdatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REFERRERRATEUPDATED_EVENT, log);
                ReferrerRateUpdatedEventResponse typedResponse = new ReferrerRateUpdatedEventResponse();
                typedResponse.log = log;
                typedResponse.referrer = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.rate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReferrerRateUpdatedEventResponse> referrerRateUpdatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REFERRERRATEUPDATED_EVENT));
        return referrerRateUpdatedEventFlowable(filter);
    }

    public List<ReferrerUpdatedEventResponse> getReferrerUpdatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REFERRERUPDATED_EVENT, transactionReceipt);
        ArrayList<ReferrerUpdatedEventResponse> responses = new ArrayList<ReferrerUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReferrerUpdatedEventResponse typedResponse = new ReferrerUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.old = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.current = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReferrerUpdatedEventResponse> referrerUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ReferrerUpdatedEventResponse>() {
            @Override
            public ReferrerUpdatedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REFERRERUPDATED_EVENT, log);
                ReferrerUpdatedEventResponse typedResponse = new ReferrerUpdatedEventResponse();
                typedResponse.log = log;
                typedResponse.old = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.current = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReferrerUpdatedEventResponse> referrerUpdatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REFERRERUPDATED_EVENT));
        return referrerUpdatedEventFlowable(filter);
    }

    public List<SetJoinReferralCommissionRateEventResponse> getSetJoinReferralCommissionRateEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SETJOINREFERRALCOMMISSIONRATE_EVENT, transactionReceipt);
        ArrayList<SetJoinReferralCommissionRateEventResponse> responses = new ArrayList<SetJoinReferralCommissionRateEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetJoinReferralCommissionRateEventResponse typedResponse = new SetJoinReferralCommissionRateEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.joinCommissionRate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SetJoinReferralCommissionRateEventResponse> setJoinReferralCommissionRateEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SetJoinReferralCommissionRateEventResponse>() {
            @Override
            public SetJoinReferralCommissionRateEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SETJOINREFERRALCOMMISSIONRATE_EVENT, log);
                SetJoinReferralCommissionRateEventResponse typedResponse = new SetJoinReferralCommissionRateEventResponse();
                typedResponse.log = log;
                typedResponse.joinCommissionRate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SetJoinReferralCommissionRateEventResponse> setJoinReferralCommissionRateEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETJOINREFERRALCOMMISSIONRATE_EVENT));
        return setJoinReferralCommissionRateEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> acceptOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ACCEPTOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> activeReferrer(String _referrer, BigInteger _rate) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ACTIVEREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer), 
                new org.web3j.abi.datatypes.generated.Uint8(_rate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> drainBEP20Token(String _token, BigInteger _amount, String _to) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DRAINBEP20TOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _token), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount), 
                new org.web3j.abi.datatypes.Address(160, _to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getEarning(String _user) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETEARNING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple3<String, BigInteger, BigInteger>> getReferrer(String _user) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple3<String, BigInteger, BigInteger>>(function,
                new Callable<Tuple3<String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getReferrerRate(String _referrer) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETREFERRERRATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getUsers(String _referral) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUSERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referral)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> initialize() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> isOperator(String _wallet) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISOPERATOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _wallet)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> nominatePotentialOwner(String _cowner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_NOMINATEPOTENTIALOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _cowner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> operators(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OPERATORS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> recordReferral(String _user, String _referrer) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RECORDREFERRAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _user), 
                new org.web3j.abi.datatypes.Address(160, _referrer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> recordReferralCommission(String _referrer, String _user, BigInteger _commission) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RECORDREFERRALCOMMISSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer), 
                new org.web3j.abi.datatypes.Address(160, _user), 
                new org.web3j.abi.datatypes.generated.Uint256(_commission)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> referredUsers(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERREDUSERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> referrers(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERRERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> referrersEarning(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERRERSEARNING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setJoinReferralCommissionRate(BigInteger _rate) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETJOINREFERRALCOMMISSIONRATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(_rate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateOperator(String _operator, Boolean _status) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPDATEOPERATOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _operator), 
                new org.web3j.abi.datatypes.Bool(_status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateReferrer(String _old, String _current) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPDATEREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _old), 
                new org.web3j.abi.datatypes.Address(160, _current)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateReferrerRate(String _referrer, BigInteger _rate) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPDATEREFERRERRATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer), 
                new org.web3j.abi.datatypes.generated.Uint8(_rate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static NDBReferral load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NDBReferral(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static NDBReferral load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NDBReferral(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static NDBReferral load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new NDBReferral(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static NDBReferral load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new NDBReferral(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<NDBReferral> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NDBReferral.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NDBReferral> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NDBReferral.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<NDBReferral> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NDBReferral.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NDBReferral> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NDBReferral.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class DrainBEP20TokenEventResponse extends BaseEventResponse {
        public BigInteger amount;

        public String to;
    }

    public static class OperatorUpdatedEventResponse extends BaseEventResponse {
        public String operator;

        public Boolean status;
    }

    public static class OwnerChangedEventResponse extends BaseEventResponse {
        public String prevOwner;

        public String newOwner;
    }

    public static class OwnerNominatedEventResponse extends BaseEventResponse {
        public String owner;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String prevOwner;

        public String newOwner;
    }

    public static class ReferralCommissionRecordedEventResponse extends BaseEventResponse {
        public String referrer;

        public BigInteger commission;
    }

    public static class ReferralRecordedEventResponse extends BaseEventResponse {
        public String user;

        public String referrer;
    }

    public static class ReferrerActiveEventResponse extends BaseEventResponse {
        public String referrer;

        public BigInteger rate;
    }

    public static class ReferrerRateUpdatedEventResponse extends BaseEventResponse {
        public String referrer;

        public BigInteger rate;
    }

    public static class ReferrerUpdatedEventResponse extends BaseEventResponse {
        public String old;

        public String current;
    }

    public static class SetJoinReferralCommissionRateEventResponse extends BaseEventResponse {
        public BigInteger joinCommissionRate;
    }
}
