package AcccesoDatosCatalogo.repositorio;

import AcccesoDatosCatalogo.AlmacenamientoPedidos;
import EntidadesCatalogo.*;
import Utilidades.tecnicas.ArchivoHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPedidosTXT implements AlmacenamientoPedidos {

    private static final String ARCHIVO = "pedidos.txt";

    @Override
    public List<Pedido> cargarPedidos(List<Producto> productos, List<Cliente> clientes) {
        List<Pedido> pedidos = new ArrayList<>();
        List<String> lineas = ArchivoHelper.leerLineas(ARCHIVO);

        for (String linea : lineas) {
            try {
                Pedido p = parsearPedido(linea, productos, clientes);
                if (p != null) pedidos.add(p);
            } catch (Exception e) {
                System.err.println("Error recuperando pedido: " + e.getMessage());
            }
        }
        return pedidos;
    }

    @Override
    public void guardarPedidos(List<Pedido> pedidos) {
        List<String> lineas = new ArrayList<>();
        for (Pedido p : pedidos) {
            lineas.add(convertirPedidoAString(p));
        }
        ArchivoHelper.guardarLineas(ARCHIVO, lineas);
    }

    // --- PARSEO (LECTURA INTELIGENTE) ---
    private Pedido parsearPedido(String linea, List<Producto> productos, List<Cliente> clientes) {
        String[] datos = linea.split(";", -1);
        if (datos.length < 8) return null;

        // 1. Detección de Versión (Offset)
        // Offset 0: Formato Viejo
        // Offset 2: Snapshot V1 (Nombre, Cedula)
        // Offset 4: Snapshot V2 (Nombre, Cedula, Telefono, Direccion)
        int offset = calcularOffset(datos);

        int idPedido = Integer.parseInt(datos[0]);
        int idCliente = Integer.parseInt(datos[1]);
        
        // 2. Reconstrucción del Cliente (Con datos extendidos si offset=4)
        Cliente cliente = resolverCliente(idCliente, datos, offset, clientes);

        // 3. Datos Base (Desplazados por el offset)
        LocalDateTime fecha = LocalDateTime.parse(datos[2 + offset]);
        double totalDivisaCargado = Double.parseDouble(datos[3 + offset]);
        
        String estadoStr = datos[4 + offset]; 
        Pedido.EstadoPedido estado = Pedido.EstadoPedido.PAGADO; 
        try {
            estado = Pedido.EstadoPedido.valueOf(estadoStr);
        } catch (IllegalArgumentException e) {
            if (estadoStr.equalsIgnoreCase("true")) estado = Pedido.EstadoPedido.PAGADO;
            else if (estadoStr.equalsIgnoreCase("false")) estado = Pedido.EstadoPedido.ANULADO;
        }
        
        Pedido p = new Pedido(idPedido, cliente);
        p.setFecha(fecha);
        p.setEstado(estado);
        p.setTotalDivisa(BigDecimal.valueOf(totalDivisaCargado)); 
        
        // 4. Cargar Resto de Datos
        cargarDatosExtra(p, datos, offset);
        cargarDetallesRobusto(p, datos, offset, productos);
        recalcularTotalesInternos(p); 

        return p;
    }

    // --- SERIALIZACIÓN (GUARDADO V2) ---
    private String convertirPedidoAString(Pedido p) {
        StringBuilder sbDetalles = new StringBuilder();
        
        for (DetalleCompra det : p.getDetalles()) {
            String nombreSafe = sanitizar(det.getProducto().getNombre());
            sbDetalles.append(det.getProducto().getIdProducto()).append(":")
                      .append(det.getCantidad()).append(":")
                      .append(det.getProducto().getPrecio()).append(":") 
                      .append(nombreSafe) 
                      .append("|");
        }
        
        // Datos seguros del Cliente
        String nombreSnap = "Cliente Borrado";
        String cedulaSnap = "000";
        String telfSnap = "N/A";
        String dirSnap = "N/A";

        if (p.getCliente() != null) {
            nombreSnap = sanitizar(p.getCliente().getNombreCompleto());
            cedulaSnap = sanitizar(p.getCliente().getCedula());
            telfSnap = sanitizar(p.getCliente().getTelefono());
            dirSnap = sanitizar(p.getCliente().getDireccion());
        }

        // FORMATO NUEVO (OFFSET 4):
        // ID; ID_CLI; NOMBRE; CEDULA; TELF; DIR; FECHA...
        return p.getIdPedido() + ";" +
               p.getCliente().getIdCliente() + ";" +
               nombreSnap + ";" +   
               cedulaSnap + ";" +
               telfSnap + ";" +    // NUEVO CAMPO
               dirSnap + ";" +     // NUEVO CAMPO
               p.getFecha().toString() + ";" + 
               p.getTotalDivisa() + ";" +
               p.getEstado().name() + ";" + 
               p.getCodigoCuponAplicado() + ";" +  
               p.getMontoDescuentoCupon() + ";" +
               p.getTasaCambioSnapshot() + ";" +
               (p.getMetodoPago() == null ? "N/A" : p.getMetodoPago()) + ";" +
               (p.getReferenciaPago() == null ? "N/A" : p.getReferenciaPago()) + ";" +
               (p.getTotalDescuentos() == null ? BigDecimal.ZERO : p.getTotalDescuentos()) + ";" +
               sanitizar(p.getObservaciones() == null ? "Ninguna" : p.getObservaciones()) + ";" +
               sbDetalles.toString();
    }

    // --- LÓGICA INTELIGENTE ---

    private int calcularOffset(String[] datos) {
        // Intentamos ver dónde está la fecha para saber qué versión de archivo es
        
        // V0: ID; ID_CLI; FECHA (Indice 2)
        if (esFecha(datos[2])) return 0;
        
        // V1: ID; ID_CLI; NOM; CED; FECHA (Indice 4)
        if (datos.length > 4 && esFecha(datos[4])) return 2;
        
        // V2: ID; ID_CLI; NOM; CED; TELF; DIR; FECHA (Indice 6)
        if (datos.length > 6 && esFecha(datos[6])) return 4;
        
        return 0; // Por defecto
    }

    private boolean esFecha(String str) {
        try { LocalDateTime.parse(str); return true; } catch (Exception e) { return false; }
    }

    private Cliente resolverCliente(int id, String[] datos, int offset, List<Cliente> clientes) {
        // 1. Buscamos en la base de datos viva
        Cliente clienteVivo = clientes.stream()
                .filter(c -> c.getIdCliente() == id)
                .findFirst()
                .orElse(null);
                
        if (clienteVivo != null) return clienteVivo;

        // 2. Si no existe (fue borrado), reconstruimos del Snapshot
        String nombre = "Cliente Histórico";
        String cedula = "000";
        String telefono = "N/A";
        String direccion = "N/A";

        if (offset >= 2) {
            nombre = datos[2];
            cedula = datos[3];
        }
        if (offset >= 4) { // Solo versión nueva tiene estos datos
            telefono = datos[4];
            direccion = datos[5];
        }

        return new Cliente(id, cedula, nombre, telefono, "", direccion, 0);
    }

    private void cargarDatosExtra(Pedido p, String[] datos, int offset) {
        // Todo se desplaza según el offset, así que la lógica relativa se mantiene
        p.setCodigoCuponAplicado(datos[5 + offset]);
        try { p.setMontoDescuentoCupon(new BigDecimal(datos[6 + offset])); } catch(Exception e) {}
        
        if (datos.length >= (8 + offset)) {
            try { p.setTasaCambioSnapshot(Double.parseDouble(datos[7 + offset])); } catch(Exception e){}
            if (datos.length > 8+offset) p.setMetodoPago(datos[8 + offset]);
            if (datos.length > 9+offset) p.setReferenciaPago(datos[9 + offset]);
            if (datos.length > 10+offset) {
                try { p.setTotalDescuentos(new BigDecimal(datos[10 + offset])); } catch(Exception e) {}
            }
            if (datos.length > 11+offset) {
                p.setObservaciones(datos[11 + offset].equals("Ninguna") ? "" : datos[11 + offset]);
            }
        }
    }

    private void cargarDetallesRobusto(Pedido p, String[] datos, int offset, List<Producto> productos) {
        int idxDetalles = datos.length - 1; 
        if (idxDetalles < 7) return; 
        
        String[] items = datos[idxDetalles].split("\\|");
        for (String itemStr : items) {
            if(itemStr.trim().isEmpty()) continue;
            try {
                String[] partes = itemStr.split(":");
                int idProd = Integer.parseInt(partes[0]);
                int cant = Integer.parseInt(partes[1]);
                double precioHist = (partes.length >= 3) ? Double.parseDouble(partes[2]) : 0;
                String nomHist = (partes.length >= 4) ? partes[3] : "Producto Histórico";

                Producto prodReal = productos.stream().filter(pr -> pr.getIdProducto() == idProd).findFirst().orElse(null);

                if (prodReal != null) {
                    Producto snap = clonarProducto(prodReal);
                    if (precioHist > 0) snap.setPrecio(precioHist);
                    snap.setStock(cant); // Truco para pasar validación
                    p.getDetalles().add(new DetalleCompra(snap, cant));
                } else {
                    Producto fantasma = new Producto();
                    fantasma.setIdProducto(idProd);
                    fantasma.setNombre(nomHist + " (Borrado)");
                    fantasma.setPrecio(precioHist);
                    fantasma.setCategoria("Histórico");
                    fantasma.setRutaImagen("");
                    fantasma.setStock(cant); // Truco
                    p.getDetalles().add(new DetalleCompra(fantasma, cant));
                }
            } catch(Exception e) {}
        }
    }

    private void recalcularTotalesInternos(Pedido p) {
        BigDecimal subTotalCalc = BigDecimal.ZERO;
        for (DetalleCompra d : p.getDetalles()) {
            BigDecimal precio = BigDecimal.valueOf(d.getProducto().getPrecio());
            BigDecimal cant = BigDecimal.valueOf(d.getCantidad());
            subTotalCalc = subTotalCalc.add(precio.multiply(cant));
        }
        p.setSubTotal(subTotalCalc);
        
        if (p.getTotalDivisa() != null && p.getTasaCambioSnapshot() > 0) {
            BigDecimal totalBs = p.getTotalDivisa().multiply(BigDecimal.valueOf(p.getTasaCambioSnapshot())).setScale(2, RoundingMode.HALF_UP);
            p.setTotalLocal(totalBs);
        }
    }
    
    private Producto clonarProducto(Producto original) {
        Producto copia = new Producto();
        copia.setIdProducto(original.getIdProducto());
        copia.setSku(original.getSku());
        copia.setNombre(original.getNombre());
        copia.setCategoria(original.getCategoria());
        copia.setRutaImagen(original.getRutaImagen());
        return copia;
    }

    private String sanitizar(String s) {
        if (s == null) return "";
        return s.replace(";", ",").replace(":", " ").replace("|", " ").replace("\n", " ").trim();
    }
}