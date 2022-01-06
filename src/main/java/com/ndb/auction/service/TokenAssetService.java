package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.models.TokenAsset;

import org.springframework.stereotype.Service;

@Service
public class TokenAssetService extends BaseService {
    
    public int createNewTokenAsset(TokenAsset tokenAsset) {
        return tokenAssetDao.insert(tokenAsset);
    }

    public List<TokenAsset> getAllTokenAssets(String orderBy) {
        return tokenAssetDao.selectAll(orderBy);
    }

    public int deleteTokenAsset(int id) {
        return tokenAssetDao.updateDeleted(id);
    }
}
