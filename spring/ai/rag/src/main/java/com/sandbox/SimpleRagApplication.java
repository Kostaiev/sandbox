package com.sandbox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableRetry
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class SimpleRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleRagApplication.class, args);
    }

    @Scheduled(fixedDelay = 1 * 60 * 1000) //ms
    @CacheEvict(cacheNames = "facts", allEntries = true)
    public void refreshCache(){
        log.info("Cache refreshed....");
    }
}