package Servicios;

import AcccesoDatosCatalogo.AlmacenamientoPedidos;
import AcccesoDatosCatalogo.Busqueda;
import Servicios.GestionCliente;
import Servicios.GestionProducto;
import AcccesoDatosCatalogo.descuentos.ReglaDescuento;
import EntidadesCatalogo.Configuracion;
import EntidadesCatalogo.Pedido;
import EntidadesCatalogo.DetalleCompra;
import EntidadesCatalogo.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
public class GestionPedidos {
    
    private final List<Pedido> listaPedidos;
    
   
    private final GestionProducto gestionProducto;
    private final AlmacenamientoPedidos almacenamiento; 
    private final List<ReglaDescuento> reglasDescuento; 

    // --- Constructor ---
    // Recibimos la interfaz AlmacenamientoPedidos para desacoplar la persistencia
    public GestionPedidos(GestionProducto gp, GestionCliente gc, AlmacenamientoPedidos almacenamiento) {
        this.gestionProducto = gp;
        this.almacenamiento = almacenamiento;
        this.reglasDescuento = new ArrayList<>();
        
        // Carga usando la interfaz (Abstracción)
        this.listaPedidos = almacenamiento.cargarPedidos(gp.obtenerTodos(), gc.obtenerTodos());
    }

    public void agregarReglaDescuento(ReglaDescuento regla) {
        this.reglasDescuento.add(regla);
    }

    // --- LÓGICA DE NEGOCIO (Strategy Pattern) ---
public void aplicarReglasDeNegocio(Pedido p, Configuracion config) {
        
       
        p.setPorcentajeIvaSnapshot(config.getIvaPorcentaje());
        p.setTasaCambioSnapshot(config.getTasaCambio());
        p.limpiarLogDescuentos();
        
        // --- CÁLCULO EXPLÍCITO DEL SUBTOTAL (EN VARIABLE LOCAL) ---
        BigDecimal subTotalBruto = BigDecimal.ZERO;
        
        for (DetalleCompra det : p.getDetalles()) {
            BigDecimal precio = BigDecimal.valueOf(det.getProducto().getPrecio());
            BigDecimal cantidad = BigDecimal.valueOf(det.getCantidad());
            BigDecimal subItem = precio.multiply(cantidad);
            
            // Sumamos al acumulador local para usarlo en los descuentos
            subTotalBruto = subTotalBruto.add(subItem);
        }
        
       
        p.setSubTotal(subTotalBruto);
     
        
        BigDecimal ahorroEnItems = BigDecimal.ZERO;
        BigDecimal descuentosGlobales = BigDecimal.ZERO;
        
        // A. Descuentos por ITEM (Si el Controlador ya los calculó)
        for(DetalleCompra det : p.getDetalles()) {
            if (det.getDescuentoAplicado() > 0) {
                BigDecimal descUnitario = BigDecimal.valueOf(det.getDescuentoAplicado());
                BigDecimal cantidad = BigDecimal.valueOf(det.getCantidad());
                BigDecimal totalAhorroItem = descUnitario.multiply(cantidad);
                
                ahorroEnItems = ahorroEnItems.add(totalAhorroItem);
                p.agregarLogDescuento("Desc. Producto (" + det.getProducto().getNombre() + ")", totalAhorroItem);
            }
        }
       

        // B. Descuentos GLOBALES
        
        // --- 1. Cupón ---
        if (p.getMontoDescuentoCupon() != null && p.getMontoDescuentoCupon().compareTo(BigDecimal.ZERO) > 0) {
            descuentosGlobales = descuentosGlobales.add(p.getMontoDescuentoCupon());
            p.agregarLogDescuento("Cupón [" + p.getCodigoCuponAplicado() + "]", p.getMontoDescuentoCupon());
        }
        
        // --- 2. Fidelidad ---
        if (p.getPorcentajeDescuentoFidelidad() > 0) {
            BigDecimal porcFid = BigDecimal.valueOf(p.getPorcentajeDescuentoFidelidad());
            BigDecimal montoFidelidad = subTotalBruto.multiply(porcFid);
            
            descuentosGlobales = descuentosGlobales.add(montoFidelidad);
            p.agregarLogDescuento("Cliente Frecuente (5%)", montoFidelidad);
        }
        
        // --- 3. Reglas Avanzadas (Strategy) ---
        for (ReglaDescuento regla : reglasDescuento) {
             // Ahora que subTotalBruto > 0, las reglas deberían aplicar
             if (regla.aplica(p)) {
                BigDecimal montoRegla = regla.calcularDescuento(p);
                
                // LÓGICA DE NO DUPLICIDAD:
                // Si el item ya tiene descuento manual, NO aplicamos regla de volumen para no duplicar.
                boolean yaTieneDescItem = ahorroEnItems.compareTo(BigDecimal.ZERO) > 0;
                
                if (!yaTieneDescItem || !regla.getClass().getSimpleName().contains("Volumen")) {
                    descuentosGlobales = descuentosGlobales.add(montoRegla);
                    p.agregarLogDescuento("Promo Especial", montoRegla);
                }
             }
        }

        // 3. APLICAR TOTALES FINALES
        BigDecimal granTotalDescuentos = ahorroEnItems.add(descuentosGlobales);
        
        // Validación de seguridad: Descuento no mayor al total
        if (granTotalDescuentos.compareTo(subTotalBruto) > 0) {
            granTotalDescuentos = subTotalBruto;
        }
        
        p.setTotalDescuentos(granTotalDescuentos);
      
        // Base Imponible
        BigDecimal baseImponible = subTotalBruto.subtract(granTotalDescuentos);
        
        // Impuestos y Finales
        BigDecimal tasaIva = BigDecimal.valueOf(p.getPorcentajeIvaSnapshot());
        BigDecimal iva = baseImponible.multiply(tasaIva);
        
        BigDecimal totalDivisa = baseImponible.add(iva);
        p.setTotalDivisa(totalDivisa.setScale(2, RoundingMode.HALF_UP));
        
        BigDecimal tasaCambio = BigDecimal.valueOf(p.getTasaCambioSnapshot());
        p.setTotalLocal(totalDivisa.multiply(tasaCambio).setScale(2, RoundingMode.HALF_UP));
    }
    // --- 1. REGISTRAR VENTA ---
    public boolean registrarPedido(Pedido nuevoPedido) {
        // Validaciones básicas
        if (nuevoPedido == null) return false;
        if (nuevoPedido.getDetalles().isEmpty()) return false; 
        if (nuevoPedido.getCliente() == null) return false;

        // 1. GENERAR ID AUTOMÁTICO (CORREGIDO)
        // Buscamos el ID más alto existente en toda la lista, sin importar el orden
        int maxId = 0;
        for (Pedido p : listaPedidos) {
            if (p.getIdPedido() > maxId) {
                maxId = p.getIdPedido();
            }
        }
        // El nuevo ID siempre será el máximo encontrado + 1
        nuevoPedido.setIdPedido(maxId + 1);
        
        // Estado inicial
        nuevoPedido.setEstado(Pedido.EstadoPedido.PAGADO);

        // 2. ACTUALIZAR STOCK
        for (DetalleCompra detalle : nuevoPedido.getDetalles()) {
            Producto prod = detalle.getProducto();
            int cantidadComprada = detalle.getCantidad();
            
            int stockFinal = prod.getStock() - cantidadComprada;
            prod.setStock(stockFinal);
            
            gestionProducto.actualizarProducto(prod);
        }

        // 3. GUARDAR EL PEDIDO
        listaPedidos.add(nuevoPedido);
        almacenamiento.guardarPedidos(listaPedidos); 
        
        return true;
    }

    // --- 2. ANULAR PEDIDO ---
    public boolean anularPedido(int idPedido) {
        Pedido pedido = buscarPorId(idPedido);
        
        if (pedido != null && pedido.isEstado() == true) {
            // 1. Cambiamos estado a Falso (Anulado)
          pedido.setEstado(Pedido.EstadoPedido.ANULADO);
            
            // 2. DEVOLVER STOCK
            for (DetalleCompra detalle : pedido.getDetalles()) {
                Producto prod = detalle.getProducto();
                int cantidadDevolver = detalle.getCantidad();
                
                prod.setStock(prod.getStock() + cantidadDevolver);
                gestionProducto.actualizarProducto(prod);
            }
            
            // 3. Guardar cambios (Usando la interfaz)
            almacenamiento.guardarPedidos(listaPedidos);
            return true;
        }
        return false;
    }

    // --- 3. BÚSQUEDAS (Retornando List para flexibilidad) ---

    public Pedido buscarPorId(int id) {
        return Busqueda.buscarUno(listaPedidos, p -> p.getIdPedido() == id);
    }
    
    public List<Pedido> buscarPorCliente(String cedula) {
        // Casteo seguro o cambio de retorno en Busqueda (asumiendo que devuelve ArrayList)
        return Busqueda.buscarVarios(listaPedidos, p -> 
            p.getCliente().getCedula().equalsIgnoreCase(cedula)
        );
    }
    
    public List<Pedido> buscarPorFecha(LocalDate fechaBusqueda) {
        return Busqueda.buscarVarios(listaPedidos, p -> 
            p.getFecha().toLocalDate().equals(fechaBusqueda)
        );
    }
    
    public ArrayList<Pedido> buscarPedidosEntreFechas(LocalDate inicio, LocalDate fin) {
        return Busqueda.buscarVarios(listaPedidos, p -> {
            LocalDate fechaP = p.getFecha().toLocalDate();
            return (fechaP.isEqual(inicio) || fechaP.isAfter(inicio)) &&
                   (fechaP.isEqual(fin) || fechaP.isBefore(fin));
        });
    }

    public List<Pedido> obtenerTodos() {
        return listaPedidos;
    }
    
    // Estadísticas
    public double calcularVentasDelDia() {
        double totalDia = 0;
        LocalDate hoy = LocalDate.now();
        
        for(Pedido p : listaPedidos) {
            if (p.getEstado() == Pedido.EstadoPedido.PAGADO && p.getFecha().toLocalDate().equals(hoy)) {
                // Usamos el helper de compatibilidad o .doubleValue()
                totalDia += p.getTotalDivisa().doubleValue();
            }
        }
        return totalDia;
    }
}