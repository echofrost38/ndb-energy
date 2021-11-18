package com.ndb.auction.resolver;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import org.reactivestreams.Publisher;
// import reactor.core.publisher.Flux;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import java.time.Duration;
import java.time.LocalDateTime;
// import java.util.UUID;

import com.ndb.auction.models.Notification;
import com.ndb.auction.publisher.NotificationPublisher;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationResolver implements GraphQLSubscriptionResolver {

    private final NotificationPublisher notificationPublisher;
    
    @PreAuthorize("isAuthenticated()")
    public Publisher<Notification> notifications() {
        // Add new notification into Repository
        log.info("Incoming new User in Subscription => msg at: {}", LocalDateTime.now());
        return notificationPublisher.getNotificationPublisher();
    }
}
