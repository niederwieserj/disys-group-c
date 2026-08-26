package com.energy.community.guiapp.dto;

public record EnergySummary(
        double communityProduced,
        double communityUsed,
        double gridUsed
) {
}