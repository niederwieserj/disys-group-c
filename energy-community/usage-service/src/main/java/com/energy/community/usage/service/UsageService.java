package com.energy.community.usage.service;

import com.energy.community.usage.config.RabbitMqConfig;
import com.energy.community.usage.dto.EnergyMessageDto;
import com.energy.community.usage.dto.UsageUpdateDto;
import com.energy.community.usage.entity.EnergyEntity;
import com.energy.community.usage.repository.EnergyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UsageService {

    private static final String PRODUCER = "PRODUCER";
    private static final String USER = "USER";
    private static final String COMMUNITY = "COMMUNITY";

    private final EnergyRepository energyRepository;
    private final EnergyMessageParser energyMessageParser;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public UsageService(
            EnergyRepository energyRepository,
            EnergyMessageParser energyMessageParser,
            RabbitTemplate rabbitTemplate
    ) {
        this.energyRepository = energyRepository;
        this.energyMessageParser = energyMessageParser;
        this.rabbitTemplate = rabbitTemplate;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }

    @RabbitListener(queues = RabbitMqConfig.PRODUCED_KWH_QUEUE)
    public void processProducedKwhMessage(String message) {
        processEnergyMessage(message);
    }

    @RabbitListener(queues = RabbitMqConfig.USED_KWH_QUEUE)
    public void processUsedKwhMessage(String message) {
        processEnergyMessage(message);
    }

    public synchronized void processEnergyMessage(String rawMessage) {
        try {
            EnergyMessageDto message =
                    energyMessageParser.parse(rawMessage);

            if (!isValidCommunityMessage(message)) {
                return;
            }

            EnergyEntity entity =
                    getOrCreateHourlyEntity(message.datetime());

            applyMessageToHourlyUsage(entity, message);

            EnergyEntity savedEntity =
                    energyRepository.save(entity);

            sendUsageUpdate(savedEntity);

        } catch (IOException | RuntimeException exception) {
            System.err.println(
                    "Could not process energy message: "
                            + exception.getMessage()
            );
        }
    }

    private boolean isValidCommunityMessage(
            EnergyMessageDto message
    ) {
        if (message.datetime() == null || message.kwh() <= 0) {
            return false;
        }

        return COMMUNITY.equalsIgnoreCase(message.association())
                && (
                PRODUCER.equalsIgnoreCase(message.type())
                        || USER.equalsIgnoreCase(message.type())
        );
    }

    private EnergyEntity getOrCreateHourlyEntity(
            LocalDateTime datetime
    ) {
        LocalDateTime hour =
                datetime.truncatedTo(ChronoUnit.HOURS);

        return energyRepository
                .findByHour(hour)
                .orElseGet(() -> createHourlyEntity(hour));
    }

    private EnergyEntity createHourlyEntity(
            LocalDateTime hour
    ) {
        EnergyEntity entity = new EnergyEntity();

        entity.setHour(hour);
        entity.setCommunityProduced(0);
        entity.setCommunityUsed(0);
        entity.setGridUsed(0);

        return entity;
    }

    private void applyMessageToHourlyUsage(
            EnergyEntity entity,
            EnergyMessageDto message
    ) {
        if (PRODUCER.equalsIgnoreCase(message.type())) {
            entity.setCommunityProduced(
                    roundToThreeDecimals(
                            entity.getCommunityProduced()
                                    + message.kwh()
                    )
            );

            return;
        }

        double availableCommunityEnergy = Math.max(
                0,
                entity.getCommunityProduced()
                        - entity.getCommunityUsed()
        );

        double communityUsage = Math.min(
                message.kwh(),
                availableCommunityEnergy
        );

        double gridUsage =
                message.kwh() - communityUsage;

        entity.setCommunityUsed(
                roundToThreeDecimals(
                        entity.getCommunityUsed()
                                + communityUsage
                )
        );

        entity.setGridUsed(
                roundToThreeDecimals(
                        entity.getGridUsed()
                                + gridUsage
                )
        );
    }

    private void sendUsageUpdate(
            EnergyEntity entity
    ) throws JsonProcessingException {

        UsageUpdateDto usageUpdateDto =
                new UsageUpdateDto(
                        entity.getHour(),
                        entity.getCommunityProduced(),
                        entity.getCommunityUsed(),
                        entity.getGridUsed()
                );

        String json =
                objectMapper.writeValueAsString(
                        usageUpdateDto
                );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.USAGE_UPDATE_QUEUE,
                json
        );

        System.out.println(
                "Sent usage update: " + json
        );
    }

    private double roundToThreeDecimals(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}