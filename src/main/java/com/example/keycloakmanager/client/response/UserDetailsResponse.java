package com.example.keycloakmanager.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * A response containing the details of a user.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsResponse {

    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String primaryEmail;
}
