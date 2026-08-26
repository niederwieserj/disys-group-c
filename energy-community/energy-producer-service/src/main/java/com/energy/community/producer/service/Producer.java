package com.energy.community.producer.service;

import com.energy.community.producer.client.WeatherClient;
import com.energy.community.producer.config.RabbitMqConfig;
import com.energy.community.producer.dto.ProducedKwhDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class Producer {

    private static final double PV_PLANT_PEAK_KW = 10.0;
    private static final int SECONDS_IN_HOUR = 3600;

    private final RabbitTemplate rabbitTemplate;
    private final WeatherClient weatherClient;
    private final TaskScheduler taskScheduler;
    private final ObjectMapper objectMapper;

    private Double sunshineSecondsInHour;

    public Producer(
            RabbitTemplate rabbitTemplate,
            WeatherClient weatherClient,
            TaskScheduler taskScheduler
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.weatherClient = weatherClient;
        this.taskScheduler = taskScheduler;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startProductionSchedule() {
        scheduleNextProduction();
    }

    private void scheduleNextProduction() {
        int delaySeconds =
                ThreadLocalRandom.current().nextInt(1, 6);

        taskScheduler.schedule(
                () -> {
                    try {
                        produce(delaySeconds);
                    } finally {
                        scheduleNextProduction();
                    }
                },
                Instant.now().plusSeconds(delaySeconds)
        );
    }

    private void produce(int intervalSeconds) {
        if (sunshineSecondsInHour == null) {
            return;
        }

        double currentKilowatt =
                (sunshineSecondsInHour / SECONDS_IN_HOUR)
                        * PV_PLANT_PEAK_KW;

        double kilowattHours =
                currentKilowatt
                        * intervalSeconds
                        / SECONDS_IN_HOUR;

        kilowattHours *=
                0.8 + ThreadLocalRandom.current().nextDouble() * 0.4;

        kilowattHours = Math.max(0, kilowattHours);

        ProducedKwhDto producedKwhDto =
                new ProducedKwhDto(
                        "PRODUCER",
                        "COMMUNITY",
                        kilowattHours,
                        LocalDateTime.now()
                );

        try {
            String json =
                    objectMapper.writeValueAsString(producedKwhDto);

            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.PRODUCED_KWH_QUEUE,
                    json
            );

            System.out.printf(
                    "Produced %.6f kWh after %d seconds%n",
                    kilowattHours,
                    intervalSeconds
            );

        } catch (JsonProcessingException e) {
            System.err.println(
                    "Could not serialize produced energy message: "
                            + e.getMessage()
            );
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshWeather() {
        try {
            int currentHour = LocalDateTime.now().getHour();

            sunshineSecondsInHour =
                    weatherClient.getSunshineDurationForHour(
                            currentHour
                    );

            System.out.println(
                    "Weather data updated. Sunshine duration: "
                            + sunshineSecondsInHour
                            + " seconds"
            );

        } catch (IOException e) {
            System.err.println(
                    "Could not retrieve weather data: "
                            + e.getMessage()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            System.err.println(
                    "Weather request was interrupted."
            );
        }
    }
}