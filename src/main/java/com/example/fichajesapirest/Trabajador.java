package com.example.fichajesapirest;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Trabajador {
    private String nombre;
    private String apellidos;
    private String dni;
    @SerializedName("hora_entrada_manana")
    private String horaEntradaManana;
    @SerializedName("hora_salida_manana")
    private String horaSalidaManana;
    @SerializedName("hora_entrada_tarde")
    private String horaEntradaTarde;
    @SerializedName("hora_salida_tarde")
    private String horaSalidaTarde;
    @SerializedName("vacaciones_desde")
    private String vacacionesDesde;
    @SerializedName("vacaciones_hasta")
    private String vacacionesHasta;
    private String correo;
    private String id;
    private List<RegistroFichaje> fichajes;
    private List<PeriodoVacaciones> periodosVacaciones;

    // Constructor vacío
    public Trabajador() {
        this.fichajes = new ArrayList<>();
        this.periodosVacaciones = new ArrayList<>();
    }

    // Constructor con parámetros básicos
    public Trabajador(String nombre, String apellidos, String dni) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.fichajes = new ArrayList<>();
        this.periodosVacaciones = new ArrayList<>();
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDni() {
        return dni;
    }

    public String getHoraEntradaManana() {
        return horaEntradaManana;
    }

    public String getHoraSalidaManana() {
        return horaSalidaManana;
    }

    public String getHoraEntradaTarde() {
        return horaEntradaTarde;
    }

    public String getHoraSalidaTarde() {
        return horaSalidaTarde;
    }

    public String getVacacionesDesde() {
        return periodosVacaciones != null && !periodosVacaciones.isEmpty() ? 
            periodosVacaciones.get(0).getFechaDesde() : "";
    }

    public String getVacacionesHasta() {
        return periodosVacaciones != null && !periodosVacaciones.isEmpty() ? 
            periodosVacaciones.get(0).getFechaHasta() : "";
    }

    public String getCorreo() {
        return correo != null && !correo.isEmpty() ? correo : id;
    }

    public String getId() {
        return id;
    }

    public List<RegistroFichaje> getFichajes() {
        return fichajes;
    }

    public List<PeriodoVacaciones> getPeriodosVacaciones() {
        return periodosVacaciones;
    }

    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setHoraEntradaManana(String horaEntradaManana) {
        this.horaEntradaManana = horaEntradaManana;
    }

    public void setHoraSalidaManana(String horaSalidaManana) {
        this.horaSalidaManana = horaSalidaManana;
    }

    public void setHoraEntradaTarde(String horaEntradaTarde) {
        this.horaEntradaTarde = horaEntradaTarde;
    }

    public void setHoraSalidaTarde(String horaSalidaTarde) {
        this.horaSalidaTarde = horaSalidaTarde;
    }

    public void setVacacionesDesde(String vacacionesDesde) {
        this.vacacionesDesde = vacacionesDesde;
    }

    public void setVacacionesHasta(String vacacionesHasta) {
        this.vacacionesHasta = vacacionesHasta;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFichajes(List<RegistroFichaje> fichajes) {
        this.fichajes = fichajes;
    }

    public void setPeriodosVacaciones(List<PeriodoVacaciones> periodosVacaciones) {
        this.periodosVacaciones = periodosVacaciones;
    }

    public void addFichaje(RegistroFichaje fichaje) {
        this.fichajes.add(fichaje);
    }

    public void addPeriodoVacaciones(PeriodoVacaciones periodo) {
        if (this.periodosVacaciones == null) {
            this.periodosVacaciones = new ArrayList<>();
        }
        this.periodosVacaciones.add(periodo);
    }
}

