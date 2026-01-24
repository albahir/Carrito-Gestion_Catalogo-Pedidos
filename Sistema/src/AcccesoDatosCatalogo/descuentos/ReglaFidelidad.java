package AcccesoDatosCatalogo.descuentos;

import EntidadesCatalogo.Pedido;
import java.math.BigDecimal;

public class ReglaFidelidad implements ReglaDescuento {
    
    // Umbral ejemplo: más de 5 compras previas
    private static final int UMBRAL_COMPRAS = 5;
    private static final BigDecimal PORCENTAJE_FIDELIDAD = new BigDecimal("0.05"); // 5%

    @Override
    public boolean aplica(Pedido pedido) {
        return pedido.getCliente() != null && pedido.getCliente().getComprasRealizadas() > UMBRAL_COMPRAS;
    }

    @Override
    public BigDecimal calcularDescuento(Pedido pedido) {
        // Calcula 5% sobre el subtotal base si aplica
        BigDecimal subTotal = pedido.getSubTotal(); 
        return subTotal.multiply(PORCENTAJE_FIDELIDAD);
    }
}