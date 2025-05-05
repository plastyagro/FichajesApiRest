package com.example.fichajesapirest;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;                                                        
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.control.DatePicker;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;

public class DetallesTrabajadorController implements Initializable {
    @FXML
    private Label nombreLabel;
    @FXML
    private Label apellidosLabel;
    @FXML
    private Label dniLabel;
    @FXML
    private Label horarioLabel;
    @FXML
    private Label diasTrabajadosLabel;
    @FXML
    private Label diasTrabajadosCalendarioLabel;
    @FXML
    private Label resumenMensualLabel;
    @FXML
    private DatePicker calendarioTrabajo;
    @FXML
    private TextField horaEntradaMananaField;
    @FXML
    private TextField horaSalidaMananaField;
    @FXML
    private TextField horaEntradaTardeField;
    @FXML
    private TextField horaSalidaTardeField;
    @FXML
    private Button guardarHorarioButton;
    @FXML
    private Button exportarButton;
    @FXML
    private Button cerrarButton;
    @FXML
    private TableView<RegistroFichaje> entradasMananaTableView;
    @FXML
    private TableView<RegistroFichaje> entradasTardeTableView;
    @FXML
    private TableView<RegistroFichaje> salidasMananaTableView;
    @FXML
    private TableView<RegistroFichaje> salidasTardeTableView;
    @FXML
    private TableColumn<RegistroFichaje, String> fechaEntradaMananaCol;
    @FXML
    private TableColumn<RegistroFichaje, String> fechaEntradaTardeCol;
    @FXML
    private TableColumn<RegistroFichaje, String> fechaSalidaMananaCol;
    @FXML
    private TableColumn<RegistroFichaje, String> fechaSalidaTardeCol;
    @FXML
    private TableColumn<RegistroFichaje, String> tipoEntradaMananaCol;
    @FXML
    private TableColumn<RegistroFichaje, String> tipoEntradaTardeCol;
    @FXML
    private TableColumn<RegistroFichaje, String> tipoSalidaMananaCol;
    @FXML
    private TableColumn<RegistroFichaje, String> tipoSalidaTardeCol;
    @FXML
    private TableColumn<RegistroFichaje, String> estadoEntradaMananaCol;
    @FXML
    private TableColumn<RegistroFichaje, String> estadoEntradaTardeCol;
    @FXML
    private TableColumn<RegistroFichaje, String> estadoSalidaMananaCol;
    @FXML
    private TableColumn<RegistroFichaje, String> estadoSalidaTardeCol;
    @FXML
    private TableColumn<RegistroFichaje, String> iconosEntradaMananaColumn;
    @FXML
    private TableColumn<RegistroFichaje, String> iconosEntradaTardeColumn;
    @FXML
    private TableColumn<RegistroFichaje, String> iconosSalidaMananaColumn;
    @FXML
    private TableColumn<RegistroFichaje, String> iconosSalidaTardeColumn;
    @FXML
    private ToggleGroup grupoEntradas;
    @FXML
    private ToggleGroup grupoSalidas;
    @FXML
    private MenuButton menuEntradas;
    @FXML
    private MenuButton menuSalidas;
    @FXML
    private CheckBox checkEntradas;
    @FXML
    private CheckBox checkSalidas;
    @FXML
    private CheckBox checkPuntual;
    @FXML
    private CheckBox checkTarde;
    @FXML
    private CheckBox checkAusente;
    @FXML
    private ChoiceBox<String> periodoFiltro;
    @FXML
    private DatePicker fechaPersonalizada;
    @FXML
    private Label indicadorRetrasos;
    @FXML
    private Label indicadorAusencias;

    private Trabajador trabajador;
    private List<RegistroFichaje> fichajesTrabajador;
    private Set<LocalDate> diasTrabajados = new HashSet<>();
    private List<RegistroFichaje> fichajesOriginales;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cargar las imágenes
        Image verde = new Image(getClass().getResource("/verde.png").toExternalForm());
        Image rojo = new Image(getClass().getResource("/rojo.png").toExternalForm());

        // Configurar las columnas de las tablas de entradas
        configurarColumnasTabla(fechaEntradaMananaCol, tipoEntradaMananaCol, estadoEntradaMananaCol, iconosEntradaMananaColumn);
        configurarColumnasTabla(fechaEntradaTardeCol, tipoEntradaTardeCol, estadoEntradaTardeCol, iconosEntradaTardeColumn);

        // Configurar las columnas de las tablas de salidas
        configurarColumnasTabla(fechaSalidaMananaCol, tipoSalidaMananaCol, estadoSalidaMananaCol, iconosSalidaMananaColumn);
        configurarColumnasTabla(fechaSalidaTardeCol, tipoSalidaTardeCol, estadoSalidaTardeCol, iconosSalidaTardeColumn);

        // Configurar las columnas de iconos para todas las tablas
        configurarColumnasIconos(iconosEntradaMananaColumn, verde, rojo);
        configurarColumnasIconos(iconosEntradaTardeColumn, verde, rojo);
        configurarColumnasIconos(iconosSalidaMananaColumn, verde, rojo);
        configurarColumnasIconos(iconosSalidaTardeColumn, verde, rojo);

        // Configurar el calendario
        configurarCalendario();

        // Configurar los menús
        menuEntradas.setText("Mañana");
        menuSalidas.setText("Mañana");
        
        // Mostrar las tablas de mañana por defecto
        mostrarEntradasManana();
        mostrarSalidasManana();

        // Configurar el ChoiceBox de períodos
        periodoFiltro.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fechaPersonalizada.setVisible("Personalizado".equals(newVal));
                if (!"Personalizado".equals(newVal)) {
                    filtrarPorFecha();
                }
            }
        });

        // Configurar el DatePicker personalizado
        fechaPersonalizada.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarPorFecha();
            }
        });

        // Aplicar animaciones a los elementos principales
        animateElement(nombreLabel, 100);
        animateElement(apellidosLabel, 150);
        animateElement(dniLabel, 200);
        animateElement(horarioLabel, 250);
        animateElement(periodoFiltro, 300);
        animateElement(calendarioTrabajo, 350);

        // Configurar el listener para el cierre de la ventana después de que la escena esté disponible
        nombreLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                stage.setOnCloseRequest(this::limpiarRecursos);
            }
        });
        
        // Configurar listeners para los filtros
        periodoFiltro.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarPorFecha();
            }
        });

        fechaPersonalizada.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarPorFecha();
            }
        });
    }

    private void animateElement(Node node, int delay) {
        node.setOpacity(0);
        node.setTranslateY(20);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
        fadeIn.setToValue(1);
        fadeIn.setDelay(Duration.millis(delay));

        TranslateTransition translateIn = new TranslateTransition(Duration.millis(300), node);
        translateIn.setToY(0);
        translateIn.setDelay(Duration.millis(delay));

        fadeIn.play();
        translateIn.play();
    }

    private void configurarCalendario() {
        // Deshabilitar la edición del DatePicker
        calendarioTrabajo.setEditable(false);

        // Configurar el estilo de los días trabajados
        calendarioTrabajo.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (!empty && date != null) {
                    if (diasTrabajados.contains(date)) {
                        setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    }
                }
            }
        });

        // Añadir listener para actualizar el resumen cuando cambia el mes
        calendarioTrabajo.setOnAction(event -> {
            actualizarResumenMensual(calendarioTrabajo.getValue());
        });
    }

    private void actualizarResumenMensual(LocalDate fecha) {
        if (fecha == null) return;

        // Obtener el primer y último día del mes
        LocalDate primerDiaMes = fecha.withDayOfMonth(1);
        LocalDate ultimoDiaMes = fecha.withDayOfMonth(fecha.lengthOfMonth());

        // Contar días trabajados en el mes
        long diasTrabajadosMes = diasTrabajados.stream()
                .filter(dia -> !dia.isBefore(primerDiaMes) && !dia.isAfter(ultimoDiaMes))
                .count();

        // Actualizar las etiquetas de días trabajados
        diasTrabajadosLabel.setText(diasTrabajadosMes + " días");
        diasTrabajadosCalendarioLabel.setText(diasTrabajadosMes + " días");

        // Crear el resumen mensual
        StringBuilder resumen = new StringBuilder();
        resumen.append("Mes: ").append(fecha.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new Locale("es")))
                .append(" ").append(fecha.getYear()).append("\n\n")
                .append("Días trabajados: ").append(diasTrabajadosMes).append("\n")
                .append("Días totales: ").append(fecha.lengthOfMonth()).append("\n")
                .append("Porcentaje: ").append(String.format("%.1f", (diasTrabajadosMes * 100.0 / fecha.lengthOfMonth()))).append("%");

        resumenMensualLabel.setText(resumen.toString());
    }

    private void actualizarCalendario() {
        diasTrabajados.clear();

        // Obtener todos los registros de entrada y salida
        List<RegistroFichaje> registros = new ArrayList<>();
        registros.addAll(fichajesTrabajador);

        // Extraer las fechas únicas de los registros
        for (RegistroFichaje registro : registros) {
            try {
                // Convertir el String de fecha a LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime fechaHora = LocalDateTime.parse(registro.getFechaHora(), formatter);
                LocalDate fecha = fechaHora.toLocalDate();
                diasTrabajados.add(fecha);
            } catch (Exception e) {
                System.err.println("Error al procesar la fecha: " + registro.getFechaHora());
                e.printStackTrace();
            }
        }

        // Actualizar el calendario con la fecha actual
        LocalDate hoy = LocalDate.now();
        calendarioTrabajo.setValue(hoy);
        actualizarResumenMensual(hoy);
    }

    public void setTrabajador(Trabajador trabajador, List<RegistroFichaje> fichajes) {
        this.trabajador = trabajador;
        this.fichajesTrabajador = fichajes;
        this.fichajesOriginales = new ArrayList<>(fichajes);

        // Actualizar la información del trabajador
        nombreLabel.setText(trabajador.getNombre());
        apellidosLabel.setText(trabajador.getApellidos());
        dniLabel.setText(trabajador.getDni());

        // Actualizar el horario
        String horario = String.format("Mañana: %s - %s\nTarde: %s - %s",
                formatearHora(trabajador.getHoraEntradaManana()),
                formatearHora(trabajador.getHoraSalidaManana()),
                formatearHora(trabajador.getHoraEntradaTarde()),
                formatearHora(trabajador.getHoraSalidaTarde()));
        horarioLabel.setText(horario);

        // Calcular estadísticas
        calcularEstadisticas();

        // Actualizar los campos de horario
        horaEntradaMananaField.setText(formatearHora(trabajador.getHoraEntradaManana()));
        horaSalidaMananaField.setText(formatearHora(trabajador.getHoraSalidaManana()));
        horaEntradaTardeField.setText(formatearHora(trabajador.getHoraEntradaTarde()));
        horaSalidaTardeField.setText(formatearHora(trabajador.getHoraSalidaTarde()));

        // Actualizar las tablas
        actualizarTablasConFichajes(fichajesTrabajador);
    }

    private String formatearHora(String hora) {
        if (hora == null || hora.isEmpty()) return "00:00";
        String[] partes = hora.split(":");
        if (partes.length != 2) return "00:00";
        try {
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);
            return String.format("%02d:%02d", horas, minutos);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }

    private ImageView crearIcono(Image imagen) {
        ImageView view = new ImageView(imagen);
        view.setFitWidth(20);
        view.setFitHeight(20);
        return view;
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void exportarDatos() {
        if (trabajador == null || fichajesTrabajador == null || fichajesTrabajador.isEmpty()) {
            mostrarAlerta("No hay datos para exportar", "No se encontraron registros para este trabajador.");
            return;
        }

        // Crear diálogo de selección
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Exportar Datos");
        dialog.setHeaderText("Seleccione qué datos desea exportar:");

        // Crear botones de opción
        ToggleGroup group = new ToggleGroup();
        RadioButton rbEntradas = new RadioButton("Solo Entradas");
        RadioButton rbSalidas = new RadioButton("Solo Salidas");
        RadioButton rbAmbos = new RadioButton("Entradas y Salidas");
        rbAmbos.setSelected(true);

        rbEntradas.setToggleGroup(group);
        rbSalidas.setToggleGroup(group);
        rbAmbos.setToggleGroup(group);

        VBox content = new VBox(10);
        content.getChildren().addAll(rbEntradas, rbSalidas, rbAmbos);
        dialog.getDialogPane().setContent(content);

        // Añadir botones de acción
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Mostrar diálogo y procesar selección
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                String tipoExportacion = selected.getText();

                // Crear diálogo de selección de archivo
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar archivo CSV");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Archivos CSV", "*.csv")
                );
                fileChooser.setInitialFileName(trabajador.getNombre() + "_" + trabajador.getApellidos() + ".csv");

                File file = fileChooser.showSaveDialog(exportarButton.getScene().getWindow());
                if (file != null) {
                    try {
                        exportarCSV(file, tipoExportacion);
                        mostrarAlerta("Exportación exitosa", "Los datos se han exportado correctamente.");
                    } catch (IOException e) {
                        mostrarAlerta("Error", "No se pudo exportar los datos: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void exportarCSV(File file, String tipoExportacion) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Escribir encabezado
            writer.write("Fecha y Hora,Tipo,Estado\n");

            // Filtrar y escribir registros según la selección
            for (RegistroFichaje registro : fichajesTrabajador) {
                if (tipoExportacion.equals("Solo Entradas") && !registro.getTipo().equals("entrada")) {
                    continue;
                }
                if (tipoExportacion.equals("Solo Salidas") && !registro.getTipo().equals("salida")) {
                    continue;
                }

                // Formatear la fecha
                DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime fecha = LocalDateTime.parse(registro.getFechaHora(), parser);
                String fechaFormateada = fecha.format(formatter);

                // Escribir línea
                writer.write(String.format("%s,%s,%s\n",
                        fechaFormateada,
                        registro.getTipo(),
                        registro.getEstado()
                ));
            }
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
    private void filtrarPorFecha() {
        if (fichajesTrabajador == null) {
            return;
        }

        List<RegistroFichaje> registrosFiltrados = new ArrayList<>(fichajesTrabajador);
        final LocalDate hoy = LocalDate.now();

        // Aplicar filtro de fechas según el período seleccionado
        String periodo = periodoFiltro.getValue();
        if (periodo != null) {
            final LocalDate fechaInicio;
            final LocalDate fechaFin;

            switch (periodo) {
                case "Hoy":
                    fechaInicio = hoy;
                    fechaFin = hoy;
                    break;
                case "Esta semana":
                    fechaInicio = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
                    fechaFin = hoy;
                    break;
                case "Este mes":
                    fechaInicio = hoy.withDayOfMonth(1);
                    fechaFin = hoy;
                    break;
                case "Últimos 7 días":
                    fechaInicio = hoy.minusDays(7);
                    fechaFin = hoy;
                    break;
                case "Últimos 30 días":
                    fechaInicio = hoy.minusDays(30);
                    fechaFin = hoy;
                    break;
                case "Personalizado":
                    fechaInicio = fechaPersonalizada.getValue();
                    fechaFin = fechaPersonalizada.getValue();
                    break;
                default:
                    fechaInicio = null;
                    fechaFin = hoy;
            }

            if (fechaInicio != null) {
            registrosFiltrados = registrosFiltrados.stream()
                    .filter(registro -> {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime fechaRegistro = LocalDateTime.parse(registro.getFechaHora(), formatter);
                            LocalDate fecha = fechaRegistro.toLocalDate();
                                return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
                        } catch (Exception e) {
                            System.err.println("Error al filtrar por fecha: " + e.getMessage());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            }
        }

        // Actualizar las tablas
        actualizarTablasConFichajes(registrosFiltrados);
    }

    @FXML
    private void limpiarFiltro() {
        periodoFiltro.setValue(null);
        fechaPersonalizada.setValue(null);
        actualizarTablasConFichajes(fichajesTrabajador);
    }

    private void actualizarTablasConFichajes(List<RegistroFichaje> fichajes) {
        List<RegistroFichaje> entradasManana = new ArrayList<>();
        List<RegistroFichaje> entradasTarde = new ArrayList<>();
        List<RegistroFichaje> salidasManana = new ArrayList<>();
        List<RegistroFichaje> salidasTarde = new ArrayList<>();

        int contadorRetrasos = 0;
        int contadorAusencias = 0;

        for (RegistroFichaje registro : fichajes) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime fechaRegistro = LocalDateTime.parse(registro.getFechaHora(), formatter);
                LocalTime horaRegistro = fechaRegistro.toLocalTime();

                // Obtener los horarios del trabajador
                LocalTime horaEntradaManana = LocalTime.parse(formatearHora(trabajador.getHoraEntradaManana()));
                LocalTime horaSalidaManana = LocalTime.parse(formatearHora(trabajador.getHoraSalidaManana()));
                LocalTime horaEntradaTarde = LocalTime.parse(formatearHora(trabajador.getHoraEntradaTarde()));
                LocalTime horaSalidaTarde = LocalTime.parse(formatearHora(trabajador.getHoraSalidaTarde()));

                // Determinar si es mañana o tarde
                boolean esManana = horaRegistro.isBefore(LocalTime.of(14, 0));

                if (registro.getTipo().equals("entrada")) {
                    if (esManana) {
                        // Entrada de mañana
                        if (horaRegistro.isBefore(horaEntradaManana)) {
                            registro.setEstado("Anticipada");
                        } else if (horaRegistro.equals(horaEntradaManana)) {
                            registro.setEstado("Puntual");
                        } else {
                            registro.setEstado("Retrasada");
                            contadorRetrasos++;
                        }
                        entradasManana.add(registro);
                    } else {
                        // Entrada de tarde
                        if (horaRegistro.isBefore(horaEntradaTarde)) {
                            registro.setEstado("Anticipada");
                        } else if (horaRegistro.equals(horaEntradaTarde)) {
                            registro.setEstado("Puntual");
                        } else {
                            registro.setEstado("Retrasada");
                            contadorRetrasos++;
                        }
                        entradasTarde.add(registro);
                    }
                } else if (registro.getTipo().equals("salida")) {
                    if (esManana) {
                        // Salida de mañana
                        if (horaRegistro.isBefore(horaSalidaManana)) {
                            registro.setEstado("Anticipada");
                        } else if (horaRegistro.equals(horaSalidaManana)) {
                            registro.setEstado("Puntual");
                        } else {
                            registro.setEstado("Tardía");
                            contadorRetrasos++;
                        }
                        salidasManana.add(registro);
                    } else {
                        // Salida de tarde
                        if (horaRegistro.isBefore(horaSalidaTarde)) {
                            registro.setEstado("Anticipada");
                        } else if (horaRegistro.equals(horaSalidaTarde)) {
                            registro.setEstado("Puntual");
                        } else {
                            registro.setEstado("Tardía");
                            contadorRetrasos++;
                        }
                        salidasTarde.add(registro);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al procesar el registro: " + registro.getFechaHora());
                e.printStackTrace();
            }
        }

        // Ordenar los registros por fecha
        entradasManana.sort((r1, r2) -> r2.getFechaHora().compareTo(r1.getFechaHora()));
        entradasTarde.sort((r1, r2) -> r2.getFechaHora().compareTo(r1.getFechaHora()));
        salidasManana.sort((r1, r2) -> r2.getFechaHora().compareTo(r1.getFechaHora()));
        salidasTarde.sort((r1, r2) -> r2.getFechaHora().compareTo(r1.getFechaHora()));

        // Cargar los datos en las tablas
        entradasMananaTableView.getItems().setAll(entradasManana);
        entradasTardeTableView.getItems().setAll(entradasTarde);
        salidasMananaTableView.getItems().setAll(salidasManana);
        salidasTardeTableView.getItems().setAll(salidasTarde);

        // Actualizar los indicadores
        indicadorRetrasos.setText(contadorRetrasos + "");
        indicadorAusencias.setText(contadorAusencias + "");

        // Actualizar el calendario
        actualizarCalendario();
    }

    @FXML
    private void guardarHorario() {
        try {
            System.out.println("Iniciando actualización de horario...");

            // Validar los horarios
            if (!validarFormatoHora(horaEntradaMananaField.getText()) ||
                    !validarFormatoHora(horaSalidaMananaField.getText()) ||
                    !validarFormatoHora(horaEntradaTardeField.getText()) ||
                    !validarFormatoHora(horaSalidaTardeField.getText())) {
                mostrarAlerta("Error", "Los horarios deben estar en formato HH:mm");
                return;
            }

            // Guardar la selección actual del período
            String periodoSeleccionado = periodoFiltro.getValue();

            System.out.println("Horarios antes de actualizar:");
            System.out.println("Entrada Mañana: " + trabajador.getHoraEntradaManana());
            System.out.println("Salida Mañana: " + trabajador.getHoraSalidaManana());
            System.out.println("Entrada Tarde: " + trabajador.getHoraEntradaTarde());
            System.out.println("Salida Tarde: " + trabajador.getHoraSalidaTarde());

            // Actualizar el objeto trabajador
            trabajador.setHoraEntradaManana(horaEntradaMananaField.getText());
            trabajador.setHoraSalidaManana(horaSalidaMananaField.getText());
            trabajador.setHoraEntradaTarde(horaEntradaTardeField.getText());
            trabajador.setHoraSalidaTarde(horaSalidaTardeField.getText());

            System.out.println("Horarios después de actualizar:");
            System.out.println("Entrada Mañana: " + trabajador.getHoraEntradaManana());
            System.out.println("Salida Mañana: " + trabajador.getHoraSalidaManana());
            System.out.println("Entrada Tarde: " + trabajador.getHoraEntradaTarde());
            System.out.println("Salida Tarde: " + trabajador.getHoraSalidaTarde());

            // Actualizar en Firebase
            System.out.println("Actualizando en Firebase...");
            FirebaseRESTExample.actualizarHorarioTrabajador(trabajador);
            System.out.println("Actualización en Firebase completada");

            // Actualizar el horario en la información personal
            String horario = String.format("Mañana: %s - %s\nTarde: %s - %s",
                    formatearHora(trabajador.getHoraEntradaManana()),
                    formatearHora(trabajador.getHoraSalidaManana()),
                    formatearHora(trabajador.getHoraEntradaTarde()),
                    formatearHora(trabajador.getHoraSalidaTarde()));
            horarioLabel.setText(horario);

            System.out.println("Actualizando tablas...");
            // Actualizar las tablas con los nuevos estados
            actualizarTablasConFichajes(fichajesOriginales);

            // Restaurar la selección del período
            if (periodoSeleccionado != null) {
                periodoFiltro.setValue(periodoSeleccionado);
                filtrarPorFecha();
            }

            // Forzar la actualización de las celdas de iconos
            entradasMananaTableView.refresh();
            entradasTardeTableView.refresh();
            salidasMananaTableView.refresh();
            salidasTardeTableView.refresh();
            System.out.println("Actualización de tablas completada");

            mostrarAlerta("Éxito", "Horario actualizado correctamente");
        } catch (Exception e) {
            System.err.println("Error al actualizar el horario: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo actualizar el horario: " + e.getMessage());
        }
    }

    private boolean validarFormatoHora(String hora) {
        if (hora == null || hora.isEmpty()) return false;
        try {
            String[] partes = hora.split(":");
            if (partes.length != 2) return false;
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);
            return horas >= 0 && horas < 24 && minutos >= 0 && minutos < 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void calcularEstadisticas() {
        if (fichajesTrabajador == null || fichajesTrabajador.isEmpty()) {
            diasTrabajadosLabel.setText("0 días");
            return;
        }

        // Calcular días trabajados
        Set<LocalDate> diasUnicos = new HashSet<>();
        for (RegistroFichaje registro : fichajesTrabajador) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime fechaHora = LocalDateTime.parse(registro.getFechaHora(), formatter);
                diasUnicos.add(fechaHora.toLocalDate());
            } catch (Exception e) {
                System.err.println("Error al procesar la fecha: " + registro.getFechaHora());
            }
        }

        int totalDias = diasUnicos.size();
        diasTrabajadosLabel.setText(totalDias + " días");
    }

    private void configurarTamanioDatePicker(DatePicker datePicker) {
        datePicker.setEditable(false);
        datePicker.setPrefWidth(250);
        datePicker.setMaxWidth(250);
        datePicker.setMinWidth(250);

        // Configurar el popup
        datePicker.setOnShowing(event -> {
            PopupControl popup = (PopupControl) datePicker.getScene().getWindow();
            popup.setWidth(250);
            popup.setHeight(300);
        });
    }

    private void configurarColumnasTabla(TableColumn<RegistroFichaje, String> fechaCol,
                                       TableColumn<RegistroFichaje, String> tipoCol,
                                       TableColumn<RegistroFichaje, String> estadoCol,
                                       TableColumn<RegistroFichaje, String> iconosCol) {
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        estadoCol.setCellValueFactory(new PropertyValueFactory<>("estado"));
        iconosCol.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));

        // Formatear la columna de fecha y hora
        fechaCol.setCellFactory(column -> new TableCell<RegistroFichaje, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    try {
                        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm");
                        LocalDateTime fecha = LocalDateTime.parse(item, parser);
                        setText(fecha.format(formatter));
                        getStyleClass().add("date-time-cell");
                        
                        // Añadir efecto de pulsación
                        setOnMousePressed(e -> {
                            setStyle("-fx-background-color: #e0e0e0;");
                        });
                        setOnMouseReleased(e -> {
                            setStyle("");
                        });
                    } catch (Exception e) {
                        setText(item);
                    }
                }
            }
        });

        // Aplicar estilos y efectos de pulsación a las columnas
        estadoCol.setCellFactory(column -> new TableCell<RegistroFichaje, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().add("estado-column");
                    
                    // Añadir efecto de pulsación
                    setOnMousePressed(e -> {
                        setStyle("-fx-background-color: #e0e0e0;");
                    });
                    setOnMouseReleased(e -> {
                        setStyle("");
                    });
                }
            }
        });

        iconosCol.getStyleClass().add("iconos-column");
    }

    private void configurarColumnasIconos(TableColumn<RegistroFichaje, String> col, Image verde, Image rojo) {
        col.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    try {
                        HBox iconos = new HBox(5);
                        iconos.setStyle("-fx-alignment: center;");

                        if (getTableRow() != null && getTableRow().getItem() != null) {
                            RegistroFichaje registro = getTableRow().getItem();
                            String estado = registro.getEstado();
                            String tipo = registro.getTipo();
                            System.out.println("Procesando iconos para registro: " + registro.getFechaHora());
                            System.out.println("Estado: " + estado);
                            System.out.println("Tipo: " + tipo);

                            switch (estado) {
                                case "Puntual":
                                    ImageView iconoVerde = new ImageView(verde);
                                    iconoVerde.setFitHeight(16);
                                    iconoVerde.setFitWidth(16);
                                    iconos.getChildren().add(iconoVerde);
                                    break;
                                case "Anticipada":
                                    if (tipo.equals("entrada")) {
                                        // Para entradas anticipadas, mostrar dos puntos verdes
                                        ImageView iconoVerde1 = new ImageView(verde);
                                        iconoVerde1.setFitHeight(16);
                                        iconoVerde1.setFitWidth(16);
                                        ImageView iconoVerde2 = new ImageView(verde);
                                        iconoVerde2.setFitHeight(16);
                                        iconoVerde2.setFitWidth(16);
                                        iconos.getChildren().addAll(iconoVerde1, iconoVerde2);
                                    } else {
                                        // Para salidas anticipadas, mostrar un punto rojo
                                        ImageView iconoRojo = new ImageView(rojo);
                                        iconoRojo.setFitHeight(16);
                                        iconoRojo.setFitWidth(16);
                                        iconos.getChildren().add(iconoRojo);
                                    }
                                    break;
                                case "Retrasada":
                                case "Tardía":
                                    ImageView iconoVerde3 = new ImageView(verde);
                                    iconoVerde3.setFitHeight(16);
                                    iconoVerde3.setFitWidth(16);
                                    ImageView iconoRojo2 = new ImageView(rojo);
                                    iconoRojo2.setFitHeight(16);
                                    iconoRojo2.setFitWidth(16);
                                    iconos.getChildren().addAll(iconoVerde3, iconoRojo2);
                                    break;
                            }
                        }

                        setGraphic(iconos);
                    } catch (Exception e) {
                        System.err.println("Error al procesar los iconos: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    private void mostrarEntradasManana() {
        entradasMananaTableView.setVisible(true);
        entradasTardeTableView.setVisible(false);
        menuEntradas.setText("Mañana");
    }

    @FXML
    private void mostrarEntradasTarde() {
        entradasMananaTableView.setVisible(false);
        entradasTardeTableView.setVisible(true);
        menuEntradas.setText("Tarde");
    }

    @FXML
    private void mostrarSalidasManana() {
        salidasMananaTableView.setVisible(true);
        salidasTardeTableView.setVisible(false);
        menuSalidas.setText("Mañana");
    }

    @FXML
    private void mostrarSalidasTarde() {
        salidasMananaTableView.setVisible(false);
        salidasTardeTableView.setVisible(true);
        menuSalidas.setText("Tarde");
    }

    private void limpiarRecursos(WindowEvent event) {
        // Limpiar referencias a objetos grandes
        fichajesTrabajador = null;
        fichajesOriginales = null;
        diasTrabajados.clear();
        
        // Limpiar las tablas
        entradasMananaTableView.getItems().clear();
        entradasTardeTableView.getItems().clear();
        salidasMananaTableView.getItems().clear();
        salidasTardeTableView.getItems().clear();
        
        // Limpiar los DatePickers
        calendarioTrabajo.setValue(null);
        fechaPersonalizada.setValue(null);
        
        // Limpiar los campos de texto
        horaEntradaMananaField.clear();
        horaSalidaMananaField.clear();
        horaEntradaTardeField.clear();
        horaSalidaTardeField.clear();
    }
}