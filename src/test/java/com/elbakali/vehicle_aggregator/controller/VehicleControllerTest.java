package com.elbakali.vehicle_aggregator.controller;

import com.elbakali.vehicle_aggregator.dto.RecallResponse;
import com.elbakali.vehicle_aggregator.dto.VehicleSummary;
import com.elbakali.vehicle_aggregator.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.Mockito.when;

/**
 * Integration test suite for the VehicleController.
 * Validates HTTP routing, WebFlux endpoint responses, and JSON serialization.
 */
@WebFluxTest(controllers = VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    // Uses the new Spring 4.0 MockitoBean instead of the deprecated MockBean
    @MockitoBean
    private VehicleService vehicleService;

    @Test
    void getVehicleSummary_Returns200Ok_AndCorrectJson() {
        // --- ARRANGE ---
        String testVin = "1V2GNPE84PC002297";
        VehicleSummary mockSummary = new VehicleSummary(
                testVin, "VW", "ID.4", "2023", "N/A", "SUV", "N/A", "Electric", "N/A", "N/A"
        );

        when(vehicleService.getVehicleSummary(testVin)).thenReturn(Mono.just(mockSummary));

        // --- ACT & ASSERT ---
        webTestClient.get()
                .uri("/api/vehicles/{vin}", testVin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.vin").isEqualTo(testVin)
                .jsonPath("$.make").isEqualTo("VW")
                .jsonPath("$.model").isEqualTo("ID.4");
    }

    @Test
    void getVehicleRecalls_Returns200Ok_AndCorrectJson() {
        // --- ARRANGE ---
        String testVin = "1V2GNPE84PC002297";
        RecallResponse mockResponse = new RecallResponse(0, "Success", Collections.emptyList());

        when(vehicleService.getVehicleRecalls(testVin)).thenReturn(Mono.just(mockResponse));

        // --- ACT & ASSERT ---
        webTestClient.get()
                .uri("/api/vehicles/{vin}/recalls", testVin)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(0)
                .jsonPath("$.message").isEqualTo("Success");
    }
}