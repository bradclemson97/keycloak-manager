package com.example.keycloakmanager.service;

import com.example.keycloakmanager.exception.CommunicationException;
import com.example.keycloakmanager.exception.DuplicateUserException;
import com.example.keycloakmanager.exception.UserCreationException;
import com.example.keycloakmanager.exception.UserDeletionException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminService, "realmName", "test-realm");
        when(keycloak.realm("test-realm")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

    @Test
    void createUserRequest_logsSuccess_on201() throws UserCreationException {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(201);
        when(usersResource.create(any())).thenReturn(response);

        UserRepresentation user = new UserRepresentation();
        user.setUsername("test@test.com");
        user.setEmail("test@test.com");

        adminService.createUserRequest(user);

        verify(usersResource).create(user);
    }

    @Test
    void createUserRequest_throwsDuplicateUserException_on409() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(409);
        when(usersResource.create(any())).thenReturn(response);

        UserRepresentation user = new UserRepresentation();
        user.setUsername("dupe@test.com");

        assertThatThrownBy(() -> adminService.createUserRequest(user))
                .isInstanceOf(DuplicateUserException.class);
    }

    @Test
    void createUserRequest_throwsUserCreationException_onOtherErrorStatus() {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(500);
        when(response.hasEntity()).thenReturn(false);
        when(usersResource.create(any())).thenReturn(response);

        UserRepresentation user = new UserRepresentation();
        user.setUsername("fail@test.com");

        assertThatThrownBy(() -> adminService.createUserRequest(user))
                .isInstanceOf(UserCreationException.class);
    }

    @Test
    void createUserRequest_throwsCommunicationException_onProcessingException() {
        when(usersResource.create(any())).thenThrow(new ProcessingException("timeout"));

        UserRepresentation user = new UserRepresentation();
        user.setUsername("timeout@test.com");

        assertThatThrownBy(() -> adminService.createUserRequest(user))
                .isInstanceOf(CommunicationException.class);
    }

    @Test
    void getUser_returnsUser_whenFound() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("found@test.com");
        when(usersResource.searchByUsername("found@test.com", true)).thenReturn(List.of(user));

        UserRepresentation result = adminService.getUser("found@test.com");

        assertThat(result.getUsername()).isEqualTo("found@test.com");
    }

    @Test
    void getUser_throwsEntityNotFoundException_whenNotFound() {
        when(usersResource.searchByUsername("missing@test.com", true)).thenReturn(List.of());

        assertThatThrownBy(() -> adminService.getUser("missing@test.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteUserRequest_succeeds_on204() {
        UserRepresentation user = new UserRepresentation();
        user.setId("kc-id-123");
        user.setUsername("delete@test.com");
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(204);
        when(usersResource.delete("kc-id-123")).thenReturn(response);

        adminService.deleteUserRequest(user);

        verify(usersResource).delete("kc-id-123");
    }

    @Test
    void deleteUserRequest_throwsUserDeletionException_onNon204() {
        UserRepresentation user = new UserRepresentation();
        user.setId("kc-id-456");
        user.setUsername("fail@test.com");
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(500);
        when(usersResource.delete("kc-id-456")).thenReturn(response);

        assertThatThrownBy(() -> adminService.deleteUserRequest(user))
                .isInstanceOf(UserDeletionException.class);
    }

    @Test
    void updateUserPassword_callsResetPassword() {
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get("kc-id-789")).thenReturn(userResource);
        CredentialRepresentation credential = new CredentialRepresentation();

        adminService.updateUserPassword("kc-id-789", credential);

        verify(userResource).resetPassword(credential);
    }

    @Test
    void updateUserPassword_throwsCommunicationException_onProcessingException() {
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get("kc-id-abc")).thenReturn(userResource);
        doThrow(new ProcessingException("connection refused")).when(userResource).resetPassword(any());

        assertThatThrownBy(() -> adminService.updateUserPassword("kc-id-abc", new CredentialRepresentation()))
                .isInstanceOf(CommunicationException.class);
    }
}
