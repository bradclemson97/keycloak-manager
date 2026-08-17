package com.example.keycloakmanager.service;

import com.example.keycloakmanager.exception.CommunicationException;
import com.example.keycloakmanager.exception.DuplicateUserException;
import com.example.keycloakmanager.exception.UserCreationException;
import com.example.keycloakmanager.exception.UserDeletionException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
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
        log.info("Deleting the Keycloak account '{}' with Keycloak id '{}'", username, keycloakId);
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

    @Override
    public void updateUserPassword(String keycloakUserId, CredentialRepresentation credential) {
        log.info("Resetting password for user with Keycloak id '{}'", keycloakUserId);
        try {
            keycloak.realm(realmName).users().get(keycloakUserId).resetPassword(credential);
            log.info("Password successfully reset for user with Keycloak id '{}'", keycloakUserId);
        } catch (ProcessingException error) {
            log.error("Error resetting password for user in Keycloak", error);
            throw new CommunicationException("Error resetting password: " + error.getMessage(), error);
        }
    }

    @Override
    public Map<String, Object> getBruteForceStatus(String keycloakId) {
        return keycloak.realm(realmName).attackDetection().bruteForceUserStatus(keycloakId);
    }

    @Override
    public void clearBruteForce(String keycloakId) {
        keycloak.realm(realmName).attackDetection().clearBruteForceForUser(keycloakId);
        log.info("Cleared brute-force state for Keycloak user {}", keycloakId);
    }

    @Override
    public void setUserEnabled(String keycloakId, boolean enabled) {
        UserRepresentation user = keycloak.realm(realmName).users().get(keycloakId).toRepresentation();
        user.setEnabled(enabled);
        keycloak.realm(realmName).users().get(keycloakId).update(user);
        log.info("Set enabled={} for Keycloak user {}", enabled, keycloakId);
    }

    @Override
    public void syncUserPermissions(UUID systemUserId, List<String> capabilities, List<String> systemRoles) {
        log.info("Syncing permissions to Keycloak for systemUserId {}", systemUserId);
        var users = keycloak.realm(realmName).users()
                .searchByAttributes("systemUserId:" + systemUserId);
        if (users == null || users.isEmpty()) {
            log.warn("No Keycloak user found for systemUserId {}", systemUserId);
            return;
        }
        var userResource = keycloak.realm(realmName).users().get(users.get(0).getId());
        UserRepresentation rep = userResource.toRepresentation();
        Map<String, List<String>> attrs = new HashMap<>(
                rep.getAttributes() != null ? rep.getAttributes() : Map.of());
        attrs.put("capabilities", capabilities);
        attrs.put("systemRoles", systemRoles);
        rep.setAttributes(attrs);
        userResource.update(rep);
        log.info("Synced {} capabilities and {} systemRoles for systemUserId {}",
                capabilities.size(), systemRoles.size(), systemUserId);
    }
}
