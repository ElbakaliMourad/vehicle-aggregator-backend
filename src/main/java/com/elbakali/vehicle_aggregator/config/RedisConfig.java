package com.elbakali.vehicle_aggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for Reactive Redis integration.
 * * In a WebFlux (reactive) application, we cannot use traditional blocking cache managers
 * (like @Cacheable) because they freeze the executing thread. Instead, we configure a
 * ReactiveRedisTemplate here to interact with Redis asynchronously.
 * * This template is designed to be "universal" (<String, Object>), meaning it can serialize
 * and cache any Java object (VehicleSummary, Recalls, etc.) without needing a separate
 * configuration for each specific DTO type.
 */
@Configuration
public class RedisConfig {

    /**
     * Configures the universal ReactiveRedisTemplate.
     * * By default, Redis stores data as raw byte arrays. This bean defines the "translators"
     * (serializers) that convert our Java objects into a format Redis can store, and vice versa.
     *
     * @param factory The reactive connection factory automatically provided by Spring Boot.
     * @return A fully configured template ready to be injected into our Service classes.
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        // 1. Key Serializer: We use standard Strings for keys (e.g., "vehicle::1VW...").
        // This ensures the keys remain highly readable if a developer needs to manually
        // inspect the database using the Redis CLI command line tool.
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // 2. Value Serializer: We use the modern Spring Data Redis 4.0+ generic JSON serializer.
        // Unlike older serializers, this one automatically embeds the Java class type
        // (e.g., "@class": "com...VehicleSummary") directly into the JSON payload.
        // This ensures Spring knows exactly which object to rebuild when reading from the cache.
        GenericJacksonJsonRedisSerializer valueSerializer = GenericJacksonJsonRedisSerializer.builder().build();

        // 3. Assemble the Serialization Context: We bind the key and value serializers together.
        // The builder is explicitly typed to <String, Object> to allow maximum flexibility
        // for scaling the application with future features.
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, Object> context = builder.value(valueSerializer).build();

        // 4. Return the configured template, attaching it to the active Redis connection.
        return new ReactiveRedisTemplate<>(factory, context);
    }
}