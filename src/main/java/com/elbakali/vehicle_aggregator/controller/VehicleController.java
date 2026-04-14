package com.elbakali.vehicle_aggregator.controller;

import com.elbakali.vehicle_aggregator.dto.VehicleSummary;
import com.elbakali.vehicle_aggregator.service.VehicleService;
import com.elbakali.vehicle_aggregator.dto.RecallResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST Controller exposing vehicle data endpoints to the frontend.
 * Handles routing of VIN-based queries for specifications and safety recalls.
 */
@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:4200") // Allows Angular app to talk to this Java app
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Endpoint to retrieve standard specifications for a specific vehicle.
     *
     * @param vin The 17-character Vehicle Identification Number.
     * @return A reactive Mono emitting the vehicle summary payload.
     */
    @GetMapping("/{vin}")
    public Mono<VehicleSummary> getVehicleSummary(@PathVariable String vin) {
        log.info("Rest request received for vin: {}", vin);

        return vehicleService.getVehicleSummary(vin);
    }

    /**
     * Endpoint to retrieve safety recalls for a specific vehicle.
     *
     * @param vin The 17-character Vehicle Identification Number.
     * @return A reactive Mono emitting the safety recall response.
     */
    @GetMapping("/{vin}/recalls")
    public Mono<RecallResponse> getVehicleRecalls(@PathVariable String vin) {
        return vehicleService.getVehicleRecalls(vin);
    }
}
