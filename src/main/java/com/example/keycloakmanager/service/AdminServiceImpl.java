package com.example.keycloakmanager.service;

import com.example.keycloakmanager.exception.CommunicationException;
import com.example.keycloakmanager.exception.DuplicateUserException;
import com.example.keycloakmanager.exception.UserCreationException;
import com.example.keycloakmanager.exception.UserDeletionException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

/**
 * A service for managing users in Keycloak.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final Keycloak keycloak;

    @Value("${KEYCLOAK_REALM:system}")
    private String realmName;

    @Override
    public void createUserRequest(UserRepresentation user) throws UserCreationException {
        log.info("Creating the keycloak account '{}", user.getEmail());
        try (Response response = keycloak.realm(realmName).users().create(user)) {
            String username = user.getUsername();
            int statusCode = response.getStatus();
            switch (statusCode) {
                case 201 -> log.info("User {} successfully created in Keyloak", username);
                case 409 -> {
                    log.error("Duplicate user {}", username);
                    throw new DuplicateUserException(username);
                }
                default -> {
                    String body = "";
                    if (response.hasEntity()) {
                        body = response.readEntity(String.class);
                    }
                    log.error("Error creating user: status code {}, with message {}", statusCode, body);
                    throw new UserCreationException(HttpStatusCode.valueOf(statusCode),
                            "Error creating user: status code " + statusCode);
                }
            }
        } catch (ProcessingException error) {
            log.error("Error creating user in Keycloak", error);
            throw new CommunicationException("Error creating user: " + error.getMessage(), error);
        }
    }

    @Override
    public UserRepresentation getUserRepresentation(String username) {
        return keycloak.realm(realmName)
                .users()
                .searchByUsername(username, true).stream()
                .findFirst().orElse(null);
    }

    @Override
    public UserRepresentation getUser(String username) {
        UserRepresentation user = getUserRepresentation(username);
        if (isNull(user)) {
            String message = String.format("The user '%s' does not exist", username);
            log.error(message);
            throw new EntityNotFoundException(message);
        }
        return user;
    }

    @Override
    public void deleteUserRequest(UserRepresentation user) {
        String username = user.getUsername();
        String keycloakId = user.getId();
        log.info("Deleting the Keycloak account '{}' wth Keycloak id '{}'", username, keycloakId);
        try (Response response = keycloak.realm(realmName).users().delete(keycloakId)) {
            int statusCode = response.getStatus();
            if (statusCode == 204) {
                log.info("User {} successfully deleted in Keycloak", username);
            } else {
                log.error("Error deleting user: status code {}", statusCode);
                throw new UserDeletionException(HttpStatusCode.valueOf(statusCode),
                        "Error deleting user: status code " + statusCode);
            }
        } catch (ProcessingException error) {
            log.error("Error deleting user in Keycloak", error);
            throw new CommunicationException("Error deleting user: " + error.getMessage(), error);
        }
    }
}
