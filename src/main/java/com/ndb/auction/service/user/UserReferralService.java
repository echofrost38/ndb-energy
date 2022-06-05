package com.ndb.auction.service.user;

import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserReferral;
import com.ndb.auction.service.BaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class UserReferralService extends BaseService {
    @Value("${ndb.referral.commissionRate}")
    private int[] tierRate;

    public UserReferral selectById(int id) {
        return userReferralDao.selectById(id);
    }

    public int insert(UserReferral m) {
        return userReferralDao.insert(m);
    }

    public boolean updateWalletConnect(String referralCode,String wallet) throws Exception {
        try {
            UserReferral guestUser = userReferralDao.selectByReferralCode(referralCode);
            User user = userDao.selectById(guestUser.getId()) ;

            if (!ndbCoinService.isActiveReferrer(wallet)){
                int rate = tierRate[user.getTierLevel()];
                ndbCoinService.activeReferrer(wallet, (double) rate);
            }
            userReferralDao.updateWalletConnect(referralCode, wallet);
            UserReferral referrerUser = userReferralDao.selectByReferralCode(guestUser.getReferredByCode());
            if (!referrerUser.getWalletConnect().isEmpty()){
                ndbCoinService.recordReferral(guestUser.getWalletConnect(),referrerUser.getWalletConnect());
            }
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}
