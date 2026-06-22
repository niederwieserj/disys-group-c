package com.energy.community.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = null;

        try {
            json = mapper.writeValueAsString(producedKwhDto);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        rabbitTemplate.convertAndSend(RabbitMqConfig.PRODUCED_KWH_QUEUE, json);
    }
}
