package com.eazybytes.eazystore.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StartupListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=== Application started successfully ===");
    }
}
