package com.example.keycloakmanager.service;

import com.example.keycloakmanager.controller.request.CreateUserRequest;
import com.example.keycloakmanager.controller.response.CreateUserResponse;
import com.example.keycloakmanager.controller.response.GetUserResponse;
import com.example.keycloakmanager.controller.response.ResetPasswordResponse;
import com.example.keycloakmanager.exception.UserCreationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private AdminService adminService;

    @Mock
    private CredentialService credentialService;

    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void createUser_createsUserInKeycloak_andReturnsPassword() throws UserCreationException {
        UUID systemUserId = UUID.randomUUID();
        CreateUserRequest request = CreateUserRequest.builder()
                .systemUserId(systemUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        CredentialRepresentation credential = new CredentialRepresentation();
        when(credentialService.generateUserPassword()).thenReturn("apple-mango-grape-peach");
        when(credentialService.createPasswordCredential(anyString())).thenReturn(credential);

        CreateUserResponse response = usersService.createUser(request);

        assertThat(response.getSystemUserId()).isEqualTo(systemUserId);
        assertThat(response.getPassword()).isEqualTo("apple-mango-grape-peach");

        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(adminService).createUserRequest(userCaptor.capture());
        UserRepresentation captured = userCaptor.getValue();
        assertThat(captured.getUsername()).isEqualTo("john.doe@example.com");
        assertThat(captured.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(captured.isEmailVerified()).isTrue();
        assertThat(captured.isEnabled()).isTrue();
    }

    @Test
    void createUser_propagatesUserCreationException() throws UserCreationException {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .build();
        CredentialRepresentation credential = new CredentialRepresentation();
        when(credentialService.generateUserPassword()).thenReturn("apple-mango-grape-peach");
        when(credentialService.createPasswordCredential(anyString())).thenReturn(credential);
        doThrow(new UserCreationException(HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak error"))
                .when(adminService).createUserRequest(any());

        assertThatThrownBy(() -> usersService.createUser(request))
                .isInstanceOf(UserCreationException.class);
    }

    @Test
    void getUser_returnsUserDetails() {
        UserRepresentation user = new UserRepresentation();
        user.setId("kc-id-123");
        user.setUsername("jane@example.com");
        user.setEmail("jane@example.com");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmailVerified(true);
        user.setEnabled(true);
        when(adminService.getUser("jane@example.com")).thenReturn(user);

        GetUserResponse response = usersService.getUser("jane@example.com");

        assertThat(response.getKeycloakId()).isEqualTo("kc-id-123");
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.isEmailVerified()).isTrue();
        assertThat(response.isEnabled()).isTrue();
    }

    @Test
    void resetPassword_generatesNewPasswordAndUpdatesKeycloak() {
        UserRepresentation user = new UserRepresentation();
        user.setId("kc-id-456");
        user.setUsername("jane@example.com");
        CredentialRepresentation credential = new CredentialRepresentation();
        when(adminService.getUser("jane@example.com")).thenReturn(user);
        when(credentialService.generateUserPassword()).thenReturn("new-pass-phrase-here");
        when(credentialService.createPasswordCredential("new-pass-phrase-here")).thenReturn(credential);

        ResetPasswordResponse response = usersService.resetPassword("jane@example.com");

        assertThat(response.getPassword()).isEqualTo("new-pass-phrase-here");
        verify(adminService).updateUserPassword("kc-id-456", credential);
    }

    @Test
    void rollbackUser_deletesUserByUsername() {
        UserRepresentation user = new UserRepresentation();
        user.setId("kc-id-789");
        when(adminService.getUser("jane@example.com")).thenReturn(user);

        usersService.rollbackUser("jane@example.com");

        verify(adminService).deleteUserRequest(user);
    }
}
