package EntidadesCatalogo;

import Utilidades.Validaciones;

public class Cliente {
    
    // Atributos
    private int idCliente;
    private String cedula; // Identificador único legal
    private String nombreCompleto;
    private String telefono;
    private String correo;
    private String direccion;
    private int comprasRealizadas;

    // Constructor Vacío
    public Cliente() {
        this.nombreCompleto = "Sin Nombre";
        this.direccion = "Sin Dirección";
    }

    // Constructor Completo
    public Cliente(int idCliente, String cedula, String nombreCompleto, String telefono, String correo, String direccion,int comprasRealizadas) {
       this(); // Carga defaults
        
        // Usamos los setters para validar al construir
        if (!setIdCliente(idCliente)) this.idCliente = 0;
        
        if (!setCedula(cedula)) this.cedula = "0000000000"; // Cédula dummy si falla
        
        if (!setNombreCompleto(nombreCompleto)) this.nombreCompleto = "CLIENTE INVALIDO";
        
        if (!setTelefono(telefono)) this.telefono = "0000000000";
        
        if (!setCorreo(correo)) this.correo = "correo@invalido.com";
        
        setDireccion(direccion); 
        this.comprasRealizadas=0;
        
    }

    // --- Getters y Setters ---

// 1. ID: Debe ser positivo
  public boolean setIdCliente(int id) {
        if (id <= 0) return false;
        this.idCliente = id;
        return true;
    }
    
    // Sobrecarga útil para formularios
    public boolean setIdCliente(String idTexto) {
        try { return setIdCliente(Integer.parseInt(idTexto)); } 
        catch (Exception e) { return false; }
    }

    // 2. CÉDULA: No vacía y formato numérico (ajustar regex según tu país)
 public boolean setCedula(String cedula) {
        if (!Validaciones.esCedulaValida(cedula)) return false;
        this.cedula = cedula.trim().toUpperCase();
        return true;
    }

    public boolean setNombreCompleto(String nombre) {
        if (!Validaciones.esNombreValido(nombre)) return false;
        this.nombreCompleto = nombre.trim().toUpperCase();
        return true;
    }

    // 4. TELÉFONO: Solo Números (y quizás el símbolo +)
public boolean setTelefono(String telefono) {
        if (!Validaciones.esTelefonoValido(telefono)) return false;
        this.telefono = telefono.trim();
        return true;
    }

    public boolean setCorreo(String correo) {
        if (!Validaciones.esEmailValido(correo)) return false;
        this.correo = correo.trim().toLowerCase(); // Correos siempre en minúscula
        return true;
    }

    // 6. DIRECCIÓN: Suele permitir todo (letras, números #, guiones -)
    public void setDireccion(String direccion) {
        if (direccion == null) direccion = "";
        this.direccion = direccion.trim().toUpperCase();
    }
    public void setComprasRealizadas(int comprasRealizadas) {
        this.comprasRealizadas = comprasRealizadas;
    }

    // GETTERS
    
    public int getIdCliente() { return idCliente; }
    public String getCedula() { return cedula; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public int getComprasRealizadas() { return comprasRealizadas; }
    
    public void incrementarCompras() { 
        this.comprasRealizadas++; 
    }
  
    // MÉTODOS PARA LISTAS (Duplicados)
    

    @Override
    public String toString() {
        
        return cedula + " - " + nombreCompleto;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.idCliente;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        // Dos clientes son iguales si tienen el mismo ID
        final Cliente other = (Cliente) obj;
        return this.idCliente == other.idCliente;
    }
    }
