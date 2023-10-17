module com.example.testfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;
    requires com.jfoenix;

    opens com.example.testfx to javafx.fxml, com.fasterxml.jackson.databind, javafx.media, com.jfoenix;
    exports com.example.testfx;
}