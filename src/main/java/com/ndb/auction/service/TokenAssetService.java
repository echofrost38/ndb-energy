package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ndb.auction.dao.oracle.TokenAssetDao;
import com.ndb.auction.models.TokenAsset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenAssetService {
    
    @Autowired
    private TokenAssetDao tokenAssetDao;

    private List<TokenAsset> assetList;
    private Map<String, Integer> assetMap;
    private Map<Integer, TokenAsset> assetIdMap;

    public int getTokenIdBySymbol(String symbol) {
        if(assetMap == null) {
            fillList();
        }
        return assetMap.get(symbol);
    }

    public TokenAsset getTokenAssetById(int id) {
        if(assetIdMap == null) {
            fillList();
        }
        return assetIdMap.get(id);
    }

    private synchronized void fillList() {
        if(assetList == null) {
            assetList = new ArrayList<>();
            assetMap = new HashMap<>();
            assetIdMap = new HashMap<>();
        }
        
        this.assetList.clear();
        this.assetList = tokenAssetDao.selectAll(null);
        this.assetMap.clear();
        for (TokenAsset tokenAsset : assetList) {
            assetMap.put(tokenAsset.getTokenSymbol(), tokenAsset.getId());
            assetIdMap.put(tokenAsset.getId(), tokenAsset);
        }
    }

    public int createNewTokenAsset(TokenAsset tokenAsset) {
        int result = tokenAssetDao.insert(tokenAsset);
        this.fillList();
        return result;
    }

    public List<TokenAsset> getAllTokenAssets(String orderBy) {
        if(this.assetList == null) {
            fillList();
        }
        return this.assetList;
    }

    public int deleteTokenAsset(int id) {
        int result = tokenAssetDao.updateDeleted(id);
        this.fillList();
        return result;
    }
}
