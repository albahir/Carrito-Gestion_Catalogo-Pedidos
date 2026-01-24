package AcccesoDatosCatalogo;

import EntidadesCatalogo.Producto;
import java.util.List;
import AcccesoDatosCatalogo.repositorio.RepositorioProductoTXT;
public class GestionProducto {

    private final List<Producto> listaProductos;
    private final RepositorioProductoTXT repositorio;
    public GestionProducto() {
        this.repositorio= new RepositorioProductoTXT();
        // Al iniciar, cargamos todo del TXT a la memoria
        this.listaProductos = repositorio.cargarProductos();
    }

    public List<Producto> obtenerTodos() {
        return listaProductos;
    }

   public Producto buscarPorId(int id) {
        return Busqueda.buscarUno(listaProductos, p -> p.getIdProducto() == id);
    }
    // --- AQUÍ ESTÁ LA CLAVE PARA QUE SE GUARDE LA FOTO ---
    public boolean agregarProducto(Producto nuevo) {
        try {
            // 1. Generar ID automático (El último + 1)
            int maxId = 0;
            for (Producto p : listaProductos) {
                if (p.getIdProducto() > maxId) maxId = p.getIdProducto();
            }
            nuevo.setIdProducto(maxId + 1);

            // 2. Agregar a la lista en memoria
            listaProductos.add(nuevo);

            // 3. ¡IMPORTANTE! GUARDAR EN DISCO INMEDIATAMENTE
            repositorio.guardarProductos(listaProductos);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarProducto(Producto pEditado) {
        try {
            for (int i = 0; i < listaProductos.size(); i++) {
                Producto pOriginal = listaProductos.get(i);
                
                if (pOriginal.getIdProducto() == pEditado.getIdProducto()) {
                    // 1. Reemplazamos el objeto viejo por el nuevo en la lista
                    listaProductos.set(i, pEditado);
                    
                    // 2. ¡IMPORTANTE! SOBRESCRIBIR EL ARCHIVO TXT
                    // Si no haces esto, la foto se pierde al recargar.
                    repositorio.guardarProductos(listaProductos);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarProducto(int idProducto) {
        try {
            for (int i = 0; i < listaProductos.size(); i++) {
                if (listaProductos.get(i).getIdProducto() == idProducto) {
                    // 1. Borrar de la lista
                    listaProductos.remove(i);
                    
                    // 2. Guardar cambios en el archivo
                    repositorio.guardarProductos(listaProductos);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Validar si existe un nombre (Excluyendo un ID específico para cuando editamos)
   public boolean existeNombre(String nombre, int idExcluir) {
        // Busca si hay algun producto con el mismo nombre PERO diferente ID
        Producto encontrado = Busqueda.buscarUno(listaProductos, p -> 
            p.getNombre().equalsIgnoreCase(nombre) && p.getIdProducto() != idExcluir
        );
        return encontrado != null;
    }
}