/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VistasCatalogo;


import AcccesoDatosCatalogo.GestionCliente;
import AcccesoDatosCatalogo.GestionPedidos;
import AcccesoDatosCatalogo.GestionProducto;
import AcccesoDatosCatalogo.descuentos.ReglaFidelidad;
import AcccesoDatosCatalogo.descuentos.ReglaVolumen;
import AcccesoDatosCatalogo.repositorio.RepositorioPedidosTXT;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    public static void main(String[] args) {
        
        // 1. EMBELLECER LA INTERFAZ (Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("No se pudo cargar el estilo visual nativo. Usando Java default.");
        }

        // 2. INICIALIZAR EL BACKEND (Cargar datos de archivos)
        
        System.out.println("Cargando Catálogo de Productos...");
        GestionProducto gestionProducto = new GestionProducto(); // Variable creada aquí
        
        System.out.println("Cargando Cartera de Clientes...");
        GestionCliente gestionCliente = new GestionCliente(); // Variable creada aquí
        
        System.out.println("Cargando Historial de Pedidos...");
        
        // CORRECCIÓN: Usamos las variables 'gestionProducto' y 'gestionCliente' que creamos arriba.
        // Antes tenías 'gp' y 'gc', que no existían.
        GestionPedidos gestionPedidos = new GestionPedidos(
                gestionProducto, 
                gestionCliente, 
                new RepositorioPedidosTXT()
        );
        
        // Configuramos las estrategias de descuento
        gestionPedidos.agregarReglaDescuento(new ReglaVolumen());
        gestionPedidos.agregarReglaDescuento(new ReglaFidelidad());

        // 3. INICIALIZAR LA INTERFAZ GRÁFICA (Frontend)
        java.awt.EventQueue.invokeLater(() -> {
            
            // Creamos la ventana pasándole los gestores correctos
            FrmMenuPrincipal ventana = new FrmMenuPrincipal(gestionProducto, gestionCliente, gestionPedidos);
            
            ventana.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); 
            ventana.setVisible(true);
        });
    }
}