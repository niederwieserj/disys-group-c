package com.energy.community.producer.dto;

import java.time.LocalDateTime;

public record ProducedKwhDto(String type, String association, double kwh, LocalDateTime timestamp) {
}
