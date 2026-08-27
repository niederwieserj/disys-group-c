package com.energy.community.current.percentage.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USAGE_UPDATE_QUEUE = "Usage_Update_Queue";

    @Bean
    public Queue usageUpdateQueue() {
        return new Queue(USAGE_UPDATE_QUEUE, true);
    }
}