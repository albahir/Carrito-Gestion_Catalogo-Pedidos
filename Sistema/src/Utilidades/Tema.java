package Utilidades;

import java.awt.Color;
import java.awt.Font;

public class Tema {
    // --- PALETA PRINCIPAL ---
    public static final Color OBSIDIAN = new Color(38, 38, 40);
    public static final Color LAVENDER = new Color(198, 195, 242);
    public static final Color BLANCO = Color.WHITE;
    public static final Color FONDO_GLOBAL = new Color(245, 246, 250);
    
    // --- ESTADOS Y SEMÁNTICA ---
    public static final Color EXITO = new Color(39, 174, 96);      // Verde fuerte
    public static final Color EXITO_BG = new Color(209, 231, 221); // Verde fondo suave
    public static final Color ROJO_ERROR = new Color(231, 76, 60);
    public static final Color ERROR = new Color(220, 53, 69);      // Rojo
    public static final Color ERROR_BG = new Color(255, 235, 238); // Rojo fondo suave
    public static final Color ADVERTENCIA = new Color(255, 193, 7);// Amarillo/Naranja
    public static final Color INFO = new Color(23, 162, 184);      // Cyan/Azul Info
    
    // --- BADGES Y ETIQUETAS ---
    public static final Color BADGE_FLASH = new Color(255, 193, 7); // Amarillo Oro
    public static final Color BADGE_MAYORISTA = new Color(33, 150, 243); // Azul Material
    public static final Color BADGE_STOCK_BAJO = new Color(231, 76, 60); // Rojo Alerta
    public static final Color AGOTADO_BG = new Color(100, 100, 100); // Gris Oscuro
    
    // --- TEXTOS ---
    public static final Color TEXTO_OSCURO = OBSIDIAN;
    public static final Color TEXTO_GRIS = new Color(150, 150, 160);
    public static final Color TEXTO_GRIS_OSCURO = new Color(100, 100, 110);
    
    // --- COMPONENTES UI ---
    public static final Color BORDE_INPUT = new Color(170, 170, 180);
    public static final Color SELECCION_TABLA = new Color(230, 240, 255);
    public static final Color LINEA_SEPARADOR = new Color(230, 230, 230);

    // --- FUENTES ---
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_MONOSPACE = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FUENTE_PRECIO = new Font("Segoe UI", Font.BOLD, 14);
}