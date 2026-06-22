package com.energy.community.common;

import java.time.LocalDateTime;

public record EnergyMessage(
        EnergyMessageType type,
        Association association,
        double kwh,
        LocalDateTime datetime
) {
}