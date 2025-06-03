package com.example.fichajesapirest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Servicio para manejar las horas extras de los trabajadores.
 */
public class HorasExtraService {
    
    /**
     * Calcula las horas extras de un trabajador para un mes y año específicos.
     * Las horas extras se distribuyen proporcionalmente a lo largo del mes.
     * @param trabajador El trabajador
     * @param mes El mes (1-12)
     * @param año El año
     */
    public void calcularHorasExtrasTrabajador(Trabajador trabajador, int mes, int año) {
        // Obtener todos los fichajes del trabajador
        List<RegistroFichaje> fichajes = new FirebaseRESTExample().obtenerFichajesTrabajador(trabajador.getDni());
        
        // Filtrar fichajes del mes y año especificados
        List<RegistroFichaje> fichajesMes = new ArrayList<>();
        for (RegistroFichaje fichaje : fichajes) {
            try {
                LocalDateTime fechaHora = LocalDateTime.parse(fichaje.getFechaHora(), 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                if (fechaHora.getMonthValue() == mes && fechaHora.getYear() == año) {
                    fichajesMes.add(fichaje);
                }
            } catch (Exception e) {
                System.err.println("Error al procesar fecha: " + fichaje.getFechaHora());
            }
        }

        // Calcular el total de horas extras del mes
        double totalHorasExtras = 0;
        for (RegistroFichaje fichaje : fichajesMes) {
            if ("salida".equals(fichaje.getTipo())) {
                try {
                    LocalDateTime fechaHora = LocalDateTime.parse(fichaje.getFechaHora(), 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    
                    // Determinar si es turno de mañana o tarde
                    boolean esManana = fechaHora.toLocalTime().isBefore(java.time.LocalTime.of(14, 0));
                    
                    // Obtener hora de salida esperada
                    String horaSalidaEsperada = esManana ? 
                        trabajador.getHoraSalidaManana() : 
                        trabajador.getHoraSalidaTarde();
                    
                    if (horaSalidaEsperada != null && !horaSalidaEsperada.isEmpty()) {
                        java.time.LocalTime horaEsperada = java.time.LocalTime.parse(horaSalidaEsperada);
                        java.time.LocalTime horaReal = fechaHora.toLocalTime();
                        
                        // Calcular diferencia en horas
                        double horasExtra = java.time.Duration.between(horaEsperada, horaReal).toMinutes() / 60.0;
                        if (horasExtra > 0) {
                            totalHorasExtras += horasExtra;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al calcular horas extra: " + e.getMessage());
                }
            }
        }

        // Si hay horas extras, distribuirlas proporcionalmente
        if (totalHorasExtras > 0) {
            // Obtener los días laborables del mes (excluyendo fines de semana)
            List<LocalDate> diasLaborables = new ArrayList<>();
            LocalDate primerDia = LocalDate.of(año, mes, 1);
            LocalDate ultimoDia = primerDia.plusMonths(1).minusDays(1);
            
            for (LocalDate fecha = primerDia; !fecha.isAfter(ultimoDia); fecha = fecha.plusDays(1)) {
                if (fecha.getDayOfWeek().getValue() <= 5) { // 1-5 son días laborables (L-V)
                    diasLaborables.add(fecha);
                }
            }

            // Calcular horas extras por día
            double horasPorDia = totalHorasExtras / diasLaborables.size();

            // Crear registros de horas extras para cada día laborable
            for (LocalDate dia : diasLaborables) {
                HorasExtra registroHorasExtra = new HorasExtra(
                    dia,
                    horasPorDia,
                    "Pendiente",
                    LocalDateTime.now()
                );
                
                // Guardar en Firebase (implementar según necesidad)
                // FirebaseRESTExample.guardarHorasExtra(trabajador.getDni(), registroHorasExtra);
            }
        }
    }

    /**
     * Obtiene las horas extras de un trabajador.
     * @param trabajador El trabajador
     * @return Lista de horas extras
     */
    public List<HorasExtra> getHorasExtrasTrabajador(Trabajador trabajador) {
        // Implementar la obtención de horas extras desde Firebase
        // Por ahora retornamos una lista vacía
        return new ArrayList<>();
    }
} 