package com.energy.community.current.percentage.service;

import com.energy.community.current.percentage.config.RabbitMQConfig;
import com.energy.community.current.percentage.dto.UsageUpdateDto;
import com.energy.community.current.percentage.entity.PercentageEntity;
import com.energy.community.current.percentage.repository.PercentageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class CurrentPercentageService {

    private final PercentageRepository currentPercentageRepository;

    public CurrentPercentageService(PercentageRepository currentPercentageRepository) {
        this.currentPercentageRepository = currentPercentageRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.USAGE_UPDATE_QUEUE)
    public void  processUsageUpdate(UsageUpdateDto usageUpdateDto){
        if (usageUpdateDto == null || usageUpdateDto.hour() == null) {
            return;
        }
        double communityDepleted = calculateCommunityDepletedPercentage(
                usageUpdateDto.communityProduced(),
                usageUpdateDto.communityUsed()
        );

        double gridPortion = calculateGridPortionPercentage(
                usageUpdateDto.communityUsed(),
                usageUpdateDto.gridUsed()
        );
        PercentageEntity entity = new PercentageEntity();
        entity.setHour(usageUpdateDto.hour());
        entity.setCommunityDepleted(communityDepleted);
        entity.setGridPortion(gridPortion);

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
}
