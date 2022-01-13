package com.ndb.auction.config;

import com.ndb.auction.background.TaskRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class AppConfig {

    @Autowired
    TaskRunner taskRunner;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        taskRunner.run();
    }

}
