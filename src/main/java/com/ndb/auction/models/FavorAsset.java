package com.ndb.auction.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavorAsset extends BaseModel {
    
    public FavorAsset() {
        this.assets = new ArrayList<>();
    }

    public FavorAsset(int userId, String raw) {
        this.userId = userId;
        String[] assetList = raw.split(",");
        this.assets = Arrays.asList(assetList);
    }

    private int userId;
    private List<String> assets;
}
