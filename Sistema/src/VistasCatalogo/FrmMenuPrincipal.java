package VistasCatalogo;

import Utilidades.UI.Tema;
import Utilidades.UI.UIFabric;
import Utilidades.UI.PanelRedondeado;
import Servicios.GestionPedidos;
import Servicios.GestionCliente;
import Servicios.GestionProducto;
import Controladores.ControladorVenta;
import Controladores.ControladorInventario;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import AcccesoDatosCatalogo.*;
import AcccesoDatosCatalogo.repositorio.RepositorioConfiguracionTXT;
import EntidadesCatalogo.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class FrmMenuPrincipal extends JFrame {
    private JButton btnCatalogo;
    private JButton btnPedido;
    private JButton btnHistorial;
    private JButton btnCambiarCliente;

    // --- CEREBRO (BACKEND) ---
    private final GestionProducto gp;
    private final GestionPedidos gped;
    private final Configuracion config;

    // --- ESTADO ---
    private boolean MODO_ADMIN = false;
    
    // --- COMPONENTES FILTROS ---
    private JPanel panelFiltros; 
    private JPanel panelContenidoFiltros; // CRÍTICO: Variable de clase para CardLayout
    
    // Filtros Catálogo
    private JComboBox<String> cmbCategoriaFiltro;
    private JTextField txtPrecioMin, txtPrecioMax;
    private JCheckBox chkOfertas, chkMayorista, chkInactivos, chkSinStock;
    private JLabel lblSeparador;
    
    // Filtros Historial
    private JComboBox<String> cmbEstadoHist;
    private JComboBox<String> cmbMetodoHist;
    private JComboBox<String> cmbRangoFecha;
    private JTextField txtBuscar;

    // --- COMPONENTES VISUALES ---
    private JPanel panelCentralCard; 
    private CardLayout cardLayout;
    private JPanel panelDerechoCard;
    private CardLayout cardLayoutDerecho;
    
    // Paneles Modulares
    private PanelCatalogo panelCatalogo;
    private PanelCarrito panelCarrito;
    private PanelHistorial panelHistorial;
    private PanelFormulario panelFormulario;
    
    // Controladores
    private ControladorInventario controladorInventario; 
    private final ControladorVenta controladorVenta;
 
    
    public FrmMenuPrincipal(GestionProducto gp, GestionCliente gc, GestionPedidos gped) {
        this.gp = gp;
        this.gped = gped;
       RepositorioConfiguracionTXT repoConfig = new RepositorioConfiguracionTXT();
this.config = repoConfig.cargarConfiguracion();

        configurarVentana();
        initUI(); 
        
        // 1. INICIALIZAR CONTROLADOR VENTA
       controladorVenta = new ControladorVenta(this, gped, gc, gp, config, panelCarrito, panelCatalogo);
                                                
        // 2. CONECTAR EVENTOS CARRITO
        panelCarrito.setEventos(
            controladorVenta::confirmarVenta,        
            controladorVenta::cancelarVenta,
            controladorVenta::aplicarCupon,
            controladorVenta::quitarCupon,
            controladorVenta::modificarCantidadCarrito, 
            controladorVenta::eliminarItemCarrito
        );
        
        // 3. PANTALLA INICIAL
        cambiarPantalla("VENTA", btnPedido);
    }
    
    private void configurarVentana() {
        setSize(1366, 768);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FONDO_GLOBAL);
        setLayout(new BorderLayout());
    }

    private void initUI() {
        // 1. BARRA LATERAL
        add(crearBarraLateral(), BorderLayout.WEST);
        
        // 2. PANEL DERECHO
        cardLayoutDerecho = new CardLayout();
        panelDerechoCard = new JPanel(cardLayoutDerecho);
        panelDerechoCard.setPreferredSize(new Dimension(420, 0));
        
        panelCarrito = new PanelCarrito();
        panelFormulario = new PanelFormulario();
        panelHistorial = new PanelHistorial(this, gped);
        
        // 3. CONEXIÓN BACKEND
        double tasaDelDia = config.getTasaCambio();
        panelCatalogo = new PanelCatalogo(gp, this::clickEnProducto, tasaDelDia);
        
        controladorInventario = new ControladorInventario(this, gp, panelCatalogo, panelFormulario);

        // Eventos Formulario
        panelFormulario.setEventos(
            () -> { 
                controladorInventario.guardar();    
                ejecutarFiltrosCatalogo(); // Refrescamos lista                  
            }, 
            () -> { 
                controladorInventario.eliminar();   
                ejecutarFiltrosCatalogo();                  
            }, 
            controladorInventario::limpiar
        );

        panelDerechoCard.add(panelCarrito, "CARRITO");
        panelDerechoCard.add(panelFormulario, "FORMULARIO");
        add(panelDerechoCard, BorderLayout.EAST);

        // 4. ZONA CENTRAL
        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBackground(Tema.LAVENDER);
        contenedorCentral.setBorder(new EmptyBorder(20, 20, 20, 20)); 

        PanelRedondeado panelBlanco = new PanelRedondeado(Color.WHITE, 35);
        panelBlanco.setLayout(new BorderLayout());

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false); 
        
        topContainer.add(crearBarraSuperior());
        topContainer.add(crearPanelFiltros()); // Aquí se inicializan los filtros
        
        panelBlanco.add(topContainer, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        panelCentralCard = new JPanel(cardLayout);
        panelCentralCard.setOpaque(false);

        JScrollPane scrollCat = new JScrollPane(panelCatalogo);
        scrollCat.setBorder(null);
        scrollCat.getVerticalScrollBar().setUnitIncrement(16);
        scrollCat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCat.getViewport().setBackground(Color.WHITE);

        panelCentralCard.add(scrollCat, "CATALOGO");
        panelCentralCard.add(panelHistorial, "HISTORIAL");

        panelBlanco.add(panelCentralCard, BorderLayout.CENTER);
        contenedorCentral.add(panelBlanco, BorderLayout.CENTER);
        
        add(contenedorCentral, BorderLayout.CENTER);
    }

    // --- LÓGICA DE CONTROL ---
    private void clickEnProducto(Producto p) {
        if (MODO_ADMIN) {
            controladorInventario.cargarProductoParaEditar(p);
        } else {
            controladorVenta.agregarAlCarrito(p);
        }
    }

    // --- BARRA LATERAL ---
   private JPanel crearBarraLateral() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Tema.OBSIDIAN);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel lblLogo = new JLabel(" Menú");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(Tema.LAVENDER); 
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(60));

        // Botones usando el nuevo método de UIFabric
        btnCatalogo = UIFabric.crearBotonNavegacion("Catálogo", false);
        btnCatalogo.addActionListener(e -> cambiarPantalla("GESTION", btnCatalogo)); 
        sidebar.add(btnCatalogo);
        sidebar.add(Box.createVerticalStrut(15));

        btnPedido = UIFabric.crearBotonNavegacion("Pedido", true); // Activo por defecto
        btnPedido.addActionListener(e -> cambiarPantalla("VENTA", btnPedido));
        sidebar.add(btnPedido);
        sidebar.add(Box.createVerticalStrut(15));

        btnHistorial = UIFabric.crearBotonNavegacion("Historial", false);
        btnHistorial.addActionListener(e -> cambiarPantalla("HISTORIAL", btnHistorial));
        sidebar.add(btnHistorial);
        sidebar.add(Box.createVerticalStrut(15));
        
        sidebar.add(Box.createVerticalGlue());

        JButton btnSalir = UIFabric.crearBotonNavegacion("Cerrar Sesión", false);
        btnSalir.addActionListener(e -> System.exit(0));
        sidebar.add(btnSalir);

        return sidebar;    
    }
    // --- MÉTODO MAESTRO DE NAVEGACIÓN ---
    private void cambiarPantalla(String modo, JButton botonPresionado) {
        if(panelFiltros != null) panelFiltros.setVisible(false);        

        // Actualizar estado visual de botones (Usando helper de UIFabric)
        UIFabric.setEstadoBotonNavegacion(btnCatalogo, false);
        UIFabric.setEstadoBotonNavegacion(btnPedido, false);
        UIFabric.setEstadoBotonNavegacion(btnHistorial, false);
        UIFabric.setEstadoBotonNavegacion(botonPresionado, true);
        
        // Reset Buscador
        if (txtBuscar != null) {
            txtBuscar.setText("Buscar...");
            txtBuscar.setForeground(Color.GRAY);
        }
        
        CardLayout clFiltros = (panelContenidoFiltros != null) ? (CardLayout) panelContenidoFiltros.getLayout() : null;

        switch (modo) {
            case "VENTA":
                MODO_ADMIN = false;
                panelCatalogo.setModoAdmin(false);
                cardLayout.show(panelCentralCard, "CATALOGO");
                
                panelDerechoCard.setVisible(true);
                cardLayoutDerecho.show(panelDerechoCard, "CARRITO");
                if (btnCambiarCliente != null) btnCambiarCliente.setVisible(true);
                
                if(clFiltros != null) clFiltros.show(panelContenidoFiltros, "FILTROS_CATALOGO");
                
                configurarFiltrosVisibles(false);
                controladorInventario.limpiar(); 
                limpiarFiltros();
                break;
                
            case "GESTION":
                MODO_ADMIN = true;
                panelCatalogo.setModoAdmin(true);
                cardLayout.show(panelCentralCard, "CATALOGO");
                
                panelDerechoCard.setVisible(true);
                cardLayoutDerecho.show(panelDerechoCard, "FORMULARIO");
                
                if(clFiltros != null) clFiltros.show(panelContenidoFiltros, "FILTROS_CATALOGO");
                if (btnCambiarCliente != null) btnCambiarCliente.setVisible(false);
                
                configurarFiltrosVisibles(true);
                limpiarFiltros();
                break;
                
            case "HISTORIAL":
                panelDerechoCard.setVisible(false);
                cardLayout.show(panelCentralCard, "HISTORIAL");
                
                if(clFiltros != null) clFiltros.show(panelContenidoFiltros, "FILTROS_HISTORIAL");
                if (btnCambiarCliente != null) btnCambiarCliente.setVisible(false);
                if (panelHistorial != null) panelHistorial.cargarDatos();
                break;
        }
    }
    private void configurarFiltrosVisibles(boolean visible) {
        if(chkInactivos != null) {
            chkInactivos.setVisible(visible);
            chkSinStock.setVisible(visible);
            lblSeparador.setVisible(visible);
            if(!visible) {
                chkInactivos.setSelected(false);
                chkSinStock.setSelected(false);
            }
        }
    }

    private void limpiarFiltros() {
        if (txtBuscar != null) {
            txtBuscar.setText("Buscar...");
            txtBuscar.setForeground(Color.GRAY);
            txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                 new LineBorder(Color.WHITE, 1, true), 
                 new EmptyBorder(5, 15, 5, 15)));
        }
             
        if (cmbCategoriaFiltro != null) cmbCategoriaFiltro.setSelectedIndex(0);
        if (txtPrecioMin != null) txtPrecioMin.setText("");
        if (txtPrecioMax != null) txtPrecioMax.setText("");
        
        if (chkOfertas != null) {
            chkOfertas.setSelected(false);
            chkMayorista.setSelected(false);
            chkInactivos.setSelected(false);
            chkSinStock.setSelected(false);
        }
        
        // Ejecutar filtro limpio (Catálogo por defecto)
        ejecutarFiltrosCatalogo();
    }

    // --- BARRA SUPERIOR ---
    private JPanel crearBarraSuperior() {
        // Usamos PanelRedondeado en lugar de clase anónima
        PanelRedondeado barra = new PanelRedondeado(Tema.OBSIDIAN, 30);
        barra.setLayout(new BorderLayout());
        barra.setBorder(new EmptyBorder(20, 30, 15, 30));
        
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIzquierdo.setOpaque(false);
         
        // Usamos el nuevo buscador de UIFabric
        txtBuscar = UIFabric.crearCampoBusqueda("Buscar...");
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
               if (btnHistorial.getModel().isSelected()) { // Usamos estado del modelo
                   ejecutarFiltrosHistorial();
               } else {
                   ejecutarFiltrosCatalogo();
               }
            }
        });
        
        JButton btnFiltro = UIFabric.crearBotonPrincipal("Filtros", Tema.LAVENDER, Tema.OBSIDIAN);
        btnFiltro.setPreferredSize(new Dimension(100, 35));
        btnFiltro.addActionListener(e -> {
            boolean estado = !panelFiltros.isVisible();
            panelFiltros.setVisible(estado);
            barra.repaint(); 
            panelFiltros.getParent().revalidate();
        });
        
        panelIzquierdo.add(txtBuscar);
        panelIzquierdo.add(btnFiltro);
       
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        panelDerecho.setOpaque(false);

        btnCambiarCliente = UIFabric.crearBotonPrincipal("Cambiar", Tema.LAVENDER, Tema.OBSIDIAN);
        btnCambiarCliente.setPreferredSize(new Dimension(100, 35)); 
        btnCambiarCliente.addActionListener(e -> controladorVenta.abrirSeleccionCliente());

        panelDerecho.add(btnCambiarCliente);

        barra.add(panelIzquierdo, BorderLayout.WEST);
        barra.add(panelDerecho, BorderLayout.EAST);

        return barra;
    }


    // --- PANEL FILTROS (DINÁMICO CON CARDLAYOUT) ---
private JPanel crearPanelFiltros() {
        panelFiltros = new JPanel(new BorderLayout());
        panelFiltros.setBackground(Tema.OBSIDIAN);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(100, 100, 100)), 
            new EmptyBorder(10, 30, 10, 30)
        ));
        panelFiltros.setVisible(false); 

        panelContenidoFiltros = new JPanel(new CardLayout());
        panelContenidoFiltros.setOpaque(false);

        // 1. FILTROS CATÁLOGO
        JPanel pCatalogo = new JPanel(new GridLayout(2, 1, 0, 5));
        pCatalogo.setOpaque(false);

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        fila1.setOpaque(false);
        fila1.add(UIFabric.crearLabelBlanco("Categoría:"));
        
     String[] cats = {
         "Todas",
    "Víveres",       // Harina, Arroz, Pasta, Aceite...
    "Bebidas",       // Refrescos, Aguas, Jugos...
    "Refrigerados",  // Lácteos, Margarina...
    "Charcutería",   // Jamón, Queso, Salchichas...
    "Carnicería",    // Carne, Pollo, Pescado...
    "Snacks",        // Papas, Dulces, Galletas...
    "Enlatados",     // Atún, Maíz, Salsas...
    "Panadería",     // Pan, Tortas...
    "Limpieza"       // Jabón, Detergente (Suele venderse junto a comida)
};
        cmbCategoriaFiltro = new JComboBox<>(cats);
        cmbCategoriaFiltro.setBackground(Color.WHITE);
        cmbCategoriaFiltro.setPreferredSize(new Dimension(150, 25));
        cmbCategoriaFiltro.addActionListener(e -> ejecutarFiltrosCatalogo());
        fila1.add(cmbCategoriaFiltro);

        fila1.add(Box.createHorizontalStrut(10));
        fila1.add(UIFabric.crearLabelBlanco("Precio:"));
        
        txtPrecioMin = new JTextField(4);
        txtPrecioMax = new JTextField(4);
        KeyAdapter k = new KeyAdapter() { @Override public void keyReleased(KeyEvent e) { ejecutarFiltrosCatalogo(); }};
        txtPrecioMin.addKeyListener(k);
        txtPrecioMax.addKeyListener(k);
        
        fila1.add(txtPrecioMin);
        fila1.add(UIFabric.crearLabelBlanco("-"));
        fila1.add(txtPrecioMax);
        fila1.add(Box.createHorizontalStrut(15));
        
        JButton btnLimpiar = UIFabric.crearBotonPrincipal("Limpiar", Tema.LAVENDER, Tema.OBSIDIAN);
        btnLimpiar.setPreferredSize(new Dimension(90, 25));
        btnLimpiar.setBackground(new Color(220, 220, 220));
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        fila1.add(btnLimpiar);
        
        pCatalogo.add(fila1);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        fila2.setOpaque(false);
        
        chkOfertas = UIFabric.crearCheckOscuro("Ofertas Flash");
        chkMayorista = UIFabric.crearCheckOscuro("Mayorista");
        chkInactivos = UIFabric.crearCheckOscuro("Inactivos");
        chkSinStock = UIFabric.crearCheckOscuro("Agotados");
        
        ActionListener al = e -> ejecutarFiltrosCatalogo();
        chkOfertas.addActionListener(al);
        chkMayorista.addActionListener(al);
        chkInactivos.addActionListener(al);
        chkSinStock.addActionListener(al);
        
        fila2.add(chkOfertas);
        fila2.add(chkMayorista);
        
        lblSeparador = new JLabel("|");
        lblSeparador.setForeground(Color.GRAY);
        lblSeparador.setBorder(new EmptyBorder(0, 10, 0, 10));
        lblSeparador.setVisible(false);
        fila2.add(lblSeparador);
        
        chkInactivos.setVisible(false); 
        chkSinStock.setVisible(false);
        fila2.add(chkInactivos);
        fila2.add(chkSinStock);
        
        pCatalogo.add(fila2);

        // 2. FILTROS HISTORIAL (EFICIENTE)
        JPanel pHistorial = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pHistorial.setOpaque(false);
        
        // A. COMBO DE RANGOS (Solución Eficiente)
        pHistorial.add(UIFabric.crearLabelBlanco("Fecha:"));
        String[] rangos = {"Todo el Historial", "Hoy", "Ayer", "Últimos 7 Días", "Este Mes", "Mes Pasado"};
        cmbRangoFecha = new JComboBox<>(rangos);
        cmbRangoFecha.setPreferredSize(new Dimension(140, 30));
        cmbRangoFecha.setBackground(Color.WHITE);
        cmbRangoFecha.addActionListener(e -> ejecutarFiltrosHistorial()); 
        pHistorial.add(cmbRangoFecha);

        pHistorial.add(Box.createHorizontalStrut(10));
        
        pHistorial.add(UIFabric.crearLabelBlanco("Estado:"));
        cmbEstadoHist = new JComboBox<>(new String[]{"Todos", "PAGADO", "ANULADO"});
        cmbEstadoHist.setBackground(Color.WHITE);
        cmbEstadoHist.addActionListener(e -> ejecutarFiltrosHistorial());
        pHistorial.add(cmbEstadoHist);
        
        pHistorial.add(Box.createHorizontalStrut(10));
        
        pHistorial.add(UIFabric.crearLabelBlanco("Método:"));
        cmbMetodoHist = new JComboBox<>(new String[]{"Todos", "Zelle", "Pago Móvil", "Punto Venta", "Efectivo ($)"});
        cmbMetodoHist.setBackground(Color.WHITE);
        cmbMetodoHist.addActionListener(e -> ejecutarFiltrosHistorial());
        pHistorial.add(cmbMetodoHist);
        
        panelContenidoFiltros.add(pCatalogo, "FILTROS_CATALOGO");
        panelContenidoFiltros.add(pHistorial, "FILTROS_HISTORIAL");
        
        panelFiltros.add(panelContenidoFiltros, BorderLayout.CENTER);
        
        return panelFiltros;
    }

    // --- MÉTODOS DE FILTRADO ---
    
    private void ejecutarFiltrosCatalogo() {
        if(panelCatalogo == null) return;

        String texto = obtenerTextoBuscador();
        String cat = (String) cmbCategoriaFiltro.getSelectedItem();
        
        double min = 0, max = 999999;
        try { min = Double.parseDouble(txtPrecioMin.getText()); } catch(Exception ex){}
        try { max = Double.parseDouble(txtPrecioMax.getText()); } catch(Exception ex){}
        if(max == 0) max = 999999; 

        boolean soloOfertas = chkOfertas.isSelected();
        boolean soloMayorista = chkMayorista.isSelected();
        boolean verInactivos = MODO_ADMIN && chkInactivos.isSelected();
        boolean verSinStock = MODO_ADMIN && chkSinStock.isSelected();

        panelCatalogo.filtrarAvanzado(texto, cat, min, max, soloOfertas, soloMayorista, verInactivos, verSinStock);
    }






private void ejecutarFiltrosHistorial() {
        if (panelHistorial == null) return;
        
        String texto = obtenerTextoBuscador();
        String estado = (String) cmbEstadoHist.getSelectedItem();
        String metodo = (String) cmbMetodoHist.getSelectedItem();
        
        // Calcular fechas automáticamente
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = null;
        LocalDate fin = null;
        
        String rango = (String) cmbRangoFecha.getSelectedItem();
        
        switch (rango) {
            case "Hoy" -> {
                inicio = hoy; fin = hoy;
            }
            case "Ayer" -> {
                inicio = hoy.minusDays(1); fin = hoy.minusDays(1); 
            }
            case "Últimos 7 Días" -> {
                inicio = hoy.minusDays(6); fin = hoy;
            }
            case "Este Mes" -> {
                inicio = hoy.withDayOfMonth(1); fin = hoy.withDayOfMonth(hoy.lengthOfMonth());
            }
            case "Mes Pasado" -> {
                LocalDate mesPasado = hoy.minusMonths(1);
                inicio = mesPasado.withDayOfMonth(1);
                fin = mesPasado.withDayOfMonth(mesPasado.lengthOfMonth());
            }
            default -> {
                inicio = null;
                fin = null;
                // "Todo el Historial"
            }
        }
        
        panelHistorial.filtrar(texto, estado, metodo, inicio, fin);
    }
private String obtenerTextoBuscador() {
        if (txtBuscar == null) return "";
        String valor = txtBuscar.getText();
        if (valor.equals("Buscar...") || txtBuscar.getForeground().equals(Color.GRAY)) {
            return "";
        }
        return valor;
    }
    
}