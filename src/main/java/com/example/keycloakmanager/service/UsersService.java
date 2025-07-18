package com.example.keycloakmanager.service;

import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * A service for performing user actions.
 *
 */
public interface UsersService {

    /**
     * Creates a new users with the specified fields in Keycloak.
     *
     * @param request the DTO with the fields for the new user
     * @return the new user details and password for the user
     * @throws UserCreationException if the user creation request fails.
     */
    CreateUserResponse createUser(CreateUserRequest request) throws
            UserCreationException, JsonProcessingException;

    /**
     * Rolls back creating a user with the given username in Keycloak.
     *
     * @param username the keycloak username of the user.
     */
    void rollbackUser(String username) throws JsonProcessingException;
}
