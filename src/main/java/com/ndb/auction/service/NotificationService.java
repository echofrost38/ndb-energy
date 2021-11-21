package com.ndb.auction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ndb.auction.dao.NotificationDao;
import com.ndb.auction.dao.NotificationTypeDao;
import com.ndb.auction.dao.UserDao;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.NotificationType;
import com.ndb.auction.models.User;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

@Slf4j
@Component
public class NotificationService {

    final Sinks.Many<Notification> sink;

    @Autowired
    public UserDao userDao;

    @Autowired
    public NotificationDao notificationDao;

    @Autowired
    public NotificationTypeDao notificationTypeDao;

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
    public void send(Integer type, String title, String msg, String userId) {
        
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
            log.info("Send to {} message is failed!", userId);
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

        List<User> userlist = userDao.getUserList();
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
            log.info("Broadcast message is failed!");
        }
    }

    public Publisher<Notification> getNotificationPublisher() {
        
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid = userDetails.getId();
        User user = userDao.getUserById(userid);

        return sink.asFlux().filter(p -> {
            // log.info("notification userid : {}, current userid : {}", p.getUserId(), userid);
            // return p.getUserId().equals(userid);
            return p.isBroadcast() 
                ? user.allowNotification(p)
                : p.getUserId().equals(userid);
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
		String userId = userDetails.getId();

        return notificationDao.setReadStatus(userId, nId);
    }
}
