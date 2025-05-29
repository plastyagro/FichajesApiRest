package com.example.fichajesapirest;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

public class FirebaseRESTExample {

    private static final String FIREBASE_URL = "https://firestore.googleapis.com/v1/projects/fichajesplastyagro/databases/(default)/documents";
    private static final String SERVICE_ACCOUNT_JSON_PATH = "firebase-service-account.json";

    // Metodo para obtener el token de acceso
    static String getAccessTokenFromServiceAccount() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(SERVICE_ACCOUNT_JSON_PATH)) {
            if (is == null) {
                throw new FileNotFoundException("No se pudo encontrar el archivo de credenciales: " + SERVICE_ACCOUNT_JSON_PATH);
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            String jsonString = content.toString();
            JSONObject serviceAccountJson = new JSONObject(jsonString);

            String clientEmail = serviceAccountJson.getString("client_email");
            String privateKeyPem = serviceAccountJson.getString("private_key");

            String jwt = createJwt(clientEmail, privateKeyPem);
            return requestAccessToken(jwt);
        } catch (IOException e) {
            throw new Exception("Error al leer el archivo de credenciales: " + e.getMessage());
        }
    }

    private static String createJwt(String clientEmail, String privateKeyPem) throws Exception {
        long nowSeconds = System.currentTimeMillis() / 1000;
        long expSeconds = nowSeconds + 3600;

        JSONObject headerJson = new JSONObject();
        headerJson.put("alg", "RS256");
        headerJson.put("typ", "JWT");

        JSONObject payloadJson = new JSONObject();
        payloadJson.put("iss", clientEmail);
        payloadJson.put("scope", "https://www.googleapis.com/auth/datastore");
        payloadJson.put("aud", "https://oauth2.googleapis.com/token");
        payloadJson.put("iat", nowSeconds);
        payloadJson.put("exp", expSeconds);

        String encodedHeader = base64UrlEncode(headerJson.toString().getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payloadJson.toString().getBytes(StandardCharsets.UTF_8));

        String unsignedToken = encodedHeader + "." + encodedPayload;

        PrivateKey privateKey = loadPrivateKey(privateKeyPem);
        String signature = signJwt(unsignedToken, privateKey);

        return unsignedToken + "." + signature;
    }

    private static PrivateKey loadPrivateKey(String pem) throws Exception {
        pem = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // quitar saltos de linea y espacios en blanco

        byte[] keyBytes = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private static String signJwt(String unsignedToken, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(unsignedToken.getBytes(StandardCharsets.UTF_8));
        byte[] signed = signature.sign();
        return base64UrlEncode(signed);
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static String requestAccessToken(String jwt) throws IOException {
        String url = "https://oauth2.googleapis.com/token";
        String requestBody = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + jwt;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = con.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        InputStream is = (responseCode == 200) ? con.getInputStream() : con.getErrorStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (responseCode == 200) {
            JSONObject responseJson = new JSONObject(response.toString());
            return responseJson.getString("access_token");
        } else {
            System.err.println("Error al obtener el token: " + response.toString());
            return null;
        }
    }

    static String fetchFirestoreRawJson(String accessToken) throws IOException {
        String url = FIREBASE_URL + "/registros_fichajes";

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setRequestProperty("Content-Type", "application/json");

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Imprime la respuesta JSON para ver su estructura
        System.out.println("Raw Firestore JSON Response: " + response.toString());

        return response.toString();
    }

    public static void actualizarHorarioTrabajador(Trabajador trabajador) throws Exception {
        String accessToken = getAccessTokenFromServiceAccount();
        if (accessToken == null) {
            throw new Exception("No se pudo obtener el token de acceso");
        }

        // Primero obtener el documento actual
        String url = FIREBASE_URL + "/usuarios/" + trabajador.getCorreo();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setRequestProperty("Content-Type", "application/json");

        // Leer la respuesta actual
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Parsear el documento actual
        JSONObject currentDoc = new JSONObject(response.toString());
        JSONObject currentFields = currentDoc.getJSONObject("fields");

        // Actualizar solo los campos de horario
        currentFields.put("horaEntradaManana", new JSONObject().put("stringValue", trabajador.getHoraEntradaManana()));
        currentFields.put("horaSalidaManana", new JSONObject().put("stringValue", trabajador.getHoraSalidaManana()));
        currentFields.put("horaEntradaTarde", new JSONObject().put("stringValue", trabajador.getHoraEntradaTarde()));
        currentFields.put("horaSalidaTarde", new JSONObject().put("stringValue", trabajador.getHoraSalidaTarde()));

        // Crear el nuevo documento con todos los campos
        JSONObject document = new JSONObject();
        document.put("fields", currentFields);

        // Actualizar el documento
        con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        con.setDoOutput(true);

        // Enviar los datos
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = document.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Verificar la respuesta
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            throw new Exception("Error al actualizar el horario: " + response.toString());
        }
    }

    public static List<RegistroFichaje> obtenerFichajesTrabajador(String dni) {
        List<RegistroFichaje> fichajes = new ArrayList<>();
        
        try {
            String accessToken = getAccessTokenFromServiceAccount();
            if (accessToken == null) {
                System.err.println("Error: No se pudo obtener el token de acceso");
                return fichajes;
            }

            String url = FIREBASE_URL + "/registros_fichajes?pageSize=1000";
            
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.has("documents")) {
                        JSONArray documents = jsonResponse.getJSONArray("documents");
                            
                        for (int i = 0; i < documents.length(); i++) {
                            JSONObject doc = documents.getJSONObject(i);
                            
                            if (doc.has("fields")) {
                                JSONObject fields = doc.getJSONObject("fields");
                                
                                if (fields.has("dni_usuario")) {
                                    String dniUsuario = fields.getJSONObject("dni_usuario").getString("stringValue");
                                    
                                    if (dniUsuario.equals(dni)) {
                                        RegistroFichaje fichaje = new RegistroFichaje();
                                        fichaje.setDni(dni);
                                        fichaje.setFechaHora(fields.getJSONObject("fecha_hora").getString("stringValue"));
                                        fichaje.setTipo(fields.getJSONObject("tipo").getString("stringValue"));
                                        
                                        // Obtener el estado si existe
                                        if (fields.has("estado")) {
                                            fichaje.setEstado(fields.getJSONObject("estado").getString("stringValue"));
                                        }
                                        
                                        // Obtener el horario histórico si existe
                                        if (fields.has("horario_historico")) {
                                            fichaje.setHorarioHistorico(fields.getJSONObject("horario_historico").getString("stringValue"));
                                        }
                                        
                                        fichajes.add(fichaje);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.err.println("Error al obtener fichajes. Código de respuesta: " + responseCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    System.err.println("Mensaje de error: " + errorResponse.toString());
                }
            }
            
            connection.disconnect();
        } catch (Exception e) {
            System.err.println("Error al obtener fichajes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fichajes;
    }

    public List<Trabajador> obtenerTrabajadores() {
        List<Trabajador> trabajadores = new ArrayList<>();
        try {
            String accessToken = getAccessTokenFromServiceAccount();
            if (accessToken == null) {
                System.err.println("Error: No se pudo obtener el token de acceso");
                throw new Exception("No se pudo obtener el token de acceso");
            }

            String url = FIREBASE_URL + "/usuarios";
            
            HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setRequestProperty("Content-Type", "application/json");

            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    JSONObject json = new JSONObject(response.toString());
                    if (json.has("documents")) {
                        JSONArray documentos = json.getJSONArray("documents");
                        
                        for (int i = 0; i < documentos.length(); i++) {
                            JSONObject doc = documentos.getJSONObject(i);
                            if (doc.has("fields")) {
                                JSONObject fields = doc.getJSONObject("fields");
                                
                                Trabajador trabajador = new Trabajador();
                                trabajador.setDni(fields.has("dni") ? 
                                    fields.getJSONObject("dni").getString("stringValue") : "");
                                trabajador.setNombre(fields.has("nombre") ? 
                                    fields.getJSONObject("nombre").getString("stringValue") : "");
                                trabajador.setApellidos(fields.has("apellidos") ? 
                                    fields.getJSONObject("apellidos").getString("stringValue") : "");
                                trabajador.setHoraEntradaManana(fields.has("horaEntradaManana") ? 
                                    fields.getJSONObject("horaEntradaManana").getString("stringValue") : "08:00");
                                trabajador.setHoraSalidaManana(fields.has("horaSalidaManana") ? 
                                    fields.getJSONObject("horaSalidaManana").getString("stringValue") : "14:00");
                                trabajador.setHoraEntradaTarde(fields.has("horaEntradaTarde") ? 
                                    fields.getJSONObject("horaEntradaTarde").getString("stringValue") : "15:30");
                                trabajador.setHoraSalidaTarde(fields.has("horaSalidaTarde") ? 
                                    fields.getJSONObject("horaSalidaTarde").getString("stringValue") : "17:00");
                                trabajador.setVacacionesDesde(fields.has("vacaciones_desde") ? 
                                    fields.getJSONObject("vacaciones_desde").getString("stringValue") : "");
                                trabajador.setVacacionesHasta(fields.has("vacaciones_hasta") ? 
                                    fields.getJSONObject("vacaciones_hasta").getString("stringValue") : "");
                                
                                // Obtener el ID del documento
                                String documentId = doc.getString("name").split("/")[doc.getString("name").split("/").length - 1];
                                trabajador.setId(documentId);
                                trabajador.setCorreo(documentId); // Establecer el ID como correo
                                
                                trabajadores.add(trabajador);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener trabajadores: " + e.getMessage());
            e.printStackTrace();
        }
        return trabajadores;
    }
}
