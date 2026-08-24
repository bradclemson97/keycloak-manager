package com.example.keycloakmanager.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealmSetupService {

    private final Keycloak keycloak;

    @Value("${KEYCLOAK_REALM:system}")
    private String realmName;

    @PostConstruct
    public void setupProtocolMappers() {
        ensureUserAttributeMapper("capabilities");
        ensureUserAttributeMapper("systemRoles");
    }

    private void ensureUserAttributeMapper(String attributeName) {
        try {
            var clientScopes = keycloak.realm(realmName).clientScopes().findAll();
            var rolesScope = clientScopes.stream()
                    .filter(cs -> "roles".equals(cs.getName()) || "profile".equals(cs.getName()))
                    .findFirst()
                    .orElse(null);

            if (rolesScope == null) {
                log.warn("Could not find 'roles' or 'profile' client scope in realm '{}' — skipping mapper setup for '{}'",
                        realmName, attributeName);
                return;
            }

            var existingMappers = keycloak.realm(realmName).clientScopes()
                    .get(rolesScope.getId()).getProtocolMappers().getMappers();

            boolean exists = existingMappers != null && existingMappers.stream()
                    .anyMatch(m -> attributeName.equals(m.getName()));

            if (exists) {
                log.info("Protocol mapper '{}' already exists in scope '{}' — skipping", attributeName, rolesScope.getName());
                return;
            }

            ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
            mapper.setName(attributeName);
            mapper.setProtocol("openid-connect");
            mapper.setProtocolMapper("oidc-usermodel-attribute-mapper");
            mapper.setConfig(Map.of(
                    "user.attribute", attributeName,
                    "claim.name", attributeName,
                    "jsonType.label", "String",
                    "id.token.claim", "false",
                    "access.token.claim", "true",
                    "userinfo.token.claim", "false",
                    "multivalued", "true",
                    "aggregate.attrs", "false"
            ));

            try (var response = keycloak.realm(realmName).clientScopes()
                    .get(rolesScope.getId()).getProtocolMappers().createMapper(mapper)) {
                if (response.getStatus() == 201) {
                    log.info("Created protocol mapper '{}' in scope '{}'", attributeName, rolesScope.getName());
                } else {
                    log.warn("Unexpected status {} creating protocol mapper '{}': {}",
                            response.getStatus(), attributeName,
                            response.hasEntity() ? response.readEntity(String.class) : "");
                }
            }
        } catch (Exception e) {
            log.error("Failed to set up protocol mapper '{}': {}", attributeName, e.getMessage(), e);
        }
    }
}
