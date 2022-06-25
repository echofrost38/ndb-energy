package com.ndb.auction.resolver;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.models.presale.PresaleOrderPayments;
import com.ndb.auction.service.user.UserDetailsImpl;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresaleOrderResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    /// PreSaleOrder
    @PreAuthorize("isAuthenticated()")
    public PreSaleOrder placePreSaleOrder(int presaleId, Long ndbAmount, int destination, String extAddr) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        var kycStatus = shuftiService.kycStatusCkeck(userId);
        if(!kycStatus) {
            String msg = messageSource.getMessage("no_kyc", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "userId");
        }

        // check presale status
        PreSale presale = presaleService.getPresaleById(presaleId);
        if(presale == null) {
            String msg = messageSource.getMessage("no_presale", null, Locale.ENGLISH);
            throw new PreSaleException(msg, "presaleId");
        }

        // check NDB amount
        double remain = presale.getTokenAmount() - presale.getSold();
        
        if(ndbAmount > remain) {
            String msg = messageSource.getMessage("presale_amount_overflow", null, Locale.ENGLISH);
            throw new PreSaleException(msg, "ndbAmount");
        }        

        if(presale.getStatus() != PreSale.STARTED) {
            String msg = messageSource.getMessage("not_started", null, Locale.ENGLISH);
            throw new PreSaleException(msg, "presaleId");
        }

        // create new Presale order
        Double ndbPrice = presale.getTokenPrice();
        PreSaleOrder presaleOrder = new PreSaleOrder(userId, presaleId, ndbAmount, ndbPrice, destination, extAddr);
        return presaleOrderService.placePresaleOrder(presaleOrder);
    }

    @PreAuthorize("isAuthenticated()")
    public List<PreSaleOrder> getPresaleOrders(int presaleId) {
        return presaleOrderService.getPresaleOrders(presaleId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<PreSaleOrder> getPresaleOrdersByUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return presaleOrderService.getPresaleOrdersByUserId(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public PreSaleOrder getPresaleById(int id) {
        return presaleOrderService.getPresaleById(id);
    }

    @PreAuthorize("isAuthenticated()")
    public PresaleOrderPayments getPresaleOrderTransactions(int orderId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        return presaleOrderService.getPaymentsByOrder(userId, orderId);
    }

}
