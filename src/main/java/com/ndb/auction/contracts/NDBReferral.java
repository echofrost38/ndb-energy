package com.ndb.auction.contracts;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
    public static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b031916339081178255604051909182917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a350610f7e806100616000396000f3fe608060405234801561001057600080fd5b50600436106100f55760003560e01c8063715018a61161009757806390cbd78d1161006657806390cbd78d1461023c5780639ecfc6ea1461024f578063dc1694b81461026f578063e21a92bc1461028257600080fd5b8063715018a614610208578063795476281461021057806379ba5097146102235780638da5cb5b1461022b57600080fd5b80634a9fefc7116100d35780634a9fefc71461018857806352c043c1146101b45780636b366c66146101c75780636d44a3b2146101f557600080fd5b80630c7f7b6b146100fa57806313e7c9d81461010f5780634a3b68cc14610147575b600080fd5b61010d610108366004610d0d565b6102a5565b005b61013261011d366004610d46565b60026020526000908152604090205460ff1681565b60405190151581526020015b60405180910390f35b610170610155366004610d46565b6003602052600090815260409020546001600160a01b031681565b6040516001600160a01b03909116815260200161013e565b610170610196366004610d46565b6001600160a01b039081166000908152600360205260409020541690565b61010d6101c2366004610d46565b610550565b6101e76101d5366004610d46565b60046020526000908152604090205481565b60405190815260200161013e565b61010d610203366004610d71565b6105c4565b61010d610642565b61010d61021e366004610d9f565b6106c2565b61010d610746565b6000546001600160a01b0316610170565b61010d61024a366004610d71565b61083b565b6101e761025d366004610d46565b60066020526000908152604090205481565b61010d61027d366004610de1565b610923565b610132610290366004610d46565b60056020526000908152604090205460ff1681565b3360009081526002602052604090205460ff166102dd5760405162461bcd60e51b81526004016102d490610e0d565b60405180910390fd5b6001600160a01b03811660009081526005602052604090205460ff16151560011461034a5760405162461bcd60e51b815260206004820152601960248201527f5f7265666572726572206e6f7420796574206163746976652e0000000000000060448201526064016102d4565b6001600160a01b0382166103a05760405162461bcd60e51b815260206004820152601960248201527f5f7573657220697320746865207a65726f20616464726573730000000000000060448201526064016102d4565b6001600160a01b0381166103f65760405162461bcd60e51b815260206004820152601d60248201527f5f726566657272657220697320746865207a65726f206164647265737300000060448201526064016102d4565b806001600160a01b0316826001600160a01b0316036104575760405162461bcd60e51b815260206004820152601b60248201527f5f7573657220697320657175616c20746f205f7265666572726572000000000060448201526064016102d4565b6001600160a01b0382811660009081526003602052604090205416156104bf5760405162461bcd60e51b815260206004820152601f60248201527f5f757365722068617320616c7265616479206265656e2072656665727265640060448201526064016102d4565b6001600160a01b03828116600090815260036020908152604080832080546001600160a01b03191694861694851790559282526004905220546105039060016109ee565b6001600160a01b038083166000818152600460205260408082209490945592519092918516917ff61ccbe316daff56654abed758191f9a4dcac526d43747a50a2d545c0ca64d8291a35050565b6000546001600160a01b0316331461057a5760405162461bcd60e51b81526004016102d490610e51565b600180546001600160a01b0319166001600160a01b0383169081179091556040517f906a1c6bd7e3091ea86693dd029a831c19049ce77f1dce2ce0bab1cacbabce2290600090a250565b6000546001600160a01b031633146105ee5760405162461bcd60e51b81526004016102d490610e51565b6001600160a01b038216600081815260026020526040808220805460ff191685151590811790915590519092917f966c160e1c4dbc7df8d69af4ace01e9297c3cf016397b7914971f2fbfa32672d91a35050565b6000546001600160a01b0316331461066c5760405162461bcd60e51b81526004016102d490610e51565b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319908116909155600180549091169055565b6000546001600160a01b031633146106ec5760405162461bcd60e51b81526004016102d490610e51565b6107006001600160a01b0384168284610a54565b604080518381526001600160a01b03831660208201527f605022943465a74fc0f43f0f842fd8163e45145ade2fea43728ab758e317ba6d910160405180910390a1505050565b6001546001600160a01b031633146107d75760405162461bcd60e51b815260206004820152604860248201527f596f75206d757374206265206e6f6d696e6174656420617320706f74656e746960448201527f616c206f776e6572206265666f726520796f752063616e20616363657074206f6064820152670776e6572736869760c41b608482015260a4016102d4565b600154600080546040516001600160a01b0393841693909116917fb532073b38c83145e3e5135377a08bf9aab55bc0fd7c1179cd4fb995d2a5159c91a360018054600080546001600160a01b03199081166001600160a01b03841617909155169055565b3360009081526002602052604090205460ff1661086a5760405162461bcd60e51b81526004016102d490610e0d565b6001600160a01b0382166108c05760405162461bcd60e51b815260206004820152601d60248201527f5f726566657272657220697320746865207a65726f206164647265737300000060448201526064016102d4565b6001600160a01b038216600081815260056020908152604091829020805460ff19168515159081179091558251938452908301527feeac18f4442c3ada07dc695cf4a1d0bbf0ba9a9d71d22673a9c8aa163acf89d0910160405180910390a15050565b3360009081526002602052604090205460ff166109525760405162461bcd60e51b81526004016102d490610e0d565b6001600160a01b0382161580159061096a5750600081115b156109ea576001600160a01b03821660009081526006602052604090205461099290826109ee565b6001600160a01b038316600081815260066020526040908190209290925590517f91badd4ef769cf56b7db0b350b95c9fb6d973e6e37d28a51fed219cc7d53184f906109e19084815260200190565b60405180910390a25b5050565b6000806109fb8385610e86565b905083811015610a4d5760405162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f77000000000060448201526064016102d4565b9392505050565b604080516001600160a01b038416602482015260448082018490528251808303909101815260649091019091526020810180516001600160e01b031663a9059cbb60e01b179052610aa6908490610aab565b505050565b6000610b00826040518060400160405280602081526020017f5361666542455032303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b0316610b7d9092919063ffffffff16565b805190915015610aa65780806020019051810190610b1e9190610eac565b610aa65760405162461bcd60e51b815260206004820152602a60248201527f5361666542455032303a204245503230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b60648201526084016102d4565b6060610b8c8484600085610b94565b949350505050565b606082471015610bf55760405162461bcd60e51b815260206004820152602660248201527f416464726573733a20696e73756666696369656e742062616c616e636520666f6044820152651c8818d85b1b60d21b60648201526084016102d4565b843b610c435760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e747261637400000060448201526064016102d4565b600080866001600160a01b03168587604051610c5f9190610ef9565b60006040518083038185875af1925050503d8060008114610c9c576040519150601f19603f3d011682016040523d82523d6000602084013e610ca1565b606091505b5091509150610cb1828286610cbc565b979650505050505050565b60608315610ccb575081610a4d565b825115610cdb5782518084602001fd5b8160405162461bcd60e51b81526004016102d49190610f15565b6001600160a01b0381168114610d0a57600080fd5b50565b60008060408385031215610d2057600080fd5b8235610d2b81610cf5565b91506020830135610d3b81610cf5565b809150509250929050565b600060208284031215610d5857600080fd5b8135610a4d81610cf5565b8015158114610d0a57600080fd5b60008060408385031215610d8457600080fd5b8235610d8f81610cf5565b91506020830135610d3b81610d63565b600080600060608486031215610db457600080fd5b8335610dbf81610cf5565b9250602084013591506040840135610dd681610cf5565b809150509250925092565b60008060408385031215610df457600080fd5b8235610dff81610cf5565b946020939093013593505050565b60208082526024908201527f4f70657261746f723a2063616c6c6572206973206e6f7420746865206f70657260408201526330ba37b960e11b606082015260800190565b6020808252818101527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604082015260600190565b60008219821115610ea757634e487b7160e01b600052601160045260246000fd5b500190565b600060208284031215610ebe57600080fd5b8151610a4d81610d63565b60005b83811015610ee4578181015183820152602001610ecc565b83811115610ef3576000848401525b50505050565b60008251610f0b818460208701610ec9565b9190910192915050565b6020815260008251806020840152610f34816040850160208701610ec9565b601f01601f1916919091016040019291505056fea2646970667358221220b79e782e2e1d4f5fa0040416e7175912bc6ae77bc12a2ee03cf13c302dae597a64736f6c634300080d0033";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_ACTIVEREFERRER = "activeReferrer";

    public static final String FUNC_DRAINBEP20TOKEN = "drainBEP20Token";

    public static final String FUNC_GETREFERRER = "getReferrer";

    public static final String FUNC_NOMINATEPOTENTIALOWNER = "nominatePotentialOwner";

    public static final String FUNC_OPERATORS = "operators";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RECORDREFERRAL = "recordReferral";

    public static final String FUNC_RECORDREFERRALCOMMISSION = "recordReferralCommission";

    public static final String FUNC_REFERRALSCOUNT = "referralsCount";

    public static final String FUNC_REFERRERS = "referrers";

    public static final String FUNC_REFERRERSVALID = "referrersValid";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TOTALREFERRALCOMMISSIONS = "totalReferralCommissions";

    public static final String FUNC_UPDATEOPERATOR = "updateOperator";

    public static final Event ACTIVEREFERRER_EVENT = new Event("ActiveReferrer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
    ;

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

    public List<ActiveReferrerEventResponse> getActiveReferrerEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ACTIVEREFERRER_EVENT, transactionReceipt);
        ArrayList<ActiveReferrerEventResponse> responses = new ArrayList<ActiveReferrerEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ActiveReferrerEventResponse typedResponse = new ActiveReferrerEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.referrer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ActiveReferrerEventResponse> activeReferrerEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ActiveReferrerEventResponse>() {
            @Override
            public ActiveReferrerEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ACTIVEREFERRER_EVENT, log);
                ActiveReferrerEventResponse typedResponse = new ActiveReferrerEventResponse();
                typedResponse.log = log;
                typedResponse.referrer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ActiveReferrerEventResponse> activeReferrerEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ACTIVEREFERRER_EVENT));
        return activeReferrerEventFlowable(filter);
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

    public RemoteFunctionCall<TransactionReceipt> acceptOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ACCEPTOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> activeReferrer(String _referrer, Boolean _status) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ACTIVEREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer), 
                new org.web3j.abi.datatypes.Bool(_status)), 
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

    public RemoteFunctionCall<String> getReferrer(String _user) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteFunctionCall<TransactionReceipt> recordReferralCommission(String _referrer, BigInteger _commission) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RECORDREFERRALCOMMISSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer), 
                new org.web3j.abi.datatypes.generated.Uint256(_commission)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> referralsCount(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERRALSCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> referrers(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERRERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> referrersValid(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERRERSVALID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> totalReferralCommissions(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALREFERRALCOMMISSIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> updateOperator(String _operator, Boolean _status) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPDATEOPERATOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _operator), 
                new org.web3j.abi.datatypes.Bool(_status)), 
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

    public static RemoteCall<NDBReferral> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NDBReferral.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NDBReferral> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NDBReferral.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NDBReferral> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NDBReferral.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class ActiveReferrerEventResponse extends BaseEventResponse {
        public String referrer;

        public Boolean status;
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
}
