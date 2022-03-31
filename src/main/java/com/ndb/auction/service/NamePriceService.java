package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.dao.oracle.other.BuyNamePriceDao;
import com.ndb.auction.models.BuyNamePrice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NamePriceService {
    @Autowired
    private BuyNamePriceDao namePriceDao;

    public int insert(BuyNamePrice m) {
        BuyNamePrice e = namePriceDao.select(m.getNumOfChars());
        if(e != null) {
            return 0;
        }
        return namePriceDao.insert(m);
    }

    public List<BuyNamePrice> selectAll() {
        return namePriceDao.selectAll();
    }

    public BuyNamePrice select(int chars) {
        return namePriceDao.select(chars);
    }

    public int update(int id, int chars, double price) {
        return namePriceDao.update(id, chars, price);
    }

    public int delete(int id) {
        return namePriceDao.delete(id);
    }
}
