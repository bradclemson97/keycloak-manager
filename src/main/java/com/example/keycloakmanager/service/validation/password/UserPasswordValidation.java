package com.example.keycloakmanager.service.validation.password;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.keycloakmanager.config.SystemConstant.*;
import static java.util.Objects.isNull;

/**
 * Implementation of Password Validation for Users.
 */
@Slf4j
@NoArgsConstructor
@Component
public class UserPasswordValidation implements PasswordValidation {

    private final Pattern wordsPattern = Pattern.compile(PASS_WORD_FORMAT);

    @Override
    public boolean isValid(String value) {
        if (isNull(value)
        || !passwordLengthCondition(value)) {
            return false;
        }
        List<String> words = getWords(value);
        return numberWordsCondition(words)
                && noDuplicateWordsCondition(words);
    }

    /**
     * For validating a password's length.
     *
     * @param password the password to be checked
     * @return if the password is of valid length
     */
    public boolean passwordLengthCondition(String password) {
        int minLength = USER_PASS_MIN_LENGTH;
        if (password.length() < minLength) {
            log.error("Password must be at least {} characters long, {} is only {} characters long.",
                    minLength, password, password.length());
            return false;
        }
        return true;
    }

    /**
     * For getting the words in a password phrase.
     *
     * @param password the password containing the words in a phrase.
     * @return a list of words
     */
    public List<String> getWords(String password) {
        Matcher matcher = wordsPattern.matcher(password);
        return matcher.results()
                .map(result -> result.group(1))
                .toList();
    }

    /**
     * For validating the number of words
     *
     * @param words the list of words to be validated
     * @return if there are at least the minimum number of words
     */
    public boolean numberWordsCondition(List<String> words) {
        if (words.size() < USER_PASS_MIN_WORDS) {
            log.error("The password must have at least {} words, this password has {} words",
                    USER_PASS_MIN_WORDS,
                    words.size());
            return false;
        }
        return true;
    }

    /**
     * For validating a list of words has no duplicates.
     *
     * @param words the list of words to be validated
     * @return if all words are unique
     */
    public boolean noDuplicateWordsCondition(List<String> words) {
        Set<String> wordSet = words.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        if (wordSet.size() != words.size()) {
            log.error("No two words should be the same.");
            return false;
        }
        return true;
    }
}
