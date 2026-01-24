package VistasCatalogo;


import Utilidades.*;
import AcccesoDatosCatalogo.GestionCliente;
import EntidadesCatalogo.Cliente;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Pattern;

public class DialogoCliente extends JDialog {
    
    // Campos de texto
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextField txtCorreo;
    private JComboBox<String> cmbTipoCedula;
    private JComboBox<String> cmbPrefijoTelf;
    
    private final GestionCliente gestionCliente;
    private final JFrame parentFrame;
    private Cliente clienteCreado = null;

    public DialogoCliente(JFrame parent,GestionCliente gc) {
        super(parent, "Nuevo Cliente", true);
        this.parentFrame=parent;
        this.gestionCliente=gc;
        setSize(420, 580); // Un poco más alto para que respire
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Panel Principal Blanco
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40)); // Márgenes amplios
        setContentPane(panel);

        initUI(panel);
    }

    private void initUI(JPanel panel) {
        // 1. TÍTULO
        JLabel lblTitulo = new JLabel("Registrar Cliente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Tema.OBSIDIAN);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // 2. FORMULARIO (Central)
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

       JPanel pCedula = new JPanel(new BorderLayout(5, 0));
        pCedula.setBackground(Color.WHITE);
        
        cmbTipoCedula = new JComboBox<>(new String[]{"V", "E", "J", "G"});
        cmbTipoCedula.setBackground(Color.WHITE);
        cmbTipoCedula.setPreferredSize(new Dimension(60, 30));
        // Borde gris suave para que combine
        cmbTipoCedula.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 180))); 
        
        txtCedula = new JTextField();
        // Validamos solo números y máx 9 dígitos (para el cuerpo de la cédula)
        Validaciones.limitarInput(txtCedula, 9, "NUMEROS"); 
        
        pCedula.add(cmbTipoCedula, BorderLayout.WEST);
        pCedula.add(txtCedula, BorderLayout.CENTER);
        
        // Usamos crearPanelCampo pero le pasamos nuestro panel compuesto
        form.add(UIFabric.crearPanelCampo("Cédula / ID:", pCedula)); 
        form.add(Box.createVerticalStrut(15));

        txtNombre = new JTextField();
        form.add(UIFabric.crearPanelCampo("Nombre Completo:", txtNombre));
        Validaciones.limitarInput(txtNombre, 50, "LETRAS");
        form.add(Box.createVerticalStrut(15));

       JPanel pTelf = new JPanel(new BorderLayout(5, 0));
        pTelf.setBackground(Color.WHITE);
        
        String[] prefijos = {"0414", "0424", "0412", "0416", "0426", "0286", "0212"};
        cmbPrefijoTelf = new JComboBox<>(prefijos);
        cmbPrefijoTelf.setBackground(Color.WHITE);
        cmbPrefijoTelf.setPreferredSize(new Dimension(80, 30));
        cmbPrefijoTelf.setBorder(BorderFactory.createLineBorder(new Color(170, 170, 180)));
        
        txtTelefono = new JTextField();
        // Validamos a 7 dígitos exactos (el cuerpo del número)
        Validaciones.limitarInput(txtTelefono, 7, "NUMEROS"); 
        
        pTelf.add(cmbPrefijoTelf, BorderLayout.WEST);
        pTelf.add(txtTelefono, BorderLayout.CENTER);
        
        form.add(UIFabric.crearPanelCampo("Teléfono:", pTelf));
        form.add(Box.createVerticalStrut(15));

        txtCorreo = new JTextField();
        form.add(UIFabric.crearPanelCampo("Correo Electrónico:", txtCorreo));
        Validaciones.limitarInput(txtCorreo, 60, "TODO");
        form.add(Box.createVerticalStrut(15));

        txtDireccion = new JTextField();
        form.add(UIFabric.crearPanelCampo("Dirección:", txtDireccion));
        Validaciones.limitarInput(txtDireccion, 100, "TODO");

        // 3. BOTÓN GUARDAR (Sur)
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton btnGuardar = UIFabric.crearBotonPrincipal("Guardar Datos", Tema.OBSIDIAN, Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(300, 45));
        btnGuardar.addActionListener(e -> guardar());

        panelBoton.add(btnGuardar);

        // Agregar todo al panel principal
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        panel.add(panelBoton, BorderLayout.SOUTH);
    }

   

 private void guardar() {
       // 1. OBTENER DATOS Y CONCATENAR
        String tipoCed = (String) cmbTipoCedula.getSelectedItem();
        String numCed = txtCedula.getText().trim();
        // Resultado ej: "V-12345678"
        String cedulaFinal = tipoCed + "-" + numCed; 

        String nombre = txtNombre.getText().trim();
        
        String prefijo = (String) cmbPrefijoTelf.getSelectedItem();
        String numTelf = txtTelefono.getText().trim();
        // Resultado ej: "0414-1234567"
        String telefonoFinal = prefijo + "-" + numTelf; 
        
        String email = txtCorreo.getText().trim();

        // 1. CAMPOS OBLIGATORIOS
        if(cedulaFinal.isEmpty() || nombre.isEmpty()) {
            MensajesUI.mostrarInfo(parentFrame, "Cédula y Nombre son obligatorios.");
            return;
        }

        // 2. CANTIDAD MÍNIMA EN NOMBRE
        if (nombre.length() < 3) {
            MensajesUI.mostrarInfo(parentFrame, "El nombre es muy corto (Mínimo 3 letras).");
            return;
        }
        
        // 3. VALIDACIÓN DE TELÉFONO (Longitud fija si se requiere)
       if (numTelf.isEmpty() || numTelf.length() != 7) {
            MensajesUI.mostrarInfo(parentFrame, "El teléfono debe tener 7 dígitos (Ej: 1234567).");
            return;
        }

        // 4. VALIDACIÓN DE CORREO (Regex)
        // Solo valida si escribió algo. Si lo dejó vacío, lo permitimos.
        if (!email.isEmpty()&& !Validaciones.esEmailValido(email)) {
            String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            if (!Pattern.matches(emailRegex, email)) {
                MensajesUI.mostrarInfo(parentFrame, "El formato del correo es inválido.");
                return;
            }
        }

        // 5. VALIDAR DUPLICADOS
        boolean existe = false;
        
        // Obtenemos todos los clientes (Asumiendo que gestionCliente tiene el método obtenerTodos publico)
        // Si no tienes acceso a la lista directa, usa el método buscar normal pero limpia los datos antes
        
        // ESTRATEGIA SEGURA:
        // Iteramos sobre todos los clientes existentes
        for (Cliente c : gestionCliente.obtenerTodos()) {
            // Limpiamos la cédula de la base de datos (quitamos letras y guiones)
            String cedulaSoloNumerosDB = c.getCedula().replaceAll("[^0-9]", "");
            
            // Comparamos solo los números
            if (cedulaSoloNumerosDB.equals(numCed)) {
                existe = true;
                break;
            }
        }

        if (existe) {
            MensajesUI.mostrarInfo(parentFrame, 
                "<html><font color='red'><b>¡Duplicado!</b></font><br>La cédula terminada en " + numCed + " ya está registrada.</html>");
            return;
        }
        // Crear Cliente
        clienteCreado = new Cliente(0, cedulaFinal, nombre, telefonoFinal, email, txtDireccion.getText().trim(), 0);
        dispose();
        boolean guardadoExitoso = gestionCliente.agregarCliente(clienteCreado);
        
        if (guardadoExitoso) {
            MensajesUI.mostrarInfo(parentFrame, "Cliente registrado y guardado exitosamente.");
            dispose();
        } else {
            MensajesUI.mostrarError(parentFrame, "Error crítico al guardar en el archivo de clientes.");
            clienteCreado = null; 
        }
    }

    public Cliente mostrar() {
        setVisible(true);
        return clienteCreado;
    }
}