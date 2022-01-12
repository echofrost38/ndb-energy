package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;

import com.ndb.auction.dao.oracle.other.NotificationDao;
import com.ndb.auction.dao.oracle.other.NotificationTypeDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.NotificationType;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {

    @Autowired
    public UserDao userDao;

    @Autowired
    public NotificationDao notificationDao;

    @Autowired
    public NotificationTypeDao notificationTypeDao;

    @Autowired
    private SMSService smsService;

    @Autowired
    public MailService mailService;

    // for notificaion type Cache
    private List<NotificationType> typeList;

    private void clearChache() {
        typeList.clear();
    }

    private synchronized void buildCache() {
        clearChache();
        this.typeList = notificationTypeDao.getAllNotificationTypes();
    }

    public NotificationService() {
        this.typeList = null;
    }

    public List<NotificationType> getAllNotificationTypes() {
        if(this.typeList == null) {
            buildCache();
        }
        return this.typeList;
    }

    public String addNotificationType(String name) {
        notificationTypeDao.create(name);
        buildCache();
        return name;
    }

    public int deleteNotificationType(int id) {
        int result = notificationTypeDao.deleteById(id);
        buildCache();
        return result;
    }

    public NotificationType updateNotificationType(Integer id, String name) {
        NotificationType notificationType = new NotificationType();
        notificationType.setId(id);
        notificationType.setTName(name);
        notificationTypeDao.updateNotificationType(notificationType);
        buildCache();
        return notificationType;
    }

    public NotificationType getNotificationByName(String name) {
        return notificationTypeDao.getNotificationTypeByName(name);
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

    //////////////////////// version 2 ///////////////////////////

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

    public NotificationType addNewNotificationType(int nType, String tName, boolean broadcast) {
        NotificationType type2 = new NotificationType(nType, tName, broadcast);
        notificationTypeDao.addNewNotificationType(type2);
        return type2;
    }

    public List<NotificationType> getNotificationTypes() {
        return notificationTypeDao.getAllNotificationTypes();
    }

}
