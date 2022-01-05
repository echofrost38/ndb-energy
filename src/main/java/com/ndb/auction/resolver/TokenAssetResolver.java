package com.ndb.auction.resolver;

import java.util.List;

import com.ndb.auction.models.TokenAsset;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class TokenAssetResolver extends BaseResolver implements GraphQLMutationResolver, GraphQLQueryResolver{
    
    public int createTokenAsset(String tokenName, String tokenSymbol, String network, String address, String symbol) {
        TokenAsset tokenAsset = new TokenAsset(tokenName, tokenSymbol, network, address, symbol);
        return tokenAssetService.createNewTokenAsset(tokenAsset);
    }

    public List<TokenAsset> getTokenAssets(String orderBy) {
        return tokenAssetService.getAllTokenAssets(orderBy);
    }

    public int deleteTokenAsset(int id) {
        return tokenAssetService.deleteTokenAsset(id);
    }
}
