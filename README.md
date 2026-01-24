# 🛒 Sistema de Gestión de Carrito y Catálogo (POS)

<div align="center">

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![NetBeans](https://img.shields.io/badge/NetBeans-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white)
![Status](https://img.shields.io/badge/Estado-Terminado-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

</div>

## Java NetBeans Status

Este proyecto es una aplicación de escritorio robusta desarrollada en Java Swing que implementa un sistema de Punto de Venta (POS).  
Permite la gestión integral de un catálogo de productos, control de inventario en tiempo real, administración de clientes, procesamiento de carritos de compras y un registro histórico detallado de pedidos.

> **Nota:** La persistencia de datos se maneja mediante almacenamiento en archivos de texto (.txt), lo que facilita su despliegue inmediato sin necesidad de configurar bases de datos complejas.

### ✨ Características Clave
* 📦 **Gestión de Catalogo:** Control de stock en tiempo real con validaciones.
* 📈 **Estrategias de Marketing:** Configuración de **Ofertas Flash** (con temporizador) y **Precios Mayoristas** desde el panel administrativo.
* 🛒 **Carrito Dinámico:** Cálculo automático de totales e impuestos.
* 💳 **Múltiples Métodos de Pago:** Zelle, Pago Móvil, Efectivo y Punto de Venta.
* 📸 **Snapshots de Precios:** Guarda el precio histórico del producto al momento de la venta.
* 🧾 **Comprobantes Digitales:** Generación de resumen de pedido.

---

## 📋 Requisitos Previos

Para ejecutar y compilar este proyecto, asegúrate de contar con lo siguiente en tu entorno:

- ☕ Java Development Kit (JDK): Versión 17 o superior.
- 💻 IDE Recomendado: NetBeans (estructura nativa detectada), IntelliJ IDEA o Eclipse.
- 🐙 Control de Versiones: Git.


## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio

Abre tu terminal y ejecuta:

```bash
git clone https://github.com/albahir/Carrito-Gesti-n_Catalogo-Pedidos.git
cd sistema-catalogo
```


### 2. Importar en el IDE

En NetBeans / IntelliJ / Eclipse:

- Selecciona `File > Open Project` (o `Import`)
- Navega hasta la carpeta raíz clonada.
- El IDE detectará automáticamente la carpeta `src`.

### 3. Compilación

**Desde el IDE:**
- Haz clic derecho sobre el proyecto.
- Selecciona `Clean and Build` (Limpiar y Construir).

**Desde terminal (Manual):**
```bash
javac -d build src/**/*.java
```

### 4. Ejecución ▶️

Localiza la clase principal `Main.java` dentro del paquete de vistas y ejecútala:
- Clic derecho → `Run File`.



## 📦 Estructura del Proyecto
```
sistema-catalogo/
├── src/
│   ├── AcccesoDatosCatalogo/   # Lógica de negocio y persistencia (.txt)
│   ├── Controladores/          # Puentes entre Vista y Modelo
│   ├── EntidadesCatalogo/      #  Producto, Cliente, Pedido, Configuracion, DetalleCompra, PedidoBuilder
│   ├── Utilidades/             # Herramientas (UIFabric, Formatos)
│   └── VistasCatalogo/         # Paneles y Dialogos de la Interfaz en Swing
├── productos.txt               # Base de datos plana
├── clientes.txt                # Base de datos plana
└── README.md
```

## 🚀 Guía de Uso Paso a Paso

### 1. Gestión de Inventario (Admin)
Antes de vender, el sistema permite administrar el catálogo desde el `PanelFormulario`.

* **Carga de Productos:** Ingresa nombre, precio base y stock inicial.
* **Imágenes:** Puedes subir fotos arrastrándolas al recuadro o haciendo clic (Drag & Drop).
* **Estrategias de Precio:**
    * ⚡ **Oferta Flash:** Activa descuentos por tiempo limitado.
    * 📦 **Mayorista:** Configura descuentos automáticos por volumen (Ej: "Lleva 6 y ahorra 10%").

![Vista Formulario](https://via.placeholder.com/800x400?text=Tu+Captura+del+PanelFormulario+Aqui)
> *Panel de gestión con configuración de ofertas y carga de imágenes.*

---

### 2. Identificación del Cliente
Al iniciar una venta, el sistema solicita la cédula.
* Si el cliente existe, carga sus datos.
* Si es nuevo, despliega el formulario de registro (`DialogoCliente`) con validaciones de teléfono y correo.

![Vista Cliente](https://via.placeholder.com/800x200?text=Tu+Captura+del+DialogoCliente+Aqui)

---

### 3. Proceso de Venta (Carrito)
Navega por el catálogo visual. Los productos muestran etiquetas inteligentes (**"Agotado"**, **"Oferta -20%"**, **"Mayorista x6"**).
* Agrega productos al carrito.
* Ajusta cantidades (el sistema valida el stock disponible en tiempo real).

![Vista Catalogo](https://via.placeholder.com/800x400?text=Tu+Captura+del+Catalogo+y+Carrito+Aqui)

---

### 4. Procesamiento del Pago
Al confirmar, selecciona el método de pago preferido:
* **Zelle / Pago Móvil:** Requiere validación de referencia.
* **Punto de Venta / Efectivo:** Flujos simplificados.

![Vista Pago](https://via.placeholder.com/800x400?text=Tu+Captura+del+DialogoPago+Aqui)

---

### 5. Comprobante y Historial
Al finalizar, se genera un ticket digital detallando:
* Items comprados.
* Descuentos aplicados (Ahorro total).
* Datos fiscales de la empresa y cliente.

![Vista Recibo](https://via.placeholder.com/400x600?text=Tu+Captura+del+Recibo+Aqui)

---


## 📝 Notas Adicionales
- ⚠️ Permisos: Asegúrate de que la carpeta del proyecto tenga permisos de Lectura/Escritura, ya que la app necesita crear y modificar los archivos `.txt`.
- 📸 Snapshots: El sistema guarda una "foto instantánea" de los precios y nombres de los productos al momento de la venta. Esto garantiza que el historial no se altere si cambias el catálogo en el futuro.
- 🗄️ Base de Datos: No requiere MySQL ni PostgreSQL. Todo es portable.

👤 Autor
- Desarrollado por Manuel Rodriguez/albahir.
- 👨‍💻 Desarrollado en Java Swing.

