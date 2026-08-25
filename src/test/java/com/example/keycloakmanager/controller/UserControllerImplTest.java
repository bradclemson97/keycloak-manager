package com.example.keycloakmanager.controller;

import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.controller.response.GetUserResponse;
import com.example.keycloakmanager.controller.response.ResetPasswordResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import com.example.keycloakmanager.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersService usersService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void createUser_returnsCreatedWithPassword() throws Exception {
        UUID systemUserId = UUID.randomUUID();
        CreateUserRequest request = CreateUserRequest.builder()
                .systemUserId(systemUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        CreateUserResponse response = CreateUserResponse.builder()
                .systemUserId(systemUserId)
                .password("apple-mango-grape-peach")
                .build();
        when(usersService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.systemUserId").value(systemUserId.toString()))
                .andExpect(jsonPath("$.password").value("apple-mango-grape-peach"));
    }

    @Test
    void createUser_missingFirstName_returnsBadRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_serviceThrowsConflict_returnsConflict() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        when(usersService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new UserCreationException(HttpStatus.CONFLICT, "User already exists"));

        mockMvc.perform(post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUser_returnsUserDetails() throws Exception {
        GetUserResponse response = GetUserResponse.builder()
                .keycloakId("kc-id-123")
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .emailVerified(true)
                .enabled(true)
                .build();
        when(usersService.getUser("jane@example.com")).thenReturn(response);

        mockMvc.perform(get("/v1/user/jane@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value("kc-id-123"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(usersService.getUser("missing@example.com"))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/v1/user/missing@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void resetPassword_returnsNewPassword() throws Exception {
        ResetPasswordResponse response = ResetPasswordResponse.builder()
                .password("new-pass-phrase-here")
                .build();
        when(usersService.resetPassword("jane@example.com")).thenReturn(response);

        mockMvc.perform(put("/v1/user/jane@example.com/password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").value("new-pass-phrase-here"));
    }

    @Test
    void resetPassword_notFound_returns404() throws Exception {
        when(usersService.resetPassword("missing@example.com"))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(put("/v1/user/missing@example.com/password"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rollbackUser_returnsOk() throws Exception {
        doNothing().when(usersService).rollbackUser("john.doe@example.com");

        mockMvc.perform(delete("/v1/user/rollback/john.doe@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void rollbackUser_notFound_returns404() throws Exception {
        doThrow(new EntityNotFoundException("User not found"))
                .when(usersService).rollbackUser("missing@example.com");

        mockMvc.perform(delete("/v1/user/rollback/missing@example.com"))
                .andExpect(status().isNotFound());
    }
}
