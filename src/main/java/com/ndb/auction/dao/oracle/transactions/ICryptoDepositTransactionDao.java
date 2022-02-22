package com.ndb.auction.dao.oracle.transactions;

import java.util.List;

import com.ndb.auction.models.transactions.CryptoDepositTransaction;

public interface ICryptoDepositTransactionDao {
    public int insertDepositAddress(int id, String address);
    public List<CryptoDepositTransaction> selectByDepositAddress(String depositAddress);
}
