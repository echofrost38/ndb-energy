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

    private int userId;
    private Boolean verification;
    private Double wallet;
    private List<Integer> auctions;
    private Double direct;
    private List<StakeHist> staking;

    public TierTask(int userId) {
        this.wallet = 0.0;
        this.direct = 0.0;
        this.userId = userId;
        this.verification = false;
        this.auctions = new ArrayList<>();
        this.staking = new ArrayList<>();
    }

    public String getAuctionsString() {
        List<String> list=new ArrayList<String>();
        for(Integer i:this.auctions)
            list.add(i.toString());
        return String.join(AUCTION_SEPARATOR, list);
    }

    public void setAuctionsByString(String input) {
        List<Integer> list = new ArrayList<>();
        if(input == null) {
            this.auctions = list;
            return;
        }
        String[] array = input.split(AUCTION_SEPARATOR);
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
