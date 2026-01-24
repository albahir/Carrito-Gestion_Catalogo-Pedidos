package VistasCatalogo;

import EntidadesCatalogo.DetalleCompra;
import EntidadesCatalogo.Pedido;
import Utilidades.Formato;
import Utilidades.Tema;
import Utilidades.UIFabric;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import javax.swing.border.MatteBorder;

public class DialogoResumenPedido extends JDialog {

    public DialogoResumenPedido(JFrame parent, Pedido pedido, double tasaCambio) {
        super(parent, "Comprobante de Pedido", true);
        setSize(420, 750); // Altura ajustada
        setLocationRelativeTo(parent);
        setResizable(false);
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 25, 20, 25));
        setContentPane(content);

        // --- 1. CABECERA ---
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);

        JLabel lblIcon = new JLabel("✅", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JLabel lblTitulo = new JLabel("¡Pedido Confirmado!", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(39, 174, 96));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ID y Fecha
        JLabel lblIdPedido = new JLabel("Pedido #" + String.format("%04d", pedido.getIdPedido()));
        lblIdPedido.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblIdPedido.setForeground(Color.GRAY);
        lblIdPedido.setAlignmentX(Component.CENTER_ALIGNMENT);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a");
        JLabel lblFecha = new JLabel(pedido.getFecha().format(fmt));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(Color.LIGHT_GRAY);
        lblFecha.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblIcon);
        header.add(Box.createVerticalStrut(5));
        header.add(lblTitulo);
        header.add(Box.createVerticalStrut(5));
        header.add(lblIdPedido);
        header.add(lblFecha);
        header.add(Box.createVerticalStrut(8));

        // --- CORRECCIÓN 1: BADGE DINÁMICO (VERDE O AMARILLO) ---
        boolean isPagado = (pedido.getEstado() == Pedido.EstadoPedido.PAGADO);
        
        String textoEstado = isPagado ? " PAGADO " : " POR PAGAR ";
        Color bgEstado = isPagado ? new Color(209, 231, 221) : new Color(255, 243, 205); // Verde claro vs Amarillo
        Color fgEstado = isPagado ? new Color(15, 81, 50) : new Color(133, 100, 4);      // Verde oscuro vs Marrón
        
        JLabel lblEstado = new JLabel(textoEstado, SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEstado.setOpaque(true);
        lblEstado.setBackground(bgEstado);
        lblEstado.setForeground(fgEstado);
        
        JPanel pBadge = new JPanel();
        pBadge.setBackground(Color.WHITE);
        pBadge.add(lblEstado);
        
        header.add(pBadge);
        content.add(header, BorderLayout.NORTH);

        // --- 2. CUERPO (DATOS + ITEMS) ---
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Color.WHITE);
        
        // Bloque Cliente
        JPanel pCliente = new JPanel(new BorderLayout());
        pCliente.setBackground(new Color(250, 250, 252));
        pCliente.setBorder(new EmptyBorder(10, 10, 10, 10));
        pCliente.setMaximumSize(new Dimension(2000, 70));
        
        String htmlCliente = String.format("<html><b>Cliente:</b> %s<br><b>Cédula:</b> %s<br><b>Teléfono:</b> %s<br><font size='2' color='gray'>%s</font></html>", 
                pedido.getCliente().getNombreCompleto(),
                pedido.getCliente().getCedula(),
                pedido.getCliente().getTelefono(),
                pedido.getCliente().getDireccion());
                
        
        JLabel lblInfoCliente = new JLabel(htmlCliente);
        lblInfoCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pCliente.add(lblInfoCliente, BorderLayout.CENTER);
        
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(pCliente);
       JPanel pPago = new JPanel(new BorderLayout());
        pPago.setBackground(Color.WHITE);
        pPago.setBorder(new EmptyBorder(5, 12, 5, 12)); // Márgenes laterales para alinear
        pPago.setMaximumSize(new Dimension(2000, 30)); 

        // 1. Método de Pago (Izquierda, en Azul/Negrita)
        String metodoTexto = (pedido.getMetodoPago() != null) ? pedido.getMetodoPago() : "Sin Definir";
        JLabel lblMetodo = new JLabel(metodoTexto);
        lblMetodo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMetodo.setForeground(new Color(0, 102, 204)); // Azul para destacar

        // 2. Referencia (Derecha, en Gris)
        String refTexto = (pedido.getReferenciaPago() != null && !pedido.getReferenciaPago().equals("N/A")) 
                          ? "Ref: " + pedido.getReferenciaPago() 
                          : "";
        JLabel lblRef = new JLabel(refTexto);
        lblRef.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRef.setForeground(Color.DARK_GRAY);

        pPago.add(lblMetodo, BorderLayout.WEST);
        pPago.add(lblRef, BorderLayout.EAST);

        cuerpo.add(Box.createVerticalStrut(5)); // Pequeña separación del cliente
        cuerpo.add(pPago);
        
        // Separador Items
        JLabel lblDetalles = new JLabel("Resumen de Artículos:");
        lblDetalles.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDetalles.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuerpo.add(lblDetalles);
        cuerpo.add(Box.createVerticalStrut(5));

        // Lista de Items
        JPanel listaItems = new JPanel();
        listaItems.setLayout(new BoxLayout(listaItems, BoxLayout.Y_AXIS));
        listaItems.setBackground(Color.WHITE);
        
        // -- CORRECCIÓN 2: ELIMINADO EL IF DE DESCUENTOS DUPLICADO AQUÍ --

        for(DetalleCompra det : pedido.getDetalles()) {
            JPanel fila = new JPanel(new BorderLayout());
            fila.setBackground(Color.WHITE);
            fila.setMaximumSize(new Dimension(2000, 25));
            
            JLabel lblProd = new JLabel(det.getCantidad() + " x " + det.getProducto().getNombre());
            lblProd.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            JLabel lblPrice = new JLabel(Formato.dinero(det.getSubTotal()));
            lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            fila.add(lblProd, BorderLayout.WEST);
            fila.add(lblPrice, BorderLayout.EAST);
            listaItems.add(fila);
            listaItems.add(Box.createVerticalStrut(3));
        }
        
        // Scroll para items (CORRECCIÓN 3: SOLUCIÓN AL CONFLICTO DE LAYOUT)
        JScrollPane scroll = new JScrollPane(listaItems);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(350, 150)); // Altura fija para que no aplaste al footer
        
        cuerpo.add(scroll);
        content.add(cuerpo, BorderLayout.CENTER);

        // --- 3. FOOTER FINANCIERO ---
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new MatteBorder(2, 0, 0, 0, Tema.LAVENDER));

        // A. LISTA DETALLADA DE DESCUENTOS (AQUÍ ES EL LUGAR CORRECTO)
        if (!pedido.getLogDescuentos().isEmpty()) {
             JPanel pLog = new JPanel(new GridLayout(0, 1));
             pLog.setBackground(new Color(245, 255, 245)); // Fondo muy sutil
             pLog.setBorder(new EmptyBorder(5, 5, 5, 5));
             
             JLabel lblTitleDesc = new JLabel("Detalle de Ahorros:");
             lblTitleDesc.setFont(new Font("Segoe UI", Font.BOLD, 10));
             lblTitleDesc.setForeground(new Color(34, 139, 34));
             pLog.add(lblTitleDesc);

             for (String log : pedido.getLogDescuentos()) {
                JLabel l = new JLabel("• " + log);
                l.setFont(new Font("Consolas", Font.PLAIN, 10));
                l.setForeground(new Color(0, 100, 0));
                pLog.add(l);
             }
             footer.add(pLog);
             footer.add(Box.createVerticalStrut(5));
        }

        footer.add(Box.createVerticalStrut(5));

        // Subtotal
        footer.add(crearFila("Subtotal:", Formato.dinero(pedido.getSubTotal().doubleValue()), false, Color.GRAY));
        
        // Descuentos Total
        if (pedido.getTotalDescuentos().compareTo(BigDecimal.ZERO )>0) {
            footer.add(crearFila("Total Descuentos:", "-" + Formato.dinero(pedido.getTotalDescuentos().doubleValue()), false, new Color(39, 174, 96)));
        }

        // Total Divisa
        footer.add(Box.createVerticalStrut(5));
        footer.add(crearFila("TOTAL A PAGAR:", Formato.dinero(pedido.getTotalDivisaDouble()), true, Tema.OBSIDIAN));
         
        footer.add(Box.createVerticalStrut(10));
        
        // Sección Bolívares
        JPanel pBs = new JPanel(new BorderLayout());
        pBs.setBackground(new Color(240, 240, 240)); 
        pBs.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        JLabel lblTasa = new JLabel("Tasa: " + String.format("%,.2f", pedido.getTasaCambioSnapshot()));
        lblTasa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTasa.setForeground(Color.GRAY);
        
        JLabel lblTotalBs = new JLabel(String.format("Bs. %,.2f", pedido.getTotalLocal()));
        lblTotalBs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalBs.setForeground(Color.BLACK);
        
        pBs.add(lblTasa, BorderLayout.WEST);
        pBs.add(lblTotalBs, BorderLayout.EAST);
        footer.add(pBs);
        
        footer.add(Box.createVerticalStrut(15)); 

        // Info Empresa
        JPanel pInfoEmpresa = new JPanel(new GridLayout(3, 1));
        pInfoEmpresa.setBackground(Color.WHITE);
        
        JLabel lblEmpresa = new JLabel("TU EMPRESA C.A.", SwingConstants.CENTER);
        lblEmpresa.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEmpresa.setForeground(Color.DARK_GRAY);
        
        JLabel lblRif = new JLabel("RIF: J-12345678-9 | Dirección Fiscal: Av. Principal...", SwingConstants.CENTER);
        lblRif.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblRif.setForeground(Color.GRAY);
        
        JLabel lblLegales = new JLabel("Conserve este ticket para reclamos (Válido por 3 días).", SwingConstants.CENTER);
        lblLegales.setFont(new Font("Segoe UI", Font.ITALIC, 9));
        lblLegales.setForeground(Color.LIGHT_GRAY);
        
        pInfoEmpresa.add(lblEmpresa);
        pInfoEmpresa.add(lblRif);
        pInfoEmpresa.add(lblLegales);
        
        footer.add(pInfoEmpresa);
        footer.add(Box.createVerticalStrut(10));

        // Botón Cerrar
        JButton btnCerrar = UIFabric.crearBotonPrincipal("Cerrar Comprobante", Tema.OBSIDIAN, Color.WHITE);
        btnCerrar.addActionListener(e -> dispose());
        
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pBtn.setBackground(Color.WHITE);
        pBtn.add(btnCerrar);
        
        content.add(footer, BorderLayout.SOUTH);
        footer.add(pBtn);
    }

    private JPanel crearFila(String label, String valor, boolean isBold, Color colorValor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(2000, 20));
        
        JLabel l1 = new JLabel(label);
        l1.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, 12));
        l1.setForeground(Color.GRAY);
        
        JLabel l2 = new JLabel(valor);
        l2.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, 12));
        l2.setForeground(colorValor);
        
        p.add(l1, BorderLayout.WEST);
        p.add(l2, BorderLayout.EAST);
        return p;
    }
}