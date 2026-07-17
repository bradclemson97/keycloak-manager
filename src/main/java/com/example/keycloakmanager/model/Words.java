package com.example.keycloakmanager.model;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Stores a large dataset of words indexed for efficient random selection by minimum length.
 */
@Slf4j
public class Words {

    private final List<String> sortedWords;
    private final int[] sortedLengths;
    private final SecureRandom random = new SecureRandom();

    /**
     * Builds word index sorted by word length for O(log n) minimum-length lookups.
     *
     * @param wordLengthMap a map of words to their lengths.
     */
    public Words(Map<String, Integer> wordLengthMap) {
        List<Map.Entry<String, Integer>> entries = wordLengthMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .toList();

        sortedWords = entries.stream().map(Map.Entry::getKey).toList();
        sortedLengths = entries.stream().mapToInt(Map.Entry::getValue).toArray();
    }

    /**
     * Returns a random word of at least {@code minLength} characters in O(log n) time.
     *
     * @param minLength the minimum character length of the word to return.
     * @return a randomly selected word, or {@code null} if no word meets the length requirement.
     */
    public String getRandomWordOverLength(int minLength) {
        int startIndex = lowerBound(sortedLengths, minLength);
        int available = sortedWords.size() - startIndex;

        if (available <= 0) {
            log.warn("No valid words found that are {} characters or longer", minLength);
            return null;
        }

        log.debug("Found {} words that are {} characters or longer", available, minLength);
        return sortedWords.get(startIndex + random.nextInt(available));
    }

    private int lowerBound(int[] lengths, int target) {
        int lo = 0, hi = lengths.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (lengths[mid] < target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
