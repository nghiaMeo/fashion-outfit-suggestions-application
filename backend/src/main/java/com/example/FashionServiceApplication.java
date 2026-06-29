package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Application Entry Point - Unified Monolithic Service
 * 
 * Modules:
 * - Auth Module (User authentication & authorization)
 * - Wardrobe Module (Item & Outfit management)
 * - Social Module (Friendship & Chat)
 * - Notification Module (Real-time notifications)
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class FashionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FashionServiceApplication.class, args);
    }
}
