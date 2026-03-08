package src.controlador;

import java.awt.Point;
import java.io.*;
import java.util.*;

import src.model.Grafo;
import src.model.GrafoDirigido;
import src.model.GrafoNoDirigido;
import src.vista.PanelGrafo;

public class GrafoPersistence {

    private static final String CARPETA_SAVES = "saves/";

    // Constructor ya no es necesario, pero lo mantenemos por compatibilidad
    public GrafoPersistence() {
        File carpeta = new File(CARPETA_SAVES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    // ========== MÉTODOS ESTÁTICOS PARA PERSISTENCIA ==========
    /**
     * Guarda el grafo completo (estructura + posiciones)
     */
    public static void guardarGrafo(Grafo grafo, PanelGrafo panel, String nombre) throws IOException {
        guardarEstructura(grafo, nombre);
        guardarPosiciones(grafo, panel, nombre);
        guardarTipo(grafo, nombre);
    }

    /**
     * Carga el grafo completo (estructura + posiciones)
     */
    public static void cargarGrafo(Grafo grafo, PanelGrafo panel, String nombre) throws IOException {
        boolean esDirigido = leerTipo(nombre);

        // Crear grafo temporal del tipo correcto
        Grafo nuevoGrafo = esDirigido ? new GrafoDirigido() : new GrafoNoDirigido();

        // Cargar estructura en el grafo temporal
        cargarEstructura(nuevoGrafo, nombre, esDirigido);

        // Transferir datos al grafo existente
        transferirDatos(grafo, nuevoGrafo);

        // Cargar posiciones
        cargarPosiciones(grafo, panel, nombre);
    }

    /**
     * Lista los grafos guardados (sin extensiones)
     */
    public static String[] listarGrafosGuardados() {
        File carpeta = new File(CARPETA_SAVES);
        File[] archivos = carpeta.listFiles((dir, name)
                -> name.endsWith(".txt") && !name.endsWith("_pos.txt") && !name.endsWith("_tipo.txt"));

        if (archivos == null) {
            return new String[0];
        }

        String[] nombres = new String[archivos.length];
        for (int i = 0; i < archivos.length; i++) {
            nombres[i] = archivos[i].getName().replace(".txt", "");
        }
        return nombres;
    }

    // ========== MÉTODOS DE EXPORTACIÓN (desde GrafoArchivo) ==========
    public static void exportarResultados(Map<Integer, Integer> componentes, String archivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            writer.println("=== REPORTE DE COMPONENTES CONEXAS ===");
            writer.println("Fecha: " + new Date());
            writer.println();

            int numComponentes = componentes.values().stream()
                    .mapToInt(Integer::intValue)
                    .max().orElse(-1) + 1;

            writer.println("Total de componentes: " + numComponentes);
            writer.println();

            for (int i = 0; i < numComponentes; i++) {
                writer.print("Componente " + i + ": ");
                for (Map.Entry<Integer, Integer> entry : componentes.entrySet()) {
                    if (entry.getValue() == i) {
                        writer.print(entry.getKey() + " ");
                    }
                }
                writer.println();
            }
        }
    }

    public static void exportarResultadosCSV(Map<Integer, Integer> componentes, String archivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            writer.println("Nodo,Componente");
            for (Map.Entry<Integer, Integer> entry : componentes.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        }
    }

    public static void exportarEstadisticasCSV(Grafo grafo, String archivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            writer.println("ESTADÍSTICAS DEL GRAFO");
            writer.println("Fecha," + new Date());
            writer.println("Total Nodos," + grafo.contarNodos());
            writer.println("Total Aristas," + grafo.contarAristas());
            writer.println();

            writer.println("GRADO POR NODO");
            writer.println("Nodo,Grado");

            int sumaGrados = 0;
            for (Integer valor : grafo.getValoresNodos()) {
                int grado = grafo.obtenerGrado(valor);
                sumaGrados += grado;
                writer.println(valor + "," + grado);
            }

            double promedio = (double) sumaGrados / grafo.contarNodos();
            writer.println();
            writer.println("Grado promedio," + String.format("%.2f", promedio));

            Map<Integer, Integer> componentes = grafo.componentesConexas();
            writer.println();
            writer.println("COMPONENTES CONEXAS");
            writer.println("Componente,Nodos");

            int numComponentes = componentes.values().stream()
                    .mapToInt(Integer::intValue)
                    .max().orElse(-1) + 1;

            for (int i = 0; i < numComponentes; i++) {
                writer.print(i + ",");
                for (Map.Entry<Integer, Integer> entry : componentes.entrySet()) {
                    if (entry.getValue() == i) {
                        writer.print(entry.getKey() + " ");
                    }
                }
                writer.println();
            }
        }
    }

    // ========== MÉTODOS PRIVADOS AUXILIARES ==========
    private static void guardarEstructura(Grafo grafo, String nombre) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CARPETA_SAVES + nombre + ".txt"))) {
            for (Integer valor : grafo.getValoresNodos()) {
                writer.print(valor + ":");
                List<Integer> adyacentes = grafo.getAdyacentes(valor);
                for (int i = 0; i < adyacentes.size(); i++) {
                    writer.print(adyacentes.get(i));
                    if (i < adyacentes.size() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
        }
    }

    private static void guardarPosiciones(Grafo grafo, PanelGrafo panel, String nombre) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CARPETA_SAVES + nombre + "_pos.txt"))) {
            for (Integer valor : grafo.getValoresNodos()) {
                Point p = panel.getPosicionNodo(valor);
                if (p != null) {
                    writer.println(valor + ":" + p.x + "," + p.y);
                }
            }
        }
    }

    private static void guardarTipo(Grafo grafo, String nombre) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CARPETA_SAVES + nombre + "_tipo.txt"))) {
            writer.println(grafo instanceof GrafoDirigido ? "DIRIGIDO" : "NO_DIRIGIDO");
        }
    }

    private static boolean leerTipo(String nombre) throws IOException {
        File archivoTipo = new File(CARPETA_SAVES + nombre + "_tipo.txt");
        if (archivoTipo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoTipo))) {
                String tipo = reader.readLine();
                return "DIRIGIDO".equals(tipo);
            }
        }
        return false; // Por defecto no dirigido
    }

    private static void cargarEstructura(Grafo grafo, String nombre, boolean esDirigido) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(CARPETA_SAVES + nombre + ".txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] partes = linea.split(":");
                int valorNodo = Integer.parseInt(partes[0]);
                grafo.insertarNodo(valorNodo);

                if (partes.length > 1 && !partes[1].isEmpty()) {
                    String[] adyacentes = partes[1].split(",");
                    for (String adj : adyacentes) {
                        int vecino = Integer.parseInt(adj);
                        if (esDirigido || vecino > valorNodo) {
                            grafo.agregarArista(valorNodo, vecino);
                        }
                    }
                }
            }
        }
    }

    private static void cargarPosiciones(Grafo grafo, PanelGrafo panel, String nombre) throws IOException {
        File archivoPos = new File(CARPETA_SAVES + nombre + "_pos.txt");
        if (archivoPos.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoPos))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] partes = linea.split(":");
                    if (partes.length == 2) {
                        int valor = Integer.parseInt(partes[0]);
                        String[] coords = partes[1].split(",");
                        if (coords.length == 2) {
                            int x = Integer.parseInt(coords[0]);
                            int y = Integer.parseInt(coords[1]);
                            panel.setPosicionNodo(valor, x, y);
                        }
                    }
                }
            }
        } else {
            distribuirNodosAutomaticamente(grafo, panel);
        }
    }

    private static void transferirDatos(Grafo destino, Grafo origen) {
        destino.limpiarGrafo();
        for (Integer valor : origen.getValoresNodos()) {
            destino.insertarNodo(valor);
        }
        for (Integer valor : origen.getValoresNodos()) {
            for (Integer vecino : origen.getAdyacentes(valor)) {
                destino.agregarArista(valor, vecino);
            }
        }
    }

    private static void distribuirNodosAutomaticamente(Grafo grafo, PanelGrafo panel) {
        int numNodos = grafo.contarNodos();
        if (numNodos == 0) {
            return;
        }

        int ancho = 800, alto = 600;
        int margen = 80;
        int centroX = margen + (ancho - 2 * margen) / 2;
        int centroY = margen + (alto - 2 * margen) / 2;
        int radio = Math.min(ancho - 2 * margen, alto - 2 * margen) / 2 - 20;

        if (radio < 50) {
            radio = 150;
        }

        int i = 0;
        for (Integer valor : grafo.getValoresNodos()) {
            double angulo = 2 * Math.PI * i / numNodos;
            int x = centroX + (int) (radio * Math.cos(angulo));
            int y = centroY + (int) (radio * Math.sin(angulo));
            panel.setPosicionNodo(valor, x, y);
            i++;
        }
    }
}
