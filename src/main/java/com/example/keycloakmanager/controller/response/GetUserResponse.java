package com.example.keycloakmanager.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {

    @Schema(description = "The Keycloak-assigned ID of the user")
    private String keycloakId;

    @Schema(description = "The username of the user in Keycloak")
    private String username;

    @Schema(description = "The email address of the user")
    private String email;

    @Schema(description = "The first name of the user")
    private String firstName;

    @Schema(description = "The last name of the user")
    private String lastName;

    @Schema(description = "Whether the user's email address has been verified")
    private boolean emailVerified;

    @Schema(description = "Whether the user account is enabled")
    private boolean enabled;
}
