package com.ndb.auction.service.interfaces;

import java.util.List;

import com.ndb.auction.models.AvatarComponent;
import com.ndb.auction.models.AvatarSet;
import com.ndb.auction.models.Bid;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.User;

/**
 * 1. Airdrop Information
 * 2. Deposit & withdraw => payment & withdraw ??????
 * 
 * #1 Verify setup will go to auth service!
 * #2 Connect wallet will go to payment service!  
 * @author klinux
 *
 */

public interface IProfileService {
	/**
	 * Initial loading full user's profile
	 * 1. Bid activity
	 * 2. Wallet ( transaction informations )
	 * 3. Assets ( wallet details )
	 * 4. Market information
	 */
	
	// get user info ( account details, assets, notify setting)
	User getUserProfile(String userId);
	
	// CONFLICTED: get bid activity ( it can get from BidService too )
	List<Bid> getBidActivity(String userId);
	
	// get transaction history
	
	
	// get notifications and notify settings
	List<Notification> getNotifications(String userId);
	Integer getNotifySetting(String userId);
	
	// update notify settings
	Integer updateNotifySetting(String userId, Integer setting);
	
	// change password
	Integer changePassword(String userId, String password);

	// set avatar
	String setAvatar(String id, String prefix, String userName);

	// update avatar set
	AvatarSet updateAvatarSet(String userId, AvatarSet set);

	String purchaseComponent(List<AvatarComponent> components);
	
}
