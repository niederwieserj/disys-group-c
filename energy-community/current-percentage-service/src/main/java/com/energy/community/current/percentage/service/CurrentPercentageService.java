package com.energy.community.current.percentage.service;

import com.energy.community.current.percentage.config.RabbitMQConfig;
import com.energy.community.current.percentage.dto.UsageUpdateDto;
import com.energy.community.current.percentage.entity.PercentageEntity;
import com.energy.community.current.percentage.repository.PercentageRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
public class CurrentPercentageService {

    private final PercentageRepository currentPercentageRepository;

    public CurrentPercentageService(PercentageRepository currentPercentageRepository) {
        this.currentPercentageRepository = currentPercentageRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.USAGE_UPDATE_QUEUE)
    public void processUsageUpdate(String rawMessage) {
        UsageUpdateDto usageUpdateDto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            usageUpdateDto = mapper.readValue(rawMessage, UsageUpdateDto.class);
        } catch (IOException e) {
            System.out.println("Could not process usage update: " + e.getMessage());
            return;
        }

        if (usageUpdateDto == null || usageUpdateDto.hour() == null) {
            return;
        }
        double communityDepleted = calculateCommunityDepletedPercentage(
                usageUpdateDto.community_produced(),
                usageUpdateDto.community_used()
        );

        double gridPortion = calculateGridPortionPercentage(
                usageUpdateDto.community_used(),
                usageUpdateDto.grid_used()
        );
        PercentageEntity entity = new PercentageEntity();
        entity.setHour(usageUpdateDto.hour());
        entity.setCommunityDepleted(round(communityDepleted));
        entity.setGridPortion(round(gridPortion));

        currentPercentageRepository.deleteAll();
        currentPercentageRepository.save(entity);
    }


    private double calculateCommunityDepletedPercentage(double communityProduced, double communityUsage){
        if(communityProduced == 0){
            return 0;
        }
        double communityDepleted = (communityUsage/communityProduced) * 100;
        return communityDepleted;
    }

    private double calculateGridPortionPercentage(double communityUsage, double gridUsage){
        double totalUsage = communityUsage + gridUsage;
        if(totalUsage == 0){
            return 0;
        }
        double gridPortion = (gridUsage / totalUsage) * 100;
        return  gridPortion;

    }

    private double round(double value){
        return Math.round(value*100)/100;
    }
}
