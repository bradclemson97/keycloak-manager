package com.example.keycloakmanager.config;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.admin.client.ClientBuilderWrapper;
import org.keycloak.admin.client.JacksonProvider;
import org.keycloak.admin.client.spi.ResteasyClientProvider;

import javax.net.ssl.SSLContext;
import java.util.concurrent.TimeUnit;

/**
 * Client provider for keycloak
 */
public class ClientProvider implements ResteasyClientProvider {

    public Client newRestEasyClient(Object customJasksonProvider, SSLContext sslContext,
                                    boolean disableTrustManager) {
        ClientBuilder clientBuilder = ClientBuilderWrapper.create(sslContext, disableTrustManager);
        clientBuilder.register(JacksonProvider.class, 100);
        clientBuilder.connectTimeout(5, TimeUnit.SECONDS);

        return clientBuilder.build();
    }

    public <R> R targetProxy(WebTarget client, Class<R> targetClass) {
        return ((ResteasyWebTarget) client).proxy(targetClass);
    }
}
