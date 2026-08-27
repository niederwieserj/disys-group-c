package com.energy.community.producer.service;

import com.energy.community.producer.client.WeatherClient;
import com.energy.community.producer.config.RabbitMqConfig;
import com.energy.community.producer.dto.ProducedKwhDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.TaskScheduler;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProducerTest {

    private RabbitTemplate rabbitTemplate;
    private WeatherClient weatherClient;
    private TaskScheduler taskScheduler;
    private Producer producer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        weatherClient = mock(WeatherClient.class);
        taskScheduler = mock(TaskScheduler.class);

        producer = new Producer(
                rabbitTemplate,
                weatherClient,
                taskScheduler
        );

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void refreshWeatherLoadsSunshineData()
            throws IOException, InterruptedException {

        when(weatherClient.getSunshineDurationForHour(anyInt()))
                .thenReturn(1800.0);

        producer.refreshWeather();

        verify(weatherClient)
                .getSunshineDurationForHour(anyInt());
    }

    @Test
    void noEnergyIsSentBeforeWeatherDataIsLoaded() {
        ArgumentCaptor<Runnable> taskCaptor =
                ArgumentCaptor.forClass(Runnable.class);

        producer.startProductionSchedule();

        verify(taskScheduler)
                .schedule(
                        taskCaptor.capture(),
                        any(Instant.class)
                );

        taskCaptor.getValue().run();

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void scheduledProductionSendsValidProducerMessage()
            throws Exception {

        when(weatherClient.getSunshineDurationForHour(anyInt()))
                .thenReturn(3600.0);

        producer.refreshWeather();

        ArgumentCaptor<Runnable> taskCaptor =
                ArgumentCaptor.forClass(Runnable.class);

        producer.startProductionSchedule();

        verify(taskScheduler)
                .schedule(
                        taskCaptor.capture(),
                        any(Instant.class)
                );

        taskCaptor.getValue().run();

        ArgumentCaptor<String> messageCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.PRODUCED_KWH_QUEUE),
                messageCaptor.capture()
        );

        ProducedKwhDto message =
                objectMapper.readValue(
                        messageCaptor.getValue(),
                        ProducedKwhDto.class
                );

        assertEquals("PRODUCER", message.type());
        assertEquals("COMMUNITY", message.association());

        assertTrue(message.kwh() > 0);
        assertTrue(message.kwh() <= 0.017);

        assertNotNull(message.datetime());
    }
}