package Controladores;

import AcccesoDatosCatalogo.GestionProducto;
import EntidadesCatalogo.Producto;
import Utilidades.MensajesUI;
import VistasCatalogo.PanelCatalogo;
import VistasCatalogo.PanelFormulario;
import java.time.LocalDateTime;
import javax.swing.JFrame;

public class ControladorInventario {

    // Referencias a lo que este controlador debe manejar
    private final GestionProducto gp;
    private final PanelCatalogo panelCatalogo;
    private final PanelFormulario panelFormulario;
    private final JFrame parentFrame; // Para mostrar los mensajes

    // Estado interno
    private Producto productoEnEdicion = null;

    public ControladorInventario(JFrame parent, GestionProducto gp, PanelCatalogo panelCatalogo, PanelFormulario panelFormulario) {
        this.parentFrame = parent;
        this.gp = gp;
        this.panelCatalogo = panelCatalogo;
        this.panelFormulario = panelFormulario;
    }

    // 1. Cuando haces clic en un producto del catálogo (Modo Admin)
    public void cargarProductoParaEditar(Producto p) {
        this.productoEnEdicion = p;
        panelFormulario.cargarProducto(p);
    }

    // 2. Lógica de Guardar
   // 2. Lógica de Guardar
    public void guardar() {
        try {
            // Recoger datos de la vista
            String nombre = panelFormulario.getNombre();
            String precioStr = panelFormulario.getPrecio();
            String cat = panelFormulario.getCategoria();
            boolean activo = panelFormulario.isActivo();
            String stockStr = panelFormulario.getStock();
            String desc = panelFormulario.getDescripcion();
            boolean esFlash = panelFormulario.isOfertaFlash();
            String precioFlashStr = panelFormulario.getPrecioOferta();
           LocalDateTime fechaVencimiento = panelFormulario.getFechaFinOferta(); 
            String rutaImagen = panelFormulario.getRutaImagen();
            boolean esMayorista = panelFormulario.isDescVolumen();
            int cantMayorista = 6; 
            double porcMayorista = 0;
            int idActual = (productoEnEdicion == null) ? -1 : productoEnEdicion.getIdProducto();
           
            if (gp.existeNombre(nombre.trim(), idActual)) {
                MensajesUI.mostrarError(parentFrame, "Ya existe otro producto con el nombre: " + nombre);
                return; 
            }
            try {
                cantMayorista = Integer.parseInt(panelFormulario.getCantVolumen());
                porcMayorista = Double.parseDouble(panelFormulario.getPorcVolumen());
            } catch (Exception e) {}
            // Validaciones básicas antes de convertir números
            if (nombre.isEmpty() || precioStr.isEmpty()) {
                MensajesUI.mostrarInfo(parentFrame, "El nombre y el precio son obligatorios.");
                return;
            }

            // Conversiones numéricas
            double precio = Double.parseDouble(precioStr);
            int stock = stockStr.isEmpty() ? 0 : Integer.parseInt(stockStr); // Evitar error si vacío
            double precioFlash = precioFlashStr.isEmpty() ? 0.0 : Double.parseDouble(precioFlashStr);

            if (productoEnEdicion == null) {
                // --- CASO 1: CREAR NUEVO ---
                // Primero creamos el objeto (NO uses productoEnEdicion aquí porque es null)
                Producto nuevo = new Producto(0, nombre, precio, cat, activo);
                
                // Ahora le metemos los datos extra
                nuevo.setStock(stock);
                nuevo.setDescripcion(desc);
                nuevo.setEnOfertaFlash(esFlash);
                nuevo.setPrecioOferta(precioFlash);
                nuevo.setFechaFinOferta(fechaVencimiento);
                nuevo.setRutaImagen(rutaImagen);
                nuevo.setTieneDescuentoVolumen(esMayorista);
                nuevo.setCantidadParaDescuento(cantMayorista);
                nuevo.setPorcentajeDescuentoVolumen(porcMayorista);
                if (gp.agregarProducto(nuevo)) {
                    MensajesUI.mostrarInfo(parentFrame, "Producto creado exitosamente.");
                    panelCatalogo.cargarProductos();
                    recargarVista();
                } else {
                    MensajesUI.mostrarInfo(parentFrame, "Error al guardar en base de datos.");
                }
            } else {
                // --- CASO 2: ACTUALIZAR EXISTENTE ---
                // Aquí sí usamos productoEnEdicion porque ya existe
                productoEnEdicion.setNombre(nombre);
                productoEnEdicion.setPrecioUnitario(precio);
                productoEnEdicion.setCategoria(cat);
                productoEnEdicion.setEstado(activo);
                
                // ¡IMPORTANTE! Faltaba actualizar estos campos al editar:
                productoEnEdicion.setStock(stock);
                productoEnEdicion.setDescripcion(desc);
                productoEnEdicion.setEnOfertaFlash(esFlash);
                productoEnEdicion.setPrecioOferta(precioFlash);
                productoEnEdicion.setFechaFinOferta(fechaVencimiento);
                productoEnEdicion.setRutaImagen(rutaImagen);
                productoEnEdicion.setTieneDescuentoVolumen(esMayorista);
                productoEnEdicion.setCantidadParaDescuento(cantMayorista);
                productoEnEdicion.setPorcentajeDescuentoVolumen(porcMayorista);
                
                if (gp.actualizarProducto(productoEnEdicion)) {
                    MensajesUI.mostrarInfo(parentFrame, "Producto actualizado correctamente.");
                    panelCatalogo.cargarProductos();
                    recargarVista();
                    
                } else {
                    MensajesUI.mostrarInfo(parentFrame, "Error al actualizar.");
                }
            }

        } catch (NumberFormatException e) {
            MensajesUI.mostrarInfo(parentFrame, "Verifique los números (Precio o Stock).");
        } catch (Exception e) {
            MensajesUI.mostrarInfo(parentFrame, "Error inesperado: " + e.getMessage());
        }
    }

    // 3. Lógica de Eliminar
    public void eliminar() {
        if (productoEnEdicion != null) {
            boolean confirm = MensajesUI.confirmar(parentFrame, "Eliminar Producto", 
                    "¿Estás seguro de borrar <b>" + productoEnEdicion.getNombre() + "</b>?");
            
            if (confirm) {
                if (gp.eliminarProducto(productoEnEdicion.getIdProducto())) {
                    recargarVista();
                } else {
                    MensajesUI.mostrarInfo(parentFrame, "No se pudo eliminar el producto.");
                }
            }
        } else {
            MensajesUI.mostrarInfo(parentFrame, "Seleccione un producto primero para eliminar.");
        }
    }

    // 4. Lógica de Limpiar
    public void limpiar() {
        productoEnEdicion = null;
        panelFormulario.limpiar();
    }

    // Helper privado
    private void recargarVista() {
        panelCatalogo.cargarProductos(); // Refresca la lista visual
        limpiar(); // Resetea el formulario
    }
}