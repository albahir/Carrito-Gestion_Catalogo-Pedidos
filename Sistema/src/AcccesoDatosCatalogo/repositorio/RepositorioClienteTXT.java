package AcccesoDatosCatalogo.repositorio;

import EntidadesCatalogo.Cliente;
import Utilidades.ArchivoHelper;
import java.util.ArrayList;
import java.util.List;

public class RepositorioClienteTXT {

    private static final String ARCHIVO = "clientes.txt";

    public List<Cliente> cargarClientes() {
        List<Cliente> lista = new ArrayList<>();
        List<String> lineas = ArchivoHelper.leerLineas(ARCHIVO);

        for (String linea : lineas) {
            try {
                String[] datos = linea.split(";");
                if (datos.length >= 6) {
                    Cliente c = new Cliente();
                    c.setIdCliente(Integer.parseInt(datos[0]));
                    c.setCedula(datos[1]);
                    c.setNombreCompleto(datos[2]);
                    c.setTelefono(datos[3]);
                    c.setCorreo(datos[4]);
                    c.setDireccion(datos[5]);
                    
                    if (datos.length > 6) {
                        c.setComprasRealizadas(Integer.parseInt(datos[6]));
                    }
                    lista.add(c);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parseando cliente: " + linea);
            }
        }
        return lista;
    }

    public void guardarClientes(List<Cliente> clientes) {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : clientes) {
            String linea = c.getIdCliente() + ";" + 
                           c.getCedula() + ";" + 
                           c.getNombreCompleto() + ";" + 
                           c.getTelefono() + ";" + 
                           c.getCorreo() + ";" + 
                           c.getDireccion() + ";" +
                           c.getComprasRealizadas();
            lineas.add(linea);
        }
        ArchivoHelper.guardarLineas(ARCHIVO, lineas);
    }
}