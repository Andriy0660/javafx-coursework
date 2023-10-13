module com.example.testfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;


    opens com.example.testfx to javafx.fxml, com.fasterxml.jackson.databind, javafx.media;
    exports com.example.testfx;
}