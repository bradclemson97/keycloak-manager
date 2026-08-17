package com.example.keycloakmanager.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SyncPermissionsRequest {
    private List<String> capabilities;
    private List<String> systemRoles;
}
