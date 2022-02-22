package com.ndb.auction.service.payment;

import java.util.List;

import com.ndb.auction.models.transactions.Transaction;

public interface ITransactionService {
    public List<? extends Transaction> selectAll(String orderBy); 
    public List<? extends Transaction> selectByUser(int userId, String orderBy);
    public Transaction selectById(int id);
    public int update(int id, int status);
}
