package com.energy.community.guiapp;

import com.energy.community.guiapp.presentationModel.EnergyCommunityModel;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;

public class EnergyController implements Initializable{

    private EnergyCommunityModel model;

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
        start_time.setPromptText("00:00");
        end_time.setPromptText("00:00");

        applyBindings();
        getEnergyData();
    }



    private void applyBindings() {
        show_usage_data_kWh_btn.selectedProperty().bindBidirectional(
                model.boolProperty());

        usage_and_production_data_panel.visibleProperty().bind(model.boolToVisibilityBinding());
        usage_and_production_data_panel.managedProperty().bind(model.boolToVisibilityBinding());
    }

    private void getEnergyData() {
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

            } else {
                System.err.println("Backend returned error status: " + (response != null ? response.statusCode() : "null"));
            }

        } catch (IOException e) {
            System.err.println("Network/Connection error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Request interrupted: " + e.getMessage());
        }
    }


    @FXML
    protected void refreshUsageDataPc(ActionEvent actionEvent) {
        getEnergyData();
    }


}
