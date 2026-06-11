package com.energy.community.restapi.repository;

import com.energy.community.restapi.dto.EnergyDto;
import com.energy.community.restapi.entity.EnergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

// JpaRepository<Table, ID> -> type of table, type of ID
public interface EnergyRepository extends JpaRepository<EnergyEntity, Integer> {

    List<EnergyEntity> findEnergyEntitiesByHour(LocalDateTime hour);

    List<EnergyEntity> findEnergyEntitiesByHourBetween(LocalDateTime start, LocalDateTime end);
}
