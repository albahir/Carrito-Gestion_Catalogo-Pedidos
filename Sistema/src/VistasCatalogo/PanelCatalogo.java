package VistasCatalogo;

import Utilidades.UI.Tema;
import Servicios.GestionProducto;
import EntidadesCatalogo.Producto;
import Utilidades.UI.UIFabric;
import java.awt.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;

// Asegúrate de tener la clase PanelRedondeado y Tema disponibles.
// Si las tienes en FrmMenuPrincipal, muévelas a archivos propios o copia esto al final de este archivo.

public class PanelCatalogo extends JPanel {
    private boolean modoAdmin = false;
    
    private JPanel panelOfertas;  
    private JPanel panelRegulares; 
    private JPanel contenedorPrincipal;
   private JPanel panelMayorista;
   private final double tasaCambio;
    
    // Dependencias
    private final GestionProducto gp;
    private final Consumer<Producto> alClickearAgregar; // El "mensajero" hacia el Frame principal
  
   
   private JLabel lblTituloOfertas;
   private JLabel lblTituloMayorista;
    private JLabel lblTituloRegulares;
    
    private JScrollPane scrollMayorista;
    private JScrollPane scrollOfertas;
    
    private List<Producto> listaCompletaProductos;
    public PanelCatalogo(GestionProducto gp, Consumer<Producto> accionAlAgregar,double tasa) {
        this.gp = gp;
        this.alClickearAgregar = accionAlAgregar;
        this.tasaCambio = tasa;
        this.listaCompletaProductos = new ArrayList<>();

       setLayout(new BorderLayout()); // Usar BorderLayout en el principal
setBackground(Tema.FONDO_GLOBAL);



initComponentes();
cargarProductos();
    }
    
private void initComponentes() {
        // Contenedor Vertical (Scrollable)
        contenedorPrincipal = new JPanel();
        contenedorPrincipal.setLayout(new BoxLayout(contenedorPrincipal, BoxLayout.Y_AXIS));
        contenedorPrincipal.setBackground(Tema.FONDO_GLOBAL);
        contenedorPrincipal.setBorder(new EmptyBorder(10, 20, 20, 20)); 

        // --- 1. TÍTULOS (Usando UIFabric) ---
        lblTituloOfertas = UIFabric.crearTituloSeccion("Ofertas Flash", Tema.BADGE_FLASH, Tema.TEXTO_OSCURO);
        lblTituloMayorista = UIFabric.crearTituloSeccion("Ofertas X Cantidad", Tema.BADGE_MAYORISTA, Color.WHITE);
        lblTituloRegulares = UIFabric.crearTituloSeccion("Catálogo General", Tema.OBSIDIAN, Color.WHITE);

        // --- 2. PANELES DE PRODUCTOS ---
        
        // Panel Ofertas (FlowLayout Izquierda)
        panelOfertas = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelOfertas.setBackground(Tema.FONDO_GLOBAL);
        scrollOfertas = crearScrollHorizontal(panelOfertas);

        // Panel Mayorista
        panelMayorista = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelMayorista.setBackground(Tema.FONDO_GLOBAL);
        scrollMayorista = crearScrollHorizontal(panelMayorista);

        // Panel Regulares (FlowLayout que envuelve)
        panelRegulares = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panelRegulares.setBackground(Tema.FONDO_GLOBAL);
        panelRegulares.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // --- 3. SCROLL GLOBAL ---
        JScrollPane scrollGlobal = new JScrollPane(contenedorPrincipal);
        scrollGlobal.setBorder(null);
        scrollGlobal.getVerticalScrollBar().setUnitIncrement(16);
        scrollGlobal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollGlobal, BorderLayout.CENTER);
    }

// Helper para no repetir código de scroll
    private JScrollPane crearScrollHorizontal(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER); 
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        scroll.setBorder(null); 
        scroll.getHorizontalScrollBar().setUnitIncrement(16); 
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(0, 300));
        scroll.setMaximumSize(new Dimension(2000, 300));
        return scroll;
    }
public void setModoAdmin(boolean esAdmin) {
        this.modoAdmin = esAdmin;
        
    }
    /**
     * Borra todo y vuelve a dibujar las tarjetas. 
     * Útil si cambias precios en el inventario y quieres refrescar aquí.
     */
  public void cargarProductos() {
        this.listaCompletaProductos = gp.obtenerTodos(); 
        filtrarProductos("");
    }
  
 

   public void filtrarProductos(String textoBusqueda) {
       filtrarAvanzado(textoBusqueda, "Todas", 0, 999999, false, false, modoAdmin, false);
    }
private void actualizarVista(ArrayList<Producto> listaProductos) {
        panelOfertas.removeAll();
        panelMayorista.removeAll();
        panelRegulares.removeAll();
        contenedorPrincipal.removeAll();
        
        // 1. ORDENAMIENTO (Esto sí déjalo aquí, es visual)
        listaProductos.sort((p1, p2) -> {
            boolean p1Ok = p1.isEstado() && p1.getStock() > 0;
            boolean p2Ok = p2.isEstado() && p2.getStock() > 0;
            return Boolean.compare(p2Ok, p1Ok);
        });

        boolean hayOfertas = false;
        boolean hayMayorista = false;
        boolean hayRegulares = false;

        for (Producto p : listaProductos) {
           
            
            TarjetaProducto tarjeta = new TarjetaProducto(p, () -> alClickearAgregar.accept(p), modoAdmin, tasaCambio);

            if (p.isEnOfertaFlash()) { 
                panelOfertas.add(tarjeta);
                hayOfertas = true;
            }else if(p.isTieneDescuentoVolumen()){
                panelMayorista.add(tarjeta);
                    hayMayorista = true;
                
            } 
            else {
                panelRegulares.add(tarjeta);
                hayRegulares = true;
            }
        }
        // 3. Reconstrucción visual (Igual que siempre)
        if (hayOfertas) {
            contenedorPrincipal.add(lblTituloOfertas);
            contenedorPrincipal.add(Box.createVerticalStrut(5));
            contenedorPrincipal.add(scrollOfertas);
        }
        if (hayMayorista) {
            contenedorPrincipal.add(lblTituloMayorista);
            contenedorPrincipal.add(Box.createVerticalStrut(5));
            contenedorPrincipal.add(scrollMayorista);
            contenedorPrincipal.add(Box.createVerticalStrut(20)); // Espacio extra
        }

        if (hayRegulares) {
            int filas = (int) Math.ceil((double) panelRegulares.getComponentCount() / 4.0); 
            int alturaEstimada = Math.max(300, filas * 280); 
            panelRegulares.setPreferredSize(new Dimension(800, alturaEstimada));
            
            contenedorPrincipal.add(Box.createVerticalStrut(20));
            contenedorPrincipal.add(lblTituloRegulares);
            contenedorPrincipal.add(panelRegulares);
        }
        
       if (!hayOfertas && !hayMayorista && !hayRegulares) {
            JLabel lblVacio = new JLabel("No se encontraron productos.");
            lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblVacio.setForeground(Color.GRAY);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            contenedorPrincipal.add(Box.createVerticalStrut(50));
            contenedorPrincipal.add(lblVacio);
        }

        panelOfertas.revalidate();
        panelRegulares.revalidate();
        panelMayorista.revalidate();
        contenedorPrincipal.revalidate();
        
        SwingUtilities.invokeLater(() -> {
            scrollOfertas.revalidate();
            scrollMayorista.revalidate();
            this.revalidate();
            this.repaint();
        });
    }

private String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                         .replaceAll("\\p{M}", "")
                         .toLowerCase();
    }
   // Método NUEVO y POTENTE para filtrar por todo
public void filtrarAvanzado(String texto, String categoria, double min, double max, 
                                boolean soloOfertas, boolean soloMayorista, 
                                boolean verSoloInactivos, boolean verSoloSinStock) {
        
        ArrayList<Producto> resultados = new ArrayList<>();
        String textoBuscado = normalizar(texto.trim());
        boolean buscandoEspecifico = !textoBuscado.isEmpty();
        
        for (Producto p : listaCompletaProductos) {
            
          if (modoAdmin && verSoloInactivos && p.isEstado()) continue;
            // Cliente: Nunca ve inactivos
            if (!modoAdmin && !p.isEstado()) continue;

            // 2. STOCK:
            // Admin: Ve todo, a menos que marque "Solo Agotados" (entonces oculta con stock)
            if (modoAdmin && verSoloSinStock && p.getStock() > 0) continue;
            // Cliente: No ve agotados, A MENOS que esté buscando algo específico por texto
            if (!modoAdmin && p.getStock() <= 0 && !buscandoEspecifico) continue;

            // --- FILTROS ---

            // 3. BUSCADOR (Insensible a tildes y mayúsculas)
            if (buscandoEspecifico) {
                String nombreNorm = normalizar(p.getNombre());
                String skuNorm = normalizar(p.getSku());
                String descNorm = normalizar(p.getDescripcion());
                
                if (!nombreNorm.contains(textoBuscado) && 
                    !skuNorm.contains(textoBuscado) && 
                    !descNorm.contains(textoBuscado)) {
                    continue; 
                }
            }
            
            // 3. FILTROS BÁSICOS
            if (!categoria.equals("Todas") && !p.getCategoria().equalsIgnoreCase(categoria)) continue;
            if (p.getPrecio() < min || (max > 0 && p.getPrecio() > max)) continue;
            
            // 4. FILTROS EXCLUYENTES ("SOLO...")
            if (soloOfertas && !p.isEnOfertaFlash()) continue;
            if (soloMayorista && !p.isTieneDescuentoVolumen()) continue;
            
        

            resultados.add(p);
        }
        actualizarVista(resultados);
    }
}

