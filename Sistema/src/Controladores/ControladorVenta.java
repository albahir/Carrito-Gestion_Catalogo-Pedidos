package Controladores;

import AcccesoDatosCatalogo.GestionCliente;
import AcccesoDatosCatalogo.GestionPedidos;
import AcccesoDatosCatalogo.GestionProducto;
import EntidadesCatalogo.Cliente;
import EntidadesCatalogo.DetalleCompra;
import EntidadesCatalogo.Pedido;
import EntidadesCatalogo.Producto;
import EntidadesCatalogo.Configuracion;
import Utilidades.MensajesUI;
import VistasCatalogo.DialogoCliente;
import VistasCatalogo.DialogoIdentificarCliente;
import VistasCatalogo.DialogoPago;
import VistasCatalogo.DialogoResumenPedido;
import VistasCatalogo.PanelCarrito;
import VistasCatalogo.PanelCatalogo; // Importación necesaria
import java.math.BigDecimal;
import javax.swing.JFrame;

public class ControladorVenta {

    private final JFrame parentFrame;
    private final GestionPedidos gestionPedidos;
    private final GestionCliente gestionCliente;
    private final Configuracion config;
    
    // Vistas
    private final PanelCarrito panelCarrito;
    private final PanelCatalogo panelCatalogo;
    

    // Estado de la Venta Actual
    private Pedido pedidoActual;
    private Cliente clienteActual; 
    

    // Constructor Actualizado (Ya no pide el JLabel lblCliente porque lo movimos al carrito)
  public ControladorVenta(JFrame parent, GestionPedidos gp, GestionCliente gc, GestionProducto gpro, 
                            Configuracion cfg, PanelCarrito pCarrito, PanelCatalogo pCatalogo) {
        this.parentFrame = parent;
        this.gestionPedidos = gp;
        this.gestionCliente = gc;
        this.config = cfg;
        this.panelCarrito = pCarrito;
        this.panelCatalogo = pCatalogo;

        // Inicializamos un pedido vacío
        this.pedidoActual = new Pedido();
        this.clienteActual = null; 
        actualizarVistaCarrito();
    }
  
  public void aplicarCupon(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return;
        
        String codigoNormalizado = codigo.trim().toUpperCase();
        double descuento = 0;

        if (codigoNormalizado.equals("BIENVENIDO")) descuento = 20.0;
        else if (codigoNormalizado.equals("PROMO50")) descuento = 50.0;
        else if (codigoNormalizado.equals("VIP")) descuento = 100.0;

        if (descuento > 0) {
            // 1. Aplicamos al MODELO (Pedido)
            pedidoActual.setCodigoCuponAplicado(codigoNormalizado); 
            pedidoActual.setMontoDescuentoCupon(BigDecimal.valueOf(descuento));
            panelCarrito.mostrarMensajeCupon("¡Cupón " + codigoNormalizado + " aplicado!", false);
            actualizarVistaCarrito();
        } else {
           panelCarrito.mostrarMensajeCupon("Cupón inválido", true);
            pedidoActual.setMontoDescuentoCupon(BigDecimal.ZERO);
            panelCarrito.mostrarMensajeCupon("Cupón inválido o expirado.", true);
            actualizarVistaCarrito();
        }
    }
  public void quitarCupon() {
        if (pedidoActual != null) {
            pedidoActual.setCodigoCuponAplicado("");
            pedidoActual.setMontoDescuentoCupon(BigDecimal.ZERO);
         panelCarrito.mostrarMensajeCupon(" ", false);
           actualizarVistaCarrito(); // Recalcula todo a 0
            
        }
    }
    // --- 1. GESTIÓN DEL CARRITO ---

    public void agregarAlCarrito(Producto p) {
       if (this.clienteActual == null) {
            abrirSeleccionCliente();
            
            
            
           if (this.clienteActual == null) {
                return; 
            }
        }
        // Verificar stock antes de agregar
        if (p.getStock() <= 0) {
            MensajesUI.mostrarError(parentFrame, "Producto agotado.");
            return;
        }

        // Buscar si ya existe en el carrito
        DetalleCompra detalleExistente = null;
        for (DetalleCompra det : pedidoActual.getDetalles()) {
            if (det.getProducto().getIdProducto() == p.getIdProducto()) {
                detalleExistente = det;
                break;
            }
        }

        if (detalleExistente != null) {
            // Si ya existe, validamos stock y sumamos 1
            if (detalleExistente.getCantidad() + 1 > p.getStock()) {
                MensajesUI.mostrarInfo(parentFrame, "No hay más stock disponible de " + p.getNombre());
                return;
            }
            detalleExistente.setCantidad(detalleExistente.getCantidad() + 1);
            // Recalcular descuento individual
            recalcularDescuentosItem(detalleExistente);
        } else {
            // Si es nuevo, creamos el detalle
            DetalleCompra nuevoDetalle = new DetalleCompra(p, 1);
            recalcularDescuentosItem(nuevoDetalle);
            pedidoActual.getDetalles().add(nuevoDetalle);
        }
        gestionPedidos.aplicarReglasDeNegocio(pedidoActual, config);
        actualizarVistaCarrito();
    }

    public void modificarCantidadCarrito(int index, int delta) {
        if (index >= 0 && index < pedidoActual.getDetalles().size()) {
            DetalleCompra det = pedidoActual.getDetalles().get(index);
            int nuevaCant = det.getCantidad() + delta;

            // Validaciones
            if (nuevaCant < 1) {
               
                return;
            } // No bajar de 1
            if (nuevaCant > det.getProducto().getStock()) {
                MensajesUI.mostrarInfo(parentFrame, "Stock máximo alcanzado para este producto.");
                return;
            }

            det.setCantidad(nuevaCant);
            gestionPedidos.aplicarReglasDeNegocio(pedidoActual, config);
            recalcularDescuentosItem(det);
            actualizarVistaCarrito();
        }
    }

    public void eliminarItemCarrito(int index) {
        if (index >= 0 && index < pedidoActual.getDetalles().size()) {
            pedidoActual.getDetalles().remove(index);
            gestionPedidos.aplicarReglasDeNegocio(pedidoActual, config);
            actualizarVistaCarrito();
        }
    }
    
    // Método auxiliar para lógica de descuentos
 private void recalcularDescuentosItem(DetalleCompra det) {
        Producto p = det.getProducto();
        double ahorroUnitario = 0; 
        
        if (p.isEnOfertaFlash()) {
            ahorroUnitario = p.getPrecio() - p.getPrecioOferta();
        } else if (p.isTieneDescuentoVolumen() && det.getCantidad() >= p.getCantidadParaDescuento()) {
            ahorroUnitario = p.getPrecio() * (p.getPorcentajeDescuentoVolumen() / 100.0);
        }
        det.setDescuentoAplicado(ahorroUnitario);
    }

    private void actualizarVistaCarrito() {
    gestionPedidos.aplicarReglasDeNegocio(pedidoActual, config);
     // Enviamos el pedido ya calculado al panel
        panelCarrito.renderizarPedido(pedidoActual);
    }

    // --- 2. GESTIÓN DEL CLIENTE ---

 public void abrirSeleccionCliente() {
        DialogoIdentificarCliente dialogo = new DialogoIdentificarCliente(parentFrame, gestionCliente);
        String entradaUsuario = dialogo.mostrar(); // El usuario escribe "123456" o "V-123456"
        
        if (entradaUsuario != null && !entradaUsuario.isEmpty()) {
            
            // --- CAMBIO CLAVE: USAMOS LA BÚSQUEDA INTELIGENTE ---
            // Toda la lógica de probar V-, E-, J- ahora está encapsulada aquí.
            Cliente c = gestionCliente.buscarInteligente(entradaUsuario);

            if (c != null) {
                // CASO 1: ENCONTRADO
                setClienteActual(c);
            } else {
                // CASO 2: NO EXISTE -> OFRECER REGISTRO
                boolean registrar = MensajesUI.confirmar(parentFrame, "Cliente Nuevo", 
                        "La cédula " + entradaUsuario + " no existe. ¿Desea registrarlo?");
                
                if (registrar) {
                    DialogoCliente registro = new DialogoCliente(parentFrame, gestionCliente);
                    // Opcional: Podrías pasarle la cédula escrita para que no la vuelva a tipear
                    // registro.preCargarCedula(entradaUsuario); 
                    
                    Cliente nuevo = registro.mostrar();
                    if (nuevo != null) {
                        setClienteActual(nuevo);
                    }
                }
            }
        }
    }
    public void setClienteActual(Cliente c) {
    this.clienteActual = c;
        pedidoActual.setCliente(c);
        
       
        if (c.getComprasRealizadas() > 5) {
            pedidoActual.setPorcentajeDescuentoFidelidad(0.05); // 5%
            MensajesUI.mostrarInfo(parentFrame, "Cliente Frecuente: 5% descuento aplicado.");
        } else {
            pedidoActual.setPorcentajeDescuentoFidelidad(0.0);
        }
        
        // Actualizar UI
        panelCarrito.setCliente(c);
        actualizarVistaCarrito();
    }

    // --- 3. FINALIZAR VENTA ---

  // --- 3. FINALIZAR VENTA (Lógica Inteligente) ---
    public void confirmarVenta() {
        // 1. Validaciones previas
        if (pedidoActual.getDetalles().isEmpty()) return;
        if (clienteActual == null) { abrirSeleccionCliente(); return; }

        boolean ventaListaParaGuardar = false;

        // --- VERIFICACIÓN DE TOTAL A PAGAR ---
        // Comparamos si el total en dólares es mayor a 0
        if (pedidoActual.getTotalDivisa().compareTo(BigDecimal.ZERO) > 0) {
            
            // CASO A: HAY DEUDA PENDIENTE -> Flujo Normal (DialogoPago)
            DialogoPago dialogoPago = new DialogoPago(
                parentFrame, 
                pedidoActual.getTotalDivisa(), 
                config.getTasaCambio() 
            );
            
            // Esperamos a que el usuario pague y confirme en el diálogo
            if (dialogoPago.mostrar()) { 
                pedidoActual.setEstado(Pedido.EstadoPedido.PAGADO);
                pedidoActual.setMetodoPago(dialogoPago.getMetodoSeleccionado());  
                pedidoActual.setReferenciaPago(dialogoPago.getReferenciaIngresada()); 
                pedidoActual.setObservaciones(dialogoPago.getObservaciones());
                ventaListaParaGuardar = true;
            }
            
        } else {
            
            // CASO B: TOTAL CUBIERTO (0.00$) -> Flujo Automático (Canje)
            MensajesUI.mostrarInfo(parentFrame, "¡Genial! Tu cupón cubre el 100% de la compra.");
            
            pedidoActual.setEstado(Pedido.EstadoPedido.PAGADO);
            pedidoActual.setMetodoPago("CUPON"); // Método especial para auditoría
            // Generamos una referencia automática única
            pedidoActual.setReferenciaPago("CANJE-" + System.currentTimeMillis()); 
            pedidoActual.setObservaciones("Canje total por cupón: " + pedidoActual.getCodigoCuponAplicado());
            
            ventaListaParaGuardar = true; // Saltamos directo a guardar
        }

        // 4. PERSISTENCIA Y FINALIZACIÓN (Común para ambos casos)
        if (ventaListaParaGuardar) {
            if (gestionPedidos.registrarPedido(pedidoActual)) {
                
                // Actualizar estadísticas del cliente
                clienteActual.setComprasRealizadas(clienteActual.getComprasRealizadas() + 1);
                gestionCliente.actualizarCliente(clienteActual);
                
                // Refrescar inventario en catálogo
                if (panelCatalogo != null) {
                    panelCatalogo.cargarProductos(); 
                }
                
                // 5. MOSTRAR TICKET / COMPROBANTE
                DialogoResumenPedido ticket = new DialogoResumenPedido(parentFrame, pedidoActual, config.getTasaCambio());
                ticket.setVisible(true);
                
                // Limpiar para la siguiente venta
                limpiarVenta();
            }
        }
    }
 
   public void cancelarVenta() {
   limpiarVenta();
}
    private void limpiarVenta() {
       this.pedidoActual = new Pedido();
       this.pedidoActual.calcularTotalesConBigDecimal(config);
        this.clienteActual = null;
        panelCarrito.setCliente(null);
        panelCarrito.mostrarMensajeCupon(" ", false);
        panelCarrito.renderizarPedido(pedidoActual);
    }
}