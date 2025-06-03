package com.example.fichajesapirest;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar los días festivos.
 * Proporciona métodos para obtener y gestionar los días festivos de un año específico.
 */
public class FestivosService {
    // URL base para la API de festivos
    private static final String API_URL = "https://date.nager.at/api/v3/PublicHolidays/";
    private static final String COUNTRY_CODE = "ES";

    /**
     * Obtiene la lista de festivos para un año específico.
     * @param año El año para el cual se quieren obtener los festivos
     * @return Lista de objetos Festivo con la información de los días festivos
     * @throws RuntimeException Si ocurre un error al obtener los festivos
     */
    public static List<Festivo> obtenerFestivos(int año) {
        List<Festivo> festivos = new ArrayList<>();
        try {
            
            // Obtener festivos de la API
            URL url = new URL(API_URL + año + "/" + COUNTRY_CODE);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray jsonArray = new JSONArray(response.toString());
                
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject festivo = jsonArray.getJSONObject(i);
                    
                    if (!festivo.has("date") || !festivo.has("localName")) {
                        continue;
                    }
                    
                    String fechaStr = festivo.getString("date");
                    String nombre = festivo.getString("localName");
                    
                    // Solo incluir festivos nacionales y regionales de Extremadura
                    boolean esFestivoValido = false;
                    String tipo = "Nacional";
                    
                    if (festivo.has("counties") && !festivo.isNull("counties")) {
                        try {
                            JSONArray counties = festivo.getJSONArray("counties");
                            if (counties.toString().contains("ES-EX")) {
                                tipo = "Regional";
                                esFestivoValido = true;
                            }
                        } catch (Exception e) {
                            System.err.println("Error al procesar counties para " + nombre + ": " + e.getMessage());
                        }
                    } else {
                        // Si no tiene counties, es un festivo nacional
                        esFestivoValido = true;
                    }
                    
                    if (esFestivoValido) {
                        try {
                            LocalDate fecha = LocalDate.parse(fechaStr, formatter);
                            festivos.add(new Festivo(fecha, nombre, tipo));
                        } catch (Exception e) {
                            System.err.println("Error al procesar fecha del festivo " + nombre + ": " + e.getMessage());
                        }
                    }
                }
            } else {
                System.err.println("Error al obtener festivos de la API. Código de respuesta: " + responseCode);
            }

            // Añadir festivos locales específicos de Mérida
            
            // Festivos locales específicos de Mérida (estos son fijos cada año)
            festivos.add(new Festivo(LocalDate.of(año, 5, 22), "Emerita Lvdica", "Local"));
            festivos.add(new Festivo(LocalDate.of(año, 9, 10), "Santa Eulalia", "Local"));
            festivos.add(new Festivo(LocalDate.of(año, 12, 13), "Santa Lucía", "Local"));

            // Ordenar los festivos por fecha
            festivos.sort((f1, f2) -> f1.getFecha().compareTo(f2.getFecha()));


        } catch (Exception e) {
            System.err.println("Error al obtener festivos: " + e.getMessage());
            e.printStackTrace();
        }
        return festivos;
    }

    /**
     * Verifica si una fecha específica es festiva.
     * @param fecha La fecha a verificar
     * @return true si la fecha es festiva, false en caso contrario
     */
    public static boolean esFestivo(LocalDate fecha) {
        try {
            // Obtener los festivos del año de la fecha
            List<Festivo> festivos = obtenerFestivos(fecha.getYear());
            
            // Verificar si la fecha está en la lista de festivos
            return festivos.stream()
                    .anyMatch(festivo -> festivo.getFecha().equals(fecha));
        } catch (Exception e) {
            System.err.println("Error al verificar si es festivo: " + e.getMessage());
            return false;
        }
    }
} 