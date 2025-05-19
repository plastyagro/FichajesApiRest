package com.example.fichajesapirest;

import java.time.LocalDate;

public class Festivo {
    private LocalDate fecha;
    private String nombre;
    private String tipo;

    public Festivo(LocalDate fecha, String nombre, String tipo) {
        this.fecha = fecha;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
} 