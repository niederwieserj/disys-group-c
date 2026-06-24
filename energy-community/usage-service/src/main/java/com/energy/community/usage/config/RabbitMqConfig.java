package com.energy.community.usage.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String PRODUCED_KWH_QUEUE = "producedKwh";
    public static final String USED_KWH_QUEUE = "usedKwh";
    public static final String USAGE_UPDATE_QUEUE = "Usage_Update_Queue";
    public static final String RABBITMQ_USERNAME = "guest";
    public static final String RABBITMQ_PASSWORD = "guest";

    @Bean
    public Queue producedKwhQueue() {
        return new Queue(PRODUCED_KWH_QUEUE, false);
    }

    @Bean
    public Queue usedKwhQueue() {
        return new Queue(USED_KWH_QUEUE, false);
    }

    @Bean
    public Queue usageUpdateQueue() {
        return new Queue(USAGE_UPDATE_QUEUE, true);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername(RABBITMQ_USERNAME);
        connectionFactory.setPassword(RABBITMQ_PASSWORD);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
}
