package com.elbakali.vehicle_aggregator.service;

import com.elbakali.vehicle_aggregator.client.NhtsaClient;
import com.elbakali.vehicle_aggregator.dto.NhtsaVariable;
import com.elbakali.vehicle_aggregator.dto.VehicleSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    // Lombok automatically builds the constructor for this
    private final NhtsaClient nhtsaClient;

    public Mono<VehicleSummary> getVehicleSummary(String vin) {
        log.info("Requesting vehicle data for VIN: {}", vin);

        // Fetch the data, then MAP it from the messy NhtsaResponse to our clean VehicleSummary
        return nhtsaClient.fetchVehicleDetails(vin)
                .map(response -> buildSummary(vin, response.results()));
    }

    // This method acts as the mapping factory
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

    // Java Stream to safely search the 140 items
    private String extractValue(List<NhtsaVariable> results, int targetId) {
        if (results == null) return "N/A";

        return results.stream()
                .filter(var -> var.variableId() != null && var.variableId() == targetId)
                .filter(var -> var.value() != null) // Safely drop any null values sent by the API
                .map(NhtsaVariable::value)
                .findFirst()
                .orElse("N/A"); // Returns "N/A" if the item was missing or dropped
    }
}