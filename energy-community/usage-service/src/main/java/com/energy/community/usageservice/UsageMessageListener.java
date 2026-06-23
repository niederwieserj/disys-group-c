package com.energy.community.usageservice;

import com.energy.community.common.EnergyMessage;
import com.energy.community.common.RabbitQueues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UsageMessageListener {

    private final UsageCalculationService usageCalculationService;
    private final ObjectMapper objectMapper;

    public UsageMessageListener(
            UsageCalculationService usageCalculationService,
            ObjectMapper objectMapper
    ) {
        this.usageCalculationService = usageCalculationService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitQueues.ENERGY_MESSAGES)
    public void receiveMessage(String jsonMessage) throws Exception {
        System.out.println("Received energy message: " + jsonMessage);

        EnergyMessage message = objectMapper.readValue(jsonMessage, EnergyMessage.class);

        usageCalculationService.processEnergyMessage(message);
    }
}