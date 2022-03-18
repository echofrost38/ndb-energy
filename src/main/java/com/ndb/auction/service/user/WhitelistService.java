package com.ndb.auction.service.user;

import java.util.List;

import com.ndb.auction.dao.oracle.user.WhitelistDao;
import com.ndb.auction.models.user.Whitelist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WhitelistService {
    @Autowired
    private WhitelistDao whitelistDao;

    public int insert(int userId, String reason) {
        Whitelist m = new Whitelist(userId, reason);
        return whitelistDao.insert(m);
    }

    public Whitelist selectByUser(int userId) {
        return whitelistDao.selectByUserId(userId);
    }

    public int delete(int userId) {
        return whitelistDao.remove(userId);
    }

    public List<Whitelist> selectAll() {
        return whitelistDao.selectAll();
    }
}
