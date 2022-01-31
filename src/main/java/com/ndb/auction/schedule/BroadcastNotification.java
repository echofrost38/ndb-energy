package com.ndb.auction.schedule;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.ndb.auction.dao.oracle.other.NotificationDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.service.utils.SMSService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class BroadcastNotification {
    
    private final int QUEUE_SIZE = 20;

    @Autowired
    private UserDao userDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private SMSService smsService;

    @Autowired
    private MailService mailService;

    private Queue<Notification> notifications;

    public BroadcastNotification() {
        this.notifications = new ArrayBlockingQueue<>(QUEUE_SIZE);
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        System.out.println("New Broadcast Added");
    }

    @Scheduled(fixedDelay = 60000)
    public void broadcast() {
        
        if(notifications.size() == 0)
            return;
        System.out.println("In schedule...");
        
        for (Notification notification : notifications) {
            List<User> userList = userDao.selectAll(null);
            
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
            notifications.remove(notification);
        }

    }

}
