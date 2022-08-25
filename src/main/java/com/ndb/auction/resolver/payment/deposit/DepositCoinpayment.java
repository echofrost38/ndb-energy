package com.ndb.auction.resolver.payment.deposit;

import com.ndb.auction.exceptions.PaymentException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.exceptions.UserSuspendedException;
import com.ndb.auction.models.transactions.coinpayment.CoinpaymentDepositTransaction;
import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.web3.NyyuWalletService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
public class DepositCoinpayment extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {

    @Autowired
    NyyuWalletService nyyuWalletService;
    private static final String DEPOSIT = "DEPOSIT";
    
    // get deposit address 
    @PreAuthorize("isAuthenticated()")
    public CoinpaymentDepositTransaction createChargeForDeposit(String coin, String network, String cryptoType) throws ClientProtocolException, IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        if (userService.isUserSuspended(userId)) {
            throw new UserSuspendedException("User is suspended!");
        }

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if (!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        CoinpaymentDepositTransaction m;
        network = network.equals("ERC20") ? "BEP20" : network;
        if (network.equals("BEP20") || network.equals("TRC20") || network.equals("SOL")) {
            m = new CoinpaymentDepositTransaction(0, userId,  0.0, 0.0, 0.0, DEPOSIT, cryptoType, network, coin);
            NyyuWallet nyyuWallet = nyyuWalletService.selectByUserId(userId, network);
            if (nyyuWallet != null){
                // check it is registered or not
                if(nyyuWallet.getNyyuPayRegistered()) {
                    m.setDepositAddress(nyyuWallet.getPublicKey());
                } else {
                    var address = nyyuWalletService.registerNyyuWallet(nyyuWallet);
                    if(address == null) {
                        String msg = messageSource.getMessage("no_registered_wallet", null, Locale.ENGLISH);
                        throw new PaymentException(msg, "cryptoType");
                    }
                    m.setDepositAddress(address);
                }
            } else {
                var address = nyyuWalletService.generateNyyuWallet(network, userId);
                if(address == null) {
                    String msg = messageSource.getMessage("no_registered_wallet", null, Locale.ENGLISH);
                    throw new PaymentException(msg, "cryptoType");
                }
                m.setDepositAddress(address);
            }
            return m;
        } else {
            m = new CoinpaymentDepositTransaction(0, userId,  0.0, 0.0, 0.0, DEPOSIT, cryptoType, network, coin);
            return coinpaymentWalletService.createNewTransaction(m);
        }
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<CoinpaymentDepositTransaction> getCoinpaymentDepositTx() {
        return coinpaymentWalletService.selectAll(DEPOSIT);
    }

    @PreAuthorize("isAuthenticated()")
    public List<CoinpaymentDepositTransaction> getCoinpaymentDepositTxByUser(int showStatus) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return coinpaymentWalletService.selectByUser(userId, showStatus, DEPOSIT);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<CoinpaymentDepositTransaction> getCoinpaymentDepositTxByAdmin(int userId) {
        return coinpaymentWalletService.selectByUser(userId, 1, DEPOSIT);
    }

    @PreAuthorize("isAuthenticated()")
    public CoinpaymentDepositTransaction getCoinpaymentDepositTxById(int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        var m = coinpaymentWalletService.selectById(id);
        if(m.getUserId() != userId) {
            throw new UnauthorizedException("You have no permission.", "id");
        }
        return m;
    }

    @PreAuthorize("isAuthenticated()")
    public int changeCoinpaymentDepositShowStatus(int id, int showStatus) {
        return coinpaymentWalletService.changeShowStatus(id, showStatus);
    }

}
