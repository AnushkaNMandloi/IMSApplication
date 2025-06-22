package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitingConfig {
    
    @Bean
    public ConcurrentHashMap<String, Integer> requestCounts() {
        return new ConcurrentHashMap<>();
    }
    
    @Bean
    public ConcurrentHashMap<String, Long> requestTimestamps() {
        return new ConcurrentHashMap<>();
    }
    
    @Bean
    public ScheduledExecutorService rateLimitCleanupScheduler() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        // Clean up expired entries every minute
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            long windowSizeMs = 60 * 1000; // 1 minute window
            
            requestTimestamps().entrySet().removeIf(entry -> 
                currentTime - entry.getValue() > windowSizeMs);
            
            // Remove corresponding count entries
            requestCounts().entrySet().removeIf(entry -> 
                !requestTimestamps().containsKey(entry.getKey()));
                
        }, 1, 1, TimeUnit.MINUTES);
        
        return scheduler;
    }
} 