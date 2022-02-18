package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ndb.auction.dao.oracle.FiatAssetDao;
import com.ndb.auction.models.FiatAsset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FiatAssetService {
    
    @Autowired
    private FiatAssetDao fiatAssetDao;

    private List<FiatAsset> assetList;
    private Map<String,Integer> assetMap;
    private Map<Integer, FiatAsset> assetIdMap;

    public int getFiatIdByName(String name) {
        if(assetMap == null) {
            fillList();
        }
        return assetMap.get(name);
    }

    public FiatAsset getFiatAssetById(int id) {
        if(assetMap == null) {
            fillList();
        }
        return assetIdMap.get(id);
    }

    public int createNewFiatAsset(FiatAsset fiatAsset) {
        int result = fiatAssetDao.insert(fiatAsset);
        fillList();
        return result;
    }

    public List<FiatAsset> getAllFiatAssets(String orderBy) {
        if(assetList == null) {
            fillList();
        }
        return assetList;
    }

    private synchronized void fillList() {
        if(assetList == null) {
            assetList = new ArrayList<>();
            assetMap = new HashMap<>();
            assetIdMap = new HashMap<>();
        }

        assetList.clear();
        assetList = fiatAssetDao.selectAll(null);
        assetMap.clear();
        for (FiatAsset asset : assetList) {
            assetMap.put(asset.getName(), asset.getId());
            assetIdMap.put(asset.getId(), asset);
        }
    }

}
