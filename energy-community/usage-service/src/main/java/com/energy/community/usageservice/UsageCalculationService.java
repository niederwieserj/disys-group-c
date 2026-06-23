package com.energy.community.usageservice;

import com.energy.community.common.Association;
import com.energy.community.common.EnergyMessage;
import com.energy.community.common.EnergyMessageType;
import com.energy.community.common.RabbitQueues;
import com.energy.community.common.UsageUpdateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UsageCalculationService {

    private final EnergyHourlyMetricRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public UsageCalculationService(
            EnergyHourlyMetricRepository repository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processEnergyMessage(EnergyMessage message) throws Exception {
        if (message.association() != Association.COMMUNITY) {
            return;
        }

        LocalDateTime hour = message.datetime().truncatedTo(ChronoUnit.HOURS);

        EnergyHourlyMetric metric = repository.findByHour(hour)
                .orElseGet(() -> new EnergyHourlyMetric(hour));

        if (message.type() == EnergyMessageType.PRODUCER) {
            processProducerMessage(metric, message.kwh());
        }

        if (message.type() == EnergyMessageType.USER) {
            processUserMessage(metric, message.kwh());
        }

        repository.save(metric);

        sendUsageUpdateMessage(hour);

        System.out.println("Updated usage data for hour: " + hour);
    }

    private void processProducerMessage(EnergyHourlyMetric metric, double kwh) {
        double newProduced = metric.getCommunityProduced() + kwh;
        metric.setCommunityProduced(round(newProduced));
    }

    private void processUserMessage(EnergyHourlyMetric metric, double kwh) {
        double availableCommunityEnergy = metric.getCommunityProduced() - metric.getCommunityUsed();

        double communityPart = Math.min(kwh, Math.max(availableCommunityEnergy, 0.0));
        double gridPart = kwh - communityPart;

        metric.setCommunityUsed(round(metric.getCommunityUsed() + communityPart));
        metric.setGridUsed(round(metric.getGridUsed() + gridPart));
    }

    private void sendUsageUpdateMessage(LocalDateTime hour) throws Exception {
        UsageUpdateMessage updateMessage = new UsageUpdateMessage(hour);
        String jsonMessage = objectMapper.writeValueAsString(updateMessage);

        rabbitTemplate.convertAndSend(RabbitQueues.USAGE_UPDATES, jsonMessage);

        System.out.println("Sent usage update message: " + jsonMessage);
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}