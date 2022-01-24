package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.Auction;

import org.springframework.stereotype.Service;

@Service
public class PresaleService extends BaseService {
    
    // create new presale
    public int createNewPresale(PreSale presale) {
        
        PreSale prev = presaleDao.selectByRound(presale.getRound());
        if(prev != null) {
            throw new PreSaleException(String.format("Presale round %d already exists.", presale.getRound()), String.valueOf(presale.getRound()));
        }

        List<PreSale> presales = presaleDao.selectByStatus(PreSale.STARTED);
        if(presales.size() != 0) {
            throw new PreSaleException("opened_presale", "conflict");
        }

        // check date
        Long currentTime = System.currentTimeMillis();
        if(currentTime > presale.getStartedAt()) {
            throw new PreSaleException("Presale start time invalid", "started at");
        }

        if(presale.getStartedAt() > presale.getEndedAt()) {
            throw new PreSaleException("Presale end time invalid", "ended at");
        }

        // check auction round
        List<Auction> auctions = auctionDao.getAuctionByStatus(Auction.STARTED);
        if(auctions.size() != 0) {
            throw new PreSaleException("opened_auction", "conflict");
        }
        auctions = auctionDao.getAuctionByStatus(Auction.COUNTDOWN);
        if(auctions.size() != 0) {
            throw new PreSaleException("countdown_auction", "conflict");
        }

        int result = presaleDao.insert(presale);
        int count = presale.getConditions().size();
        if(count != presaleConditionDao.insertConditionList(presale.getConditions())){
            return 0;
        }
        return result;
    }
    
}
