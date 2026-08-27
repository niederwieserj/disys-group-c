package com.energy.community.current.percentage.service;

import com.energy.community.current.percentage.config.RabbitMQConfig;
import com.energy.community.current.percentage.dto.UsageUpdateDto;
import com.energy.community.current.percentage.entity.PercentageEntity;
import com.energy.community.current.percentage.repository.PercentageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CurrentPercentageService {

    private final PercentageRepository percentageRepository;
    private final ObjectMapper objectMapper;

    public CurrentPercentageService(PercentageRepository percentageRepository) {
        this.percentageRepository = percentageRepository;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = RabbitMQConfig.USAGE_UPDATE_QUEUE)
    public void processUsageUpdate(String rawMessage) {
        try {
            UsageUpdateDto usageUpdate = objectMapper.readValue(
                    rawMessage,
                    UsageUpdateDto.class);

            if (!isValid(usageUpdate)) {
                return;
            }

            LocalDateTime messageHour = usageUpdate.hour().truncatedTo(ChronoUnit.HOURS);

            LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

            if (!messageHour.equals(currentHour)) {
                return;
            }

            removeOutdatedPercentages(currentHour);

            PercentageEntity entity = new PercentageEntity();
            entity.setHour(currentHour);

            entity.setCommunityDepleted(
                    calculateCommunityDepletedPercentage(
                            usageUpdate.community_produced(),
                            usageUpdate.community_used()));

            entity.setGridPortion(
                    calculateGridPortionPercentage(
                            usageUpdate.community_used(),
                            usageUpdate.grid_used()));

            percentageRepository.save(entity);

        } catch (IOException exception) {
            System.out.println(
                    "Could not process usage update: "
                            + exception.getMessage());
        }
    }

    private boolean isValid(UsageUpdateDto usageUpdate) {
        return usageUpdate != null
                && usageUpdate.hour() != null
                && usageUpdate.community_produced() >= 0
                && usageUpdate.community_used() >= 0
                && usageUpdate.grid_used() >= 0;
    }

    private void removeOutdatedPercentages(LocalDateTime currentHour) {
        List<PercentageEntity> outdatedPercentages = percentageRepository.findByHourNot(currentHour);

        outdatedPercentages.forEach(
                percentageRepository::delete);
    }

    private double calculateCommunityDepletedPercentage(
            double communityProduced,
            double communityUsed) {
        if (communityProduced == 0) {
            return 0;
        }

        return (communityUsed / communityProduced) * 100;
    }

    private double calculateGridPortionPercentage(
            double communityUsed,
            double gridUsed) {
        double totalUsage = communityUsed + gridUsed;

        if (totalUsage == 0) {
            return 0;
        }

        return (gridUsed / totalUsage) * 100;
    }
}