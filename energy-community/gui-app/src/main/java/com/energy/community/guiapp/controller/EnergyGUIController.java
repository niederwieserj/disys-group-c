package com.energy.community.guiapp.controller;

import com.energy.community.guiapp.dto.EnergySummary;
import com.energy.community.guiapp.dto.PercentageDto;
import com.energy.community.guiapp.model.EnergyCommunityModel;
import com.energy.community.guiapp.service.EnergyService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class EnergyGUIController implements Initializable {

    private final EnergyCommunityModel model = new EnergyCommunityModel();
    private final EnergyService energyService = new EnergyService();

    @FXML
    private Text community_pool_pc_value;

    @FXML
    private Text grid_portion_pc_value;

    @FXML
    private Text community_produced_kWh_value;

    @FXML
    private Text community_used_kWh_value;

    @FXML
    private Text grid_used_kWh_value;

    @FXML
    private DatePicker start_date_picker;

    @FXML
    private DatePicker end_date_picker;

    @FXML
    private TextField start_time;

    @FXML
    private TextField end_time;

    @FXML
    private Pane usage_and_production_data_panel;

    @FXML
    private Button refresh_usage_data_pc_btn;

    @FXML
    private ToggleButton show_usage_data_kWh_btn;

    @FXML
    private Label welcomeText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        start_date_picker.setPromptText("mm/dd/yyyy");
        end_date_picker.setPromptText("mm/dd/yyyy");
        start_time.setPromptText("00:00");
        end_time.setPromptText("00:00");

        applyBindings();
        getCurrentPercentageData();
    }

    private void applyBindings() {
        show_usage_data_kWh_btn.selectedProperty()
                .bindBidirectional(model.boolProperty());

        usage_and_production_data_panel.visibleProperty()
                .bind(model.boolToVisibilityBinding());

        usage_and_production_data_panel.managedProperty()
                .bind(model.boolToVisibilityBinding());
    }

    @FXML
    private void refreshUsageDataPc() {
        getCurrentPercentageData();
    }

    private void getCurrentPercentageData() {
        try {
            PercentageDto percentage = energyService.getCurrentPercentage();

            if (percentage == null) {
                community_pool_pc_value.setText("No data found for current hour!");
                grid_portion_pc_value.setText("");
                return;
            }

            community_pool_pc_value.setText(
                    String.format("%.2f %%", percentage.community_depleted())
            );

            grid_portion_pc_value.setText(
                    String.format("%.2f %%", percentage.grid_portion())
            );

        } catch (IOException e) {
            showCurrentDataError("Could not reach server!");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showCurrentDataError("Request interrupted!");
        }
    }

    @FXML
    private void showHistoricalEnergyData() {
        if (show_usage_data_kWh_btn.isSelected()) {
            getHistoricalEnergyData();
        }
    }

    private void getHistoricalEnergyData() {
        LocalDateTime start = getDateTime(start_date_picker, start_time);
        if (start == null) {
            return;
        }

        LocalDateTime end = getDateTime(end_date_picker, end_time);
        if (end == null) {
            return;
        }

        if (start.isAfter(end)) {
            showWarning("Start date and time must be before end date and time.");
            return;
        }

        try {
            EnergySummary summary =
                    energyService.getHistoricalSummary(start, end);

            community_produced_kWh_value.setText(
                    String.format("%.2f kWh", summary.communityProduced())
            );

            community_used_kWh_value.setText(
                    String.format("%.2f kWh", summary.communityUsed())
            );

            grid_used_kWh_value.setText(
                    String.format("%.2f kWh", summary.gridUsed())
            );

        } catch (IOException e) {
            showHistoricalDataError("Could not reach server!");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            showHistoricalDataError("Request interrupted!");
        }
    }

    private LocalDateTime getDateTime(
            DatePicker datePicker,
            TextField timeField
    ) {
        LocalDate date = datePicker.getValue();
        String timeText = timeField.getText();

        if (date == null || timeText == null || timeText.isBlank()) {
            showWarning("Please select a valid date and time.");
            return null;
        }

        try {
            LocalTime time = LocalTime.parse(timeText);
            return LocalDateTime.of(date, time);

        } catch (Exception e) {
            showWarning("Invalid time. Please use HH:mm, for example 14:30.");
            return null;
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showCurrentDataError(String message) {
        community_pool_pc_value.setText(message);
        grid_portion_pc_value.setText(message);
    }

    private void showHistoricalDataError(String message) {
        community_produced_kWh_value.setText(message);
        community_used_kWh_value.setText(message);
        grid_used_kWh_value.setText(message);
    }
}