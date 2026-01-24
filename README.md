# 🛒 Sistema de Gestión de Carrito y Catálogo (POS)

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white) 
![NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white) 
![Status](https://img.shields.io/badge/Estado-Terminado-brightgreen?style=for-the-badge)

Este proyecto es una aplicación de escritorio robusta desarrollada en **Java Swing** que implementa un sistema de Punto de Venta (POS).  
Permite la gestión integral de un catálogo de productos, control de inventario en tiempo real, administración de clientes, procesamiento de carritos de compras y un registro histórico detallado de pedidos.

> **Nota:** La persistencia de datos se maneja mediante almacenamiento en archivos de texto (`.txt`), lo que facilita su despliegue inmediato sin necesidad de configurar bases de datos complejas.

---

## 📋 Requisitos Previos

Para ejecutar y compilar este proyecto, asegúrate de contar con lo siguiente en tu entorno:

* ☕ **Java Development Kit (JDK):** Versión 17 o superior.  
* 💻 **IDE Recomendado:** NetBeans (estructura nativa detectada), IntelliJ IDEA o Eclipse.  
* 🐙 **Control de Versiones:** Git.  
* 🛠️ **Sistema de Construcción:** Herramientas estándar (Ant, Maven o Gradle) según tu configuración local.  

---

## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio
Abre tu terminal y ejecuta:

```bash
git clone <URL_DEL_REPOSITORIO>
cd sistema-catalogo
2. Importar en el IDE
En NetBeans / IntelliJ / Eclipse:

Selecciona File > Open Project (o Import)

Navega hasta la carpeta raíz clonada.

El IDE detectará automáticamente la carpeta src.

3. Compilación
Desde el IDE:

Haz clic derecho sobre el proyecto.

Selecciona Clean and Build (Limpiar y Construir).

Desde terminal (Manual):

bash
javac -d build src/**/*.java
4. Ejecución ▶️
Localiza la clase principal Main.java dentro del paquete de vistas y ejecútala:

Clic derecho → Run File.

📦 Estructura del Proyecto
El código sigue una arquitectura por capas para separar responsabilidades:

🗂️ Modelo (EntidadesCatalogo): Clases del dominio como Producto, Cliente y Pedido.

🗄️ Datos (AcccesoDatosCatalogo): Lógica de negocio y repositorios TXT (RepositorioPedidosTXT, etc.).

🎮 Controladores: Intermediarios como ControladorVenta.

🎨 Vistas (VistasCatalogo): Interfaz gráfica Swing (PanelCarrito, DialogoPago, etc.).

🛠️ Utilidades: Herramientas transversales y componentes visuales (UIFabric).

🚀 Uso Básico
Inicio: Al abrir la app, se cargarán automáticamente los datos desde productos.txt y clientes.txt.

Identificación 👤: El sistema pedirá la cédula del cliente. Si no existe, se abrirá el DialogoCliente para registrarlo.

Carrito de Compras 🛒:

Navega por el catálogo y agrega productos.

Usa el PanelCarrito para modificar cantidades. El stock se valida en tiempo real.

Pago 💳:

Haz clic en Confirmar Pago.

Selecciona el método: Zelle, Pago Móvil, Efectivo o Punto de Venta.

Ingresa las referencias bancarias necesarias.

Finalización 🧾: Se genera un comprobante digital y la transacción se guarda en el Historial.

📝 Notas Adicionales
⚠️ Permisos: Asegúrate de que la carpeta del proyecto tenga permisos de Lectura/Escritura, ya que la app necesita crear y modificar los archivos .txt.

📸 Snapshots: El sistema guarda una "foto instantánea" de los precios y nombres de los productos al momento de la venta. Esto garantiza que el historial no se altere si cambias el catálogo en el futuro.

🗄️ Base de Datos: No requiere MySQL ni PostgreSQL. Todo es portable.

👨‍💻 Desarrollado en Java Swing
