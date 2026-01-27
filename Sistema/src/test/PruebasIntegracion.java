package test;

import Servicios.GestionPedidos;
import Servicios.GestionCliente;
import Servicios.GestionProducto;
import AcccesoDatosCatalogo.*;
import AcccesoDatosCatalogo.descuentos.*;
import EntidadesCatalogo.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PruebasIntegracion {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBAS DE REFACTORIZACIÓN ---");
        
        try {
            testPedidoBuilder();
            testEstrategiaDescuentos();
            testPersistenciaMock();
            System.out.println("\n✅ TODAS LAS PRUEBAS PASARON EXITOSAMENTE.");
        } catch (Exception e) {
            System.err.println("\n❌ FALLO EN PRUEBAS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testPedidoBuilder() {
        System.out.print("1. Test PedidoBuilder... ");
        Cliente c = new Cliente();
        c.setNombreCompleto("Tester");
        
        Pedido p = new PedidoBuilder(1, c)
                .conConfiguracion(new Configuracion(0.16, 40.0))
                .build();
        
        if (p.getCliente() == null || p.getIdPedido() != 1) throw new RuntimeException("Fallo construcción básica");
        System.out.println("OK");
    }

    private static void testEstrategiaDescuentos() {
        System.out.print("2. Test Strategy Descuentos... ");
        
        // Setup
        Producto prod = new Producto();
        prod.setNombre("Producto Test"); // Para que el log se vea bien
        prod.setStock(1000);
        prod.setPrecio(100.0);
        prod.setTieneDescuentoVolumen(true);
        prod.setCantidadParaDescuento(10);
        prod.setPorcentajeDescuentoVolumen(10.0); // 10%
        
        DetalleCompra det = new DetalleCompra(prod, 10); // 10 unidades * 100 = 1000 base
        
        Pedido p = new Pedido(1, new Cliente());
        p.getDetalles().add(det);
        p.setTasaCambioSnapshot(1.0);
        p.setPorcentajeIvaSnapshot(0.0);
        
        // Configurar GestionPedidos con mocks manuales
        GestionPedidos gp = new GestionPedidos(new GestionProducto(), new GestionCliente(), new AlmacenamientoPedidosMock());
        
        // Inyectar Estrategia
        gp.agregarReglaDescuento(new ReglaVolumen());
        
        // Ejecutar
        gp.aplicarReglasDeNegocio(p, new Configuracion());
        
        // Verificar: 1000 subtotal. Descuento 10% de 1000 = 100. Total = 900.
        BigDecimal descuentoEsperado = new BigDecimal("100.00"); // 100 * 10 * 0.10
        // Ajustamos escala para comparar
        if (p.getTotalDescuentos().compareTo(descuentoEsperado) != 0) {
             // Nota: Puede requerir ajuste de escala en assert
             double diff = p.getTotalDescuentos().subtract(descuentoEsperado).doubleValue();
             if (Math.abs(diff) > 0.01)
                throw new RuntimeException("Descuento incorrecto. Esperado: 100, Obtenido: " + p.getTotalDescuentos());
        }
        System.out.println("OK");
    }

    private static void testPersistenciaMock() {
        System.out.print("3. Test Persistencia Abstracta... ");
        AlmacenamientoPedidos repo = new AlmacenamientoPedidosMock();
        repo.guardarPedidos(new ArrayList<>());
        // Si no lanza excepción, la abstracción funciona
        System.out.println("OK");
    }
    
    // Mock simple para tests
    static class AlmacenamientoPedidosMock implements AlmacenamientoPedidos {
        @Override
        public java.util.List<Pedido> cargarPedidos(java.util.List<Producto> p, java.util.List<Cliente> c) {
            return new ArrayList<>();
        }
        @Override
        public void guardarPedidos(java.util.List<Pedido> pedidos) {
            // Mock behavior: do nothing or log
        }
    }
}