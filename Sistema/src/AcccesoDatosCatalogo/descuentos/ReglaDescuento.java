package AcccesoDatosCatalogo.descuentos;

import EntidadesCatalogo.Pedido;
import java.math.BigDecimal;

/**
 * Interfaz Strategy para reglas de descuento.
 */
public interface ReglaDescuento {
    boolean aplica(Pedido pedido);
    BigDecimal calcularDescuento(Pedido pedido);
}