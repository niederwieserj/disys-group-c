package com.energy.community.restapi.dto;

import java.time.LocalDateTime;

public record PercentageDto(LocalDateTime hour, double community_depleted, double grid_portion) {
}
