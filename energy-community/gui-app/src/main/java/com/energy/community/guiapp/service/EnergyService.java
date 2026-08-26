package com.energy.community.guiapp.service;

import com.energy.community.guiapp.client.EnergyApiClient;
import com.energy.community.guiapp.dto.EnergyDto;
import com.energy.community.guiapp.dto.EnergySummary;
import com.energy.community.guiapp.dto.PercentageDto;

import java.io.IOException;
import java.time.LocalDateTime;

public class EnergyService {

    private final EnergyApiClient apiClient;

    public EnergyService() {
        this.apiClient = new EnergyApiClient();
    }

    public PercentageDto getCurrentPercentage()
            throws IOException, InterruptedException {

        PercentageDto[] data = apiClient.getCurrentPercentage();

        if (data == null || data.length == 0) {
            return null;
        }

        return data[0];
    }

    public EnergySummary getHistoricalSummary(
            LocalDateTime start,
            LocalDateTime end
    ) throws IOException, InterruptedException {

        EnergyDto[] data =
                apiClient.getHistoricalEnergy(start, end);

        double totalProduced = 0.0;
        double totalUsed = 0.0;
        double totalGrid = 0.0;

        for (EnergyDto energy : data) {
            totalProduced += energy.community_produced();
            totalUsed += energy.community_used();
            totalGrid += energy.grid_used();
        }

        return new EnergySummary(
                totalProduced,
                totalUsed,
                totalGrid
        );
    }
}