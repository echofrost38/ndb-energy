package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.dao.oracle.other.TierDao;
import com.ndb.auction.models.tier.Tier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TierService {

    @Autowired
    private TierDao tierDao;

	private List<Tier> tierList;

	private synchronized void fillTierList() {
		this.tierList = tierDao.getUserTiers();
	}

	public Tier addNewUserTier(int level, String name, long point) {
		Tier tier = new Tier(level, name, point);
		tierDao.addNewUserTier(tier);
		fillTierList();
		return tier;
	}

	public Tier updateUserTier(int level, String name, long point) {
		Tier tier = new Tier(level, name, point);
		tierDao.updateUserTier(tier);
		fillTierList();
		return tier;
	}

	public List<Tier> getUserTiers() {
		if(this.tierList == null) {
			fillTierList();
		}
		return this.tierList;
	}

	public int deleteUserTier(int level) {
		int _level = tierDao.deleteUserTier(level);
		fillTierList();
		return _level;
	}

}
