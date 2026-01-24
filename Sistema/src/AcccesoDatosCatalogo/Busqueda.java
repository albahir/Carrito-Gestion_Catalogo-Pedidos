package AcccesoDatosCatalogo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Herramienta genérica para buscar en cualquier lista.
 * <T> significa que funciona con cualquier Tipo de objeto (Producto, Cliente, String, etc.)
 */
public class Busqueda {

    
    public static <T> T buscarUno(List<T> lista, Predicate<T> condicion) {
        for (T elemento : lista) {
            // El método .test() ejecuta la condición que le enviemos
            if (condicion.test(elemento)) {
                return elemento;
            }
        }
        return null; 
    }

    // 2. BUSCAR VARIOS (Devuelve una lista con todos los que cumplan)
    public static <T> ArrayList<T> buscarVarios(List<T> lista, Predicate<T> condicion) {
        ArrayList<T> resultados = new ArrayList<>();
        
        for (T elemento : lista) {
            if (condicion.test(elemento)) {
                resultados.add(elemento);
            }
        }
        return resultados;
    }
}