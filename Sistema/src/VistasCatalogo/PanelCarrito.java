package VistasCatalogo;

import Utilidades.UI.ImagenUtils;
import Utilidades.UI.Tema;
import Utilidades.UI.PanelRedondeado;
import Utilidades.tecnicas.Formato;
import Utilidades.tecnicas.Validaciones;
import Utilidades.UI.UIFabric;
import EntidadesCatalogo.Cliente;
import EntidadesCatalogo.DetalleCompra;
import EntidadesCatalogo.Pedido;
import EntidadesCatalogo.Producto;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PanelCarrito extends JPanel {

    // --- Componentes UI ---
    private JLabel lblClienteNombre; // Nuevo lugar para el cliente
    private JLabel lblMensajeCupon;
    private JPanel contenedorItems; 
    private JTextField txtCupon;
    private JButton btnAplicarCupon;
    // Footer Financiero
    private JLabel lblSubtotalVal;
    private JLabel lblDescuentoVal;
    private JLabel lblTotalDivisa ;
    private JLabel lblBaseBs;
    private JLabel lblIvaBs;
    private JLabel lblTotalPagarBs;
    private JLabel lblTasaBadge;
    private JButton btnCancelar;
    
    // --- Datos ---
    private final double porcentajeFidelidad = 0;
    

    // --- Callbacks ---
    private Runnable accionQuitarCupon;
    private Runnable accionCancelar;
    private Runnable accionConfirmar;
    private Consumer<String> accionAplicarCupon;
    private BiConsumer<Integer, Integer> accionModificarCantidad; 
    private Consumer<Integer> accionEliminar;
    // Colores Locales

    public PanelCarrito() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(450, 0)); 
        setMinimumSize(new Dimension(450, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)));

        initUI();
    }

public void setEventos(Runnable confirmar, Runnable cancelar, Consumer<String> aplicarCupon, 
                           Runnable quitarCupon,BiConsumer<Integer, Integer> modCant, Consumer<Integer> elim) {
        this.accionConfirmar = confirmar;
        this.accionCancelar = cancelar;
        this.accionAplicarCupon = aplicarCupon;
        this.accionQuitarCupon = quitarCupon;
        this.accionModificarCantidad = modCant;
        this.accionEliminar = elim;
    }

    // Método para que el ControladorVenta actualice el nombre del cliente AQUÍ
   public void setCliente(Cliente c) {
       if (c != null) {
            String extraInfo = "";
            // Si tiene fidelidad, lo mostramos visualmente en el nombre
            if (porcentajeFidelidad > 0) {
                extraInfo = "<br><font color='#27ae60' size='2'>★ Cliente Frecuente (5% OFF)</font>";
            }
            lblClienteNombre.setText("<html><div style='text-align: right;'>Cliente:<br><b>" + c.getNombreCompleto() + "</b>" + extraInfo + "</div></html>");
            lblClienteNombre.setForeground(Tema.OBSIDIAN);
        } else {
            lblClienteNombre.setText("<html><div style='text-align: right;'>Cliente:<br><b>Consumidor Final</b></div></html>");
            lblClienteNombre.setForeground(Color.GRAY);
        }
    }

 private void initUI() {
        // 1. CABECERA
        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(new EmptyBorder(25, 25, 15, 25));
        
        // Título Grande
        JLabel lblTitulo = new JLabel("Tu Carrito");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Tema.OBSIDIAN);
        
        // Info Cliente
        JPanel pCliente = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pCliente.setBackground(Color.WHITE);
        
        JLabel iconUser = new JLabel("👤"); 
        iconUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        lblClienteNombre = new JLabel("Cliente: ...");
        lblClienteNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblClienteNombre.setHorizontalAlignment(SwingConstants.RIGHT);
        
        pCliente.add(lblClienteNombre);
        pCliente.add(iconUser);
        
        cabecera.add(lblTitulo, BorderLayout.WEST);
        cabecera.add(pCliente, BorderLayout.EAST);
        
        // 2. LISTA SCROLLABLE
        contenedorItems = new JPanel();
        contenedorItems.setLayout(new BoxLayout(contenedorItems, BoxLayout.Y_AXIS));
        contenedorItems.setBackground(Color.WHITE);
        contenedorItems.setBorder(new EmptyBorder(10, 15, 10, 25));

        JScrollPane scroll = new JScrollPane(contenedorItems);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(380, 300));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);



// Armar panel principal
add(cabecera, BorderLayout.NORTH);
add(scroll, BorderLayout.CENTER);
add(crearFooter(), BorderLayout.SOUTH);
    }

 
 // 3. FOOTER DETALLADO
 private JPanel crearFooter() {
     
JPanel footer = new JPanel();
footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
footer.setBackground(new Color(250, 250, 252)); 
footer.setBorder(new EmptyBorder(10, 20, 15, 20)); // Márgenes reducidos

// --- A. FILA SUPERIOR: CUPÓN (Izq) + TASA (Der) ---
JPanel pFilaSuperior = new JPanel(new BorderLayout());
pFilaSuperior.setBackground(new Color(250, 250, 252));
pFilaSuperior.setBorder(new EmptyBorder(0, 0, 5, 0)); // Poco espacio abajo

// 1. IZQUIERDA: LÓGICA DEL CUPÓN
JPanel pZonaCupon = new JPanel();
pZonaCupon.setLayout(new BoxLayout(pZonaCupon, BoxLayout.Y_AXIS));
pZonaCupon.setOpaque(false);

JLabel lblPregunta = new JLabel("¿Cupón de descuento?");
lblPregunta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
lblPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);

JPanel pInputCupon = new JPanel(new BorderLayout(7, 0));
pInputCupon.setOpaque(false);
pInputCupon.setAlignmentX(Component.LEFT_ALIGNMENT);
pInputCupon.setMaximumSize(new Dimension(220, 25)); // Input más pequeño y compacto

txtCupon = new JTextField();
txtCupon.setPreferredSize(new Dimension(100, 35));
txtCupon.putClientProperty("JTextField.placeholderText", "Código");
Validaciones.forzarMayusculas(txtCupon);

btnAplicarCupon = new JButton("Aplicar");
btnAplicarCupon.setPreferredSize(new Dimension(60, 35));
btnAplicarCupon.setBackground(Tema.OBSIDIAN);
btnAplicarCupon.setForeground(Color.WHITE);
btnAplicarCupon.setFocusPainted(false);
btnAplicarCupon.setBorderPainted(false);
btnAplicarCupon.setFont(new Font("Segoe UI", Font.BOLD, 12));
btnAplicarCupon.setCursor(new Cursor(Cursor.HAND_CURSOR));
btnAplicarCupon.setPreferredSize(new Dimension(80, 35));

pInputCupon.add(txtCupon, BorderLayout.CENTER);
pInputCupon.add(btnAplicarCupon, BorderLayout.EAST);

lblMensajeCupon = new JLabel(" ");
lblMensajeCupon.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Letra pequeña para errores
lblMensajeCupon.setAlignmentX(Component.LEFT_ALIGNMENT);

pZonaCupon.add(lblPregunta);
pZonaCupon.add(pInputCupon);
pZonaCupon.add(lblMensajeCupon);

// 2. DERECHA: BADGE TASA (Alineado a la derecha del todo)
JPanel pZonaTasa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4)); 
pZonaTasa.setOpaque(false);
pZonaTasa.setBorder(new EmptyBorder(10, 0, 0, 0));

lblTasaBadge = new JLabel("Tasa: 0,00 Bs");
lblTasaBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
lblTasaBadge.setForeground(Tema.BLANCO);
lblTasaBadge.setBackground(Tema.OBSIDIAN);
lblTasaBadge.setOpaque(true); 
lblTasaBadge.setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding interno

// Agregamos el badge al panel derecho
pZonaTasa.add(lblTasaBadge);

// Unimos Izquierda y Derecha en la fila superior
pFilaSuperior.add(pZonaCupon, BorderLayout.WEST);
pFilaSuperior.add(pZonaTasa, BorderLayout.EAST); // Usamos EAST para pegarlo a la derecha

footer.add(pFilaSuperior);

// --- SEPARADOR (Lo mantenemos como pediste) ---
footer.add(new JSeparator());
footer.add(Box.createVerticalStrut(5)); // Espacio pequeño

// --- B. FILAS FINANCIERAS (Compactadas) ---
lblSubtotalVal = crearFilaFinanciera(footer, "Subtotal ($):");
// Eliminamos espacio extra entre subtotal y descuento
lblDescuentoVal = crearFilaFinanciera(footer, "Descuentos ($):");
lblDescuentoVal.setForeground(new Color(39, 174, 96)); 

JSeparator sep = new JSeparator();
sep.setMaximumSize(new Dimension(2000, 1));
footer.add(Box.createVerticalStrut(3)); // Espacio mínimo
footer.add(sep);
footer.add(Box.createVerticalStrut(3));

// Total Divisa
lblTotalDivisa = crearFilaFinanciera(footer, "Total Divisa ($):");
lblTotalDivisa.setFont(new Font("Segoe UI", Font.BOLD, 14));
lblTotalDivisa.setForeground(Tema.OBSIDIAN);

footer.add(Box.createVerticalStrut(5)); // Espacio antes de los Bolívares

// --- C. SECCIÓN BOLÍVARES (Compacta) ---
// NOTA: Aquí borré el bloque duplicado de "pTasa" que tenías antes, porque ya está arriba.

lblBaseBs = crearFilaFinanciera(footer, "Base Imponible (Bs):");
lblIvaBs = crearFilaFinanciera(footer, "IVA 16% (Bs):");

footer.add(Box.createVerticalStrut(5));

// Total Final Bolívares
JPanel pTotal = new JPanel(new BorderLayout());
pTotal.setOpaque(false);
JLabel txtTotal = new JLabel("TOTAL Bs");
txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Un poco más pequeño

lblTotalPagarBs = new JLabel("Bs. 0,00");
lblTotalPagarBs.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Ajustado tamaño
lblTotalPagarBs.setForeground(Tema.OBSIDIAN);

pTotal.add(txtTotal, BorderLayout.WEST);
pTotal.add(lblTotalPagarBs, BorderLayout.EAST);
footer.add(pTotal);
footer.add(Box.createVerticalStrut(15)); // Espacio antes de botones

// D. Botones Acción
JPanel pBotones = new JPanel(new GridLayout(1, 2, 10, 0));
pBotones.setOpaque(false);        


btnCancelar= UIFabric.crearBotonTexto("Cancelar", Tema.BADGE_STOCK_BAJO);
btnCancelar.addActionListener(e -> { if (accionCancelar != null) accionCancelar.run(); });

JButton btnConfirmar = UIFabric.crearBotonPrincipal("Confirmar", Tema.OBSIDIAN, Color.WHITE);
btnConfirmar.addActionListener(e -> { if (accionConfirmar != null) accionConfirmar.run(); });

pBotones.add(btnCancelar);
pBotones.add(btnConfirmar);

footer.add(pBotones);

    return footer;
}
    
  public void mostrarMensajeCupon(String mensaje, boolean esError) {
        lblMensajeCupon.setText(mensaje);
        if (esError) {
            lblMensajeCupon.setForeground(new Color(231, 76, 60)); // Rojo
        } else {
            lblMensajeCupon.setForeground(new Color(39, 174, 96)); // Verde
        }
    }
    // Helper para filas del footer (Ej: Subtotal .... $100)
    private JLabel crearFilaFinanciera(JPanel parent, String texto) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(2000, 20));
        JLabel lblTxt = new JLabel(texto);
        lblTxt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTxt.setForeground(Color.GRAY);
        
        JLabel lblVal = new JLabel("$0.00");
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVal.setForeground(Color.DARK_GRAY);
        
        p.add(lblTxt, BorderLayout.WEST);
        p.add(lblVal, BorderLayout.EAST);
        parent.add(p);
        parent.add(Box.createVerticalStrut(3));
        return lblVal;
    }

   public void renderizarPedido(Pedido pedido) {
        
        // 1. Limpiar lista visual
        contenedorItems.removeAll();
        ArrayList<DetalleCompra> detalles = pedido.getDetalles();

        if (detalles.isEmpty()) {
            contenedorItems.setLayout(new GridBagLayout()); // Centrar contenido
            
            contenedorItems.setBackground( Color.WHITE);
            JPanel panelVacio = new JPanel();
            panelVacio.setLayout(new BoxLayout(panelVacio, BoxLayout.Y_AXIS));
            panelVacio.setBackground(Color.WHITE);
            panelVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblIcono = new JLabel("🛒"); 
            lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
            lblIcono.setForeground(new Color(230, 230, 230)); 
            lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblTexto = new JLabel("Tu carrito está vacío");
            lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTexto.setForeground(Color.LIGHT_GRAY);
            lblTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblSub = new JLabel("Agrega productos del catálogo");
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblSub.setForeground(Color.LIGHT_GRAY);
            lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

            panelVacio.add(lblIcono);
            panelVacio.add(Box.createVerticalStrut(10));
            panelVacio.add(lblTexto);
            panelVacio.add(lblSub);
            
            contenedorItems.add(panelVacio);
            configurarInputCupon(false, false, "");
            lblMensajeCupon.setText(" ");
        } else {
            contenedorItems.setLayout(new BoxLayout(contenedorItems, BoxLayout.Y_AXIS));
            
           contenedorItems.setBackground(new Color(240, 240, 245));
            // --- LÓGICA DE CUPÓN ---
            // Limpiamos listeners previos para evitar duplicados al repintar
            for(java.awt.event.ActionListener al : btnAplicarCupon.getActionListeners()) {
                btnAplicarCupon.removeActionListener(al);
            }

            
            
            if (pedido.getMontoDescuentoCupon().compareTo(BigDecimal.ZERO) > 0) {
                // ESTADO: CUPÓN APLICADO -> MODO "QUITAR"
                txtCupon.setEnabled(false);
                txtCupon.setText(pedido.getCodigoCuponAplicado());
                btnAplicarCupon.setEnabled(true);
                btnAplicarCupon.setText("Quitar");
                btnAplicarCupon.setBackground(new Color(231, 76, 60)); // Rojo
                
                btnAplicarCupon.addActionListener(e -> {
                    if(accionQuitarCupon != null) accionQuitarCupon.run();
                });
            } else {
                // ESTADO: SIN CUPÓN -> MODO "APLICAR"
                txtCupon.setEnabled(true);
                // Si el campo tiene texto (del intento anterior), no lo borramos a menos que sea necesario
                if (pedido.getCodigoCuponAplicado() == null && txtCupon.getText().isEmpty()) {
                     txtCupon.setText("");
                }
                btnAplicarCupon.setEnabled(true);
                btnAplicarCupon.setText("Aplicar");
                btnAplicarCupon.setBackground(Tema.OBSIDIAN); // Negro
                
                btnAplicarCupon.addActionListener(e -> {
                    if(accionAplicarCupon != null) accionAplicarCupon.accept(txtCupon.getText());
                });
            }

            // Renderizar items
            for (int i = 0; i < detalles.size(); i++) {
                contenedorItems.add(crearFilaItem(detalles.get(i), i));
                contenedorItems.add(Box.createVerticalStrut(10)); 
            }
        }
        
        // 2. ACTUALIZAR TOTALES
        lblSubtotalVal.setText(Formato.dinero(pedido.getSubTotal().doubleValue()));
        
        double totalDescuentos = pedido.getTotalDescuentosDouble();
        if (totalDescuentos > 0) {
            lblDescuentoVal.setText("-" + Formato.dinero(totalDescuentos));
            lblDescuentoVal.setForeground(new Color(39, 174, 96));
            
            String tooltipHTML = "<html><body style='background-color: #FFFFE0; padding: 5px;'>"
                    + "<b>Desglose de Ahorro:</b><br>"
                    + "<table border='0' cellpadding='2'>"
                    + pedido.getResumenDescuentosHTML() 
                    + "</table></body></html>";
                    
            lblDescuentoVal.setToolTipText(tooltipHTML);
        } else {
             lblDescuentoVal.setText("$0.00");
             lblDescuentoVal.setToolTipText(null);
        }

       double totalBs = pedido.getTotalLocalDouble();
double tasa = pedido.getTasaCambioSnapshot();
double totalUSD = pedido.getTotalDivisaDouble(); 

// 1. Mostrar Total Divisa
lblTotalDivisa.setText(String.format("Ref. Pagable: $%,.2f", totalUSD));

// 2. ACTUALIZAR EL BADGE DE ARRIBA (Junto al cupón)
lblTasaBadge.setText(String.format("Tasa BCV: %,.2f Bs", tasa));
if (tasa <= 0) lblTasaBadge.setVisible(false);
else lblTasaBadge.setVisible(true);

// 3. Desglose en Bolívares (Ingeniería inversa para visualización)
double baseBs = totalBs / (1 + pedido.getPorcentajeIvaSnapshot()); 
double ivaBs = totalBs - baseBs;

lblBaseBs.setText(String.format("Bs. %,.2f", baseBs));
lblIvaBs.setText(String.format("Bs. %,.2f", ivaBs));
lblTotalPagarBs.setText(String.format("Bs. %,.2f", totalBs));

contenedorItems.revalidate();
contenedorItems.repaint();
    }
    private void configurarInputCupon(boolean habilitarInput, boolean habilitarBoton, String texto) {
        txtCupon.setEnabled(habilitarInput);
        btnAplicarCupon.setEnabled(habilitarBoton);
        if(!texto.isEmpty()) txtCupon.setText(texto);
        else if(!habilitarInput && texto.isEmpty()) txtCupon.setText(""); // Limpiar si deshabilitamos sin texto
    }

    // --- DISEÑO DE LA FILA (ITEM DEL CARRITO) ---
    private JPanel crearFilaItem(DetalleCompra det, int index) {
      // Usamos nuestro PanelRedondeado de utilidades
        PanelRedondeado card = new PanelRedondeado(Color.WHITE, 20);
        card.setLayout(new BorderLayout(10, 0)); 
        card.setMaximumSize(new Dimension(2000, 80));
        card.setBorder(new EmptyBorder(8, 8, 8, 12));
         
        // 1. IMAGEN
        JLabel lblImg = new JLabel();
        lblImg.setPreferredSize(new Dimension(70, 70));
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = ImagenUtils.cargarImagenEscalada(det.getProducto().getRutaImagen(), 60, 60);
        if (icon != null) lblImg.setIcon(icon);
        else lblImg.setText("📷");
        
        // 2. INFO CENTRO
        JPanel centro = new JPanel(new GridLayout(3, 1));
        centro.setOpaque(false); 
        
        JLabel lblNombre = new JLabel(det.getProducto().getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JPanel pBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pBadges.setOpaque(false);
        Producto p = det.getProducto();
        
       int stockTotal = p.getStock();
    int enCarrito = det.getCantidad();
    int restantes = stockTotal - enCarrito;
    
    // Creamos la etiqueta
    JLabel badgeStock = new JLabel();
    badgeStock.setOpaque(true);
    badgeStock.setFont(new Font("Segoe UI", Font.BOLD, 10));
    badgeStock.setBorder(new EmptyBorder(2, 5, 2, 5));

    if (restantes <= 0) {
        // CASO 1: YA NO QUEDA NADA MÁS
        badgeStock.setText("¡Máximo!"); // O "Agotado"
        badgeStock.setBackground(new Color(255, 235, 238)); // Fondo Rojo suave
        badgeStock.setForeground(new Color(231, 76, 60));   // Texto Rojo
    } else {
        
        // CASO 2: AÚN PUEDES AGREGAR MÁS
        badgeStock.setText("Quedan: " + restantes);
        badgeStock.setBackground(new Color(240, 240, 245)); // Gris
        badgeStock.setForeground(Color.GRAY);
    }

    pBadges.add(badgeStock);
    pBadges.add(Box.createHorizontalStrut(5));
        // 1. Badge Oferta Flash
if (p.isEnOfertaFlash()) {
    JLabel badgeFlash = new JLabel("FLASH");
    badgeFlash.setOpaque(true);
    badgeFlash.setBackground(new Color(255, 152, 0)); // Naranja
    badgeFlash.setForeground(Color.WHITE);
    badgeFlash.setFont(new Font("Segoe UI", Font.BOLD, 12));
    badgeFlash.setBorder(new EmptyBorder(2, 5, 2, 5));
    pBadges.add(badgeFlash);
    pBadges.add(Box.createHorizontalStrut(5));
}

// 2. Badge Mayoreo
if (p.isTieneDescuentoVolumen() && det.getCantidad() >= p.getCantidadParaDescuento()) {
    pBadges.add(Box.createHorizontalStrut(5));
   String txtMayoreo = "MAYOR-" + (int)p.getPorcentajeDescuentoVolumen() + "%"; 
            pBadges.add(UIFabric.crearBadge(txtMayoreo, Tema.BADGE_MAYORISTA, Color.WHITE));
    
}
        
       double precioOriginal = det.getProducto().getPrecio();
double precioPagado = det.getProducto().getPrecio(); // Por defecto

// Si es oferta flash, el precio unitario real baja
if (det.getProducto().isEnOfertaFlash()) {
    precioPagado = det.getProducto().getPrecioOferta();
}

// FORMATO DE PRECIO UNITARIO (Centro)


JLabel lblUnitario = new JLabel(Formato.precioTachado(precioOriginal, precioPagado));
lblUnitario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUnitario.setForeground(Color.GRAY);

        centro.add(lblNombre);
        centro.add(pBadges);
        centro.add(lblUnitario);

        // 3. CONTROLES (Derecha)
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        
        JLabel lblSubtotal = new JLabel(Formato.dinero(det.getSubTotal()));
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtotal.setForeground(Tema.OBSIDIAN);
        lblSubtotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0)); // Más espacio entre botones
        btns.setOpaque(false);
        
      
        JButton btnMenos = UIFabric.crearBotonIcono("-");
        btnMenos.addActionListener(e -> { if(accionModificarCantidad!=null) accionModificarCantidad.accept(index, -1); });
        
        JLabel lblCant = new JLabel(String.valueOf(det.getCantidad()));
        lblCant.setPreferredSize(new Dimension(25, 24));
        lblCant.setHorizontalAlignment(SwingConstants.CENTER);
        lblCant.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JButton btnMas = UIFabric.crearBotonIcono("+");
        btnMas.addActionListener(e -> { if(accionModificarCantidad!=null) accionModificarCantidad.accept(index, 1); });
         
        JButton btnDel = UIFabric.crearBotonIcono("X");
        // El de eliminar lo hacemos un poco rojizo al pasar el mouse (opcional) o mantener lavanda
        btnDel.addActionListener(e -> { if(accionEliminar!=null) accionEliminar.accept(index); });

        btns.add(btnMenos);
        btns.add(lblCant);
        btns.add(btnMas);
        btns.add(Box.createHorizontalStrut(8));
        btns.add(btnDel);
        
        derecha.add(lblSubtotal, BorderLayout.NORTH);
        derecha.add(btns, BorderLayout.SOUTH);

        card.add(lblImg, BorderLayout.WEST);
        card.add(centro, BorderLayout.CENTER);
        card.add(derecha, BorderLayout.EAST);

        return card;
    }
    
    
    


    
}