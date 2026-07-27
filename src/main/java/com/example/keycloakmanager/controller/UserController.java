package com.example.keycloakmanager.controller;

import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.controller.response.GetUserResponse;
import com.example.keycloakmanager.controller.response.LockoutStatusResponse;
import com.example.keycloakmanager.controller.response.ResetPasswordResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import com.example.keycloakmanager.exception.response.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.keycloakmanager.config.SystemConstant.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for a Keycloak user managing their account.
 */
@Tag(name = "User Credential Management", description = "Endpoints for user managing their account")
@ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "401", description = "Unauthorised", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
        mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
@Validated
@RequestMapping("/" + API_VERSION + "/" + API_USER)
public interface UserController {

    @Operation(summary = "Add a new user to Keycloak", description = "Create a new user with the specified details in Keycloak")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "User creation successful")
    @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(
            mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)
    ))
    CreateUserResponse createUser(
            @RequestBody @Valid CreateUserRequest userRequest)
            throws UserCreationException;

    @Operation(summary = "Get a Keycloak user by email", description = "Retrieves a user's details from Keycloak by their email address")
    @GetMapping("/{email}")
    @ApiResponse(responseCode = "200", description = "User found")
    GetUserResponse getUser(
            @Parameter(name = "email", description = "The email address of the user")
            @NotBlank(message = "The path variable email cannot be blank")
            @PathVariable String email);

    @Operation(summary = "Reset a user's password in Keycloak", description = "Generates and sets a new passphrase for the specified user")
    @PutMapping("/{email}/" + API_PASSWORD)
    @ApiResponse(responseCode = "200", description = "Password reset successful")
    ResetPasswordResponse resetPassword(
            @Parameter(name = "email", description = "The email address of the user")
            @NotBlank(message = "The path variable email cannot be blank")
            @PathVariable String email);

    @Operation(summary = "Rollback a request to create a user in Keycloak",
            description = "Rolls back creating a user with the specified details in Keycloak")
    @DeleteMapping("/" + API_ROLLBACK + "/{primaryEmail}")
    @ApiResponse(responseCode = "200", description = "Rollback successful")
    void rollbackUser(
            @Parameter(name = "primaryEmail", description = "The email of the user to roll back")
            @NotBlank(message = "The path variable primaryEmail cannot be blank")
            @PathVariable String primaryEmail);

    @Operation(summary = "Get lockout status for a user", description = "Returns whether the user is locked in Keycloak and how many failed attempts have occurred")
    @GetMapping("/{email}/" + API_LOCKOUT_STATUS)
    @ApiResponse(responseCode = "200", description = "Lockout status retrieved")
    LockoutStatusResponse getLockoutStatus(
            @Parameter(name = "email", description = "The email address of the user")
            @NotBlank(message = "The path variable email cannot be blank")
            @PathVariable String email);

    @Operation(summary = "Unlock a user in Keycloak", description = "Clears brute-force state and re-enables the user account in Keycloak")
    @PutMapping("/{email}/" + API_UNLOCK)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "User unlocked successfully")
    void unlockInKeycloak(
            @Parameter(name = "email", description = "The email address of the user")
            @NotBlank(message = "The path variable email cannot be blank")
            @PathVariable String email);

}
