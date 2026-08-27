package com.energy.community.restapi.controller;

import com.energy.community.restapi.dto.EnergyDto;
import com.energy.community.restapi.dto.PercentageDto;
import com.energy.community.restapi.service.EnergyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/energy/historical")
    public List<EnergyDto> getEnergyHistorical(
            @RequestParam(name = "start")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam(name = "end")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "start must be before or equal to end"
            );
        }

        return energyService.getHistoricalEnergy(start, end);
    }

    @GetMapping("/energy/current")
    public List<PercentageDto> getEnergyCurrent() {
        return energyService.getCurrentPercentage();
    }
}