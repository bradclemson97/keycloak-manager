package com.example.keycloakmanager.controller;

import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.controller.response.GetUserResponse;
import com.example.keycloakmanager.controller.response.ResetPasswordResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import com.example.keycloakmanager.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UsersService usersService;

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) throws UserCreationException {
        log.info("Handling create request for user '{}' with system user id '{}'",
                request.getEmail(), request.getSystemUserId());
        CreateUserResponse response = usersService.createUser(request);
        log.info("Handled create request for user '{}' with system user id '{}'",
                request.getEmail(), request.getSystemUserId());
        return response;
    }

    @Override
    public GetUserResponse getUser(String email) {
        log.info("Handling get user request for '{}'", email);
        GetUserResponse response = usersService.getUser(email);
        log.info("Handled get user request for '{}'", email);
        return response;
    }

    @Override
    public ResetPasswordResponse resetPassword(String email) {
        log.info("Handling password reset request for '{}'", email);
        ResetPasswordResponse response = usersService.resetPassword(email);
        log.info("Handled password reset request for '{}'", email);
        return response;
    }

    @Override
    public void rollbackUser(String primaryEmail) {
        log.info("Handling create user rollback request for user '{}'", primaryEmail);
        usersService.rollbackUser(primaryEmail);
        log.info("Handled create user rollback request for user '{}'", primaryEmail);
    }
}
