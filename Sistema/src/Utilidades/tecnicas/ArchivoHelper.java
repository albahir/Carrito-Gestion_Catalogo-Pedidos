package Utilidades.tecnicas;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoHelper {
    
    // Método genérico para leer cualquier archivo de texto
    public static List<String> leerLineas(String rutaArchivo) {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(rutaArchivo);
        
        if (!archivo.exists()) return lineas;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) { // Filtro básico de líneas vacías
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo " + rutaArchivo + ": " + e.getMessage());
        }
        return lineas;
    }

    // Método genérico para guardar (Sobreescribir)
    public static void guardarLineas(String rutaArchivo, List<String> lineas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error escribiendo archivo " + rutaArchivo + ": " + e.getMessage());
        }
    }
}