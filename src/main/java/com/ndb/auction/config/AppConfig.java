package com.ndb.auction.config;

import java.util.Date;

import com.ndb.auction.service.CryptoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableAsync
public class AppConfig {

    public static boolean appStartUp;

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        appStartUp = true;
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
