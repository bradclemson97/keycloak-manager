package com.example.keycloakmanager.service;

import jakarta.validation.constraints.NotBlank;
import org.keycloak.representations.idm.CredentialRepresentation;

/**
 * A service for working with credentials.
 */
public interface CredentialService {

    /**
     * Generates a user password
     *
     * @return the generated passphrase
     */
    String generateUserPassword();

    /**
     * Generates a passphrase comprised of english words separated by hyphens.
     *
     * @param minChars the minimum number of characters each word
     * @param minLength the minimum length of the total passphrase including hyphens
     * @param numWords the number of words in the passphrase
     * @return the generated passphrase
     */
    String generatePassphrase(int minChars, int minLength, int numWords);

    /**
     * Creates a {@link org.keycloak.representations.idm.CredentialRepresentation} for a password.
     *
     * @param password a plaintext String password
     * @return The build credential object with defaults set
     */
    CredentialRepresentation createPasswordCredential(@NotBlank String password);
}
