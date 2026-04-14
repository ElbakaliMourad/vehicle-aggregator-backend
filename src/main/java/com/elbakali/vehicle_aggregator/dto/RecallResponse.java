package com.elbakali.vehicle_aggregator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A Data Transfer Object (DTO) representing the top-level response from the NHTSA Recalls API.
 * Contains the total count and the array of individual recall summaries.
 */
public record RecallResponse(

        @JsonProperty("Count")
        int count,

        @JsonProperty("Message")
        String message,

        @JsonProperty("results")
        List<RecallSummary> results
) {
}