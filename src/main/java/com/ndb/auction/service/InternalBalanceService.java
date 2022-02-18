package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.models.TokenAsset;
import com.ndb.auction.models.balance.CryptoBalance;
import com.ndb.auction.payload.BalancePayload;

import org.springframework.stereotype.Service;

@Service
public class InternalBalanceService extends BaseService {
    
    // getting balances
    public List<BalancePayload> getInternalBalances(int userId) {
        List<CryptoBalance> iBalances = balanceDao.selectByUserId(userId, null);
        List<BalancePayload> balanceList = new ArrayList<>();
        for (CryptoBalance balance : iBalances) {
            TokenAsset asset = tokenAssetService.getTokenAssetById(balance.getTokenId());
            BalancePayload b = new BalancePayload(asset.getTokenName(), asset.getTokenSymbol(), asset.getSymbol(), balance.getFree(), balance.getHold());
            balanceList.add(b);
        }
        return balanceList;
    } 

    public CryptoBalance getBalance(int userId, String symbol) {
        int tokenId = tokenAssetService.getTokenIdBySymbol(symbol);
        return balanceDao.selectById(userId, tokenId);
    }

    public int addFreeBalance(int userId, String cryptoType, Double amount) {
        int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
        return balanceDao.addFreeBalance(userId, tokenId, amount);
    }

    public int addHoldBalance(int userId, String cryptoType, Double amount) {
        int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
        return balanceDao.addHoldBalance(userId, tokenId, amount);
    }

    public int deductFree(int userId, String cryptoType, Double amount) {
        int tokenId = tokenAssetService.getTokenIdBySymbol(cryptoType);
        return balanceDao.deductFreeBalance(userId, tokenId, amount);
    }

}
