package EntidadesCatalogo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;

public class Pedido {
      public enum EstadoPedido {
    EN_PROCESO,  // El cliente está comprando (Carrito)
    POR_PAGAR,   // Confirmó, pero no ha pagado
    PAGADO,      // Dinero recibido, stock descontado
    ANULADO      // Cancelado
}
      private String observaciones;
    private String metodoPago;  
    private String referenciaPago;
    // --- Identidad ---
    private int idPedido;
    private Cliente cliente;      
    private LocalDateTime fecha;  
    
    // CAMBIO 1: Estado Evolucionado
    private EstadoPedido estado;        
    
    // --- Contenido ---
    private ArrayList<DetalleCompra> detalles; 
    
    // CAMBIO 2: Bitácora de Descuentos (Para el recibo)
    private List<String> logDescuentos;

    // --- Económicos (BigDecimal) ---
    private BigDecimal subTotal;       
    private BigDecimal montoIVA;      
    private BigDecimal totalDivisa;   
    private BigDecimal totalLocal;     
    private BigDecimal totalDescuentos; 
    
    // Cupones y Auxiliares
    private String codigoCuponAplicado = ""; 
    private BigDecimal montoDescuentoCupon; 
    private double porcentajeDescuentoFidelidad = 0.0; 
    private BigDecimal montoDescuentoFidelidad; 
    
    // Snapshots
    private double tasaCambioSnapshot; 
    private double porcentajeIvaSnapshot;

    // --- CONSTRUCTOR VACÍO ---
    public Pedido() {
        this.idPedido = 0;
        this.cliente = new Cliente();
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
        this.logDescuentos = new ArrayList<>(); // Inicializar lista
        this.estado = EstadoPedido.EN_PROCESO; // Estado inicial por defecto
        
        // Inicialización segura
        this.subTotal = BigDecimal.ZERO;
        this.montoIVA = BigDecimal.ZERO;
        this.totalDivisa = BigDecimal.ZERO;
        this.totalLocal = BigDecimal.ZERO;
        this.totalDescuentos = BigDecimal.ZERO;
        this.montoDescuentoCupon = BigDecimal.ZERO;
        this.montoDescuentoFidelidad = BigDecimal.ZERO;
    }
 
    // --- CONSTRUCTOR NUEVO ---
    public Pedido(int idPedido, Cliente cliente) {
        this(); 
        setIdPedido(idPedido);
        setCliente(cliente);
        this.detalles = new ArrayList<>();
    }
    

    // --- CONSTRUCTOR CARGA (Compatibilidad con Archivo Antiguo) ---
    // NOTA: Aquí convertimos el boolean viejo al Enum nuevo para no romper tu TXT actual
    public Pedido(int idPedido, Cliente cliente, LocalDateTime fecha, double totalDivisaDouble, boolean estadoViejo) {
        this();
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.fecha = fecha;
        // Mapeo de compatibilidad
        this.estado = estadoViejo ? EstadoPedido.PAGADO : EstadoPedido.ANULADO;
        this.totalDivisa = BigDecimal.valueOf(totalDivisaDouble);
    }
  
 public String getResumenDescuentosHTML() {
        StringBuilder sb = new StringBuilder();
        // Sumamos descuentos de items (Asumiendo que DetalleCompra devuelve double, lo convertimos)
        double descItems = detalles.stream().mapToDouble(DetalleCompra::getDescuentoAplicado).sum();
        
        if (descItems > 0) {
            sb.append(String.format("<tr><td>📦 Productos:</td><td align='right'><b>$%.2f</b></td></tr>", descItems));
        }
        if (montoDescuentoCupon.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("<tr><td>🎟️ Cupón (%s):</td><td align='right'><b>$%.2f</b></td></tr>", 
                      codigoCuponAplicado, montoDescuentoCupon.doubleValue()));
        }
        // Nota: asumo que tienes un campo montoDescuentoFidelidad calculado externamente
        if (montoDescuentoFidelidad.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("<tr><td>⭐ Fidelidad:</td><td align='right'><b>$%.2f</b></td></tr>", 
                      montoDescuentoFidelidad.doubleValue()));
        }
        return sb.toString();
    }
    // --- MÉTODOS PARA EL LOG DE DESCUENTOS ---
    public void limpiarLogDescuentos() {
        this.logDescuentos.clear();
    }
    
    public void agregarLogDescuento(String descripcion, BigDecimal monto) {
        // Guardamos texto tipo: "Cupón VIP: -$20.00"
        this.logDescuentos.add(String.format("%s: -$%.2f", descripcion, monto.doubleValue()));
    }
    
    public List<String> getLogDescuentos() {
        return logDescuentos;
    }

    // --- GETTERS Y SETTERS ---
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int id) { if (id > 0) this.idPedido = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { if(cliente != null) this.cliente = cliente; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { if(fecha != null) this.fecha = fecha; }

    public ArrayList<DetalleCompra> getDetalles() { return detalles; }
    
    // Nuevo Getter/Setter de Estado
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    // Compatibilidad para código viejo que use isEstado()
    public boolean isEstado() { 
        return estado == EstadoPedido.PAGADO || estado == EstadoPedido.POR_PAGAR; 
    }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    public BigDecimal getMontoIVA() { return montoIVA; }
    public void setMontoIVA(BigDecimal montoIVA) { this.montoIVA = montoIVA; }

    public BigDecimal getTotalDivisa() { return totalDivisa; }
    public void setTotalDivisa(BigDecimal totalDivisa) { this.totalDivisa = totalDivisa; }

    public BigDecimal getTotalLocal() { return totalLocal; }
    public void setTotalLocal(BigDecimal totalLocal) { this.totalLocal = totalLocal; }
    
    public BigDecimal getTotalDescuentos() { return totalDescuentos; }
    public void setTotalDescuentos(BigDecimal t) { this.totalDescuentos = t; }

    // Helpers UI
    public double getTotalDivisaDouble() { return totalDivisa.doubleValue(); }
    public double getTotalLocalDouble() { return totalLocal.doubleValue(); }
    public double getSubTotalDouble() { return subTotal.doubleValue(); }
    public double getTotalDescuentosDouble() { return totalDescuentos.doubleValue(); }

    public double getTasaCambioSnapshot() { return tasaCambioSnapshot; }
    public void setTasaCambioSnapshot(double tasa) { this.tasaCambioSnapshot = tasa; }

    public double getPorcentajeIvaSnapshot() { return porcentajeIvaSnapshot; }
    public void setPorcentajeIvaSnapshot(double iva) { this.porcentajeIvaSnapshot = iva; }

    public String getCodigoCuponAplicado() { return codigoCuponAplicado; }
    public void setCodigoCuponAplicado(String codigo) { 
        this.codigoCuponAplicado = (codigo == null) ? "" : codigo.trim().toUpperCase(); 
    }

    public BigDecimal getMontoDescuentoCupon() { return montoDescuentoCupon; }
    public void setMontoDescuentoCupon(BigDecimal monto) { this.montoDescuentoCupon = monto; }

    public double getPorcentajeDescuentoFidelidad() { return porcentajeDescuentoFidelidad; }
    public void setPorcentajeDescuentoFidelidad(double porcentaje) { this.porcentajeDescuentoFidelidad = porcentaje; }
    public void setMontoDescuentoFidelidad(BigDecimal monto) { this.montoDescuentoFidelidad = monto; }

    // --- LÓGICA DE FORMATO ---
    public void calcularTotalesConBigDecimal(Configuracion config) {
        BigDecimal sub = BigDecimal.ZERO;
        for(DetalleCompra det : detalles) {
            sub = sub.add(BigDecimal.valueOf(det.getSubTotal()));
        }
        this.subTotal = sub;
    }
    public String getMetodoPago() { return metodoPago != null ? metodoPago : "N/A"; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getReferenciaPago() { return referenciaPago != null ? referenciaPago : ""; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }
public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    @Override
    public String toString() {
        return String.format("Pedido #%d (%s) - %s - $%.2f", 
                             idPedido, estado, cliente.getNombreCompleto(), totalDivisa.doubleValue());
    }
}