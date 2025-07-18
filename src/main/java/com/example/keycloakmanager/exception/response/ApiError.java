package com.example.keycloakmanager.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * A response representing an error message ad a map of errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private String message;
    private Map<String, List<String>> errors;

    public ApiError(String message, String error) {
        this.message = message;
        errors = Map.of("message", List.of(String.valueOf(error)));
    }

    public ApiError(String message, String field, String error) {
        this.message = message;
        errors = Map.of(field, List.of(String.valueOf(error)));
    }
}
