package com.ndb.auction.resolver;

import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.models.FavorAsset;
import com.ndb.auction.models.TokenAsset;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public int updateSymbol(int id, String symbol) {
        return tokenAssetService.updateTokenSymbol(id, symbol);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int deleteTokenAsset(int id) {
        return tokenAssetService.deleteTokenAsset(id);
    }

    @PreAuthorize("isAuthenticated()")
    public int updateFavorAssets(String assets) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return tokenAssetService.insertOrUpdate(userId, assets);
    }

    @PreAuthorize("isAuthenticated()")
    public List<String> selectFavorAssets() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        FavorAsset m = tokenAssetService.selectFavorAsset(userId);
        if(m == null) {
            return new ArrayList<String>();
        }
        return m.getAssets();
    }
    
}
