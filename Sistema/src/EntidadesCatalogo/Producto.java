/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EntidadesCatalogo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author lenovo
 */
public class Producto {
    
    //identidad
    private int idProducto;
    private String sku;
    private String nombre;
    private String descripcion;
    private String categoria;
    //Negocio
    private double precio;
    private double porcentajeDescuento;
    private int stock;
    private int stockMinimo;
    private boolean tieneDescuentoVolumen;
    private int cantidadParaDescuento; 
    private double porcentajeDescuentoVolumen; 
    private boolean enOfertaFlash; // true = Activar precio especial
    private double precioOferta;
    //Visuales
    private LocalDate fechaIngreso;
    private LocalDateTime fechaFinOferta;
    private boolean estado;
    private String rutaImagen;
  
    public Producto() {
        this.estado = true;
        this.fechaIngreso = LocalDate.now();
        this.stockMinimo = 5;
    }
    
  // Constructor Completo 
    // Constructor COMPLETO (Usado por GestorArchivos/Base de Datos)
    public Producto(int idProducto, String sku, String nombre, String descripcion, 
                    String categoria, double precio, int stock, java.time.LocalDate fechaIngreso, 
                    boolean estado, String rutaImagen) {
        
        this.idProducto = idProducto;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.rutaImagen = rutaImagen;
    }
   
    public Producto(int id, String nombre, double precio, String categoria, boolean estado) {
        this(); // Llama al constructor vacío para poner fechas y defaults
        this.idProducto = id;
        this.setNombre(nombre);
        this.setPrecio(precio); // Usa la validación existente
        this.setCategoria(categoria);
        this.estado = estado;
        // Valores default para lo que falta
        this.descripcion = ""; 
        this.stock = 0; 
        this.fechaIngreso= LocalDate.now();
    }
   // VALIDACIONES ESTRICTAS 

    // 1. Categoría: SOLO LETRAS (Ideal para evitar "Electrónica123")
    public boolean setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return false; 
        }
        // REGEX: ^ = inicio, [a-zA-Z...] = caracteres permitidos, + = uno o más, $ = fin
        
        if (!categoria.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            return false; 
        }
        this.categoria = categoria.trim();
        return true;
    }

    // 2. Nombre: Permite letras y números, pero no vacío.
    public boolean setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false; 
        }
        
        this.nombre = nombre.trim();
        return true;
    }

    // 3. Precio negativo
    public boolean setPrecio(double precio) {
        if (precio < 0) {
            return false; 
        }
        this.precio = precio;
        return true;
    }

    // 4. Stock negativo
    public boolean setStock(int stock) {
        if (stock < 0) {
            return false; 
        }
        this.stock = stock;
        return true;
    }

    // 5.Id Positivo
    public boolean setIdProducto(int id) {
        if (id <= 0) {
            return false;
        }
        this.idProducto = id;
        return true;
    }
 public void setFechaFinOferta(LocalDateTime fechaFinOferta) {
        this.fechaFinOferta = fechaFinOferta;
    }
    // EVITAR DUPLICADOS 
  
    
    @Override
    public int hashCode() {
        
        int hash = 7;
        hash = 59 * hash + this.idProducto;
        return hash;
    }
    

    @Override
    public boolean equals(Object obj) {
        // Compara si dos objetos son "el mismo producto"
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final Producto other = (Producto) obj;
        
        // REGLA: Son duplicados si tienen el mismo ID
        if (this.idProducto != other.idProducto) {
            return false;
        }
        return true;
    }
public boolean isOfertaActiva() {
        if (!enOfertaFlash) return false; // Si el check está apagado, no hay oferta
        if (fechaFinOferta == null) return true; // Si no hay fecha limite, es indefinida (opcional)
        
        // La oferta es válida si AHORA es antes de la FECHA FIN
        return LocalDateTime.now().isBefore(fechaFinOferta);
    }
public boolean isEnOfertaFlash() {
        return isOfertaActiva();
    }
    
    // Método crudo para el formulario (para saber si el checkbox debe estar marcado)
    public boolean isEnOfertaFlashConfigurada() {
        return enOfertaFlash;
    }
    // --- Getters y otros Setters simples ---
    public int getIdProducto() { return idProducto; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getNombre() { return nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getCategoria() { return categoria; }
    
    public double getPrecio() { return precio; }
    
    public int getStock() { return stock; }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    
    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
    
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    
    
    public void setEnOfertaFlash(boolean enOfertaFlash) {
        this.enOfertaFlash = enOfertaFlash;
    }

    public double getPrecioOferta() {
        return precioOferta;
    }

    public boolean setPrecioOferta(double precioOferta) {
        // El precio oferta debe ser positivo y lógicamente menor al precio normal
        if (precioOferta < 0) return false;
        
        // Opcional: Validar que sea menor al precio real
        if (precioOferta >= this.getPrecio()) {
            // Advertencia: La oferta es más cara que el precio normal
        }
        
        this.precioOferta = precioOferta;
        return true;
    }
    public boolean isTieneDescuentoVolumen() {
        return tieneDescuentoVolumen;
    }

    public void setTieneDescuentoVolumen(boolean tieneDescuentoVolumen) {
        this.tieneDescuentoVolumen = tieneDescuentoVolumen;
    }

    public int getCantidadParaDescuento() {
        return cantidadParaDescuento;
    }

    public void setCantidadParaDescuento(int cantidadParaDescuento) {
        this.cantidadParaDescuento = cantidadParaDescuento;
    }

    public double getPorcentajeDescuentoVolumen() {
        return porcentajeDescuentoVolumen;
    }
public LocalDateTime getFechaFinOferta() {
        return fechaFinOferta;
    }
    public void setPorcentajeDescuentoVolumen(double porcentajeDescuentoVolumen) {
        this.porcentajeDescuentoVolumen = porcentajeDescuentoVolumen;
    }
    public double getPrecioUnitario() {
        return this.getPrecio(); // Redirige a tu método original
    }

    public void setPrecioUnitario(double precio) {
        this.setPrecio(precio); // Redirige a tu validación original
    }
    @Override
   public String toString() {
    String info = nombre + " - $" + precio;
    if (enOfertaFlash) {
        info += " [¡OFERTA $" + precioOferta + "!]";
    }
    return info;
}
}

