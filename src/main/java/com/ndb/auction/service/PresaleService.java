package com.ndb.auction.service;

import java.util.List;
import java.util.Locale;

import com.ndb.auction.exceptions.PreSaleException;
import com.ndb.auction.exceptions.UnauthorizedException;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.presale.PreSale;
import com.ndb.auction.models.presale.PreSaleCondition;

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
            String msg = messageSource.getMessage("no_presale", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "presale");
        }

        presales = presaleDao.selectByStatus(PreSale.COUNTDOWN);
        if(presales.size() != 0) {
            String msg = messageSource.getMessage("already_in_action", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "presale");
        }

        // check date
        Long currentTime = System.currentTimeMillis();
        if(currentTime > presale.getStartedAt()) {
            String msg = messageSource.getMessage("invalid_time", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "presale");
        }

        if(presale.getStartedAt() > presale.getEndedAt()) {
            String msg = messageSource.getMessage("invalid_time", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "presale");
        }

        // check auction round
        List<Auction> auctions = auctionDao.getAuctionByStatus(Auction.STARTED);
        if(auctions.size() != 0) {
            String msg = messageSource.getMessage("already_in_action", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "auction");
        }
        auctions = auctionDao.getAuctionByStatus(Auction.COUNTDOWN);
        if(auctions.size() != 0) {
            String msg = messageSource.getMessage("already_in_action", null, Locale.ENGLISH);
            throw new UnauthorizedException(msg, "auction");
        }

        presale = presaleDao.insert(presale);
        int count = presale.getConditions().size();
        if(count != presaleConditionDao.insertConditionList(presale.getConditions())){
            return 0;
        }

        schedule.setPresaleCountdown(presale);
        return 1;
    }

    public int startPresale(int presaleId) {
        notificationService.broadcastNotification(
            Notification.NEW_ROUND_STARTED, 
            "NEW ROUND STARTED", 
            "PreSale Round has been started.");
        return presaleDao.updateStatus(presaleId, PreSale.STARTED);
    }

    public int getNewRound() {
        return presaleDao.getNewRound();
    }

    public int closePresale(int presaleId) {
        notificationService.broadcastNotification(
            Notification.ROUND_FINISHED, 
            "ROUND CLOSED", 
            "PreSale Round has been closed.");
        return presaleDao.updateStatus(presaleId, PreSale.ENDED);
    }

    public PreSale getPresaleById(int presaleId) {
        return presaleDao.selectById(presaleId);
    }

    public List<PreSale> getPresaleByStatus(int status) {
        return presaleDao.selectByStatus(status);
    }

    public List<PreSale> getPresales() {
        return presaleDao.selectAll();
    }

    public int addSoldAmount(int presaleId, double sold) {
        return presaleDao.updateSold(presaleId, sold);
    }

    public List<PreSaleCondition> getConditionsById(int presaleId) {
        return presaleConditionDao.selectById(presaleId);
    }
    
}
