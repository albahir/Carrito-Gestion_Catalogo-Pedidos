package AcccesoDatosCatalogo.repositorio;

import EntidadesCatalogo.Producto;
import Utilidades.ArchivoHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioProductoTXT {

    private static final String ARCHIVO = "productos.txt";

    public List<Producto> cargarProductos() {
        List<Producto> lista = new ArrayList<>();
        List<String> lineas = ArchivoHelper.leerLineas(ARCHIVO);

        for (String linea : lineas) {
            try {
                // Usamos split con límite -1 para mantener campos vacíos
                String[] datos = linea.split(";", -1);
                if (datos.length < 5) continue;

                // Parseo delegado a método privado para limpieza (Refactorización B)
                Producto p = parsearProducto(datos);
                if (p != null) lista.add(p);
                
            } catch (Exception e) {
                System.err.println("Error cargando producto: " + e.getMessage());
            }
        }
        return lista;
    }

    public void guardarProductos(List<Producto> productos) {
        List<String> lineas = new ArrayList<>();
        for (Producto p : productos) {
            lineas.add(convertirProductoAString(p));
        }
        ArchivoHelper.guardarLineas(ARCHIVO, lineas);
    }

    // --- Métodos Privados de Parseo (Limpieza) ---

    private Producto parsearProducto(String[] datos) {
        try {
            int id = Integer.parseInt(datos[0]);
            String sku = (datos[1].equals("null") || datos[1].isEmpty()) ? "PRO-" + id : datos[1];
            String nombre = datos[2];
            String desc = datos[3].replace("<br>", "\n");
            String cat = datos[4];
            double precio = Double.parseDouble(datos[5]);
            int stock = Integer.parseInt(datos[6]);
            
            LocalDate fecha = parsearFechaSegura(datos[7]);
            boolean estado = Boolean.parseBoolean(datos[8]);
            String img = datos.length > 9 ? datos[9] : "";

            Producto p = new Producto(id, sku, nombre, desc, cat, precio, stock, fecha, estado, img);

            // Cargar Extras (Ofertas y Volumen)
            cargarDatosExtendidos(p, datos);
            return p;
        } catch (Exception e) {
            return null; // O lanzar excepción según prefieras
        }
    }

    private void cargarDatosExtendidos(Producto p, String[] datos) {
        if (datos.length > 10) p.setEnOfertaFlash(Boolean.parseBoolean(datos[10]));
        if (datos.length > 11) try { p.setPrecioOferta(Double.parseDouble(datos[11])); } catch(Exception e){}
        
        if (datos.length > 14) {
            p.setTieneDescuentoVolumen(Boolean.parseBoolean(datos[12]));
            p.setCantidadParaDescuento(Integer.parseInt(datos[13]));
            p.setPorcentajeDescuentoVolumen(Double.parseDouble(datos[14]));
        }
        
        if (datos.length > 15 && !datos[15].equals("null") && !datos[15].isEmpty()) {
            try { p.setFechaFinOferta(LocalDateTime.parse(datos[15])); } catch(Exception e){}
        }
    }

    private LocalDate parsearFechaSegura(String fechaStr) {
        try { return LocalDate.parse(fechaStr); } catch (Exception e) { return LocalDate.now(); }
    }
    
  private String convertirProductoAString(Producto p) {
        String descSafe = (p.getDescripcion() == null) ? "" : p.getDescripcion().replace("\n", "<br>");
        String imgSafe = (p.getRutaImagen() == null) ? "" : p.getRutaImagen();
        String skuSafe = (p.getSku() == null) ? "PRO-" + p.getIdProducto() : p.getSku();

        StringBuilder sb = new StringBuilder();
        sb.append(p.getIdProducto()).append(";");
        sb.append(skuSafe).append(";");
        sb.append(p.getNombre()).append(";");
        sb.append(descSafe).append(";");
        sb.append(p.getCategoria()).append(";");
        sb.append(p.getPrecio()).append(";");
        sb.append(p.getStock()).append(";");
        sb.append(p.getFechaIngreso()).append(";");
        sb.append(p.isEstado()).append(";");
        sb.append(imgSafe).append(";");
        sb.append(p.isEnOfertaFlashConfigurada()).append(";");
        sb.append(p.getPrecioOferta()).append(";");
        sb.append(p.isTieneDescuentoVolumen()).append(";");
        sb.append(p.getCantidadParaDescuento()).append(";");
        sb.append(p.getPorcentajeDescuentoVolumen()).append(";");
        
        String fechaOfertaStr = (p.getFechaFinOferta() == null) ? "null" : p.getFechaFinOferta().toString();
        sb.append(fechaOfertaStr);
        
        return sb.toString();
    }
}