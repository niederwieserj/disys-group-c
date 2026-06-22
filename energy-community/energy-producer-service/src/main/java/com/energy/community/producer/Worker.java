package com.energy.community.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Worker {
    private final RabbitTemplate rabbitTemplate;

    public Worker(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public void Work() {
        ProducedKwhDto producedKwhDto = new ProducedKwhDto("PRODUCER", "COMMUNITY", 0.0023, LocalDateTime.now());
        rabbitTemplate.convertAndSend(RabbitMqConfig.PRODUCED_KWH_QUEUE, "test");
    }
}
