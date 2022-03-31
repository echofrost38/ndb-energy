package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresaleOrderResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    /// PreSaleOrder
    @PreAuthorize("isAuthenticated()")
    public PreSaleOrder placePreSaleOrder(int presaleId, Long ndbAmount, int destination, String extAddr) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();

        // check presale status
        PreSale presale = presaleService.getPresaleById(presaleId);
        if(presale == null) {
            throw new PreSaleException("no_presale", "presaleId");
        }

        if(presale.getStatus() != PreSale.STARTED) {
            throw new PreSaleException("not_started", "presaleId");
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

}
