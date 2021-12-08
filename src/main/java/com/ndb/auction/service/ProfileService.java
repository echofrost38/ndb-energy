package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ndb.auction.exceptions.AvatarNotFoundException;
import com.ndb.auction.exceptions.BidException;
import com.ndb.auction.exceptions.UserNotFoundException;
import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarProfile;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.User;
import com.ndb.auction.models.user.Wallet;
import com.ndb.auction.service.interfaces.IProfileService;

/**
 * 1. get market data
 * 2. get avatar ? / set avatar!!!!!
 * 3. Airdrop where to go???????????
 * @author klinux
 *
 */

@Service
public class ProfileService extends BaseService implements IProfileService {

	// actually return user it self!!!!
	@Override
	public User getUserProfile(String userId) {
		return userDao.getUserById(userId);
	}

	@Override
	public List<Notification> getNotifications(String userId) {
		return notificationDao.getNotificationsByUser(userId);
	}

	@Override
	public Integer getNotifySetting(String userId) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			throw new UserNotFoundException("We were unable to find a user id", "userId");
		}
	
		return user.getNotifySetting();
	}

	@Override
	public Integer updateNotifySetting(String userId, Integer setting) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			throw new UserNotFoundException("We were unable to find a user id", "userId");
		}		
		user.setNotifySetting(setting);
		userDao.updateUser(user);
		return setting;
	}

	@Override
	public Integer changePassword(String userId, String password) {
		
		return null;
	}

	@Override
	public List<Bid> getBidActivity(String userId) {
		return bidDao.getBidListByUser(userId);
	}

	/**
	 * prefix means avatar first name!
	 * once user select avatar with first name, user will have owned components 
	 */
	@Override
	public String setAvatar(String id, String prefix, String name) {
		// check user exists
		User user = userDao.getUserByAvatar(prefix, name);
		if(user != null) {
			return "Already exists";
		}
		
		user = userDao.getUserById(id);
		if(user == null) {
			throw new UserNotFoundException("We were unable to find a user with avatar", "name");
		}
		
		// update purchase list and user avatar set!!
		AvatarProfile profile = avatarDao.getAvatarProfileByName(prefix);
		List<AvatarSet> sets = profile.getAvatarSet();
		List<AvatarComponent> components = avatarDao.getAvatarComponentsBySet(sets);
		Map<String, List<String>> purchasedMap = user.getAvatarPurchase();
		for (AvatarComponent comp : components) {
			String group = comp.getGroupId();
			List<String> purchased = purchasedMap.get(group);
			if(purchased == null) {
				purchased = new ArrayList<String>();
				purchasedMap.put(group, purchased);
			}
			purchased.add(comp.getCompId());
		}
		user.setAvatarPurchase(purchasedMap);
		user.setAvatar(sets);
		user.setAvatarPrefix(prefix);
		user.setAvatarName(name);
		userDao.updateUser(user);
		return "Success";
	}

	@Override
	public List<AvatarSet> updateAvatarSet(String userId, List<AvatarSet> set) {
		User user = userDao.getUserById(userId);
		if(user == null) {
			throw new UserNotFoundException("We were unable to find a user id", "userId");
		}
		double totalPrice = 0.0;
		double price = 0.0;
		String groupId = "";
		String compId = "";
		List<AvatarComponent> purchasedComponents = new ArrayList<AvatarComponent>();

		
		for (AvatarSet avatarSet : set) {
			groupId = avatarSet.getGroupId();
			compId = avatarSet.getCompId();
			AvatarComponent component = avatarDao.getAvatarComponent(groupId, compId);
			if(component == null) {
				throw new AvatarNotFoundException("Cannot find avatar component.", "compId");
			}

			// check free
			price = component.getPrice();
			if(price == 0) {
				continue;
			}

			// check purchased
			Map<String, List<String>> purchasedMap = user.getAvatarPurchase();
			List<String> purchaseList = purchasedMap.get(groupId);
			if(purchaseList == null) {
				purchaseList = new ArrayList<String>();
				purchasedMap.put(groupId, purchaseList);
			}
			
			if(purchaseList.contains(compId)) {
				continue;
			}

			// check remained
			if(component.getLimited() <= component.getPurchased()) {
				return null;
			}

			totalPrice += component.getPrice();
			component.increasePurchase();
			purchaseList.add(component.getCompId());
			purchasedComponents.add(component);
		}

		// check user's NDB wallet 
		Wallet ndbWallet = userWalletService.getWalletById(userId, "NDB");
		double balance = ndbWallet.getFree();
		if(balance < totalPrice) {
			throw new BidException("You don't have enough balance in wallet.", "set");
		}
		ndbWallet.setFree(balance - totalPrice);
		
		
		user.setAvatar(set);
		userDao.updateUser(user);

		// update component purchased ?
		avatarDao.updateAvatarComponents(purchasedComponents);

		return set;
	}

	@Override
	public String purchaseComponent(List<AvatarComponent> components) {
		
		return null;
	}

}
