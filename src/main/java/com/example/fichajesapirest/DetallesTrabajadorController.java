package com.example.fichajesapirest;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;                                                        
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
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

import javafx.scene.control.DatePicker;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import java.time.temporal.ChronoUnit;
import javafx.beans.property.SimpleStringProperty;
import java.util.Map;
import java.util.HashMap;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.application.Platform;
import java.awt.Desktop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controlador para la vista de detalles del trabajador.
 * Gestiona la visualización y manipulación de la información del trabajador,
 * incluyendo sus fichajes, horarios, vacaciones y generación de informes PDF.
 */
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
    @FXML
    private DatePicker vacacionesDesdePicker;
    @FXML
    private DatePicker vacacionesHastaPicker;
    @FXML
    private Button guardarVacacionesButton;
    @FXML
    private Button eliminarVacacionesButton;
    @FXML
    private TableView<Festivo> festivosTableView;
    @FXML
    private TableColumn<Festivo, LocalDate> fechaFestivoCol;
    @FXML
    private TableColumn<Festivo, String> nombreFestivoCol;
    @FXML
    private TableColumn<Festivo, String> tipoFestivoCol;
    @FXML
    private Button verHistorialButton;
    @FXML
    private TextArea vacacionesTextArea;
    @FXML
    private ComboBox<String> mesHorasExtrasCombo;
    @FXML
    private ComboBox<Integer> añoHorasExtrasCombo;
    @FXML
    private TableView<HorasExtra> horasExtrasTableView;
    @FXML
    private TableColumn<HorasExtra, LocalDate> fechaHorasExtrasCol;
    @FXML
    private TableColumn<HorasExtra, Double> horasAcumuladasCol;
    @FXML
    private TableColumn<HorasExtra, String> estadoHorasExtrasCol;
    @FXML
    private TableColumn<HorasExtra, LocalDateTime> fechaCalculoCol;
    @FXML
    private Label totalHorasExtrasLabel;

    private Trabajador trabajador;
    private List<RegistroFichaje> fichajesTrabajador;
    private Set<LocalDate> diasTrabajados = new HashSet<>();
    private List<RegistroFichaje> fichajesOriginales;
    private List<RegistroHistorial> historialPDFs = new ArrayList<>();
    private static final String HISTORIAL_FILE_PATTERN = "historial_pdfs_%s.json";
    private static final String ESTADO_CARGA_FILE_PATTERN = "estado_carga_%s.json";
    private LocalDateTime fechaCambioHorario = null;
    private FirebaseRESTExample firebase = new FirebaseRESTExample();

    private HorasExtraService horasExtraService = new HorasExtraService();

    /**
     * Inicializa el controlador y configura los componentes de la interfaz.
     * @param location La ubicación utilizada para resolver rutas relativas
     * @param resources Los recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cargar las imágenes
        Image verde = new Image(getClass().getResource("/verde.png").toExternalForm());
        Image rojo = new Image(getClass().getResource("/rojo.png").toExternalForm());
        Image advertencia = new Image(getClass().getResource("/advertencia_icono.png").toExternalForm());

        // Configurar las columnas de estado
        estadoEntradaMananaCol.setCellValueFactory(new PropertyValueFactory<>("estado"));
        estadoEntradaTardeCol.setCellValueFactory(new PropertyValueFactory<>("estado"));
        estadoSalidaMananaCol.setCellValueFactory(new PropertyValueFactory<>("estado"));
        estadoSalidaTardeCol.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar las columnas de iconos
        iconosEntradaMananaColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        iconosEntradaTardeColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        iconosSalidaMananaColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        iconosSalidaTardeColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar las columnas de fecha y tipo
        fechaEntradaMananaCol.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        tipoEntradaMananaCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        fechaEntradaTardeCol.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        tipoEntradaTardeCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        fechaSalidaMananaCol.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        tipoSalidaMananaCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        fechaSalidaTardeCol.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        tipoSalidaTardeCol.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Configurar los cellFactory para los iconos
        iconosEntradaMananaColumn.setCellFactory(col -> crearCellFactoryIconos());
        iconosEntradaTardeColumn.setCellFactory(col -> crearCellFactoryIconos());
        iconosSalidaMananaColumn.setCellFactory(col -> crearCellFactoryIconos());
        iconosSalidaTardeColumn.setCellFactory(col -> crearCellFactoryIconos());

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

        // Configurar las columnas de la tabla de festivos
        fechaFestivoCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFecha()));
        nombreFestivoCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        tipoFestivoCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo()));

        // Configurar el formateador de fecha para la columna de fecha
        fechaFestivoCol.setCellFactory(col -> new TableCell<Festivo, LocalDate>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        // Cargar los festivos del año actual
        cargarFestivos();

        // Cargar el historial de PDFs al iniciar
        cargarHistorialPDFs();

        // Configurar el TextArea de vacaciones
        vacacionesTextArea.setEditable(false);
        vacacionesTextArea.setWrapText(true);
        vacacionesTextArea.setStyle("-fx-background-color: white; -fx-font-size: 12px;");

        // Inicializar los combos de mes y año para horas extras
        mesHorasExtrasCombo.getItems().addAll(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );
        
        int añoActual = LocalDate.now().getYear();
        añoHorasExtrasCombo.getItems().addAll(
            añoActual - 1, añoActual, añoActual + 1
        );
        añoHorasExtrasCombo.setValue(añoActual);

        // Configurar la tabla de horas extras
        configurarTablaHorasExtras();
    }

    private void configurarTablaHorasExtras() {
        fechaHorasExtrasCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        horasAcumuladasCol.setCellValueFactory(new PropertyValueFactory<>("horasAcumuladas"));
        estadoHorasExtrasCol.setCellValueFactory(new PropertyValueFactory<>("estado"));
        fechaCalculoCol.setCellValueFactory(new PropertyValueFactory<>("fechaCalculo"));

        // Formatear la columna de fecha
        fechaHorasExtrasCol.setCellFactory(col -> new TableCell<HorasExtra, LocalDate>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        // Formatear la columna de horas acumuladas
        horasAcumuladasCol.setCellFactory(col -> new TableCell<HorasExtra, Double>() {
            @Override
            protected void updateItem(Double horas, boolean empty) {
                super.updateItem(horas, empty);
                if (empty || horas == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", horas));
                }
            }
        });

        // Formatear la columna de fecha de cálculo
        fechaCalculoCol.setCellFactory(col -> new TableCell<HorasExtra, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });
    }

    @FXML
    private void calcularHorasExtras() {
        if (trabajador == null) {
            mostrarAlerta("Error", "No hay trabajador seleccionado");
            return;
        }

        String mesSeleccionado = mesHorasExtrasCombo.getValue();
        Integer añoSeleccionado = añoHorasExtrasCombo.getValue();

        if (mesSeleccionado == null || añoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, seleccione mes y año");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cálculo");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Desea calcular las horas extras para " + mesSeleccionado + " de " + añoSeleccionado + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                int numeroMes = obtenerNumeroMes(mesSeleccionado);
                horasExtraService.calcularHorasExtrasTrabajador(trabajador, numeroMes, añoSeleccionado);
                cargarHorasExtras();
                mostrarAlerta("Éxito", "Horas extras calculadas correctamente");
            } catch (Exception e) {
                mostrarAlerta("Error", "Error al calcular horas extras: " + e.getMessage());
            }
        }
    }

    private void cargarHorasExtras() {
        if (trabajador == null) return;

        List<HorasExtra> horasExtras = horasExtraService.getHorasExtrasTrabajador(trabajador);
        horasExtrasTableView.getItems().setAll(horasExtras);

        // Calcular y mostrar el total
        double total = horasExtras.stream()
            .mapToDouble(HorasExtra::getHorasAcumuladas)
            .sum();
        totalHorasExtrasLabel.setText(String.format("%.2f", total));
    }

    private int obtenerNumeroMes(String nombreMes) {
        Map<String, Integer> meses = new HashMap<>();
        meses.put("Enero", 1);
        meses.put("Febrero", 2);
        meses.put("Marzo", 3);
        meses.put("Abril", 4);
        meses.put("Mayo", 5);
        meses.put("Junio", 6);
        meses.put("Julio", 7);
        meses.put("Agosto", 8);
        meses.put("Septiembre", 9);
        meses.put("Octubre", 10);
        meses.put("Noviembre", 11);
        meses.put("Diciembre", 12);

        return meses.getOrDefault(nombreMes, 1);
    }

    /**
     * Aplica una animación de entrada a un elemento de la interfaz.
     * @param node El nodo a animar
     * @param delay El retraso en milisegundos antes de iniciar la animación
     */
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

    /**
     * Configura el calendario de trabajo con los días trabajados.
     */
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

    /**
     * Actualiza el resumen mensual de días trabajados.
     * @param fecha La fecha para la cual se calcula el resumen
     */
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

        if (resumenMensualLabel != null) {
        resumenMensualLabel.setText(resumen.toString());
        }
    }

    /**
     * Actualiza el calendario con los días trabajados.
     */
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

    /**
     * Establece el trabajador y sus fichajes en el controlador.
     * @param trabajador El trabajador a mostrar
     * @param registroFichajes La lista de fichajes del trabajador
     */
    public void setTrabajador(Trabajador trabajador, List<RegistroFichaje> registroFichajes) {
        this.trabajador = trabajador;
        
        // Verificar si el trabajador tiene vacaciones establecidas y si alguna es futura o en curso
        if (trabajador.getPeriodosVacaciones() != null && !trabajador.getPeriodosVacaciones().isEmpty()) {
            LocalDate hoy = LocalDate.now();
            List<PeriodoVacaciones> vacacionesActivas = trabajador.getPeriodosVacaciones().stream()
                .filter(periodo -> {
                    try {
                        LocalDate fechaHasta = LocalDate.parse(periodo.getFechaHasta());
                        return !fechaHasta.isBefore(hoy); // Incluye vacaciones en curso y futuras
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

            if (!vacacionesActivas.isEmpty()) {
                // Crear el diálogo de información
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Información de Vacaciones");
                alert.setHeaderText("El trabajador tiene períodos de vacaciones activos");
                
                // Crear el contenido del diálogo
                StringBuilder content = new StringBuilder();
                content.append("Períodos de vacaciones activos:\n\n");
                
                for (PeriodoVacaciones periodo : vacacionesActivas) {
                    try {
                        LocalDate fechaDesde = LocalDate.parse(periodo.getFechaDesde());
                        LocalDate fechaHasta = LocalDate.parse(periodo.getFechaHasta());
                        
                        // Determinar si las vacaciones están en curso o son futuras
                        String estado = fechaDesde.isAfter(hoy) ? "Futuras" : "En curso";
                        
                        content.append(estado).append(":\n")
                               .append("Desde: ").append(fechaDesde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                               .append("\nHasta: ").append(fechaHasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                               .append("\n\n");
                    } catch (Exception e) {
                        content.append("Desde: ").append(periodo.getFechaDesde())
                               .append("\nHasta: ").append(periodo.getFechaHasta())
                               .append("\n\n");
                    }
                }
                
                alert.setContentText(content.toString());
                
                // Mostrar el diálogo y esperar a que el usuario pulse Aceptar
                alert.showAndWait();
            }
        }
        
        // Cargar el estado de carga para obtener la fecha de cambio de horario
        cargarEstadoCarga();
        
        // Obtener todos los fichajes del trabajador
        List<RegistroFichaje> todosLosFichajes = firebase.obtenerFichajesTrabajador(trabajador.getDni());
        
        // Filtrar los fichajes según la fecha de cambio de horario
        if (fechaCambioHorario != null) {
            this.fichajesTrabajador = todosLosFichajes.stream()
                .filter(fichaje -> {
                    try {
                        LocalDateTime fechaFichaje = LocalDateTime.parse(fichaje.getFechaHora(), 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return !fechaFichaje.isBefore(fechaCambioHorario);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        } else {
            this.fichajesTrabajador = new ArrayList<>(todosLosFichajes);
        }
        
        this.fichajesOriginales = new ArrayList<>(fichajesTrabajador);
        this.diasTrabajados = new HashSet<>();

        // Actualizar la interfaz con los datos del trabajador
        nombreLabel.setText(trabajador.getNombre());
        apellidosLabel.setText(trabajador.getApellidos());
        dniLabel.setText(trabajador.getDni());

        // Configurar el horario
        StringBuilder horario = new StringBuilder();
        if (trabajador.getHoraEntradaManana() != null && trabajador.getHoraSalidaManana() != null) {
            horario.append("Mañana: ").append(formatearHora(trabajador.getHoraEntradaManana()))
                  .append(" - ").append(formatearHora(trabajador.getHoraSalidaManana())).append("\n");
        }
        if (trabajador.getHoraEntradaTarde() != null && trabajador.getHoraSalidaTarde() != null) {
            horario.append("Tarde: ").append(formatearHora(trabajador.getHoraEntradaTarde()))
                  .append(" - ").append(formatearHora(trabajador.getHoraSalidaTarde()));
        }
        horarioLabel.setText(horario.toString());

        // Configurar los campos de horario
        horaEntradaMananaField.setText(formatearHora(trabajador.getHoraEntradaManana()));
        horaSalidaMananaField.setText(formatearHora(trabajador.getHoraSalidaManana()));
        horaEntradaTardeField.setText(formatearHora(trabajador.getHoraEntradaTarde()));
        horaSalidaTardeField.setText(formatearHora(trabajador.getHoraSalidaTarde()));

        // Cargar los períodos de vacaciones en el TextArea
        if (trabajador.getPeriodosVacaciones() != null) {
            StringBuilder sb = new StringBuilder();
            for (PeriodoVacaciones periodo : trabajador.getPeriodosVacaciones()) {
                sb.append("Desde: ").append(periodo.getFechaDesde())
                  .append(" - Hasta: ").append(periodo.getFechaHasta())
                  .append("\n");
            }
            vacacionesTextArea.setText(sb.toString());
        }

        // Cargar los fichajes en las tablas
        cargarFichajesEnTablas();

        // Actualizar el calendario
        actualizarCalendario();

        // Calcular y mostrar estadísticas
        calcularEstadisticas();

        // Cargar el historial de PDFs
        cargarHistorialPDFs();

        // Cargar las horas extras del trabajador
        cargarHorasExtras();
    }

    /**
     * Formatea una hora en formato HH:mm.
     * @param hora La hora a formatear
     * @return La hora formateada
     */
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

    /**
     * Crea un icono con la imagen especificada.
     * @param imagen La imagen para el icono
     * @return El ImageView configurado
     */
    private ImageView crearIcono(Image imagen) {
        ImageView view = new ImageView(imagen);
        view.setFitWidth(20);
        view.setFitHeight(20);
        return view;
    }

    /**
     * Cierra la ventana actual.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Exporta los datos del trabajador a un archivo CSV.
     */
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

    /**
     * Exporta los datos a un archivo CSV.
     * @param file El archivo donde se guardarán los datos
     * @param tipoExportacion El tipo de datos a exportar
     * @throws IOException Si ocurre un error al escribir el archivo
     */
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

    /**
     * Muestra una alerta con el título y mensaje especificados.
     * @param titulo El título de la alerta
     * @param mensaje El mensaje a mostrar
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Filtra los fichajes por fecha según el período seleccionado.
     */
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

    /**
     * Limpia los filtros aplicados.
     */
    @FXML
    private void limpiarFiltro() {
        periodoFiltro.setValue(null);
        fechaPersonalizada.setValue(null);
        actualizarTablasConFichajes(fichajesTrabajador);
    }

    /**
     * Guarda el horario del trabajador.
     */
    @FXML
    private void guardarHorario() {
        try {
            // Validar los horarios (permitiendo campos vacíos)
            if (!validarFormatoHora(horaEntradaMananaField.getText()) ||
                    !validarFormatoHora(horaSalidaMananaField.getText()) ||
                    !validarFormatoHora(horaEntradaTardeField.getText()) ||
                    !validarFormatoHora(horaSalidaTardeField.getText())) {
                mostrarAlerta("Error", "Los horarios deben estar en formato HH:mm o vacíos");
                return;
            }

            // Guardar los fichajes actuales antes de limpiar
            List<RegistroFichaje> fichajesActuales = new ArrayList<>(fichajesTrabajador);

            // Actualizar solo los horarios en el objeto trabajador
            trabajador.setHoraEntradaManana(horaEntradaMananaField.getText().isEmpty() ? null : horaEntradaMananaField.getText());
            trabajador.setHoraSalidaManana(horaSalidaMananaField.getText().isEmpty() ? null : horaSalidaMananaField.getText());
            trabajador.setHoraEntradaTarde(horaEntradaTardeField.getText().isEmpty() ? null : horaEntradaTardeField.getText());
            trabajador.setHoraSalidaTarde(horaSalidaTardeField.getText().isEmpty() ? null : horaSalidaTardeField.getText());

            // Actualizar en Firebase
            FirebaseRESTExample.actualizarHorarioTrabajador(trabajador);

            // Actualizar el horario en la información personal
            StringBuilder horario = new StringBuilder();
            if (trabajador.getHoraEntradaManana() != null && trabajador.getHoraSalidaManana() != null) {
                horario.append("Mañana: ").append(formatearHora(trabajador.getHoraEntradaManana()))
                      .append(" - ").append(formatearHora(trabajador.getHoraSalidaManana())).append("\n");
            }
            if (trabajador.getHoraEntradaTarde() != null && trabajador.getHoraSalidaTarde() != null) {
                horario.append("Tarde: ").append(formatearHora(trabajador.getHoraEntradaTarde()))
                      .append(" - ").append(formatearHora(trabajador.getHoraSalidaTarde()));
            }
            horarioLabel.setText(horario.toString());

            // Generar PDF con los fichajes actuales
            generarYMostrarPDF();

            // Limpiar las tablas y los datos
            entradasMananaTableView.getItems().clear();
            entradasTardeTableView.getItems().clear();
            salidasMananaTableView.getItems().clear();
            salidasTardeTableView.getItems().clear();

            // Limpiar los indicadores
            indicadorRetrasos.setText("0");
            indicadorAusencias.setText("0");

            // Limpiar el calendario
            calendarioTrabajo.setValue(null);
            diasTrabajadosLabel.setText("0 días");
            diasTrabajadosCalendarioLabel.setText("0 días");
            resumenMensualLabel.setText("");

            // Limpiar la lista de fichajes
            fichajesTrabajador.clear();
            diasTrabajados.clear();

            // Guardar la fecha del cambio de horario
            fechaCambioHorario = LocalDateTime.now();
            guardarEstadoCarga();

            mostrarAlerta("Éxito", "Horario actualizado correctamente. Las tablas se han limpiado para registrar los nuevos fichajes con el nuevo horario.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el horario: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo actualizar el horario: " + e.getMessage());
        }
    }

    /**
     * Valida el formato de una hora.
     * @param hora La hora a validar
     * @return true si el formato es válido, false en caso contrario
     */
    private boolean validarFormatoHora(String hora) {
        if (hora == null || hora.isEmpty()) return true; // Permitir campos vacíos
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

    /**
     * Calcula las estadísticas de asistencia del trabajador.
     */
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

    /**
     * Configura el tamaño del DatePicker.
     * @param datePicker El DatePicker a configurar
     */
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

    /**
     * Configura las columnas de una tabla de fichajes.
     * @param fechaCol Columna de fecha
     * @param tipoCol Columna de tipo
     * @param estadoCol Columna de estado
     * @param iconosCol Columna de iconos
     */
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

    /**
     * Configura las columnas de iconos en una tabla.
     * @param col La columna a configurar
     * @param verde Imagen para estado correcto
     * @param rojo Imagen para estado incorrecto
     * @param advertencia Imagen para advertencias
     */
    private void configurarColumnasIconos(TableColumn<RegistroFichaje, String> col, Image verde, Image rojo, Image advertencia) {
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
                            
                            // Si el estado es null, no mostramos ningún icono
                            if (estado == null) {
                                setGraphic(null);
                                return;
                            }


                            switch (estado) {
                                case "Puntual":
                                    ImageView iconoVerde = new ImageView(verde);
                                    iconoVerde.setFitHeight(16);
                                    iconoVerde.setFitWidth(16);
                                    iconos.getChildren().add(iconoVerde);
                                    break;
                                case "Anticipada":
                                    if (tipo.equals("entrada")) {
                                        ImageView iconoVerde1 = new ImageView(verde);
                                        iconoVerde1.setFitHeight(16);
                                        iconoVerde1.setFitWidth(16);
                                        ImageView iconoVerde2 = new ImageView(verde);
                                        iconoVerde2.setFitHeight(16);
                                        iconoVerde2.setFitWidth(16);
                                        iconos.getChildren().addAll(iconoVerde1, iconoVerde2);
                                    } else {
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
                                    
                                    Region espacio = new Region();
                                    espacio.setPrefWidth(10);
                                    
                                    ImageView iconoAdvertencia = new ImageView(advertencia);
                                    iconoAdvertencia.setFitHeight(16);
                                    iconoAdvertencia.setFitWidth(16);
                                    iconoAdvertencia.setStyle("-fx-cursor: hand;");
                                    
                                    if (trabajador != null) {
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        LocalDateTime fechaRegistro = LocalDateTime.parse(registro.getFechaHora(), formatter);
                                        LocalTime horaRegistro = fechaRegistro.toLocalTime();
                                        
                                        LocalTime horaEsperada;
                                        if (tipo.equals("entrada")) {
                                            horaEsperada = fechaRegistro.toLocalTime().isBefore(LocalTime.of(14, 0)) ?
                                                LocalTime.parse(formatearHora(trabajador.getHoraEntradaManana())) :
                                                LocalTime.parse(formatearHora(trabajador.getHoraEntradaTarde()));
                                        } else {
                                            horaEsperada = fechaRegistro.toLocalTime().isBefore(LocalTime.of(14, 0)) ?
                                                LocalTime.parse(formatearHora(trabajador.getHoraSalidaManana())) :
                                                LocalTime.parse(formatearHora(trabajador.getHoraSalidaTarde()));
                                        }
                                        
                                        long minutosRetraso = ChronoUnit.MINUTES.between(horaEsperada, horaRegistro);
                                        
                                        iconoAdvertencia.setOnMouseClicked(event -> {
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Detalles del Retraso");
                                            alert.setHeaderText(null);
                                            alert.setContentText(String.format(
                                                "Tipo: %s\n" +
                                                "Hora esperada: %s\n" +
                                                "Hora real: %s\n" +
                                                "Tiempo de retraso: %d minutos",
                                                tipo.equals("entrada") ? "Entrada" : "Salida",
                                                horaEsperada.format(DateTimeFormatter.ofPattern("HH:mm")),
                                                horaRegistro.format(DateTimeFormatter.ofPattern("HH:mm")),
                                                minutosRetraso
                                            ));
                                            alert.showAndWait();
                                        });
                                    }
                                    
                                    iconos.getChildren().addAll(iconoVerde3, iconoRojo2, espacio, iconoAdvertencia);
                                    break;
                                default:
                                    // Si el estado no coincide con ninguno de los casos anteriores, no mostramos iconos
                                    setGraphic(null);
                                    return;
                            }
                        }

                        setGraphic(iconos);
                    } catch (Exception e) {
                        System.err.println("Error al procesar los iconos: " + e.getMessage());
                        e.printStackTrace();
                        setGraphic(null);
                    }
                }
            }
        });
    }

    /**
     * Muestra las entradas de la mañana.
     */
    @FXML
    private void mostrarEntradasManana() {
        entradasMananaTableView.setVisible(true);
        entradasTardeTableView.setVisible(false);
        menuEntradas.setText("Mañana");
    }

    /**
     * Muestra las entradas de la tarde.
     */
    @FXML
    private void mostrarEntradasTarde() {
        entradasMananaTableView.setVisible(false);
        entradasTardeTableView.setVisible(true);
        menuEntradas.setText("Tarde");
    }

    /**
     * Muestra las salidas de la mañana.
     */
    @FXML
    private void mostrarSalidasManana() {
        salidasMananaTableView.setVisible(true);
        salidasTardeTableView.setVisible(false);
        menuSalidas.setText("Mañana");
    }

    /**
     * Muestra las salidas de la tarde.
     */
    @FXML
    private void mostrarSalidasTarde() {
        salidasMananaTableView.setVisible(false);
        salidasTardeTableView.setVisible(true);
        menuSalidas.setText("Tarde");
    }

    /**
     * Limpia los recursos al cerrar la ventana.
     * @param event El evento de cierre
     */
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

        // Guardar el estado de carga antes de cerrar
        if (trabajador != null) {
            guardarEstadoCarga();
        }
    }

    /**
     * Guarda un nuevo período de vacaciones.
     */
    @FXML
    private void guardarVacaciones() {
        if (trabajador == null) return;

        LocalDate fechaDesde = vacacionesDesdePicker.getValue();
        LocalDate fechaHasta = vacacionesHastaPicker.getValue();

        if (fechaDesde == null || fechaHasta == null) {
            mostrarAlerta("Error", "Por favor, seleccione ambas fechas.");
            return;
        }

        if (fechaDesde.isAfter(fechaHasta)) {
            mostrarAlerta("Error", "La fecha de inicio debe ser anterior a la fecha de fin.");
            return;
        }

        // Crear nuevo período de vacaciones
        PeriodoVacaciones nuevoPeriodo = new PeriodoVacaciones(
            fechaDesde.format(DateTimeFormatter.ISO_LOCAL_DATE),
            fechaHasta.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        // Añadir el período a la lista del trabajador
        if (trabajador.getPeriodosVacaciones() == null) {
            trabajador.setPeriodosVacaciones(new ArrayList<>());
        }
        trabajador.getPeriodosVacaciones().add(nuevoPeriodo);

        // Actualizar el TextArea
        StringBuilder sb = new StringBuilder();
        for (PeriodoVacaciones periodo : trabajador.getPeriodosVacaciones()) {
            sb.append("Desde: ").append(periodo.getFechaDesde())
              .append(" - Hasta: ").append(periodo.getFechaHasta())
              .append("\n");
        }
        vacacionesTextArea.setText(sb.toString());

        // Limpiar los DatePickers
        vacacionesDesdePicker.setValue(null);
        vacacionesHastaPicker.setValue(null);

        // Guardar los cambios
        guardarCambios();
    }

    /**
     * Elimina todos los períodos de vacaciones.
     */
    @FXML
    private void eliminarVacaciones() {
        if (trabajador == null || trabajador.getPeriodosVacaciones() == null || trabajador.getPeriodosVacaciones().isEmpty()) {
            mostrarAlerta("Error", "No hay períodos de vacaciones para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar todos los períodos de vacaciones?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            trabajador.getPeriodosVacaciones().clear();
            vacacionesTextArea.clear();
            guardarCambios();
        }
    }

    /**
     * Carga los festivos del año actual.
     */
    private void cargarFestivos() {
        try {
            int añoActual = LocalDate.now().getYear();
            List<Festivo> festivos = FestivosService.obtenerFestivos(añoActual);
            
            if (festivos.isEmpty()) {
                return;
            }
            
            // Ordenar los festivos por fecha
            festivos.sort((f1, f2) -> f1.getFecha().compareTo(f2.getFecha()));
            
            // Actualizar la tabla
            festivosTableView.getItems().setAll(festivos);
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los festivos: " + e.getMessage());
        }
    }

    /**
     * Crea una celda de tabla con iconos.
     * @return La celda configurada
     */
    private TableCell<RegistroFichaje, String> crearCellFactoryIconos() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                try {
                    RegistroFichaje registro = (RegistroFichaje) getTableRow().getItem();
                    String estado = registro.getEstado();
                    String tipo = registro.getTipo();

                    if (estado == null || estado.isEmpty()) {
                        setGraphic(null);
                        return;
                    }

                    HBox iconos = new HBox(5);
                    iconos.setStyle("-fx-alignment: center;");

                    switch (estado) {
                        case "Puntual":
                            ImageView iconoVerde = new ImageView(new Image(getClass().getResourceAsStream("/verde.png")));
                            iconoVerde.setFitHeight(16);
                            iconoVerde.setFitWidth(16);
                            iconos.getChildren().add(iconoVerde);
                            break;
                        case "Retrasada":
                        case "Tardía":
                            ImageView iconoVerde1 = new ImageView(new Image(getClass().getResourceAsStream("/verde.png")));
                            iconoVerde1.setFitHeight(16);
                            iconoVerde1.setFitWidth(16);
                            ImageView iconoRojo = new ImageView(new Image(getClass().getResourceAsStream("/rojo.png")));
                            iconoRojo.setFitHeight(16);
                            iconoRojo.setFitWidth(16);
                            iconos.getChildren().addAll(iconoVerde1, iconoRojo);
                            break;
                        case "Anticipada":
                            if ("salida".equals(tipo)) {
                                ImageView iconoRojo2 = new ImageView(new Image(getClass().getResourceAsStream("/rojo.png")));
                                iconoRojo2.setFitHeight(16);
                                iconoRojo2.setFitWidth(16);
                                iconos.getChildren().add(iconoRojo2);
                            } else {
                                ImageView iconoVerde2 = new ImageView(new Image(getClass().getResourceAsStream("/verde.png")));
                                iconoVerde2.setFitHeight(16);
                                iconoVerde2.setFitWidth(16);
                                ImageView iconoVerde3 = new ImageView(new Image(getClass().getResourceAsStream("/verde.png")));
                                iconoVerde3.setFitHeight(16);
                                iconoVerde3.setFitWidth(16);
                                iconos.getChildren().addAll(iconoVerde2, iconoVerde3);
                            }
                            break;
                    }

                    setGraphic(iconos);
                } catch (Exception e) {
                    setGraphic(null);
                }
            }
        };
    }

    /**
     * Carga los fichajes en las tablas.
     */
    private void cargarFichajesEnTablas() {
        if (fichajesTrabajador == null || fichajesTrabajador.isEmpty()) {
            return;
        }

        // Filtrar los fichajes según la fecha de cambio de horario
        List<RegistroFichaje> fichajesFiltrados = fichajesTrabajador;
        if (fechaCambioHorario != null) {
            fichajesFiltrados = fichajesTrabajador.stream()
                .filter(fichaje -> {
                    try {
                        LocalDateTime fechaFichaje = LocalDateTime.parse(fichaje.getFechaHora(), 
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return !fechaFichaje.isBefore(fechaCambioHorario);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        }

        // Actualizar las tablas con los fichajes filtrados
        actualizarTablasConFichajes(fichajesFiltrados);
    }

    /**
     * Actualiza las tablas con los fichajes proporcionados.
     * @param fichajes Lista de fichajes a mostrar
     */
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

                // Determinar si es mañana o tarde basado en el horario configurado
                boolean esManana = false;
                if (trabajador.getHoraEntradaManana() != null && trabajador.getHoraSalidaManana() != null) {
                    LocalTime horaSalidaManana = LocalTime.parse(formatearHora(trabajador.getHoraSalidaManana()));
                    esManana = horaRegistro.isBefore(horaSalidaManana.plusMinutes(30));
                } else if (trabajador.getHoraEntradaTarde() != null && trabajador.getHoraSalidaTarde() != null) {
                    LocalTime horaEntradaTarde = LocalTime.parse(formatearHora(trabajador.getHoraEntradaTarde()));
                    esManana = horaRegistro.isBefore(horaEntradaTarde);
                }

                // Obtener la hora esperada según el tipo de registro y turno
                LocalTime horaEsperada = null;
                if ("entrada".equals(registro.getTipo())) {
                    if (esManana && trabajador.getHoraEntradaManana() != null) {
                        horaEsperada = LocalTime.parse(formatearHora(trabajador.getHoraEntradaManana()));
                    } else if (!esManana && trabajador.getHoraEntradaTarde() != null) {
                        horaEsperada = LocalTime.parse(formatearHora(trabajador.getHoraEntradaTarde()));
                    }
                } else {
                    if (esManana && trabajador.getHoraSalidaManana() != null) {
                        horaEsperada = LocalTime.parse(formatearHora(trabajador.getHoraSalidaManana()));
                    } else if (!esManana && trabajador.getHoraSalidaTarde() != null) {
                        horaEsperada = LocalTime.parse(formatearHora(trabajador.getHoraSalidaTarde()));
                    }
                }

                // Asignar el estado según la diferencia
                String estado = "Puntual";
                if (horaEsperada != null) {
                    long diferenciaMinutos = ChronoUnit.MINUTES.between(horaEsperada, horaRegistro);
                    if (diferenciaMinutos <= -5) {
                        estado = "Anticipada";
                    } else if (diferenciaMinutos >= 5) {
                        estado = "Retrasada";
                        contadorRetrasos++;
                    }
                }

                // Asignar el estado al registro
                registro.setEstado(estado);

                // Añadir el registro a la lista correspondiente
                if ("entrada".equals(registro.getTipo())) {
                    if (esManana) {
                        entradasManana.add(registro);
                    } else {
                        entradasTarde.add(registro);
                    }
                } else if ("salida".equals(registro.getTipo())) {
                    if (esManana) {
                        salidasManana.add(registro);
                    } else {
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
        indicadorRetrasos.setText(String.valueOf(contadorRetrasos));
        indicadorAusencias.setText(String.valueOf(contadorAusencias));

        // Actualizar el calendario
        actualizarCalendario();
    }

    /**
     * Muestra el historial de PDFs generados.
     */
    @FXML
    private void verHistorial() {
        try {
            // Crear una nueva ventana para el historial
            Stage historialStage = new Stage();
            historialStage.initModality(Modality.APPLICATION_MODAL);
            historialStage.setTitle("Historial de PDFs - " + trabajador.getNombre() + " " + trabajador.getApellidos());

            // Crear la tabla para el historial
            TableView<RegistroHistorial> historialTable = new TableView<>();
            historialTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            historialTable.setStyle("-fx-background-color: white;");
            historialTable.setPrefHeight(700);

            // Crear las columnas
            TableColumn<RegistroHistorial, String> fechaCol = new TableColumn<>("Fecha de Generación");
            fechaCol.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
            fechaCol.setPrefWidth(180);
            fechaCol.setStyle("-fx-alignment: CENTER;");

            TableColumn<RegistroHistorial, String> nombreCol = new TableColumn<>("Nombre del Archivo");
            nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombreArchivo"));
            nombreCol.setPrefWidth(500);
            nombreCol.setStyle("-fx-alignment: CENTER;");

            // Columna para los botones de acción
            TableColumn<RegistroHistorial, Void> accionesCol = new TableColumn<>("Acciones");
            accionesCol.setPrefWidth(180);
            accionesCol.setStyle("-fx-alignment: CENTER;");

            accionesCol.setCellFactory(col -> new TableCell<>() {
                private final Button verButton = new Button("Ver");
                private final Button eliminarButton = new Button("Eliminar");
                private final HBox buttons = new HBox(10, verButton, eliminarButton);

                {
                    buttons.setStyle("-fx-alignment: CENTER;");
                    verButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 80px;");
                    eliminarButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-min-width: 80px;");

                    verButton.setOnAction(e -> {
                        RegistroHistorial registro = getTableView().getItems().get(getIndex());
                        try {
                            // Abrir el PDF en un hilo separado
                            new Thread(() -> {
                                try {
                                    File file = new File(registro.getRutaArchivo());
                                    if (file.exists()) {
                                        Desktop.getDesktop().open(file);
                                    } else {
                                        Platform.runLater(() -> 
                                            mostrarAlerta("Error", "El archivo no existe: " + registro.getRutaArchivo())
                                        );
                                    }
                                } catch (Exception ex) {
                                    Platform.runLater(() -> 
                                        mostrarAlerta("Error", "No se pudo abrir el archivo: " + ex.getMessage())
                                    );
                                }
                            }).start();

                        } catch (Exception ex) {
                            mostrarAlerta("Error", "No se pudo abrir el archivo: " + ex.getMessage());
                        }
                    });

                    eliminarButton.setOnAction(e -> {
                        RegistroHistorial registro = getTableView().getItems().get(getIndex());
                        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmacion.setTitle("Confirmar Eliminación");
                        confirmacion.setHeaderText(null);
                        confirmacion.setContentText("¿Está seguro de que desea eliminar este registro?");

                        if (confirmacion.showAndWait().get() == ButtonType.OK) {
                            try {
                                File file = new File(registro.getRutaArchivo());
                                if (file.exists()) {
                                    file.delete();
                                }
                                historialPDFs.remove(registro);
                                guardarHistorialPDFs();
                                historialTable.getItems().remove(registro);
                                mostrarAlerta("Éxito", "Registro eliminado correctamente");
                            } catch (Exception ex) {
                                mostrarAlerta("Error", "No se pudo eliminar el archivo: " + ex.getMessage());
                            }
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : buttons);
                }
            });

            // Añadir las columnas a la tabla
            historialTable.getColumns().addAll(fechaCol, nombreCol, accionesCol);

            // Cargar el historial de PDFs
            cargarHistorialPDFs();
            
            // Ordenar los registros por fecha (más recientes primero)
            historialPDFs.sort((a, b) -> b.getFechaGeneracion().compareTo(a.getFechaGeneracion()));

            // Añadir los registros a la tabla
            historialTable.getItems().addAll(historialPDFs);

            // Crear el contenedor principal
            VBox root = new VBox(10);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: white;");
            root.getChildren().add(historialTable);

            // Crear la escena
            Scene scene = new Scene(root, 1000, 600);

            // Configurar la ventana
            historialStage.setScene(scene);
            historialStage.setResizable(true);
            historialStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo mostrar el historial: " + e.getMessage());
        }
    }

    /**
     * Carga el historial de PDFs desde el archivo.
     */
    private void cargarHistorialPDFs() {
        try {
            if (trabajador == null) return;
            
            String historialFile = String.format(HISTORIAL_FILE_PATTERN, trabajador.getDni());
            File file = new File(historialFile);
            if (file.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                historialPDFs = mapper.readValue(file, 
                    new TypeReference<List<RegistroHistorial>>() {});
            } else {
                historialPDFs = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el historial de PDFs: " + e.getMessage());
            historialPDFs = new ArrayList<>();
        }
    }

    /**
     * Guarda el historial de PDFs en el archivo.
     */
    private void guardarHistorialPDFs() {
        try {
            if (trabajador == null) return;
            
            String historialFile = String.format(HISTORIAL_FILE_PATTERN, trabajador.getDni());
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(historialFile), historialPDFs);
        } catch (Exception e) {
            System.err.println("Error al guardar el historial de PDFs: " + e.getMessage());
        }
    }

    /**
     * Genera y muestra un PDF con los registros del trabajador.
     */
    private void generarYMostrarPDF() {
        try {
            // Crear el documento PDF
            Document document = new Document(PageSize.A4.rotate());
            
            // Obtener el mes más antiguo y más reciente de los fichajes
            LocalDate fechaMasAntigua = null;
            LocalDate fechaMasReciente = null;
            
            for (RegistroFichaje registro : fichajesTrabajador) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime fechaHora = LocalDateTime.parse(registro.getFechaHora(), formatter);
                    LocalDate fecha = fechaHora.toLocalDate();
                    
                    if (fechaMasAntigua == null || fecha.isBefore(fechaMasAntigua)) {
                        fechaMasAntigua = fecha;
                    }
                    if (fechaMasReciente == null || fecha.isAfter(fechaMasReciente)) {
                        fechaMasReciente = fecha;
                    }
                } catch (Exception e) {
                    System.err.println("Error al procesar fecha: " + registro.getFechaHora());
                }
            }
            
            // Hacer las variables finales para usarlas en el lambda
            final LocalDate fechaInicio = fechaMasAntigua;
            final LocalDate fechaFin = fechaMasReciente;
            
            // Formatear los nombres de los meses en español
            String mesAntiguo = fechaInicio != null ? 
                fechaInicio.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new Locale("es")) : "";
            String mesReciente = fechaFin != null ? 
                fechaFin.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new Locale("es")) : "";
            
            // Obtener la fecha y hora actual para el nombre del archivo
            String fechaHoraActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            // Crear el nombre del archivo con fecha y hora
            String fileName = String.format("Registro_%s_%s_%s_%s_%s.pdf",
                trabajador.getNombre(),
                trabajador.getApellidos(),
                mesAntiguo,
                mesReciente,
                fechaHoraActual);
            
            String filePath = "pdfs/" + fileName;
            
            // Crear el directorio si no existe
            new File("pdfs").mkdirs();
            
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Configurar fuentes
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);

            // Título
            Paragraph title = new Paragraph("Registro de Asistencia", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Información del trabajador
            Paragraph workerInfo = new Paragraph();
            workerInfo.setFont(normalFont);
            workerInfo.add("Trabajador: " + trabajador.getNombre() + " " + trabajador.getApellidos() + "\n");
            workerInfo.add("DNI: " + trabajador.getDni() + "\n");
            workerInfo.add("Horario del Período:\n");

            // Obtener el horario actual del trabajador
            if (trabajador.getHoraEntradaManana() != null && trabajador.getHoraSalidaManana() != null) {
                workerInfo.add("Mañana: " + formatearHora(trabajador.getHoraEntradaManana()) + 
                             " - " + formatearHora(trabajador.getHoraSalidaManana()) + "\n");
            }
            if (trabajador.getHoraEntradaTarde() != null && trabajador.getHoraSalidaTarde() != null) {
                workerInfo.add("Tarde: " + formatearHora(trabajador.getHoraEntradaTarde()) + 
                             " - " + formatearHora(trabajador.getHoraSalidaTarde()) + "\n");
            }
            workerInfo.setSpacingAfter(20);
            document.add(workerInfo);

            // Tabla de Entradas
            Paragraph entradasTitle = new Paragraph("Registro de Entradas", subtitleFont);
            entradasTitle.setSpacingBefore(20);
            entradasTitle.setSpacingAfter(10);
            document.add(entradasTitle);

            PdfPTable entradasTable = new PdfPTable(4);
            entradasTable.setWidthPercentage(100);
            entradasTable.setWidths(new float[]{3, 2, 2, 2});

            // Encabezados de la tabla de entradas
            String[] entradasHeaders = {"Fecha y Hora", "Tipo", "Estado", "Turno"};
            for (String header : entradasHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                entradasTable.addCell(cell);
            }

            // Datos de entradas
            for (RegistroFichaje registro : fichajesTrabajador) {
                if (registro.getTipo().equalsIgnoreCase("entrada")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime fechaHora = LocalDateTime.parse(registro.getFechaHora(), formatter);
                    String turno = fechaHora.toLocalTime().isBefore(LocalTime.of(14, 0)) ? "Mañana" : "Tarde";
                    
                    entradasTable.addCell(new PdfPCell(new Phrase(fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont)));
                    entradasTable.addCell(new PdfPCell(new Phrase(registro.getTipo(), normalFont)));
                    entradasTable.addCell(new PdfPCell(new Phrase(registro.getEstado(), normalFont)));
                    entradasTable.addCell(new PdfPCell(new Phrase(turno, normalFont)));
                }
            }
            document.add(entradasTable);

            // Sección de Vacaciones
            List<PeriodoVacaciones> periodosAEliminar = new ArrayList<>();
            if (trabajador.getPeriodosVacaciones() != null && !trabajador.getPeriodosVacaciones().isEmpty()) {
                List<PeriodoVacaciones> periodosRelevantes = trabajador.getPeriodosVacaciones().stream()
                    .filter(periodo -> {
                        LocalDate fechaDesdeVacaciones = LocalDate.parse(periodo.getFechaDesde());
                        LocalDate fechaHastaVacaciones = LocalDate.parse(periodo.getFechaHasta());
                        return (fechaInicio != null && !fechaHastaVacaciones.isBefore(fechaInicio)) &&
                               (fechaFin != null && !fechaDesdeVacaciones.isAfter(fechaFin));
                    })
                    .collect(Collectors.toList());

                if (!periodosRelevantes.isEmpty()) {
                    Paragraph vacacionesTitle = new Paragraph("Períodos de Vacaciones", subtitleFont);
                    vacacionesTitle.setSpacingBefore(20);
                    vacacionesTitle.setSpacingAfter(10);
                    document.add(vacacionesTitle);

                    PdfPTable vacacionesTable = new PdfPTable(2);
                    vacacionesTable.setWidthPercentage(100);
                    vacacionesTable.setWidths(new float[]{1, 1});

                    // Encabezados de la tabla de vacaciones
                    String[] vacacionesHeaders = {"Fecha Inicio", "Fecha Fin"};
                    for (String header : vacacionesHeaders) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        cell.setPadding(5);
                        vacacionesTable.addCell(cell);
                    }

                    // Datos de vacaciones
                    for (PeriodoVacaciones periodo : periodosRelevantes) {
                        LocalDate fechaDesde = LocalDate.parse(periodo.getFechaDesde());
                        LocalDate fechaHasta = LocalDate.parse(periodo.getFechaHasta());
                        
                        vacacionesTable.addCell(new PdfPCell(new Phrase(
                            fechaDesde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont)));
                        vacacionesTable.addCell(new PdfPCell(new Phrase(
                            fechaHasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont)));
                    }

                    document.add(vacacionesTable);
                    periodosAEliminar.addAll(periodosRelevantes);
                }
            }

            // Tabla de Salidas
            Paragraph salidasTitle = new Paragraph("Registro de Salidas", subtitleFont);
            salidasTitle.setSpacingBefore(20);
            salidasTitle.setSpacingAfter(10);
            document.add(salidasTitle);

            PdfPTable salidasTable = new PdfPTable(4);
            salidasTable.setWidthPercentage(100);
            salidasTable.setWidths(new float[]{3, 2, 2, 2});

            // Encabezados de la tabla de salidas
            String[] salidasHeaders = {"Fecha y Hora", "Tipo", "Estado", "Turno"};
            for (String header : salidasHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                salidasTable.addCell(cell);
            }

            // Datos de salidas
            for (RegistroFichaje registro : fichajesTrabajador) {
                if (registro.getTipo().equalsIgnoreCase("salida")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime fechaHora = LocalDateTime.parse(registro.getFechaHora(), formatter);
                    String turno = fechaHora.toLocalTime().isBefore(LocalTime.of(14, 0)) ? "Mañana" : "Tarde";
                    
                    salidasTable.addCell(new PdfPCell(new Phrase(fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont)));
                    salidasTable.addCell(new PdfPCell(new Phrase(registro.getTipo(), normalFont)));
                    salidasTable.addCell(new PdfPCell(new Phrase(registro.getEstado(), normalFont)));
                    salidasTable.addCell(new PdfPCell(new Phrase(turno, normalFont)));
                }
            }
            document.add(salidasTable);

            // Pie de página con fecha de generación
            Paragraph footer = new Paragraph();
            footer.setFont(normalFont);
            footer.add("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setSpacingBefore(20);
            document.add(footer);

            // Añadir derechos de autor
            Paragraph copyright = new Paragraph();
            copyright.setFont(footerFont);
            copyright.add("© " + LocalDate.now().getYear() + " Sistema de Control de Asistencia - Todos los derechos reservados\n");
            copyright.add("Este documento es confidencial y de uso exclusivo para la gestión interna de la empresa.\n");
            copyright.add("Cualquier reproducción o distribución no autorizada está prohibida.");
            copyright.setAlignment(Element.ALIGN_CENTER);
            copyright.setSpacingBefore(10);
            document.add(copyright);

            document.close();

            // Añadir al historial
            historialPDFs.add(new RegistroHistorial(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                fileName,
                filePath
            ));

            // Guardar el historial actualizado
            guardarHistorialPDFs();

            // Eliminar las vacaciones que se incluyeron en el PDF
            if (!periodosAEliminar.isEmpty()) {
                trabajador.getPeriodosVacaciones().removeAll(periodosAEliminar);
                
                // Actualizar el TextArea de vacaciones
                StringBuilder sb = new StringBuilder();
                for (PeriodoVacaciones periodo : trabajador.getPeriodosVacaciones()) {
                    sb.append("Desde: ").append(periodo.getFechaDesde())
                      .append(" - Hasta: ").append(periodo.getFechaHasta())
                      .append("\n");
                }
                vacacionesTextArea.setText(sb.toString());
                
                // Guardar los cambios en Firebase
                guardarCambios();
            }

            // Mostrar mensaje de éxito
            mostrarAlerta("Éxito", "PDF generado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al generar el PDF: " + e.getMessage());
        }
    }

    /**
     * Guarda el estado de carga actual.
     */
    private void guardarEstadoCarga() {
        try {
            if (trabajador == null) return;
            
            String estadoFile = String.format(ESTADO_CARGA_FILE_PATTERN, trabajador.getDni());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> estado = new HashMap<>();
            estado.put("fechaCambioHorario", fechaCambioHorario != null ? 
                fechaCambioHorario.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
            mapper.writeValue(new File(estadoFile), estado);
        } catch (Exception e) {
            System.err.println("Error al guardar el estado de carga: " + e.getMessage());
        }
    }

    /**
     * Carga el estado de carga guardado.
     */
    private void cargarEstadoCarga() {
        try {
            if (trabajador == null) return;
            
            String estadoFile = String.format(ESTADO_CARGA_FILE_PATTERN, trabajador.getDni());
            File file = new File(estadoFile);
            if (file.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> estado = mapper.readValue(file, new TypeReference<Map<String, String>>() {});
                String fechaStr = estado.get("fechaCambioHorario");
                if (fechaStr != null) {
                    fechaCambioHorario = LocalDateTime.parse(fechaStr, 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el estado de carga: " + e.getMessage());
            fechaCambioHorario = null;
        }
    }

    /**
     * Guarda los cambios realizados en el trabajador.
     */
    private void guardarCambios() {
        try {
            // Actualizar el trabajador en Firebase
            FirebaseRESTExample.actualizarHorarioTrabajador(trabajador);
            mostrarAlerta("Éxito", "Los cambios se han guardado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al guardar los cambios: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron guardar los cambios: " + e.getMessage());
        }
    }
}