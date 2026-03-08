# 🔍DetConv v2.0 - Detector de Componentes Conexas

DetConv es una aplicación interactiva desarrollada en Java para la visualización, edición y análisis de grafos, tanto dirigidos como no dirigidos. Permite construir grafos de manera intuitiva mediante clics, detectar componentes conexas, calcular grados y exportar resultados para su posterior análisis.

## 🕹️Funcionalidades Principales

•   Edición Interactiva de Grafos
    -Implementa un sistema de edición basado en estrategias de clic que permite:
    -Agregar Nodos: Haz clic en el panel para crear nuevos nodos con valores únicos auto-generados.
    -Conectar Nodos: Selecciona dos nodos para crear una arista entre ellos.
    -Eliminar Nodos: Haz clic en un nodo para eliminarlo junto con todas sus aristas.
    -Eliminar Aristas: Haz clic cerca de una arista para eliminarla.

•   Soporte para Grafos Dirigidos y No Dirigidos
    -GrafoNoDirigido.java: Implementa aristas bidireccionales con detección de componentes conexas.
    -GrafoDirigido.java: Implementa aristas direccionales con algoritmo de Kosaraju para componentes fuertemente conexas.
    -Interfaz gráfica que dibuja flechas en grafos dirigidos y líneas simples en no dirigidos.

•    Detección de Componentes Conexas
    -Para grafos no dirigidos: componentes conexas mediante DFS.
    -Para grafos dirigidos: componentes fuertemente conexas mediante el algoritmo de Kosaraju.

•   Cálculo de Grados
    -No dirigidos: Grado total por nodo y promedio.
    -Dirigidos: Grado de entrada, grado de salida y grado total por nodo.

•   Persistencia de Datos
    -Guardado y carga completa de grafos incluyendo:
    -Estructura de nodos y aristas
    -Posiciones de los nodos en el panel
    -Tipo de grafo (dirigido/no dirigido)
    -Auto-guardado cada 30 segundos (configurable)

•   Exportación de Resultados
    -CSV de Componentes: Listado de nodos por componente.
    -Estadísticas Completas: Grados, promedios, componentes y más en formato CSV.

•   Visualización Profesional
    -Splash screen con animación de fade-in.
    -Icono personalizado en la ventana.
    -Panel de dibujo de nodos y su conexión (implementado en PanelGrafo).
    -Colores diferenciados: nodos rojos, aristas negras con flechas para dirigidos.

•   Interfaz de Usuario Intuitiva
    -Panel lateral con herramientas y resultados.
    -Botones de acción en la parte inferior.
    -Selector de tipo de grafo (dirigido/no dirigido).
    -Área de resultados con formato monospace.

### 🛠️Características Técnicas

•   Arquitectura: Implementación del patrón MVC (Modelo-Vista-Controlador) con:
    -Strategy Pattern: Para comportamientos de clic (AgregarNodoStrategy, ConectarAristaStrategy, etc.)
    -Observer Pattern: Para notificaciones de cambios en el grafo (GrafoListener)
    -Template Method: A través de la clase abstracta Grafo

•   Persistencia: Sistema robusto de guardado/carga en archivos de texto plano:
    -nombre.txt: Estructura del grafo (nodos:adyacentes)
    -nombre_pos.txt: Posiciones de los nodos
    -nombre_tipo.txt: Tipo de grafo (DIRIGIDO/NO_DIRIGIDO)

•   Algoritmos Implementados:
    -DFS para componentes conexas
    -Kosaraju para componentes fuertemente conexas
    -Detección de proximidad para selección de nodos/aristas
    -Cálculo de distancia punto-línea para detección de aristas

### 🖼️ Pantallas

### ⚙️Requisitos del Sistema

•   Java JDK: 11 o superior
•   Sistema Operativo: Windows, Linux o macOS
•   Resolución Mínima: 1280×720 píxeles
•   Espacio en Disco: ~10 MB para la aplicación, más espacio para archivos guardados

### 🚀Instalación y Ejecución

•   Clonar o descargar el repositorio.
•   Importar el proyecto en tu IDE favorito (NetBeans, IntelliJ IDEA, Eclipse).
•   Compilar todas las clases del paquete src/.
•   Ejecutar la clase src.vista.DetectorConexas.java.

### ⚙️Tecnologías Utilizadas

•   Lenguaje: Java 11 en adelante
•   Interfaz Gráfica: Java Swing / AWT
•   Persistencia: Archivos de texto plano (.txt)
•   Exportación: Formato CSV (.csv)

### 📖 Instalación y guía de Uso

1. Clonar o descargar el repositorio.
2. Importar el proyecto en tu IDE favorito (NetBeans, IntelliJ o Eclipse).
3. Compilar y Ejecutar la clase Controldeaccesos.java.

### Guía de Uso Rápido

1. Inicio: La aplicación muestra un splash screen y luego la ventana principal.

2. Seleccionar tipo de grafo: Usa los radio botones en la esquina superior derecha (No Dirigido/Dirigido).

3. Elegir modo de edición: En el panel lateral, selecciona:
•   Agregar Nodos: Haz clic en el área blanca para crear nodos.
•   Conectar Nodos: Haz clic en un nodo y luego en otro para crear una arista.
•   Eliminar Nodos: Haz clic en un nodo para eliminarlo.
•   Eliminar Aristas: Haz clic cerca de una arista para eliminarla.

4. Analizar: Usa los botones panel inferior:
•   Detectar Componentes: Muestra las componentes conexas.
•   Calcular Grado: Muestra información detallada de grados.
•   Exportar CSV: Guarda resultados en formato CSV.
•   Guardar/Cargar Grafo: Persistencia completa.
•   Limpiar Grafo: Reinicia el grafo actual.
•   Información: Muestra ayuda y créditos.

## 🔩Estructura de Datos

```text
src/
├── model/
│   ├── Grafo.java                 # Clase abstracta base
│   ├── GrafoNoDirigido.java        # Implementación no dirigida
│   ├── GrafoDirigido.java          # Implementación dirigida (con Kosaraju)
│   ├── Nodo.java                   # Entidad nodo con adyacentes
│   └── GrafoListener.java          # Interfaz Observer
│
├── vista/
│   ├── DetectorConexas.java        # Punto de entrada principal
│   ├── VentanaPrincipal.java        # Ventana principal
│   ├── PanelGrafo.java             # Panel de dibujo del grafo
│   ├── SplashScreen.java           # Pantalla de bienvenida
│   ├── GestorIcono.java            # Utilidad para iconos
│   ├── EstrategiaClick.java        # Interfaz Strategy
│   ├── ClickInfo.java              # Encapsulador de clics
│   ├── AgregarNodoStrategy.java    # Estrategia: agregar nodos
│   ├── ConectarAristaStrategy.java # Estrategia: conectar nodos
│   ├── EliminarNodoStrategy.java   # Estrategia: eliminar nodos
│   └── EliminarAristaStrategy.java # Estrategia: eliminar aristas
│
├── controlador/
│   ├── ControladorGrafo.java       # Controlador MVC
│   └── GrafoPersistence.java       # Persistencia y exportación
│
└── resources/
    └── icons/
        ├── Icon.png                 # Icono principal
        └── image.png                 # Imagen del título

```

---

## 🧩 Patrones de Diseño Implementados

1. Strategy:  Utilizada en EstrategiaClick.java e implementaciones. Para añadir comportamientos de clic intercambiables.
2. Observer: Utilizada en GrafoListener.java + PanelGrafo.java. Para la notificación de cambios en el grafo.
3. MVC:  Utilizada en model/, vista/, controlador/. Para la separación de responsabilidades.
4. Template Method: Utilizada en Grafo.java + subclases. Para la creación de algoritmos comunes con variantes específicas.
5. Factory Method: cambiarTipoGrafo() en VentanaPrincipal.  Para la creación de grafos según su tipo.

## 👤Autor

**Samuel González** - *Desarrollador y diseñador*
