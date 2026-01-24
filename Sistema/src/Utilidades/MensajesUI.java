package Utilidades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MensajesUI extends JDialog {

    private boolean confirmacion = false; // Para saber si dijo SI o NO
    private String textoIngresado = null; // Para guardar lo que escriba
    private JTextField txtInput;

    // Colores
   

    // Constructor Privado
    private MensajesUI(Component parent, String titulo, String mensaje, int tipo) {
        // Tipos: 0=INFO, 1=ERROR, 2=CONFIRMACION, 3=INPUT
        super(parent instanceof Frame ? (Frame) parent : SwingUtilities.getWindowAncestor(parent), titulo, ModalityType.APPLICATION_MODAL);
        
        setUndecorated(true);
       

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.FONDO_GLOBAL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(25, 30, 25, 30)
        ));
        setContentPane(panel);

        // 1. TÍTULO
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        // Si es ERROR, título rojo. Si no, oscuro.
        if (tipo == 1) lblTitulo.setForeground(Tema.ERROR);
        else lblTitulo.setForeground(Tema.OBSIDIAN);
        
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        // 2. CONTENIDO
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(Tema.FONDO_GLOBAL);

        // Mensaje (con HTML para salto de línea automático)
        JLabel lblMsg = new JLabel("<html><div style='width: 320px; text-align: left;'>" + mensaje + "</div></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMsg.setForeground(Color.GRAY);
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(lblMsg);

        // Input (Solo si tipo == 3)
        if (tipo == 3) {
            centro.add(Box.createVerticalStrut(15));
            txtInput = new JTextField();
            txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            txtInput.setForeground(Tema.OBSIDIAN);
            txtInput.setMaximumSize(new Dimension(320, 35)); 
            txtInput.setPreferredSize(new Dimension(320, 35));
            txtInput.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Borde bonito al input
            txtInput.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200,200,200)), 
                new EmptyBorder(5, 5, 5, 5)));
                
            centro.add(txtInput);
        }
        panel.add(centro, BorderLayout.CENTER);

        // 3. BOTONES
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Tema.FONDO_GLOBAL);
        panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnCancelar = crearBoton("Cancelar", false);
        btnCancelar.addActionListener(e -> {
            confirmacion = false;
            textoIngresado = null;
            dispose();
        });

        JButton btnAceptar = crearBoton("Aceptar", true);
        btnAceptar.addActionListener(e -> {
            confirmacion = true;
            if (tipo == 3) textoIngresado = txtInput.getText();
            dispose();
        });

        // LÓGICA CORREGIDA DE BOTONES:
        // Cancelar solo aparece en CONFIRMACION (2) o INPUT (3)
        if (tipo == 2 || tipo == 3) {
            panelBotones.add(btnCancelar);
        }
        // Aceptar siempre aparece
        panelBotones.add(btnAceptar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
       
        pack(); // <--- ESTA ES LA MAGIA: Ajusta la altura automáticamente al contenido
        setLocationRelativeTo(parent);
        // Foco automático al input
        if (tipo == 3) {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) { txtInput.requestFocus(); }
            });
        }
    }

    // --- MÉTODOS PÚBLICOS ESTÁTICOS ---

    // 1. Mostrar Información (Solo botón Aceptar)
    public static void mostrarInfo(Component parent, String mensaje) {
        MensajesUI d = new MensajesUI(parent, "Información", mensaje, 0);
        d.setVisible(true);
    }

    // 2. Mostrar Error (Título Rojo, Solo botón Aceptar) - ¡ESTE FALTABA!
    public static void mostrarError(Component parent, String mensaje) {
        MensajesUI d = new MensajesUI(parent, "¡Atención!", mensaje, 1);
        d.setVisible(true);
    }

    // 3. Confirmar (Botones Aceptar y Cancelar) -> Retorna true/false
    public static boolean confirmar(Component parent, String titulo, String mensaje) {
        MensajesUI d = new MensajesUI(parent, titulo, mensaje, 2);
        d.setVisible(true);
        return d.confirmacion;
    }

    // 4. Leer Texto (Input + Aceptar/Cancelar) -> Retorna String o null
    public static String leerTexto(Component parent, String titulo, String mensaje) {
        MensajesUI d = new MensajesUI(parent, titulo, mensaje, 3);
        d.setVisible(true);
        return d.textoIngresado;
    }

    // --- HELPER BOTONES ---
    private JButton crearBoton(String texto, boolean solido) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (solido) {
                    g2.setColor(Tema.OBSIDIAN); 
                } else {
                    g2.setColor(new Color(245, 245, 245)); 
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setForeground(solido ? Color.WHITE : Tema.OBSIDIAN);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto Hover simple
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        
        return btn;
    }
}