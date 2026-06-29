package com.energy.community.producer.service;

import com.energy.community.producer.config.RabbitMqConfig;
import com.energy.community.producer.dto.ProducedKwhDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Component
public class Producer {
    private final RabbitTemplate rabbitTemplate;
    private JsonNode weatherInfo = null;
    private final double PV_PLANT_PEAK_KW = 10.0; // Assuming 50 sqm of roof area
    private final int SECONDS_IN_HOUR = 3600;
    private final int MINUTES_IN_HOUR = 60;
    private final String WEATHER_API_BASE_URL = "https://api.open-meteo.com/v1/forecast?";
    private final String WEATHER_API_PARAMETERS = String.join("&",
            "latitude=48.2",
            "longitude=16.37",
            "hourly=sunshine_duration",
            "forecast_days=1",
            "temporal_resolution=native",
            "models=geosphere_arome_austria");

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 5000) // Send in 5 sec interval to simulate several producers
    public void Produce() {
        if (weatherInfo == null) {
            return;
        }

        int hour = LocalDateTime.now().getHour();
        double sunshineSecondsInHour = Double.parseDouble(weatherInfo.path("hourly").path("sunshine_duration").get(hour).toString());

        double currentkiloWatt = (sunshineSecondsInHour / SECONDS_IN_HOUR) * PV_PLANT_PEAK_KW;
        double kiloWattHoursPerMinute = currentkiloWatt / MINUTES_IN_HOUR;
        kiloWattHoursPerMinute *= 0.8 + (Math.random() * 0.4); // Add some noise (multiply with number between 0.8 and 1.2)

        if (kiloWattHoursPerMinute < 0) {
            kiloWattHoursPerMinute = 0;
        }

        ProducedKwhDto producedKwhDto = new ProducedKwhDto("PRODUCER", "COMMUNITY", kiloWattHoursPerMinute, LocalDateTime.now());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = null;

        try {
            json = mapper.writeValueAsString(producedKwhDto);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        if (json == null) {
            return;
        }

        System.out.println("kWh produced: " + kiloWattHoursPerMinute);

        rabbitTemplate.convertAndSend(RabbitMqConfig.PRODUCED_KWH_QUEUE, json);
    }

    @Scheduled(fixedRate = 3600000)
    public void GetWeatherFromApi() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(WEATHER_API_BASE_URL + WEATHER_API_PARAMETERS))
                .build();

        HttpResponse<String> response = null;

        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null) {
                String body = response.body();
                System.out.println(body);
                ObjectMapper mapper = new ObjectMapper();
                weatherInfo = mapper.readTree(body);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
