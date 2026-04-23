package com.energy.community.restapi.controller;

import com.energy.community.restapi.dto.EnergyDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EnergyController {

    // Initialize with dummy data
    private List<EnergyDto> energyDtos = new ArrayList<>(List.of(
            new EnergyDto(24.3),
            new EnergyDto(18.4)
    ));

    @GetMapping("/energy")
    public List<EnergyDto> getEnergy() {
        return energyDtos;
    }
}
