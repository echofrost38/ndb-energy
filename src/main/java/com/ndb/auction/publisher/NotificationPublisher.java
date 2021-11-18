package com.ndb.auction.publisher;

import com.ndb.auction.models.Notification;
import com.ndb.auction.service.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

import java.time.Duration;

/**
 * Subscription (Chapter 33)
 */
@Slf4j
@Component
public class NotificationPublisher {

    /**
     * Use Sinks.Many with Reactor v3.4+
     */
    final Sinks.Many<Notification> sink;

    public NotificationPublisher() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public void publish(Notification notification) {
        EmitResult result = sink.tryEmitNext(notification);

        if (result.isFailure()) {
          // do something here, since emission failed
        }
    }

    public Publisher<Notification> getNotificationPublisher() {
        UserDetailsImpl userDetails = (UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userid = userDetails.getId();
        // return sink.asFlux().filter(p -> {
        //     log.info("notification userid : {}, current userid : {}", p.getUserId(), userid);
        //     return p.getUserId().equals(userid);
        // }).map(n -> {
        //     return n;
        // }); 
        return Flux.interval(Duration.ofSeconds(2))
            .map(num -> {
                System.out.println(num);
                Notification notification = new Notification();

                notification.setUserId("35c9bcdd-647e-4a39-86e6-b388bd97d88f");
                notification.setStamp(System.currentTimeMillis());
                return notification;
            });
    }
}
