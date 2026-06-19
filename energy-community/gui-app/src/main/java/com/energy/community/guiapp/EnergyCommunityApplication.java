package com.energy.community.guiapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EnergyCommunityApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EnergyCommunityApplication.class.getResource("energy-community.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 482, 436);
        stage.setTitle("Energy community");
        stage.setScene(scene);
        stage.show();
    }
}
