package com.ndb.auction.models;

import java.util.List;

import com.ndb.auction.models.tier.StakeTask;
import com.ndb.auction.models.tier.WalletTask;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskSetting extends BaseModel {

    private double verification;
    private double auction;
    private double direct;
    private List<WalletTask> wallet;
    private List<StakeTask> staking;
}
