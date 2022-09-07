package com.ndb.auction.resolver.payment.presale;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.transactions.wallet.NyyuWalletTransaction;
import com.ndb.auction.models.user.User;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.payment.wallet.NyyuWalletTransactionService;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PresaleWallet extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    private final NyyuWalletTransactionService nyyuWalletTxnService;

    @PreAuthorize("isAuthenticated()")
    public String payWalletForPresale(int presaleId, int orderId, String cryptoType) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        User user = userService.getUserById(userId);

        // getting presale 
        PreSale presale = presaleService.getPresaleById(presaleId);
        if(presale == null) {
            String msg = messageSource.getMessage("no_presale", null, Locale.ENGLISH);
            throw new AuctionException(msg, "presaleId");
        }

        if(presale.getStatus() != PreSale.STARTED) {
            String msg = messageSource.getMessage("not_started", null, Locale.ENGLISH);
            throw new AuctionException(msg, "presaleId");
        }

        // get presale order
        PreSaleOrder order = presaleOrderService.getPresaleById(orderId);
        if(order == null) {
            String msg = messageSource.getMessage("no_order", null, Locale.ENGLISH);
            throw new AuctionException(msg, "presaleId");
        }

        if(order.getStatus() != 0) {
            String msg = messageSource.getMessage("presale_processed", null, Locale.ENGLISH);
            throw new BidException(msg, "order");
        }

        // get amount 
        double totalOrder = 0.0;
		double tierFeeRate = txnFeeService.getFee(user.getTierLevel());
        double payAmount = order.getNdbAmount() * order.getNdbPrice();

        var white = whitelistService.selectByUser(userId);
		if(white != null) tierFeeRate = 0.0;

        totalOrder = 100 * payAmount / (100 - tierFeeRate);
        
        // check crypto Type balance
        double cryptoPrice = thirdAPIUtils.getCryptoPriceBySymbol(cryptoType);
        double cryptoAmount = totalOrder / cryptoPrice; // required amount!
        double freeBalance = internalBalanceService.getFreeBalance(userId, cryptoType);
        if(freeBalance < cryptoAmount) {
            String msg = messageSource.getMessage("insufficient", null, Locale.ENGLISH);
            throw new AuctionException(msg, "amount");
        }

        // // deduct free balance
        internalBalanceService.deductFree(userId, cryptoType, cryptoAmount);
        nyyuWalletTxnService.insertNewTransaction(userId, orderId, 1, cryptoAmount, cryptoType);
        internalBalanceService.handlePresaleOrder(userId, 0, totalOrder, "NYYU", order);

        return "Success";
    }

    @PreAuthorize("isAuthenticated()")
    public List<NyyuWalletTransaction> getNyyuWalletTxns() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return nyyuWalletTxnService.getNyyuWalletTxnsByUserId(userId);
    }

    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<NyyuWalletTransaction> getNyyuWalletTxnsByUserId(int userId) {
        return nyyuWalletTxnService.getNyyuWalletTxnsByUserId(userId);
    }
    
    @PreAuthorize("hasRole('ROLE_SUPER')")
    public List<NyyuWalletTransaction> getNyyuWalletTxnsByOrderId(int orderId, int orderType) {
        return nyyuWalletTxnService.getNyyuWalletTxnsByOrderId(orderId, orderType);
    }
}
