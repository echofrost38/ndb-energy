package com.ndb.auction.service;

import com.ndb.auction.dao.oracle.other.TierTaskDao;
import com.ndb.auction.dao.oracle.other.TierTaskStakeDao;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TierTaskService {

	@Autowired
	private TierTaskDao tierTaskDao;

	@Autowired
	private TierTaskStakeDao tierTaskStakeDao;

	public TierTask updateTierTask(TierTask tierTask) {
		tierTaskDao.insertOrUpdate(tierTask);
		tierTaskStakeDao.updateAll(tierTask.getUserId(), tierTask.getStaking());
		return tierTask;
	}

	public TierTask getTierTask(int userId) {
		TierTask tierTask = tierTaskDao.selectByUserId(userId);

		if(tierTask == null) return null;

		tierTask.setStaking(tierTaskStakeDao.selectAll(userId));
		return tierTask;
	}
}
