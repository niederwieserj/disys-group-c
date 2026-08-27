package com.energy.community.restapi.service;

import com.energy.community.restapi.dto.EnergyDto;
import com.energy.community.restapi.dto.PercentageDto;
import com.energy.community.restapi.entity.EnergyEntity;
import com.energy.community.restapi.entity.PercentageEntity;
import com.energy.community.restapi.repository.EnergyRepository;
import com.energy.community.restapi.repository.PercentageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnergyServiceTest {

    private EnergyRepository energyRepository;
    private PercentageRepository percentageRepository;
    private EnergyService energyService;

    @BeforeEach
    void setUp() {
        energyRepository = mock(EnergyRepository.class);
        percentageRepository = mock(PercentageRepository.class);

        energyService = new EnergyService(
                energyRepository,
                percentageRepository
        );
    }

    @Test
    void historicalEnergyIsMappedToDto() {
        LocalDateTime start =
                LocalDateTime.of(2026, 8, 27, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 8, 27, 12, 0);

        EnergyEntity entity = new EnergyEntity();
        entity.setId(1);
        entity.setHour(LocalDateTime.of(2026, 8, 27, 11, 0));
        entity.setCommunityProduced(5.0);
        entity.setCommunityUsed(3.0);
        entity.setGridUsed(1.0);

        when(energyRepository.findEnergyEntitiesByHourBetween(start, end))
                .thenReturn(List.of(entity));

        List<EnergyDto> result =
                energyService.getHistoricalEnergy(start, end);

        assertEquals(1, result.size());

        EnergyDto dto = result.get(0);

        assertEquals(1, dto.id());
        assertEquals(entity.getHour(), dto.hour());
        assertEquals(5.0, dto.community_produced(), 0.000001);
        assertEquals(3.0, dto.community_used(), 0.000001);
        assertEquals(1.0, dto.grid_used(), 0.000001);

        verify(energyRepository)
                .findEnergyEntitiesByHourBetween(start, end);
    }

    @Test
    void currentPercentageIsMappedToDto() {
        PercentageEntity entity = new PercentageEntity();
        entity.setHour(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0));
        entity.setCommunityDepleted(60.0);
        entity.setGridPortion(25.0);

        when(percentageRepository.findPercentageEntitiesByHourBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(entity));

        List<PercentageDto> result =
                energyService.getCurrentPercentage();

        assertEquals(1, result.size());

        PercentageDto dto = result.get(0);

        assertEquals(entity.getHour(), dto.hour());
        assertEquals(60.0, dto.community_depleted(), 0.000001);
        assertEquals(25.0, dto.grid_portion(), 0.000001);

        verify(percentageRepository)
                .findPercentageEntitiesByHourBetween(
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                );
    }

    @Test
    void historicalEnergyReturnsEmptyListWhenNoDataExists() {
        LocalDateTime start =
                LocalDateTime.of(2026, 8, 26, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 8, 26, 12, 0);

        when(energyRepository.findEnergyEntitiesByHourBetween(start, end))
                .thenReturn(List.of());

        List<EnergyDto> result =
                energyService.getHistoricalEnergy(start, end);

        assertEquals(0, result.size());
    }
}