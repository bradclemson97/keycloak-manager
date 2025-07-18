package com.example.keycloakmanager.service;

import com.example.keycloakmanager.model.Words;
import com.example.keycloakmanager.service.validation.password.UserPasswordValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.keycloakmanager.config.SystemConstant.*;
import static java.lang.Math.ceil;
import static org.apache.commons.lang3.math.NumberUtils.max;

/**
 * A service for working with credentials
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CredentialServiceImpl implements CredentialService {

    private final UserPasswordValidation userPasswordValidation;
    private final Words englishWords;

    @Override
    public String generateUserPassword() {
        String password = "";
        boolean validPass = false;
        // attempt to generate a valid password
        for (int i = 0; i < PASS_GENERATION_MAX_ATTEMPTS && !validPass; i++) {
            password = generatePassphrase(
                    PASS_GENERATION_LENGTH, USER_PASS_WORD_MIN_LENGTH, PASS_GENERATION_WORDS);
            validPass = userPasswordValidation.isValid(password);
        }
        return password;
    }

    @Override
    public String generatePassphrase(int minChars, int minLength, int numWords) {
        List<String> words = new ArrayList<>();
        for (int i = 0; i < numWords; i++) {
            int wordsLeft = numWords - i;
            int charsDone = String.join("-", words).length();
            int averageToComplete = getAverageToComplete(wordsLeft, minChars - charsDone);
            int min = max(averageToComplete, minLength);
            String randomWord = Optional.ofNullable(englishWords.getRandomWordOverLength(min)).orElse("");
            words.add(randomWord);
        }
        return String.join("-", words);
    }

    private int getAverageToComplete(int numWords, int minLength) {
        return (int) ceil((double) minLength / numWords);
    }

    @Override
    public CredentialRepresentation createPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }

}
