package com.elbakali.vehicle_aggregator.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * A Data Transfer Object (DTO) representing a single vehicle safety recall.
 * Maps the raw JSON fields from the NHTSA API into clean Java variables.
 */
public record RecallSummary(

        @JsonAlias("NHTSACampaignNumber")
        String campaignNumber,

        @JsonAlias("ReportReceivedDate")
        String reportReceivedDate,

        @JsonAlias("Component")
        String component,

        @JsonAlias("Summary")
        String summary,

        @JsonAlias("Consequence")
        String consequence,

        @JsonAlias("Remedy")
        String remedy
) {
}