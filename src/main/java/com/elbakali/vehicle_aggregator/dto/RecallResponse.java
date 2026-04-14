package com.elbakali.vehicle_aggregator.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

/**
 * A Data Transfer Object (DTO) representing the top-level response from the NHTSA Recalls API.
 * Contains the total count and the array of individual recall summaries.
 */
public record RecallResponse(

        @JsonAlias("Count")
        int count,

        @JsonAlias("Message")
        String message,

        @JsonAlias("Results")
        List<RecallSummary> results
) {
}