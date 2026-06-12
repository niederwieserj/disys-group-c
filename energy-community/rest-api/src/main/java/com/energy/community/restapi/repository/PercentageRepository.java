package com.energy.community.restapi.repository;

import com.energy.community.restapi.entity.PercentageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PercentageRepository extends JpaRepository<PercentageEntity, Integer> {
    List<PercentageEntity> findPercentageEntitiesByHourBetween(LocalDateTime start, LocalDateTime end);
}
