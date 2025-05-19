package com.example.fichajesapirest;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TablaFirebaseApp {
    @FXML private FlowPane trabajadoresGrid;
    @FXML private TextField buscadorTextField;
    @FXML private Button actualizarButton;
    @FXML private Button exportarButton;
    @FXML private DatePicker fechaDesde;
    @FXML private DatePicker fechaHasta;

    private FirebaseRESTExample firebase = new FirebaseRESTExample();
    private ObservableList<Trabajador> trabajadores = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cargar los trabajadores
        cargarTrabajadores();
        
        // Configurar actualización automática cada 5 minutos
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(5), e -> actualizarDatos()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void cargarTrabajadores() {
        try {
            trabajadores.clear();
            trabajadoresGrid.getChildren().clear();
            List<Trabajador> listaTrabajadores = firebase.obtenerTrabajadores();
            
            if (listaTrabajadores.isEmpty()) {
                mostrarAlerta("Información", "No se encontraron trabajadores en la base de datos");
            } else {
                trabajadores.addAll(listaTrabajadores);
                for (Trabajador trabajador : trabajadores) {
                    VBox card = crearTarjetaTrabajador(trabajador);
                    card.setOnMouseClicked((MouseEvent event) -> {
                        if (event.getClickCount() == 1) {
                            abrirDetallesTrabajador(trabajador);
                        }
                    });
                    trabajadoresGrid.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los trabajadores: " + e.getMessage());
        }
    }

    private VBox crearTarjetaTrabajador(Trabajador trabajador) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-padding: 15; -fx-pref-width: 250;");
        card.setAlignment(Pos.CENTER);
        
        // Cambiar el cursor a mano para toda la tarjeta
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseEntered(e -> {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 3); -fx-padding: 15; -fx-pref-width: 250; -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });

        card.setOnMouseExited(e -> {
            card.setCursor(javafx.scene.Cursor.HAND);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); -fx-padding: 15; -fx-pref-width: 250; -fx-scale-x: 1.0; -fx-scale-y: 1.0;");
        });

        // Iniciales del trabajador
        Circle avatar = new Circle(30);
        avatar.setFill(Color.valueOf("#4CAF50"));
        Text iniciales = new Text(trabajador.getNombre().substring(0, 1) + trabajador.getApellidos().substring(0, 1));
        iniciales.setFill(Color.WHITE);
        iniciales.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        StackPane avatarContainer = new StackPane(avatar, iniciales);
        avatarContainer.setCursor(javafx.scene.Cursor.HAND);

        // Nombre y apellidos
        Label nombreLabel = new Label(trabajador.getNombre());
        nombreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        nombreLabel.setCursor(javafx.scene.Cursor.HAND);
        
        Label apellidosLabel = new Label(trabajador.getApellidos());
        apellidosLabel.setStyle("-fx-font-size: 14px;");
        apellidosLabel.setCursor(javafx.scene.Cursor.HAND);

        card.getChildren().addAll(avatarContainer, nombreLabel, apellidosLabel);

        // Efecto de pulsación
        card.setOnMousePressed(e -> {
            card.setStyle("-fx-background-color: #c8e6c9; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); -fx-padding: 15; -fx-pref-width: 250; -fx-scale-x: 0.98; -fx-scale-y: 0.98;");
        });

        card.setOnMouseReleased(e -> {
            card.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 3); -fx-padding: 15; -fx-pref-width: 250; -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });

        // Evento de clic
        card.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 1) {
                abrirDetallesTrabajador(trabajador);
            }
        });

        return card;
    }

    private void abrirDetallesTrabajador(Trabajador trabajador) {
        try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fichajesapirest/detalles_trabajador.fxml"));
                    Parent root = loader.load();
                    
                    DetallesTrabajadorController controller = loader.getController();
            controller.setTrabajador(trabajador, firebase.obtenerFichajesTrabajador(trabajador.getDni()));
                    
                    Stage stage = new Stage();
                    stage.setTitle("Detalles del Trabajador");
                    stage.setScene(new Scene(root));
            stage.setMaximized(true);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
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
            trabajadoresGrid.getChildren().clear();
            trabajadoresGrid.getChildren().addAll(trabajadores.stream().map(this::crearTarjetaTrabajador).collect(Collectors.toList()));
            return;
        }

        ObservableList<Trabajador> filtrado = FXCollections.observableArrayList();

            for (Trabajador trabajador : trabajadores) {
                    String nombreCompleto = (trabajador.getNombre() + " " + trabajador.getApellidos()).toLowerCase();
            if (nombreCompleto.contains(textoBuscado)) {
                        filtrado.add(trabajador);
            }
        }

        trabajadoresGrid.getChildren().clear();
        trabajadoresGrid.getChildren().addAll(filtrado.stream().map(this::crearTarjetaTrabajador).collect(Collectors.toList()));
    }
}
