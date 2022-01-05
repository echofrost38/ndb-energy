package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.dao.TierDao;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.UserTier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TierService {

    @Autowired
    private TierDao tierDao;

    private TaskSetting taskSetting;
	private List<UserTier> tierList;

    // User Tier
	private synchronized void fillTierList() {
		this.tierList = tierDao.getUserTiers();
	}

	public UserTier addNewUserTier(int level, String name, double points) {
		UserTier tier = new UserTier(level, name, points);
		tierDao.addNewUserTier(tier);
		fillTierList();
		return tier;
	}

	public UserTier updateUserTier(int level, String name, double points) {
		UserTier tier = new UserTier(level, name, points);
		tierDao.updateUserTier(tier);
		fillTierList();
		return tier;
	}

	public List<UserTier> getUserTiers() {
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

	// Task Setting
	private synchronized void fillTaskSetting() {
		this.taskSetting = tierDao.getTaskSettings();
	}
	
	public TaskSetting addNewSetting(TaskSetting setting) {
		tierDao.addNewSetting(setting);
		fillTaskSetting();
		return this.taskSetting;
	}

	public TaskSetting updateTaskSetting(TaskSetting setting) {
		tierDao.updateSetting(setting);
		fillTaskSetting();
		return this.taskSetting;
	}

	public TaskSetting getTaskSetting() {
		if(this.taskSetting == null) {
			fillTaskSetting();
		}
		return this.taskSetting;
	}

	// Tier Task
	public TierTask createNewTierTask(TierTask tierTask) {
		return tierDao.createNewTask(tierTask);
	}

	public TierTask updateTierTask(TierTask tierTask) {
		return tierDao.updateTierTask(tierTask);
	}

	public TierTask getTierTask(String userId) {
		return tierDao.getTierTask(userId);
	}
}
