package com.energy.community.guiapp.client;

import com.energy.community.guiapp.dto.EnergyDto;
import com.energy.community.guiapp.dto.PercentageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class EnergyApiClient {

    private static final String BASE_URL = "http://localhost:8080/energy";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EnergyApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    public PercentageDto[] getCurrentPercentage()
            throws IOException, InterruptedException {

        String responseBody = sendGetRequest(BASE_URL + "/current");

        return objectMapper.readValue(
                responseBody,
                PercentageDto[].class
        );
    }

    public EnergyDto[] getHistoricalEnergy(
            LocalDateTime start,
            LocalDateTime end
    ) throws IOException, InterruptedException {

        String url = String.format(
                "%s/historical?start=%s&end=%s",
                BASE_URL,
                start,
                end
        );

        String responseBody = sendGetRequest(url);

        return objectMapper.readValue(
                responseBody,
                EnergyDto[].class
        );
    }

    private String sendGetRequest(String url)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "REST API returned HTTP status "
                            + response.statusCode()
            );
        }

        return response.body();
    }
}