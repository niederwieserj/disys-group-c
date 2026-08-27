package com.energy.community.usage.service;

import com.energy.community.usage.dto.EnergyMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EnergyMessageParser {

    private final ObjectMapper objectMapper;

    public EnergyMessageParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public EnergyMessageDto parse(String message) throws IOException {
        return objectMapper.readValue(message, EnergyMessageDto.class);
    }
}