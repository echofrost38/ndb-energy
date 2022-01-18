package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.TokenAsset;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class TokenAssetResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver{
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int createTokenAsset(
        String tokenName, 
        String tokenSymbol, 
        String network, 
        String address, 
        String symbol
    ) {
        TokenAsset tokenAsset = new TokenAsset(tokenName, tokenSymbol, network, address, symbol);
        return tokenAssetService.createNewTokenAsset(tokenAsset);
    }

    @PreAuthorize("isAuthenticated()")
    public List<TokenAsset> getTokenAssets(String orderBy) {
        return tokenAssetService.getAllTokenAssets(orderBy);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int deleteTokenAsset(int id) {
        return tokenAssetService.deleteTokenAsset(id);
    }
}
