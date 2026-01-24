package AcccesoDatosCatalogo;

import EntidadesCatalogo.Pedido;
import EntidadesCatalogo.Producto;
import EntidadesCatalogo.Cliente;
import java.util.List;

/**
 * Contrato para el almacenamiento de pedidos.
 * Permite cambiar entre TXT, Base de Datos o Memoria sin afectar al sistema.
 */
public interface AlmacenamientoPedidos {
    List<Pedido> cargarPedidos(List<Producto> productos, List<Cliente> clientes);
    void guardarPedidos(List<Pedido> pedidos);
}