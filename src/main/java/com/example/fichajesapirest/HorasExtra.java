package com.example.fichajesapirest;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase que representa las horas extras de un trabajador.
 */
public class HorasExtra {
    private LocalDate fecha;
    private double horasAcumuladas;
    private String estado;
    private LocalDateTime fechaCalculo;

    public HorasExtra() {
    }

    public HorasExtra(LocalDate fecha, double horasAcumuladas, String estado, LocalDateTime fechaCalculo) {
        this.fecha = fecha;
        this.horasAcumuladas = horasAcumuladas;
        this.estado = estado;
        this.fechaCalculo = fechaCalculo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getHorasAcumuladas() {
        return horasAcumuladas;
    }

    public void setHorasAcumuladas(double horasAcumuladas) {
        this.horasAcumuladas = horasAcumuladas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }
} 