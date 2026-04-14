package com.elbakali.vehicle_aggregator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Data Transfer Object (DTO) representing a single vehicle safety recall.
 * Maps the raw JSON fields from the NHTSA API into clean Java variables.
 */
public record RecallSummary(

        @JsonProperty("NHTSACampaignNumber")
        String campaignNumber,

        @JsonProperty("ReportReceivedDate")
        String reportReceivedDate,

        @JsonProperty("Component")
        String component,

        @JsonProperty("Summary")
        String summary,

        @JsonProperty("Consequence")
        String consequence,

        @JsonProperty("Remedy")
        String remedy
) {
}