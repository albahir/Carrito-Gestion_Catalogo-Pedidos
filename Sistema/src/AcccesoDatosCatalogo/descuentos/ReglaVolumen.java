package AcccesoDatosCatalogo.descuentos;

import EntidadesCatalogo.DetalleCompra;
import EntidadesCatalogo.Pedido;
import java.math.BigDecimal;

public class ReglaVolumen implements ReglaDescuento {

    @Override
    public boolean aplica(Pedido pedido) {
        
        // Aplica si algún detalle tiene producto con descuento por volumen activado
        return pedido.getDetalles().stream()
                .anyMatch(d -> d.getProducto().isTieneDescuentoVolumen() 
                            && d.getCantidad() >= d.getProducto().getCantidadParaDescuento());
    }

    @Override
    public BigDecimal calcularDescuento(Pedido pedido) {
        BigDecimal totalDescuento = BigDecimal.ZERO;

        for (DetalleCompra det : pedido.getDetalles()) {
            if (det.getProducto().isTieneDescuentoVolumen() 
                && det.getCantidad() >= det.getProducto().getCantidadParaDescuento()) {
                
                BigDecimal precio = BigDecimal.valueOf(det.getProducto().getPrecio());
                BigDecimal porcentaje = BigDecimal.valueOf(det.getProducto().getPorcentajeDescuentoVolumen() / 100.0);
                BigDecimal cantidad = BigDecimal.valueOf(det.getCantidad());
                
                // Descuento = Precio * Porcentaje * Cantidad
                BigDecimal descuentoItem = precio.multiply(porcentaje).multiply(cantidad);
                totalDescuento = totalDescuento.add(descuentoItem);
            }
        }
        return totalDescuento;
    }
}