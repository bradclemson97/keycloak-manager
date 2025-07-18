package com.example.keycloakmanager.exception;

import org.springframework.http.HttpStatusCode;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * An exception that is thrown when a duplicate Keycloak user is detected.
 */
public class DuplicateUserException extends UserCreationException {

    private static final HttpStatusCode CODE = CONFLICT;

    public DuplicateUserException(String username) { super(CODE, "Duplicate user: " + username); }
}
