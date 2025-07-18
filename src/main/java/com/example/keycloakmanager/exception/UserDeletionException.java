package com.example.keycloakmanager.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * An exception that is thrown when a Keycloak user deletion operation fails.
 */
public class UserDeletionException extends ResponseStatusException {

    public UserDeletionException(HttpStatusCode code, String message) { super(code, message); }
}
