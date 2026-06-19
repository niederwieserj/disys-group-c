package com.energy.community.guiapp;

import com.energy.community.guiapp.presentationModel.EnergyCommunityModel;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class EnergyController implements Initializable{

   //? private static final String API = "https://api.energy-community.com";
    private EnergyCommunityModel model;

    @FXML
    Text community_produced_kWh_value;
    @FXML
    Text community_used_kWh_value;
    @FXML
    Text grid_used_kWh_value;
    @FXML
    Text community_pool_pc_value;
    @FXML
    Text grid_portion_pc_value;
    @FXML
    DatePicker start_date_picker;
    @FXML
    DatePicker end_date_picker;


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

        // die daten zu ISO LocalDateTimes konvertieren, das Spring Boot backend erwartet
        String startIso = startDate.atStartOfDay().toString();
        String endIso = endDate.atTime(23, 59, 59).toString();

        // die url formattieren, dass es zum getMapping passt (/energy/historical bzw /energy/current)
        String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", startIso, endIso);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null && response.statusCode() == 200) {
                System.out.println("Successfully fetched data: " + response.body());

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
