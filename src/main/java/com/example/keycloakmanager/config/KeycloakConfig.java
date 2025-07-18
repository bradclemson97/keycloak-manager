package com.example.keycloakmanager.config;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the Keycloak admin client.
 */
@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {

    private final KeycloakProperties properties;

    /**
     * Creates an instance of the Keycloak admin client.
     *
     * @return a new Keycloak instance
     */
    @Bean
    public Keycloak keycloak(KeycloakBuilder keycloakBuilder) {
        Keycloak.setClientProvider(new ClientProvider());

        return keycloakBuilder
                .grantType(CLIENT_CREDENTIALS)
                .build();
    }

    /**
     * Creates builder with default values for creating keycloak instances.
     *
     * @return a new keycloak builder
     */
    @Bean
    public KeycloakBuilder keycloakBuilder() {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(properties.getRealm())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret());
    }

}

