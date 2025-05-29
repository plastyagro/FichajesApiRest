package com.example.fichajesapirest;

public class RegistroHistorial {
    private String fechaGeneracion;
    private String nombreArchivo;
    private String rutaArchivo;

    // Constructor vacío necesario para la serialización JSON
    public RegistroHistorial() {
    }

    public RegistroHistorial(String fechaGeneracion, String nombreArchivo, String rutaArchivo) {
        this.fechaGeneracion = fechaGeneracion;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
} 