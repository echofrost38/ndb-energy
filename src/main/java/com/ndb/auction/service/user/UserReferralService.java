package com.ndb.auction.service.user;

import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserReferral;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class UserReferralService extends BaseService {
    @Autowired
    private final RandomStringGenerator stringGenerator;

    @Value("${ndb.referral.commissionRate}")
    private int[] tierRate;

    public UserReferralService(RandomStringGenerator stringGenerator) {
        this.stringGenerator = stringGenerator;
    }

    public UserReferral selectById(int id) {
        return userReferralDao.selectById(id);
    }

    public int insert(UserReferral m) {
        return userReferralDao.insert(m);
    }

    public boolean createNewReferrer(int userId,String referredByCode){
        try {
            UserReferral referral = new UserReferral();
            referral.setId(userId);
            referral.setReferralCode(generateCode());
            if (!referredByCode.isEmpty() && userReferralDao.existsUserByReferralCode(referredByCode)==1)
                referral.setReferredByCode(referredByCode);
            userReferralDao.insert(referral);
            return true;
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }
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
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean updateReferrerAddress(int userId,String old, String current){
        try {
            UserReferral referrer = userReferralDao.selectById(userId);
            if (referrer.getWalletConnect().equals(old)){
                String hash = ndbCoinService.updateReferrer(old, current);
                if (!hash.isEmpty()){
                    referrer.setWalletConnect(current);
                    userReferralDao.update(referrer);
                    return true;
                }
            }
            return false;
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public boolean updateCommissionRate(int userId){
        try {
            UserReferral referrer = userReferralDao.selectById(userId);
            User user = userDao.selectById(referrer.getId()) ;
            int rate = tierRate[user.getTierLevel()];
            if (!referrer.getWalletConnect().isEmpty() && rate > 0){
                String hash = ndbCoinService.updateReferrerRate(referrer.getWalletConnect(), (double) rate);
                if (!hash.isEmpty()) return true;
            }
            return false;
        }catch(Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }
    }
    private String generateCode() {
        String generated = "";
        do {
            generated = stringGenerator.generate();
        } while (userReferralDao.existsUserByReferralCode(generated)==1);

        return generated;
    }
}
