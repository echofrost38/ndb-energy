package com.ndb.auction.service;

import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.InternalWallet;

public class InternalWalletService extends BaseService {
    // get deposit address
    public InternalWallet getDepositAddress(int userId) {
        return walletDao.selectByUserId(userId);
    }

    // add favourite token
    public int addFavouriteToken(int userId, String token) {
        InternalWallet wallet = walletDao.selectByUserId(userId);
        if(wallet == null) {
            throw new UserNotFoundException("Cannot Find Token Object", "userId");
        }
        String favourites = wallet.getFavouriteTokens();
        favourites += token + ",";
        wallet.setFavouriteTokens(favourites);
        return walletDao.update(wallet);
    }

    // get favorite tokens
    public String getFavouriteTokens(int userId) {
        InternalWallet wallet = walletDao.selectByUserId(userId);
        if(wallet == null) {
            throw new UserNotFoundException("Cannot Find Token Object", "userId");
        }
        return wallet.getFavouriteTokens();
    }
}
