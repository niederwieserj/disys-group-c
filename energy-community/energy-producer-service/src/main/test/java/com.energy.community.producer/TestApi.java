package com.energy.community.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class TestApi {
    public static void main(String[] args) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=48.2&longitude=16.37&hourly=sunshine_duration&forecast_days=1&temporal_resolution=native&models=geosphere_arome_austria"))
                .build();

        HttpResponse<String> response = null;

        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null) {
                String body = response.body();
                System.out.println(body);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(body);
                int hour = LocalDateTime.now().getHour();
                double sunshineSecondsInHour = Double.parseDouble(node.path("hourly").path("sunshine_duration").get(hour).toString());

                double kiloWattPeak = 10.0; // Assuming 50 sqm of roof area
                int secondsInHour = 3600;
                int minutesInHour = 60;
                double currentkiloWatt = (sunshineSecondsInHour / secondsInHour) * kiloWattPeak;
                double kiloWattHoursPerMinute = currentkiloWatt / minutesInHour;
                System.out.println(kiloWattHoursPerMinute);
            }
            else {
            }
        } catch (IOException e) {
        } catch (InterruptedException e) {
        }
    }
}
