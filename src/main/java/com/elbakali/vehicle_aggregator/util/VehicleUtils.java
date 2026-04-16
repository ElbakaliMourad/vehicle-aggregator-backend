package com.elbakali.vehicle_aggregator.util;

import org.springframework.util.StringUtils;

/**
 * Utility class for normalizing and formatting raw vehicle data.
 * Designed to clean up inconsistent string formats received from external APIs
 * (like the NHTSA databases) before sending them to the frontend UI.
 */
public class VehicleUtils {

    /**
     * Private constructor to prevent instantiation.
     * Utility classes should only contain static methods and never be instantiated
     * as an object (e.g., 'new VehicleUtils()').
     */
    private VehicleUtils() {}

    /**
     * Converts a messy string into standard Title Case.
     * Example: "VOLKSWAGEN" -> "Volkswagen", "mercedes benz" -> "Mercedes Benz"
     *
     * @param text The raw string to be formatted.
     * @return The formatted string in Title Case, or "N/A" if the input is null/empty.
     */
    public static String formatTitleCase(String text) {
        // Fallback for missing data
        if (!StringUtils.hasText(text)) {
            return "N/A";
        }

        // Force everything to lowercase and split into an array of words based on spaces
        String[] words = text.toLowerCase().split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize the first letter and append the rest of the word
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    /**
     * Parses a raw displacement string and formats it to a single decimal place.
     * Example: "2.480000" -> "2.5L"
     *
     * @param displacement The raw displacement string.
     * @return The rounded displacement string appended with "L", or the original string if parsing fails.
     */
    public static String formatDisplacement(String displacement) {
        if (!StringUtils.hasText(displacement)) {
            return "N/A";
        }

        try {
            double value = Double.parseDouble(displacement);

            // Format to exactly one decimal place (e.g., 2.5) and append 'L'
            return String.format("%.1fL", value);
        } catch (NumberFormatException e) {
            // If the API returns text instead of a number, safely return the raw text
            return displacement;
        }
    }

    /**
     * Maps long or inconsistent drive type descriptions to standard 3-character abbreviations.
     * Example: "RWD/Rear-Wheel Drive" -> "RWD"
     *
     * @param driveType The raw drive type string.
     * @return The standard abbreviation (RWD, FWD, AWD, 4WD), or the original string if no match is found.
     */
    public static String normalizeDriveType(String driveType) {
        if (!StringUtils.hasText(driveType)) {
            return "N/A";
        }

        // Force to uppercase immediately to avoid case-sensitive matching issues
        String upper = driveType.toUpperCase();

        // Check for common keywords and map them to standard abbreviations
        if (upper.contains("RWD") || upper.contains("REAR")) {
            return "RWD";
        } else if (upper.contains("FWD") || upper.contains("FRONT")) {
            return "FWD";
        } else if (upper.contains("AWD") || upper.contains("ALL")) {
            return "AWD";
        } else if (upper.contains("4WD") || upper.contains("4X4")) {
            return "4WD";
        }

        // Return the original string if it doesn't match our expected formats
        return driveType;
    }

    /**
     * Normalizes complex NHTSA body class descriptions into clean, single-word categories.
     * Example: "Sport Utility Vehicle (SUV)/Multi-Purpose Vehicle (MPV)" -> "SUV"
     *
     * @param bodyClass The raw body class string.
     * @return A simplified body class, or the original string if no match is found.
     */
    public static String normalizeBodyClass(String bodyClass) {
        if (!StringUtils.hasText(bodyClass)) {
            return "N/A";
        }

        // Force to uppercase immediately to avoid case-sensitive matching issues
        String upper = bodyClass.toUpperCase();

        // Check for common keywords using ALL CAPS
        if (upper.contains("SPORT UTILITY") || upper.contains("SUV")) {
            return "SUV";
        } else if (upper.contains("SEDAN")) {
            return "Sedan";
        } else if (upper.contains("PICKUP") || upper.contains("TRUCK")) {
            return "Pickup";
        } else if (upper.contains("HATCHBACK")) {
            return "Hatchback";
        } else if (upper.contains("COUPE")) {
            return "Coupe";
        } else if (upper.contains("MULTI-PURPOSE") || upper.contains("MPV") || upper.contains("VAN")) {
            return "Van/MPV";
        }

        // Return the original string formatted nicely if it doesn't match our specific list
        return formatTitleCase(bodyClass);
    }
}