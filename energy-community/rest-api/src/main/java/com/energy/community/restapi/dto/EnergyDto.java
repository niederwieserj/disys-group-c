package com.energy.community.restapi.dto;

import java.time.LocalDateTime;

public record EnergyDto(int id, LocalDateTime hour, double community_produced, double community_used, double grid_used) {

}
