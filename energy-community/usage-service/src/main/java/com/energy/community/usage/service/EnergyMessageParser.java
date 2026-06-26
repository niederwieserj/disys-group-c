package com.energy.community.usage.service;

import com.energy.community.usage.dto.EnergyMessageDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class EnergyMessageParser {

    private final ObjectMapper objectMapper;

    public EnergyMessageParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public EnergyMessageDto parse(String message) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(message);
        String type = jsonNode.path("type").asText();
        String association = jsonNode.path("association").asText();
        double kwh = jsonNode.path("kwh").asDouble();
        LocalDateTime timestamp = parseTimestamp(jsonNode);

        return new EnergyMessageDto(type, association, kwh, timestamp);
    }

    private LocalDateTime parseTimestamp(JsonNode jsonNode) {
        JsonNode timestampNode = jsonNode.hasNonNull("timestamp")
                ? jsonNode.path("timestamp")
                : jsonNode.path("datetime");

        return LocalDateTime.parse(timestampNode.asText());
    }
}
