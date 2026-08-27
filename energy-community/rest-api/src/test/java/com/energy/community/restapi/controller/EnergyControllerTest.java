package com.energy.community.restapi.controller;

import com.energy.community.restapi.dto.EnergyDto;
import com.energy.community.restapi.dto.PercentageDto;
import com.energy.community.restapi.service.EnergyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnergyControllerTest {

    private EnergyService energyService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        energyService = mock(EnergyService.class);

        EnergyController energyController =
                new EnergyController(energyService);

        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(energyController)
                .build();
    }

    @Test
    void historicalEndpointReturnsEnergyData() throws Exception {
        LocalDateTime start =
                LocalDateTime.of(2026, 8, 27, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 8, 27, 12, 0);

        EnergyDto energyDto = new EnergyDto(
                1,
                LocalDateTime.of(2026, 8, 27, 11, 0),
                5.0,
                3.0,
                1.0
        );

        when(energyService.getHistoricalEnergy(start, end))
                .thenReturn(List.of(energyDto));

        mockMvc.perform(
                        get("/energy/historical")
                                .param("start", "2026-08-27T10:00:00")
                                .param("end", "2026-08-27T12:00:00")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].community_produced").value(5.0))
                .andExpect(jsonPath("$[0].community_used").value(3.0))
                .andExpect(jsonPath("$[0].grid_used").value(1.0));

        verify(energyService)
                .getHistoricalEnergy(start, end);
    }

    @Test
    void historicalEndpointRejectsInvalidDateRange() throws Exception {
        mockMvc.perform(
                        get("/energy/historical")
                                .param("start", "2026-08-27T15:00:00")
                                .param("end", "2026-08-27T10:00:00")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(energyService);
    }

    @Test
    void currentEndpointReturnsPercentageData() throws Exception {
        PercentageDto percentageDto = new PercentageDto(
                LocalDateTime.of(2026, 8, 27, 14, 0),
                60.0,
                25.0
        );

        when(energyService.getCurrentPercentage())
                .thenReturn(List.of(percentageDto));

        mockMvc.perform(get("/energy/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].community_depleted").value(60.0))
                .andExpect(jsonPath("$[0].grid_portion").value(25.0));

        verify(energyService).getCurrentPercentage();
    }
}