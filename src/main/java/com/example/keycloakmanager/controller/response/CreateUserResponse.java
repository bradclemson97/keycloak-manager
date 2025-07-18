package com.example.keycloakmanager.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * The object for the data of create User Responses.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateUserResponse {

    private UUID systemUserId;
    private String password;

}
