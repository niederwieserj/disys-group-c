package com.energy.community.usage.repository;

import com.energy.community.usage.entity.EnergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EnergyRepository extends JpaRepository<EnergyEntity, Integer> {
    Optional<EnergyEntity> findByHour(LocalDateTime hour);
}
