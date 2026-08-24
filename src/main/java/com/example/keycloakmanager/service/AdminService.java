package com.example.keycloakmanager.service;

import com.example.keycloakmanager.exception.UserCreationException;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {

    /**
     * Requests the creation of a new user with the specified UserRepresentation in Keycloak.
     *
     * @param user the UserRepresentation for the new user
     * @throws com.example.keycloakmanager.exception.UserCreationException  if the user creation fails
     * @throws com.example.keycloakmanager.exception.DuplicateUserException if a user with the same username already exists in Keycloak
     * @throws com.example.keycloakmanager.exception.CommunicationException if there was a problem communicating with the Keycloak server
     */
    void createUserRequest(UserRepresentation user)
            throws UserCreationException;

    /**
     * Requests the search and {@link UserRepresentation} object for a given username if one exists.
     *
     * @param username username of the user to be found.
     * @return the retrieved user representation from Keycloak
     */
    UserRepresentation getUserRepresentation(String username);

    /**
     * Requests the search and {@link org.keycloak.representations.idm.UserRepresentation} object for a given username if one exists.
     *
     * @param username username of the user to be found.
     * @return the retrieved user representation from Keycloak
     * @throws jakarta.persistence.EntityNotFoundException when the user does not exist
     */
    UserRepresentation getUser(String username);

    /**
     * Requests the deletion of a user with the specified UserRepresentation in Keycloak.
     *
     * @param user the UserRepresentation for the user to delete
     * @throws com.example.keycloakmanager.exception.UserDeletionException if the user deletion fails
     * @throws com.example.keycloakmanager.exception.CommunicationException if there was a problem communicating with the Keycloak server.
     */
    void deleteUserRequest(UserRepresentation user);

    /**
     * Resets the password for a user identified by their Keycloak ID.
     *
     * @param keycloakUserId the Keycloak-assigned ID of the user
     * @param credential the new credential to set
     * @throws com.example.keycloakmanager.exception.CommunicationException if there was a problem communicating with the Keycloak server.
     */
    void updateUserPassword(String keycloakUserId, org.keycloak.representations.idm.CredentialRepresentation credential);

    Map<String, Object> getBruteForceStatus(String keycloakId);

    void clearBruteForce(String keycloakId);

    void setUserEnabled(String keycloakId, boolean enabled);

    void syncUserPermissions(UUID systemUserId, List<String> capabilities, List<String> systemRoles);
}