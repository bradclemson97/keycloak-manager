package com.example.keycloakmanager.client.config;

import feign.Logger.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This Configuration class can be used for Spring Feign Clients interfaces for configuration.
 */
@Configuration
public class FeignClientConfig {

    @Value("${feign.log-level:HEADERS}")
    private Level level;

    @Bean
    Level feignLoggerLevel() { return level; }
}
