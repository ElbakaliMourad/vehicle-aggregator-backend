package com.elbakali.vehicle_aggregator.service;

import com.elbakali.vehicle_aggregator.client.NhtsaClient;
import com.elbakali.vehicle_aggregator.dto.NhtsaVariable;
import com.elbakali.vehicle_aggregator.dto.VehicleSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

/**
 * Service layer responsible for orchestrating vehicle data retrieval.
 * Implements a reactive cache-aside pattern to minimize external API calls
 * to the NHTSA database while maintaining a fully non-blocking WebFlux pipeline.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final NhtsaClient nhtsaClient;

    // Universal caching template configured for generic Object serialization
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "vehicle::";

    // Configured Time-To-Live (TTL) for cached vehicle profiles
    private static final Duration CACHE_TTL = Duration.ofDays(30);

    /**
     * Retrieves a summarized vehicle profile based on the provided VIN.
     * First attempts to resolve the request from the Redis cache. On a cache miss,
     * queries the NHTSA API, maps the response, and populates the cache.
     *
     * @param vin The sanitized 17-character Vehicle Identification Number.
     * @return A Mono emitting the populated VehicleSummary.
     */
    public Mono<VehicleSummary> getVehicleSummary(String vin) {
        String cacheKey = CACHE_PREFIX + vin;

        // Attempt to resolve the request from the distributed Redis cache
        return redisTemplate.opsForValue().get(cacheKey)
                .doOnNext(obj -> log.info("Cache hit for VIN: {}", vin))

                // Properly convert the generic LinkedHashMap into the strongly-typed DTO
                .map(obj -> objectMapper.convertValue(obj, VehicleSummary.class))

                // On cache miss, lazily evaluate the fallback mechanism.
                // Mono.defer() ensures the external API call is only subscribed to if the cache is actually empty.
                .switchIfEmpty(Mono.defer(() -> fetchAndCacheVehicle(vin, cacheKey)));
    }

    /**
     * Fallback method invoked on a cache miss. Fetches data from the external API,
     * transforms the payload, and persists it to Redis.
     */
    private Mono<VehicleSummary> fetchAndCacheVehicle(String vin, String cacheKey) {
        log.info("Cache miss. Fetching from NHTSA API for VIN: {}", vin);

        return nhtsaClient.fetchVehicleDetails(vin)
                .map(response -> buildSummary(vin, response.results()))
                .flatMap(summary ->
                        // Asynchronously persist the mapped DTO to the Redis cache with the configured TTL,
                        // then emit the result to the downstream subscriber.
                        redisTemplate.opsForValue().set(cacheKey, summary, CACHE_TTL)
                                .thenReturn(summary)
                );
    }

    /**
     * Maps the raw NHTSA variable list into the application's normalized DTO.
     */
    private VehicleSummary buildSummary(String vin, List<NhtsaVariable> results) {
        return new VehicleSummary(
                vin,
                extractValue(results, 26), // Make
                extractValue(results, 28), // Model
                extractValue(results, 29), // Model Year
                extractValue(results, 38), // Trim
                extractValue(results, 5),  // Body Class
                extractValue(results, 15), // Drive Type
                extractValue(results, 24), // Fuel Type - Primary
                extractValue(results, 9),  // Engine Number of Cylinders
                extractValue(results, 13)  // Displacement (L)
        );
    }

    /**
     * Safely extracts a specific variable value from the NHTSA response payload.
     * Filters out null values and returns a default "N/A" string if the target ID is missing.
     */
    private String extractValue(List<NhtsaVariable> results, int targetId) {
        if (results == null) return "N/A";

        return results.stream()
                .filter(var -> var.variableId() != null && var.variableId() == targetId)
                .filter(var -> var.value() != null)
                .map(NhtsaVariable::value)
                .findFirst()
                .orElse("N/A");
    }
}