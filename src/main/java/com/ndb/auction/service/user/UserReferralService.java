package com.ndb.auction.service.user;

import com.ndb.auction.exceptions.ReferralException;
import com.ndb.auction.models.referral.ActiveReferralResponse;
import com.ndb.auction.models.referral.UpdateReferralAddressResponse;
import com.ndb.auction.models.user.User;
import com.ndb.auction.models.user.UserReferral;
import com.ndb.auction.models.user.UserReferralEarning;
import com.ndb.auction.models.wallet.NyyuWallet;
import com.ndb.auction.service.BaseService;
import com.ndb.auction.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        if (referrer!=null) {
            List<UserReferral> users = userReferralDao.getAllByReferredByCode(referrer.getReferralCode());
            for (UserReferral item : users) {
                UserReferralEarning earning = new UserReferralEarning();
                String name = userAvatarDao.selectById(item.getId()).getName();
                if (name !=null) {
                    String address= item.getWalletConnect();

                    if (address==null) {
                        NyyuWallet nyyuWallet = nyyuWalletDao.selectByUserId(item.getId());
                        if (nyyuWallet!=null) address= nyyuWallet.getPublicKey();
                    }

                    if (address != null) {
                        double amount = ndbCoinService.getUserEarning(address);
                        earning.setAmount(amount);
                    }
                    earning.setName(name);
                    list.add(earning);
                }
            }
        }
        return list;
    }

    public int insert(UserReferral m) {
        return userReferralDao.insert(m);
    }

    public void handleReferralOnPreSaleOrder(int userId,String wallet){
        try {
            UserReferral buyerReferral = userReferralDao.selectById(userId);
            String referredByCode = buyerReferral.getReferredByCode();
            if (referredByCode == null) return;
            UserReferral referrer = userReferralDao.selectByReferralCode(referredByCode);
            if (ndbCoinService.isReferralRecorded(wallet, referrer.getWalletConnect())) return;
            String oldWallet = buyerReferral.getWalletConnect();
            if (oldWallet != null){
                if (ndbCoinService.isReferralRecorded(oldWallet,referrer.getWalletConnect())) {
                    userReferralService.updateReferrerAddress(userId, wallet);
                    return;
                }
            } else if (!ndbCoinService.isActiveReferrer(wallet)) {
                User user = userDao.selectById(userId);
                int rate = tierRate[user.getTierLevel()];
                ndbCoinService.activeReferrer(wallet, (double) rate);
            }
            ndbCoinService.recordReferral(wallet, referrer.getWalletConnect());
        } catch(Exception ex){
            throw new ReferralException(ex.getMessage());
        }
    }

    public boolean updateReferralOnPresaleOrder(int userId,String wallet){
        try {
            UserReferral invited = userReferralDao.selectById(userId);
            String referredByCode = invited.getReferredByCode();
            if (referredByCode == null) return false;
            else {
                User user = userDao.selectById(userId);
                int rate = tierRate[user.getTierLevel()];
                if (!ndbCoinService.isActiveReferrer(wallet)) {
                    ndbCoinService.activeReferrer(wallet, (double) rate);
                }
                UserReferral referrer = userReferralDao.selectByReferralCode(referredByCode);
                if (!ndbCoinService.isReferralRecorded(wallet,referrer.getWalletConnect()))
                    ndbCoinService.recordReferral(wallet, referrer.getWalletConnect());
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    public UserReferral createNewReferrer(int userId,String referredByCode, String walletAddress){
        try {
            referredByCode = (referredByCode!=null) ? referredByCode: "";
            //active referrer use NYYU wallet
            if (!ndbCoinService.isActiveReferrer(walletAddress)){
                User user = userDao.selectById(userId) ;
                int rate = tierRate[user.getTierLevel()];
                ndbCoinService.activeReferrer(walletAddress, (double) rate);
            }

            // update database
            UserReferral referral = new UserReferral();
            referral.setId(userId);
            referral.setWalletConnect(walletAddress);
            referral.setReferralCode(generateCode());
            referral.setReferredByCode(referredByCode);
            userReferralDao.insert(referral);
            if (!referredByCode.isEmpty() && userReferralDao.existsUserByReferralCode(referredByCode)==1){
                //record referral
                String referrerWallet;
                UserReferral referrer = userReferralDao.selectByReferralCode(referredByCode);
                if (referrer!=null){
                    if (referrer.getWalletConnect()!=null)
                        referrerWallet = referrer.getWalletConnect();
                    else {
                        NyyuWallet nyyuWallet = nyyuWalletDao.selectByUserId(userId);
                        referrerWallet = nyyuWallet.getPublicKey();
                    }
                    ndbCoinService.recordReferral(walletAddress,referrerWallet);
                }
            }

            return referral;
        } catch (Exception e){
            throw new ReferralException(e.getMessage());
        }
    }

    public ActiveReferralResponse activateReferralCode(int userId, String wallet) {
        try {
            if (wallet.equals("0x0000000000000000000000000000000000000000"))
            {
                NyyuWallet nyyuWallet = nyyuWalletDao.selectByUserId(userId);
                if (nyyuWallet!=null)
                    wallet = nyyuWallet.getPublicKey();
                else
                    wallet = nyyuWalletService.generateBEP20Address(userId);
            }
            UserReferral guestUser = userReferralDao.selectById(userId);
            if (guestUser==null) {
                guestUser = new UserReferral();
                guestUser.setId(userId);
                guestUser.setReferralCode(generateCode());
            }
            User user = userDao.selectById(userId) ;
            int rate = tierRate[user.getTierLevel()];
            //set active referrer
            if (!ndbCoinService.isActiveReferrer(wallet)){
                ndbCoinService.activeReferrer(wallet, (double) rate);
            }
            //update database

            guestUser.setWalletConnect(wallet);
            userReferralDao.insertOrUpdate(guestUser);

            ActiveReferralResponse response = new ActiveReferralResponse();
            response.setCode(guestUser.getReferralCode());
            response.setReferralWallet(wallet);
            response.setRate(rate);
            response.setCommissionRate(tierRate);
            return response;
        }catch (Exception e){
            throw new ReferralException(e.getMessage());
        }
    }

    // update new wallet for user or referrer
    public UpdateReferralAddressResponse updateReferrerAddress(int userId, String current){
        try {
            UserReferral referrer = userReferralDao.selectById(userId);
            if (referrer == null){
                String msg = messageSource.getMessage("no_referral", null, Locale.ENGLISH);
                throw new ReferralException(msg);
            }
            int lockTime = ndbCoinService.lockingTimeRemain(referrer.getWalletConnect());
            if (lockTime > 0) {
                int p1 = lockTime % 60;
                int p2 = lockTime / 60;
                int p3 = p2 % 60;
                p2 = p2 / 60;

                Object[] params = new Object[]{
                        String.join(" ",String.valueOf(p2), p2 == 1 || p2 == 0 ? "hour" : "hours" ),
                        String.join(" ",String.valueOf(p3), p3 == 1 || p3 == 0 ? "minute" : "minutes" ),
                        String.join(" ",String.valueOf(p1), p1 == 1 || 1 == 0 ? "second" : "seconds" )
                };//
                String msg = messageSource.getMessage("limit_timelock", params, Locale.ENGLISH);
                throw new ReferralException(msg);
            }

            if (current.equals("0x0000000000000000000000000000000000000000")){
                NyyuWallet  nyyuWallet =  nyyuWalletDao.selectByUserId(userId);
                if (nyyuWallet == null )
                    current = nyyuWalletService.generateBEP20Address(userId);
                else
                    current = nyyuWallet.getPublicKey();
            }

            UpdateReferralAddressResponse response = new UpdateReferralAddressResponse();
            response.setReferralWallet(current);
            if (!current.equals(referrer.getWalletConnect()))
            {
                if (referrer.getWalletConnect()==null){
                    User user = userDao.selectById(userId) ;
                    if (!ndbCoinService.isActiveReferrer(current)){
                        int rate = tierRate[user.getTierLevel()];
                        ndbCoinService.activeReferrer(current, (double) rate);
                        referrer.setWalletConnect(current);
                        userReferralDao.update(referrer);
                        response.setStatus(true);
                        return response;
                    }
                } else {
                    String hash = ndbCoinService.updateReferrer(referrer.getWalletConnect(), current);
                    if (!hash.isEmpty()) {
                        referrer.setWalletConnect(current);
                        userReferralDao.update(referrer);
                        referrer.setWalletConnect(current);
                        userReferralDao.insertOrUpdate(referrer);
                        response.setStatus(true);
                        return response;
                    }
                }
            }

            User user = userDao.selectById(userId);
            response.setRate(tierRate[user.getTierLevel()]);
            response.setCommissionRate(tierRate);
            response.setStatus(false);
            return response;
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

    public int checkTimeLock(int userId){
        UserReferral referrer = userReferralDao.selectById(userId);
        int lockTime = ndbCoinService.lockingTimeRemain(referrer.getWalletConnect());
        return  lockTime;
    }
    private String generateCode() {
        String generated = "";
        do {
            generated = stringGenerator.generate();
        } while (userReferralDao.existsUserByReferralCode(generated)==1);

        return generated;
    }
}
