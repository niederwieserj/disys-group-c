package com.energy.community.restapi.service;

import com.energy.community.restapi.dto.EnergyDto;
import com.energy.community.restapi.dto.PercentageDto;
import com.energy.community.restapi.entity.EnergyEntity;
import com.energy.community.restapi.entity.PercentageEntity;
import com.energy.community.restapi.repository.EnergyRepository;
import com.energy.community.restapi.repository.PercentageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EnergyService {

    private final EnergyRepository energyRepository;
    private final PercentageRepository percentageRepository;

    public EnergyService(
            EnergyRepository energyRepository,
            PercentageRepository percentageRepository
    ) {
        this.energyRepository = energyRepository;
        this.percentageRepository = percentageRepository;
    }

    public List<EnergyDto> getHistoricalEnergy(
            LocalDateTime start,
            LocalDateTime end
    ) {
        return energyRepository
                .findEnergyEntitiesByHourBetween(start, end)
                .stream()
                .map(this::mapEnergyEntityToDto)
                .toList();
    }

    public List<PercentageDto> getCurrentPercentage() {
        LocalDateTime currentHour =
                LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        LocalDateTime nextHour =
                currentHour.plusHours(1);

        return percentageRepository
                .findPercentageEntitiesByHourBetween(
                        currentHour,
                        nextHour
                )
                .stream()
                .map(this::mapPercentageEntityToDto)
                .toList();
    }

    private EnergyDto mapEnergyEntityToDto(EnergyEntity entity) {
        return new EnergyDto(
                entity.getId(),
                entity.getHour(),
                entity.getCommunityProduced(),
                entity.getCommunityUsed(),
                entity.getGridUsed()
        );
    }

    private PercentageDto mapPercentageEntityToDto(
            PercentageEntity entity
    ) {
        return new PercentageDto(
                entity.getHour(),
                entity.getCommunityDepleted(),
                entity.getGridPortion()
        );
    }
}