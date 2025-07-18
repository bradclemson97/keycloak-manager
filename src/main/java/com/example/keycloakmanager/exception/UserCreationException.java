package com.example.keycloakmanager.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

/**
 * An exception that is thrown when a Keycloak user creation operation fails.
 */
public class UserCreationException extends ResponseStatusException {

    public UserCreationException(HttpStatusCode code, String message) { super(code, message);}
}
