package com.energy.community.restapi.controller;

import com.energy.community.restapi.dto.EnergyDto;
import com.energy.community.restapi.dto.PercentageDto;
import com.energy.community.restapi.entity.EnergyEntity;
import com.energy.community.restapi.entity.PercentageEntity;
import com.energy.community.restapi.repository.EnergyRepository;
import com.energy.community.restapi.repository.PercentageRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RestController
public class EnergyController {

    private final EnergyRepository energyRepository;
    private final PercentageRepository percentageRepository;

    public EnergyController(EnergyRepository energyRepository, PercentageRepository percentageRepository) {
        this.energyRepository = energyRepository;
        this.percentageRepository = percentageRepository;
    }

    @GetMapping("/energy/historical")
    public List<EnergyDto> getEnergyHistorical(
            @RequestParam(name="start") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(name= "end") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return filterEnergyByHour(start, end);
    }

    @GetMapping("/energy/current")
    public List<PercentageDto> getEnergyCurrent() {
        LocalDateTime hourStart = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime hourEnd = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS);
        return filterPercentageByHour(hourStart, hourEnd);
    }

    private List<PercentageDto> filterPercentageByHour(LocalDateTime start, LocalDateTime end) {
        return this.percentageRepository.findPercentageEntitiesByHourBetween(start, end)
                .stream()
                .map(this::mapPercentageEntityToDto)
                .toList();
    }

    private List<EnergyDto> filterEnergyByHour(LocalDateTime start, LocalDateTime end) {
        return this.energyRepository.findEnergyEntitiesByHourBetween(start, end)
                .stream()
                .map(this::mapEnergyEntityToDto)
                .toList();
    }

    private PercentageDto mapPercentageEntityToDto(PercentageEntity entity) {
        return new PercentageDto(
                entity.getHour(),
                entity.getCommunityDepleted(),
                entity.getGridPortion()
        );
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
}
