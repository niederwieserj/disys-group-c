package com.energy.community.user.service;

import com.energy.community.user.config.RabbitMqConfig;
import com.energy.community.user.dto.UsedKwhDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class EnergyUserProducer {

    private final RabbitTemplate rabbitTemplate;
    private final EnergyUsageGenerator usageGenerator;
    private final TaskScheduler taskScheduler;
    private final ObjectMapper objectMapper;

    public EnergyUserProducer(
            RabbitTemplate rabbitTemplate,
            EnergyUsageGenerator usageGenerator,
            TaskScheduler taskScheduler
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.usageGenerator = usageGenerator;
        this.taskScheduler = taskScheduler;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startUsageSchedule() {
        scheduleNextUsageMessage();
    }

    private void scheduleNextUsageMessage() {
        int delaySeconds =
                ThreadLocalRandom.current().nextInt(1, 6);

        taskScheduler.schedule(
                () -> {
                    try {
                        sendUsageMessage(delaySeconds);
                    } finally {
                        scheduleNextUsageMessage();
                    }
                },
                Instant.now().plusSeconds(delaySeconds)
        );
    }

    private void sendUsageMessage(int intervalSeconds) {
        UsedKwhDto usedKwhDto = new UsedKwhDto(
                "USER",
                "COMMUNITY",
                usageGenerator.generateKwhForCurrentTime(),
                LocalDateTime.now()
        );

        try {
            String json =
                    objectMapper.writeValueAsString(usedKwhDto);

            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.USED_KWH_QUEUE,
                    json
            );

            System.out.printf(
                    "Used %.3f kWh after %d seconds%n",
                    usedKwhDto.kwh(),
                    intervalSeconds
            );

        } catch (JsonProcessingException exception) {
            System.err.println(
                    "Could not serialize usage message: "
                            + exception.getMessage()
            );
        }
    }
}