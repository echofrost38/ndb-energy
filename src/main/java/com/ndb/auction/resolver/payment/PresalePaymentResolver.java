package com.ndb.auction.resolver.payment;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.payload.CryptoPayload;
import com.ndb.auction.resolver.BaseResolver;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresalePaymentResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
    
    // create crypto payment
    @PreAuthorize("isAuthenticated()")
    public CryptoPayload createNewPayment(int presaleId, int destination, String address) {
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
        PreSaleOrder presaleOrder = new PreSaleOrder(userId, presaleId, destination, address);
        return presaleOrderService.createNewPresaleOrder(presaleOrder);        
    }

}
