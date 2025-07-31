package com.cricketacademy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot Application class for Cricket Academy API
 * This class serves as the entry point for the application
 */
@SpringBootApplication
@EnableConfigurationProperties
public class CricketAcademyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CricketAcademyApplication.class, args);
    }
}