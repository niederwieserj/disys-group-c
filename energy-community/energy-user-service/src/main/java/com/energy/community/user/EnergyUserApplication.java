package com.energy.community.user;

import com.energy.community.user.config.RabbitMqConfig;
import com.energy.community.user.dto.UsedKwhDto;
import com.energy.community.user.service.EnergyUsageGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@EnableScheduling
public class EnergyUserApplication {

    private final RabbitTemplate rabbitTemplate;
    private final EnergyUsageGenerator usageGenerator;
    private final ObjectMapper objectMapper;

    public EnergyUserApplication(RabbitTemplate rabbitTemplate, EnergyUsageGenerator usageGenerator) {
        this.rabbitTemplate = rabbitTemplate;
        this.usageGenerator = usageGenerator;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static void main(String[] args) {
        SpringApplication.run(EnergyUserApplication.class, args);
    }

    @Scheduled(fixedRate = 5000)
    private void sendUsageMessage() {
        UsedKwhDto usedKwhDto = new UsedKwhDto(
                "USER",
                "COMMUNITY",
                usageGenerator.generateKwhForCurrentTime(),
                LocalDateTime.now()
        );

        try {
            String json = objectMapper.writeValueAsString(usedKwhDto);
            rabbitTemplate.convertAndSend(RabbitMqConfig.USED_KWH_QUEUE, json);
            System.out.println("kWh used: " + usedKwhDto.kwh());
        } catch (JsonProcessingException exception) {
            System.out.println("Could not serialize usage message: " + exception.getMessage());
        }
    }

    private long randomDelayInMilliseconds() {
        return ThreadLocalRandom.current().nextLong(1000, 5001);
    }
}
