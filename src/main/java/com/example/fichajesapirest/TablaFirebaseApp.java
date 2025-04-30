package com.example.fichajesapirest;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TablaFirebaseApp {

    @FXML private TableView<Trabajador> tableView;
    @FXML private TableColumn<Trabajador, String> nombreCol;
    @FXML private TableColumn<Trabajador, String> apellidosCol;
    @FXML private Button actualizarButton;
    @FXML private Button exportarButton;
    @FXML private DatePicker fechaDesde;
    @FXML private DatePicker fechaHasta;
    @FXML private TextField buscadorTextField;

    private FirebaseRESTExample firebase = new FirebaseRESTExample();
    private ObservableList<Trabajador> trabajadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar las columnas
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidosCol.setCellValueFactory(new PropertyValueFactory<>("apellidos"));

        // Configurar la tabla para que no permita seleccionar filas vacías
        tableView.setRowFactory(tv -> {
            TableRow<Trabajador> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    abrirDetallesTrabajador(event);
                }
            });
            return row;
        });

        // Cargar los trabajadores
        cargarTrabajadores();
        
        // Configurar actualización automática cada 5 minutos
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(5), e -> actualizarDatos()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void cargarTrabajadores() {
        try {
            System.out.println("Iniciando carga de trabajadores...");
            trabajadores.clear();
            List<Trabajador> listaTrabajadores = firebase.obtenerTrabajadores();
            System.out.println("Trabajadores obtenidos de Firebase: " + listaTrabajadores.size());
            
            if (listaTrabajadores.isEmpty()) {
                System.out.println("No se encontraron trabajadores en la base de datos");
                mostrarAlerta("Información", "No se encontraron trabajadores en la base de datos");
            } else {
                trabajadores.addAll(listaTrabajadores);
                System.out.println("Trabajadores añadidos a la tabla: " + trabajadores.size());
                tableView.setItems(trabajadores);
                System.out.println("Tabla actualizada con los trabajadores");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar trabajadores: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los trabajadores: " + e.getMessage());
        }
    }

    @FXML
    private void abrirDetallesTrabajador(MouseEvent event) {
        if (event.getClickCount() == 1) { // Solo procesar clics simples
            Trabajador trabajadorSeleccionado = tableView.getSelectionModel().getSelectedItem();
            if (trabajadorSeleccionado != null) {
                try {
                    // Obtener los fichajes del trabajador
                    List<RegistroFichaje> fichajes = firebase.obtenerFichajesTrabajador(trabajadorSeleccionado.getDni());
                    
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fichajesapirest/detalles_trabajador.fxml"));
                    Parent root = loader.load();
                    
                    DetallesTrabajadorController controller = loader.getController();
                    controller.setTrabajador(trabajadorSeleccionado, fichajes);
                    
                    Stage stage = new Stage();
                    stage.setTitle("Detalles del Trabajador");
                    stage.setScene(new Scene(root));
                    stage.setMaximized(true);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void actualizarDatos() {
        cargarTrabajadores();
    }

    @FXML
    private void exportarDatos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar como CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        fileChooser.setInitialFileName("registros_fichajes.csv");

        File archivo = fileChooser.showSaveDialog(null);
        if (archivo != null) {
            try (FileWriter csvWriter = new FileWriter(archivo)) {
                csvWriter.append("Nombre,Apellidos,Tipo,Fecha y Hora\n");
                for (Trabajador trabajador : trabajadores) {
                    for (RegistroFichaje registro : trabajador.getFichajes()) {
                        csvWriter.append(trabajador.getNombre()).append(",");
                        csvWriter.append(trabajador.getApellidos()).append(",");
                        csvWriter.append(registro.getTipo()).append(",");
                        csvWriter.append(registro.getFechaHora()).append("\n");
                    }
                }
                System.out.println("Datos exportados correctamente.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Exportación cancelada.");
        }
    }

    @FXML
    private void filtrarPorFecha() {
        filtrarPorNombreYFecha();
    }

    private void filtrarPorNombreYFecha() {
        String textoBuscado = buscadorTextField.getText().toLowerCase();
        
        if (textoBuscado.isEmpty()) {
            // Si no hay texto de búsqueda, mostrar todos los trabajadores
            tableView.setItems(trabajadores);
            return;
        }

        ObservableList<Trabajador> filtrado = FXCollections.observableArrayList();

        for (Trabajador trabajador : trabajadores) {
            String nombreCompleto = (trabajador.getNombre() + " " + trabajador.getApellidos()).toLowerCase();
            if (nombreCompleto.contains(textoBuscado)) {
                filtrado.add(trabajador);
            }
        }

        tableView.setItems(filtrado);
    }
}
