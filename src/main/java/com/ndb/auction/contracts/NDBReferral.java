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
import org.web3j.tuples.generated.Tuple4;
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
    public static final String BINARY = "608060405234801561001057600080fd5b50611d87806100206000396000f3fe608060405234801561001057600080fd5b50600436106101a95760003560e01c80636d70f7ae116100f95780638da5cb5b11610097578063c606339f11610071578063c606339f146104ad578063daa80bb3146104c0578063e06cf687146104e9578063ebfe8eb5146104fc57600080fd5b80638da5cb5b14610476578063a2f0822414610487578063b3162faa1461049a57600080fd5b806379ba5097116100d357806379ba5097146104405780637c24543d146104485780638129fc1c1461045b5780638b3f577f1461046357600080fd5b80636d70f7ae146103f9578063715018a614610425578063795476281461042d57600080fd5b80634fdac60f11610166578063602b386e11610140578063602b386e1461036d578063695c2f4e1461038d5780636a83de79146103a05780636d44a3b2146103e657600080fd5b80634fdac60f1461032757806352c043c11461033a5780635bf11d4a1461034d57600080fd5b80630c7f7b6b146101ae57806313e7c9d8146101c3578063186bde88146101fb5780631b4a3ebe1461023c5780632a3db2a7146102765780634a9fefc714610299575b600080fd5b6101c16101bc3660046119c0565b61050f565b005b6101e66101d13660046119f9565b60026020526000908152604090205460ff1681565b60405190151581526020015b60405180910390f35b6102246102093660046119f9565b6003602052600090815260409020546001600160a01b031681565b6040516001600160a01b0390911681526020016101f2565b61026861024a3660046119f9565b6001600160a01b031660009081526005602052604090205460ff1690565b6040519081526020016101f2565b6101e66102843660046119f9565b60096020526000908152604090205460ff1681565b6102f36102a73660046119f9565b6001600160a01b0380821660008181526003602090815260408083205490941680835260058252848320546006549484526009909252939091205460ff91821692821691169193509193565b604080516001600160a01b039095168552602085019390935260ff90911691830191909152151560608201526080016101f2565b6101c16103353660046119f9565b61077b565b6101c16103483660046119f9565b6107d3565b61026861035b3660046119f9565b60046020526000908152604090205481565b61038061037b3660046119f9565b610847565b6040516101f29190611a16565b6101c161039b366004611a63565b6108c0565b6103cd6103ae3660046119f9565b6005602052600090815260409020805460019091015460ff9091169082565b6040805160ff90931683526020830191909152016101f2565b6101c16103f4366004611a8a565b610926565b6101e66104073660046119f9565b6001600160a01b031660009081526002602052604090205460ff1690565b6101c16109a4565b6101c161043b366004611ab8565b610a24565b6101c1610aa8565b6101c1610456366004611b0b565b610b9d565b6101c1610c95565b6101c16104713660046119c0565b610cec565b6000546001600160a01b0316610224565b6101c1610495366004611b0b565b6112b4565b6101c16104a8366004611b40565b6113aa565b6102686104bb3660046119f9565b611416565b6102686104ce3660046119f9565b6001600160a01b031660009081526004602052604090205490565b6101c16104f73660046119f9565b611498565b6101c161050a366004611b5b565b6114e1565b3360009081526002602052604090205460ff166105475760405162461bcd60e51b815260040161053e90611bae565b60405180910390fd5b6001600160a01b03821661059d5760405162461bcd60e51b815260206004820152601960248201527f5f7573657220697320746865207a65726f206164647265737300000000000000604482015260640161053e565b6001600160a01b0381166105c35760405162461bcd60e51b815260040161053e90611bf2565b806001600160a01b0316826001600160a01b031614156106255760405162461bcd60e51b815260206004820152601b60248201527f5f7573657220697320657175616c20746f205f72656665727265720000000000604482015260640161053e565b6001600160a01b03828116600090815260036020526040902054161561068d5760405162461bcd60e51b815260206004820152601f60248201527f5f757365722068617320616c7265616479206265656e20726566657272656400604482015260640161053e565b6001600160a01b03811660009081526005602052604090205460ff166106f55760405162461bcd60e51b815260206004820152601760248201527f5265666572726572206e6f742061637469766520796574000000000000000000604482015260640161053e565b6001600160a01b03808216600081815260056020908152604080832060020180546001810182559084528284200180549588166001600160a01b0319968716811790915580845260039092528083208054909516841790945592519192917ff61ccbe316daff56654abed758191f9a4dcac526d43747a50a2d545c0ca64d829190a35050565b3360009081526002602052604090205460ff166107aa5760405162461bcd60e51b815260040161053e90611bae565b6007546107b79042611c3f565b6001600160a01b03909116600090815260086020526040902055565b6000546001600160a01b031633146107fd5760405162461bcd60e51b815260040161053e90611c57565b600180546001600160a01b0319166001600160a01b0383169081179091556040517f906a1c6bd7e3091ea86693dd029a831c19049ce77f1dce2ce0bab1cacbabce2290600090a250565b6001600160a01b0381166000908152600560209081526040918290206002018054835181840281018401909452808452606093928301828280156108b457602002820191906000526020600020905b81546001600160a01b03168152600190910190602001808311610896575b50505050509050919050565b6000546001600160a01b031633146108ea5760405162461bcd60e51b815260040161053e90611c57565b60078190556040518181527f1089e4cd6526fcaeb2e85095ac6583ff99fbe58b548c89f488fdd4dc9033371c906020015b60405180910390a150565b6000546001600160a01b031633146109505760405162461bcd60e51b815260040161053e90611c57565b6001600160a01b038216600081815260026020526040808220805460ff191685151590811790915590519092917f966c160e1c4dbc7df8d69af4ace01e9297c3cf016397b7914971f2fbfa32672d91a35050565b6000546001600160a01b031633146109ce5760405162461bcd60e51b815260040161053e90611c57565b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319908116909155600180549091169055565b6000546001600160a01b03163314610a4e5760405162461bcd60e51b815260040161053e90611c57565b610a626001600160a01b0384168284611615565b604080518381526001600160a01b03831660208201527f605022943465a74fc0f43f0f842fd8163e45145ade2fea43728ab758e317ba6d910160405180910390a1505050565b6001546001600160a01b03163314610b395760405162461bcd60e51b815260206004820152604860248201527f596f75206d757374206265206e6f6d696e6174656420617320706f74656e746960448201527f616c206f776e6572206265666f726520796f752063616e20616363657074206f6064820152670776e6572736869760c41b608482015260a40161053e565b600154600080546040516001600160a01b0393841693909116917fb532073b38c83145e3e5135377a08bf9aab55bc0fd7c1179cd4fb995d2a5159c91a360018054600080546001600160a01b03199081166001600160a01b03841617909155169055565b3360009081526002602052604090205460ff16610bcc5760405162461bcd60e51b815260040161053e90611bae565b6001600160a01b038216610bf25760405162461bcd60e51b815260040161053e90611bf2565b60008160ff1611610c345760405162461bcd60e51b815260206004820152600c60248201526b52617465206973207a65726f60a01b604482015260640161053e565b6001600160a01b038216600081815260056020908152604091829020805460ff191660ff861690811790915591519182527f719a65f5451c353953daae53c6a31ed24315a4f03b27b41f346172c7f125afca91015b60405180910390a25050565b60008054336001600160a01b0319909116811782556006805460ff1916600a17905561a8c0600755604051909182917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a350565b3360009081526002602052604090205460ff16610d1b5760405162461bcd60e51b815260040161053e90611bae565b6001600160a01b03821660009081526008602052604090205482901580610d5a57506001600160a01b0381166000908152600860205260409020544210155b610da65760405162461bcd60e51b815260206004820152601d60248201527f5265666572726572205570646174652069732074696d656c6f636b6564000000604482015260640161053e565b816001600160a01b0316836001600160a01b03161415610e085760405162461bcd60e51b815260206004820152601960248201527f5f6f6c6420697320657175616c20746f205f63757272656e7400000000000000604482015260640161053e565b6001600160a01b038316610e5e5760405162461bcd60e51b815260206004820181905260248201527f6e657720726566657272657220697320746865207a65726f2061646472657373604482015260640161053e565b6001600160a01b038216610ec05760405162461bcd60e51b8152602060048201526024808201527f63757272656e7420726566657272657220697320746865207a65726f206164646044820152637265737360e01b606482015260840161053e565b6001600160a01b03831660009081526005602052604090205460ff1615611061576001600160a01b03808416600090815260056020526040808220928516825290208154815460ff191660ff9091161781556001808301549082015560028083018054610f309284019190611925565b5050506001600160a01b038216600090815260056020908152604080832060020180548251818502810185019093528083529192909190830182828015610fa057602002820191906000526020600020905b81546001600160a01b03168152600190910190602001808311610f82575b5050505050905060005b8151811015611027578360036000848481518110610fca57610fca611c8c565b60200260200101516001600160a01b03166001600160a01b0316815260200190815260200160002060006101000a8154816001600160a01b0302191690836001600160a01b031602179055506110208160010190565b9050610faa565b506001600160a01b0384166000908152600560205260408120805460ff19168155600181018290559061105d6002830182611975565b5050505b6001600160a01b038381166000908152600360205260409020541615611259576001600160a01b03808416600090815260036020908152604080832054909316808352600582528383208451606081018652815460ff16815260018201548185015260028201805487518187028101870189528181529497929593949286019383018282801561111a57602002820191906000526020600020905b81546001600160a01b031681526001909101906020018083116110fc575b505050919092525050506040810151805191925090600090815b8181101561117f57886001600160a01b031684828151811061115857611158611c8c565b60200260200101516001600160a01b031614156111775780925061117f565b600101611134565b506001600160a01b03851660009081526005602052604090206002018054889190849081106111b0576111b0611c8c565b6000918252602080832090910180546001600160a01b039485166001600160a01b0319918216179091558b84168084526004808452604080862080548f891680895283892091909155600380885283892080549f909a169e87169e909e17909855838752600986528187208054988852828820805460ff909a16151560ff199a8b1617905593875283549097169092559983528320805490911690559690965294909455505050505b6007546112669042611c3f565b6001600160a01b038084166000818152600860205260408082209490945592519092918616917fc40302e3b5897f6966b131753cb09f65aa712ae82e3f49b189d089d5694256e391a3505050565b3360009081526002602052604090205460ff166112e35760405162461bcd60e51b815260040161053e90611bae565b6001600160a01b0382166113095760405162461bcd60e51b815260040161053e90611bf2565b60008160ff161161134b5760405162461bcd60e51b815260206004820152600c60248201526b52617465206973207a65726f60a01b604482015260640161053e565b6001600160a01b0382166000818152600560209081526040808320805460ff191660ff87169081178255600190910193909355519182527fb2a60f125657306a9287a8eafa2344a7fe2976d44c293ddbb26f032992c5549c9101610c89565b6000546001600160a01b031633146113d45760405162461bcd60e51b815260040161053e90611c57565b6006805460ff191660ff83169081179091556040519081527f161ac3d6084899bc7d75453abbf3ccc9a2977f8ffdc6d75a337d1251725bdb589060200161091b565b3360009081526002602052604081205460ff166114455760405162461bcd60e51b815260040161053e90611bae565b6001600160a01b03821660009081526008602052604090205442101561148f576001600160a01b038216600090815260086020526040902054611489904290611ca2565b92915050565b5060005b919050565b3360009081526002602052604090205460ff166114c75760405162461bcd60e51b815260040161053e90611bae565b6001600160a01b0316600090815260086020526040812055565b3360009081526002602052604090205460ff166115105760405162461bcd60e51b815260040161053e90611bae565b6001600160a01b0384161580159061153057506001600160a01b03831615155b801561153c5750600082115b1561160f576001600160a01b038381166000908152600960209081526040808320805460ff19168615151790559287168252600590522060010154611581908361166c565b6001600160a01b038086166000908152600560209081526040808320600101949094559186168152600490915220546115ba908361166c565b6001600160a01b038085166000908152600460209081526040918290209390935551848152908616917f91badd4ef769cf56b7db0b350b95c9fb6d973e6e37d28a51fed219cc7d53184f910160405180910390a25b50505050565b604080516001600160a01b038416602482015260448082018490528251808303909101815260649091019091526020810180516001600160e01b031663a9059cbb60e01b1790526116679084906116d2565b505050565b6000806116798385611c3f565b9050838110156116cb5760405162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015260640161053e565b9392505050565b6000611727826040518060400160405280602081526020017f5361666542455032303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b03166117a49092919063ffffffff16565b80519091501561166757808060200190518101906117459190611cb9565b6116675760405162461bcd60e51b815260206004820152602a60248201527f5361666542455032303a204245503230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b606482015260840161053e565b60606117b384846000856117bb565b949350505050565b60608247101561181c5760405162461bcd60e51b815260206004820152602660248201527f416464726573733a20696e73756666696369656e742062616c616e636520666f6044820152651c8818d85b1b60d21b606482015260840161053e565b6001600160a01b0385163b6118735760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e7472616374000000604482015260640161053e565b600080866001600160a01b0316858760405161188f9190611d02565b60006040518083038185875af1925050503d80600081146118cc576040519150601f19603f3d011682016040523d82523d6000602084013e6118d1565b606091505b50915091506118e18282866118ec565b979650505050505050565b606083156118fb5750816116cb565b82511561190b5782518084602001fd5b8160405162461bcd60e51b815260040161053e9190611d1e565b8280548282559060005260206000209081019282156119655760005260206000209182015b8281111561196557825482559160010191906001019061194a565b50611971929150611996565b5090565b50805460008255906000526020600020908101906119939190611996565b50565b5b808211156119715760008155600101611997565b6001600160a01b038116811461199357600080fd5b600080604083850312156119d357600080fd5b82356119de816119ab565b915060208301356119ee816119ab565b809150509250929050565b600060208284031215611a0b57600080fd5b81356116cb816119ab565b6020808252825182820181905260009190848201906040850190845b81811015611a575783516001600160a01b031683529284019291840191600101611a32565b50909695505050505050565b600060208284031215611a7557600080fd5b5035919050565b801515811461199357600080fd5b60008060408385031215611a9d57600080fd5b8235611aa8816119ab565b915060208301356119ee81611a7c565b600080600060608486031215611acd57600080fd5b8335611ad8816119ab565b9250602084013591506040840135611aef816119ab565b809150509250925092565b803560ff8116811461149357600080fd5b60008060408385031215611b1e57600080fd5b8235611b29816119ab565b9150611b3760208401611afa565b90509250929050565b600060208284031215611b5257600080fd5b6116cb82611afa565b60008060008060808587031215611b7157600080fd5b8435611b7c816119ab565b93506020850135611b8c816119ab565b9250604085013591506060850135611ba381611a7c565b939692955090935050565b60208082526024908201527f4f70657261746f723a2063616c6c6572206973206e6f7420746865206f70657260408201526330ba37b960e11b606082015260800190565b6020808252601d908201527f5f726566657272657220697320746865207a65726f2061646472657373000000604082015260600190565b634e487b7160e01b600052601160045260246000fd5b60008219821115611c5257611c52611c29565b500190565b6020808252818101527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604082015260600190565b634e487b7160e01b600052603260045260246000fd5b600082821015611cb457611cb4611c29565b500390565b600060208284031215611ccb57600080fd5b81516116cb81611a7c565b60005b83811015611cf1578181015183820152602001611cd9565b8381111561160f5750506000910152565b60008251611d14818460208701611cd6565b9190910192915050565b6020815260008251806020840152611d3d816040850160208701611cd6565b601f01601f1916919091016040019291505056fea2646970667358221220032a58ebe6800c5e7fad643251139d3f0ea83c85e39b0ac480aca761380107c064736f6c634300080a0033";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_ACTIVEREFERRER = "activeReferrer";

    public static final String FUNC_DRAINBEP20TOKEN = "drainBEP20Token";

    public static final String FUNC_FIRSTPURCHASE = "firstPurchase";

    public static final String FUNC_GETEARNING = "getEarning";

    public static final String FUNC_GETREFERRER = "getReferrer";

    public static final String FUNC_GETREFERRERRATE = "getReferrerRate";

    public static final String FUNC_GETUSERS = "getUsers";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_ISOPERATOR = "isOperator";

    public static final String FUNC_LOCKUPDATEREFERRER = "lockUpdateReferrer";

    public static final String FUNC_LOCKINGTIMEREMAIN = "lockingTimeRemain";

    public static final String FUNC_NOMINATEPOTENTIALOWNER = "nominatePotentialOwner";

    public static final String FUNC_OPERATORS = "operators";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RECORDREFERRAL = "recordReferral";

    public static final String FUNC_RECORDREFERRALCOMMISSION = "recordReferralCommission";

    public static final String FUNC_REFERRERDETAILS = "referrerDetails";

    public static final String FUNC_REFERRERSBYUSER = "referrersByUser";

    public static final String FUNC_REFERRERSEARNING = "referrersEarning";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SETJOINREFERRALCOMMISSIONRATE = "setJoinReferralCommissionRate";

    public static final String FUNC_SETUPDATEREFERRERTIMELOCK = "setUpdateReferrerTimelock";

    public static final String FUNC_UNLOCKUPDATEREFERRER = "unlockUpdateReferrer";

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

    public static final Event SETUPDATEREFERRERTIMELOCK_EVENT = new Event("SetUpdateReferrerTimelock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
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

    public List<SetUpdateReferrerTimelockEventResponse> getSetUpdateReferrerTimelockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SETUPDATEREFERRERTIMELOCK_EVENT, transactionReceipt);
        ArrayList<SetUpdateReferrerTimelockEventResponse> responses = new ArrayList<SetUpdateReferrerTimelockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SetUpdateReferrerTimelockEventResponse typedResponse = new SetUpdateReferrerTimelockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.timelock = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SetUpdateReferrerTimelockEventResponse> setUpdateReferrerTimelockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SetUpdateReferrerTimelockEventResponse>() {
            @Override
            public SetUpdateReferrerTimelockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SETUPDATEREFERRERTIMELOCK_EVENT, log);
                SetUpdateReferrerTimelockEventResponse typedResponse = new SetUpdateReferrerTimelockEventResponse();
                typedResponse.log = log;
                typedResponse.timelock = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SetUpdateReferrerTimelockEventResponse> setUpdateReferrerTimelockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SETUPDATEREFERRERTIMELOCK_EVENT));
        return setUpdateReferrerTimelockEventFlowable(filter);
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

    public RemoteFunctionCall<Boolean> firstPurchase(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FIRSTPURCHASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getEarning(String _user) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETEARNING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple4<String, BigInteger, BigInteger, Boolean>> getReferrer(String _user) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple4<String, BigInteger, BigInteger, Boolean>>(function,
                new Callable<Tuple4<String, BigInteger, BigInteger, Boolean>>() {
                    @Override
                    public Tuple4<String, BigInteger, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, BigInteger, BigInteger, Boolean>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue());
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

    public RemoteFunctionCall<TransactionReceipt> lockUpdateReferrer(String _referrer) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_LOCKUPDATEREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> lockingTimeRemain(String _user) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_LOCKINGTIMEREMAIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteFunctionCall<TransactionReceipt> recordReferralCommission(String _referrer, String _user, BigInteger _commission, Boolean isPurchased) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RECORDREFERRALCOMMISSION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer), 
                new org.web3j.abi.datatypes.Address(160, _user), 
                new org.web3j.abi.datatypes.generated.Uint256(_commission), 
                new org.web3j.abi.datatypes.Bool(isPurchased)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> referrerDetails(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERRERDETAILS, 
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

    public RemoteFunctionCall<String> referrersByUser(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REFERRERSBYUSER, 
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

    public RemoteFunctionCall<TransactionReceipt> setUpdateReferrerTimelock(BigInteger _timelock) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETUPDATEREFERRERTIMELOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_timelock)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unlockUpdateReferrer(String _referrer) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNLOCKUPDATEREFERRER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _referrer)), 
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

    public static class SetUpdateReferrerTimelockEventResponse extends BaseEventResponse {
        public BigInteger timelock;
    }
}
