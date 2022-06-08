package com.ndb.auction.service.user;

import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserDetail;
import com.ndb.auction.models.user.UserReferral;
import com.ndb.auction.models.user.UserReferralEarning;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        User user = userDao.selectById(id);
        UserReferral referral = userReferralDao.selectById(id);
        referral.setRate(tierRate[user.getTierLevel()]);
        return referral;
    }

    public List<UserReferralEarning> earningByReferrer(int userId){
        List<UserReferralEarning> list =new ArrayList<>();
        //ndbCoinService.getUserEarning("0x22a6D404D8cF1effA88fD27D095caD00Ca50190b");
        UserReferral referrer = userReferralDao.selectById(userId);
        List<UserReferral> users = userReferralDao.getAllByReferredByCode(referrer.getReferralCode());
        for (UserReferral item : users){
            String address = item.getWalletConnect();
            String name = userDao.selectById(item.getId()).getName();
            if (!address.isEmpty()){
                long amount = ndbCoinService.getUserEarning(address);
                UserReferralEarning earning = new UserReferralEarning();
                earning.setName(name);
                earning.setAmount(amount);
                list.add(earning);
            }
        }
        return list;
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
