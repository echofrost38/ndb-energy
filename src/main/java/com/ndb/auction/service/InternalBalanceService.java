package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.models.InternalBalance;
import com.ndb.auction.models.TokenAsset;
import com.ndb.auction.payload.Balance;

import org.springframework.stereotype.Service;

@Service
public class InternalBalanceService extends BaseService {
    
    // getting balances
    public List<Balance> getInternalBalances(int userId) {
        List<InternalBalance> iBalances = balanceDao.selectByUserId(userId, null);
        List<Balance> balanceList = new ArrayList<>();
        for (InternalBalance balance : iBalances) {
            TokenAsset asset = tokenAssetService.getTokenAssetById(balance.getTokenId());
            Balance b = new Balance(asset.getTokenName(), asset.getTokenSymbol(), asset.getSymbol(), balance.getFree(), balance.getHold());
            balanceList.add(b);
        }
        return balanceList;
    } 

    public int addFreeBalance(int userId, String cryptoType, Double amount) {
        int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
        return balanceDao.addFreeBalance(userId, tokenId, amount);
    }

}
