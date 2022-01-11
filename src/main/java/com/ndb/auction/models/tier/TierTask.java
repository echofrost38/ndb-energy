package com.ndb.auction.models.tier;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TierTask {

    public static final String AUCTION_SEPARATOR = ",";

    public TierTask(int userId) {
        this.userId = userId;
        this.verification = false;
        this.auctions = new ArrayList<>();
        this.staking = new ArrayList<>();
    }

    private int userId;
    private Boolean verification;
    private long wallet;
    private List<Integer> auctions;
    private long direct;
    private List<StakeHist> staking;

    public void setAuctions(String input) {
        String[] array = input.split(",");
        List<Integer> list = new ArrayList<>();
        for (String s : array) {
            try {
                var value = Integer.parseInt(s);
                list.add(value);
            } catch (Exception e) {
            }
        }
        this.auctions = list;
    }

}
