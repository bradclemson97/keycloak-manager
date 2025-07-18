package com.example.keycloakmanager.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * A response to the details of a user.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    private UUID systemUserId;
    private UserDetailsResponse userDetails;
}
