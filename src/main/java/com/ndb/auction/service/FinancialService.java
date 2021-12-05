package com.ndb.auction.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.stereotype.Service;

@Service
public class FinancialService extends BaseService {

    private final int INTERNAL = 0;
    private final int EXTERNAL = 1;

    // withdrawal

    // deposit
    
    // direct sale
    public String directSale(String userId, double amount, int whereTo) {
        if (whereTo == INTERNAL) {
            BigInteger _amount = BigDecimal.valueOf(amount).toBigInteger();
            userWalletService.addFreeAmount(userId, "NDB", _amount);
            return "Success";
        } else if (whereTo == EXTERNAL) {
            // external transfer : Call NDB Token contract!
            
            return "Success";
        } else {
            return "failed";
        }
    }
    
}
