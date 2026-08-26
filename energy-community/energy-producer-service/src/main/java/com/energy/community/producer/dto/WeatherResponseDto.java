package com.energy.community.producer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponseDto(
        Hourly hourly
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hourly(
            @JsonProperty("sunshine_duration")
            List<Double> sunshineDuration
    ) {
    }
}