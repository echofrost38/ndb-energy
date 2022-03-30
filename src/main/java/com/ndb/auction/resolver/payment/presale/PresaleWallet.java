package com.ndb.auction.resolver.payment.presale;

import com.ndb.auction.exceptions.AuctionException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.user.User;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresaleWallet extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    @PreAuthorize("isAuthenticated()")
    public String payWalletForPresale(int presaleId, int orderId, String cryptoType) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        User user = userService.getUserById(userId);

        // getting presale 
        PreSale presale = presaleService.getPresaleById(presaleId);
        if(presale == null) {
            throw new AuctionException("There is no presale.", "presaleId");
        }

        if(presale.getStatus() != PreSale.STARTED) {
            throw new AuctionException("Presale is not started.", "presaleId");
        }

        // get presale order
        PreSaleOrder order = presaleOrderService.getPresaleById(orderId);
        if(order == null) {
            throw new AuctionException("There is no presale order.", "presaleId");
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
        if(freeBalance < cryptoAmount) throw new AuctionException("insufficient", "amount");

        // deduct free balance
        internalBalanceService.deductFree(userId, cryptoType, cryptoAmount);
        stripeBaseService.handlePresaleOrder(userId, order);

        return "Success";
    }
}
