package Utilidades.tecnicas;

public class Formato {
    
    // Convierte un double a formato moneda: 150.5 -> "$150.50"
    public static String dinero(double cantidad) {
        return String.format("$%.2f", cantidad);
    }
    
    // Formato para descuentos: 20 -> "-20%"
    public static String descuento(double porcentaje) {
        return String.format("-%.0f%%", porcentaje);
    }
    public static String precioTachado(double precioOriginal, double precioPagado) {
    if (precioPagado < precioOriginal) {
        return String.format("<html><font color='gray'><s>%s</s></font> <font color='black'>%s</font></html>", 
                dinero(precioOriginal), dinero(precioPagado));
    }
    return dinero(precioOriginal);
}
}