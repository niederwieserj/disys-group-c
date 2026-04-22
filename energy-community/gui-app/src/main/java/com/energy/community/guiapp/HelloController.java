package com.energy.community.guiapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/energy"))
                .build();

        HttpResponse<String> response = null;

        try {
             response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null) {
                welcomeText.setText(response.body());
            }
            else {
                welcomeText.setText("fuck");
            }
        } catch (IOException e) {
            welcomeText.setText(e.getMessage());
        } catch (InterruptedException e) {
            welcomeText.setText("ex2");
        }


    }
}
