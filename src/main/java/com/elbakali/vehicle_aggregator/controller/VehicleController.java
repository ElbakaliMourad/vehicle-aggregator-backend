package com.elbakali.vehicle_aggregator.controller;

import com.elbakali.vehicle_aggregator.dto.VehicleSummary;
import com.elbakali.vehicle_aggregator.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "http://localhost:4200") // Allows Angular app to talk to this Java app
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/{vin}")
    public Mono<VehicleSummary> getVehicleSummary(@PathVariable String vin) {
        log.info("Rest request received for vin: {}", vin);

        return vehicleService.getVehicleSummary(vin);
    }
}
