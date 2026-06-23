package com.energy.community.current.percentage.repository;

import com.energy.community.current.percentage.entity.EnergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface EnergyRepository extends JpaRepository<EnergyEntity, Integer> {
    List<EnergyEntity> findEnergyEntitiesByHourBetween(LocalDateTime start, LocalDateTime end);
}
