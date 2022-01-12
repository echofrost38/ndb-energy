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

import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

@Component
public class NotificationService {

    final Sinks.Many<Notification> sink;

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

    public NotificationService() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * create new payment and check confirm status
     * 
     * @param type   : Notification Type
     * @param title  : Notification Title
     * @param msg    : Notification Content
     * @param userId : Receiver ID
     * @return void
     */
    public void send(Integer type, String title, String msg, int userId) {

        Notification notification = new Notification(userId, type, title, msg);
        notification.setUserId(userId);
        // notification.setBroadcast(false);
        notificationDao.addNewNotification(notification);

        // Publish
        EmitResult result = sink.tryEmitNext(notification);

        if (result.isFailure()) {
            // do something here, since emission failed
            // log.info("Send to {} message is failed!", userId);
        }
    }

    public List<NotificationType> getAllNotificationTypes() {
        return notificationTypeDao.getAllNotificationTypes();
    }

    public String addNotificationType(String name) {
        return notificationTypeDao.create(name);
    }

    public int deleteNotificationType(int id) {
        return notificationTypeDao.deleteById(id);
    }

    public NotificationType updateNotificationType(Integer id, String name) {
        NotificationType notificationType = new NotificationType();
        notificationType.setId(id);
        notificationType.setTName(name);
        return notificationTypeDao.updateNotificationType(notificationType);
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
        addNewNotification(userId, type, title, msg);

        User user = userDao.selectById(userId);

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
