package com.example.keycloakmanager.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * An exception that is thrown when there is a communication problem with a remote service
 */
public class CommunicationException extends ResponseStatusException {

    private static final HttpStatusCode CODE = SERVICE_UNAVAILABLE;

    public CommunicationException(String message) { super(CODE, message); }

    public CommunicationException(String message, Throwable cause) { super(CODE, message, cause); }
}
