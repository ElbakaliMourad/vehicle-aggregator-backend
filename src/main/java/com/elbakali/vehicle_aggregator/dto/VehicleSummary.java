package com.elbakali.vehicle_aggregator.dto;

public record VehicleSummary(
        String vin,
        String make,
        String model,
        String year,
        String trim,
        String bodyClass,
        String driveType,
        String fuelType,
        String engineCylinders,
        String displacementLiters
        // We will add the EV battery stuff and Recalls here later!
) {}