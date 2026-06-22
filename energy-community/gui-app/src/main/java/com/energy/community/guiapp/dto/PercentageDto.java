package com.energy.community.guiapp.dto;

import java.time.LocalDateTime;

public record PercentageDto(LocalDateTime hour, double community_depleted, double grid_portion) {
}
