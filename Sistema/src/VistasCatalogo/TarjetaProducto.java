package VistasCatalogo;

import java.awt.event.ActionListener; 
import java.time.Duration; 
import java.time.LocalDateTime;
import javax.swing.Timer;
import Utilidades.*;
import EntidadesCatalogo.Producto;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class TarjetaProducto extends JPanel {

    private final Producto producto;
    private final Runnable accionClick; // Una sola acción unificada
    private final boolean esModoAdmin;
    private final double tasaCambio;
    private JLabel lblContadorTiempo; 
    private Timer timerOferta;
   
    
    // CONSTRUCTOR DE 2 ARGUMENTOS (Esto corrige tu error)
    public TarjetaProducto(Producto p, Runnable accionClick, boolean esModoAdmin,double tasaCambio) {
        this.producto = p;
        this.accionClick = accionClick;
        this.esModoAdmin = esModoAdmin;
        this.tasaCambio = tasaCambio;

        setLayout(null); 
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(180, 270)); 
        setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
        if(esModoAdmin){
           setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Evento: Al hacer clic en cualquier parte, ejecuta la acción
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { accionClick.run(); }
            @Override
            public void mouseEntered(MouseEvent e) { setBackground(new Color(250, 250, 255)); }
            @Override
            public void mouseExited(MouseEvent e) { setBackground(Color.WHITE); }
        });
        }else{
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
        }
        initUI();
    }

   private void initUI() {
       
       
        // =================================================================
        // 3. IMAGEN
        // =================================================================
        JLabel lblImg = new JLabel();
        lblImg.setBounds(0, 40, 180, 130);
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        
        ImageIcon icon = ImagenUtils.cargarImagenEscalada(producto.getRutaImagen(), 140, 130);
        if (icon != null) lblImg.setIcon(icon);
        else {
            lblImg.setText("📷");
            lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            lblImg.setForeground(Color.LIGHT_GRAY);
        }
        add(lblImg);
        // =================================================================
        // 1. ETIQUETAS DE ESTADO (POSICIONES ORIGINALES)
        // =================================================================
        
     int yPosEtiqueta = 165; // Posición Y: Parte inferior de la foto (170 - 20 - margen)
        int margenDerecho = 7;

        // Estado: INACTIVO
        if (!producto.isEstado()) {
            JLabel lblInactivo = UIFabric.crearBadge("INACTIVO", Tema.ERROR, Color.WHITE);
            // Hacemos la fuente un pelín más pequeña para que sea sutil
            lblInactivo.setFont(new Font("Segoe UI", Font.BOLD, 10)); 
            
            Dimension d = lblInactivo.getPreferredSize();
            int anchoReal = d.width + 4;
            int x = 180 - margenDerecho - anchoReal;
            
            lblInactivo.setBounds(x, yPosEtiqueta, anchoReal, 18);
            add(lblInactivo);
            setComponentZOrder(lblInactivo, 0); // ¡IMPORTANTE! Traer al frente sobre la foto
            yPosEtiqueta -= 20; // Si hay otra etiqueta, que se ponga arriba de esta
        }
        
        // Estado: AGOTADO
        if (producto.getStock() <= 0) {
            JLabel lblAgotado = UIFabric.crearBadge("AGOTADO", Tema.AGOTADO_BG, Color.WHITE);
            lblAgotado.setFont(new Font("Segoe UI", Font.BOLD, 10));
            
            Dimension d = lblAgotado.getPreferredSize();
            int anchoReal = d.width + 4;
            int x = 180 - margenDerecho - anchoReal;
            
            lblAgotado.setBounds(x, yPosEtiqueta, anchoReal, 18);
            add(lblAgotado);
            setComponentZOrder(lblAgotado, 0); // Traer al frente
        }
        // =================================================================
        // 2. BADGES SUPERIORES (Oferta y Mayorista)
        // =================================================================
        
        // A. OFERTA FLASH (Círculo Top-Izquierda)
        if (producto.isEnOfertaFlash()) {
            double precioOrig = producto.getPrecio();
            double precioOff = producto.getPrecioOferta();
            int porcentaje = (precioOrig > 0) ? (int) Math.round((1 - (precioOff / precioOrig)) * 100) : 0;

            JLabel lblBadge = UIFabric.crearBadgeCircular("-" + porcentaje + "%", Tema.BADGE_FLASH, Tema.OBSIDIAN, 42);
            lblBadge.setBounds(8, 8, 42, 42); // Posición original
            add(lblBadge);
        }
        
        // B. MAYORISTA (Top-Derecha)
        boolean aplicaMayorista = producto.isTieneDescuentoVolumen();
        boolean hayStockSuficiente = producto.getStock() >= producto.getCantidadParaDescuento();
        
        if (aplicaMayorista && (hayStockSuficiente || esModoAdmin)) {
            String texto = "x" + producto.getCantidadParaDescuento();
            JLabel lblMayoreo = UIFabric.crearBadge(texto, Tema.BADGE_MAYORISTA, Color.WHITE);
            
            // CORRECCIÓN: Usar ancho real del texto
            Dimension d = lblMayoreo.getPreferredSize();
            int anchoReal = Math.max(28, d.width + 2); // Mínimo 28px para que sea casi cuadrado si es "x6"
            
            // Calcular posición X para no chocar con el botón '+'
            int margenBtn = (!esModoAdmin && producto.getStock() > 0) ? 45 : 10;
            int x = 180 - margenBtn - anchoReal;
            
            lblMayoreo.setBounds(x, 12, anchoReal, 20);
            add(lblMayoreo);
        }
        // C. BOTÓN AGREGAR (+) 
        // Solo se agrega si NO es admin Y si hay stock (Lógica original simplificada)
        if (!esModoAdmin && producto.getStock() > 0) {
            JButton btnPlus = UIFabric.crearBotonIcono("+");
            btnPlus.setBounds(142, 8, 30, 30);
            btnPlus.addActionListener(e -> accionClick.run());
            add(btnPlus);
        }


        // =================================================================
        // 4. PRECIOS Y TEXTOS
        // =================================================================
        
        // Precio Dólar
        JLabel lblPrecio = new JLabel();
        lblPrecio.setBounds(12, 170, 160, 20);
        lblPrecio.setFont(Tema.FUENTE_PRECIO);

        if (producto.isEnOfertaFlash()) {
            lblContadorTiempo = new JLabel("Calculando...");
            lblContadorTiempo.setFont(Tema.FUENTE_PEQUENA.deriveFont(Font.ITALIC, 10f));
            
            lblContadorTiempo.setForeground(new Color(230, 126, 34));
            lblContadorTiempo.setBounds(12, 226, 160, 15);
            add(lblContadorTiempo);

            lblPrecio.setText("<html><font color='gray'><s>" + Formato.dinero(producto.getPrecio()) +
                    "</s></font> <font color='#E74C3C'>" + Formato.dinero(producto.getPrecioOferta()) + "</font></html>");
            
            iniciarContadorOferta();
        } else {
            lblPrecio.setText(Formato.dinero(producto.getPrecio()));
            lblPrecio.setForeground(Tema.TEXTO_OSCURO); // Usando Tema
        }
        add(lblPrecio);

        // Precio Bs (Referencial)
        double precioFinalUSD = producto.isEnOfertaFlash() ? producto.getPrecioOferta() : producto.getPrecio();
        double precioBs = precioFinalUSD * tasaCambio;
        JLabel lblRefBs = new JLabel(String.format("Bs. %.2f"+" Sin IVA", precioBs));
        lblRefBs.setBounds(12, 190, 160, 15);
        lblRefBs.setFont(Tema.FUENTE_PEQUENA.deriveFont(10f)); // Fuente pequeña del Tema
        lblRefBs.setForeground(Tema.TEXTO_GRIS);
        add(lblRefBs);

        // Nombre
        JLabel lblNombre = new JLabel("<html>" + producto.getNombre() + "</html>");
        lblNombre.setBounds(12, 208, 160, 35);
        lblNombre.setVerticalAlignment(SwingConstants.TOP);
        lblNombre.setFont(Tema.FUENTE_NORMAL);
        lblNombre.setForeground(Tema.TEXTO_OSCURO);
        add(lblNombre);
        
        // Categoría
        JLabel lblCat = new JLabel(producto.getCategoria());
        lblCat.setBounds(12, 245, 100, 15);
        lblCat.setFont(Tema.FUENTE_PEQUENA.deriveFont(10f));
        lblCat.setForeground(Tema.TEXTO_GRIS);
        add(lblCat);
        
        // Stock (Solo si hay stock, para no chocar con Agotado/Inactivo si coinciden)
        if (producto.getStock() > 0) {
            String textoStock = "Disp: " + producto.getStock();
            JLabel lblStock = new JLabel(textoStock);
            lblStock.setBounds(110, 240, 60, 15); // Tu posición original
            lblStock.setHorizontalAlignment(SwingConstants.RIGHT);
            lblStock.setFont(Tema.FUENTE_PEQUENA.deriveFont(Font.BOLD, 10f));
            
            // Color rojo si queda poco
            if (producto.getStock() < 5) lblStock.setForeground(Tema.BADGE_STOCK_BAJO);
            else lblStock.setForeground(Tema.TEXTO_GRIS);
            
            add(lblStock);
        }
    }
    private void iniciarContadorOferta() {
        LocalDateTime fechaFin = producto.getFechaFinOferta();
        if (fechaFin == null) {
            lblContadorTiempo.setText("Tiempo limitado");
            return;
        }

        ActionListener tarea = e -> {
            LocalDateTime ahora = LocalDateTime.now();
            if (ahora.isAfter(fechaFin)) {
                lblContadorTiempo.setText("¡Finalizó!");
                lblContadorTiempo.setForeground(Color.GRAY);
                if (timerOferta != null) timerOferta.stop();
            } else {
                Duration duracion = Duration.between(ahora, fechaFin);
                long dias = duracion.toDays();
                long horas = duracion.toHoursPart();
                long mins = duracion.toMinutesPart();
                
                String texto = (dias > 0) ? String.format("%dd %02dh %02dm", dias, horas, mins) 
                                          : String.format("%02dh %02dm", horas, mins);
                lblContadorTiempo.setText("Termina en: " + texto);
            }
        };
        tarea.actionPerformed(null); // Ejecutar ya
        timerOferta = new Timer(60000, tarea);
        timerOferta.start();
    }
}
