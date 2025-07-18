package com.example.keycloakmanager.controller;

import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import com.example.keycloakmanager.service.CredentialService;
import com.example.keycloakmanager.service.UsersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for a Keycloak user managing their account.
 *
 * <p>
 *     This controller provides endpoints for a Keycloak user retrieving, updating
 *     Keycloak users. It uses the {@link com.example.keycloakmanager.service.CredentialService}
 *     to interact with Keycloak.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UsersService usersService;
    private final CredentialService credentialService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request)
        throws UserCreationException, JsonProcessingException {
        log.info("Handling saga create request for user '{}' with system user id '{}'",
                request.getEmail(), request.getSystemUserId());

        CreateUserResponse response = usersService.createUser(request);

        log.info("Handled saga create request for user '{}' with system user id '{}'",
                request.getEmail(), request.getSystemUserId());
        return response;
    }

    @Override
    public void rollbackUser(String primaryEmail) throws JsonProcessingException {
        log.info("Handling create user rollback request for user '{}'", primaryEmail);
        usersService.rollbackUser(primaryEmail);
        log.info("Handled create user rollback reqest for user '{}'", primaryEmail);
    }
}
