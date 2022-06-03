package com.ndb.auction.service.withdraw;

import java.util.List;

import com.ndb.auction.dao.oracle.withdraw.TokenDao;
import com.ndb.auction.models.withdraw.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Autowired
    private TokenDao tokenDao;

    // add new token
    public Token addNewToken(String tokenName, String tokenSymbol, String network, String address, boolean withdraw) {
        var token = Token.builder()
            .tokenName(tokenName)
            .tokenSymbol(tokenSymbol)
            .network(network)
            .address(address)
            .withdrawable(withdraw)
            .build();
        return tokenDao.save(token);
    }

    public List<Token> getTokens() {
        return tokenDao.selectAll();
    }
}
