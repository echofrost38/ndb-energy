package com.ndb.auction.resolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.reactivestreams.Publisher;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import com.ndb.auction.models.Notification;
import com.ndb.auction.models.Notification;
import com.ndb.auction.models.NotificationType;
import com.ndb.auction.models.NotificationType;
import com.ndb.auction.service.user.UserDetailsImpl;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationResolver extends BaseResolver implements GraphQLSubscriptionResolver, GraphQLMutationResolver, GraphQLQueryResolver {

    @PreAuthorize("isAuthenticated()")
    public Publisher<Notification> notifications() {
        log.info("Incoming new User in Subscription => msg at: {}", LocalDateTime.now());
        return notificationService.getNotificationPublisher();
    }

    @PreAuthorize("isAuthenticated()")
    public Notification setNotificationRead(String id) {
        return notificationService.setNotificationRead(id);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> getAllUnReadNotifications() {
        return notificationService.getAllUnReadNotifications();
    }

    /*
        Notification Type
    */

    @PreAuthorize("isAuthenticated()")
    public List<NotificationType> getAllNotificationTypes() {
        return notificationService.getAllNotificationTypes();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addNotificationType(String name) {
        return notificationService.addNotificationType(name);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<NotificationType> deleteNotificationType(Integer id) {
        return notificationService.deleteNotificationType(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public NotificationType updateNotificationType(Integer id, String name) {
        return notificationService.updateNotificationType(id, name);
    }

    ///////////////////////////// version 2 ////////////////////////
    
    public Notification addNewNotification(int userId, int nType, String title, String msg) {
        return notificationService.addNewNotification(userId, nType, title, msg);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> getNotifications(Long stamp, int limit) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return notificationService.getPaginatedNotifications(userId, stamp, limit);
    }

    @PreAuthorize("isAuthenticated()")
    public Notification setNotificationReadFlag(Long stamp) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return notificationService.setNotificationReadFlag(userId, stamp);
    }

    @PreAuthorize("isAuthenticated()")
    public String setNotificationReadFlagAll() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return notificationService.setNotificationReadFlagAll(userId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Notification> getUnreadNotifications() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return notificationService.getUnreadNotification2s(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public NotificationType addNewNotificationType2(int nType, String tName, boolean broadcast) {
        return notificationService.addNewNotificationType(nType, tName, broadcast);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<NotificationType> getNotificationTypes2() {
        return notificationService.getNotificationTypes();
    }

    @PreAuthorize("isAuthenticated()")
    public int changeNotifySetting(int nType, boolean status) {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getId();
        return userService.changeNotifySetting(userId, nType, status);
    }

}
