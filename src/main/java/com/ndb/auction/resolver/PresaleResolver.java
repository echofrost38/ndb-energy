package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleCondition;
import com.ndb.auction.models.presale.PreSaleOrder;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PresaleResolver extends BaseResolver implements GraphQLQueryResolver, GraphQLMutationResolver{
    
    // create new presale round
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int createNewPresale(
        int round,
        Long startedAt,
        Long endedAt,
        Long tokenAmount,
        Long tokenPrice,
        List<PreSaleCondition> conditions
    ) {
        PreSale presale = new PreSale(round, startedAt, endedAt, tokenAmount, tokenPrice, conditions);
        return presaleService.createNewPresale(presale);
    }

    @PreAuthorize("isAuthenticated()")
    public List<PreSale> getPreSales() {
        return presaleService.getPresales();
    }

    @PreAuthorize("isAuthenticated()")
    public List<PreSale> getPreSaleByStatus(int status) {
        List<PreSale> presales = presaleService.getPresaleByStatus(status);
        for (PreSale preSale : presales) {
            List<PreSaleCondition> conditions = presaleService.getConditionsById(preSale.getId());
            preSale.setConditions(conditions);
        }   
        return presales;
    }

    /// PreSaleOrder
    @PreAuthorize("isAuthenticated()")
    public PreSaleOrder placePreSaleOrder(int presaleId, int destination, String extAddr) {
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
        PreSaleOrder presaleOrder = new PreSaleOrder(userId, presaleId, destination, extAddr);
        return presaleOrderService.placePresaleOrder(presaleOrder);
    }

}
