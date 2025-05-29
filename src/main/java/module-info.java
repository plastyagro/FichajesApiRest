module com.example.fichajesapirest {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.json;
    requires unirest.java;
    requires java.desktop;
    requires java.mail;
    requires itextpdf;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    opens com.example.fichajesapirest to javafx.fxml;
    exports com.example.fichajesapirest;
}