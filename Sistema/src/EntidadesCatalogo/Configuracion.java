package EntidadesCatalogo;

public class Configuracion {
    
    private double ivaPorcentaje;
    private double tasaCambio;
    
    // Atributos para descuentos
    private double umbralMontoDescuento;
    private double porcentajeDescuentoGlobal;

    // Constructor por defecto (Valores iniciales)
    public Configuracion() {
        this.ivaPorcentaje = 0.16; // 16% por defecto
        this.tasaCambio = 308.0;
        this.umbralMontoDescuento = 0;
        this.porcentajeDescuentoGlobal = 0;
    }

    // Constructor cargado
    public Configuracion(double iva, double tasa) {
        this.ivaPorcentaje = iva;
        this.tasaCambio = tasa;
    }

    // Getters y Setters
    public double getIvaPorcentaje() { return ivaPorcentaje; }
    public void setIvaPorcentaje(double ivaPorcentaje) { this.ivaPorcentaje = ivaPorcentaje; }

    public double getTasaCambio() { return tasaCambio; }
    public void setTasaCambio(double tasaCambio) { this.tasaCambio = tasaCambio; }

    public double getUmbralMontoDescuento() { return umbralMontoDescuento; }
    public void setUmbralMontoDescuento(double umbral) { this.umbralMontoDescuento = umbral; }

    public double getPorcentajeDescuentoGlobal() { return porcentajeDescuentoGlobal; }
    public void setPorcentajeDescuentoGlobal(double porcentaje) { this.porcentajeDescuentoGlobal = porcentaje; }
}