package com.ndb.auction.service.user;

import com.ndb.auction.exceptions.ReferralException;
import com.ndb.auction.models.user.User;
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
        referral.setCommissionRate(tierRate);
        return referral;
    }

    public List<UserReferralEarning> earningByReferrer(int userId){
        List<UserReferralEarning> list =new ArrayList<>();
        UserReferral referrer = userReferralDao.selectById(userId);
        List<UserReferral> users = userReferralDao.getAllByReferredByCode(referrer.getReferralCode());
        for (UserReferral item : users){
            String address = item.getWalletConnect();
            String name = userDao.selectById(item.getId()).getName();
            if (address!=null){
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

    public UserReferral createNewReferrer(int userId,String referredByCode){
        try {
            referredByCode = (referredByCode!=null) ? referredByCode: "";
            // update database
            UserReferral referral = new UserReferral();
            referral.setId(userId);
            referral.setReferralCode(generateCode());
            if (!referredByCode.isEmpty() && userReferralDao.existsUserByReferralCode(referredByCode)==1)
                referral.setReferredByCode(referredByCode);
            else
                referral.setReferredByCode("");

            userReferralDao.insert(referral);
            return referral;
        } catch (Exception e){
            throw new ReferralException(e.getMessage());
        }
    }

    public String activateReferralCode(int userId,String wallet) {
        try {
            UserReferral guestUser = userReferralDao.selectById(userId);
            if (guestUser==null){
                guestUser = new UserReferral();
                //set active referrer
                if (!ndbCoinService.isActiveReferrer(wallet)){
                    User user = userDao.selectById(userId) ;
                    int rate = tierRate[user.getTierLevel()];
                    ndbCoinService.activeReferrer(wallet, (double) rate);
                }
                //update database
                guestUser.setId(userId);
                guestUser.setReferralCode(generateCode());
                guestUser.setWalletConnect(wallet);
                userReferralDao.insert(guestUser);

                return guestUser.getReferralCode();
            }
            return "";
        }catch (Exception e){
            throw new ReferralException(e.getMessage());
        }
    }

    // update new wallet for user or referrer
    public boolean updateReferrerAddress(int userId, String current){
        try {
            UserReferral referrer = userReferralDao.selectById(userId);
            if (referrer.getWalletConnect()==null){
                User user = userDao.selectById(userId) ;
                if (!ndbCoinService.isActiveReferrer(current)){
                    int rate = tierRate[user.getTierLevel()];
                    ndbCoinService.activeReferrer(current, (double) rate);
                    referrer.setWalletConnect(current);
                    userReferralDao.update(referrer);
                    return true;
                }
            } else {
                String hash = ndbCoinService.updateReferrer(referrer.getWalletConnect(), current);
                if (!hash.isEmpty()) {
                    referrer.setWalletConnect(current);
                    userReferralDao.update(referrer);
                    return true;
                }
            }
            return false;
        } catch (Exception e){
            throw new ReferralException(e.getMessage());

        }
    }

    public boolean updateCommissionRate(int userId, int tierLevel){
        try {
            UserReferral referrer = userReferralDao.selectById(userId);
            User user = userDao.selectById(referrer.getId()) ;
            if (tierLevel > user.getTierLevel()) {
                int rate = tierRate[tierLevel];
                if (!referrer.getWalletConnect().isEmpty() && rate > 0) {
                    String hash = ndbCoinService.updateReferrerRate(referrer.getWalletConnect(), (double) rate);
                    if (!hash.isEmpty()) return true;
                }
            }
            return false;
        }catch(Exception e){
            throw new ReferralException(e.getMessage());
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
