package VistasCatalogo;

import AcccesoDatosCatalogo.Busqueda;
import AcccesoDatosCatalogo.GestionPedidos;
import EntidadesCatalogo.Pedido;
import Utilidades.Formato;
import Utilidades.Tema;
import Utilidades.UIFabric;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class PanelHistorial extends JPanel {

    private final GestionPedidos gestionPedidos;
    private final JFrame parentFrame;
    private JTable tabla;
    private DefaultTableModel modelo;
    private List<Pedido> listaActual;

    public PanelHistorial(JFrame parent, GestionPedidos gp) {
        this.parentFrame = parent;
        this.gestionPedidos = gp;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        initUI();
        cargarDatos();
    }

    private void initUI() {
        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Historial de Transacciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Tema.OBSIDIAN);
        
        JButton btnRefrescar = UIFabric.crearBotonPrincipal("Refrescar Datos", Tema.LAVENDER, Tema.OBSIDIAN);
        btnRefrescar.setPreferredSize(new Dimension(160, 40));
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        headerPanel.add(lblTitulo, BorderLayout.WEST);
        headerPanel.add(btnRefrescar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. DEFINICIÓN DE COLUMNAS (11 Columnas Totales)
        String[] columnas = {
            "ID",           // 0
            "Fecha",        // 1
            "Cliente",      // 2
            "Método",       // 3
            "Ref.",         // 4
            "Tasa",         // 5 
            "Ahorro",       // 6 (Verde - FALTABA ESTA COLUMNA EN EL HEADER)
            "Total ($)",    // 7
            "Total (Bs)",   // 8
            "Estado",       // 9 
            "Notas"         // 10
        };
        
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        // 3. TABLA
        tabla = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                }
                return c;
            }
        };
        
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(new Color(230, 240, 255));
        tabla.setSelectionForeground(Color.BLACK);

        JTableHeader tHeader = tabla.getTableHeader();
        tHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tHeader.setBackground(Tema.LAVENDER);
        tHeader.setForeground(Tema.OBSIDIAN);
        tHeader.setPreferredSize(new Dimension(0, 45));
        tHeader.setBorder(new LineBorder(Tema.LAVENDER, 1));

        configurarColumnas();

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) mostrarDetalle();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        scroll.getViewport().setBackground(Color.WHITE);
        
        add(scroll, BorderLayout.CENTER);
        
        JLabel lblInfo = new JLabel("<html><i>* Haga doble clic en una fila para ver el recibo digital.</i></html>");
        lblInfo.setBorder(new EmptyBorder(10, 0, 0, 0));
        lblInfo.setForeground(Color.GRAY);
        add(lblInfo, BorderLayout.SOUTH);
    }

    private void configurarColumnas() {
        TableColumnModel cm = tabla.getColumnModel();
        
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(JLabel.RIGHT);
        rightRender.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Renderer Verde para Ahorro
        DefaultTableCellRenderer greenRender = new DefaultTableCellRenderer();
        greenRender.setHorizontalAlignment(JLabel.RIGHT);
        greenRender.setBorder(new EmptyBorder(0, 0, 0, 10));
        greenRender.setForeground(new Color(39, 174, 96)); 

        // Renderer para Estado (PAGADO=Verde, ANULADO=Rojo)
        DefaultTableCellRenderer estadoRender = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = (String) value;
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                if ("PAGADO".equals(val)) setForeground(new Color(39, 174, 96));
                else if ("ANULADO".equals(val)) setForeground(Color.RED);
                else setForeground(Color.GRAY);
                return c;
            }
        };

        // Asignación de Renderers
        cm.getColumn(0).setCellRenderer(centerRender); // ID
        cm.getColumn(1).setCellRenderer(centerRender); // Fecha
        cm.getColumn(5).setCellRenderer(centerRender); // Tasa
        cm.getColumn(6).setCellRenderer(greenRender);  // Ahorro (Col 6)
        cm.getColumn(7).setCellRenderer(rightRender);  // Total USD
        cm.getColumn(8).setCellRenderer(rightRender);  // Total Bs
        cm.getColumn(9).setCellRenderer(estadoRender); // Estado

        // Anchos
        cm.getColumn(0).setPreferredWidth(40);
        cm.getColumn(1).setPreferredWidth(120);
        cm.getColumn(2).setPreferredWidth(180);
        cm.getColumn(6).setPreferredWidth(70); // Ahorro
        cm.getColumn(9).setPreferredWidth(80); // Estado
        cm.getColumn(10).setPreferredWidth(180); // Notas
    }

    public void cargarDatos() {
        this.listaActual = gestionPedidos.obtenerTodos(); 
        llenarTabla(this.listaActual);
    }

    // --- FILTRADO ---
    public void filtrar(String texto, String estado, String metodo, LocalDate inicio, LocalDate fin) {
        if (listaActual == null) cargarDatos();
        String query = (texto != null && !texto.equals("Buscar producto...")) ? texto.toLowerCase().trim() : "";
        boolean filtrarEstado = estado != null && !estado.equals("Todos");
        boolean filtrarMetodo = metodo != null && !metodo.equals("Todos");

        ArrayList<Pedido> resultados = new ArrayList<>();
        try {
            resultados = Busqueda.buscarVarios(listaActual, p -> {
                boolean coincideTexto = true;
                if (!query.isEmpty()) {
                    String nombre = (p.getCliente() != null) ? p.getCliente().getNombreCompleto() : "";
                    String cedula = (p.getCliente() != null) ? p.getCliente().getCedula() : "";
                    String ref = (p.getReferenciaPago() != null) ? p.getReferenciaPago() : "";
                    
                    coincideTexto = String.valueOf(p.getIdPedido()).contains(query) || 
                                    nombre.toLowerCase().contains(query) || 
                                    cedula.toLowerCase().contains(query) || 
                                    ref.toLowerCase().contains(query);
                }
                boolean coincideEstado = !filtrarEstado || (p.getEstado() != null && p.getEstado().toString().equalsIgnoreCase(estado));
                boolean coincideMetodo = !filtrarMetodo || (p.getMetodoPago() != null && p.getMetodoPago().equalsIgnoreCase(metodo));
                
                boolean coincideFecha = true;
                if (p.getFecha() != null) {
                    LocalDate f = p.getFecha().toLocalDate();
                    if (inicio != null && f.isBefore(inicio)) coincideFecha = false;
                    if (fin != null && f.isAfter(fin)) coincideFecha = false;
                }
                return coincideTexto && coincideEstado && coincideMetodo && coincideFecha;
            });
        } catch (Exception e) { resultados = new ArrayList<>(); }
        llenarTabla(resultados);
    }

    private void llenarTabla(List<Pedido> lista) {
        modelo.setRowCount(0);
        if (lista == null) return; 

        List<Pedido> listaOrdenada = new ArrayList<>(lista);
        listaOrdenada.sort((p1, p2) -> Integer.compare(p2.getIdPedido(), p1.getIdPedido()));

        for (Pedido p : listaOrdenada) {
            String obs = p.getObservaciones() != null ? p.getObservaciones() : "";
            
            double totalBsDisplay = p.getTotalLocalDouble();
            if (totalBsDisplay == 0 && p.getTasaCambioSnapshot() > 0) {
                totalBsDisplay = p.getTotalDivisaDouble() * p.getTasaCambioSnapshot();
            }

            // AQUÍ LLENAMOS LAS 11 COLUMNAS EN ORDEN CORRECTO
            Object[] fila = {
                p.getIdPedido(),                            // 0
                p.getFechaFormateada(),                     // 1
                p.getCliente().getNombreCompleto(),         // 2
                p.getMetodoPago() != null ? p.getMetodoPago() : "N/A", // 3
                p.getReferenciaPago() != null ? p.getReferenciaPago() : "", // 4
                p.getTasaCambioSnapshot(),                  // 5
                // COLUMNA 6 (AHORRO): Solo mostramos el número
                Formato.dinero(p.getTotalDescuentos() != null ? p.getTotalDescuentos().doubleValue() : 0),
                // COLUMNA 7 (TOTAL USD)
                Formato.dinero(p.getTotalDivisaDouble()),
                // COLUMNA 8 (TOTAL BS)
                String.format("Bs. %,.2f", totalBsDisplay),
                // COLUMNA 9 (ESTADO ENUM)
                p.getEstado().name(),
                // COLUMNA 10 (NOTAS)
                obs
            };
            modelo.addRow(fila);
        }
    }
    private void mostrarDetalle() {
        int row = tabla.getSelectedRow();
        if (row != -1) {
            int id = (int) modelo.getValueAt(row, 0);
            Pedido p = gestionPedidos.buscarPorId(id);
            if (p != null) {
                DialogoResumenPedido dialogo = new DialogoResumenPedido(parentFrame, p, p.getTasaCambioSnapshot());
                dialogo.setVisible(true);
            }
        }
    }
}