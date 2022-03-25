package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Bid;
import com.ndb.auction.payload.statistics.RoundChance;
import com.ndb.auction.payload.statistics.RoundPerform1;
import com.ndb.auction.payload.statistics.RoundPerform2;
import com.ndb.auction.utils.SortRoundByNumber;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatService extends BaseService {

    @Autowired
    private SortRoundByNumber sortRound;

    private List<RoundPerform2> roundPerform2List = null;

    private List<RoundChance> roundChanceList = null;

    private List<RoundPerform1> roundPerform1List = null;

    private synchronized void buildRoundStatCache() {
        List<Auction> roundList = auctionDao.getAuctionByStatus(Auction.ENDED);

        int size = roundList.size();
        if (size == 0) {
            this.roundPerform1List = null;
            this.roundPerform2List = null;
            this.roundChanceList = null;
            return;
        }
        Auction roundArray[] = new Auction[size];

        roundList.toArray(roundArray);
        sortRound.mergeSort(roundArray, 0, roundArray.length - 1);

        double min = Double.MAX_VALUE, max = 0, std = 0;
        double win = 0, failed = 0, total = 0, winRate = 0, failedRate = 0;

        StandardDeviation standardDeviation = new StandardDeviation();

        roundPerform1List = new ArrayList<>();
        roundPerform2List = new ArrayList<>();
        roundChanceList = new ArrayList<>();

        for (Auction round : roundArray) {

            // Round Performance 2
            // Round Chance
            int roundId = round.getId();
            int roundNumber = round.getRound();
            List<Bid> bidList = bidDao.getBidListByRound(roundId);
            double[] priceArr = new double[bidList.size()];
            int cnt = 0;
            for (Bid bid : bidList) {
                double tokenPrice = bid.getTokenPrice();
                if (min > tokenPrice)
                    min = tokenPrice;
                if (max < tokenPrice)
                    max = tokenPrice;
                priceArr[cnt] = tokenPrice; // TODO: long -> double

                total += bid.getTokenAmount();
                if (bid.getStatus() == Bid.WINNER) {
                    win += bid.getTokenAmount();
                } else {
                    failed += bid.getTokenAmount();
                }

                cnt++;
            }
            std = standardDeviation.evaluate(priceArr); // TODO: double -> long
            RoundPerform2 roundPerform2 = new RoundPerform2(roundNumber, min, max, std);
            roundPerform2List.add(roundPerform2);

            winRate = total == 0 ? 0 : win / total;
            failedRate = total == 0 ? 0 : failed / total;
            RoundChance roundChance = new RoundChance(roundNumber, winRate, failedRate);
            roundChanceList.add(roundChance);

            RoundPerform1 roundPerform1 = new RoundPerform1(roundNumber, win, round.getSold());
            roundPerform1List.add(roundPerform1);

            total = 0;
            win = 0;
            failed = 0;
        }

    }

    public synchronized void updateRoundCache(int roundId) {
        if (roundChanceList == null) {
            buildRoundStatCache();
        }

        Auction round = auctionDao.getAuctionById(roundId);

        if (round != null) {
            List<Bid> bidList = bidDao.getBidListByRound(roundId);

            double min = Double.MAX_VALUE, max = 0, std = 0;
            StandardDeviation standardDeviation = new StandardDeviation();
            double priceArr[] = new double[bidList.size()];
            int cnt = 0;
            double win = 0, failed = 0, total = 0, winRate = 0, failedRate = 0;
            for (Bid bid : bidList) {

                double tokenPrice = bid.getTokenPrice();
                if (min > tokenPrice)
                    min = tokenPrice;
                if (max < tokenPrice)
                    max = tokenPrice;
                priceArr[cnt] = tokenPrice;
                cnt++;

                total += bid.getTokenAmount();
                if (bid.getStatus() == Bid.WINNER) {
                    win += bid.getTokenAmount();
                } else {
                    failed += bid.getTokenAmount();
                }

            }
            winRate = total == 0 ? 0 : win / total;
            failedRate = total == 0 ? 0 : failed / total;
            RoundChance chance = new RoundChance(round.getRound(), winRate, failedRate);
            roundChanceList.add(chance);

            RoundPerform1 roundPerform1 = new RoundPerform1(round.getRound(), win, round.getSold());
            roundPerform1List.add(roundPerform1);

            std = standardDeviation.evaluate(priceArr); // TODO: double -> long
            RoundPerform2 roundPerform2 = new RoundPerform2(round.getRound(), min, max, std);
            roundPerform2List.add(roundPerform2);
        }
    }

    // Statistics - Round performance 2
    public List<RoundPerform2> getRoundPerform2() {
        if (roundPerform2List == null) {
            buildRoundStatCache();
        }

        if (roundPerform2List == null) {
            return null;
        }

        List<RoundPerform2> list = new ArrayList<RoundPerform2>(this.roundPerform2List);

        List<Auction> rounds = auctionDao.getAuctionByStatus(Auction.STARTED);
        if (rounds.size() == 1) {
            Auction startedRound = rounds.get(0);
            int roundNumber = startedRound.getRound();
            int roundId = startedRound.getId();
            List<Bid> bidList = bidDao.getBidListByRound(roundId);
            double min = Double.MAX_VALUE, max = 0, std = 0;
            StandardDeviation standardDeviation = new StandardDeviation();
            double priceArr[] = new double[bidList.size()];
            int cnt = 0;
            for (Bid bid : bidList) {
                double tokenPrice = bid.getTokenPrice();
                if (min > tokenPrice)
                    min = tokenPrice;
                if (max < tokenPrice)
                    max = tokenPrice;
                priceArr[cnt] = tokenPrice;
                cnt++;
            }
            std = standardDeviation.evaluate(priceArr); // TODO: double -> long
            std = Double.isNaN(std) ? 0 : std;
            RoundPerform2 roundPerform2 = new RoundPerform2(roundNumber, min, max, std);
            list.add(roundPerform2);
        }

        return list;
    }

    // Statistics - Bid performance
    public List<RoundPerform1> getRoundPerform1() {
        if (roundPerform1List == null) {
            buildRoundStatCache();
        }

        if (roundPerform1List == null) {
            return null;
        }

        List<RoundPerform1> list = new ArrayList<>(this.roundPerform1List);

        List<Auction> rounds = auctionDao.getAuctionByStatus(Auction.STARTED);
        if (rounds.size() == 1) {
            Auction startedRound = rounds.get(0);
            int roundNumber = startedRound.getRound();
            List<Bid> bidList = bidDao.getBidListByRound(roundNumber);
            long win = 0;
            for (Bid bid : bidList) {
                if (bid.getStatus() == Bid.WINNER) {
                    win += bid.getTokenAmount();
                }
            }
            RoundPerform1 roundPerform1 = new RoundPerform1(roundNumber, win, startedRound.getSold());
            list.add(roundPerform1);
        }

        return list;
    }

    // Statistics - Round Chance
    public List<RoundChance> getRoundChance() {
        if (roundChanceList == null) {
            buildRoundStatCache();
        }
        if (roundChanceList == null) {
            return null;
        }
        List<RoundChance> list = new ArrayList<>(this.roundChanceList);

        List<Auction> rounds = auctionDao.getAuctionByStatus(Auction.STARTED);
        if (rounds.size() == 1) {
            int roundNumber = rounds.get(0).getRound();

            List<Bid> bidList = bidDao.getBidListByRound(roundNumber);

            double win = 0, failed = 0, total = 0, winRate = 0, failedRate = 0;
            for (Bid bid : bidList) {
                total += bid.getTokenAmount();
                if (bid.getStatus() == Bid.WINNER) {
                    win += bid.getTokenAmount();
                } else {
                    failed += bid.getTokenAmount();
                }

                winRate = total == 0 ? 0 : win / total;
                failedRate = total == 0 ? 0 : failed / total;
                RoundChance chance = new RoundChance(roundNumber, winRate, failedRate);
                list.add(chance);
            }

        }

        return list;
    }

}
