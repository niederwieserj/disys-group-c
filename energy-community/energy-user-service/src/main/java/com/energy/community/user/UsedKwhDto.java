package com.energy.community.user;

import java.time.LocalDateTime;

public record UsedKwhDto(String type, String association, double kwh, LocalDateTime timestamp) {
}
