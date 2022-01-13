package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.dao.oracle.other.TaskSettingDao;
import com.ndb.auction.dao.oracle.other.TaskSettingStakeDao;
import com.ndb.auction.dao.oracle.other.TaskSettingWalletDao;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.StakeTask;
import com.ndb.auction.models.tier.WalletTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskSettingService {

    @Autowired
    private TaskSettingDao taskSettingDao;

	@Autowired
	private TaskSettingStakeDao stakeTaskDao;

	@Autowired
	private TaskSettingWalletDao walletTaskDao;

    private TaskSetting taskSetting;

	private synchronized void fillTaskSetting() {
		this.taskSetting = taskSettingDao.getTaskSettings();
		this.taskSetting.setStaking(stakeTaskDao.selectAll());
		this.taskSetting.setWallet(walletTaskDao.selectAll());
	}
	
	public TaskSetting updateTaskSetting(TaskSetting setting) {
		taskSettingDao.updateSetting(setting);
		List<WalletTask> walletTask = setting.getWallet();
		if(walletTask != null && walletTask.size() != 0) {
			walletTaskDao.updateAll(walletTask);
		}

		List<StakeTask> stakeTask = setting.getStaking();
		if(stakeTask != null && stakeTask.size() != 0) {
			stakeTaskDao.updateAll(stakeTask);
		}
		
		fillTaskSetting();
		return this.taskSetting;
	}

	public TaskSetting getTaskSetting() {
		if(this.taskSetting == null) {
			fillTaskSetting();
		}
		return this.taskSetting;
	}

}
