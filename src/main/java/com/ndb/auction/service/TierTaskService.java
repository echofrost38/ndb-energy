package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.dao.oracle.other.TierTaskDao;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TierTaskService {

    @Autowired
    private TierTaskDao tierTaskDao;

	// Tier Task
	public TierTask createNewTierTask(TierTask tierTask) {
		return tierTaskDao.createNewTask(tierTask);
	}

	public TierTask updateTierTask(TierTask tierTask) {
		return tierTaskDao.updateTierTask(tierTask);
	}

	public TierTask getTierTask(int userId) {
		return tierTaskDao.getTierTask(userId);
	}
}
