package VistasCatalogo;

import Utilidades.Formato;
import Utilidades.Tema;
import Utilidades.UIFabric;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import Utilidades.Validaciones;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class DialogoPago extends JDialog {

    private boolean pagoExitoso = false;
    private final BigDecimal montoTotalUSD;
    private final double tasaCambio;
    private final BigDecimal montoTotalBs;
    
    // Componentes Dinámicos
    private JPanel panelFormularios;
    private CardLayout cardLayout;
    private JLabel lblMontoGrande;
    private JLabel lblMoneda;
    
    // Inputs
    private JTextField txtZelleEmail, txtZelleTitular, txtRefZelle;
    private JTextField txtPmTelefono, txtRefPm;
    private JComboBox<String> cmbPmBanco;
    private JTextField txtRefPunto;
    private JTextField txtObservaciones;
    
    // Estado
    private String metodoSeleccionado = "ZELLE";
    private List<JButton> listaBotonesMenu;
    private JButton btnActual; 

    public DialogoPago(JFrame parent, BigDecimal montoTotalUSD, double tasaCambio) {
        super(parent, "Procesar Pago", true);
        this.montoTotalUSD = montoTotalUSD;
        this.tasaCambio = tasaCambio;
        this.listaBotonesMenu = new ArrayList<>();
        
        // Calcular Bs
        this.montoTotalBs = montoTotalUSD.multiply(BigDecimal.valueOf(tasaCambio))
                                         .setScale(2, RoundingMode.HALF_UP);
        
        setSize(750, 650); 
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initUI();
        configurarValidaciones();
    }

    private void initUI() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        setContentPane(content);

        // --- 1. HEADER ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        header.setBackground(new Color(250, 250, 252));
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        JLabel lblTitulo = new JLabel("Total a Pagar:");
        lblTitulo.setFont(Tema.FUENTE_NORMAL);
        lblTitulo.setForeground(Color.GRAY);
        
        lblMontoGrande = new JLabel(Formato.dinero(montoTotalUSD.doubleValue()));
        lblMontoGrande.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblMontoGrande.setForeground(Tema.OBSIDIAN);
        
        lblMoneda = new JLabel("USD");
        lblMoneda.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMoneda.setForeground(Color.GRAY);
        
        header.add(lblTitulo);
        header.add(lblMontoGrande);
        header.add(lblMoneda);

        content.add(header, BorderLayout.NORTH);

        // --- 2. SELECTOR LATERAL ---
        JPanel panelSelector = new JPanel();
        panelSelector.setLayout(new BoxLayout(panelSelector, BoxLayout.Y_AXIS));
        panelSelector.setBackground(Color.WHITE);
        panelSelector.setBorder(new EmptyBorder(20, 15, 20, 15));
        panelSelector.setPreferredSize(new Dimension(180, 0));
        
        // Creamos botones usando UIFabric (Base Blanca)
        JButton btnZelle = UIFabric.crearBotonPrincipal("Zelle", Color.WHITE, Color.GRAY);
        JButton btnPM = UIFabric.crearBotonPrincipal("Pago Móvil", Color.WHITE, Color.GRAY);
        JButton btnPunto = UIFabric.crearBotonPrincipal("Punto Venta", Color.WHITE, Color.GRAY);
        JButton btnEfectivo = UIFabric.crearBotonPrincipal("Efectivo $", Color.WHITE, Color.GRAY);
        
        configurarBotonMenu(btnZelle, "ZELLE");
        configurarBotonMenu(btnPM, "PM");
        configurarBotonMenu(btnPunto, "PUNTO");
        configurarBotonMenu(btnEfectivo, "EFECTIVO_USD");
        
        panelSelector.add(btnZelle);
        panelSelector.add(Box.createVerticalStrut(10));
        panelSelector.add(btnPM);
        panelSelector.add(Box.createVerticalStrut(10));
        panelSelector.add(btnPunto);
        panelSelector.add(Box.createVerticalStrut(10));
        panelSelector.add(btnEfectivo);

        // Línea divisoria
        JPanel pOeste = new JPanel(new BorderLayout());
        pOeste.setBackground(Color.WHITE);
        pOeste.add(panelSelector, BorderLayout.CENTER);
        pOeste.setBorder(new MatteBorder(0, 0, 0, 1, new Color(240, 240, 240)));
        
        content.add(pOeste, BorderLayout.WEST);

        // --- 3. FORMULARIOS DINÁMICOS ---
        cardLayout = new CardLayout();
        panelFormularios = new JPanel(cardLayout);
        panelFormularios.setBackground(Color.WHITE);
        panelFormularios.setBorder(new EmptyBorder(25, 30, 25, 30)); 
        
        panelFormularios.add(crearPanelZelle(), "ZELLE");
        panelFormularios.add(crearPanelPagoMovil(), "PM");
        panelFormularios.add(crearPanelPunto(), "PUNTO");
        panelFormularios.add(crearPanelEfectivo(), "EFECTIVO_USD");
        
        content.add(panelFormularios, BorderLayout.CENTER);

        // --- 4. FOOTER ---
        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.setBackground(Color.WHITE);
        panelSur.setBorder(new MatteBorder(1, 0, 0, 0, new Color(240, 240, 240))); 

        // A. Área de Notas (Padding 15px laterales)
        JPanel pObs = new JPanel(new BorderLayout(10, 0));
        pObs.setBackground(Color.WHITE);
        pObs.setBorder(new EmptyBorder(15, 30, 5, 30)); 

        JLabel lblObs = new JLabel("Notas / Observaciones:");
        lblObs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblObs.setForeground(Color.GRAY);

        txtObservaciones = UIFabric.crearInput(); // Usamos tu fábrica
        txtObservaciones.setToolTipText("Ej: Entregar en portería, Llamar antes, etc.");
        
        pObs.add(lblObs, BorderLayout.WEST);
        pObs.add(txtObservaciones, BorderLayout.CENTER);

        // B. Botones de Acción
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pBotones.setBackground(Color.WHITE);
        
        JButton btnCancelar = UIFabric.crearBotonBordeado("Cancelar", Color.GRAY);
        btnCancelar.setPreferredSize(new Dimension(120, 40));
        btnCancelar.addActionListener(e -> dispose());
        
        JButton btnConfirmar = UIFabric.crearBotonPrincipal("CONFIRMAR PAGO", Tema.OBSIDIAN, Color.WHITE);
        btnConfirmar.setPreferredSize(new Dimension(200, 40));
        btnConfirmar.addActionListener(e -> validarYConfirmar());
        
        pBotones.add(btnCancelar);
        pBotones.add(btnConfirmar);
        
        // Agregar todo al panel sur
        panelSur.add(pObs);
        panelSur.add(pBotones);
        
        content.add(panelSur, BorderLayout.SOUTH);
        
        // Seleccionar Zelle por defecto
        seleccionarBotonVisualmente(btnZelle, "ZELLE");
    }

    // --- LÓGICA DE CONTROL VISUAL (CORREGIDA) ---
    
    private void configurarBotonMenu(JButton btn, String codigoCard) {
        btn.setPreferredSize(new Dimension(170, 42));
        btn.setMaximumSize(new Dimension(170, 42));
        
        // IMPORTANTE: NO borramos los listeners originales para no romper el click.
        // En su lugar, agregamos lógica que tiene prioridad visual.
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != btnActual) {
                    btn.setBackground(new Color(245, 245, 245)); // Hover suave si no está seleccionado
                } else {
                    // Si está seleccionado, aseguramos que siga negro
                    btn.setBackground(Tema.OBSIDIAN);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Al salir el mouse, UIFabric intenta ponerlo blanco. 
                // Nosotros lo corregimos inmediatamente:
                if (btn == btnActual) {
                    btn.setBackground(Tema.OBSIDIAN);
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(Color.GRAY);
                }
            }
        });

        // Acción de click
        btn.addActionListener(e -> seleccionarBotonVisualmente(btn, codigoCard));
        
        listaBotonesMenu.add(btn);
    }
    
    private void seleccionarBotonVisualmente(JButton nuevoBtn, String codigoCard) {
        // 1. Cambiar Panel
        cardLayout.show(panelFormularios, codigoCard);
        this.metodoSeleccionado = codigoCard;
        actualizarTotalVisual(codigoCard);
        
        // Guardar actual
        this.btnActual = nuevoBtn;
        
        // 2. Refrescar TODOS los estilos
        for (JButton b : listaBotonesMenu) {
            if (b == nuevoBtn) {
                // ACTIVO: Negro Sólido
                b.setBackground(Tema.OBSIDIAN);
                b.setForeground(Color.WHITE);
                b.setBorder(null); 
            } else {
                // INACTIVO: Blanco con Borde Gris
                b.setBackground(Color.WHITE);
                b.setForeground(Color.GRAY);
                b.setBorder(new LineBorder(new Color(230, 230, 230), 1));
            }
        }
    }

    // --- PANELES ESPECÍFICOS ---

   private JPanel crearPanelZelle() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        
        p.add(crearTituloFormulario("Detalles Zelle"));
        p.add(Box.createVerticalStrut(5));
        
        // Usamos una tabla simple sin anchos fijos complejos para que fluya
        String infoHtml = "<html><table border='0' cellspacing='3' cellpadding='0' width='100%'>" +
                          "<tr><td>📧 <b>Email:</b></td><td>pagos@mitienda.com</td></tr>" +
                          "<tr><td>👤 <b>Titular:</b></td><td>Tu Empresa LLC</td></tr>" +
                          "</table></html>";
                          
        // Usamos el crearCajaInfo ajustado (ver paso 5)
        p.add(crearCajaInfo(infoHtml));
        p.add(Box.createVerticalStrut(15));
        
        p.add(crearSubtituloFormulario("Registrar Comprobante"));
        p.add(Box.createVerticalStrut(10));
        
        p.add(UIFabric.crearLabelCampo("Correo Zelle Emisor:"));
        txtZelleEmail = UIFabric.crearInput();
        p.add(txtZelleEmail);
        p.add(Box.createVerticalStrut(5));
        
        p.add(UIFabric.crearLabelCampo("Nombre del Titular:"));
        txtZelleTitular = UIFabric.crearInput();
        p.add(txtZelleTitular);
        p.add(Box.createVerticalStrut(5));
        
        p.add(UIFabric.crearLabelCampo("Número de Referencia (#):"));
        txtRefZelle = UIFabric.crearInput();
        p.add(txtRefZelle);
        
        p.add(Box.createVerticalGlue());
        return p;
    }
    private JPanel crearPanelPunto() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        
        p.add(crearTituloFormulario("Punto de Venta"));
        p.add(Box.createVerticalStrut(15));
        
        // Texto simple y directo
        String infoHtml = "<html><b>Instrucciones:</b><br>" +
                          "1. Pase la tarjeta por el punto físico.<br>" +
                          "2. Ingrese abajo el número de lote.</html>";
                          
        p.add(crearCajaInfo(infoHtml));
        p.add(Box.createVerticalStrut(25));
        
        p.add(crearSubtituloFormulario("Datos del Ticket"));
        p.add(Box.createVerticalStrut(10));
        
        p.add(UIFabric.crearLabelCampo("Lote / Referencia:"));
        txtRefPunto = UIFabric.crearInput();
        p.add(txtRefPunto);
        
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel crearPanelPagoMovil() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        
        p.add(crearTituloFormulario("Pago Móvil"));
        p.add(Box.createVerticalStrut(5)); // Menos espacio
        
        // --- 1. INFO TIENDA (Más compacta) ---
        // Quitamos saltos de línea innecesarios
        String infoHtml = "<html><table border='0' cellspacing='2' cellpadding='0'>" +
                          "<tr><td>🏦 <b>Banco:</b></td><td>Banesco (0134)</td></tr>" +
                          "<tr><td>📱 <b>Tel:</b></td><td>0414-1234567</td></tr>" +
                          "<tr><td>🆔 <b>RIF:</b></td><td>J-12345678-9</td></tr>" +
                          "</table></html>";
                          
        JPanel infoBox = crearCajaInfo(infoHtml);
        // Reducimos la altura máxima para que no empuje el resto hacia afuera
        infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90)); 
        infoBox.setPreferredSize(new Dimension(300, 85));
        p.add(infoBox);
        
        p.add(Box.createVerticalStrut(10));
        p.add(crearSubtituloFormulario("Datos del Cliente"));
        p.add(Box.createVerticalStrut(5));
        
        // --- 2. BANCO CLIENTE ---
        p.add(UIFabric.crearLabelCampo("Banco Emisor:"));
        cmbPmBanco = new JComboBox<>(new String[]{"Banesco", "Banco de Venezuela", "Mercantil", "Provincial", "BNC"});
        cmbPmBanco.setBackground(Color.WHITE);
        cmbPmBanco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Altura 30 más compacta
        cmbPmBanco.setPreferredSize(new Dimension(300, 30));
        cmbPmBanco.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        cmbPmBanco.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(cmbPmBanco);
        p.add(Box.createVerticalStrut(5));
        
        // --- 3. TELÉFONO (COMBO + CAMPO) ---
        p.add(UIFabric.crearLabelCampo("Teléfono Emisor:"));
        
        JPanel pTelefono = new JPanel(new BorderLayout(5, 0));
        pTelefono.setBackground(Color.WHITE);
        pTelefono.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Altura fija
        pTelefono.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Combo de Prefijos
        JComboBox<String> cmbPrefijo = new JComboBox<>(new String[]{"0414", "0424", "0412", "0416", "0426"});
        cmbPrefijo.setBackground(Color.WHITE);
        cmbPrefijo.setPreferredSize(new Dimension(70, 30));
        cmbPrefijo.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        // Campo de Texto (Validado a 7 números en el paso 1)
        txtPmTelefono = UIFabric.crearInput(); 
        txtPmTelefono.setPreferredSize(new Dimension(100, 30)); // Reset de altura
        
        pTelefono.add(cmbPrefijo, BorderLayout.WEST);
        pTelefono.add(txtPmTelefono, BorderLayout.CENTER);
        
        p.add(pTelefono);
        p.add(Box.createVerticalStrut(5));
        
        // --- 4. REFERENCIA ---
        p.add(UIFabric.crearLabelCampo("Referencia (Últimos dígitos):"));
        txtRefPm = UIFabric.crearInput();
        txtRefPm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Altura compacta
        p.add(txtRefPm);
        
        // Glue para empujar todo arriba
        p.add(Box.createVerticalGlue());
        return p;
    }
    private JPanel crearPanelEfectivo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        
        JLabel icon = new JLabel("💵", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
        
        JLabel titulo = new JLabel("Pago en Efectivo", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Tema.OBSIDIAN);
        
        JLabel text = new JLabel("<html><center>Reciba el dinero en caja<br>y verifique los billetes.</center></html>", SwingConstants.CENTER);
        text.setFont(Tema.FUENTE_NORMAL);
        text.setForeground(Color.GRAY);
        
        JPanel centro = new JPanel(new GridLayout(3, 1, 0, 10));
        centro.setBackground(Color.WHITE);
        centro.add(icon);
        centro.add(titulo);
        centro.add(text);
        
        p.add(centro, BorderLayout.CENTER);
        
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(p);
        
        return contenedor;
    }

    // --- HELPERS ---

    private JLabel crearTituloFormulario(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(Tema.OBSIDIAN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
private JLabel crearSubtituloFormulario(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.GRAY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
private JPanel crearCajaInfo(String html) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.FONDO_GLOBAL); // USAMOS EL TEMA
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel l = new JLabel(html);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(60, 60, 70));
        p.add(l, BorderLayout.CENTER);
        return p;
    }
  public String getMetodoSeleccionado() {
        // Retornamos nombres más bonitos para la base de datos
        switch (metodoSeleccionado) {
            case "PM": return "Pago Móvil";
            case "EFECTIVO_USD": return "Efectivo ($)";
            case "PUNTO": return "Punto Venta";
            default: return metodoSeleccionado; // "ZELLE"
        }
    }

    public String getReferenciaIngresada() {
        switch (metodoSeleccionado) {
            case "ZELLE": return txtRefZelle.getText().trim();
            case "PM": return txtRefPm.getText().trim();
            case "PUNTO": return txtRefPunto.getText().trim();
            default: return "N/A"; // Efectivo no suele tener referencia
        }
    }

    private void actualizarTotalVisual(String metodo) {
        if (metodo.equals("PM")) {
            lblMontoGrande.setText(String.format("%,.2f", montoTotalBs));
            lblMoneda.setText("Bs. (Tasa: " + tasaCambio + ")");
            lblMontoGrande.setForeground(new Color(0, 102, 204)); 
        } else {
            lblMontoGrande.setText(Formato.dinero(montoTotalUSD.doubleValue()));
            lblMoneda.setText("USD");
            lblMontoGrande.setForeground(Tema.OBSIDIAN);
        }
    }
private void configurarValidaciones() {
        // Zelle
        Validaciones.limitarInput(txtZelleTitular, 50, "LETRAS");
        Validaciones.limitarInput(txtRefZelle, 20, "NUMEROS");
        
        // Pago Móvil (7 dígitos exactos para el cuerpo del teléfono)
        Validaciones.limitarInput(txtPmTelefono, 7, "NUMEROS");
        Validaciones.limitarInput(txtRefPm, 6, "NUMEROS");
        
        // Punto de Venta
        Validaciones.limitarInput(txtRefPunto, 12, "NUMEROS");
    }
   private void validarYConfirmar() {
        if (metodoSeleccionado.equals("ZELLE")) {
            if (txtZelleEmail.getText().trim().isEmpty() || 
                txtZelleTitular.getText().trim().isEmpty() || 
                txtRefZelle.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos del Zelle.");
                return;
            }
            // REDUNDANCIA ELIMINADA: Llamada directa
            if (!Validaciones.esEmailValido(txtZelleEmail.getText())) {
                JOptionPane.showMessageDialog(this, "El correo Zelle no es válido.");
                return;
            }
            
        } else if (metodoSeleccionado.equals("PM")) {
            String telf = txtPmTelefono.getText().trim();
            if (telf.isEmpty() || telf.length() != 7) {
                JOptionPane.showMessageDialog(this, "El teléfono debe tener 7 dígitos (Ej: 1234567).");
                return;
            }
            if (txtRefPm.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la referencia del pago móvil.");
                return;
            }
            
        } else if (metodoSeleccionado.equals("PUNTO")) {
            if (txtRefPunto.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el número de lote/referencia.");
                return;
            }
        }
        
        this.pagoExitoso = true;
        dispose();
    }
   public String getObservaciones() {
        return txtObservaciones.getText().trim();
    }
    public boolean mostrar() {
        setVisible(true);
        return pagoExitoso;
    }
}