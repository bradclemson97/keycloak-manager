package com.example.keycloakmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class holds the Keycloak properties for the application.
 */
@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakProperties {

    /**
     * The URL of your Keycloak server's authentication endpoint.
     */
    private String serverUrl;

    /**
     * The name of the Keycloak realm containing your application and users.
     */
    private String realm;

    /**
     * The ID of the client that represents your application in Keycloak
     */
    private String clientId;

    /**
     * The client secret that allows your application to authenticate with Keycloak.
     */
    private String clientSecret;
}
