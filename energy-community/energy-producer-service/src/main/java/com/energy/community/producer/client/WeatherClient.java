package com.energy.community.producer.client;

import com.energy.community.producer.dto.WeatherResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class WeatherClient {

    private static final String WEATHER_API_URL =
            "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=48.2"
                    + "&longitude=16.37"
                    + "&hourly=sunshine_duration"
                    + "&forecast_days=1"
                    + "&temporal_resolution=native"
                    + "&models=geosphere_arome_austria"
                    + "&timezone=Europe%2FVienna";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WeatherClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public double getSunshineDurationForHour(int hour)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WEATHER_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Weather API returned HTTP status "
                            + response.statusCode()
            );
        }

        WeatherResponseDto weather = objectMapper.readValue(
                response.body(),
                WeatherResponseDto.class
        );

        if (weather.hourly() == null) {
            throw new IOException("Weather API response contains no hourly data");
        }

        List<Double> sunshineDuration =
                weather.hourly().sunshineDuration();

        if (sunshineDuration == null
                || hour < 0
                || hour >= sunshineDuration.size()) {

            throw new IOException(
                    "No sunshine duration available for hour " + hour
            );
        }

        return sunshineDuration.get(hour);
    }
}