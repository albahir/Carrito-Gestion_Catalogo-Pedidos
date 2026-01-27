package Utilidades.UI;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;

public class ImagenUtils {

    /**
     * Carga una imagen desde una ruta, la escala manteniendo la proporción
     * y devuelve un ImageIcon listo para usar en un JLabel.
     */
    public static ImageIcon cargarImagenEscalada(String ruta, int anchoMax, int altoMax) {
        // 1. Validar que la ruta no sea nula ni vacía y que el archivo exista
        if (ruta == null || ruta.isEmpty() || !new File(ruta).exists()) {
            return null; // Retornamos null para que la vista ponga el icono por defecto "📷"
        }

        try {
            // 2. Cargar la imagen original
            ImageIcon original = new ImageIcon(ruta);
            int w = original.getIconWidth();
            int h = original.getIconHeight();

            // Si la imagen está corrupta (ancho 0), salir
            if (w <= 0 || h <= 0) return null;

            // 3. Lógica de Proporción (Aspect Ratio)
            // Calculamos qué tanto hay que achicarla para que quepa en la tarjeta
            double ratio = Math.min((double) anchoMax / w, (double) altoMax / h);
            
            int nuevoAncho = (int) (w * ratio);
            int nuevoAlto = (int) (h * ratio);

            // 4. Escalar y devolver
            Image img = original.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
            
        } catch (Exception e) {
            // Si falla algo al leer el archivo, retornamos null
            return null;
        }
    }
}