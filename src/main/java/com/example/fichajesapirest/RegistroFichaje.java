package com.example.fichajesapirest;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class RegistroFichaje {
    private String idUsuario;
    private String nombre;
    private String apellidos;
    private String dni;
    private String fechaHora;
    private String tipo;
    private String horaEntradaManana;
    private String horaSalidaManana;
    private String horaEntradaTarde;
    private String horaSalidaTarde;
    private List<ImageView> iconos;
    private String advertencia;
    private String estado;
    private String horarioHistorico;

    // Constructor vacío
    public RegistroFichaje() {
        this.iconos = new ArrayList<>();
    }

    // Constructor con todos los parámetros (8)
    public RegistroFichaje(String nombre, String apellidos, String fechaHora, String tipo, 
                          String horaEntradaManana, String horaSalidaManana, 
                          String horaEntradaTarde, String horaSalidaTarde) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.horaEntradaManana = horaEntradaManana;
        this.horaSalidaManana = horaSalidaManana;
        this.horaEntradaTarde = horaEntradaTarde;
        this.horaSalidaTarde = horaSalidaTarde;
        this.iconos = new ArrayList<>();
        this.idUsuario = generarIdUsuario(nombre, apellidos);
    }

    // Constructor adicional para cuando no se tienen los horarios
    public RegistroFichaje(String nombre, String apellidos, String fechaHora, String tipo) {
        this(nombre, apellidos, fechaHora, tipo, "", "", "", "");
    }

    private String generarIdUsuario(String nombre, String apellidos) {
        String nombreNormalizado = nombre.toLowerCase().trim()
                .replaceAll("á", "a")
                .replaceAll("é", "e")
                .replaceAll("í", "i")
                .replaceAll("ó", "o")
                .replaceAll("ú", "u")
                .replaceAll("ñ", "n")
                .replaceAll("\\s+", "");

        String apellidosNormalizados = apellidos.toLowerCase().trim()
                .replaceAll("á", "a")
                .replaceAll("é", "e")
                .replaceAll("í", "i")
                .replaceAll("ó", "o")
                .replaceAll("ú", "u")
                .replaceAll("ñ", "n")
                .replaceAll("\\s+", "");

        return nombreNormalizado + apellidosNormalizados;
    }

    public String getIdUsuario() {
        if (idUsuario == null) {
            idUsuario = generarIdUsuario(nombre, apellidos);
        }
        System.out.println("ID de usuario generado: " + idUsuario); // Añadir log
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getHoraEntradaManana() {
        return horaEntradaManana;
    }

    public void setHoraEntradaManana(String horaEntradaManana) {
        this.horaEntradaManana = horaEntradaManana;
    }

    public String getHoraSalidaManana() {
        return horaSalidaManana;
    }

    public void setHoraSalidaManana(String horaSalidaManana) {
        this.horaSalidaManana = horaSalidaManana;
    }

    public String getHoraEntradaTarde() {
        return horaEntradaTarde;
    }

    public void setHoraEntradaTarde(String horaEntradaTarde) {
        this.horaEntradaTarde = horaEntradaTarde;
    }

    public String getHoraSalidaTarde() {
        return horaSalidaTarde;
    }

    public void setHoraSalidaTarde(String horaSalidaTarde) {
        this.horaSalidaTarde = horaSalidaTarde;
    }

    public String getFecha() {
        return fechaHora.substring(0, 10);
    }

    public List<ImageView> getIconos() {
        return iconos;
    }

    public void setIconos(List<ImageView> iconos) {
        this.iconos = iconos;
    }

    public String getAdvertencia() {
        return advertencia;
    }

    public void setAdvertencia(String advertencia) {
        this.advertencia = advertencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHorarioHistorico() {
        return horarioHistorico;
    }

    public void setHorarioHistorico(String horarioHistorico) {
        this.horarioHistorico = horarioHistorico;
    }

    public void addIcono(ImageView icono) {
        this.iconos.add(icono);
    }
}
