package com.energy.community.user.service;

import com.energy.community.user.config.RabbitMqConfig;
import com.energy.community.user.dto.UsedKwhDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EnergyUserProducerTest {

    private RabbitTemplate rabbitTemplate;
    private EnergyUsageGenerator usageGenerator;
    private TaskScheduler taskScheduler;
    private EnergyUserProducer energyUserProducer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        usageGenerator = mock(EnergyUsageGenerator.class);
        taskScheduler = mock(TaskScheduler.class);

        energyUserProducer = new EnergyUserProducer(
                rabbitTemplate,
                usageGenerator,
                taskScheduler
        );

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void startUsageScheduleCreatesScheduledTask() {
        energyUserProducer.startUsageSchedule();

        verify(taskScheduler).schedule(
                any(Runnable.class),
                any(Instant.class)
        );
    }

    @Test
    void scheduledTaskUsesGeneratedEnergyValue() throws Exception {
        when(usageGenerator.generateKwhForCurrentTime())
                .thenReturn(0.012);

        ArgumentCaptor<Runnable> taskCaptor =
                ArgumentCaptor.forClass(Runnable.class);

        energyUserProducer.startUsageSchedule();

        verify(taskScheduler).schedule(
                taskCaptor.capture(),
                any(Instant.class)
        );

        taskCaptor.getValue().run();

        verify(usageGenerator)
                .generateKwhForCurrentTime();
    }

    @Test
    void scheduledTaskSendsValidUserMessage() throws Exception {
        when(usageGenerator.generateKwhForCurrentTime())
                .thenReturn(0.015);

        ArgumentCaptor<Runnable> taskCaptor =
                ArgumentCaptor.forClass(Runnable.class);

        energyUserProducer.startUsageSchedule();

        verify(taskScheduler).schedule(
                taskCaptor.capture(),
                any(Instant.class)
        );

        taskCaptor.getValue().run();

        ArgumentCaptor<String> messageCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.USED_KWH_QUEUE),
                messageCaptor.capture()
        );

        UsedKwhDto message =
                objectMapper.readValue(
                        messageCaptor.getValue(),
                        UsedKwhDto.class
                );

        assertEquals("USER", message.type());
        assertEquals("COMMUNITY", message.association());
        assertEquals(0.015, message.kwh(), 0.000001);
        assertNotNull(message.datetime());
    }
}