package com.example.keycloakmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class KeycloakManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeycloakManagerApplication.class, args);
    }
}
