package com.energy.community.common;

import java.time.LocalDateTime;

public record UsageUpdateMessage(
        LocalDateTime hour
) {
}