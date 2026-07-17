package com.example.keycloakmanager.service.validation.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserPasswordValidationTest {

    private UserPasswordValidation validation;

    @BeforeEach
    void setUp() {
        validation = new UserPasswordValidation();
    }

    @Test
    void isValid_returnsFalse_whenNull() {
        assertThat(validation.isValid(null)).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenTooShort() {
        assertThat(validation.isValid("short-pass")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenTooFewWords() {
        // 17+ chars but only 2 words
        assertThat(validation.isValid("longword-anotherlongword")).isFalse();
    }

    @Test
    void isValid_returnsFalse_whenDuplicateWords() {
        assertThat(validation.isValid("apple-orange-apple-mango")).isFalse();
    }

    @Test
    void isValid_returnsTrue_whenValidPassphrase() {
        assertThat(validation.isValid("apple-orange-mango-grape")).isTrue();
    }

    @Test
    void passwordLengthCondition_returnsFalse_whenBelowMinimum() {
        assertThat(validation.passwordLengthCondition("short")).isFalse();
    }

    @Test
    void passwordLengthCondition_returnsTrue_whenAtMinimum() {
        String exactly17 = "a".repeat(17);
        assertThat(validation.passwordLengthCondition(exactly17)).isTrue();
    }

    @Test
    void getWords_parsesHyphenSeparatedWords() {
        List<String> words = validation.getWords("apple-banana-cherry");
        assertThat(words).containsExactly("apple", "banana", "cherry");
    }

    @Test
    void numberWordsCondition_returnsFalse_whenTooFew() {
        assertThat(validation.numberWordsCondition(List.of("one", "two"))).isFalse();
    }

    @Test
    void numberWordsCondition_returnsTrue_whenSufficientWords() {
        assertThat(validation.numberWordsCondition(List.of("one", "two", "three"))).isTrue();
    }

    @Test
    void noDuplicateWordsCondition_returnsFalse_whenDuplicatesPresent() {
        assertThat(validation.noDuplicateWordsCondition(List.of("apple", "Apple"))).isFalse();
    }

    @Test
    void noDuplicateWordsCondition_returnsTrue_whenAllUnique() {
        assertThat(validation.noDuplicateWordsCondition(List.of("apple", "banana", "cherry"))).isTrue();
    }
}
