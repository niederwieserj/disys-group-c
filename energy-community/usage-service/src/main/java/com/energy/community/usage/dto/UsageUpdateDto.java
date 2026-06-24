package com.energy.community.usage.dto;

import java.time.LocalDateTime;

public record UsageUpdateDto(
        LocalDateTime hour,
        double communityProduced,
        double communityUsed,
        double gridUsed
) {
}
