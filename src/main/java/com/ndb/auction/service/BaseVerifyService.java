package com.ndb.auction.service;

import java.util.List;

import com.ndb.auction.models.KYCSetting;

import org.springframework.stereotype.Service;

@Service
public class BaseVerifyService extends BaseService{
    // KYC/AML Limit!
    public int updateKYCSetting(String kind, Double bid, Double direct, Double deposit, Double withdraw) {
        return kycSettingDao.updateKYCSetting(kind, bid, direct, deposit, withdraw);
    }

    public List<KYCSetting> getKYCSetting() {
        return kycSettingDao.getKYCSettings();
    }
}
