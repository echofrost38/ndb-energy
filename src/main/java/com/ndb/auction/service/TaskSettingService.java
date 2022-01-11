package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.dao.oracle.other.TaskSettingDao;
import com.ndb.auction.dao.oracle.other.TierDao;
import com.ndb.auction.models.TaskSetting;
import com.ndb.auction.models.tier.Tier;
import com.ndb.auction.models.tier.TierTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskSettingService {

    @Autowired
    private TaskSettingDao taskSettingDao;

    private TaskSetting taskSetting;

	private synchronized void fillTaskSetting() {
		this.taskSetting = taskSettingDao.getTaskSettings();
	}
	
	public TaskSetting addNewSetting(TaskSetting setting) {
		taskSettingDao.addNewSetting(setting);
		fillTaskSetting();
		return this.taskSetting;
	}

	public TaskSetting updateTaskSetting(TaskSetting setting) {
		taskSettingDao.updateSetting(setting);
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
