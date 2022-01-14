package com.ndb.auction.config;

import java.util.Date;

import com.ndb.auction.background.TaskRunner;
import com.ndb.auction.service.CryptoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class AppConfig {

    public static boolean appStartUp;

    @Autowired
    TaskRunner taskRunner;

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        appStartUp = true;
        taskRunner.run();
    }

    @Autowired
    CryptoService cryptoService;

    @Scheduled(fixedDelay = 3600 * 1000)
    public void scheduleFixedRateTask() {
        if (!appStartUp)
            return;
        int count = cryptoService.deleteExpired();
        System.out.println(count + " crypto transactions deleted on " + new Date());
    }

}
