package Utilidades;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;


public class UIFabric {

    // ==========================================
    // 1. BOTONES
    // ==========================================
    
    public static JButton crearBotonPrincipal(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        estilizarBotonBase(btn, bg, fg);
        btn.setFont(Tema.FUENTE_BOLD);
        // Efecto Hover simple
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }
    public static JButton crearBotonTexto(String texto, Color colorTexto) {
        JButton btn = new JButton(texto);
        
        // Estilo Base
        btn.setFont(Tema.FUENTE_BOLD); // Usa la fuente estándar (Bold 14)
        btn.setForeground(colorTexto);
        
        // Quitar decoración estándar de Java Swing
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto Hover: Oscurecer un poco el texto al pasar el mouse
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { 
                btn.setForeground(colorTexto.darker()); 
            }
            @Override
            public void mouseExited(MouseEvent e) { 
                btn.setForeground(colorTexto); 
            }
        });
        
        return btn;
    }
public static JButton crearBotonItem(String texto) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setBackground(Tema.LAVENDER);
        btn.setForeground(Tema.OBSIDIAN);
        btn.setFont(Tema.FUENTE_BOLD);
        btn.setBorder(new LineBorder(Tema.BORDE_INPUT, 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    public static JButton crearBotonBordeado(String texto, Color colorBorde) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        estilizarBotonBase(btn, Color.WHITE, colorBorde);
        btn.setBorder(new LineBorder(colorBorde, 1, true));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(245, 245, 245)); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    public static JButton crearBotonIcono(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(Tema.LAVENDER.darker());
                else g2.setColor(Tema.LAVENDER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setMargin(new Insets(0, 0, 0, 0));
        estilizarBotonBase(btn, Tema.LAVENDER, Tema.OBSIDIAN);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        return btn;
    }
    
    // Botón para Menú Lateral (Sidebar)
    public static JButton crearBotonMenuLateral(String texto, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 25, 12, 20));
        
        Color colorTexto = activo ? Tema.OBSIDIAN : new Color(180, 180, 180);
        btn.setForeground(colorTexto);
        
        // El pintado del fondo (hover/activo) se maneja mejor en la clase anónima o helper
        // pero aquí configuramos la base.
        return btn;
    }

    private static void estilizarBotonBase(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ==========================================
    // 2. INPUTS Y FORMULARIOS
    // ==========================================

    public static JTextField crearInput() {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(300, 35));
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        t.setFont(Tema.FUENTE_NORMAL);
        t.setForeground(Tema.TEXTO_OSCURO);
        t.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Tema.BORDE_INPUT, 1),
            new EmptyBorder(5, 8, 5, 8) // Padding interno del texto
        ));
        return t;
    }

    public static JPanel crearPanelCampo(String titulo, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(Color.WHITE);
        
        JLabel l = new JLabel(titulo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Tema.TEXTO_GRIS_OSCURO);
        
        // Ajuste específico si es Spinner para que combine con los TextField
        if (input instanceof JSpinner) {
            estilizarSpinner((JSpinner) input);
        }
        
        p.add(l, BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        return p;
    }

    public static void estilizarSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setBackground(Color.WHITE);
            defaultEditor.getTextField().setForeground(Tema.TEXTO_OSCURO);
            defaultEditor.getTextField().setBorder(new EmptyBorder(0, 5, 0, 5));
        }
        spinner.setBorder(new LineBorder(Tema.BORDE_INPUT, 1));
    }
    
    public static void estilizarSlider(JSlider slider) {
        slider.setOpaque(false);
        slider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        slider.setBackground(Color.WHITE);
    }

    public static JCheckBox crearCheck(String texto) {
        JCheckBox c = new JCheckBox(texto);
        c.setOpaque(false);
        c.setFont(Tema.FUENTE_PEQUENA.deriveFont(Font.BOLD));
        c.setForeground(Tema.TEXTO_OSCURO);
        c.setFocusPainted(false);
        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return c;
    }
    
    public static JCheckBox crearCheckOscuro(String texto) {
        JCheckBox c = crearCheck(texto);
        c.setForeground(Color.WHITE);
        return c;
    }
    
    public static JLabel crearLabelBlanco(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(Tema.FUENTE_BOLD.deriveFont(12f));
        return l;
    }
    public static JLabel crearLabelCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.FUENTE_BOLD.deriveFont(12f));
        l.setForeground(Tema.TEXTO_GRIS_OSCURO);
        l.setBorder(new EmptyBorder(0, 0, 5, 0)); // Espacio abajo
        return l;
    }

    // ==========================================
    // 3. COMPONENTES VISUALES (BADGES, CARDS)
    // ==========================================

    // Crea un badge redondeado (Ej: "FLASH", "-20%", "AGOTADO")
    public static JLabel crearBadge(String texto, Color bg, Color fg) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        lbl.setForeground(fg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(2, 6, 2, 6)); // Padding interno
        return lbl;
    }
    
    // Crea un badge circular (Ej: El "-25%" en la tarjeta de producto)
    public static JLabel crearBadgeCircular(String texto, Color bg, Color fg, int size) {
        JLabel lbl = new JLabel("<html><center>" + texto + "</center></html>", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                super.paintComponent(g);
            }
        };
        lbl.setForeground(fg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setOpaque(false);
        lbl.setBounds(0, 0, size, size); // Útil para layouts nulos
        return lbl;
    }

    // Crea los títulos de sección bonitos (Ej: "Ofertas Flash" con fondo amarillo)
    public static JLabel crearTituloSeccion(String texto, Color bg, Color fg) {
        JLabel lbl = new JLabel(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(fg);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        // Ajuste de tamaño para que el fondo se pinte bien
        Dimension d = lbl.getPreferredSize();
        lbl.setMaximumSize(new Dimension(d.width + 10, d.height));
        return lbl;
    }

    // Crea una caja de información con borde suave (Usada en DialogoPago)
    public static JPanel crearPanelInfo(String htmlContent) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.FONDO_GLOBAL);
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel l = new JLabel(htmlContent);
        l.setFont(Tema.FUENTE_PEQUENA);
        l.setForeground(Tema.TEXTO_OSCURO);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    // ==========================================
    // 4. TABLAS
    // ==========================================
    
    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(40);
        tabla.setFont(Tema.FUENTE_NORMAL);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(Tema.LINEA_SEPARADOR);
        tabla.setSelectionBackground(Tema.SELECCION_TABLA);
        tabla.setSelectionForeground(Color.BLACK);
        
        // Header
        JTableHeader header = tabla.getTableHeader();
        header.setFont(Tema.FUENTE_BOLD);
        header.setBackground(Tema.LAVENDER);
        header.setForeground(Tema.OBSIDIAN);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(new MatteBorder(0, 0, 1, 0, Tema.OBSIDIAN)); // Línea inferior en header
        
        // Renderer para filas alternas (Zebra)
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Tema.FONDO_GLOBAL);
                }
                // Bordes para celdas
                setBorder(noFocusBorder);
                if (value instanceof Number) {
                    setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                return c;
            }
        });
    }
   // --- 5. COMPONENTES DE NAVEGACIÓN (CORREGIDO) ---
   public static JButton crearBotonNavegacion(String texto, boolean activo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. LÓGICA DE FONDO
                if (isSelected()) { 
                    // Si está ACTIVO (Seleccionado): Fondo Lavanda Sólido
                    g2.setColor(Tema.LAVENDER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    
                    // Forzamos el color del texto a Oscuro para contraste
                    setForeground(Tema.OBSIDIAN); 
                    
                } else if (getModel().isRollover()) {
                    // Si el mouse está encima (Hover): Fondo Lavanda Transparente
                    g2.setColor(new Color(198, 195, 242, 50)); 
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    
                    // Texto Blanco Brillante al pasar el mouse
                    setForeground(Color.WHITE);
                    
                } else {
                    // Estado NORMAL (Inactivo): Fondo Transparente
                    // Texto Gris Claro para que se vea sobre el fondo oscuro
                    setForeground(new Color(180, 180, 180));
                }
                
                // 2. Pintar el texto y el icono (super)
                super.paintComponent(g);
            }
        };
        
        // Configuración Base
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 25, 12, 20));
        
        // Estado inicial
        btn.setSelected(activo); 
        
        // Listener simple solo para repintar al pasar el mouse
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { 
                btn.repaint(); 
            }
            @Override
            public void mouseExited(MouseEvent e) { 
                btn.repaint(); 
            }
        });

        return btn;
    }
    
    // Método helper para cambiar estado desde el Menú Principal
    public static void setEstadoBotonNavegacion(JButton btn, boolean activo) {
        btn.setSelected(activo); // Esto dispara la lógica del paintComponent de arriba
        btn.repaint(); // Forzamos el redibujado inmediato
    }
   
    // --- 6. BUSCADOR CON PLACEHOLDER ---
    
    public static JTextField crearCampoBusqueda(String placeholder) {
        JTextField txt = new JTextField(placeholder);
        txt.setPreferredSize(new Dimension(250, 35));
        txt.setFont(Tema.FUENTE_NORMAL);
        txt.setForeground(Color.GRAY);
        txt.setBackground(Color.WHITE);
        
        // Borde inicial
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.WHITE, 1, true), 
            new EmptyBorder(5, 15, 5, 15) 
        ));

        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txt.getText().trim().equals(placeholder)) {
                    txt.setText("");
                    txt.setForeground(Tema.OBSIDIAN);
                    txt.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(Tema.LAVENDER, 2, true), // Borde Lavender al enfocar
                        new EmptyBorder(5, 15, 5, 15)));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txt.getText().trim().isEmpty()) {
                    txt.setText(placeholder);
                    txt.setForeground(Color.GRAY);
                    txt.setBorder(BorderFactory.createCompoundBorder(
                         new LineBorder(Color.WHITE, 1, true), 
                         new EmptyBorder(5, 15, 5, 15)));
                }
            }
        });
        return txt;
    }
}