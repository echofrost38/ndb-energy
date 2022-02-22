package com.ndb.auction.dao.oracle.transactions;

import java.util.List;

import com.ndb.auction.models.transactions.Transaction;

public interface ITransactionDao {
    public int insert(Transaction m);
    public List<Transaction> selectAll(String orderBy); 
    public List<Transaction> selectByUser(int userId, String orderBy);
    public int update(int id, int status);
}
