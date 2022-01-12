package com.ndb.auction.background;

import java.util.List;

import com.ndb.auction.dao.oracle.other.NotificationDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.MailService;
import com.ndb.auction.service.SMSService;

import org.springframework.beans.factory.annotation.Autowired;

public class BroadcastNotification implements BackgroundTask {
    
    @Autowired
    private UserDao userDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private SMSService smsService;

    @Autowired
    private MailService mailService;

    private Notification notification;

    public BroadcastNotification(Notification notification) {
        this.notification = notification;
    }
    
    @Override
    public void runTask() {
        // Get All users
        List<User> userList = userDao.selectAll("ID");
        
        String title = notification.getTitle();
        String msg = notification.getMsg();
        
        for (User user : userList) {
            if((user.getNotifySetting() & 0x01 << notification.getNType()) == 0)
                continue;
            notification.setUserId(user.getId());
            notificationDao.addNewNotification(notification);
            
            // send SMS, Email, Notification here
            try {
                smsService.sendNormalSMS(user.getPhone(), title + "\n" + msg);
            } catch (Exception e) {
            }

            try {
                mailService.sendNormalEmail(user, title, msg);
            } catch (Exception e) {
            }

        }
    }

}
