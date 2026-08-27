package com.energy.community.current.percentage.repository;

import com.energy.community.current.percentage.entity.PercentageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PercentageRepository extends JpaRepository<PercentageEntity, LocalDateTime> {

    List<PercentageEntity> findPercentageEntitiesByHourBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<PercentageEntity> findByHourNot(LocalDateTime hour);
}