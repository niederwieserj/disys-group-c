package com.energy.community.usageservice;

import com.energy.community.common.RabbitQueues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue energyMessagesQueue() {
        return QueueBuilder.durable(RabbitQueues.ENERGY_MESSAGES).build();
    }

    @Bean
    public Queue usageUpdatesQueue() {
        return QueueBuilder.durable(RabbitQueues.USAGE_UPDATES).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}