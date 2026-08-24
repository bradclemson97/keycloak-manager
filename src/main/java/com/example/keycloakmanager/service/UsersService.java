package com.example.keycloakmanager.service;

import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.controller.response.GetUserResponse;
import com.example.keycloakmanager.controller.response.LockoutStatusResponse;
import com.example.keycloakmanager.controller.response.ResetPasswordResponse;
import com.example.keycloakmanager.exception.UserCreationException;

import java.util.List;
import java.util.UUID;

/**
 * A service for performing user actions.
 */
public interface UsersService {

    /**
     * Creates a new user with the specified fields in Keycloak.
     *
     * @param request the DTO with the fields for the new user
     * @return the new user details and password for the user
     * @throws UserCreationException if the user creation request fails.
     */
    CreateUserResponse createUser(CreateUserRequest request) throws UserCreationException;

    /**
     * Retrieves a user from Keycloak by their email address.
     *
     * @param email the email address of the user
     * @return the user's details
     * @throws jakarta.persistence.EntityNotFoundException if the user does not exist
     */
    GetUserResponse getUser(String email);

    /**
     * Generates and sets a new passphrase for the specified user in Keycloak.
     *
     * @param email the email address of the user
     * @return the newly generated password
     * @throws jakarta.persistence.EntityNotFoundException if the user does not exist
     */
    ResetPasswordResponse resetPassword(String email);

    /**
     * Rolls back creating a user with the given username in Keycloak.
     *
     * @param username the keycloak username of the user.
     */
    void rollbackUser(String username);

    LockoutStatusResponse getLockoutStatus(String email);

    void unlockInKeycloak(String email);

    void syncUserPermissions(UUID systemUserId, List<String> capabilities, List<String> systemRoles);
}
