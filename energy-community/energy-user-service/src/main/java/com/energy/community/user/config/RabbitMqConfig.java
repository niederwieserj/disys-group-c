package com.energy.community.user.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String USED_KWH_QUEUE = "usedKwh";

    @Bean
    public Queue usedKWh() {
        return new Queue(USED_KWH_QUEUE, true);
    }
}