package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.FiatAsset;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class FiatAssetReolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver {
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int createFiatAsset(
        String fiatName,
        String symbol
    ) {
        FiatAsset asset = new FiatAsset(fiatName, symbol);
        return fiatAssetService.createNewFiatAsset(asset);
    }

    @PreAuthorize("isAuthenticated()")
    public List<FiatAsset> getFiatAssets(String orderBy) {
        return fiatAssetService.getAllFiatAssets(orderBy);
    }

    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // public int deleteTokenAsset(int id) {
    //     return tokenAssetService.deleteTokenAsset(id);
    // }
}
