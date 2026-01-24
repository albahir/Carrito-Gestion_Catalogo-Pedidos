package EntidadesCatalogo;

public class DetalleCompra {
    
    // --- Atributos ---
    private final Producto producto;     
    private int cantidad;          
    
    
    private double precioUnitario; 
    private double descuentoAplicado; 
    
    private double subTotal;       

    
  // --- Constructor ---
    public DetalleCompra(Producto producto, int cantidadSolicitada) {
       
        if (producto == null) {
            this.producto = new Producto(); // Producto vacío para evitar NullPointer
            this.cantidad = cantidadSolicitada;
            return;
        }
        
        this.producto = producto;
       this.precioUnitario = producto.getPrecio(); 
        this.descuentoAplicado = 0;
        
     
        
        if (!setCantidad(cantidadSolicitada)) {
            if (producto.getStock() > 0) {
                this.cantidad = 1;
            } else {
                this.cantidad = 0; 
            }
            
        }
        calcularSubTotal();
    }

    // --- Lógica de Negocio ---

  private void calcularSubTotal() {
        double precioFinalUnitario = this.precioUnitario - this.descuentoAplicado;
        this.subTotal = precioFinalUnitario * this.cantidad;
    }

    public boolean setCantidad(int nuevaCantidad) {
        // Regla 1: Debe ser positivo
        if (nuevaCantidad <= 0) {
            return false; 
        }
        
        // Regla 2: No puede superar el stock disponible en bodega
        if (nuevaCantidad > producto.getStock()) {
            return false; 
        }
        
        // Si pasa los filtros:
        this.cantidad = nuevaCantidad;
        calcularSubTotal(); // Recalculamos el dinero
        return true;
    }

    // --- NUEVO SETTER NECESARIO ---
    public void setDescuentoAplicado(double descuentoUnitario) {
        // Validación básica
        if (descuentoUnitario < 0) descuentoUnitario = 0;
        
        this.descuentoAplicado = descuentoUnitario;
        
        // ¡IMPORTANTE! Al cambiar el descuento, hay que recalcular cuánto paga el cliente
        calcularSubTotal(); 
    }
    public boolean agregarCantidad(int cantidadExtra) {
        if (cantidadExtra <= 0) {
            return false;
        }
        
        int totalDeseado = this.cantidad + cantidadExtra;
        
       
        if (totalDeseado > producto.getStock()) {
            return false;
        }
        
        this.cantidad = totalDeseado;
        calcularSubTotal();
        return true;
    }
    
    public static boolean verificarStockSuficiente(Producto p, int cantidadRequerida) {
        if (p == null) return false;
        if (cantidadRequerida <= 0) return false;
        return p.getStock() >= cantidadRequerida;
    }

    // --- Getters ---

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getDescuentoAplicado() { return descuentoAplicado; }
    public double getSubTotal() { return subTotal; }

    public void aplicarDescuentoExtra(double porcentaje) {
        // 1. Calculamos cuánto dinero se descuenta por unidad con este nuevo porcentaje
        this.descuentoAplicado = this.precioUnitario * porcentaje;
        
        // 2. ¡IMPORTANTE! Recalculamos el total de la línea
        // (Llama al método privado que ya teníamos)
        calcularSubTotal(); 
    }
    // --- Formatos ---

    @Override
    public String toString() {
        return String.format("%s x %d | Total: $%.2f", producto.getNombre(), cantidad, subTotal);
    }
    
    public String toArchivoFormat() {
        return producto.getIdProducto() + "," + cantidad + "," + precioUnitario;
    }

    public void setSubTotal(double doubleValue) {
       this.subTotal = subTotal;
    }
}
