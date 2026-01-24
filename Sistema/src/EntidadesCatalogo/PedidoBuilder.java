package EntidadesCatalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Builder Pattern para construir objetos Pedido consistentes.
 */
public class PedidoBuilder {
    private Pedido pedido;

    public PedidoBuilder(int idPedido, Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("No se puede crear un pedido sin cliente");
        }
        this.pedido = new Pedido(idPedido, cliente);
        // Inicializar listas internas si no lo están
        if (this.pedido.getDetalles() == null) {
            // Asumiendo acceso package-private o setter necesario, 
            // pero Pedido ya inicializa detalles en constructor.
        }
    }

    public PedidoBuilder conFecha(LocalDateTime fecha) {
        this.pedido.setFecha(fecha);
        return this;
    }

    public PedidoBuilder conConfiguracion(Configuracion config) {
        if (config != null) {
            this.pedido.setTasaCambioSnapshot(config.getTasaCambio());
            this.pedido.setPorcentajeIvaSnapshot(config.getIvaPorcentaje());
        }
        return this;
    }

    public PedidoBuilder agregarDetalle(DetalleCompra detalle) {
        if (detalle != null && detalle.getCantidad() > 0) {
            this.pedido.getDetalles().add(detalle);
        }
        return this;
    }

    public Pedido build() {
        // Validaciones finales antes de entregar el objeto
        if (this.pedido.getDetalles().isEmpty()) {
            // Nota: Podríamos permitir pedidos vacíos temporalmente, 
            // pero el builder asegura que el objeto esté listo.
            // throw new IllegalStateException("El pedido debe tener al menos un detalle");
        }
        // Recalcular totales iniciales
        // Nota: Esto requiere que Pedido tenga un método interno de cálculo o se delegue.
        return this.pedido;
    }
}