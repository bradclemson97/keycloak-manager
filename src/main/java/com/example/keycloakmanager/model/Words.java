package com.example.keycloakmanager.model;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * A class for storing a large dataset of words by their length and methods for getting random
 * words from that dataset.
 */
@Slf4j
public class Words {

    private final TreeMap<Integer, HashSet<String>> lengthWordMap;

    private final SecureRandom random = new SecureRandom();

    /**
     * Constructor which converts a {@link Map} dataset of words and their lengths into a
     * {@link TreeMap} of lengths mapped to a {@link HashSet} of words of the given length.
     *
     * @param wordLengthMap a map containing words and their lengths.
     */
    public Words(Map<String, Integer> wordLengthMap) {
        lengthWordMap = wordLengthMap.entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        TreeMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toCollection(HashSet::new))
                ));
    }

    /**
     * Selects a random word using {@link java.util.Random} over the length given from the stored dataset.
     *
     * @param minLength the minimum length a randomly selected word can be.
     * @return the randomly selected word.
     */
    public String getRandomWordOverLength(int minLength) {
        // Get a sub-map containing all lengths greater than or equal to minLength
        // Collect all words with the length greater than or equal to minLength
        HashSet<String> words = lengthWordMap.tailMap(minLength)
                .values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(HashSet::new));

        if (words.isEmpty()) {
            log.warn("No valid words found that are {} characters or longer", minLength);
            return null;
        }

        int size = words.size();
        log.debug("Found {} words that are {} characters or longer", size, minLength);

        // Select a random word from the list of words
        int randomWordIndex = random.nextInt(size);
        return words.stream().skip(randomWordIndex).findFirst().orElse(null);

    }
}
