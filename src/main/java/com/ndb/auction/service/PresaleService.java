package com.ndb.auction.service;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.models.presale.PreSale;

import org.springframework.stereotype.Service;

@Service
public class PresaleService extends BaseService {
    
    // create new presale
    public int createNewPresale(PreSale presale) {
        PreSale prev = presaleDao.selectByRound(presale.getRound());
        if(prev != null) {
            throw new PreSaleException(String.format("Presale round %d already exists.", presale.getRound()), String.valueOf(presale.getRound()));
        }
        // check date
        Long currentTime = System.currentTimeMillis();
        if(currentTime > presale.getStartedAt()) {
            throw new PreSaleException("Presale start time invalid", "started at");
        }

        if(presale.getStartedAt() > presale.getEndedAt()) {
            throw new PreSaleException("Presale end time invalid", "ended at");
        }

        int result = presaleDao.insert(presale);
        int count = presale.getConditions().size();
        if(count != presaleConditionDao.insertConditionList(presale.getConditions())){
            return 0;
        }
        return result;
    }
    
}
