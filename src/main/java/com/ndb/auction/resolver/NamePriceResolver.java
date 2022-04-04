package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.BuyNamePrice;
import com.ndb.auction.service.NamePriceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class NamePriceResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    @Autowired
    private NamePriceService namePriceService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int createNewNamePrice(int chars, double price) {
        var m = new BuyNamePrice(chars, price);
        return namePriceService.insert(m);
    }

    @PreAuthorize("isAuthenticated()")
    public List<BuyNamePrice> getAllBuyNamePrices() {
        return namePriceService.selectAll();
    }

    @PreAuthorize("isAuthenticated()")
    public BuyNamePrice getBuyName(int chars) {
        return namePriceService.select(chars);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int updateNamePrice(int id, int chars, double price) {
        return namePriceService.update(id, chars, price);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int deleteNamePrice(int id) {
        return namePriceService.delete(id);
    }
    
}
