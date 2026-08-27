package com.energy.community.usage.dto;

import java.time.LocalDateTime;

public record EnergyMessageDto(
        String type,
        String association,
        double kwh,
        LocalDateTime datetime
) {
}