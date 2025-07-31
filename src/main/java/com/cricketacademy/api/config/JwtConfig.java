package com.cricketacademy.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Configuration class for JWT properties
 */
@Configuration
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {
    
    private String secret;
    private Long expiration;
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public Long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}