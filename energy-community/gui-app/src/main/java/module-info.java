module com.energy.community.guiapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;


    opens com.energy.community.guiapp to javafx.fxml;

    exports com.energy.community.guiapp.dto to com.fasterxml.jackson.databind;
    opens com.energy.community.guiapp.dto to com.fasterxml.jackson.databind;
    exports com.energy.community.guiapp;

    opens com.energy.community.guiapp.controller to javafx.fxml;
}