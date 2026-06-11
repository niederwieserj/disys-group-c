package com.energy.community.restapi.controller;

import com.energy.community.restapi.dto.EnergyDto;
import com.energy.community.restapi.entity.EnergyEntity;
import com.energy.community.restapi.repository.EnergyRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RestController
public class EnergyController {

    private final EnergyRepository energyRepository;

    public EnergyController(EnergyRepository energyRepository) {
        this.energyRepository = energyRepository;
    }

    @GetMapping("/energy/historical")
    public List<EnergyDto> getEnergyHistorical(
            @RequestParam(name="start") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(name= "end") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return filterEnergyByHour(start, end);
    }

    @GetMapping("/energy/current")
    public List<EnergyDto> getEnergyCurrent() {
        LocalDateTime hourStart = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime hourEnd = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.HOURS);
        return filterEnergyByHour(hourStart, hourEnd);
    }

    private List<EnergyDto> filterEnergyByHour(LocalDateTime start, LocalDateTime end) {
        return this.energyRepository.findEnergyEntitiesByHourBetween(start, end)
                .stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    private EnergyDto mapEntityToDto(EnergyEntity entity) {
        return new EnergyDto(
                entity.getId(),
                entity.getHour(),
                entity.getCommunity_produced(),
                entity.getCommunity_used(),
                entity.getGrid_used()
        );
    }
}
