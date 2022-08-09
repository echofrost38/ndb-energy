package com.ndb.auction.service;

import com.ndb.auction.dao.oracle.ServerMaintenanceDao;
import com.ndb.auction.models.Auction;
import com.ndb.auction.models.Bid;
import com.ndb.auction.payload.statistics.RoundChance;
import com.ndb.auction.payload.statistics.RoundPerform1;
import com.ndb.auction.payload.statistics.RoundPerform2;
import com.ndb.auction.utils.SortRoundByNumber;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerManageService extends BaseService {

    private final ServerMaintenanceDao serverMaintenanceDao;

    public String checkMaintenance() {
        return serverMaintenanceDao.check();
    }

}
