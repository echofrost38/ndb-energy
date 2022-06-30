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
public class NDBreferral extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50611e6e806100206000396000f3fe608060405234801561001057600080fd5b506004361061018e5760003560e01c806379547628116100de5780639691b12d11610097578063c12a50c611610071578063c12a50c614610421578063c606339f14610467578063daa80bb31461047a578063e06cf687146104a357600080fd5b80639691b12d146103e8578063a2f08224146103fb578063b3162faa1461040e57600080fd5b8063795476281461038e57806379ba5097146103a15780637c24543d146103a95780638129fc1c146103bc5780638b3f577f146103c45780638da5cb5b146103d757600080fd5b806352c043c11161014b578063695c2f4e11610125578063695c2f4e146103345780636d44a3b2146103475780636d70f7ae1461035a578063715018a61461038657600080fd5b806352c043c1146102e15780635bf11d4a146102f4578063602b386e1461031457600080fd5b80630c7f7b6b1461019357806313e7c9d8146101a85780631b4a3ebe146101e05780634a3b68cc1461021a5780634a9fefc71461025b5780634fdac60f146102ce575b600080fd5b6101a66101a1366004611ab5565b6104b6565b005b6101cb6101b6366004611aee565b60026020526000908152604090205460ff1681565b60405190151581526020015b60405180910390f35b61020c6101ee366004611aee565b6001600160a01b031660009081526005602052604090205460ff1690565b6040519081526020016101d7565b610243610228366004611aee565b6003602052600090815260409020546001600160a01b031681565b6040516001600160a01b0390911681526020016101d7565b6102a6610269366004611aee565b6001600160a01b0380821660009081526003602090815260408083205490931680835260059091529190205460065460ff91821691169193909250565b604080516001600160a01b039094168452602084019290925260ff16908201526060016101d7565b6101a66102dc366004611aee565b610721565b6101a66102ef366004611aee565b610779565b61020c610302366004611aee565b60046020526000908152604090205481565b610327610322366004611aee565b6107ed565b6040516101d79190611b0b565b6101a6610342366004611b58565b610866565b6101a6610355366004611b7f565b6108cc565b6101cb610368366004611aee565b6001600160a01b031660009081526002602052604090205460ff1690565b6101a661094a565b6101a661039c366004611bad565b6109ca565b6101a6610a4e565b6101a66103b7366004611c00565b610b43565b6101a6610c3b565b6101a66103d2366004611ab5565b610c92565b6000546001600160a01b0316610243565b6101a66103f6366004611c35565b6113f0565b6101a6610409366004611c00565b611509565b6101a661041c366004611c76565b6115ff565b61044e61042f366004611aee565b6005602052600090815260409020805460019091015460ff9091169082565b6040805160ff90931683526020830191909152016101d7565b61020c610475366004611aee565b61166b565b61020c610488366004611aee565b6001600160a01b031660009081526004602052604090205490565b6101a66104b1366004611aee565b6116c6565b3360009081526002602052604090205460ff166104ee5760405162461bcd60e51b81526004016104e590611c91565b60405180910390fd5b6001600160a01b0382166105445760405162461bcd60e51b815260206004820152601960248201527f5f7573657220697320746865207a65726f20616464726573730000000000000060448201526064016104e5565b6001600160a01b03811661056a5760405162461bcd60e51b81526004016104e590611cd5565b806001600160a01b0316826001600160a01b0316036105cb5760405162461bcd60e51b815260206004820152601b60248201527f5f7573657220697320657175616c20746f205f7265666572726572000000000060448201526064016104e5565b6001600160a01b0382811660009081526003602052604090205416156106335760405162461bcd60e51b815260206004820152601f60248201527f5f757365722068617320616c7265616479206265656e2072656665727265640060448201526064016104e5565b6001600160a01b03811660009081526005602052604090205460ff1661069b5760405162461bcd60e51b815260206004820152601760248201527f5265666572726572206e6f74206163746976652079657400000000000000000060448201526064016104e5565b6001600160a01b03808216600081815260056020908152604080832060020180546001810182559084528284200180549588166001600160a01b0319968716811790915580845260039092528083208054909516841790945592519192917ff61ccbe316daff56654abed758191f9a4dcac526d43747a50a2d545c0ca64d829190a35050565b3360009081526002602052604090205460ff166107505760405162461bcd60e51b81526004016104e590611c91565b60075461075d9042611d22565b6001600160a01b03909116600090815260086020526040902055565b6000546001600160a01b031633146107a35760405162461bcd60e51b81526004016104e590611d3a565b600180546001600160a01b0319166001600160a01b0383169081179091556040517f906a1c6bd7e3091ea86693dd029a831c19049ce77f1dce2ce0bab1cacbabce2290600090a250565b6001600160a01b03811660009081526005602090815260409182902060020180548351818402810184019094528084526060939283018282801561085a57602002820191906000526020600020905b81546001600160a01b0316815260019091019060200180831161083c575b50505050509050919050565b6000546001600160a01b031633146108905760405162461bcd60e51b81526004016104e590611d3a565b60078190556040518181527f1089e4cd6526fcaeb2e85095ac6583ff99fbe58b548c89f488fdd4dc9033371c906020015b60405180910390a150565b6000546001600160a01b031633146108f65760405162461bcd60e51b81526004016104e590611d3a565b6001600160a01b038216600081815260026020526040808220805460ff191685151590811790915590519092917f966c160e1c4dbc7df8d69af4ace01e9297c3cf016397b7914971f2fbfa32672d91a35050565b6000546001600160a01b031633146109745760405162461bcd60e51b81526004016104e590611d3a565b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319908116909155600180549091169055565b6000546001600160a01b031633146109f45760405162461bcd60e51b81526004016104e590611d3a565b610a086001600160a01b038416828461170f565b604080518381526001600160a01b03831660208201527f605022943465a74fc0f43f0f842fd8163e45145ade2fea43728ab758e317ba6d910160405180910390a1505050565b6001546001600160a01b03163314610adf5760405162461bcd60e51b815260206004820152604860248201527f596f75206d757374206265206e6f6d696e6174656420617320706f74656e746960448201527f616c206f776e6572206265666f726520796f752063616e20616363657074206f6064820152670776e6572736869760c41b608482015260a4016104e5565b600154600080546040516001600160a01b0393841693909116917fb532073b38c83145e3e5135377a08bf9aab55bc0fd7c1179cd4fb995d2a5159c91a360018054600080546001600160a01b03199081166001600160a01b03841617909155169055565b3360009081526002602052604090205460ff16610b725760405162461bcd60e51b81526004016104e590611c91565b6001600160a01b038216610b985760405162461bcd60e51b81526004016104e590611cd5565b60008160ff1611610bda5760405162461bcd60e51b815260206004820152600c60248201526b52617465206973207a65726f60a01b60448201526064016104e5565b6001600160a01b038216600081815260056020908152604091829020805460ff191660ff861690811790915591519182527f719a65f5451c353953daae53c6a31ed24315a4f03b27b41f346172c7f125afca91015b60405180910390a25050565b60008054336001600160a01b0319909116811782556006805460ff1916600a17905561a8c0600755604051909182917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a350565b3360009081526002602052604090205460ff16610cc15760405162461bcd60e51b81526004016104e590611c91565b6001600160a01b03821660009081526008602052604090205482901580610d0057506001600160a01b0381166000908152600860205260409020544210155b610d4c5760405162461bcd60e51b815260206004820152601d60248201527f5265666572726572205570646174652069732074696d656c6f636b656400000060448201526064016104e5565b816001600160a01b0316836001600160a01b031603610dad5760405162461bcd60e51b815260206004820152601960248201527f5f6f6c6420697320657175616c20746f205f63757272656e740000000000000060448201526064016104e5565b6001600160a01b038316610e035760405162461bcd60e51b815260206004820181905260248201527f6e657720726566657272657220697320746865207a65726f206164647265737360448201526064016104e5565b6001600160a01b038216610e655760405162461bcd60e51b8152602060048201526024808201527f63757272656e7420726566657272657220697320746865207a65726f206164646044820152637265737360e01b60648201526084016104e5565b6001600160a01b03821660009081526005602052604090205460ff1615610ece5760405162461bcd60e51b815260206004820152601860248201527f5f63757272656e7420726566657272657220657869737473000000000000000060448201526064016104e5565b6001600160a01b03821660009081526005602090815260408083208151606081018352815460ff168152600182015481850152600282018054845181870281018701865281815292959394860193830182828015610f5557602002820191906000526020600020905b81546001600160a01b03168152600190910190602001808311610f37575b505050919092525050506001600160a01b03851660009081526005602090815260408083208151606081018352815460ff16815260018201548185015260028201805484518187028101870186528181529798509596919592949386019391929091830182828015610ff057602002820191906000526020600020905b81546001600160a01b03168152600190910190602001808311610fd2575b50505091909252505081519192505060ff1615611118576001600160a01b03808616600090815260056020526040808220928716825290208154815460ff191660ff90911617815560018083015490820155600280830180546110569284019190611a1a565b505050604082015160005b81518110156110de57856003600084848151811061108157611081611d6f565b60200260200101516001600160a01b03166001600160a01b0316815260200190815260200160002060006101000a8154816001600160a01b0302191690836001600160a01b031602179055506110d78160010190565b9050611061565b506001600160a01b0386166000908152600560205260408120805460ff1916815560018101829055906111146002830182611a6a565b5050505b6001600160a01b038581166000908152600360205260409020541615611393576001600160a01b03808616600090815260036020908152604080832054909316808352600582528383208451606081018652815460ff1681526001820154818501526002820180548751818702810187018952818152949792959394928601938301828280156111d157602002820191906000526020600020905b81546001600160a01b031681526001909101906020018083116111b3575b5050509190925250505060408101519091506000805b825181101561123257896001600160a01b031683828151811061120c5761120c611d6f565b60200260200101516001600160a01b03160361122a57809150611232565b6001016111e7565b506001600160a01b038416600090815260056020526040902060020180548991908390811061126357611263611d6f565b9060005260206000200160006101000a8154816001600160a01b0302191690836001600160a01b03160217905550600460008a6001600160a01b03166001600160a01b0316815260200190815260200160002054600460008a6001600160a01b03166001600160a01b031681526020019081526020016000208190555083600360008a6001600160a01b03166001600160a01b0316815260200190815260200160002060006101000a8154816001600160a01b0302191690836001600160a01b03160217905550600360008a6001600160a01b03166001600160a01b0316815260200190815260200160002060006101000a8154906001600160a01b030219169055600460008a6001600160a01b03166001600160a01b0316815260200190815260200160002060009055505050505b6007546113a09042611d22565b6001600160a01b038086166000818152600860205260408082209490945592519092918816917fc40302e3b5897f6966b131753cb09f65aa712ae82e3f49b189d089d5694256e391a35050505050565b3360009081526002602052604090205460ff1661141f5760405162461bcd60e51b81526004016104e590611c91565b6001600160a01b0383161580159061143f57506001600160a01b03821615155b801561144b5750600081115b15611504576001600160a01b0383166000908152600560205260409020600101546114769082611761565b6001600160a01b038085166000908152600560209081526040808320600101949094559185168152600490915220546114af9082611761565b6001600160a01b038084166000908152600460209081526040918290209390935551838152908516917f91badd4ef769cf56b7db0b350b95c9fb6d973e6e37d28a51fed219cc7d53184f910160405180910390a25b505050565b3360009081526002602052604090205460ff166115385760405162461bcd60e51b81526004016104e590611c91565b6001600160a01b03821661155e5760405162461bcd60e51b81526004016104e590611cd5565b60008160ff16116115a05760405162461bcd60e51b815260206004820152600c60248201526b52617465206973207a65726f60a01b60448201526064016104e5565b6001600160a01b0382166000818152600560209081526040808320805460ff191660ff87169081178255600190910193909355519182527fb2a60f125657306a9287a8eafa2344a7fe2976d44c293ddbb26f032992c5549c9101610c2f565b6000546001600160a01b031633146116295760405162461bcd60e51b81526004016104e590611d3a565b6006805460ff191660ff83169081179091556040519081527f161ac3d6084899bc7d75453abbf3ccc9a2977f8ffdc6d75a337d1251725bdb58906020016108c1565b3360009081526002602052604081205460ff1661169a5760405162461bcd60e51b81526004016104e590611c91565b6001600160a01b0382166000908152600860205260409020546116be904290611d85565b90505b919050565b3360009081526002602052604090205460ff166116f55760405162461bcd60e51b81526004016104e590611c91565b6001600160a01b0316600090815260086020526040812055565b604080516001600160a01b038416602482015260448082018490528251808303909101815260649091019091526020810180516001600160e01b031663a9059cbb60e01b1790526115049084906117c7565b60008061176e8385611d22565b9050838110156117c05760405162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f77000000000060448201526064016104e5565b9392505050565b600061181c826040518060400160405280602081526020017f5361666542455032303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b03166118999092919063ffffffff16565b805190915015611504578080602001905181019061183a9190611d9c565b6115045760405162461bcd60e51b815260206004820152602a60248201527f5361666542455032303a204245503230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b60648201526084016104e5565b60606118a884846000856118b0565b949350505050565b6060824710156119115760405162461bcd60e51b815260206004820152602660248201527f416464726573733a20696e73756666696369656e742062616c616e636520666f6044820152651c8818d85b1b60d21b60648201526084016104e5565b6001600160a01b0385163b6119685760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e747261637400000060448201526064016104e5565b600080866001600160a01b031685876040516119849190611de9565b60006040518083038185875af1925050503d80600081146119c1576040519150601f19603f3d011682016040523d82523d6000602084013e6119c6565b606091505b50915091506119d68282866119e1565b979650505050505050565b606083156119f05750816117c0565b825115611a005782518084602001fd5b8160405162461bcd60e51b81526004016104e59190611e05565b828054828255906000526020600020908101928215611a5a5760005260206000209182015b82811115611a5a578254825591600101919060010190611a3f565b50611a66929150611a8b565b5090565b5080546000825590600052602060002090810190611a889190611a8b565b50565b5b80821115611a665760008155600101611a8c565b6001600160a01b0381168114611a8857600080fd5b60008060408385031215611ac857600080fd5b8235611ad381611aa0565b91506020830135611ae381611aa0565b809150509250929050565b600060208284031215611b0057600080fd5b81356117c081611aa0565b6020808252825182820181905260009190848201906040850190845b81811015611b4c5783516001600160a01b031683529284019291840191600101611b27565b50909695505050505050565b600060208284031215611b6a57600080fd5b5035919050565b8015158114611a8857600080fd5b60008060408385031215611b9257600080fd5b8235611b9d81611aa0565b91506020830135611ae381611b71565b600080600060608486031215611bc257600080fd5b8335611bcd81611aa0565b9250602084013591506040840135611be481611aa0565b809150509250925092565b803560ff811681146116c157600080fd5b60008060408385031215611c1357600080fd5b8235611c1e81611aa0565b9150611c2c60208401611bef565b90509250929050565b600080600060608486031215611c4a57600080fd5b8335611c5581611aa0565b92506020840135611c6581611aa0565b929592945050506040919091013590565b600060208284031215611c8857600080fd5b6117c082611bef565b60208082526024908201527f4f70657261746f723a2063616c6c6572206973206e6f7420746865206f70657260408201526330ba37b960e11b606082015260800190565b6020808252601d908201527f5f726566657272657220697320746865207a65726f2061646472657373000000604082015260600190565b634e487b7160e01b600052601160045260246000fd5b60008219821115611d3557611d35611d0c565b500190565b6020808252818101527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604082015260600190565b634e487b7160e01b600052603260045260246000fd5b600082821015611d9757611d97611d0c565b500390565b600060208284031215611dae57600080fd5b81516117c081611b71565b60005b83811015611dd4578181015183820152602001611dbc565b83811115611de3576000848401525b50505050565b60008251611dfb818460208701611db9565b9190910192915050565b6020815260008251806020840152611e24816040850160208701611db9565b601f01601f1916919091016040019291505056fea264697066735822122007f511b99a1717c58b32205c4b8cef40218a51cd77ed3bc5406baba36090559664736f6c634300080d0033";

    public static final String FUNC_ACCEPTOWNERSHIP = "acceptOwnership";

    public static final String FUNC_ACTIVEREFERRER = "activeReferrer";

    public static final String FUNC_DRAINBEP20TOKEN = "drainBEP20Token";

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

    public static final String FUNC_REFERREDUSERS = "referredUsers";

    public static final String FUNC_REFERRERS = "referrers";

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
    protected NDBreferral(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NDBreferral(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected NDBreferral(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected NDBreferral(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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
    public static NDBreferral load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NDBreferral(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static NDBreferral load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NDBreferral(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static NDBreferral load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new NDBreferral(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static NDBreferral load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new NDBreferral(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<NDBreferral> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NDBreferral.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NDBreferral> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NDBreferral.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<NDBreferral> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NDBreferral.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NDBreferral> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NDBreferral.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
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
