package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson Configuration for Java 8 Date/Time Support
 * 
 * This configuration class sets up Jackson ObjectMapper to properly handle
 * Java 8 date/time types like LocalDateTime, LocalDate, etc.
 * 
 * Without this configuration, Jackson would throw exceptions when trying to
 * serialize/deserialize these types in JSON.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configure ObjectMapper with Java 8 date/time support
     * 
     * @return Configured ObjectMapper instance
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Register JavaTimeModule to handle Java 8 date/time types
        // This enables proper serialization/deserialization of:
        // - LocalDateTime
        // - LocalDate
        // - LocalTime
        // - Instant
        // - Duration
        // - Period
        objectMapper.registerModule(new JavaTimeModule());
        
        return objectMapper;
    }
}
