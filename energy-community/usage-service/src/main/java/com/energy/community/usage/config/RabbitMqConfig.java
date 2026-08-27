package com.energy.community.usage.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String PRODUCED_KWH_QUEUE = "producedKwh";
    public static final String USED_KWH_QUEUE = "usedKwh";
    public static final String USAGE_UPDATE_QUEUE = "Usage_Update_Queue";

    @Bean
    public Queue producedKwhQueue() {
        return new Queue(PRODUCED_KWH_QUEUE, true);
    }

    @Bean
    public Queue usedKwhQueue() {
        return new Queue(USED_KWH_QUEUE, true);
    }

    @Bean
    public Queue usageUpdateQueue() {
        return new Queue(USAGE_UPDATE_QUEUE, true);
    }
}