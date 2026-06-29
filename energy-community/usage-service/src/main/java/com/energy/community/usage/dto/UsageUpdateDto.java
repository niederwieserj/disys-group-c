package com.energy.community.usage.dto;

import java.time.LocalDateTime;

public record UsageUpdateDto(
        LocalDateTime hour,
        double community_produced,
        double community_used,
        double grid_used
) {
}
