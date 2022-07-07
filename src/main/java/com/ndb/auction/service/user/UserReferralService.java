package com.ndb.auction.service.user;

import com.ndb.auction.exceptions.ReferralException;
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
        // only debug ndbCoinService.lockingTimeRemain("0xc4dCf172654bc858ef03EE190Ec49c6011297738");
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
            UserReferral newUser = userReferralDao.selectById(userId);
            String referredByCode = newUser.getReferredByCode();
            if (referredByCode == null) return;
            UserReferral referrer = userReferralDao.selectByReferralCode(referredByCode);
            if (ndbCoinService.isReferralRecorded(wallet, referrer.getWalletConnect())) return;
            String oldWallet = newUser.getWalletConnect();
            if (oldWallet != null){
                if (ndbCoinService.isReferralRecorded(oldWallet,referrer.getWalletConnect())) {
                    int lockTime = ndbCoinService.lockingTimeRemain(oldWallet);
                    if (lockTime > 0) {
                        int p1 = lockTime % 60;
                        int p2 = lockTime / 60;
                        int p3 = p2 % 60;
                        p2 = p2 / 60;
                        throw new ReferralException("Lock in " + p2 + ":" + p3 + ":" + p1);
                    }
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

    public String activateReferralCode(int userId,String wallet) {
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
            //set active referrer
            if (!ndbCoinService.isActiveReferrer(wallet)){
                User user = userDao.selectById(userId) ;
                int rate = tierRate[user.getTierLevel()];
                ndbCoinService.activeReferrer(wallet, (double) rate);
            }
            //update database

            guestUser.setWalletConnect(wallet);
            userReferralDao.insertOrUpdate(guestUser);

            return guestUser.getReferralCode();
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
                    referrer.setWalletConnect(current);
                    userReferralDao.insertOrUpdate(referrer);
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
