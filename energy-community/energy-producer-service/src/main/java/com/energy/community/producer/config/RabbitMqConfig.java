package com.energy.community.producer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String PRODUCED_KWH_QUEUE = "producedKwh";

    @Bean
    public Queue producedKWh() {
        return new Queue(PRODUCED_KWH_QUEUE, true);
    }
}