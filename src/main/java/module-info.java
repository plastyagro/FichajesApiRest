module com.example.fichajesapirest {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.json;
    requires unirest.java;


    opens com.example.fichajesapirest to javafx.fxml;
    exports com.example.fichajesapirest;
}