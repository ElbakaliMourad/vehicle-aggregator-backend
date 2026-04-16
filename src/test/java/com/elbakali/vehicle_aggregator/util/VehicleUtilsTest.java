package com.elbakali.vehicle_aggregator.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the VehicleUtils utility class.
 * Since this is a pure utility class, these tests run instantly without needing
 * to start up the entire Spring Boot application context.
 */
class VehicleUtilsTest {

    /**
     * Verifies that raw strings are properly converted to Title Case.
     * Tests standard inputs as well as null and empty string edge cases to ensure
     * the application does not throw a NullPointerException.
     */
    @Test
    void testFormatTitleCase() {
        // Test standard messy strings from external APIs
        assertEquals("Volkswagen", VehicleUtils.formatTitleCase("VOLKSWAGEN"));
        assertEquals("Mercedes Benz", VehicleUtils.formatTitleCase("mercedes benz"));

        // Test edge cases (null values and empty spaces) to verify our fallback logic
        assertEquals("N/A", VehicleUtils.formatTitleCase(null));
        assertEquals("N/A", VehicleUtils.formatTitleCase("   "));
    }

    /**
     * Verifies that raw displacement strings (often with many decimal places)
     * are rounded cleanly to a single decimal place with an 'L' appended.
     */
    @Test
    void testFormatDisplacement() {
        // Test mathematical rounding up and down
        assertEquals("2.5L", VehicleUtils.formatDisplacement("2.480000"));
        assertEquals("3.0L", VehicleUtils.formatDisplacement("3.01"));

        // Test error handling: what happens if the API sends text instead of numbers
        assertEquals("Unknown", VehicleUtils.formatDisplacement("Unknown"));
        assertEquals("N/A", VehicleUtils.formatDisplacement(""));
    }

    /**
     * Verifies that long, inconsistent government drive-type descriptions
     * are accurately mapped to standard 3-character abbreviations.
     */
    @Test
    void testNormalizeDriveType() {
        // Test common variations of drive types mapped to clean abbreviations
        assertEquals("RWD", VehicleUtils.normalizeDriveType("RWD/Rear-Wheel Drive"));
        assertEquals("FWD", VehicleUtils.normalizeDriveType("Front-Wheel Drive"));
        assertEquals("AWD", VehicleUtils.normalizeDriveType("All-Wheel Drive"));
        assertEquals("4WD", VehicleUtils.normalizeDriveType("4X4"));

        // Test fallback logic if the API returns a drive type not in our keyword list
        assertEquals("Custom Drive", VehicleUtils.normalizeDriveType("Custom Drive"));

        // Test null safety
        assertEquals("N/A", VehicleUtils.normalizeDriveType(null));
    }

    /**
     * Verifies that long, complex body class descriptions from the government API
     * are accurately simplified into user-friendly UI categories.
     */
    @Test
    void testNormalizeBodyClass() {
        // Test the exact messy string we found from the NHTSA API
        assertEquals("SUV", VehicleUtils.normalizeBodyClass("Sport Utility Vehicle (SUV)/Multi-Purpose Vehicle (MPV)"));

        // Test other common variations
        assertEquals("Sedan", VehicleUtils.normalizeBodyClass("Sedan/Saloon"));
        assertEquals("Pickup", VehicleUtils.normalizeBodyClass("Pickup Truck"));

        // Test our fallback logic (if it doesn't match, it should Title Case it)
        assertEquals("Motorcycle", VehicleUtils.normalizeBodyClass("MOTORCYCLE"));

        // Test null safety
        assertEquals("N/A", VehicleUtils.normalizeBodyClass(null));
    }
}