module com.example.testfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.example.testfx to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.testfx;
}