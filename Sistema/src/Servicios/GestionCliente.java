package Servicios;

import AcccesoDatosCatalogo.Busqueda;
import EntidadesCatalogo.Cliente;
import AcccesoDatosCatalogo.repositorio.RepositorioClienteTXT;
import java.util.List;
public class GestionCliente {
    
    private final List<Cliente> listaClientes;
private final RepositorioClienteTXT repositorio;
    public GestionCliente() {
       this.repositorio = new RepositorioClienteTXT();
        // Cargar datos usando el repositorio
        this.listaClientes = repositorio.cargarClientes();
    }

    // --- AGREGAR ---
    public boolean agregarCliente(Cliente nuevo) {
        if (nuevo == null) return false;

        int maxId = 0;
        for (Cliente c : listaClientes) {
            if (c.getIdCliente() > maxId) {
                maxId = c.getIdCliente();
            }
        }
        nuevo.setIdCliente(maxId + 1);
      
        // 1. Validar ID duplicado
        Cliente existeId = Busqueda.buscarUno(listaClientes, c -> c.getIdCliente() == nuevo.getIdCliente());
        if (existeId != null) return false;
        // 2. Validar Cédula duplicada (Regla de negocio importante)
        Cliente existeCedula = Busqueda.buscarUno(listaClientes, c -> c.getCedula().equalsIgnoreCase(nuevo.getCedula()));
        if (existeCedula != null) {
            System.out.println("Error: Ya existe un cliente con la cédula " + nuevo.getCedula());
            return false;
        }

        // Agregar y Guardar
        listaClientes.add(nuevo);
        repositorio.guardarClientes(listaClientes);
        return true;
    }

    // --- EDITAR ---
    public boolean actualizarCliente(Cliente editado) {
        Cliente original = Busqueda.buscarUno(listaClientes, c -> c.getIdCliente() == editado.getIdCliente());
        
        if (original != null) {
            int index = listaClientes.indexOf(original);
            listaClientes.set(index, editado);
            repositorio.guardarClientes(listaClientes);
            return true;
        }
        return false;
    }

    // --- ELIMINAR ---
    public boolean eliminarCliente(int id) {
        Cliente aEliminar = Busqueda.buscarUno(listaClientes, c -> c.getIdCliente() == id);
        
        if (aEliminar != null) {
            listaClientes.remove(aEliminar);
            repositorio.guardarClientes(listaClientes);
            return true;
        }
        return false;
    }

    // --- BÚSQUEDAS ---
    
    public Cliente buscarPorId(int id) {
        return Busqueda.buscarUno(listaClientes, c -> c.getIdCliente() == id);
    }
    
    public Cliente buscarPorCedula(String cedula) {
        return Busqueda.buscarUno(listaClientes, c -> c.getCedula().equalsIgnoreCase(cedula));
    }
    
    public List<Cliente> buscarPorNombre(String nombre) {
        return Busqueda.buscarVarios(listaClientes, c -> c.getNombreCompleto().toLowerCase().contains(nombre.toLowerCase()));
    }

    public List<Cliente> obtenerTodos() {
        return listaClientes;
    }
    public Cliente buscarInteligente(String entrada) {
        if (entrada == null || entrada.trim().isEmpty()) return null;
        String busqueda = entrada.trim();

        // 1. Intento Directo
        Cliente c = buscarPorCedula(busqueda);
        if (c != null) return c;

        // 2. Intento con Prefijos (Si el usuario solo metió números)
        if (busqueda.matches("[0-9]+")) {
            String[] prefijos = {"V-", "E-", "J-", "G-"};
            for (String pre : prefijos) {
                c = buscarPorCedula(pre + busqueda);
                if (c != null) return c;
            }
        }
        return null;
    }
}