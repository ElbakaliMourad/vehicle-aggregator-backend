package com.elbakali.vehicle_aggregator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NhtsaResponse(
        @JsonProperty("Count") int count,
        @JsonProperty("Message") String message,
        @JsonProperty("SearchCriteria") String searchCriteria,
        @JsonProperty("Results") List<NhtsaVariable> results
) {}
