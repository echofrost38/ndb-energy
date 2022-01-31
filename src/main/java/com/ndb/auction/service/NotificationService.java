package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ndb.auction.background.BroadcastNotification;
import com.ndb.auction.background.TaskRunner;
import com.ndb.auction.dao.oracle.other.NotificationDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.user.UserDetailsImpl;
import com.ndb.auction.service.utils.MailService;
import com.ndb.auction.service.utils.SMSService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {

    private Map<String, Integer> typeMap;

    public NotificationService() {
        typeMap = new HashMap<String, Integer>();
        typeMap.put("BID RANKING UPDATED", 0);
        typeMap.put("NEW ROUND STARTED", 1);
        typeMap.put("ROUND FINISHED", 2);
        typeMap.put("BID CLOSED", 3);
        typeMap.put("PAYMENT RESULT", 4);
        typeMap.put("KYC VERIFIED", 5);
        typeMap.put("DEPOSIT SUCCESS", 6);
        typeMap.put("WITHDRAW SUCCESS", 7);
    }

    @Autowired
    public UserDao userDao;

    @Autowired
    public NotificationDao notificationDao;

    @Autowired
    private SMSService smsService;

    @Autowired
    public MailService mailService;

    @Autowired
    private TaskRunner taskRunner;

    public Map<String, Integer> getTypeMap() {
        return this.typeMap;
    } 

    public Notification setNotificationRead(int nId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();

        return notificationDao.setReadFlag(nId, userId);
    }

    public List<Notification> getAllUnReadNotifications() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        int userId = userDetails.getId();
        return notificationDao.getUnreadNotifications(userId);
    }

    public void sendNotification(int userId, int type, String title, String msg) {
        User user = userDao.selectById(userId);

        if((user.getNotifySetting() & (0x01 << type)) == 0) {
            return;
        }

        addNewNotification(userId, type, title, msg);
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

    public void broadcastNotification(int type, String title, String msg) {
        Notification m = new Notification( 0, type, title, msg);
        BroadcastNotification broadcast = new BroadcastNotification(m);
        taskRunner.addNewTask(broadcast);
    }

    public Notification addNewNotification(int userId, int type, String title, String msg) {
        Notification notification = new Notification(userId, type, title, msg);
        return notificationDao.addNewNotification(notification);
    }

    public String addNewBroadcast(int type, String title, String msg) {
        List<User> userlist = userDao.selectAll(null);
        List<Notification> notifications = new ArrayList<>();
        for (User user : userlist) {
            Notification m = new Notification(user.getId(), type, title, msg);
            notifications.add(m);
        }
        notificationDao.pushNewNotifications(notifications);
        return "Success";
    }

    public List<Notification> getPaginatedNotifications(int userId, Integer offset, Integer limit) {
        return notificationDao.getPaginatedNotifications(userId, offset, limit);
    }

    public Notification getNotification(int id) {
        Notification notify = notificationDao.getNotification(id);
        return notify;
    }

    public Notification setNotificationReadFlag(int id) {
        Notification notify = notificationDao.getNotification(id);
        return notificationDao.setReadFlag(notify);
    }

    public String setNotificationReadFlagAll(int userId) {
        return notificationDao.setReadFlagAll(userId);
    }

    public List<Notification> getUnreadNotifications(int userId) {
        return notificationDao.getUnreadNotifications(userId);
    }
}
