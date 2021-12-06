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
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class UserWallet extends Contract {
    public static final String BINARY = "60806040523480156200001157600080fd5b50600080546001600160a01b031916339081178255604051909182917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a350620000b9604051806040016040528060028152602001611a5960f21b81525060405180604001604052806005815260200164195b585a5b60da1b815250604051806040016040528060048152602001636e616d6560e01b815250620000c060201b60201c565b5062000309565b6060620000d56000546001600160a01b031690565b6001600160a01b0316336001600160a01b031614620001455760405162461bcd60e51b815260206004820152602260248201527f4f776e65722063616e206f6e6c792063616c6c20746869732066756e6374696f604482015261371760f11b606482015260840160405180910390fd5b600084905060006002826040516200015e91906200028e565b908152602001604051809103902090508581600001908051906020019062000188929190620001e8565b508451620001a09060018301906020880190620001e8565b508351620001b89060028301906020870190620001e8565b50600060049091015550506040805180820190915260078152665375636365737360c81b60208201529392505050565b828054620001f690620002cc565b90600052602060002090601f0160209004810192826200021a576000855562000265565b82601f106200023557805160ff191683800117855562000265565b8280016001018555821562000265579182015b828111156200026557825182559160200191906001019062000248565b506200027392915062000277565b5090565b5b8082111562000273576000815560010162000278565b6000825160005b81811015620002b1576020818601810151858301520162000295565b81811115620002c1576000828501525b509190910192915050565b600181811c90821680620002e157607f821691505b602082108114156200030357634e487b7160e01b600052602260045260246000fd5b50919050565b61161f80620003196000396000f3fe608060405234801561001057600080fd5b50600436106100cf5760003560e01c80634e370bce1161008c5780638da5cb5b116100665780638da5cb5b146101ca5780638dfad597146101e55780639a12b1c0146101fa578063f2fde38b1461020d57600080fd5b80634e370bce14610178578063715018a6146101a05780637228c6e0146101aa57600080fd5b80631081b17e146100d45780631e75b924146100fc5780631ecc00411461010f5780632787f63a146101315780632fdc95a014610144578063312743d314610165575b600080fd5b6100e76100e23660046111c2565b610220565b60405190151581526020015b60405180910390f35b6100e761010a3660046111c2565b610379565b61012261011d36600461122f565b610422565b6040516100f39392919061135b565b6100e761013f3660046111c2565b610645565b61015761015236600461122f565b610792565b6040519081526020016100f3565b6100e76101733660046111c2565b6107eb565b61018b61018636600461139e565b610883565b604080519283526020830191909152016100f3565b6101a8610938565b005b6101bd6101b836600461122f565b6109dc565b6040516100f39190611402565b6000546040516001600160a01b0390911681526020016100f3565b6101ed610ad5565b6040516100f3919061141c565b6101bd61020836600461142f565b610bae565b6101a861021b3660046114b7565b610c8d565b600080546001600160a01b031633146102545760405162461bcd60e51b815260040161024b906114e0565b60405180910390fd5b6040518490849060009060029061026c908590611522565b90815260200160405180910390209050600081600501836040516102909190611522565b9081526040519081900360200190206001015490506000806102b28389610da7565b91509150816103035760405162461bcd60e51b815260206004820152601a60248201527f4672656520616d6f756e74206973206e6f7420656e6f7567682e000000000000604482015260640161024b565b8784600501866040516103169190611522565b908152602001604051809103902060010160010160008282546103399190611554565b925050819055508084600501866040516103539190611522565b9081526040519081900360200190206001908101919091559a9950505050505050505050565b600080546001600160a01b031633146103a45760405162461bcd60e51b815260040161024b906114e0565b604051849084906000906002906103bc908590611522565b908152604051908190036020019020905060006103d98284610dd4565b90508582600501846040516103ee9190611522565b908152602001604051809103902060010160000160008282546104119190611554565b909155509098975050505050505050565b60608060606104396000546001600160a01b031690565b6001600160a01b0316336001600160a01b0316146104695760405162461bcd60e51b815260040161024b906114e0565b600084905060006002826040516104809190611522565b908152602001604051809103902090506000816004015467ffffffffffffffff8111156104af576104af61111f565b6040519080825280602002602001820160405280156104d8578160200160208202803683370190505b5090506000826004015467ffffffffffffffff8111156104fa576104fa61111f565b604051908082528060200260200182016040528015610523578160200160208202803683370190505b5090506000836004015467ffffffffffffffff8111156105455761054561111f565b60405190808252806020026020018201604052801561057857816020015b60608152602001906001900390816105635790505b50905060008061058786610eae565b90505b6003860154811015610633576000806105a38884610f03565b91509150818585815181106105ba576105ba61156c565b602002602001018190525080600001518785815181106105dc576105dc61156c565b60200260200101818152505080602001518685815181106105ff576105ff61156c565b60209081029190910101528361061481611582565b945050505061062c818761101e90919063ffffffff16565b905061058a565b50909992985090965090945050505050565b600080546001600160a01b031633146106705760405162461bcd60e51b815260040161024b906114e0565b60405184908490600090600290610688908590611522565b90815260200160405180910390209050600081600501836040516106ac9190611522565b9081526040519081900360200190206002015490506000806106ce8389610da7565b915091508161071f5760405162461bcd60e51b815260206004820152601a60248201527f486f6c6420616d6f756e74206973206e6f7420656e6f7567682e000000000000604482015260640161024b565b8084600501866040516107329190611522565b908152604051908190036020018120600201919091558890600586019061075a908890611522565b9081526020016040518091039020600101600001600082825461077d9190611554565b9091555060019b9a5050505050505050505050565b600080546001600160a01b031633146107bd5760405162461bcd60e51b815260040161024b906114e0565b60405182906002906107d0908390611522565b9081526020016040518091039020600401549150505b919050565b600080546001600160a01b031633146108165760405162461bcd60e51b815260040161024b906114e0565b6040518490849060009060029061082e908590611522565b9081526040519081900360200190209050600061084b8284610dd4565b90508582600501846040516108609190611522565b908152602001604051809103902060010160010160008282546104119190611554565b6000806108986000546001600160a01b031690565b6001600160a01b0316336001600160a01b0316146108c85760405162461bcd60e51b815260040161024b906114e0565b604051849084906000906002906108e0908590611522565b9081526020016040518091039020600501826040516108ff9190611522565b908152604080516020928190038301812081830190925260018201548082526002909201549201829052955093505050505b9250929050565b6000546001600160a01b031633146109925760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161024b565b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319169055565b60606109f06000546001600160a01b031690565b6001600160a01b0316336001600160a01b031614610a205760405162461bcd60e51b815260040161024b906114e0565b6040518290600290610a33908390611522565b90815260200160405180910390206002018054610a4f9061159d565b80601f0160208091040260200160405190810160405280929190818152602001828054610a7b9061159d565b8015610ac85780601f10610a9d57610100808354040283529160200191610ac8565b820191906000526020600020905b815481529060010190602001808311610aab57829003601f168201915b5050505050915050919050565b60606001805480602002602001604051908101604052809291908181526020016000905b82821015610ba5578382906000526020600020018054610b189061159d565b80601f0160208091040260200160405190810160405280929190818152602001828054610b449061159d565b8015610b915780601f10610b6657610100808354040283529160200191610b91565b820191906000526020600020905b815481529060010190602001808311610b7457829003601f168201915b505050505081526020019060010190610af9565b50505050905090565b6060610bc26000546001600160a01b031690565b6001600160a01b0316336001600160a01b031614610bf25760405162461bcd60e51b815260040161024b906114e0565b60008490506000600282604051610c099190611522565b9081526020016040518091039020905085816000019080519060200190610c31929190611086565b508451610c479060018301906020880190611086565b508351610c5d9060028301906020870190611086565b50600060049091015550506040805180820190915260078152665375636365737360c81b60208201529392505050565b6000546001600160a01b03163314610ce75760405162461bcd60e51b815260206004820181905260248201527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604482015260640161024b565b6001600160a01b038116610d4c5760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b606482015260840161024b565b600080546040516001600160a01b03808516939216917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e091a3600080546001600160a01b0319166001600160a01b0392909216919091179055565b60008083831115610dbd57506000905080610931565b6001610dc984866115d2565b915091509250929050565b6000808360050183604051610de99190611522565b9081526040519081900360200190205490508015610e0b576001915050610ea8565b506003830180546001808201835560009290925290610e2b908290611554565b8460050184604051610e3d9190611522565b9081526040519081900360200190205560038401805484919083908110610e6657610e6661156c565b90600052602060002090600202016000019080519060200190610e8a929190611086565b50600484018054906000610e9d83611582565b919050555060009150505b92915050565b60005b600382015481108015610eec5750816003018181548110610ed457610ed461156c565b600091825260209091206001600290920201015460ff165b156107e65780610efb81611582565b915050610eb1565b6060610f22604051806040016040528060008152602001600081525090565b836003018381548110610f3757610f3761156c565b90600052602060002090600202016000018054610f539061159d565b80601f0160208091040260200160405190810160405280929190818152602001828054610f7f9061159d565b8015610fcc5780601f10610fa157610100808354040283529160200191610fcc565b820191906000526020600020905b815481529060010190602001808311610faf57829003601f168201915b505050505091508360050182604051610fe59190611522565b90815260200160405180910390206001016040518060400160405290816000820154815260200160018201548152505090509250929050565b60008161102a81611582565b9250505b60038301548210801561106957508260030182815481106110515761105161156c565b600091825260209091206001600290920201015460ff165b15611080578161107881611582565b92505061102e565b50919050565b8280546110929061159d565b90600052602060002090601f0160209004810192826110b457600085556110fa565b82601f106110cd57805160ff19168380011785556110fa565b828001600101855582156110fa579182015b828111156110fa5782518255916020019190600101906110df565b5061110692915061110a565b5090565b5b80821115611106576000815560010161110b565b634e487b7160e01b600052604160045260246000fd5b600082601f83011261114657600080fd5b813567ffffffffffffffff808211156111615761116161111f565b604051601f8301601f19908116603f011681019082821181831017156111895761118961111f565b816040528381528660208588010111156111a257600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000606084860312156111d757600080fd5b833567ffffffffffffffff808211156111ef57600080fd5b6111fb87838801611135565b9450602086013591508082111561121157600080fd5b5061121e86828701611135565b925050604084013590509250925092565b60006020828403121561124157600080fd5b813567ffffffffffffffff81111561125857600080fd5b61126484828501611135565b949350505050565b60005b8381101561128757818101518382015260200161126f565b83811115611296576000848401525b50505050565b600081518084526112b481602086016020860161126c565b601f01601f19169290920160200192915050565b600082825180855260208086019550808260051b84010181860160005b8481101561131357601f1986840301895261130183835161129c565b988401989250908301906001016112e5565b5090979650505050505050565b600081518084526020808501945080840160005b8381101561135057815187529582019590820190600101611334565b509495945050505050565b60608152600061136e60608301866112c8565b82810360208401526113808186611320565b905082810360408401526113948185611320565b9695505050505050565b600080604083850312156113b157600080fd5b823567ffffffffffffffff808211156113c957600080fd5b6113d586838701611135565b935060208501359150808211156113eb57600080fd5b506113f885828601611135565b9150509250929050565b602081526000611415602083018461129c565b9392505050565b60208152600061141560208301846112c8565b60008060006060848603121561144457600080fd5b833567ffffffffffffffff8082111561145c57600080fd5b61146887838801611135565b9450602086013591508082111561147e57600080fd5b61148a87838801611135565b935060408601359150808211156114a057600080fd5b506114ad86828701611135565b9150509250925092565b6000602082840312156114c957600080fd5b81356001600160a01b038116811461141557600080fd5b60208082526022908201527f4f776e65722063616e206f6e6c792063616c6c20746869732066756e6374696f604082015261371760f11b606082015260800190565b6000825161153481846020870161126c565b9190910192915050565b634e487b7160e01b600052601160045260246000fd5b600082198211156115675761156761153e565b500190565b634e487b7160e01b600052603260045260246000fd5b60006000198214156115965761159661153e565b5060010190565b600181811c908216806115b157607f821691505b6020821081141561108057634e487b7160e01b600052602260045260246000fd5b6000828210156115e4576115e461153e565b50039056fea26469706673582212204d4f5cd908a3b7f327fb10154fceb5fffc6e1f004e5e25eb79cd7a76904d885564736f6c634300080a0033";

    public static final String FUNC_ADDFREEAMOUNT = "addFreeAmount";

    public static final String FUNC_ADDHOLDAMOUNT = "addHoldAmount";

    public static final String FUNC_ADDNEWUSER = "addNewUser";

    public static final String FUNC_GETCOINLIST = "getCoinList";

    public static final String FUNC_GETUSERNAME = "getUserName";

    public static final String FUNC_GETWALLETBYID = "getWalletById";

    public static final String FUNC_GETWALLETSIZE = "getWalletSize";

    public static final String FUNC_GETWALLETS = "getWallets";

    public static final String FUNC_MAKEHOLD = "makeHold";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RELEASEHOLD = "releaseHold";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected UserWallet(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected UserWallet(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected UserWallet(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected UserWallet(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteFunctionCall<TransactionReceipt> addFreeAmount(String id, String crypto, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDFREEAMOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(crypto), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addHoldAmount(String id, String crypto, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDHOLDAMOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(crypto), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addNewUser(String id, String email, String name) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDNEWUSER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(email), 
                new org.web3j.abi.datatypes.Utf8String(name)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getCoinList() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCOINLIST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
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

    public RemoteFunctionCall<String> getUserName(String id) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETUSERNAME, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getWalletById(String id, String crypto) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETWALLETBYID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(crypto)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
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

    public RemoteFunctionCall<BigInteger> getWalletSize(String id) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETWALLETSIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple3<List<String>, List<BigInteger>, List<BigInteger>>> getWallets(String id) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETWALLETS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple3<List<String>, List<BigInteger>, List<BigInteger>>>(function,
                new Callable<Tuple3<List<String>, List<BigInteger>, List<BigInteger>>>() {
                    @Override
                    public Tuple3<List<String>, List<BigInteger>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<String>, List<BigInteger>, List<BigInteger>>(
                                convertToNative((List<Utf8String>) results.get(0).getValue()), 
                                convertToNative((List<Uint256>) results.get(1).getValue()), 
                                convertToNative((List<Uint256>) results.get(2).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> makeHold(String id, String crypto, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MAKEHOLD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(crypto), 
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

    public RemoteFunctionCall<TransactionReceipt> releaseHold(String id, String crypto, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RELEASEHOLD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(crypto), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
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
    public static UserWallet load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new UserWallet(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static UserWallet load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new UserWallet(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static UserWallet load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new UserWallet(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static UserWallet load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new UserWallet(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<UserWallet> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(UserWallet.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<UserWallet> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(UserWallet.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<UserWallet> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(UserWallet.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<UserWallet> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(UserWallet.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }
}
