package com.energy.community.energyuser;

import com.energy.community.common.Association;
import com.energy.community.common.EnergyMessage;
import com.energy.community.common.EnergyMessageType;
import com.energy.community.common.RabbitQueues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class EnergyUserApplication implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final EnergyUsageGenerator energyUsageGenerator;
    private final ObjectMapper objectMapper;

    public EnergyUserApplication(
            RabbitTemplate rabbitTemplate,
            EnergyUsageGenerator energyUsageGenerator,
            ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.energyUsageGenerator = energyUsageGenerator;
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(EnergyUserApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            double kwh = energyUsageGenerator.generateKwh();

            EnergyMessage message = new EnergyMessage(
                    EnergyMessageType.USER,
                    Association.COMMUNITY,
                    kwh,
                    LocalDateTime.now()
            );

            String jsonMessage = objectMapper.writeValueAsString(message);

            rabbitTemplate.convertAndSend(RabbitQueues.ENERGY_MESSAGES, jsonMessage);

            System.out.println("Sent energy user message: " + jsonMessage);

            int sleepTime = ThreadLocalRandom.current().nextInt(1000, 5001);
            Thread.sleep(sleepTime);
        }
    }
}