package com.example.keycloakmanager.service;

import com.example.keycloakmanager.config.SystemConstant;
import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service for performing Saga actions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final AdminService adminService;
    private final CredentialService credentialService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request)
        throws UserCreationException, JsonProcessingException {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setEmailVerified(true);
        user.setEnabled(true);

        String password = credentialService.generateUserPassword();
        log.info("Password for {} generated with a length {}", request.getEmail(), password.length());

        CredentialRepresentation credential = credentialService.createPasswordCredential(password);
        user.setCredentials(List.of(credential));

        List<String> groups = List.of(SystemConstant.SYSTEM_USERS_GROUP);
        user.setGroups(groups);

        adminService.createUserRequest(user);

        return CreateUserResponse.builder()
                .systemUserId(request.getSystemUserId())
                .password(password)
                .build();
    }

    @Override
    public void rollbackUser(String username) {
        UserRepresentation user = adminService.getUser(username);
        adminService.deleteUserRequest(user);
    }
}
