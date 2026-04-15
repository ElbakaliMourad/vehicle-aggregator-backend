package com.elbakali.vehicle_aggregator.service;

import com.elbakali.vehicle_aggregator.client.NhtsaClient;
import com.elbakali.vehicle_aggregator.dto.RecallResponse;
import com.elbakali.vehicle_aggregator.dto.VehicleSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test suite for the VehicleService class.
 * Validates the reactive WebFlux pipeline, ensuring that distributed caching (Redis)
 * and external API calls (NhtsaClient) are orchestrated correctly without executing
 * blocking operations.
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private NhtsaClient nhtsaClient;

    @Mock
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private VehicleService vehicleService;

    /**
     * Initializes the testing environment before each individual test runs.
     * Hooks the mocked Redis value operations into the main Redis template.
     */
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /**
     * Verifies the fallback logic of the getVehicleRecalls method.
     * Scenario: A user requests recalls for a VIN that does NOT exist in the Redis cache.
     * Expected Result: The service should query the NHTSA API, save the new data to Redis,
     * and emit the response back to the subscriber.
     */
    @Test
    void getVehicleRecalls_OnCacheMiss_FetchesFromApiAndCaches() {

        // --- ARRANGE: Construct mock objects and define external behavior ---
        String testVin = "1V2GNPE84PC002297";
        VehicleSummary fakeSummary = new VehicleSummary(
                testVin, "VOLKSWAGEN", "ID.4", "2023", "N/A", "SUV", "N/A", "Electric", "N/A", "N/A"
        );
        RecallResponse fakeRecallResponse = new RecallResponse(2, "Success", Collections.emptyList());

        // Simulate a cache MISS for the recall data
        when(valueOperations.get("recalls::" + testVin)).thenReturn(Mono.empty());

        // Simulate a cache HIT for the vehicle summary (required to extract Make/Model/Year)
        when(valueOperations.get("vehicle::" + testVin)).thenReturn(Mono.just(fakeSummary));
        when(objectMapper.convertValue(fakeSummary, VehicleSummary.class)).thenReturn(fakeSummary);

        // Define the behavior of our mocked NHTSA client when called with the extracted data
        when(nhtsaClient.fetchRecalls("VOLKSWAGEN", "ID.4", "2023"))
                .thenReturn(Mono.just(fakeRecallResponse));

        // Simulate a successful save operation to the Redis cache
        when(valueOperations.set(anyString(), any(), any())).thenReturn(Mono.just(true));

        // --- ACT & ASSERT: Subscribe to the Mono and verify the emitted signals ---
        StepVerifier.create(vehicleService.getVehicleRecalls(testVin))
                .expectNextMatches(response ->
                        response.count() == 2 && response.message().equals("Success")
                )
                .verifyComplete();
    }
}