package com.ndb.auction.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AppConfig {

    public static boolean appStartUp;

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        appStartUp = true;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasename("messages/errors");
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }

}
