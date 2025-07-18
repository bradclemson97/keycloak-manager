package com.example.keycloakmanager.client;

import com.example.keycloakmanager.client.config.FeignClientConfig;
import com.example.keycloakmanager.client.response.UserResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Client for the Keycloak Manager.
 */
@FeignClient(value = "UserManagerClient", url = "${USER_MANAGER_URL:http://localhost:8080}",
configuration = FeignClientConfig.class)
public interface UserManagerClient {

    /**
     * Return the information about a user request via the primaryEmail
     *
     * @return primaryEmail the primary email of the user to find the information for.
     */
    @GetMapping("v1/user/email/{primaryEmail}")
    UserResponse getUserByEmail(
            @Parameter(example = "test@test.com",
                    description = "The user's email")
            @PathVariable String primaryEmail);

}
