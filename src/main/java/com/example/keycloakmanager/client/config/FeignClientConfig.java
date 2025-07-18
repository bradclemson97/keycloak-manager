package com.example.keycloakmanager.client.config;

import feign.Logger.Level;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/**
 * This Configuration class can be used for Spring Feign Clients interfaces for configuration.
 */
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    @Value("${feign.log-level:HEADERS}")
    private final Level level;

    @Bean
    Level feignLoggerLevel() {return level;}
}
