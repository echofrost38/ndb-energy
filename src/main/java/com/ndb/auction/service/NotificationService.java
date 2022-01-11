package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ndb.auction.dao.oracle.other.NotificationDao;
import com.ndb.auction.dao.oracle.other.NotificationTypeDao;
import com.ndb.auction.dao.oracle.user.UserDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.NotificationType;
import com.ndb.auction.models.NotificationType;
import com.ndb.auction.models.user.User;
import com.ndb.auction.service.user.UserDetailsImpl;

import org.reactivestreams.Publisher;
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
	 * @param type : Notification Type
     * @param title : Notification Title
	 * @param msg : Notification Content
     * @param userId : Receiver ID
	 * @return void
	 */
    public void send(Integer type, String title, String msg, int userId) {
        
        Notification notification = new Notification(type, title, msg);
		notification.setUserId(userId);
        notification.setId(UUID.randomUUID().toString());
        notification.setBroadcast(false);

        // Save in DB
        notificationDao.pushNewNotification(notification);
        
        // Publish
        EmitResult result = sink.tryEmitNext(notification);

        if (result.isFailure()) {
            // do something here, since emission failed
//            log.info("Send to {} message is failed!", userId);
        }
    }

    /**
	 * create new payment and check confirm status
	 * @param type : Notification Type
     * @param title : Notification Title
	 * @param msg : Notification Content
	 * @return void
	 */
    public void broadcast(Integer type, String title, String msg) {
        
        Notification notification = new Notification(type, title, msg);
		notification.setId(UUID.randomUUID().toString());
        notification.setBroadcast(true);

        List<Notification> notifications = new ArrayList<Notification>();

        List<User> userlist = userDao.selectAll(null);
        for (User user : userlist) {
            Notification n = new Notification(notification);
            n.setUserId(user.getId());

            // Here, check if user allowed this notification.
            if(user.allowNotification(notification))
                notifications.add(n);
        }
        
        // Save in DB
        notificationDao.pushNewNotifications(notifications);

        // Publish
        EmitResult result = sink.tryEmitNext(notification);

        if (result.isFailure()) {
            // do something here, since emission failed
//            log.info("Broadcast message is failed!");
        }
    }

    public Publisher<Notification> getNotificationPublisher() {
        
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
        User user = userDao.selectById(userId);

        return sink.asFlux().filter(p -> {
            // log.info("notification userid : {}, current userid : {}", p.getUserId(), userid);
            // return p.getUserId().equals(userid);
            return p.isBroadcast() 
                ? user.allowNotification(p)
                : p.getUserId()==userId;
        }); 
    }

    public List<NotificationType> getAllNotificationTypes() {
        return notificationTypeDao.getAllNotificationTypes();
    }

    public String addNotificationType(String name) {
        return notificationTypeDao.create(name);
    }
    
    public List<NotificationType> deleteNotificationType(Integer id) {
        return notificationTypeDao.deleteById(id);
    }

    public NotificationType updateNotificationType(Integer id, String name) {
        NotificationType notificationType = new NotificationType();
        notificationType.setId(id);
        notificationType.setName(name);
        return notificationTypeDao.updateNotificationType(notificationType);
    }
 
    public Notification setNotificationRead(String nId) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();

        return notificationDao.setReadStatus(userId, nId);
    }

    public List<Notification> getAllUnReadNotifications() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		int userId = userDetails.getId();
        return notificationDao.getAllUnReadNotificationsByUser(userId);
    }

    //////////////////////// version 2 ///////////////////////////

    public void sendNotification(int userId, int type, String title, String msg) {
        addNewNotification(userId, type, title, msg);

        User user = userDao.selectById(userId);

        // send SMS, Email, Notification here
        try {
            smsService.sendNormalSMS(user.getPhone(), title + "\n" + msg);
        } catch (Exception e) {}
       
        try {
			mailService.sendNormalEmail(user, title, msg);
		} catch (Exception e) {}	
    }


    public Notification addNewNotification(int userId, int type, String title, String msg) {
        Notification notification = new Notification(userId, type, title, msg);
        return notificationDao.addNewNotification(notification);
    }

<<<<<<< HEAD
    public String addNewBroadcast(int type, String title, String msg) {
        List<User>userlist = userDao.getUserList();

        List<Notification2> notifications = new ArrayList<Notification2>();
        for (User user : userlist) {
            Notification2 notification2 = new Notification2(user.getId(), type, title, msg);
            notifications.add(notification2);
        }
        notificationDao.pushNewBroadcast(notifications);
        return "Success";
    }

    public List<Notification2> getPaginatedNotifications(String userId, Long stamp, int limit) {
=======
    public List<Notification> getPaginatedNotifications(int userId, Long stamp, int limit) {
>>>>>>> 85500e0f38e673d00d75cf89a1586e0abc2e4b62
        if(stamp == null) {
            return notificationDao.getFirstNotificationsByUser(userId, limit);
        }
        return notificationDao.getMoreNotificationsByUser(userId, stamp, limit);
    }

    public Notification setNotificationReadFlag(int userId, Long stamp) {
        Notification notify = notificationDao.getNotification2(userId, stamp);
        return notificationDao.setReadFlag(notify);
    }

    public String setNotificationReadFlagAll(int userId) {
        return notificationDao.setReadFlagAll(userId);
    }

    public List<Notification> getUnreadNotification2s(int userId) {
        return notificationDao.getUnreadNotifications(userId);
    }

    public NotificationType addNewNotificationType(int nType, String tName, boolean broadcast) {
        NotificationType type2 = new NotificationType(nType, tName, broadcast);
        notificationTypeDao.addNewNotificationType(type2);
        return type2;
    }


    public List<NotificationType> getNotificationTypes() {
        return notificationTypeDao.getNotificationTypes();
    }


}
