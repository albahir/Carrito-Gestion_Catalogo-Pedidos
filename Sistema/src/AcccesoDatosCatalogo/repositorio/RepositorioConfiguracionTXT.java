package AcccesoDatosCatalogo.repositorio;

import EntidadesCatalogo.Configuracion;
import Utilidades.tecnicas.ArchivoHelper;
import java.util.ArrayList;
import java.util.List;

public class RepositorioConfiguracionTXT {

    private static final String ARCHIVO = "config.txt";

    public Configuracion cargarConfiguracion() {
        List<String> lineas = ArchivoHelper.leerLineas(ARCHIVO);
        
        // Si el archivo está vacío o no existe, devolvemos la config por defecto
        if (lineas == null || lineas.isEmpty()) {
            return new Configuracion(); 
        }

        try {
            String linea = lineas.get(0); // Solo nos interesa la primera línea
            String[] datos = linea.split(";");
            
            if (datos.length >= 4) {
                double iva = Double.parseDouble(datos[0]);
                double tasa = Double.parseDouble(datos[1]);
                double umbral = Double.parseDouble(datos[2]);
                double porcGlob = Double.parseDouble(datos[3]);
                
                Configuracion c = new Configuracion(iva, tasa);
                c.setUmbralMontoDescuento(umbral);
                c.setPorcentajeDescuentoGlobal(porcGlob);
                return c;
            }
        } catch (Exception e) {
            System.err.println("Error parseando configuración, usando valores por defecto: " + e.getMessage());
        }
        
        return new Configuracion(); // Retorno seguro si falla algo
    }

    public void guardarConfiguracion(Configuracion config) {
        List<String> lineas = new ArrayList<>();
        String linea = config.getIvaPorcentaje() + ";" + 
                       config.getTasaCambio() + ";" + 
                       config.getUmbralMontoDescuento() + ";" + 
                       config.getPorcentajeDescuentoGlobal();
        lineas.add(linea);
        ArchivoHelper.guardarLineas(ARCHIVO, lineas);
    }
}