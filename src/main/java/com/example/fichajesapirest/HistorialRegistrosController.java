package com.example.fichajesapirest;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;

import java.io.File;
import java.awt.Desktop;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HistorialRegistrosController implements Initializable {

    @FXML
    private TableView<RegistroHistorial> historialTableView;

    @FXML
    private TableColumn<RegistroHistorial, String> fechaCol;

    @FXML
    private TableColumn<RegistroHistorial, String> nombreArchivoCol;

    @FXML
    private TableColumn<RegistroHistorial, Void> accionesCol;

    private ObservableList<RegistroHistorial> registros = FXCollections.observableArrayList();
    private String dniTrabajador;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar las columnas
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        nombreArchivoCol.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));

        // Configurar la columna de acciones
        accionesCol.setCellFactory(col -> new TableCell<RegistroHistorial, Void>() {
            private final Button visualizarBtn = new Button("Visualizar");
            private final Button eliminarBtn = new Button("Eliminar");

            {
                visualizarBtn.setOnAction(event -> {
                    RegistroHistorial registro = getTableView().getItems().get(getIndex());
                    visualizarPDF(registro);
                });

                eliminarBtn.setOnAction(event -> {
                    RegistroHistorial registro = getTableView().getItems().get(getIndex());
                    eliminarRegistro(registro);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, visualizarBtn, eliminarBtn);
                    setGraphic(buttons);
                }
            }
        });

        // Configurar el formateador de fecha
        fechaCol.setCellFactory(col -> new TableCell<RegistroHistorial, String>() {
            @Override
            protected void updateItem(String fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    try {
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        LocalDateTime dateTime = LocalDateTime.parse(fecha, inputFormatter);
                        setText(dateTime.format(outputFormatter));
                    } catch (Exception e) {
                        setText(fecha);
                    }
                }
            }
        });

        // Configurar el tamaño de la tabla
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) historialTableView.getScene().getWindow();
                
                // Permitir redimensionamiento manual
                historialTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

                // Configurar las columnas para que se ajusten al contenido
                fechaCol.setResizable(true);
                nombreArchivoCol.setResizable(true);
                accionesCol.setResizable(true);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setRegistros(ObservableList<RegistroHistorial> registros, String dniTrabajador) {
        this.registros = registros;
        this.dniTrabajador = dniTrabajador;
        historialTableView.setItems(registros);
    }

    private void visualizarPDF(RegistroHistorial registro) {
        try {
            File file = new File(registro.getRutaArchivo());
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                mostrarAlerta("Error", "El archivo no existe en la ruta especificada.");
            }
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el archivo: " + e.getMessage());
        }
    }

    private void eliminarRegistro(RegistroHistorial registro) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Está seguro de que desea eliminar este registro?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                // Eliminar el archivo físico
                File file = new File(registro.getRutaArchivo());
                if (file.exists()) {
                    file.delete();
                }

                // Eliminar el registro de la lista
                registros.remove(registro);
                historialTableView.refresh();

                // Guardar el historial actualizado
                guardarHistorial();

                mostrarAlerta("Éxito", "Registro eliminado correctamente");
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el registro: " + e.getMessage());
            }
        }
    }

    private void guardarHistorial() {
        try {
            String fileName = String.format("historial_pdfs_%s.json", dniTrabajador);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(fileName), registros);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el historial: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) historialTableView.getScene().getWindow();
        stage.close();
    }
} 