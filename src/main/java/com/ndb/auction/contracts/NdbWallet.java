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
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class NdbWallet extends Contract {
    public static final String BINARY = "60806040526003805460ff1916601217905534801561001d57600080fd5b50600080546001600160a01b031916339081178255604051909182917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a3506111578061006e6000396000f3fe608060405234801561001057600080fd5b50600436106101165760003560e01c8063715018a6116100a2578063b532e88011610071578063b532e8801461024b578063b64125991461025e578063c1ad090814610271578063de74cc4814610284578063f2fde38b1461029757600080fd5b8063715018a6146101f85780637f426d8314610202578063893d20e8146102155780638da5cb5b1461023a57600080fd5b8063313ce567116100e9578063313ce5671461019757806341d8f97f146101ac57806346d13187146101bf5780635e9e28fb146101d2578063674da811146101e557600080fd5b80630f296b7d1461011b57806318bf6a7714610143578063192892871461016357806329f371aa14610176575b600080fd5b61012e610129366004610d75565b6102aa565b60405190151581526020015b60405180910390f35b610156610151366004610de2565b610397565b60405161013a9190610e76565b61012e610171366004610de2565b6104d5565b610189610184366004610de2565b6105d4565b60405190815260200161013a565b60035460405160ff909116815260200161013a565b61012e6101ba366004610d75565b610688565b6101566101cd366004610de2565b6106c5565b61012e6101e0366004610d75565b610704565b61012e6101f3366004610ea9565b6107d4565b61020061089e565b005b61012e610210366004610d75565b610912565b6000546001600160a01b03165b6040516001600160a01b03909116815260200161013a565b6000546001600160a01b0316610222565b610189610259366004610de2565b61094f565b61012e61026c366004610d75565b61098c565b61012e61027f366004610d75565b610a0f565b61012e610292366004610ea9565b610a66565b6102006102a5366004610f31565b610aa3565b600080546001600160a01b031633146102de5760405162461bcd60e51b81526004016102d590610f5a565b60405180910390fd5b600060016002866040516102f29190610f8f565b90815260405190819003602001812061030a91610fe6565b908152604051908190036020019020805490915060ff1661033d5760405162461bcd60e51b81526004016102d590611082565b6103698382600301866040516103539190610f8f565b9081526040519081900360200190205490610ad9565b816003018560405161037b9190610f8f565b9081526040519081900360200190205550600190509392505050565b6000546060906001600160a01b031633146103c45760405162461bcd60e51b81526004016102d590610f5a565b600060016002856040516103d89190610f8f565b9081526040519081900360200181206103f091610fe6565b908152604051908190036020019020805490915060ff166104235760405162461bcd60e51b81526004016102d590611082565b80600201836040516104359190610f8f565b9081526020016040518091039020805461044e90610fab565b80601f016020809104026020016040519081016040528092919081815260200182805461047a90610fab565b80156104c75780601f1061049c576101008083540402835291602001916104c7565b820191906000526020600020905b8154815290600101906020018083116104aa57829003601f168201915b505050505091505092915050565b600080546001600160a01b031633146105005760405162461bcd60e51b81526004016102d590610f5a565b60006001836040516105129190610f8f565b908152604051908190036020019020805490915060ff16156105765760405162461bcd60e51b815260206004820152601c60248201527f4e64623a20746865207573657220616c7265616479206578697374730000000060448201526064016102d5565b805460ff1916600190811782558451610596918301906020870190610c39565b50826002856040516105a89190610f8f565b908152602001604051809103902090805190602001906105c9929190610c39565b506001949350505050565b600080546001600160a01b031633146105ff5760405162461bcd60e51b81526004016102d590610f5a565b600060016002856040516106139190610f8f565b90815260405190819003602001812061062b91610fe6565b908152604051908190036020019020805490915060ff1661065e5760405162461bcd60e51b81526004016102d590611082565b80600301836040516106709190610f8f565b90815260200160405180910390205491505092915050565b600080546001600160a01b031633146106b35760405162461bcd60e51b81526004016102d590610f5a565b600060018560405161030a9190610f8f565b6000546060906001600160a01b031633146106f25760405162461bcd60e51b81526004016102d590610f5a565b60006001846040516103f09190610f8f565b600080546001600160a01b0316331461072f5760405162461bcd60e51b81526004016102d590610f5a565b600060016002866040516107439190610f8f565b90815260405190819003602001812061075b91610fe6565b908152604051908190036020019020805490915060ff1661078e5760405162461bcd60e51b81526004016102d590611082565b610369836040518060600160405280602381526020016110ff6023913983600301876040516107bd9190610f8f565b908152604051908190036020019020549190610b3f565b600080546001600160a01b031633146107ff5760405162461bcd60e51b81526004016102d590610f5a565b600060016002866040516108139190610f8f565b90815260405190819003602001812061082b91610fe6565b908152604051908190036020019020805490915060ff1661085e5760405162461bcd60e51b81526004016102d590611082565b8281600201856040516108719190610f8f565b90815260200160405180910390209080519060200190610892929190610c39565b50600195945050505050565b6000546001600160a01b031633146108c85760405162461bcd60e51b81526004016102d590610f5a565b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319169055565b600080546001600160a01b0316331461093d5760405162461bcd60e51b81526004016102d590610f5a565b600060018560405161075b9190610f8f565b600080546001600160a01b0316331461097a5760405162461bcd60e51b81526004016102d590610f5a565b600060018460405161062b9190610f8f565b600080546001600160a01b031633146109b75760405162461bcd60e51b81526004016102d590610f5a565b60006001856040516109c99190610f8f565b908152604051908190036020019020805490915060ff166109fc5760405162461bcd60e51b81526004016102d590611082565b82816003018560405161037b9190610f8f565b600080546001600160a01b03163314610a3a5760405162461bcd60e51b81526004016102d590610f5a565b60006001600286604051610a4e9190610f8f565b9081526040519081900360200181206109c991610fe6565b600080546001600160a01b03163314610a915760405162461bcd60e51b81526004016102d590610f5a565b600060018560405161082b9190610f8f565b6000546001600160a01b03163314610acd5760405162461bcd60e51b81526004016102d590610f5a565b610ad681610b79565b50565b600080610ae683856110cf565b905083811015610b385760405162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f77000000000060448201526064016102d5565b9392505050565b60008184841115610b635760405162461bcd60e51b81526004016102d59190610e76565b506000610b7084866110e7565b95945050505050565b6001600160a01b038116610bde5760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084016102d5565b600080546040516001600160a01b03808516939216917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e091a3600080546001600160a01b0319166001600160a01b0392909216919091179055565b828054610c4590610fab565b90600052602060002090601f016020900481019282610c675760008555610cad565b82601f10610c8057805160ff1916838001178555610cad565b82800160010185558215610cad579182015b82811115610cad578251825591602001919060010190610c92565b50610cb9929150610cbd565b5090565b5b80821115610cb95760008155600101610cbe565b634e487b7160e01b600052604160045260246000fd5b600082601f830112610cf957600080fd5b813567ffffffffffffffff80821115610d1457610d14610cd2565b604051601f8301601f19908116603f01168101908282118183101715610d3c57610d3c610cd2565b81604052838152866020858801011115610d5557600080fd5b836020870160208301376000602085830101528094505050505092915050565b600080600060608486031215610d8a57600080fd5b833567ffffffffffffffff80821115610da257600080fd5b610dae87838801610ce8565b94506020860135915080821115610dc457600080fd5b50610dd186828701610ce8565b925050604084013590509250925092565b60008060408385031215610df557600080fd5b823567ffffffffffffffff80821115610e0d57600080fd5b610e1986838701610ce8565b93506020850135915080821115610e2f57600080fd5b50610e3c85828601610ce8565b9150509250929050565b60005b83811015610e61578181015183820152602001610e49565b83811115610e70576000848401525b50505050565b6020815260008251806020840152610e95816040850160208701610e46565b601f01601f19169190910160400192915050565b600080600060608486031215610ebe57600080fd5b833567ffffffffffffffff80821115610ed657600080fd5b610ee287838801610ce8565b94506020860135915080821115610ef857600080fd5b610f0487838801610ce8565b93506040860135915080821115610f1a57600080fd5b50610f2786828701610ce8565b9150509250925092565b600060208284031215610f4357600080fd5b81356001600160a01b0381168114610b3857600080fd5b6020808252818101527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604082015260600190565b60008251610fa1818460208701610e46565b9190910192915050565b600181811c90821680610fbf57607f821691505b60208210811415610fe057634e487b7160e01b600052602260045260246000fd5b50919050565b600080835481600182811c91508083168061100257607f831692505b602080841082141561102257634e487b7160e01b86526022600452602486fd5b818015611036576001811461104757611074565b60ff19861689528489019650611074565b60008a81526020902060005b8681101561106c5781548b820152908501908301611053565b505084890196505b509498975050505050505050565b6020808252601c908201527f4e64623a20746865207573657220646f6573206e6f7420657869737400000000604082015260600190565b634e487b7160e01b600052601160045260246000fd5b600082198211156110e2576110e26110b9565b500190565b6000828210156110f9576110f96110b9565b50039056fe4e64623a2074686520616d6f756e742065786365656473207468652062616c616e6365a264697066735822122079d8824a1ef200f1a5db8dd53f3858eb58b84b795496cfa7b9b518522e3cee3164736f6c634300080a0033";

    public static final String FUNC_CREATEACCOUNT = "createAccount";

    public static final String FUNC_CREATEWALLETWITHEMAIL = "createWalletWithEmail";

    public static final String FUNC_CREATEWALLETWITHID = "createWalletWithId";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DECREASEHOLDBALANCEWITHEMAIL = "decreaseHoldBalanceWithEmail";

    public static final String FUNC_DECREASEHOLDBALANCEWITHID = "decreaseHoldBalanceWithId";

    public static final String FUNC_GETHOLDBALANCEWITHEMAIL = "getHoldBalanceWithEmail";

    public static final String FUNC_GETHOLDBALANCEWITHID = "getHoldBalanceWithId";

    public static final String FUNC_GETOWNER = "getOwner";

    public static final String FUNC_GETPRIVATEKEYWITHEMAIL = "getPrivateKeyWithEmail";

    public static final String FUNC_GETPRIVATEKEYWITHID = "getPrivateKeyWithId";

    public static final String FUNC_INCREASEHOLDBALANCEWITHEMAIL = "increaseHoldBalanceWithEmail";

    public static final String FUNC_INCREASEHOLDBALANCEWITHID = "increaseHoldBalanceWithId";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETHOLDBALANCEWITHEMAIL = "setHoldBalanceWithEmail";

    public static final String FUNC_SETHOLDBALANCEWITHID = "setHoldBalanceWithId";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected NdbWallet(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NdbWallet(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected NdbWallet(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected NdbWallet(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
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
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
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

    public RemoteFunctionCall<TransactionReceipt> createAccount(String id, String email) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEACCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(email)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createWalletWithEmail(String email, String tokenType, String privateKey) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEWALLETWITHEMAIL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(email), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.Utf8String(privateKey)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createWalletWithId(String id, String tokenType, String privateKey) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEWALLETWITHID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.Utf8String(privateKey)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> decreaseHoldBalanceWithEmail(String email, String tokenType, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DECREASEHOLDBALANCEWITHEMAIL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(email), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> decreaseHoldBalanceWithId(String id, String tokenType, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DECREASEHOLDBALANCEWITHID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getHoldBalanceWithEmail(String email, String tokenType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETHOLDBALANCEWITHEMAIL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(email), 
                new org.web3j.abi.datatypes.Utf8String(tokenType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getHoldBalanceWithId(String id, String tokenType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETHOLDBALANCEWITHID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(tokenType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getOwner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getPrivateKeyWithEmail(String email, String tokenType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPRIVATEKEYWITHEMAIL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(email), 
                new org.web3j.abi.datatypes.Utf8String(tokenType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getPrivateKeyWithId(String id, String tokenType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPRIVATEKEYWITHID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(tokenType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> increaseHoldBalanceWithEmail(String email, String tokenType, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INCREASEHOLDBALANCEWITHEMAIL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(email), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> increaseHoldBalanceWithId(String id, String tokenType, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INCREASEHOLDBALANCEWITHID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setHoldBalanceWithEmail(String email, String tokenType, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETHOLDBALANCEWITHEMAIL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(email), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setHoldBalanceWithId(String id, String tokenType, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETHOLDBALANCEWITHID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(tokenType), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static NdbWallet load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NdbWallet(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static NdbWallet load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NdbWallet(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static NdbWallet load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new NdbWallet(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static NdbWallet load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new NdbWallet(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<NdbWallet> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NdbWallet.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NdbWallet> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NdbWallet.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<NdbWallet> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NdbWallet.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NdbWallet> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NdbWallet.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }
}
