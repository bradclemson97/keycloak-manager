package com.example.keycloakmanager.config;

import com.example.keycloakmanager.model.Words;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;

/**
 * Configuration class for system passwords.
 */
@Configuration
@RequiredArgsConstructor
public class PasswordConfig {

    private final ObjectMapper objectMapper;

    @Value("classpath:data/english-words.json")
    private Resource englishWordsResource;

    /**
     * Provides an {@link Words} instance generating a random english word.
     *
     * @return An instance of {@link ObjectMapper} configured for JSON serialisation/deserialisation.
     */
    @Bean
    public Words englishWords() throws IOException {
        HashMap<String, Integer> wordLengthMap = objectMapper.readValue(
                englishWordsResource.getInputStream(),
                new TypeReference<>() {});
        return new Words(wordLengthMap);
    }
}
