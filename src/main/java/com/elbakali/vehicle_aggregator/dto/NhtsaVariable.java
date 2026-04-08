package com.elbakali.vehicle_aggregator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NhtsaVariable(
        @JsonProperty("Value") String value,
        @JsonProperty("ValueId") String valueId,
        @JsonProperty("Variable") String variable,
        @JsonProperty("VariableId") Integer variableId
) {}
