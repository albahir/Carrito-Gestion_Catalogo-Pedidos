package VistasCatalogo;


import Utilidades.*;
import EntidadesCatalogo.Producto;
import java.awt.*;
import java.awt.event.*; 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
 

public class PanelFormulario extends JPanel {

    // Colores del Tema
   private final Color COLOR_FONDO_INPUT = Color.WHITE; // Antes era grisáceo, ahora BLANCO PURO
   
    private final Color COLOR_PRECIO_FINAL = new Color(0, 150, 0);
    
    // Componentes
    private JTextField txtNombre, txtPrecio;
    private final JComboBox<String> cmbCategoria; 
    private JSpinner spnStock;              
    private final JTextArea txtDescripcion;
    private final JCheckBox chkActivo;
    private final JLabel lblFechaRegistro;
    // Marketing
    private JCheckBox chkOfertaFlash;
    private JSlider sliderDescuento;
    private final JLabel lblPorcentajeFlash;
    private JLabel lblPrecioFinalFlash;
    private JSpinner spnFechaFin;
    
    // Volumen
    private JCheckBox chkDescVolumen;
    private JSpinner spnCantVolumen;
    private JSlider sliderVolumen;
    private final JLabel lblPorcentajeVol;
    private JLabel lblPrecioFinalVol;
    
    // Imagen
    private JLabel lblImagenPreview;
    private String rutaImagenActual = ""; 
    
    // Variables ocultas
    private String skuAuto = ""; 
    private final JButton btnGuardar;
    
    private final JButton btnEliminar;
    private final JButton btnLimpiar;

    public PanelFormulario() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // 1. AJUSTE CRÍTICO: Márgenes laterales reducidos a 20px
        // Esto permite que el panel sea estrecho (380px) sin aplastar el contenido.
        setBorder(new EmptyBorder(15, 20, 15, 20)); 

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setMaximumSize(new Dimension(2000, 120)); 
        
        // 1. Panel de Imagen (Izquierda)
        lblImagenPreview = new JLabel("<html><center>Subir<br>Foto</center></html>", SwingConstants.CENTER);
        lblImagenPreview.setPreferredSize(new Dimension(110, 110)); 
        lblImagenPreview.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblImagenPreview.setOpaque(true);
        lblImagenPreview.setBackground(COLOR_FONDO_INPUT);
        lblImagenPreview.setBorder(new LineBorder(new Color(220, 220, 230), 1, true));
        lblImagenPreview.setCursor(new Cursor(Cursor.HAND_CURSOR));
        activarDragAndDrop();
        
        // 2. Panel de Títulos (Centro)
        JPanel pnlTitulos = new JPanel(new GridLayout(2, 1)); 
        pnlTitulos.setBackground(Color.WHITE);
        pnlTitulos.setBorder(new EmptyBorder(0, 15, 0, 0)); 
        
        JLabel lblTitulo = new JLabel("Gestión Catalogo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Tema.OBSIDIAN);
        
        JLabel lblSub = new JLabel("Detalle Producto");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        
        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(lblSub);

        header.add(lblImagenPreview, BorderLayout.WEST);
        header.add(pnlTitulos, BorderLayout.CENTER);

        // --- BODY ---
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.add(Box.createVerticalStrut(20));
        
        // Nombre
        txtNombre = new JTextField();
        JPanel pnlNombre = UIFabric.crearPanelCampo("Nombre del Producto", txtNombre);
        pnlNombre.setMaximumSize(new Dimension(2000, 55)); 
        txtNombre.setBackground(Tema.BLANCO);
        form.add(pnlNombre);
        form.add(Box.createVerticalStrut(10));
        
        // Precio / Stock / Categoria (Compacto)
       JPanel rowDoble = new JPanel(new GridLayout(1, 2, 15, 0)); 
        rowDoble.setBackground(Color.WHITE);
        rowDoble.setMaximumSize(new Dimension(2000, 55)); 
        
        txtPrecio = new JTextField();
       txtPrecio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Si no es dígito, ni borrar, ni punto/coma -> lo bloqueamos
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != '.' && c != ',') {
                    e.consume(); // Ignorar tecla
                    Toolkit.getDefaultToolkit().beep(); // Sonido de error
                }
                // Evitar doble punto decimal
                if ((c == '.' || c == ',') && txtPrecio.getText().contains(".")) {
                    e.consume();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) { 
                actualizarCalculos(); // Mantenemos tu cálculo en vivo
            }
        });
        // -------------------------------------------------------
        rowDoble.add(UIFabric.crearPanelCampo("Precio Base ($)", txtPrecio));
        
        spnStock = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        spnStock.setBackground(Tema.BLANCO);
        // Asegúrate de que el método se llame igual que en tu UIFabric (styleSpinner o estilizarSpinner)
        try { UIFabric.estilizarSpinner(spnStock); } catch (Exception e) {} 

       
        rowDoble.add(UIFabric.crearPanelCampo("Stock", spnStock));
        
        form.add(rowDoble);
        form.add(Box.createVerticalStrut(10));
        
String[] cats = {
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
      cmbCategoria = new JComboBox<>(cats);
        cmbCategoria.setEditable(true);
        cmbCategoria.setBackground(Tema.BLANCO);
        JPanel pnlCat = UIFabric.crearPanelCampo("Categoría", cmbCategoria);
        pnlCat.setMaximumSize(new Dimension(2000, 55));
        
        form.add(pnlCat);
        form.add(Box.createVerticalStrut(10));
        
        // Descripción
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBackground(Tema.BLANCO);
        JScrollPane scroll = new JScrollPane(txtDescripcion);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE_INPUT));
       JPanel pnlDesc = UIFabric.crearPanelCampo("Descripción", scroll);
        pnlDesc.setMaximumSize(new Dimension(2000, 80));
        form.add(pnlDesc);
        
        form.add(Box.createVerticalStrut(20));
        
        // --- ESTRATEGIA DE PRECIOS ---
       JPanel pnlTituloEst = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTituloEst.setBackground(Color.WHITE);
        pnlTituloEst.setMaximumSize(new Dimension(2000, 25));
        
        JLabel lblEstrategia = new JLabel("Estrategia de Precios");
        lblEstrategia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstrategia.setForeground(Tema.OBSIDIAN);
        pnlTituloEst.add(lblEstrategia);
        
        form.add(pnlTituloEst);
        form.add(Box.createVerticalStrut(10));

        JPanel pnlMarketing = new JPanel(new GridLayout(1, 2, 10, 0)); // 10px Gap
        pnlMarketing.setBackground(Color.WHITE);
        pnlMarketing.setMaximumSize(new Dimension(2000, 130));
        // Panel Flash
        JPanel pnlFlash = crearContenedorEstrategia();
       chkOfertaFlash = UIFabric.crearCheck("Oferta Flash");
        JLabel lblVence = new JLabel("Vence:");
        lblVence.setFont(new Font("Segoe UI", Font.BOLD, 11));
        spnFechaFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnFechaFin, "dd/MM/yyyy HH:mm");
        spnFechaFin.setEditor(timeEditor);
        spnFechaFin.setEnabled(false); // Deshabilitado por defecto
        
        // Panelito para la fecha
        JPanel pFecha = new JPanel(new BorderLayout(5,0));
        pFecha.setOpaque(false);
        pFecha.add(lblVence, BorderLayout.WEST);
        pFecha.add(spnFechaFin, BorderLayout.CENTER);
        JPanel pnlSliderFlash = new JPanel(new BorderLayout());
        pnlSliderFlash.setOpaque(false);
        sliderDescuento = new JSlider(0, 50, 0);
        UIFabric.estilizarSlider(sliderDescuento);
        sliderDescuento.setEnabled(false);
        
        lblPorcentajeFlash = new JLabel("0%", SwingConstants.RIGHT);
        lblPorcentajeFlash.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPorcentajeFlash.setPreferredSize(new Dimension(30, 20));
        pnlSliderFlash.add(sliderDescuento, BorderLayout.CENTER);
        pnlSliderFlash.add(lblPorcentajeFlash, BorderLayout.EAST);
        
        lblPrecioFinalFlash = new JLabel("$0.00", SwingConstants.CENTER);
        lblPrecioFinalFlash.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Un poco más pequeño para que quepa
        lblPrecioFinalFlash.setForeground(Color.LIGHT_GRAY); 
        
        pnlFlash.add(chkOfertaFlash);
        pnlFlash.add(Box.createVerticalStrut(5));
        pnlFlash.add(pFecha);
        pnlFlash.add(Box.createVerticalStrut(5));
        pnlFlash.add(pnlSliderFlash);
        pnlFlash.add(Box.createVerticalGlue());
        pnlFlash.add(lblPrecioFinalFlash);
        
        // Panel Mayorista
       JPanel pnlVol = crearContenedorEstrategia();
       JPanel topVol = new JPanel(new BorderLayout());
       topVol.setOpaque(false);
        
        chkDescVolumen = UIFabric.crearCheck("Mayorista");
       
        
        spnCantVolumen = new JSpinner(new SpinnerNumberModel(6, 2, 100, 1));
        UIFabric.estilizarSpinner(spnCantVolumen);
        spnCantVolumen.setPreferredSize(new Dimension(45, 22)); // Ancho compacto
        spnCantVolumen.setToolTipText("Cantidad mínima");
        
        topVol.add(chkDescVolumen, BorderLayout.WEST);
        topVol.add(spnCantVolumen, BorderLayout.EAST); // Spinner pegado a la derecha
        
        
        JPanel pnlSliderVol = new JPanel(new BorderLayout());
        pnlSliderVol.setOpaque(false);
        sliderVolumen = new JSlider(0, 60, 0);
        UIFabric.estilizarSlider(sliderVolumen);
        lblPorcentajeVol = new JLabel("0%", SwingConstants.RIGHT);
        lblPorcentajeVol.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPorcentajeVol.setPreferredSize(new Dimension(30, 20));
        pnlSliderVol.add(sliderVolumen, BorderLayout.CENTER);
        pnlSliderVol.add(lblPorcentajeVol, BorderLayout.EAST);
        
        lblPrecioFinalVol = new JLabel("$0.00", SwingConstants.CENTER);
        lblPrecioFinalVol.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblPrecioFinalVol.setForeground(Color.LIGHT_GRAY);

        pnlVol.add(topVol);
        pnlVol.add(Box.createVerticalStrut(5));
        pnlVol.add(pnlSliderVol);
        pnlVol.add(Box.createVerticalGlue());
        pnlVol.add(lblPrecioFinalVol);
        
        pnlMarketing.add(pnlFlash);
        pnlMarketing.add(pnlVol);
        form.add(pnlMarketing);
        
        
        // Check estado
        form.add(Box.createVerticalStrut(15));
        chkActivo = UIFabric.crearCheck("Estado del Producto");
       
        chkActivo.setSelected(true);
        
        // Agregamos el check al formulario
        JPanel pnlCheck = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlCheck.setBackground(Color.WHITE);
        pnlCheck.add(chkActivo);
        form.add(pnlCheck);
        form.add(Box.createVerticalStrut(15)); 
        
        lblFechaRegistro = new JLabel("Registrado: " + LocalDate.now());
        lblFechaRegistro.setFont(new Font("Segoe UI", Font.ITALIC, 16)); // Cursiva para que se vea como metadato
        lblFechaRegistro.setForeground(Color.GRAY); // Gris suave
        lblFechaRegistro.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado
        
        form.add(lblFechaRegistro);
        
        // --- CAMBIO CLAVE: Espacio antes de los botones ---
        form.add(Box.createVerticalStrut(20)); // Separación de 30px
        
        // ============================================================
        // 4. FOOTER (AHORA INTEGRADO EN EL FORMULARIO)
        // ============================================================
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        footer.setBackground(Color.WHITE);
        // Alineación a la izquierda (AlignmentX) para que el BoxLayout no lo descuadre
        footer.setAlignmentX(Component.CENTER_ALIGNMENT); 
        footer.setMaximumSize(new Dimension(2000, 50)); // Altura máxima fija
        
        btnLimpiar = UIFabric.crearBotonTexto("Nuevo", Tema.OBSIDIAN);
        btnEliminar = UIFabric.crearBotonTexto("Eliminar", Tema.BADGE_STOCK_BAJO);
        btnGuardar = UIFabric.crearBotonPrincipal("Guardar", Tema.OBSIDIAN, Color.WHITE);

// Ajustes de tamaño
        btnLimpiar.setPreferredSize(new Dimension(110, 40));
        btnEliminar.setPreferredSize(new Dimension(110, 40));
        btnGuardar.setPreferredSize(new Dimension(130, 40)); 
        
        footer.add(btnLimpiar);
        footer.add(btnEliminar);
        footer.add(btnGuardar);
        
        // --- AQUÍ ESTÁ EL TRUCO ---
        // 1. Agregamos el footer AL FORM (no al SOUTH del panel principal)
        form.add(footer);
        
        // 2. Y ahora ponemos el "pegamento" (Glue) AL FINAL.
        // Esto empujará todo el bloque (campos + botones) hacia ARRIBA.
        form.add(Box.createVerticalGlue()); 

        // Agregamos paneles principales
        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        // Eventos
      chkOfertaFlash.addActionListener(e -> {
        boolean st = chkOfertaFlash.isSelected();
        sliderDescuento.setEnabled(st);
        spnFechaFin.setEnabled(st); // <--- ACTIVAR/DESACTIVAR FECHA
        
        // Si se activa, sugerimos fecha de mañana por defecto si está vacía
        if (st) {
             
        }
        
        lblPrecioFinalFlash.setForeground(st ? COLOR_PRECIO_FINAL : Color.LIGHT_GRAY);
        actualizarCalculos();
    });
        sliderDescuento.addChangeListener(e -> actualizarCalculos());
        
        chkDescVolumen.addActionListener(e -> {
            boolean st = chkDescVolumen.isSelected();
            sliderVolumen.setEnabled(st);
            spnCantVolumen.setEnabled(st);
            lblPrecioFinalVol.setForeground(st ? Tema.OBSIDIAN : Color.LIGHT_GRAY);
            actualizarCalculos();
        });
        sliderVolumen.addChangeListener(e -> actualizarCalculos());
        spnCantVolumen.addChangeListener(e -> actualizarCalculos());
    }
    // --- LÓGICA ---
    private void actualizarCalculos() {
        double precioBase = 0.0;
        try {
            precioBase = Double.parseDouble(txtPrecio.getText());
        } catch(Exception e) { precioBase = 0.0; }
        
        if (chkOfertaFlash.isSelected()) {
            int pct = sliderDescuento.getValue();
            double finalFlash = precioBase - (precioBase * pct / 100);
            lblPorcentajeFlash.setText(pct + "%");
            lblPrecioFinalFlash.setText(Formato.dinero(finalFlash));
            lblPrecioFinalFlash.setForeground(COLOR_PRECIO_FINAL);
        } else {
            lblPorcentajeFlash.setText("0%");
            lblPrecioFinalFlash.setText(Formato.dinero(precioBase));
            lblPrecioFinalFlash.setForeground(Color.LIGHT_GRAY);
        }
        
        if (chkDescVolumen.isSelected()) {
            int pct = sliderVolumen.getValue();
            double finalVol = precioBase - (precioBase * pct / 100);
            lblPorcentajeVol.setText(pct + "%");
           lblPrecioFinalVol.setText(Formato.dinero(finalVol));
            lblPrecioFinalVol.setForeground(Tema.OBSIDIAN);
        } else {
            lblPorcentajeVol.setText("0%");
            lblPrecioFinalVol.setText(Formato.dinero(precioBase));
            lblPrecioFinalVol.setForeground(Color.LIGHT_GRAY);
        }
    }
    
    // --- HELPERS VISUALES ---
    private JPanel crearContenedorEstrategia() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_FONDO_INPUT); 
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Tema.BORDE_INPUT, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return p;
    }
    
    
    
   

  
    
   
    private void activarDragAndDrop() {
    }

    // --- GETTERS ---
    public String getNombre() { return txtNombre.getText(); }
    public String getPrecio() { return txtPrecio.getText().trim().replace(",", "."); }
    public String getStock() { return spnStock.getValue().toString(); }
    public String getCategoria() { return cmbCategoria.getSelectedItem().toString(); }
    public String getDescripcion() { return txtDescripcion.getText(); }
    public boolean isActivo() { return chkActivo.isSelected(); }
    public String getRutaImagen() { return rutaImagenActual; }

    public String getSku() { 
        if(skuAuto == null || skuAuto.isEmpty()) {
            String n = getNombre().length() >= 3 ? getNombre().substring(0,3).toUpperCase() : "PRO";
            return n + "-" + (System.currentTimeMillis() % 10000);
        }
        return skuAuto; 
    }
    
    public String getStockMinimo() { return "5"; }

    public boolean isOfertaFlash() { return chkOfertaFlash.isSelected(); }
    public String getPrecioOferta() {
        String txt = lblPrecioFinalFlash.getText().replace("$", "").replace(",", ".");
        return txt;
    }
    
    public boolean isDescVolumen() { return chkDescVolumen.isSelected(); }
    public String getCantVolumen() { return spnCantVolumen.getValue().toString(); }
    public String getPorcVolumen() { return String.valueOf(sliderVolumen.getValue()); }

    // --- SETTERS ---
    public void cargarProducto(Producto p) {
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        spnStock.setValue(p.getStock());
        cmbCategoria.setSelectedItem(p.getCategoria());
        txtDescripcion.setText(p.getDescripcion());
        chkActivo.setSelected(p.isEstado());
        skuAuto = p.getSku();
        
        chkOfertaFlash.setSelected(p.isEnOfertaFlashConfigurada());
        if (p.getFechaFinOferta() != null) {
            Date date = Date.from(p.getFechaFinOferta().atZone(ZoneId.systemDefault()).toInstant());
            spnFechaFin.setValue(date);
        } else {
            // Si no tiene fecha, ponemos "ahora" o mañana
            spnFechaFin.setValue(new Date());
        }
        spnFechaFin.setEnabled(p.isEnOfertaFlashConfigurada());
        sliderDescuento.setEnabled(p.isEnOfertaFlash());
        if(p.isEnOfertaFlash() && p.getPrecio() > 0) {
            int porc = (int) (100 - (p.getPrecioOferta() * 100 / p.getPrecio()));
            sliderDescuento.setValue(porc);
        }
        
        chkDescVolumen.setSelected(p.isTieneDescuentoVolumen());
        spnCantVolumen.setValue(p.getCantidadParaDescuento() > 0 ? p.getCantidadParaDescuento() : 6);
        sliderVolumen.setValue((int) p.getPorcentajeDescuentoVolumen());
        
        boolean volActivo = p.isTieneDescuentoVolumen();
        spnCantVolumen.setEnabled(volActivo);
        sliderVolumen.setEnabled(volActivo);
        
        
        rutaImagenActual = p.getRutaImagen();
        rutaImagenActual = p.getRutaImagen();
        lblImagenPreview.setIcon(ImagenUtils.cargarImagenEscalada(rutaImagenActual, 130, 130));
        lblImagenPreview.setText(rutaImagenActual == null ? "<html><center>Subir<br>Foto</center></html>" : "");
        
        lblFechaRegistro.setText("Registrado: " + (p.getFechaIngreso() != null ? p.getFechaIngreso() : "Desconocida"));
        actualizarCalculos();
    }
    
    // --- MÉTODO DE VALIDACIÓN ROBUSTA ---
    private boolean validarDatos() {
        // 1. VALIDAR NOMBRE (No vacío)
        if (txtNombre.getText().trim().isEmpty()) {
            MensajesUI.mostrarError(this, "Por favor, ingresa el nombre del producto.");
            txtNombre.requestFocus();
            return false;
        }

        // 2. VALIDAR PRECIO (Numérico y Positivo)
       String precioTxt = txtPrecio.getText().trim().replace(",", "."); 
        if (precioTxt.isEmpty()) {
            MensajesUI.mostrarError(this, "El producto necesita un precio base.");
            txtPrecio.requestFocus();
            return false;
        }
        try {
            if (Double.parseDouble(precioTxt) <= 0) {
                MensajesUI.mostrarError(this, "El precio debe ser mayor a 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            MensajesUI.mostrarError(this, "El precio solo debe contener números.");
            return false;
        }
       

       // 3. VALIDAR CATEGORÍA 
        Object item = cmbCategoria.getSelectedItem();
        String catTexto = (item != null) ? item.toString().trim() : "";

        // A. Validar que no esté vacía
        if (catTexto.isEmpty()) {
            MensajesUI.mostrarError(this, "Selecciona o escribe una categoría válida.");
            cmbCategoria.requestFocus();
            return false;
        }

        // B. VALIDACIÓN ESTRICTA: Que no tenga números
        // ".*\\d.*" significa: cualquier texto que contenga al menos un dígito (0-9)
        if (catTexto.matches(".*\\d.*")) {
            MensajesUI.mostrarError(this, "La categoría NO puede contener números.\nUsa solo letras (Ej: 'Café', no 'Café 2').");
            cmbCategoria.requestFocus();
            return false; // <--- ESTO EVITA QUE SE GUARDE EL PRODUCTO
        }

        // 4. VALIDAR LÓGICA DE ESTRATEGIAS (Para evitar errores de cálculo)
        
        // Si activó Oferta Flash, debe haber movido el slider
        if (chkOfertaFlash.isSelected() && sliderDescuento.getValue() == 0) {
            MensajesUI.mostrarError(this, "Activaste 'Oferta Flash' pero el descuento es 0%.");
            return false;
        }
        
        // Si activó Mayorista, debe haber descuento y cantidad lógica
        if (chkDescVolumen.isSelected()) {
            if (sliderVolumen.getValue() == 0) {
                MensajesUI.mostrarError(this, "Activaste 'Mayorista' pero el descuento es 0%.");
                return false;
            }
            int cantMayorista = (int) spnCantVolumen.getValue();
            if (cantMayorista < 2) {
                MensajesUI.mostrarError(this, "La cantidad para mayorista debe ser al menos 2.");return false;
            }
        }

        return true; // ¡Todo pasó la prueba!
    }
    public void limpiar() {
        txtNombre.setText("");
        txtPrecio.setText("");
        spnStock.setValue(0);
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
        skuAuto = "";
        chkOfertaFlash.setSelected(false);
        sliderDescuento.setValue(0);
        sliderDescuento.setEnabled(false);
        chkDescVolumen.setSelected(false);
        sliderVolumen.setValue(0);
        sliderVolumen.setEnabled(false);
        spnCantVolumen.setEnabled(false);
        spnCantVolumen.setValue(6);
        lblImagenPreview.setIcon(null);
        lblImagenPreview.setText("<html><center>Subir<br>Foto</center></html>");
        rutaImagenActual = "";
        lblFechaRegistro.setText("Hoy: " + LocalDate.now().toString());
        spnFechaFin.setValue(new Date());
        spnFechaFin.setEnabled(false);
        actualizarCalculos();
    }
    public LocalDateTime getFechaFinOferta() {
        if (!chkOfertaFlash.isSelected()) return null;
        
        // Convertir de java.util.Date (Spinner) a LocalDateTime
        Date date = (Date) spnFechaFin.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
// ESTA ES LA VERSIÓN CORREGIDA:
public void setEventos(Runnable guardar, Runnable eliminar, Runnable limpiar) {
    // 1. Limpiar listeners anteriores para evitar duplicados
    for (ActionListener al : btnGuardar.getActionListeners()) btnGuardar.removeActionListener(al);
    for (ActionListener al : btnEliminar.getActionListeners()) btnEliminar.removeActionListener(al);
    for (ActionListener al : btnLimpiar.getActionListeners()) btnLimpiar.removeActionListener(al);

    // 2. Agregar los nuevos listeners
    btnGuardar.addActionListener(e -> { 
        if (validarDatos()) guardar.run(); 
    });
    
    btnEliminar.addActionListener(e -> eliminar.run());
    
    btnLimpiar.addActionListener(e -> limpiar.run());
}
}