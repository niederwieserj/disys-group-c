package com.energy.community.guiapp;

import com.energy.community.guiapp.presentationModel.EnergyCommunityModel;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;

public class EnergyController implements Initializable{

    private EnergyCommunityModel model;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // percentages of community pool usage and grid portion
    @FXML
    Text community_pool_pc_value;
    @FXML
    Text grid_portion_pc_value;

    // community production/usage and grid usage in kWh
    @FXML
    Text community_produced_kWh_value;
    @FXML
    Text community_used_kWh_value;
    @FXML
    Text grid_used_kWh_value;



    // date and time input fields
    @FXML
    DatePicker start_date_picker;
    @FXML
    DatePicker end_date_picker;
    @FXML
    TextField start_time;
    @FXML
    TextField end_time;

    // panel that opens when toggleButton is clicked
    @FXML
    Pane usage_and_production_data_panel;

    // buttons
    @FXML
    Button refresh_usage_data_pc_btn;
    @FXML
    ToggleButton show_usage_data_kWh_btn;
    @FXML
    private Label welcomeText;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new EnergyCommunityModel();
        start_date_picker.setPromptText("mm/dd/yyyy");
        end_date_picker.setPromptText("mm/dd/yyyy");
        start_time.setPromptText("00:00");
        end_time.setPromptText("00:00");

        applyBindings();
        getCurrentPercentageData();
        getHistoricalEnergyData();
        //getEnergyData();
    }



    private void applyBindings() {
        show_usage_data_kWh_btn.selectedProperty().bindBidirectional(
                model.boolProperty());

        usage_and_production_data_panel.visibleProperty().bind(model.boolToVisibilityBinding());
        usage_and_production_data_panel.managedProperty().bind(model.boolToVisibilityBinding());
    }
/*
    private void getEnergyData() {

        // community Pool & grid portion holen






        // datum & zeitpunkt von input feldern holen
        java.time.LocalDate startDate = (start_date_picker.getValue() != null) ? start_date_picker.getValue() : java.time.LocalDate.now().minusDays(1);
        java.time.LocalDate endDate = (end_date_picker.getValue() != null) ? end_date_picker.getValue() : java.time.LocalDate.now();

        LocalTime startTime;
        LocalTime endTime;

        try {
            startTime = LocalTime.parse(start_time.getText());
        } catch (Exception e) {
            startTime = LocalTime.MIDNIGHT;
        }

        try {
            endTime = LocalTime.parse(start_time.getText());
        } catch (Exception e) {
            endTime = LocalTime.MIDNIGHT;
        }
        String startIso = LocalDateTime.of(startDate, startTime).toString();
        String endIso = LocalDateTime.of(endDate, endTime).toString();
        // ^datum und zeitpunkt kombinieren & als datetime formatieren, ende ^

        // die url formattieren, dass es zum getMapping passt (/energy/historical bzw /energy/current)
        String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", startIso, endIso);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null && response.statusCode() == 200) {
                //System.out.println("Successfully fetched data: " + response.body());

                //  community_produced_kWh_value.setText() usw noch updaten da

                /*

                Text community_pool_pc_value;

                Text grid_portion_pc_value;

                // community production/usage and grid usage in kWh

                Text community_produced_kWh_value;

                Text community_used_kWh_value;

                Text grid_used_kWh_value;



            } else {
                System.err.println("Backend returned error status: " + (response != null ? response.statusCode() : "null"));
            }

        } catch (IOException e) {
            System.err.println("Network/Connection error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Request interrupted: " + e.getMessage());
        }
    }
    */


    private String sendGetRequest(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Backend error status: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
            return null;
        }
    }


    @FXML
    protected void refreshUsageDataPc(ActionEvent actionEvent) {
        getCurrentPercentageData();
    }

    private void getCurrentPercentageData() {
        String url = "http://localhost:8080/energy/current";
        String responseBody = sendGetRequest(url);

        if (responseBody != null) {

            updatePercentageUI(responseBody);
        }
    }

    private void getHistoricalEnergyData() {
        String startIso = getFormattedDateTime(start_date_picker, start_time, true);
        String endIso = getFormattedDateTime(end_date_picker, end_time, false);
        String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", startIso, endIso);

        String responseBody = sendGetRequest(url);

        if (responseBody != null) {
            System.out.println("in get historicalenergxdata func im IF responsebody !null");
            updateHistoricalUI(responseBody);
        }
    }

    private String getFormattedDateTime(DatePicker datePicker, TextField timeField, boolean isStart) {
       LocalDate date = (datePicker.getValue() != null) ? datePicker.getValue() :
                (isStart ? LocalDate.now().minusDays(1) : LocalDate.now());
        LocalTime time;
        try {
            time = LocalTime.parse(timeField.getText());
        } catch (Exception e) {
            time = LocalTime.MIDNIGHT;
        }
        return LocalDateTime.of(date, time).toString();
    }

    // prozentwerte für community pool (verbrauch) & grid portion updaten in der GUI
    private void updatePercentageUI(String jsonResponse) {

        try {
            JsonNode root = objectMapper.readTree(jsonResponse);


            if (root.isArray() && !root.isEmpty()) {

                JsonNode latestData = root.get(0);
                double communityDepleted = latestData.path("communityDepleted").asDouble(0.0);
                double gridPortion = latestData.path("gridPortion").asDouble(0.0);

                community_pool_pc_value.setText(String.format("%.2f %%", communityDepleted));
                grid_portion_pc_value.setText(String.format("%.2f %%", gridPortion));
            }
        } catch (Exception e) {
            System.err.println("Failed to parse percentage JSON: " + e.getMessage());
        }
    }

    private void updateHistoricalUI(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.isArray()) {
                double totalProduced = 0.0;
                double totalUsed = 0.0;
                double totalGrid = 0.0;

                for (JsonNode node : root) {
                    totalProduced += node.path("communityProduced").asDouble(0.0);
                    totalUsed += node.path("communityUsed").asDouble(0.0);
                    totalGrid += node.path("gridUsed").asDouble(0.0);
                }

                community_produced_kWh_value.setText(String.format("%.2f kWh", totalProduced));
                community_used_kWh_value.setText(String.format("%.2f kWh", totalUsed));
                grid_used_kWh_value.setText(String.format("%.2f kWh", totalGrid));
            }
        } catch (Exception e) {
            System.err.println("Failed to parse historical JSON: " + e.getMessage());
        }
    }


}
