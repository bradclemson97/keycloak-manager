package com.example.keycloakmanager.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockoutStatusResponse {
    private boolean lockedByKeycloak;
    private int failedAttempts;
}
