package com.energy.community.usageservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EnergyHourlyMetricRepository extends JpaRepository<EnergyHourlyMetric, Integer> {

    Optional<EnergyHourlyMetric> findByHour(LocalDateTime hour);
}