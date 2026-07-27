package com.example.keycloakmanager.service;

import com.example.keycloakmanager.config.SystemConstant;
import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.controller.response.GetUserResponse;
import com.example.keycloakmanager.controller.response.LockoutStatusResponse;
import com.example.keycloakmanager.controller.response.ResetPasswordResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * A service for performing user management actions against Keycloak.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final AdminService adminService;
    private final CredentialService credentialService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) throws UserCreationException {
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
        user.setGroups(List.of(SystemConstant.SYSTEM_USERS_GROUP));
        user.setAttributes(Map.of("systemUserId", List.of(request.getSystemUserId().toString())));

        adminService.createUserRequest(user);

        return CreateUserResponse.builder()
                .systemUserId(request.getSystemUserId())
                .password(password)
                .build();
    }

    @Override
    public GetUserResponse getUser(String email) {
        UserRepresentation user = adminService.getUser(email);
        return GetUserResponse.builder()
                .keycloakId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailVerified(Boolean.TRUE.equals(user.isEmailVerified()))
                .enabled(Boolean.TRUE.equals(user.isEnabled()))
                .build();
    }

    @Override
    public ResetPasswordResponse resetPassword(String email) {
        UserRepresentation user = adminService.getUser(email);
        String newPassword = credentialService.generateUserPassword();
        log.info("New password for {} generated with a length {}", email, newPassword.length());
        CredentialRepresentation credential = credentialService.createPasswordCredential(newPassword);
        adminService.updateUserPassword(user.getId(), credential);
        return ResetPasswordResponse.builder()
                .password(newPassword)
                .build();
    }

    @Override
    public void rollbackUser(String username) {
        UserRepresentation user = adminService.getUser(username);
        adminService.deleteUserRequest(user);
    }

    @Override
    public LockoutStatusResponse getLockoutStatus(String email) {
        UserRepresentation user = adminService.getUser(email);
        Map<String, Object> status = adminService.getBruteForceStatus(user.getId());
        boolean disabled = Boolean.TRUE.equals(status.get("disabled"));
        int numFailures = status.get("numFailures") instanceof Number n ? n.intValue() : 0;
        return LockoutStatusResponse.builder()
                .lockedByKeycloak(disabled)
                .failedAttempts(numFailures)
                .build();
    }

    @Override
    public void unlockInKeycloak(String email) {
        UserRepresentation user = adminService.getUser(email);
        adminService.clearBruteForce(user.getId());
        adminService.setUserEnabled(user.getId(), true);
        log.info("Unlocked Keycloak account for '{}'", email);
    }
}
