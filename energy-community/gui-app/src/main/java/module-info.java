module com.energy.community.guiapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.energy.community.guiapp to javafx.fxml;
    exports com.energy.community.guiapp;
}