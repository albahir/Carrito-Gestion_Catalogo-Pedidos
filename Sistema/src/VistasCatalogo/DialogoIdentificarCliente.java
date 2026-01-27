package VistasCatalogo;

import Utilidades.UI.Tema;
import Utilidades.tecnicas.Validaciones;
import Servicios.GestionCliente;
import EntidadesCatalogo.Cliente;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DialogoIdentificarCliente extends JDialog {

    private final JTextField txtCedula;
    private final JLabel lblResultadoNombre;
    private String cedulaSeleccionada = null;
    
    private final GestionCliente gc;
    private final Color COLOR_OBSIDIAN = new Color(38, 38, 40);
    private final Color COLOR_VERDE_EXITO = new Color(39, 174, 96);

    public DialogoIdentificarCliente(JFrame parent, GestionCliente gc) {
        super(parent, "Identificar Cliente", true);
        this.gc = gc;
        
        setUndecorated(true); // Sin bordes de Windows
        
        // Panel Principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(25, 30, 25, 30)
        ));
        panel.setPreferredSize(new Dimension(400, 260));
        setContentPane(panel);

        // 1. TÍTULO
        JLabel lblTitulo = new JLabel("Buscar Cliente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_OBSIDIAN);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // 2. CONTENIDO
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(Color.WHITE);

        // Etiqueta
        JLabel lblInst = new JLabel("Ingrese número de Cédula:");
        lblInst.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInst.setForeground(Color.GRAY);
        lblInst.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Input Cédula
        txtCedula = new JTextField();
        txtCedula.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCedula.setForeground(COLOR_OBSIDIAN);
        txtCedula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtCedula.setAlignmentX(Component.LEFT_ALIGNMENT);
        Validaciones.aplicarFiltro(txtCedula, 8, "NUMEROS");
        // --- LA MAGIA: DETECTOR DE TECLAS ---
        txtCedula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarEnTiempoReal();
            }
        });

        // Etiqueta de Resultado (Donde aparecerá el nombre)
        lblResultadoNombre = new JLabel(""); // Espacio vacío para reservar lugar
        lblResultadoNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResultadoNombre.setForeground(Color.LIGHT_GRAY);
        lblResultadoNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblResultadoNombre.setBorder(new EmptyBorder(10, 0, 0, 0)); // Margen arriba

        centro.add(lblInst);
        centro.add(Box.createVerticalStrut(5));
        centro.add(txtCedula);
        centro.add(lblResultadoNombre);
        
        panel.add(centro, BorderLayout.CENTER);

        // 3. BOTONES
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.setBackground(Color.WHITE);
        sur.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnCancelar = crearBoton("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnAceptar = crearBoton("Aceptar", true);
        btnAceptar.addActionListener(e -> validarYConfirmar());

        sur.add(btnCancelar);
        sur.add(btnAceptar);
        panel.add(sur, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parent);
    }
private void validarYConfirmar() {
        String texto = txtCedula.getText().trim();
        
        // 1. Validar Longitud Mínima
        if (texto.length() < 7) {
            lblResultadoNombre.setText("Error: Mínimo 7 dígitos requeridos.");
            lblResultadoNombre.setForeground(Tema.ERROR);
            
            // Efecto visual: Temblor o foco
            txtCedula.requestFocus();
            return; // ¡NO CERRAMOS LA VENTANA!
        }
        
        // 2. Si pasa, guardamos y cerramos
        cedulaSeleccionada = texto;
        dispose();
    }
    private void buscarEnTiempoReal() {
        String texto = txtCedula.getText().trim();
        if (texto.isEmpty()) {
            lblResultadoNombre.setText(" ");
            return;
        }

        Cliente c = gc.buscarPorCedula(texto);
        if (c == null && texto.matches("[0-9]+")) {
            String[] prefijos = {"V-", "E-", "J-", "G-"};
            for (String pre : prefijos) {
                // Probamos concatenando el prefijo (Ej: V-123456)
                c = gc.buscarPorCedula(pre + texto);
                if (c != null) break; // ¡Lo encontramos!
            }
        }
        if (c != null) {
            lblResultadoNombre.setText(c.getNombreCompleto());
            lblResultadoNombre.setForeground(COLOR_VERDE_EXITO);
            // Opcional: Si quieres que al encontrarlo se seleccione internamente para que al dar Enter funcione directo
            // cedulaSeleccionada = c.getCedula(); 
        } else {
            lblResultadoNombre.setText("Cliente no registrado");
            lblResultadoNombre.setForeground(Color.LIGHT_GRAY);
        }
    }

    public String mostrar() {
        setVisible(true);
        return cedulaSeleccionada;
    }

    // Estilo de botón (Mismo que MensajesUI)
    private JButton crearBoton(String texto, boolean solido) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(solido ? COLOR_OBSIDIAN : new Color(240, 240, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setForeground(solido ? Color.WHITE : COLOR_OBSIDIAN);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}