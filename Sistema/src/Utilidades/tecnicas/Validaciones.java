package Utilidades.tecnicas;

import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.text.*;

public class Validaciones {
// Cédula: Acepta V-12345678, E-..., J-..., o solo números y guiones
    private static final String REGEX_CEDULA = "^[a-zA-Z0-9-]+$";
    
    // Nombre: Letras, espacios y tildes. NO números.
    private static final String REGEX_NOMBRE = "^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$";
    
    // Teléfono: Números, guiones, espacios y símbolo +
    private static final String REGEX_TELEFONO = "^[0-9+\\-\\s]+$";
    
    // Email: Estándar
    private static final String REGEX_EMAIL = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    
    public static void aplicarFiltro(JTextField campo, int maxCaracteres, String tipo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            
            // Define qué caracteres son válidos según el tipo
            boolean esValido(String texto) {
                if (tipo.equals("NUMEROS")) return texto.matches("[0-9]+"); // Solo dígitos
                if (tipo.equals("LETRAS")) return texto.matches("[a-zA-ZñÑáéíóúÁÉÍÓÚ ]+"); // Letras y espacios
                return true; // "TODO" acepta cualquier cosa
            }

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                // Validación al escribir normal
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= maxCaracteres && esValido(string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                // Validación al pegar texto o reemplazar
                if (text == null) return;
                // Calculamos cuánto medirá el texto final
                int nuevaLongitud = fb.getDocument().getLength() - length + text.length();
                
                if (nuevaLongitud <= maxCaracteres && esValido(text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
    public static void forzarMayusculas(JTextField campo) {
    ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null) super.insertString(fb, offset, string.toUpperCase(), attr);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null) super.replace(fb, offset, length, text.toUpperCase(), attrs);
        }
    });
}
   public static boolean esCedulaValida(String texto) {
        return texto != null && texto.matches(REGEX_CEDULA);
    }

    public static boolean esNombreValido(String texto) {
        return texto != null && texto.matches(REGEX_NOMBRE);
    }

    public static boolean esTelefonoValido(String texto) {
        return texto != null && texto.matches(REGEX_TELEFONO);
    }

    public static boolean esEmailValido(String texto) {
         return texto != null && Pattern.matches(REGEX_EMAIL, texto);
    }
    public static void limitarInput(JTextField txt, int max, String tipo) {
        aplicarFiltro(txt, max, tipo);
    }
}
