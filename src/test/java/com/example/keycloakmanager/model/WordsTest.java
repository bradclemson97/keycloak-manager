package com.example.keycloakmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WordsTest {

    private Words words;

    @BeforeEach
    void setUp() {
        words = new Words(Map.of(
                "hi", 2,
                "cat", 3,
                "dog", 3,
                "fish", 4,
                "eagle", 5,
                "parrot", 6
        ));
    }

    @Test
    void getRandomWordOverLength_returnsNull_whenNoWordsQualify() {
        assertThat(words.getRandomWordOverLength(100)).isNull();
    }

    @Test
    void getRandomWordOverLength_returnsWord_atExactMinLength() {
        String result = words.getRandomWordOverLength(6);
        assertThat(result).isEqualTo("parrot");
    }

    @Test
    void getRandomWordOverLength_returnsWordWithinRange_whenMultipleQualify() {
        String result = words.getRandomWordOverLength(3);
        assertThat(result).isIn("cat", "dog", "fish", "eagle", "parrot");
    }

    @Test
    void getRandomWordOverLength_excludesWordsBelowMinLength() {
        for (int i = 0; i < 50; i++) {
            String result = words.getRandomWordOverLength(4);
            assertThat(result).isNotIn("hi", "cat", "dog");
        }
    }

    @Test
    void getRandomWordOverLength_includesAllWordsAboveMinLength() {
        // Run enough times to see all qualifying words appear
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (int i = 0; i < 500; i++) {
            seen.add(words.getRandomWordOverLength(2));
        }
        assertThat(seen).contains("hi", "cat", "dog", "fish", "eagle", "parrot");
    }
}
