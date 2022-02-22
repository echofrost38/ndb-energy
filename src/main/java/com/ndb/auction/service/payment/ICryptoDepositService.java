package com.ndb.auction.service.payment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.ndb.auction.models.transactions.CryptoDepositTransaction;
import com.ndb.auction.models.transactions.Transaction;

import org.apache.http.client.ClientProtocolException;

public interface ICryptoDepositService {
    public String createNewTransaction(Transaction m) throws UnsupportedEncodingException, ClientProtocolException, IOException;
    public List<CryptoDepositTransaction> selectByDepositAddress(String depositAddress);
}
